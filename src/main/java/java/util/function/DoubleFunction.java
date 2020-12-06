package java.util.function;

/**
 * Represents a function that accepts a double-valued argument and produces a
 * result.  This is the {@code double}-consuming primitive specialization for
 * {@link Function}.
 * 浮点值函数，表示接受一个浮点值参数并生成结果的函数。
 * 从double到R类型的一元函数。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(double)}.
 *
 * @param <R> the type of the result of the function 函数结果的类型
 *
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface DoubleFunction<R> {

    /**
     * Applies this function to the given argument.
     * 应用本函数到给定的浮点值参数。
     *
     * @param value the function argument 函数参数
     * @return the function result 函数结果
     */
    R apply(double value);
}
