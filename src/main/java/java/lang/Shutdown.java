
package java.lang;

/**
 * Package-private utility class containing data structures and logic
 * governing the virtual-machine shutdown sequence.
 * [虚拟机关闭程序] 包含数据结构和逻辑的虚拟机关闭程序的辅助类。
 *
 * @author   Mark Reinhold
 * @since    1.3
 */
class Shutdown {

    /**
     * 运行中
     */
    private static final int RUNNING = 0;
    /**
     * 挂钩执行中
     */
    private static final int HOOKS = 1;
    /**
     * 终结器执行中
     */
    private static final int FINALIZERS = 2;
    /**
     * Shutdown state (关闭状态)
     */
    private static int state = RUNNING;

    /** Should we run all finalizers upon exit? (要在退出时运行所有终结器吗？) */
    private static boolean runFinalizersOnExit = false;

    // The system shutdown hooks are registered with a predefined slot. (使用预定义的插槽来注册系统的关闭挂钩)
    // The list of shutdown hooks is as follows:
    // (0) Console restore hook (控制台恢复挂钩)
    // (1) Application hooks (应用程序挂钩)
    // (2) DeleteOnExit hook (在退出时删除挂钩)
    /**
     * 最大系统挂钩数量
     */
    private static final int MAX_SYSTEM_HOOKS = 10;
    /**
     * 数组+列表
     */
    private static final Runnable[] hooks = new Runnable[MAX_SYSTEM_HOOKS];

    // the index of the currently running shutdown hook to the hooks array (挂钩数组中当前运行的关闭挂钩所在的索引)
    private static int currentRunningHook = 0;

    /** The preceding static fields are protected by this lock (上述静态字段被这把锁保护) */
    private static class Lock { }
    private static Object lock = new Lock(); // 锁对象

    /** Lock object for the native halt method (本地终止方法的锁对象) */
    private static Object haltLock = new Lock();

    /** Invoked by Runtime.runFinalizersOnExit */
    static void setRunFinalizersOnExit(boolean run) {
        synchronized (lock) {
            runFinalizersOnExit = run;
        }
    }


    /**
     * Add a new shutdown hook.  Checks the shutdown state and the hook itself,
     * but does not do any security checks.
     * 添加一个新的关闭挂钩。
     * 检查关闭状态和挂钩本身，但不做任何安全检查。
     *
     * The registerShutdownInProgress parameter should be false except
     * registering the DeleteOnExitHook since the first file may
     * be added to the delete on exit list by the application shutdown
     * hooks.
     *
     * @params slot  the slot in the shutdown hook array, whose element
     *               will be invoked in order during shutdown (关闭挂钩数组中的插槽。在关闭期间，其元素将按顺序被调用)
     * @params registerShutdownInProgress true to allow the hook
     *               to be registered even if the shutdown is in progress. (true：以允许挂钩可以被注册，即使正在关闭时)
     * @params hook  the hook to be registered (待注册的挂钩)
     *
     * @throw IllegalStateException
     *        if registerShutdownInProgress is false and shutdown is in progress; or
     *        if registerShutdownInProgress is true and the shutdown process
     *           already passes the given slot
     */
    static void add(int slot, boolean registerShutdownInProgress, Runnable hook) {
        // 锁对象监视器同步语句
        synchronized (lock) {
            if (hooks[slot] != null) {
                throw new InternalError("Shutdown hook at slot " + slot + " already registered"); // 关闭钩槽已经注册过
            }

            if (!registerShutdownInProgress) {
                // 正在关闭中
                if (state > RUNNING) {
                    throw new IllegalStateException("Shutdown in progress");
                }
            } else {
                if (state > HOOKS || (state == HOOKS && slot <= currentRunningHook)) {
                    throw new IllegalStateException("Shutdown in progress");
                }
            }

            // 保存到对应的挂钩数组槽中
            hooks[slot] = hook;
        }
    }

    /**
     * Run all registered shutdown hooks.
     * 运行所有注册的关闭挂钩。
     */
    private static void runHooks() {
        for (int i = 0; i < MAX_SYSTEM_HOOKS; i++) {
            try {
                // 每次循环都新定义局部变量
                Runnable hook;
                // 同步
                synchronized (lock) {
                    // acquire the lock to make sure the hook registered during
                    // shutdown is visible here.
                    // 获取锁以确保注册的挂钩在关闭期间是可见的
                    currentRunningHook = i;
                    // 挂钩
                    hook = hooks[i];
                }
                if (hook != null) {
                    // 执行关闭挂钩线程
                    hook.run();
                }
            } catch(Throwable t) {
                if (t instanceof ThreadDeath) {
                    // 线程停止
                    ThreadDeath td = (ThreadDeath) t;
                    throw td;
                }
            }
        }
    }

    /** The halt method is synchronized on the halt lock
     * to avoid corruption of the delete-on-shutdown file list.
     * It invokes the true native halt method.
     * 终止方法是在终止锁进行同步，以避免在关闭时删除的文件列表的损坏。
     * 它调用真正的本地终止方法。
     */
    static void halt(int status) {
        synchronized (haltLock) {
            halt0(status);
        }
    }

    static native void halt0(int status);

    /** Wormhole for invoking java.lang.ref.Finalizer.runAllFinalizers (蠕虫攻击) */
    private static native void runAllFinalizers();


    /**
     * The actual shutdown sequence is defined here.
     * 这里定义实际的关闭程序。
     *
     * If it weren't for runFinalizersOnExit, this would be simple -- we'd just
     * run the hooks and then halt.  Instead we need to keep track of whether
     * we're running hooks or finalizers.  In the latter case a finalizer could
     * invoke exit(1) to cause immediate termination, while in the former case
     * any further invocations of exit(n), for any n, simply stall.  Note that
     * if on-exit finalizers are enabled they're run iff the shutdown is
     * initiated by an exit(0); they're never run on exit(n) for n != 0 or in
     * response to SIGINT, SIGTERM, etc.
     */
    private static void sequence() {
        synchronized (lock) {
            /* Guard against the possibility of a daemon thread invoking exit
             * after DestroyJavaVM initiates the shutdown sequence
             * 警惕守护线程在销毁的 JVM 启动关闭程序之后调用退出虚拟机的可能性
             */
            if (state != HOOKS) {
                return;
            }
        }
        // 运行所有注册的关闭挂钩
        runHooks();
        boolean rfoe;
        synchronized (lock) {
            // 更新到终结器执行状态
            state = FINALIZERS;
            rfoe = runFinalizersOnExit;
        }
        if (rfoe) {
            // 运行所有的终结器
            runAllFinalizers();
        }
    }


    /** Invoked by Runtime.exit, which does all the security checks.
     * Also invoked by handlers for system-provided termination events,
     * which should pass a nonzero status code.
     * 被 Runtime.exit 方法调用，它会做所有的安全检查。
     * 也可以被系统提供的终止事件的处理程序调用，它应该会传入一个非零的状态代码。
     * 退出虚拟机，被Runtime.exit方法调用，它会做所有的安全检查。
     */
    static void exit(int status) {
        boolean runMoreFinalizers = false;
        // 锁对象监视器同步语句
        synchronized (lock) {
            if (status != 0) {
                // 非正常退出
                runFinalizersOnExit = false;
            }
            switch (state) {
            case RUNNING:
                /* Initiate shutdown (启动关闭程序) */
                state = HOOKS;
                break;
            case HOOKS:
                /* Stall and halt (拖延并终止) */
                break;
            case FINALIZERS:
                if (status != 0) {
                    /* Halt immediately on nonzero status (立即终止) */
                    halt(status);
                } else {
                    /* Compatibility with old behavior:
                     * Run more finalizers and then halt
                     */
                    runMoreFinalizers = runFinalizersOnExit;
                }
                break;
            }
        }
        if (runMoreFinalizers) {
            // 运行终结器
            runAllFinalizers();
            halt(status);
        }
        // 类对象监视器同步语句，保证JVM内互斥
        synchronized (Shutdown.class) {
            /* Synchronize on the class object, causing any other thread
             * that attempts to initiate shutdown to stall indefinitely
             * 在类对象上进行同步，造成任何其他线程尝试启动关闭程序都会无限期地拖延
             */
            // 执行关闭程序
            sequence();
            // 停止虚拟机
            halt(status);
        }
    }


    /** Invoked by the JNI DestroyJavaVM procedure when the last non-daemon
     * thread has finished.  Unlike the exit method, this method does not
     * actually halt the VM.
     * 在最后一个非守护线程完成时，由 JNI 销毁的 JVM 过程调用。
     * 不像 exit 方法，此方法并不实际停止虚拟机。
     */
    static void shutdown() {
        synchronized (lock) {
            switch (state) {
            case RUNNING:
                /* Initiate shutdown */
                state = HOOKS;
                break;
            case HOOKS:
                /* Stall and then return */
            case FINALIZERS:
                break;
            }
        }
        // 类对象监视器同步语句，保证JVM内互斥
        synchronized (Shutdown.class) {
            // 执行关闭程序
            sequence();
        }
    }
}
