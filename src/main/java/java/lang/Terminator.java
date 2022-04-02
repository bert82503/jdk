
package java.lang;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Package-private utility class for setting up and tearing down
 * platform-specific support for termination-triggered shutdowns.
 * 安装和拆除支持终止触发关闭特定平台的辅助类。
 *
 * @author   Mark Reinhold
 * @since    1.3
 */
class Terminator {

    /**
     * 信号处理程序
     */
    private static SignalHandler handler = null;

    /** Invocations of setup and tear down are already synchronized
     * on the shutdown lock, so no further synchronization is needed here
     * 安装和拆除的调用是基于关闭锁同步的。
     * 设置Linux软中断信号处理程序(HUP：终端挂起、INT：键盘中断、TERM：终止)。
     */
    static void setup() {
        // 初始化信号处理程序
        if (handler != null) {
            return;
        }
        SignalHandler sh = new SignalHandler() {
            @Override
            public void handle(Signal sig) {
                // 退出虚拟机
                Shutdown.exit(sig.getNumber() + 0200);
            }
        };
        handler = sh;

        // When -Xrs is specified the user is responsible for
        // ensuring that shutdown hooks are run by calling
        // System.exit()
        // 当 -Xrs 参数被指定时，用户负责确保通过调用 System.exit() 来运行关闭挂钩
        try {
            // 终端挂起或者控制进程终止
            Signal.handle(new Signal("HUP"), sh);
        } catch (IllegalArgumentException e) {
            // 忽略异常
        }
        try {
            // 键盘中断（同 Ctrl+C）
            Signal.handle(new Signal("INT"), sh);
        } catch (IllegalArgumentException e) {
            // 忽略异常
        }
        try {
            // 终止信号
            Signal.handle(new Signal("TERM"), sh);
        } catch (IllegalArgumentException e) {
            // 忽略异常
        }
    }

    static void teardown() {
        /* The current sun.misc.Signal class does not support
         * the cancellation of handlers
         * 当前 Signal 类不支持取消信号处理程序
         */
    }

}
