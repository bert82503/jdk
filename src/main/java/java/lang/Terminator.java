/*
 * Copyright (c) 1999, 2001, Oracle and/or its affiliates. All rights reserved.
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

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Package-private utility class for setting up and tearing down
 * platform-specific support for termination-triggered shutdowns.
 *
 * @author   Mark Reinhold
 * @since    1.3
 */
// 终端器，安装及拆除由终端触发的关闭
class Terminator {

    // 中断信号处理程序
    private static SignalHandler handler = null;

    /* Invocations of setup and teardown are already synchronized
     * on the shutdown lock, so no further synchronization is needed here
     */
    // 安装及拆除的调用已同步
    static void setup() {
        if (handler != null) return;
        SignalHandler sh = new SignalHandler() {
            @Override
            public void handle(Signal sig) {
                // 关闭退出信号量
                Shutdown.exit(sig.getNumber() + 0200);
            }
        };
        handler = sh;
        // When -Xrs is specified the user is responsible for
        // ensuring that shutdown hooks are run by calling
        // System.exit()
        try {
            Signal.handle(new Signal("HUP"), sh); // 终端断线
        } catch (IllegalArgumentException e) {
        }
        try {
            Signal.handle(new Signal("INT"), sh); // 中断
        } catch (IllegalArgumentException e) {
        }
        try {
            Signal.handle(new Signal("TERM"), sh); // 终止
        } catch (IllegalArgumentException e) {
        }
    }

    static void teardown() {
        /* The current sun.misc.Signal class does not support
         * the cancellation of handlers
         */
    }

}
