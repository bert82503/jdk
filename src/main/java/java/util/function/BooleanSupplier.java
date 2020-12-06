package java.util.function;

/**
 * Represents a supplier of {@code boolean}-valued results.  This is the
 * {@code boolean}-producing primitive specialization of {@link Supplier}.
 * 表示返回布尔值结果的供应商。
 *
 * <p>There is no requirement that a new or distinct result be returned each
 * time the supplier is invoked.
 * 没有强制要求，供应商的每次调用都返回一个新的或不同的结果。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #getAsBoolean()}.
 *
 * @see Supplier
 * @since 1.8
 */
@FunctionalInterface
public interface BooleanSupplier {

    /**
     * Gets a result.
     * 获取一个布尔值的结果。
     *
     * @return a result
     */
    boolean getAsBoolean();
}
