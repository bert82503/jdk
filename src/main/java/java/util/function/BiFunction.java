
package java.util.function;

import java.util.Objects;

/**
 * Represents a function that accepts two arguments and produces a result.
 * This is the two-arity specialization of {@link Function}.
 * 从T、U到R的二元函数，表示接受两个参数并生成一个结果的二元函数。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object)}.
 *
 * @param <T> the type of the first argument to the function 函数的第一个参数类型
 * @param <U> the type of the second argument to the function 函数的第二个参数类型
 * @param <R> the type of the result of the function 函数的结果类型
 *
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface BiFunction<T, U, R> {

    /**
     * Applies this function to the given arguments.
     * 应用本函数到所有给定的参数。
     *
     * @param t the first function argument 第一个函数参数
     * @param u the second function argument 第二个函数参数
     * @return the function result 函数结果
     */
    R apply(T t, U u);

    // 混合的二元一元组合函数
    // 默认函数

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     * 返回一个after的组合函数，首先应用本二元函数到其输入参数，然后应用after一元函数到中间结果。
     * this -> after ((T, U) -> R -> V)
     *
     * @param <V> the type of output of the {@code after} function, and of the
     *           composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     */
    default <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t, U u) -> after.apply(apply(t, u));
    }
}
