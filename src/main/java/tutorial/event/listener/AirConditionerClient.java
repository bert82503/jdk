package tutorial.event.listener;

import java.util.concurrent.TimeUnit;

/**
 * 空调客户端程序，事件监听器完整的处理过程（想象成要操控空调的那个人）。
 *
 * @author xingle
 * @since 2016年08月21日 23:58
 */
public class AirConditionerClient {

    public static void main(String[] args) throws InterruptedException {
        // 1. 定义事件源
        AirConditionerEventSource eventSource = new AirConditionerEventSource();

        // 2. 定义事件监听器并注册到事件源（发布并订阅空调开关事件）
        eventSource.addSwitchListener(new AirConditionerSwitchListener() { // 空调-1
            @Override
            public void switchOpened(AirConditionerSwitchEvent event) {
                if (event.getAirConditioner().getSwitchState() == SwitchState.OPEN) {
                    System.out.println("'空调-1' 已经打开");
                    System.out.println();
                }
            }

            @Override
            public void switchClosed(AirConditionerSwitchEvent event) {
                if (event.getAirConditioner().getSwitchState() == SwitchState.CLOSE) {
                    System.out.println("'空调-1' 已经关闭");
                    System.out.println();
                }
            }
        });
//        eventSource.addSwitchListener(new AirConditionerSwitchListener() { // 空调-2
//            @Override
//            public void switchOpened(AirConditionerSwitchEvent event) {
//                if (event.getAirConditioner().getSwitchState() == SwitchState.OPEN) {
//                    System.out.println("'空调-2' 已经打开");
//                }
//            }
//
//            @Override
//            public void switchClosed(AirConditionerSwitchEvent event) {
//                if (event.getAirConditioner().getSwitchState() == SwitchState.CLOSE) {
//                    System.out.println("'空调-2' 已经关闭");
//                }
//            }
//        });

        // 3. 触发事件通知
        eventSource.openSwitch();
        TimeUnit.SECONDS.sleep(3L); // 开3秒钟
        eventSource.closeSwitch();
    }

}
