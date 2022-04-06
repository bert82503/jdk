
package java.util.function;

import java.util.Objects;

/**
 * Represents a predicate (boolean-valued function) of one argument.
 * 布尔值函数，表示具有一个参数的谓词。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #test(Object)}.
 *
 * @param <T> the type of the input to the predicate 谓词的入参类型
 *
 * @since 1.8
 */
@FunctionalInterface
public interface Predicate<T> {

    /**
     * Evaluates this predicate on the given argument.
     * 对给定的参数计算本谓词。
     *
     * @param t the input argument 输入参数
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false} 如果输入参数匹配谓词，则返回true；否则，返回false。
     */
    boolean test(T t);

    // 谓词组合函数：与、非、或
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
    default Predicate<T> and(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
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
    default Predicate<T> negate() {
        return (t) -> !test(t);
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
    default Predicate<T> or(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) || other.test(t);
    }

    // 测试两个参数是否相等
    // 静态函数

    /**
     * Returns a predicate that tests if two arguments are equal according
     * to {@link Objects#equals(Object, Object)}.
     * 返回一个谓词，本谓词根据对象测试两个参数是否相等。
     *
     * @param <T> the type of arguments to the predicate
     * @param targetRef the object reference with which to compare for equality,
     *               which may be {@code null}
     * @return a predicate that tests if two arguments are equal according
     * to {@link Objects#equals(Object, Object)}
     */
    static <T> Predicate<T> isEqual(Object targetRef) {
        return (null == targetRef)
                ? Objects::isNull
                : targetRef::equals;
    }
}
