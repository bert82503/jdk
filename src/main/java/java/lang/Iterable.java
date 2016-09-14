/*
 * Copyright (c) 2003, 2010, Oracle and/or its affiliates. All rights reserved.
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

import java.util.Iterator;

/**
 * Implementing this interface allows an object to be the target of
 * the "foreach" statement.
 * <p>
 * 实现此接口允许对象为"foreach"语句的目标。
 *
 * @param <T> the type of elements returned by the iterator (由迭代器返回的元素类型)
 *
 * @since 1.5
 */
// 核心接口 [遍历] 可迭代的类型，允许对象为"foreach"语句的目标
public interface Iterable<T> {
    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    // 核心方法 返回一个迭代器
    Iterator<T> iterator();
}
