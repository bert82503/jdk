
package java.util.concurrent.atomic;

import sun.misc.Unsafe;

/**
 * A {@code boolean} value that may be updated atomically. See the
 * {@link java.util.concurrent.atomic} package specification for
 * description of the properties of atomic variables. An
 * {@code AtomicBoolean} is used in applications such as atomically
 * updated flags, and cannot be used as a replacement for a
 * {@link java.lang.Boolean}.
 * 一个可以原子更新的布尔值。
 * <p>
 * 并发使用场景：开关(原子更新的标记)
 *
 * @since 1.5
 * @author Doug Lea
 */
public class AtomicBoolean implements java.io.Serializable {
    private static final long serialVersionUID = 4654671469794556979L;

    // setup to use Unsafe.compareAndSwapInt for updates
    /**
     * 不安全的对象
     */
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    /**
     * 值的偏移量
     */
    private static final long valueOffset;

    static {
        try {
            // 值的偏移量
            valueOffset = unsafe.objectFieldOffset
                (AtomicBoolean.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    /**
     * 整数值(volatile-可见性)
     */
    private volatile int value;

    /**
     * Creates a new {@code AtomicBoolean} with the given initial value.
     *
     * @param initialValue the initial value
     */
    public AtomicBoolean(boolean initialValue) {
        // true==1，false==0
        value = initialValue ? 1 : 0;
    }

    /**
     * Creates a new {@code AtomicBoolean} with initial value {@code false}.
     */
    public AtomicBoolean() {
    }

    /**
     * Returns the current value.
     * 返回当前值。
     *
     * @return the current value
     */
    public final boolean get() {
        return value != 0;
    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     * 如果当前值==期望值，则原子方式将该值设置为给定的更新值。
     *
     * @param expect the expected value
     * @param update the new value
     * @return {@code true} if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public final boolean compareAndSet(boolean expect, boolean update) {
        int e = expect ? 1 : 0;
        int u = update ? 1 : 0;
        // CAS更新整数值
        return unsafe.compareAndSwapInt(this, valueOffset, e, u);
    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     *
     * <p><a href="package-summary.html#weakCompareAndSet">May fail
     * spuriously and does not provide ordering guarantees</a>, so is
     * only rarely an appropriate alternative to {@code compareAndSet}.
     *
     * @param expect the expected value
     * @param update the new value
     * @return {@code true} if successful
     */
    public boolean weakCompareAndSet(boolean expect, boolean update) {
        int e = expect ? 1 : 0;
        int u = update ? 1 : 0;
        return unsafe.compareAndSwapInt(this, valueOffset, e, u);
    }

    /**
     * Unconditionally sets to the given value.
     *
     * @param newValue the new value
     */
    public final void set(boolean newValue) {
        value = newValue ? 1 : 0;
    }

    /**
     * Eventually sets to the given value.
     * 最终设置为给定值。
     *
     * @param newValue the new value
     * @since 1.6
     */
    public final void lazySet(boolean newValue) {
        int v = newValue ? 1 : 0;
        unsafe.putOrderedInt(this, valueOffset, v);
    }

    /**
     * Atomically sets to the given value and returns the previous value.
     * 原子地设置为给定值，并返回上一个值。
     *
     * @param newValue the new value
     * @return the previous value
     */
    public final boolean getAndSet(boolean newValue) {
        boolean prev;
        do {
            prev = get();
        } while (!compareAndSet(prev, newValue));
        return prev;
    }

    /**
     * Returns the String representation of the current value.
     * @return the String representation of the current value
     */
    @Override
    public String toString() {
        return Boolean.toString(get());
    }

}
