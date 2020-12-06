package java.util.function;

/**
 * Represents a function that produces an int-valued result.  This is the
 * {@code int}-producing primitive specialization for {@link Function}.
 * 表示生成整数值结果的函数。
 * 从对象类型T到int的一元函数。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #applyAsInt(Object)}.
 *
 * @param <T> the type of the input to the function 函数入参的类型
 *
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface ToIntFunction<T> {

    /**
     * Applies this function to the given argument.
     * 应用本函数到给定的参数。
     *
     * @param value the function argument 函数参数
     * @return the function result 函数结果
     */
    int applyAsInt(T value);
}
