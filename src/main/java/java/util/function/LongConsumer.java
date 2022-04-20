/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java.util.function;

import java.util.Objects;

/**
 * Represents an operation that accepts a single {@code long}-valued argument and
 * returns no result.  This is the primitive type specialization of
 * {@link Consumer} for {@code long}.  Unlike most other functional interfaces,
 * {@code LongConsumer} is expected to operate via side-effects.
 * 表示接受一个长整数值参数但不返回任何结果的操作。
 * 对象消费者被期望通过副作用操作。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(long)}.
 *
 * @see Consumer
 * @since 1.8
 */
@FunctionalInterface
public interface LongConsumer {

    /**
     * Performs this operation on the given argument.
     * 对给定的整数值参数执行本操作。
     *
     * @param value the input argument 输入参数
     */
    void accept(long value);

    // 使用场景：N个消费者模式，责任链模式
    // 默认函数

    /**
     * Returns a composed {@code LongConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     * 返回一个组合的对象消费者，本消费者按顺序执行本操作和after操作。
     * 如果执行任一操作抛出异常时，则该异常将被转发给组合操作的调用者。
     * 如果执行本操作抛出异常，则不会执行after操作。
     * this -> after
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code LongConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default LongConsumer andThen(LongConsumer after) {
        Objects.requireNonNull(after);
        return (long t) -> {
            accept(t);
            after.accept(t);
        };
    }
}
