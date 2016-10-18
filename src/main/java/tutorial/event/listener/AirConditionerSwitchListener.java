package tutorial.event.listener;

/**
 * 空调开关监听器。
 *
 * @author xingle
 * @since 2016年08月21日 23:34
 */
public interface AirConditionerSwitchListener extends java.util.EventListener {

    /**
     * 开关被打开。
     *
     * @param event 空调开关事件
     */
    void switchOpened(AirConditionerSwitchEvent event);

    /**
     * 开关被关闭。
     *
     * @param event 空调开关事件
     */
    void switchClosed(AirConditionerSwitchEvent event);

}
