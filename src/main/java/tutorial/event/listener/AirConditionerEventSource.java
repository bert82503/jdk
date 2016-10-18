package tutorial.event.listener;

import java.util.HashSet;
import java.util.Set;

/**
 * 空调事件源（空调遥控器）。
 *
 * @author xingle
 * @since 2016年08月21日 23:45
 */
public class AirConditionerEventSource {

    private final Set<AirConditionerSwitchListener> switchListeners = new HashSet<>();


    /**
     * 添加一个开关监听器。
     *
     * @param switchListener 开关监听器
     */
    public void addSwitchListener(AirConditionerSwitchListener switchListener) {
        synchronized (switchListeners) {
            switchListeners.add(switchListener);
        }
    }

    /**
     * 移除一个开关监听器。
     *
     * @param switchListener 开关监听器
     */
    public void removeSwitchListener(AirConditionerSwitchListener switchListener) {
        synchronized (switchListeners) {
            switchListeners.remove(switchListener);
        }
    }

    // 触发事件

    /**
     * 触发打开空调事件。
     */
    public void openSwitch() {
        AirConditionerSwitchEvent event = new AirConditionerSwitchEvent(
                new AirConditioner().setSwitchState(SwitchState.OPEN));
        this.notifyAllSwitchListener(event);
    }

    /**
     * 触发关闭空调事件。
     */
    public void closeSwitch() {
        AirConditionerSwitchEvent event = new AirConditionerSwitchEvent(
                new AirConditioner().setSwitchState(SwitchState.CLOSE));
        this.notifyAllSwitchListener(event);
    }

    /*
     * 通知所有的开关监听器。
     */
    private void notifyAllSwitchListener(AirConditionerSwitchEvent event) {
        if (event == null || event.getAirConditioner() == null) {
            return;
        }

        if (event.getAirConditioner().getSwitchState() == SwitchState.OPEN) {
            for (AirConditionerSwitchListener switchListener : switchListeners) {
                switchListener.switchOpened(event);
            }
        } else {
            for (AirConditionerSwitchListener switchListener : switchListeners) {
                switchListener.switchClosed(event);
            }
        }
    }
}
