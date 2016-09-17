/*
 * Copyright (c) 1994, 2005, Oracle and/or its affiliates. All rights reserved.
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

/**
 * The <code>Runnable</code> interface should be implemented by any
 * class whose instances are intended to be executed by a thread. The
 * class must define a method of no arguments called <code>run</code>.
 * <p>
 * 任何希望被线程执行的类实例都应该实现 Runnable 接口。
 * 该类必须定义一个称为 run() 的无参方法。
 * <p>
 * This interface is designed to provide a common protocol for objects that
 * wish to execute code while they are active. For example,
 * <code>Runnable</code> is implemented by class <code>Thread</code>.
 * Being active simply means that a thread has been started and has not
 * yet been stopped.
 * <p>
 * 此接口的目的是提供一个共同的协议，为对象在他们活跃时希望执行代码。
 * 活跃意味着线程已启动，并且尚未停止。
 * <p>
 * In addition, <code>Runnable</code> provides the means for a class to be
 * active while not subclassing <code>Thread</code>. A class that implements
 * <code>Runnable</code> can run without subclassing <code>Thread</code>
 * by instantiating a <code>Thread</code> instance and passing itself in
 * as the target.  In most cases, the <code>Runnable</code> interface should
 * be used if you are only planning to override the <code>run()</code>
 * method and no other <code>Thread</code> methods.
 * This is important because classes should not be subclassed
 * unless the programmer intends on modifying or enhancing the fundamental
 * behavior of the class.
 * <p>
 * 此外，Runnable 提供了类是活跃的手段而非线程子类。
 * 一个实现 Runnable 接口的类可以通过实例化一个线程({@link Thread})实例并传递它自己给目标对象。
 * 在大多数情况下，应使用 Runnable 接口，如果你只是计划覆盖 run() 方法，无其他 Thread 方法。
 * 这很重要，因为类可以不被子类化，除非程序员打算修改或加强类的基本行为。
 *
 * @author  Arthur van Hoff
 * @see     java.lang.Thread
 * @see     java.util.concurrent.Callable
 * @since   JDK1.0
 */
// 核心接口 [线程] 可运行的类型，任何希望被线程执行的类实例都应该实现Runnable接口
public interface Runnable {
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * 当对象用于创建线程，启动线程会在单独的执行线程中调用该对象的 run 方法。
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see     java.lang.Thread#run()
     */
    // 核心方法 运行线程
    void run();
}
