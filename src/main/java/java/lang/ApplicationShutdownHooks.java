
package java.lang;

import java.util.Collection;
import java.util.IdentityHashMap;

/**
 * Class to track and run user level shutdown hooks registered through
 * <tt>{@link Runtime#addShutdownHook Runtime.addShutdownHook}</tt>.
 * [虚拟机关闭程序] 跟踪和运行用户级别已注册的关闭挂钩。
 * <p>
 * 通过 <tt>{@link Runtime#addShutdownHook(Thread)}</tt> 跟踪和运行用户级别已注册的关闭挂钩。
 *
 * @see java.lang.Runtime#addShutdownHook
 * @see java.lang.Runtime#removeShutdownHook
 */
class ApplicationShutdownHooks {
    /**
     * The set of registered hooks (已注册关闭挂钩的集合)
     */
    private static IdentityHashMap<Thread, Thread> hooks;
    static {
        try {
            // 在静态类初始化时，添加关闭程序
            Shutdown.add(1 /* shutdown hook invocation order (关闭挂钩调用顺序) */,
                false /* not registered if shutdown in progress (如果正在关闭中，则不能注册) */,
                new Runnable() {
                    @Override
                    public void run() {
                        // 运行所有关闭挂钩
                        runHooks();
                    }
                }
            );
            hooks = new IdentityHashMap<>();
        } catch (IllegalStateException e) {
            // application shutdown hooks cannot be added if
            // shutdown is in progress.
            // 如果正在关机，则无法添加应用程序的关闭挂钩
            hooks = null;
        }
    }


    private ApplicationShutdownHooks() {}

    /** Add a new shutdown hook.  Checks the shutdown state and the hook itself,
     * but does not do any security checks.
     * 添加一个新的关闭挂钩。
     * 检查关闭状态和挂钩本身，但不做任何安全检查。
     */
    static synchronized void add(Thread hook) {
        if (hooks == null) {
            // 正在关闭中
            throw new IllegalStateException("Shutdown in progress");
        }

        if (hook.isAlive()) {
            // 挂钩已经运行
            throw new IllegalArgumentException("Hook already running");
        }

        if (hooks.containsKey(hook)) {
            // 以前注册的挂钩
            throw new IllegalArgumentException("Hook previously registered");
        }

        hooks.put(hook, hook);
    }

    /** Remove a previously-registered hook.  Like the add method, this method
     * does not do any security checks.
     * 删除先前注册的关闭挂钩。
     * 本方法也不做任何安全检查。
     */
    static synchronized boolean remove(Thread hook) {
        if (hooks == null) {
            // 正在关闭中
            throw new IllegalStateException("Shutdown in progress");
        }

        if (hook == null) {
            throw new NullPointerException();
        }

        return hooks.remove(hook) != null;
    }

    /** Iterates over all application hooks creating a new thread for each
     * to run in. Hooks are run concurrently and this method waits for
     * them to finish.
     * 循环访问创建每个新线程并运行其中的所有应用程序的关闭挂钩。
     * 关闭钩子并发地运行，该方法等待它们完成。
     */
    static void runHooks() {
        // 关闭挂钩线程集合
        Collection<Thread> threads;
        // 类对象监视器同步语句
        synchronized (ApplicationShutdownHooks.class) {
            threads = hooks.keySet();
            // 关闭程序启动后，就不可操作关闭挂钩
            hooks = null;
        }

        // 启动并执行所有的关闭挂钩线程
        for (Thread hook : threads) {
            // 启动此线程
            hook.start();
        }
        for (Thread hook : threads) {
            try {
                // 导致当前线程暂停执行，直到hook线程终止
                hook.join();
            } catch (InterruptedException x) {
                // 忽略线程中断异常
            }
        }
    }
}
