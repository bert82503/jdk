package java.util.function;

import java.util.Objects;

/**
 * Represents an operation on a single {@code long}-valued operand that produces
 * a {@code long}-valued result.  This is the primitive type specialization of
 * {@link UnaryOperator} for {@code long}.
 * 一元长整数运算符。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #applyAsLong(long)}.
 *
 * @see UnaryOperator
 * @since 1.8
 */
@FunctionalInterface
public interface LongUnaryOperator {

    /**
     * Applies this operator to the given operand.
     * 应用本运算符到所有给定的参数。
     *
     * @param operand the operand
     * @return the operator result
     */
    long applyAsLong(long operand);

    // 连续组合的运算符(before、after)
    // 使用场景：责任链模式
    // 默认函数

    /**
     * Returns a composed operator that first applies the {@code before}
     * operator to its input, and then applies this operator to the result.
     * If evaluation of either operator throws an exception, it is relayed to
     * the caller of the composed operator.
     *
     * @param before the operator to apply before this operator is applied
     * @return a composed operator that first applies the {@code before}
     * operator and then applies this operator
     * @throws NullPointerException if before is null
     *
     * @see #andThen(LongUnaryOperator)
     */
    default LongUnaryOperator compose(LongUnaryOperator before) {
        Objects.requireNonNull(before);
        return (long v) -> applyAsLong(before.applyAsLong(v));
    }

    /**
     * Returns a composed operator that first applies this operator to
     * its input, and then applies the {@code after} operator to the result.
     * If evaluation of either operator throws an exception, it is relayed to
     * the caller of the composed operator.
     *
     * @param after the operator to apply after this operator is applied
     * @return a composed operator that first applies this operator and then
     * applies the {@code after} operator
     * @throws NullPointerException if after is null
     *
     * @see #compose(LongUnaryOperator)
     */
    default LongUnaryOperator andThen(LongUnaryOperator after) {
        Objects.requireNonNull(after);
        return (long t) -> after.applyAsLong(applyAsLong(t));
    }

    /**
     * Returns a unary operator that always returns its input argument.
     *
     * @return a unary operator that always returns its input argument
     */
    static LongUnaryOperator identity() {
        return t -> t;
    }
}
