package java.util.function;

/**
 * Represents a supplier of {@code long}-valued results.  This is the
 * {@code long}-producing primitive specialization of {@link Supplier}.
 * 表示返回长整数值结果的供应商。
 *
 * <p>There is no requirement that a distinct result be returned each
 * time the supplier is invoked.
 * 没有强制要求，供应商的每次调用都返回一个新的或不同的结果。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #getAsLong()}.
 *
 * @see Supplier
 * @since 1.8
 */
@FunctionalInterface
public interface LongSupplier {

    /**
     * Gets a result.
     * 获取一个长整数值的结果。
     *
     * @return a result
     */
    long getAsLong();
}
