package java.util.function;

/**
 * Represents a function that accepts a long-valued argument and produces a
 * double-valued result.  This is the {@code long}-to-{@code double} primitive
 * specialization for {@link Function}.
 * 表示接受一个长整数值参数并生成浮点值结果的函数。
 * 从long到double的一元函数。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #applyAsDouble(long)}.
 *
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface LongToDoubleFunction {

    /**
     * Applies this function to the given argument.
     * 应用本函数到给定的参数。
     *
     * @param value the function argument 函数参数
     * @return the function result 函数结果
     */
    double applyAsDouble(long value);
}
