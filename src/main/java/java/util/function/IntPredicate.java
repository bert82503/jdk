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
 * Represents a predicate (boolean-valued function) of one {@code int}-valued
 * argument. This is the {@code int}-consuming primitive type specialization of
 * {@link Predicate}.
 * 布尔值函数，表示具有一个整数值参数的谓词。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #test(int)}.
 *
 * @see Predicate
 * @since 1.8
 */
@FunctionalInterface
public interface IntPredicate {

    /**
     * Evaluates this predicate on the given argument.
     * 对给定的参数计算本谓词。
     *
     * @param value the input argument 输入参数
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false} 如果输入参数匹配谓词，则返回true；否则，返回false。
     */
    boolean test(int value);

    // 谓词函数：与、非、或
    // 默认函数

    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * AND of this predicate and another.  When evaluating the composed
     * predicate, if this predicate is {@code false}, then the {@code other}
     * predicate is not evaluated.
     * 返回复合谓词，表示本谓词和另一个谓词的短路逻辑和。
     * 在计算复合谓词时，如果本谓词为假，则不计算其他谓词。
     * this && other
     *
     * <p>Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     * 在计算任一谓词期间抛出的任何异常都将转发给调用者；
     * 如果本谓词的计算抛出异常，则不会计算其他谓词。
     *
     * @param other a predicate that will be logically-ANDed with this
     *              predicate
     * @return a composed predicate that represents the short-circuiting logical
     * AND of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    default IntPredicate and(IntPredicate other) {
        Objects.requireNonNull(other);
        return (value) -> test(value) && other.test(value);
    }

    /**
     * Returns a predicate that represents the logical negation of this
     * predicate.
     * 返回一个谓词，表示本谓词的逻辑否定。
     * !this
     *
     * @return a predicate that represents the logical negation of this
     * predicate
     */
    default IntPredicate negate() {
        return (value) -> !test(value);
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * OR of this predicate and another.  When evaluating the composed
     * predicate, if this predicate is {@code true}, then the {@code other}
     * predicate is not evaluated.
     * 返回组合谓词，表示本谓词和另一个谓词的短路逻辑或。
     * 在计算复合谓词时，如果本谓词为真，则不计算其他谓词。
     * this || other
     *
     * <p>Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     * 在计算任一谓词期间抛出的任何异常都将转发给调用者；
     * 如果本谓词的计算抛出异常，则不会计算其他谓词。
     *
     * @param other a predicate that will be logically-ORed with this
     *              predicate
     * @return a composed predicate that represents the short-circuiting logical
     * OR of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    default IntPredicate or(IntPredicate other) {
        Objects.requireNonNull(other);
        return (value) -> test(value) || other.test(value);
    }
}
