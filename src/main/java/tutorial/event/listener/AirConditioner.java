package tutorial.event.listener;

/**
 * 空调。
 *
 * @author xingle
 * @since 2016年08月21日 23:14
 */
public class AirConditioner {

    /** 开关状态 */
    private SwitchState switchState;


    public SwitchState getSwitchState() {
        return switchState;
    }

    public AirConditioner setSwitchState(SwitchState switchState) {
        this.switchState = switchState;
        return this;
    }
}
