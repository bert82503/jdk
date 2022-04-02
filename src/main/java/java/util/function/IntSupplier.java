package java.util.function;

/**
 * Represents a supplier of {@code int}-valued results.  This is the
 * {@code int}-producing primitive specialization of {@link Supplier}.
 * 表示返回整数值结果的提供者。
 *
 * <p>There is no requirement that a distinct result be returned each
 * time the supplier is invoked.
 * 没有强制要求，提供者的每次调用都返回一个新的或不同的结果。
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #getAsInt()}.
 *
 * @see Supplier
 * @since 1.8
 */
@FunctionalInterface
public interface IntSupplier {

    /**
     * Gets a result.
     * 获取一个整数值的结果。
     *
     * @return a result
     */
    int getAsInt();
}
