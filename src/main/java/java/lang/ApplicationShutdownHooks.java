/*
 * Copyright (c) 2005, 2010, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.lang;

import java.util.*;

/**
 * Class to track and run user level shutdown hooks registered through
 * <tt>{@link Runtime#addShutdownHook Runtime.addShutdownHook}</tt>.
 *
 * @see java.lang.Runtime#addShutdownHook
 * @see java.lang.Runtime#removeShutdownHook
 */
// 应用程序的关闭挂钩，用于跟踪和运行用户级别的关闭挂钩，通过Runtime.addShutdownHook注册
class ApplicationShutdownHooks {
    /* The set of registered hooks/注册的挂钩集合 */
    private static IdentityHashMap<Thread, Thread> hooks;
    static {
        try {
            Shutdown.add(1 /* shutdown hook invocation order/关闭挂钩的调用顺序 */,
                false /* not registered if shutdown in progress */,
                new Runnable() {
                    @Override
                    public void run() {
                        runHooks();
                    }
                }
            );
            hooks = new IdentityHashMap<>();
        } catch (IllegalStateException e) {
            // application shutdown hooks cannot be added if
            // shutdown is in progress.
            hooks = null;
        }
    }


    private ApplicationShutdownHooks() {}

    /* Add a new shutdown hook.  Checks the shutdown state and the hook itself,
     * but does not do any security checks.
     */
    // 添加一个新的关闭挂钩
    static synchronized void add(Thread hook) {
        if (hooks == null)
            throw new IllegalStateException("Shutdown in progress"); // 正在关闭中

        if (hook.isAlive())
            throw new IllegalArgumentException("Hook already running"); // 挂钩已经运行中

        if (hooks.containsKey(hook))
            throw new IllegalArgumentException("Hook previously registered"); // 挂钩先前已注册

        hooks.put(hook, hook);
    }

    /* Remove a previously-registered hook.  Like the add method, this method
     * does not do any security checks.
     */
    // 删除先前注册的关闭挂钩
    static synchronized boolean remove(Thread hook) {
        if (hooks == null)
            throw new IllegalStateException("Shutdown in progress");

        if (hook == null)
            throw new NullPointerException();

        return hooks.remove(hook) != null;
    }

    /* Iterates over all application hooks creating a new thread for each
     * to run in. Hooks are run concurrently and this method waits for
     * them to finish.
     * 循环地访问所有的应用程序关闭挂钩，为每个挂钩创建一个新的线程来运行它。
     * 挂钩是并发地运行，本方法会等待它们完成。
     */
    // 核心实现 并发地运行所有的应用程序关闭挂钩
    static void runHooks() {
        Collection<Thread> threads; // 并发地运行所有挂钩的线程集合
        synchronized (ApplicationShutdownHooks.class) { // 类型监视器
            threads = hooks.keySet();
            hooks = null;
        }

        // 执行所有挂钩线程
        for (Thread hook : threads) {
            hook.start();
        }
        // 等待所有挂钩死亡
        for (Thread hook : threads) {
            try {
                hook.join();
            } catch (InterruptedException x) { }
        }
    }
}
