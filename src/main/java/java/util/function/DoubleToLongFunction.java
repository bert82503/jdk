package java.util.function;

/**
 * Represents a function that accepts a double-valued argument and produces a
 * long-valued result.  This is the {@code double}-to-{@code long} primitive
 * specialization for {@link Function}.
 * 表示接受一个浮点值参数并生成长整数值结果的函数。
 * 从double到long的一元函数。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #applyAsLong(double)}.
 *
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface DoubleToLongFunction {

    /**
     * Applies this function to the given argument.
     * 应用本函数到给定的参数。
     *
     * @param value the function argument 函数参数
     * @return the function result 函数结果
     */
    long applyAsLong(double value);
}
