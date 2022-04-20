
package java.lang;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

/**
 * Every Java application has a single instance of class
 * <code>Runtime</code> that allows the application to interface with
 * the environment in which the application is running. The current
 * runtime can be obtained from the <code>getRuntime</code> method.
 * 每个Java应用程序都有一个单独的运行时类的实例，允许应用程序与应用程序运行的环境进行交互。
 * 从 {@link #getRuntime()} 方法可以获得当前的运行时环境。
 * 每个Java应用程序都有一个单独的运行时类的实例(运行时环境)。
 * <p/>
 * An application cannot create its own instance of this class.
 *
 * @author unascribed
 * @see java.lang.Runtime#getRuntime()
 * @since JDK1.0
 */
public class Runtime {
    /**
     * 当前的运行时环境(单个实例)
     */
    private static final Runtime currentRuntime = new Runtime();

    /**
     * Returns the runtime object associated with the current Java application.
     * Most of the methods of class <code>Runtime</code> are instance
     * methods and must be invoked with respect to the current runtime object.
     * 返回与当前Java应用程序关联的运行时对象。
     *
     * @return the <code>Runtime</code> object associated with the current
     * Java application.
     */
    public static Runtime getRuntime() {
        return currentRuntime;
    }

    /**
     * Don't let anyone else instantiate this class (防止被实例化)
     */
    private Runtime() {
    }

    // 关闭虚拟机

    /**
     * Terminates the currently running Java virtual machine by initiating its
     * shutdown sequence.  This method never returns normally.  The argument
     * serves as a status code; by convention, a nonzero status code indicates
     * abnormal termination.
     * 通过启动关闭程序来终止当前正在运行的Java虚拟机。
     * 参数用作状态代码，按照惯例，非零状态代码表示异常终止。
     * <p> The virtual machine's shutdown sequence consists of two phases.  In
     * the first phase all registered {@link #addShutdownHook shutdown hooks},
     * if any, are started in some unspecified order and allowed to run
     * concurrently until they finish.  Once this is done the virtual machine {@link #halt
     * halts}.
     * 虚拟机的关闭程序由两个阶段组成：
     * 在第一阶段，所有注册的关闭挂钩({@link #addShutdownHook})以一些未指定的顺序启动并允许并发地运行，直到它们完成。
     * 一旦完成，虚拟机就停止了({@link #halt})。
     * <p> If this method is invoked after the virtual machine has begun its
     * shutdown sequence then if shutdown hooks are being run this method will
     * block indefinitely.  If shutdown hooks have already been run and on-exit
     * finalization has been enabled then this method halts the virtual machine
     * with the given status code if the status is nonzero; otherwise, it
     * blocks indefinitely.
     * 如果此方法在虚拟机已开始关闭程序后被调用，那么正在运行此方法的关闭挂钩将无限期地阻塞。
     * 如果关闭挂钩已运行和退出确定已启用，则此方法使用给定的非零状态代码来停止虚拟机；
     * 否则，它将无限期地阻塞。
     * <p> The <tt>{@link System#exit(int) System.exit}</tt> method is the
     * conventional and convenient means of invoking this method.
     * <p>
     * {@link System#exit(int)} 退出方法是调用此方法的常规和方便的手段。
     *
     * @param status Termination status.  By convention, a nonzero status code
     *               indicates abnormal termination. (终止状态，通常非零状态代码表示异常终止)
     * @throws SecurityException If a security manager is present and its <tt>{@link
     *                           SecurityManager#checkExit checkExit}</tt> method does not permit
     *                           exiting with the specified status (退出不允许用指定的状态)
     * @see java.lang.SecurityException
     * @see java.lang.SecurityManager#checkExit(int)
     * @see #addShutdownHook
     * @see #removeShutdownHook
     * @see #halt(int)
     */
    public void exit(int status) {
        // 安全策略管理器
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkExit(status);
        }
        // 退出虚拟机
        Shutdown.exit(status);
    }

    /**
     * Registers a new virtual-machine shutdown hook.
     * 注册一个新的虚拟机关闭挂钩。
     * <p> The Java virtual machine <i>shuts down</i> in response to two kinds
     * of events:
     * <ul>
     * <li> The program <i>exits</i> normally, when the last non-daemon
     * thread exits or when the <tt>{@link #exit exit}</tt> (equivalently,
     * <tt>{@link System#exit(int) System.exit}</tt>) method is invoked, or
     * <li> 程序正常退出，当最后一个非守护线程退出或 <tt>{@link #exit}</tt> (<tt>{@link System#exit(int)}</tt>) 方法被调用。
     * <li> The virtual machine is <i>terminated</i> in response to a
     * user interrupt, such as typing <tt>^C</tt>, or a system-wide event,
     * such as user logoff or system shutdown.
     * <li> 虚拟机被终止来响应用户中断(如 输入 <tt>^C</tt>、系统范围的事件、用户注销或关闭系统)。
     * </ul>
     * <p> A <i>shutdown hook</i> is simply an initialized but unstarted
     * thread.  When the virtual machine begins its shutdown sequence it will
     * start all registered shutdown hooks in some unspecified order and let
     * them run concurrently.  When all the hooks have finished it will then
     * run all uninvoked finalizers if finalization-on-exit has been enabled.
     * Finally, the virtual machine will halt.  Note that daemon threads will
     * continue to run during the shutdown sequence, as will non-daemon threads
     * if shutdown was initiated by invoking the <tt>{@link #exit exit}</tt>
     * method.
     * 关闭钩子是一个已初始化但未开始的线程。
     * 当虚拟机开始其关闭程序时，它将以一些未指定的顺序开始所有注册的关闭挂钩，并让它们并发地运行。
     * 当所有的关闭挂钩都已经完成，如果在退出时撤出(finalization-on-exit)已经启用，然后它会运行所有未运行的终结器。
     * 最后，虚拟机将停止。
     * 注意：守护线程在关闭过程期间将继续运行，如果关闭是通过调用 {@link #exit} 方法，则非守护线程也会继续运行。
     * <p> Once the shutdown sequence has begun it can be stopped only by
     * invoking the <tt>{@link #halt halt}</tt> method, which forcibly
     * terminates the virtual machine.
     * 一旦关闭程序开始，仅能通过调用 <tt>{@link #halt}</tt> 方法来停止，它将强制终止虚拟机。
     * <p> Once the shutdown sequence has begun it is impossible to register a
     * new shutdown hook or de-register a previously-registered hook.
     * Attempting either of these operations will cause an
     * <tt>{@link IllegalStateException}</tt> to be thrown.
     * 一旦关闭程序开始，关闭挂钩就不可变更。
     * <p> Shutdown hooks run at a delicate time in the life cycle of a virtual
     * machine and should therefore be coded defensively.  They should, in
     * particular, be written to be thread-safe and to avoid deadlocks insofar
     * as possible.  They should also not rely blindly upon services that may
     * have registered their own shutdown hooks and therefore may themselves in
     * the process of shutting down.  Attempts to use other thread-based
     * services such as the AWT event-dispatch thread, for example, may lead to
     * deadlocks.
     * 关闭挂钩运行在虚拟机的生命周期中的一个微妙的时间，因此应进行防御式编程。
     * 它们应该是线程安全的，以尽可能地避免死锁。
     * 它们也不应盲目地依赖那些可以注册自己的关闭程序的服务，可能它们就位于关闭程序的过程中。
     * 尝试使用其它基于线程的服务可能导致死锁。
     * <p> Shutdown hooks should also finish their work quickly.  When a
     * program invokes <tt>{@link #exit exit}</tt> the expectation is
     * that the virtual machine will promptly shut down and exit.  When the
     * virtual machine is terminated due to user logoff or system shutdown the
     * underlying operating system may only allow a fixed amount of time in
     * which to shut down and exit.  It is therefore inadvisable to attempt any
     * user interaction or to perform a long-running computation in a shutdown
     * hook.
     * 关闭挂钩应该迅速完成其工作。
     * 当程序调用 <tt>{@link #exit}</tt> 方法时，期望是虚拟机将立即关闭并退出。
     * <p> Uncaught exceptions are handled in shutdown hooks just as in any
     * other thread, by invoking the <tt>{@link ThreadGroup#uncaughtException
     * uncaughtException}</tt> method of the thread's <tt>{@link
     * ThreadGroup}</tt> object.  The default implementation of this method
     * prints the exception's stack trace to <tt>{@link System#err}</tt> and
     * terminates the thread; it does not cause the virtual machine to exit or
     * halt.
     * 就像在任何其它线程中那样，在关闭挂钩中处理未捕获的异常，
     * 通过调用线程组对象的 <tt>{@link ThreadGroup#uncaughtException}</tt> 方法。
     * 此方法的默认实现是打印异常的堆栈跟踪到 <tt>{@link System#err}</tt> 并终止线程，
     * 它不会导致虚拟机退出或停止。
     * <p> In rare circumstances the virtual machine may <i>abort</i>, that is,
     * stop running without shutting down cleanly.  This occurs when the
     * virtual machine is terminated externally, for example with the
     * <tt>SIGKILL</tt> signal (信号) on Unix or the <tt>TerminateProcess</tt> call on
     * Microsoft Windows.  The virtual machine may also abort if a native
     * method goes awry by, for example, corrupting internal data structures or
     * attempting to access nonexistent memory.  If the virtual machine aborts
     * then no guarantee can be made about whether or not any shutdown hooks
     * will be run.
     * 在极少数情况下，虚拟机可能中止，即停止运行而不是正常关闭。
     * 虚拟机也可能终止，如果一个本地方法出错了，如破坏内部数据结构或试图访问不存在的内存。
     * 如果虚拟机中止，是否将运行任何关闭挂钩，不能做任何保证。
     *
     * @param hook An initialized but unstarted <tt>{@link Thread}</tt> object (一个已初始化但未开始的线程对象)
     * @throws IllegalArgumentException If the specified hook has already been registered,
     *                                  or if it can be determined that the hook is already running or
     *                                  has already been run (已注册指定的关闭挂钩，或能确定挂钩线程已经在运行中或已运行)
     * @throws IllegalStateException    If the virtual machine is already in the process
     *                                  of shutting down (虚拟机已在关闭的过程中)
     * @throws SecurityException        If a security manager is present and it denies
     *                                  <tt>{@link RuntimePermission}("shutdownHooks")</tt>
     * @see #removeShutdownHook
     * @see #halt(int)
     * @see #exit(int)
     * @since 1.3
     */
    public void addShutdownHook(Thread hook) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("shutdownHooks"));
        }
        // 添加应用程序的关闭挂钩
        ApplicationShutdownHooks.add(hook);
    }

    /**
     * De-registers a previously-registered virtual-machine shutdown hook.
     * 注销一个以前已注册的虚拟机关闭挂钩。
     *
     * @param hook the hook to remove (要移除的关闭挂钩)
     * @return <tt>true</tt> if the specified hook had previously been
     * registered and was successfully de-registered, <tt>false</tt>
     * otherwise.
     * @throws IllegalStateException If the virtual machine is already in the process of shutting
     *                               down
     * @throws SecurityException     If a security manager is present and it denies
     *                               <tt>{@link RuntimePermission}("shutdownHooks")</tt>
     * @see #addShutdownHook
     * @see #exit(int)
     * @since 1.3
     */
    public boolean removeShutdownHook(Thread hook) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("shutdownHooks"));
        }
        // 移除应用程序的关闭挂钩
        return ApplicationShutdownHooks.remove(hook);
    }

    /**
     * Forcibly terminates the currently running Java virtual machine.  This
     * method never returns normally.
     * 强行终止当前正在运行的Java虚拟机。
     * <p> This method should be used with extreme caution.  Unlike the
     * <tt>{@link #exit exit}</tt> method, this method does not cause shutdown
     * hooks to be started and does not run uninvoked finalizers if
     * finalization-on-exit has been enabled.  If the shutdown sequence has
     * already been initiated then this method does not wait for any running
     * shutdown hooks or finalizers to finish their work.
     * 此方法应慎用。
     * 不像 <tt>{@link #exit}</tt> 方法，此方法不会触发关闭挂钩开始运行，也不会运行未调用的终结器。
     * 如果关闭程序已经启动，此方法不会等待任何运行中的关闭挂钩或终结器来完成它们的工作。
     *
     * @param status Termination status.  By convention, a nonzero status code
     *               indicates abnormal termination.  If the <tt>{@link Runtime#exit
     *               exit}</tt> (equivalently, <tt>{@link System#exit(int)
     *               System.exit}</tt>) method has already been invoked then this
     *               status code will override the status code passed to that method.
     * @throws SecurityException If a security manager is present and its <tt>{@link
     *                           SecurityManager#checkExit checkExit}</tt> method does not permit
     *                           an exit with the specified status
     * @see #exit
     * @see #addShutdownHook
     * @see #removeShutdownHook
     * @since 1.3
     */
    public void halt(int status) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkExit(status);
        }
        // 终止虚拟机
        Shutdown.halt(status);
    }


    // 在进程中执行系统命令

    /**
     * Executes the specified string command in a separate process.
     * 在单独的进程中执行指定的字符串命令。
     * <p>This is a convenience method.  An invocation of the form
     * <tt>exec(command)</tt>
     * behaves in exactly the same way as the invocation
     * <tt>{@link #exec(String, String[], File) exec}(command, null, null)</tt>.
     *
     * @param command a specified system command. (指定的系统命令)
     * @return A new {@link Process} object for managing the subprocess
     * @throws SecurityException        If a security manager exists and its
     *                                  {@link SecurityManager#checkExec checkExec}
     *                                  method doesn't allow creation of the subprocess
     * @throws IOException              If an I/O error occurs
     * @throws NullPointerException     If <code>command</code> is <code>null</code>
     * @throws IllegalArgumentException If <code>command</code> is empty
     * @see #exec(String[], String[], File)
     * @see ProcessBuilder
     */
    public Process exec(String command) throws IOException {
        return exec(command, null, null);
    }

    /**
     * Executes the specified string command in a separate process with the
     * specified environment.
     * <p/>
     * <p>This is a convenience method.  An invocation of the form
     * <tt>exec(command, envp)</tt>
     * behaves in exactly the same way as the invocation
     * <tt>{@link #exec(String, String[], File) exec}(command, envp, null)</tt>.
     *
     * @param command a specified system command.
     * @param envp    array of strings, each element of which
     *                has environment variable settings in the format
     *                <i>name</i>=<i>value</i>, or
     *                <tt>null</tt> if the subprocess should inherit
     *                the environment of the current process.
     * @return A new {@link Process} object for managing the subprocess
     * @throws SecurityException        If a security manager exists and its
     *                                  {@link SecurityManager#checkExec checkExec}
     *                                  method doesn't allow creation of the subprocess
     * @throws IOException              If an I/O error occurs
     * @throws NullPointerException     If <code>command</code> is <code>null</code>,
     *                                  or one of the elements of <code>envp</code> is <code>null</code>
     * @throws IllegalArgumentException If <code>command</code> is empty
     * @see #exec(String[], String[], File)
     * @see ProcessBuilder
     */
    public Process exec(String command, String[] envp) throws IOException {
        return exec(command, envp, null);
    }

    /**
     * Executes the specified string command in a separate process with the
     * specified environment and working directory.
     * <p/>
     * <p>This is a convenience method.  An invocation of the form
     * <tt>exec(command, envp, dir)</tt>
     * behaves in exactly the same way as the invocation
     * <tt>{@link #exec(String[], String[], File) exec}(cmdarray, envp, dir)</tt>,
     * where <code>cmdarray</code> is an array of all the tokens in
     * <code>command</code>.
     * <p/>
     * <p>More precisely, the <code>command</code> string is broken
     * into tokens using a {@link StringTokenizer} created by the call
     * <code>new {@link StringTokenizer}(command)</code> with no
     * further modification of the character categories.  The tokens
     * produced by the tokenizer are then placed in the new string
     * array <code>cmdarray</code>, in the same order.
     *
     * @param command a specified system command.
     * @param envp    array of strings, each element of which
     *                has environment variable settings in the format
     *                <i>name</i>=<i>value</i>, or
     *                <tt>null</tt> if the subprocess should inherit
     *                the environment of the current process.
     * @param dir     the working directory of the subprocess, or
     *                <tt>null</tt> if the subprocess should inherit
     *                the working directory of the current process.
     * @return A new {@link Process} object for managing the subprocess
     * @throws SecurityException        If a security manager exists and its
     *                                  {@link SecurityManager#checkExec checkExec}
     *                                  method doesn't allow creation of the subprocess
     * @throws IOException              If an I/O error occurs
     * @throws NullPointerException     If <code>command</code> is <code>null</code>,
     *                                  or one of the elements of <code>envp</code> is <code>null</code>
     * @throws IllegalArgumentException If <code>command</code> is empty
     * @see ProcessBuilder
     * @since 1.3
     */
    public Process exec(String command, String[] envp, File dir)
            throws IOException {
        if (command.length() == 0) {
            throw new IllegalArgumentException("Empty command");
        }

        StringTokenizer st = new StringTokenizer(command);
        String[] cmdarray = new String[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++) {
            cmdarray[i] = st.nextToken();
        }
        return exec(cmdarray, envp, dir);
    }

    /**
     * Executes the specified command and arguments in a separate process.
     * <p/>
     * <p>This is a convenience method.  An invocation of the form
     * <tt>exec(cmdarray)</tt>
     * behaves in exactly the same way as the invocation
     * <tt>{@link #exec(String[], String[], File) exec}(cmdarray, null, null)</tt>.
     *
     * @param cmdarray array containing the command to call and
     *                 its arguments.
     * @return A new {@link Process} object for managing the subprocess
     * @throws SecurityException         If a security manager exists and its
     *                                   {@link SecurityManager#checkExec checkExec}
     *                                   method doesn't allow creation of the subprocess
     * @throws IOException               If an I/O error occurs
     * @throws NullPointerException      If <code>cmdarray</code> is <code>null</code>,
     *                                   or one of the elements of <code>cmdarray</code> is <code>null</code>
     * @throws IndexOutOfBoundsException If <code>cmdarray</code> is an empty array
     *                                   (has length <code>0</code>)
     * @see ProcessBuilder
     */
    public Process exec(String cmdarray[]) throws IOException {
        return exec(cmdarray, null, null);
    }

    /**
     * Executes the specified command and arguments in a separate process
     * with the specified environment.
     * <p/>
     * <p>This is a convenience method.  An invocation of the form
     * <tt>exec(cmdarray, envp)</tt>
     * behaves in exactly the same way as the invocation
     * <tt>{@link #exec(String[], String[], File) exec}(cmdarray, envp, null)</tt>.
     *
     * @param cmdarray array containing the command to call and
     *                 its arguments.
     * @param envp     array of strings, each element of which
     *                 has environment variable settings in the format
     *                 <i>name</i>=<i>value</i>, or
     *                 <tt>null</tt> if the subprocess should inherit
     *                 the environment of the current process.
     * @return A new {@link Process} object for managing the subprocess
     * @throws SecurityException         If a security manager exists and its
     *                                   {@link SecurityManager#checkExec checkExec}
     *                                   method doesn't allow creation of the subprocess
     * @throws IOException               If an I/O error occurs
     * @throws NullPointerException      If <code>cmdarray</code> is <code>null</code>,
     *                                   or one of the elements of <code>cmdarray</code> is <code>null</code>,
     *                                   or one of the elements of <code>envp</code> is <code>null</code>
     * @throws IndexOutOfBoundsException If <code>cmdarray</code> is an empty array
     *                                   (has length <code>0</code>)
     * @see ProcessBuilder
     */
    public Process exec(String[] cmdarray, String[] envp) throws IOException {
        return exec(cmdarray, envp, null);
    }


    /**
     * Executes the specified command and arguments in a separate process with
     * the specified environment and working directory.
     * <p/>
     * <p>Given an array of strings <code>cmdarray</code>, representing the
     * tokens of a command line, and an array of strings <code>envp</code>,
     * representing "environment" variable settings, this method creates
     * a new process in which to execute the specified command.
     * <p/>
     * <p>This method checks that <code>cmdarray</code> is a valid operating
     * system command.  Which commands are valid is system-dependent,
     * but at the very least the command must be a non-empty list of
     * non-null strings.
     * <p/>
     * <p>If <tt>envp</tt> is <tt>null</tt>, the subprocess inherits the
     * environment settings of the current process.
     * <p/>
     * <p>A minimal set of system dependent environment variables may
     * be required to start a process on some operating systems.
     * As a result, the subprocess may inherit additional environment variable
     * settings beyond those in the specified environment.
     * <p/>
     * <p>{@link ProcessBuilder#start()} is now the preferred way to
     * start a process with a modified environment.
     * <p/>
     * <p>The working directory of the new subprocess is specified by <tt>dir</tt>.
     * If <tt>dir</tt> is <tt>null</tt>, the subprocess inherits the
     * current working directory of the current process.
     * <p/>
     * <p>If a security manager exists, its
     * {@link SecurityManager#checkExec checkExec}
     * method is invoked with the first component of the array
     * <code>cmdarray</code> as its argument. This may result in a
     * {@link SecurityException} being thrown.
     * <p/>
     * <p>Starting an operating system process is highly system-dependent.
     * Among the many things that can go wrong are:
     * <ul>
     * <li>The operating system program file was not found.
     * <li>Access to the program file was denied.
     * <li>The working directory does not exist.
     * </ul>
     * <p/>
     * <p>In such cases an exception will be thrown.  The exact nature
     * of the exception is system-dependent, but it will always be a
     * subclass of {@link IOException}.
     *
     * @param cmdarray array containing the command to call and
     *                 its arguments.
     * @param envp     array of strings, each element of which
     *                 has environment variable settings in the format
     *                 <i>name</i>=<i>value</i>, or
     *                 <tt>null</tt> if the subprocess should inherit
     *                 the environment of the current process.
     * @param dir      the working directory of the subprocess, or
     *                 <tt>null</tt> if the subprocess should inherit
     *                 the working directory of the current process.
     * @return A new {@link Process} object for managing the subprocess
     * @throws SecurityException         If a security manager exists and its
     *                                   {@link SecurityManager#checkExec checkExec}
     *                                   method doesn't allow creation of the subprocess
     * @throws IOException               If an I/O error occurs
     * @throws NullPointerException      If <code>cmdarray</code> is <code>null</code>,
     *                                   or one of the elements of <code>cmdarray</code> is <code>null</code>,
     *                                   or one of the elements of <code>envp</code> is <code>null</code>
     * @throws IndexOutOfBoundsException If <code>cmdarray</code> is an empty array
     *                                   (has length <code>0</code>)
     * @see ProcessBuilder
     * @since 1.3
     */
    public Process exec(String[] cmdarray, String[] envp, File dir)
            throws IOException {
        // 进程启动
        return new ProcessBuilder(cmdarray)
                .environment(envp)
                .directory(dir)
                .start();
    }


    /**
     * Returns the number of processors available to the Java virtual machine.
     * <p>
     * 返回Java虚拟机的可用处理器的数量。
     * <p> This value may change during a particular invocation of the virtual
     * machine.  Applications that are sensitive to the number of available
     * processors should therefore occasionally poll this property and adjust
     * their resource usage appropriately. </p>
     *
     * @return the maximum number of processors available to the virtual
     * machine; never smaller than one
     * @since 1.4
     */
    public native int availableProcessors();


    // JVM运行时状态(监控)

    /**
     * Returns the amount of free memory in the Java Virtual Machine.
     * Calling the
     * <code>gc</code> method may result in increasing the value returned
     * by <code>freeMemory.</code>
     * 返回Java虚拟机中可用内存的数量。
     * 调用 {@link #gc()} 方法可能会导致返回的值增加
     *
     * @return an approximation to the total amount of memory currently
     * available for future allocated objects, measured in bytes. (当前可用于未来分配对象的内存总量)
     */
    public native long freeMemory();

    /**
     * Returns the total amount of memory in the Java virtual machine.
     * The value returned by this method may vary over time, depending on
     * the host environment.
     * 返回Java虚拟机中的内存总量。
     * <p/>
     * Note that the amount of memory required to hold an object of any
     * given type may be implementation-dependent.
     *
     * @return the total amount of memory currently available for current
     * and future objects, measured in bytes. (为当前和未来的对象可用的当前内存总量)
     */
    public native long totalMemory();

    /**
     * Returns the maximum amount of memory that the Java virtual machine will
     * attempt to use.  If there is no inherent limit then the value {@link
     * java.lang.Long#MAX_VALUE} will be returned. </p>
     * 返回Java虚拟机将尝试使用的最大内存量。
     *
     * @return the maximum amount of memory that the virtual machine will
     * attempt to use, measured in bytes (虚拟机将尝试使用的最大内存量)
     * @since 1.4
     */
    public native long maxMemory();


    /**
     * Runs the garbage collector.
     * Calling this method suggests that the Java virtual machine expend
     * effort toward recycling unused objects in order to make the memory
     * they currently occupy available for quick reuse. When control
     * returns from the method call, the virtual machine has made
     * its best effort to recycle all discarded objects.
     * 运行垃圾收集器。
     * <p/>
     * The name <code>gc</code> stands for "garbage
     * collector". The virtual machine performs this recycling
     * process automatically as needed, in a separate thread, even if the
     * <code>gc</code> method is not invoked explicitly.
     * <p/>
     * The method {@link System#gc()} is the conventional and convenient
     * means of invoking this method.
     */
    public native void gc();

    /* Wormhole for calling java.lang.ref.Finalizer.runFinalization */
    private static native void runFinalization0();

    /**
     * Runs the finalization methods of any objects pending finalization.
     * Calling this method suggests that the Java virtual machine expend
     * effort toward running the <code>finalize</code> methods of objects
     * that have been found to be discarded but whose <code>finalize</code>
     * methods have not yet been run. When control returns from the
     * method call, the virtual machine has made a best effort to
     * complete all outstanding finalizations.
     * <p/>
     * The virtual machine performs the finalization process
     * automatically as needed, in a separate thread, if the
     * <code>runFinalization</code> method is not invoked explicitly.
     * <p/>
     * The method {@link System#runFinalization()} is the conventional
     * and convenient means of invoking this method.
     *
     * @see java.lang.Object#finalize()
     */
    public void runFinalization() {
        runFinalization0();
    }

    /**
     * Enables/Disables tracing of instructions. (启用/禁用跟踪指令)
     * If the <code>boolean</code> argument is <code>true</code>, this
     * method suggests that the Java virtual machine emit debugging
     * information for each instruction in the virtual machine as it
     * is executed. The format of this information, and the file or other
     * output stream to which it is emitted, depends on the host environment.
     * The virtual machine may ignore this request if it does not support
     * this feature. The destination of the trace output is system
     * dependent.
     * <p/>
     * If the <code>boolean</code> argument is <code>false</code>, this
     * method causes the virtual machine to stop performing the
     * detailed instruction trace it is performing.
     *
     * @param on <code>true</code> to enable instruction tracing;
     *           <code>false</code> to disable this feature.
     */
    public native void traceInstructions(boolean on);

    /**
     * Enables/Disables tracing of method calls. (启用/禁用跟踪方法调用)
     * If the <code>boolean</code> argument is <code>true</code>, this
     * method suggests that the Java virtual machine emit debugging
     * information for each method in the virtual machine as it is
     * called. The format of this information, and the file or other output
     * stream to which it is emitted, depends on the host environment. The
     * virtual machine may ignore this request if it does not support
     * this feature.
     * <p/>
     * Calling this method with argument false suggests that the
     * virtual machine cease emitting per-call debugging information.
     *
     * @param on <code>true</code> to enable instruction tracing;
     *           <code>false</code> to disable this feature.
     */
    public native void traceMethodCalls(boolean on);


    /**
     * Loads the specified filename as a dynamic library. The filename
     * argument must be a complete path name,
     * (for example
     * <code>Runtime.getRuntime().load("/home/avh/lib/libX11.so");</code>).
     * 加载指定的文件名作为动态库。
     * <p/>
     * First, if there is a security manager, its <code>checkLink</code>
     * method is called with the <code>filename</code> as its argument.
     * This may result in a security exception.
     * <p/>
     * This is similar to the method {@link #loadLibrary(String)}, but it
     * accepts a general file name as an argument rather than just a library
     * name, allowing any file of native code to be loaded.
     * <p/>
     * The method {@link System#load(String)} is the conventional and
     * convenient means of invoking this method.
     *
     * @param filename the file to load.
     * @throws SecurityException    if a security manager exists and its
     *                              <code>checkLink</code> method doesn't allow
     *                              loading of the specified dynamic library
     * @throws UnsatisfiedLinkError if the file does not exist.
     * @throws NullPointerException if <code>filename</code> is
     *                              <code>null</code>
     * @see java.lang.Runtime#getRuntime()
     * @see java.lang.SecurityException
     * @see java.lang.SecurityManager#checkLink(java.lang.String)
     */
    @CallerSensitive
    public void load(String filename) {
        load0(Reflection.getCallerClass(), filename);
    }

    synchronized void load0(Class fromClass, String filename) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkLink(filename);
        }
        if (!(new File(filename).isAbsolute())) {
            throw new UnsatisfiedLinkError(
                    "Expecting an absolute path of the library: " + filename);
        }
        ClassLoader.loadLibrary(fromClass, filename, true);
    }

    /**
     * Loads the dynamic library with the specified library name.
     * A file containing native code is loaded from the local file system
     * from a place where library files are conventionally obtained. The
     * details of this process are implementation-dependent. The
     * mapping from a library name to a specific filename is done in a
     * system-specific manner.
     * <p/>
     * First, if there is a security manager, its <code>checkLink</code>
     * method is called with the <code>libname</code> as its argument.
     * This may result in a security exception.
     * <p/>
     * The method {@link System#loadLibrary(String)} is the conventional
     * and convenient means of invoking this method. If native
     * methods are to be used in the implementation of a class, a standard
     * strategy is to put the native code in a library file (call it
     * <code>LibFile</code>) and then to put a static initializer:
     * <blockquote><pre>
     * static { System.loadLibrary("LibFile"); }
     * </pre></blockquote>
     * within the class declaration. When the class is loaded and
     * initialized, the necessary native code implementation for the native
     * methods will then be loaded as well.
     * <p/>
     * If this method is called more than once with the same library
     * name, the second and subsequent calls are ignored.
     *
     * @param libname the name of the library.
     * @throws SecurityException    if a security manager exists and its
     *                              <code>checkLink</code> method doesn't allow
     *                              loading of the specified dynamic library
     * @throws UnsatisfiedLinkError if the library does not exist.
     * @throws NullPointerException if <code>libname</code> is
     *                              <code>null</code>
     * @see java.lang.SecurityException
     * @see java.lang.SecurityManager#checkLink(java.lang.String)
     */
    @CallerSensitive
    public void loadLibrary(String libname) {
        loadLibrary0(Reflection.getCallerClass(), libname);
    }

    synchronized void loadLibrary0(Class fromClass, String libname) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkLink(libname);
        }
        if (libname.indexOf((int) File.separatorChar) != -1) {
            throw new UnsatisfiedLinkError(
                    "Directory separator should not appear in library name: " + libname);
        }
        ClassLoader.loadLibrary(fromClass, libname, false);
    }

}
