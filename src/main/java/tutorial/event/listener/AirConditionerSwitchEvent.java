package tutorial.event.listener;

/**
 * 空调开关事件。
 *
 * @author xingle
 * @since 2016年08月21日 23:16
 */
public class AirConditionerSwitchEvent extends java.util.EventObject {

    private static final long serialVersionUID = 619777941669668785L;


    /**
     * Constructs a air conditioner switch event from the given source.
     *
     * @param source a air conditioner
     */
    public AirConditionerSwitchEvent(AirConditioner source) {
        super(source);
    }

    /**
     * Returns the air conditioner that changed.
     */
    public AirConditioner getAirConditioner() {
        return (AirConditioner) super.getSource(); // 基于 EventObject.getSource() 实现
    }
}
