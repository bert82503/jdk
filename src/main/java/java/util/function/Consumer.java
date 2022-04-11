
package java.util.function;

import java.util.Objects;

/**
 * Represents an operation that accepts a single input argument and returns no
 * result. Unlike most other functional interfaces, {@code Consumer} is expected
 * to operate via side-effects.
 * 从T到void的一元函数，表示接受一个输入参数但不返回任何结果的操作。
 * 对象消费者被期望通过副作用操作。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object)}.
 *
 * @param <T> the type of the input to the operation
 *           操作的入参类型
 *
 * @since 1.8
 */
@FunctionalInterface
public interface Consumer<T> {

    /**
     * Performs this operation on the given argument.
     * 对给定的参数执行本操作。
     *
     * @param t the input argument 输入参数
     */
    void accept(T t);

    // 使用场景：N个消费者模式，责任链模式
    // 默认函数

    /**
     * Returns a composed {@code Consumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     * 返回一个组合的对象消费者，本消费者按顺序执行本操作和after操作。
     * 如果执行任一操作抛出异常时，则该异常将被转发给组合操作的调用者。
     * 如果执行本操作抛出异常，则不会执行after操作。
     * this -> after
     *
     * @param after the operation to perform after this operation 在本操作后要执行的操作
     * @return a composed {@code Consumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default Consumer<T> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }
}
