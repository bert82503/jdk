package java.util.function;

/**
 * Represents a function that accepts two arguments and produces an int-valued
 * result.  This is the {@code int}-producing primitive specialization for
 * {@link BiFunction}.
 * 从T、U到int的二元函数。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #applyAsInt(Object, Object)}.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 *
 * @see BiFunction
 * @since 1.8
 */
@FunctionalInterface
public interface ToIntBiFunction<T, U> {

    /**
     * Applies this function to the given arguments.
     * 应用本函数到所有给定的参数。
     *
     * @param t the first function argument 第一个函数参数
     * @param u the second function argument 第二个函数参数
     * @return the function result 函数结果
     */
    int applyAsInt(T t, U u);
}
