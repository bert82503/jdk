package java.util.function;

/**
 * Represents a supplier of {@code long}-valued results.  This is the
 * {@code long}-producing primitive specialization of {@link Supplier}.
 *
 * <p>There is no requirement that a distinct result be returned each
 * time the supplier is invoked.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #getAsLong()}.
 *
 * @see Supplier
 * @since 1.8
 */
// 表示长整数结果的供应商
public interface LongSupplier {

    /**
     * Gets a result.
     *
     * @return a result
     */
    // 获取一个长整数的结果
    long getAsLong();
}
