
package java.util.function;

import java.util.Objects;

/**
 * Represents a function that accepts one argument and produces a result.
 * 从T到R的一元函数，表示接受一个参数并产生一个结果的一元函数。
 * 类型转换体系
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object)}.
 *
 * @param <T> the type of the input to the function 函数入参的类型
 * @param <R> the type of the result of the function 函数结果的类型
 *
 * @since 1.8
 */
@FunctionalInterface
public interface Function<T, R> {

    /**
     * Applies this function to the given argument.
     * 应用本函数到给定的参数。
     *
     * @param t the function argument 函数参数
     * @return the function result 函数结果
     */
    R apply(T t);

    // 连续的组合函数(before、after)
    // 使用场景：责任链模式
    // 默认函数

    /**
     * Returns a composed function that first applies the {@code before}
     * function to its input, and then applies this function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     * 返回一个before的组合函数，首先应用before函数到其输入参数，然后应用本函数到中间结果。
     * before -> this (V -> T -> R)
     *
     * @param <V> the type of input to the {@code before} function, and to the
     *           composed function {@code before} 函数的输入参数类型
     * @param before the function to apply before this function is applied
     * @return a composed function that first applies the {@code before}
     * function and then applies this function
     * @throws NullPointerException if before is null
     *
     * @see #andThen(Function)
     */
    default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     * 返回一个after的组合函数，首先应用本函数到其输入参数，然后应用after函数到中间结果。
     * this -> after (T -> R -> V)
     *
     * @param <V> the type of output of the {@code after} function, and of the
     *           composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     *
     * @see #compose(Function)
     */
    default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }

    // 等价函数
    // 静态函数

    /**
     * Returns a function that always returns its input argument.
     * 返回一个始终返回其输入参数的函数。
     *
     * {@code Function::identity()}
     *
     * @param <T> the type of the input and output objects to the function 函数的输入和输出对象的类型
     * @return a function that always returns its input argument
     */
    static <T> Function<T, T> identity() {
        return t -> t;
    }
}
