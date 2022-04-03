package java.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A container object which may or may not contain a non-null value.
 * If a value is present, {@code isPresent()} will return {@code true} and
 * {@code get()} will return the value.
 * <p>
 * 可能包含null值的容器对象。
 * 如果值是存在的，{@link #isPresent()}会返回true，{@link #get()}会返回该值。
 * 单个元素的容器，具有集合容器的函数式和数据流的操作能力。
 *
 * <p>Additional methods that depend on the presence or absence of a contained
 * value are provided, such as {@link #orElse(java.lang.Object) orElse()}
 * (return a default value if value not present) and
 * {@link #ifPresent(java.util.function.Consumer) ifPresent()} (execute a block
 * of code if the value is present).
 * <p>
 * 其他方法，取决于提供的包含值是否存在。比如，
 * {@link #orElse(java.lang.Object) orElse()} (如果值不存在，则返回默认值)和
 * {@link #ifPresent(java.util.function.Consumer) ifPresent()} (如果值存在，则执行代码块)。
 *
 * <p>This is a <a href="../lang/doc-files/ValueBased.html">value-based</a>
 * class; use of identity-sensitive operations (including reference equality
 * ({@code ==}), identity hash code, or synchronization) on instances of
 * {@code Optional} may have unpredictable results and should be avoided.
 * <p>
 * 这是一个基于值的类，使用可选实例上的身份敏感操作(包括引用相等性(==)，身份哈希码，同步)，
 * 可能产生不可预知的结果，应该避免。
 *
 * @since 1.8
 */
public final class Optional<T> {

    /**
     * Common instance for {@code empty()}.
     * 空实例({@linkplain #empty()})
     */
    private static final Optional<?> EMPTY = new Optional<>();

    /**
     * If non-null, the value; if null, indicates no value is present
     * <p>
     * 可选的值，null表示值不存在
     */
    private final T value;

    /**
     * Constructs an empty instance.
     *
     * @implNote Generally only one empty instance, {@link Optional#EMPTY},
     * should exist per VM.
     */
    private Optional() {
        this.value = null;
    }

    /**
     * Returns an empty {@code Optional} instance.  No value is present for this
     * Optional.
     * 返回空实例。
     *
     * @apiNote Though it may be tempting to do so, avoid testing if an object
     * is empty by comparing with {@code ==} against instances returned by
     * {@code Option.empty()}. There is no guarantee that it is a singleton.
     * Instead, use {@link #isPresent()}.
     *
     * @param <T> Type of the non-existent value
     * @return an empty {@code Optional}
     */
    public static<T> Optional<T> empty() {
        @SuppressWarnings("unchecked")
        Optional<T> t = (Optional<T>) EMPTY;
        return t;
    }

    /**
     * Constructs an instance with the value present.
     *
     * @param value the non-null value to be present 存在的非null值
     * @throws NullPointerException if value is null
     */
    private Optional(T value) {
        // 必须是非null值
        this.value = Objects.requireNonNull(value);
    }

    // 静态工厂方法

    /**
     * Returns an {@code Optional} with the specified present non-null value.
     * 返回一个指定存在的非空值的可选实例。
     *
     * @param <T> the class of the value 值的类型
     * @param value the value to be present, which must be non-null 存在的值，必须不是非null
     * @return an {@code Optional} with the value present
     * @throws NullPointerException if value is null 如果值是null，则抛出NPE
     */
    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    /**
     * Returns an {@code Optional} describing the specified value, if non-null,
     * otherwise returns an empty {@code Optional}.
     * 如果值非空，返回一个描述指定值的可选实例；
     * 否则，返回一个空实例。
     *
     * @param <T> the class of the value 值的类型
     * @param value the possibly-null value to describe 描述可为null的值
     * @return an {@code Optional} with a present value if the specified value
     * is non-null, otherwise an empty {@code Optional}
     */
    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * If a value is present in this {@code Optional}, returns the value,
     * otherwise throws {@code NoSuchElementException}.
     * 如果值存在，则返回该值；否则，抛出没有这个元素异常。
     *
     * @return the non-null value held by this {@code Optional}
     * @throws NoSuchElementException if there is no value present
     *
     * @see Optional#isPresent()
     */
    public T get() {
//        if (value == null) {
        if (!isPresent()) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    // 谓词函数

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     * 如果是存在的值，则返回true；否则，返回false。
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean isPresent() {
        return value != null;
    }

    // 函数式接口
    // 函数式使用场景

    // 单值容器集合与函数式的桥梁

    // 对象消费者-Consumer

    /**
     * If a value is present, invoke the specified consumer with the value,
     * otherwise do nothing.
     * 如果值是存在的，则调用指定的操作消费该值；
     * 否则，什么都不做。
     *
     * @param consumer block to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     * null
     */
    public void ifPresent(Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
//        if (value != null)
        if (isPresent()) {
            consumer.accept(value);
        }
    }

    // 第一层：过滤
    // 谓词函数-Predicate

    /**
     * If a value is present, and the value matches the given predicate,
     * return an {@code Optional} describing the value, otherwise return an
     * empty {@code Optional}.
     * 如果值是存在的，并且值匹配给定的谓词，则返回描述该值的可选实例；
     * 否则，返回空实例。
     *
     * @param predicate a predicate to apply to the value, if present
     * @return an {@code Optional} describing the value of this {@code Optional}
     * if a value is present and the value matches the given predicate,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException if the predicate is null
     */
    public Optional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent()) {
            return this;
        } else {
            return predicate.test(value) ? this : empty();
        }
    }

    // 一元函数-Function
    // 使用规则：flatMap(mapper) { map(mapper) }

    /**
     * If a value is present, apply the provided mapping function to it,
     * and if the result is non-null, return an {@code Optional} describing the
     * result.  Otherwise return an empty {@code Optional}.
     * 如果值是存在的，则应用给定的映射函数，并且结果是非空的，则返回描述该结果的可选实例；
     * 否则，返回空实例。
     *
     * @apiNote This method supports post-processing on optional values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of file names, selects one that has
     * not yet been processed, and then opens that file, returning an
     * {@code Optional<FileInputStream>}:
     * <p>
     * 本方法支持可选值的后置处理，不需要显示地检查返回状态。
     * 例如，下面的代码遍历文件名称列表的流，选择一个尚未被处理的，然后打开该文件，并返回文件输出流的可选实例：
     *
     * <pre>{@code
     *     Optional<FileInputStream> fis =
     *         names.stream().filter(name -> !isProcessedYet(name))
     *                       .findFirst()
     *                       .map(name -> new FileInputStream(name));
     * }</pre>
     *
     * Here, {@code findFirst} returns an {@code Optional<String>}, and then
     * {@code map} returns an {@code Optional<FileInputStream>} for the desired
     * file if one exists.
     * <p>
     * {@code findFirst}返回一个字符串的可选实例，然后通过映射函数为存在的所需文件返回一个文件输入流的可选实例。
     *
     * @param <U> The type of the result of the mapping function 映射函数的结果类型
     * @param mapper a mapping function to apply to the value, if present 如果值存在，应用到值的映射函数
     * @return an {@code Optional} describing the result of applying a mapping
     * function to the value of this {@code Optional}, if a value is present,
     * otherwise an empty {@code Optional} 返回一个描述应用映射函数到该可选实例值的结果的可选实例
     * @throws NullPointerException if the mapping function is null
     */
    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        } else {
            // 映射结果可能为null，包装成Optional
            return Optional.ofNullable(mapper.apply(value));
        }
    }

    /**
     * If a value is present, apply the provided {@code Optional}-bearing
     * mapping function to it, return that result, otherwise return an empty
     * {@code Optional}.  This method is similar to {@link #map(Function)},
     * but the provided mapper is one whose result is already an {@code Optional},
     * and if invoked, {@code flatMap} does not wrap it with an additional
     * {@code Optional}.
     * <p>
     * 如果值是可选的，则应用提供的可选关系映射函数，然后返回该结果；
     * 否则，返回空实例。
     * 本方法和{@link #map(Function)}相似，但提供的映射函数的结果已是一个可选实例，
     * 如果被调用，本方法并不会使用一个额外的可选实例包装它。
     *
     * @param <U> The type parameter to the {@code Optional} returned by 返回的可选实例的类型参数
     * @param mapper a mapping function to apply to the value, if present
     *           the mapping function 应用到值的映射函数
     * @return the result of applying an {@code Optional}-bearing mapping
     * function to the value of this {@code Optional}, if a value is present,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function is null or returns
     * a null result
     */
    public <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        } else {
            // Optional<U>不能为null，原生返回
            return Objects.requireNonNull(mapper.apply(value));
        }
    }

    // 默认值

    /**
     * Return the value if present, otherwise return {@code other}.
     * 如果值存在，则返回该值；否则，返回默认值。
     * <p>
     * 注意：若other参数是方法调用返回结果，则每次都会被调用，当心！
     *
     * @param other the value to be returned if there is no value present, may
     * be null 可能为null
     * @return the value, if present, otherwise {@code other}
     */
    public T orElse(T other) {
//        return value != null ? value : other;
        return isPresent() ? value : other;
    }

    // 结果对象生产者-Supplier
    // 使用场景：数据延迟加载

    /**
     * Return the value if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     * 如果值存在，则返回该值；否则，调用结果提供者并返回调用的结果。
     * 使用场景：数据多层保护设计，如 Cache + DB。
     * 数据延迟加载，数据获取成本较大。
     *
     * @param other a {@code Supplier} whose result is returned if no value
     * is present
     * @return the value if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if value is not present and {@code other} is
     * null
     */
    public T orElseGet(Supplier<? extends T> other) {
//        return value != null ? value : other.get();
        return isPresent() ? value : other.get();
    }

    // 对象提供者-Supplier

    /**
     * Return the contained value, if present, otherwise throw an exception
     * to be created by the provided supplier.
     * 如果值存在，则返回包含的值；否则，抛出一个由提供的供应商创建的异常。
     * 使用场景：基于异常的快速失败设计(fail-fast)
     *
     * @apiNote A method reference to the exception constructor with an empty
     * argument list can be used as the supplier. For example,
     * {@code IllegalStateException::new}.
     * <p>
     * 一个引用使用空参数列表异常构造器的方法，可用于异常供应商。
     * 例如，{@code IllegalStateException::new}。
     *
     * @param <X> type of the exception to be thrown 待抛出的异常类型
     * @param exceptionSupplier The supplier which will return the exception to
     * be thrown 返回异常的供应商
     * @return the present value
     * @throws X if there is no value present
     * @throws NullPointerException if no value is present and
     * {@code exceptionSupplier} is null
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
//        if (value != null) {
        if (isPresent()) {
            return value;
        } else {
            // 值为null，则抛出异常
            throw exceptionSupplier.get();
        }
    }

    // Object

    /**
     * Indicates whether some other object is "equal to" this Optional. The
     * other object is considered equal if:
     * <ul>
     * <li>it is also an {@code Optional} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {code true} if the other object is "equal to" this object
     * otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        // 《Effective Java》总结的优化实现
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Optional)) {
            return false;
        }

        Optional<?> other = (Optional<?>) obj;
        return Objects.equals(value, other.value);
    }

    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if
     * no value is present.
     *
     * @return hash code value of the present value or 0 if no value is present
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Returns a non-empty string representation of this Optional suitable for
     * debugging. The exact presentation format is unspecified and may vary
     * between implementations and versions.
     *
     * @implSpec If a value is present the result must include its string
     * representation in the result. Empty and present Optionals must be
     * unambiguously differentiable.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        // 空实例
//        return value != null
        return isPresent()
            ? String.format("Optional[%s]", value)
            : "Optional.empty";
    }
}
