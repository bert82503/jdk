package java.util.function;

/**
 * Represents an operation on a single operand that produces a result of the
 * same type as its operand.  This is a specialization of {@code Function} for
 * the case where the operand and result are of the same type.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object)}.
 *
 * @param <T> the type of the operand and result of the operator 运算符的操作数和结果的类型
 *
 * @see Function
 * @since 1.8
 */
// 一元运算符(单个操作数，生产与操作数类型相同的结果)
public interface UnaryOperator<T> extends Function<T, T> {

    // 返回总是返回其入参的一元运算符
    /**
     * Returns a unary operator that always returns its input argument.
     *
     * @param <T> the type of the input and output of the operator
     * @return a unary operator that always returns its input argument
     */
    static <T> UnaryOperator<T> identity() {
        return t -> t;
    }
}
