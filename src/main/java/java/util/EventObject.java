/*
 * Copyright (c) 1996, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java.util;

/**
 * The root class from which all event state objects shall be derived.
 * <p>
 * All Events are constructed with a reference to the object, the "source",
 * that is logically deemed to be the object upon which the Event in question
 * initially occurred upon.
 *
 * <p>
 * 所有事件状态对象都将从其派生的根类。
 * <p>
 * 所有事件在构造时都引用了事件源("source")，在逻辑上认为该对象是最初发生有关事件的对象。
 *
 * @since JDK1.1
 */
// [事件监听机制] 事件状态对象
public class EventObject implements java.io.Serializable {

    private static final long serialVersionUID = 5516075349620653480L;

    /**
     * The object on which the Event initially occurred.
     * <p>
     * 事件源：事件最初发生的对象。(触发事件的源头)
     */
    protected transient Object source; // transient：不需要序列化的属性

    /**
     * Constructs a prototypical Event.
     * <p>
     * 构造一个典型的事件。
     *
     * @param    source    The object on which the Event initially occurred. (事件最初发生的对象)
     * @exception  IllegalArgumentException  if source is null. (非法参数异常)
     */
    public EventObject(Object source) {
        if (source == null) {
            throw new IllegalArgumentException("null source");
        }

        this.source = source;
    }

    /**
     * Returns the object on which the Event initially occurred.
     * <p>
     * 返回事件最初发生的对象(事件源)。
     *
     * @return   The object on which the Event initially occurred. (事件最初发生的对象)
     */
    // 核心方法 返回事件最初发生的对象(事件源)
    public Object getSource() {
        return source;
    }

    /**
     * Returns a String representation of this EventObject.
     * <p>
     * 返回该事件状态对象的字符串表示形式。
     *
     * @return  A String representation of this EventObject. (这个事件状态对象的字符串表示形式)
     */
    @Override
    public String toString() {
        return getClass().getName() + "[source=" + source + "]";
    }
}
