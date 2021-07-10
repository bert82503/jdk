package java.util.function;

/**
 * Represents an operation upon two {@code double}-valued operands and producing a
 * {@code double}-valued result.   This is the primitive type specialization of
 * {@link BinaryOperator} for {@code double}.
 * 二元浮点数运算符
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #applyAsDouble(double, double)}.
 *
 * @see BinaryOperator
 * @see DoubleUnaryOperator
 * @since 1.8
 */
@FunctionalInterface
public interface DoubleBinaryOperator {

    /**
     * Applies this operator to the given operands.
     * 应用本运算符到所有给定的参数。
     *
     * @param left the first operand
     * @param right the second operand
     * @return the operator result
     */
    double applyAsDouble(double left, double right);
}
