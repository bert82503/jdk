package java.util.function;

/**
 * Represents a supplier of results.
 * 表示结果的供应商，对象生产者。
 *
 * <p>There is no requirement that a new or distinct result be returned each
 * time the supplier is invoked.
 * 没有强制要求，供应商的每次调用都返回一个新的或不同的结果。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #get()}.
 *
 * @param <T> the type of results supplied by this supplier 本供应商提供的结果类型
 *
 * @since 1.8
 */
@FunctionalInterface
public interface Supplier<T> {

    /**
     * Gets a result.
     * 获取一个T类型的结果。
     *
     * @return a result
     */
    T get();
}
