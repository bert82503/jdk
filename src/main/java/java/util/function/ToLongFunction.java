package java.util.function;

/**
 * Represents a function that produces a long-valued result.  This is the
 * {@code long}-producing primitive specialization for {@link Function}.
 * 表示生成长整数值结果的函数。
 * 从对象类型T到long的一元函数。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #applyAsLong(Object)}.
 *
 * @param <T> the type of the input to the function 函数入参的类型
 *
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface ToLongFunction<T> {

    /**
     * Applies this function to the given argument.
     * 应用本函数到给定的参数。
     *
     * @param value the function argument 函数参数
     * @return the function result 函数参数
     */
    long applyAsLong(T value);
}
