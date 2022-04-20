
package java.util.function;

/**
 * Represents a supplier of results.
 * 结果生产者，表示返回结果的提供者。
 *
 * <p>There is no requirement that a new or distinct result be returned each
 * time the supplier is invoked.
 * 没有强制要求，提供者的每次调用都返回一个新的或不同的结果。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #get()}.
 *
 * @param <R> the type of results supplied by this supplier 本提供者提供的结果类型
 *
 * @since 1.8
 */
@FunctionalInterface
public interface Supplier<R> {

    /**
     * Gets a result.
     * 获取一个T类型的结果。
     *
     * @return a result
     */
    R get();
}
