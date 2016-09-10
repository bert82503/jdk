/*
 * Copyright (c) 2000, 2004, Oracle and/or its affiliates. All rights reserved.
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
 * An abstract wrapper class for an {@code EventListener} class
 * which associates a set of additional parameters with the listener.
 * Subclasses must provide the storage and accessor methods
 * for the additional arguments or parameters.
 * <p>
 * For example, a bean which supports named properties
 * would have a two argument method signature for adding
 * a {@code PropertyChangeListener} for a property:
 * <pre>
 * public void addPropertyChangeListener(String propertyName,
 *                                       PropertyChangeListener listener)
 * </pre>
 * If the bean also implemented the zero argument get listener method:
 * <pre>
 * public PropertyChangeListener[] getPropertyChangeListeners()
 * </pre>
 * then the array may contain inner {@code PropertyChangeListeners}
 * which are also {@code PropertyChangeListenerProxy} objects.
 * <p>
 * If the calling method is interested in retrieving the named property
 * then it would have to test the element to see if it is a proxy class.
 *
 * <p>
 * 事件监听器类的一个抽象包装类，其将一组附加的参数与监听器关联。
 * 子类必须为附加的方法参数提供存储和访问器方法。
 * <p>
 * 例如，支持命名属性的组件会有两个参数的方法签名，为一个属性添加属性更改监听器：
 * 如果组件实现了零参数的获取监听器列表的方法：
 * 然后，数组可能包含属性更改监听器代理对象在内的属性更改监听器列表。
 * <p>
 * 如果调用方法有兴趣检索命名的属性，那就必须测试组件看看它是不是一个代理类。
 *
 * @since 1.4
 */
// [事件监听机制] 事件监听器代理，实现事件监听器接口
public abstract class EventListenerProxy<T extends EventListener>
        implements EventListener {

    /**
     * 背后的事件监听器对象
     */
    private final T listener;

    /**
     * Creates a proxy for the specified listener.
     * <p>
     * 为指定的监听器创建一个代理。
     *
     * @param listener  the listener object (事件监听器对象)
     */
    public EventListenerProxy(T listener) {
        this.listener = listener;
    }

    /**
     * Returns the listener associated with the proxy.
     * <p>
     * 返回与代理关联的事件监听器。
     *
     * @return  the listener associated with the proxy (与代理关联的事件监听器)
     */
    public T getListener() {
        return listener;
    }
}
