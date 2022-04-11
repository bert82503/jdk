
package java.util.stream;

import java.util.function.Supplier;

/**
 * A {@link Sink} which accumulates state as elements are accepted, and allows
 * a result to be retrieved after the computation is finished.
 * 在元素被接受时累加状态的数据池，并允许在计算完成后检索结果。
 *
 * @param <T> the type of elements to be accepted
 *           要接受的元素类型
 * @param <R> the type of the result
 *           结果的类型
 *
 * @since 1.8
 */
interface TerminalSink<T, R> extends Sink<T>, Supplier<R> {
    // 结果提供者函数-Supplier
}
