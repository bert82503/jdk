package java.util.function;

/**
 * Represents a supplier of {@code int}-valued results.  This is the
 * {@code int}-producing primitive specialization of {@link Supplier}.
 *
 * <p>There is no requirement that a distinct result be returned each
 * time the supplier is invoked.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #getAsInt()}.
 *
 * @see Supplier
 * @since 1.8
 */
// 表示整数结果的供应商
public interface IntSupplier {

    /**
     * Gets a result.
     *
     * @return a result
     */
    // 获取一个整数的结果
    int getAsInt();
}
