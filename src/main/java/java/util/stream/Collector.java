
package java.util.stream;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A <a href="package-summary.html#Reduction">mutable reduction operation</a> that
 * accumulates input elements into a mutable result container, optionally transforming
 * the accumulated result into a final representation after all input elements
 * have been processed.  Reduction operations can be performed either sequentially
 * or in parallel.
 * 一种可变的归约操作，将输入元素累加到可变的结果容器中，在处理完所有输入元素后，可选地将累加的结果转换为最终表示形式。
 * 归约操作可以按顺序或并行执行。
 *
 * <p>Examples of mutable reduction operations include:
 * accumulating elements into a {@code Collection}; concatenating
 * strings using a {@code StringBuilder}; computing summary information about
 * elements such as sum, min, max, or average; computing "pivot table" summaries
 * such as "maximum valued transaction by seller", etc.  The class {@link Collectors}
 * provides implementations of many common mutable reductions.
 * 可变的归约操作的例子包括：将元素累加到一个对象集合中；使用字符串构建者连接字符串；
 * 计算元素的汇总信息，如总和、最小值、最大值或平均值；
 * 计算"数据透视表"摘要，如卖方最大交易等。
 * Collectors类提供了许多常见可变归约的实现。
 *
 * <p>A {@code Collector} is specified by four functions that work together to
 * accumulate entries into a mutable result container, and optionally perform
 * a final transform on the result.
 * 收集器由四个函数指定，它们一起将元素条目累加到一个可变的结果容器中，并可选地对结果执行最终转换。
 * They are: <ul>
 *     <li>creation of a new result container ({@link #supplier()})</li>
 *     1.创建一个新的结果容器(结果提供者)
 *     <li>incorporating a new data element into a result container ({@link #accumulator()})</li>
 *     2.将新的数据元素合并到结果容器中(累加器函数)
 *     <li>combining two result containers into one ({@link #combiner()})</li>
 *     3.将两个结果容器合并为一个结果容器(组合器函数)
 *     <li>performing an optional final transform on the container ({@link #finisher()})</li>
 *     4.在结果容器上执行可选的最终转换(完成器函数)
 * </ul>
 *
 * <p>Collectors also have a set of characteristics, such as
 * {@link Characteristics#CONCURRENT}, that provide hints that can be used by a
 * reduction implementation to provide better performance.
 * 结果收集器也有一组特征，如并发特征。它提供了一些提示，可由归约操作实现使用这些提示来提供更好的性能。
 *
 * <p>A sequential implementation of a reduction using a collector would
 * create a single result container using the supplier function, and invoke the
 * accumulator function once for each input element.  A parallel implementation
 * would partition the input, create a result container for each partition,
 * accumulate the contents of each partition into a subresult for that partition,
 * and then use the combiner function to merge the subresults into a combined
 * result.
 * 注意：使用收集器的归约操作的顺序实现将使用结果提供者函数创建单个结果容器，并为每个输入元素调用一次累加器函数。
 * 注意：并行实现将对输入元素进行分区，为每个分区创建一个结果容器，将每个分区的内容累加到这个分区的子结果中，
 * 然后使用组合器函数将子结果合并为组合结果。
 *
 * <p>To ensure that sequential and parallel executions produce equivalent
 * results, the collector functions must satisfy an <em>identity</em> and an
 * <a href="package-summary.html#Associativity">associativity</a> constraints.
 * 为了确保顺序和并行执行产生相同的结果，收集器函数必须满足一个身份和一个关联性约束。
 *
 * <p>The identity constraint says that for any partially accumulated result,
 * combining it with an empty result container must produce an equivalent
 * result.  That is, for a partially accumulated result {@code a} that is the
 * result of any series of accumulator and combiner invocations, {@code a} must
 * be equivalent to {@code combiner.apply(a, supplier.get())}.
 * 等价约束表示，对于任何部分累加的结果，将其与空结果容器组合必须产生等效结果。
 *
 * <p>The associativity constraint says that splitting the computation must
 * produce an equivalent result.
 * 关联性约束表示拆分计算必须产生等效的结果。
 * That is, for any input elements {@code t1}
 * and {@code t2}, the results {@code r1} and {@code r2} in the computation
 * below must be equivalent:
 * <pre>{@code
 *     A a1 = supplier.get();
 *     accumulator.accept(a1, t1);
 *     accumulator.accept(a1, t2);
 *     R r1 = finisher.apply(a1);  // result without splitting
 *
 *     A a2 = supplier.get();
 *     accumulator.accept(a2, t1);
 *     A a3 = supplier.get();
 *     accumulator.accept(a3, t2);
 *     R r2 = finisher.apply(combiner.apply(a2, a3));  // result with splitting
 * } </pre>
 *
 * <p>For collectors that do not have the {@code UNORDERED} characteristic,
 * two accumulated results {@code a1} and {@code a2} are equivalent if
 * {@code finisher.apply(a1).equals(finisher.apply(a2))}.  For unordered
 * collectors, equivalence is relaxed to allow for non-equality related to
 * differences in order.  (For example, an unordered collector that accumulated
 * elements to a {@code List} would consider two lists equivalent if they
 * contained the same elements, ignoring order.)
 *
 * <p>Libraries that implement reduction based on {@code Collector}, such as
 * {@link Stream#collect(Collector)}, must adhere to the following constraints:
 * 基于收集器实现归约操作的库，必须遵守以下约束：
 * <ul>
 *     <li>The first argument passed to the accumulator function, both
 *     arguments passed to the combiner function, and the argument passed to the
 *     finisher function must be the result of a previous invocation of the
 *     result supplier, accumulator, or combiner functions.</li>
 *     传递给累加器函数的第一个参数，传递给组合器函数的两个参数，以及传递给完成器函数的参数
 *     必须是先前调用结果提供者、累加器或组合器函数的结果。
 *     <li>The implementation should not do anything with the result of any of
 *     the result supplier, accumulator, or combiner functions other than to
 *     pass them again to the accumulator, combiner, or finisher functions,
 *     or return them to the caller of the reduction operation.</li>
 *     <li>If a result is passed to the combiner or finisher
 *     function, and the same object is not returned from that function, it is
 *     never used again.</li>
 *     实现不应该对任何结果提供者、累加器或组合器函数的结果进行任何操作，
 *     而只是将它们再次传递给累加器、组合器或完成器函数，或者将它们返回给归约操作的调用者。
 *     <li>Once a result is passed to the combiner or finisher function, it
 *     is never passed to the accumulator function again.</li>
 *     一旦结果被传递给组合器或完成器函数，它就再也不会传递给累加器函数。
 *     <li>For non-concurrent collectors, any result returned from the result
 *     supplier, accumulator, or combiner functions must be serially
 *     thread-confined.  This enables collection to occur in parallel without
 *     the {@code Collector} needing to implement any additional synchronization.
 *     The reduction implementation must manage that the input is properly
 *     partitioned, that partitions are processed in isolation, and combining
 *     happens only after accumulation is complete.</li>
 *     对于非并发收集器，从结果提供者、累加器或组合器函数返回的任何结果都必须被串行线程限制。
 *     这使得收集可以并行地进行，而不需要收集器实现任何额外的同步。
 *     减少实现必须管理输入是否正确分区、分区是独立处理的，并且只有在累加完成后才会合并。
 *     <li>For concurrent collectors, an implementation is free to (but not
 *     required to) implement reduction concurrently.  A concurrent reduction
 *     is one where the accumulator function is called concurrently from
 *     multiple threads, using the same concurrently-modifiable result container,
 *     rather than keeping the result isolated during accumulation.
 *     A concurrent reduction should only be applied if the collector has the
 *     {@link Characteristics#UNORDERED} characteristics or if the
 *     originating data is unordered.</li>
 *     对于并发收集器，实现可以自由(但不是必须)并发地实现归约。
 *     并发归约是指从多个线程并发地调用累加器函数，使用相同的可并发修改的结果容器，而不是在累加期间将结果隔离。
 *     只有当收集器具有无序特征，或者原始数据是无序的，才应该应用并发归约。
 * </ul>
 *
 * <p>In addition to the predefined implementations in {@link Collectors}, the
 * static factory methods {@link #of(Supplier, BiConsumer, BinaryOperator, Characteristics...)}
 * can be used to construct collectors.
 * 除了在收集器中预定义的实现之外，of(Supplier, BiConsumer, BinaryOperator, Characteristics...)的静态工厂方法也可以用来构造收集器。
 * For example, you could create a collector
 * that accumulates widgets into a {@code TreeSet} with:
 *
 * <pre>{@code
 *     Collector<Widget, ?, TreeSet<Widget>> intoSet =
 *         Collector.of(TreeSet::new, TreeSet::add,
 *                      (left, right) -> { left.addAll(right); return left; });
 * }</pre>
 *
 * (This behavior is also implemented by the predefined collector
 * {@link Collectors#toCollection(Supplier)}).
 * (这个行为也由预定义的收集器Collectors.toCollection(Supplier)实现。)
 *
 * @apiNote
 * Performing a reduction operation with a {@code Collector} should produce a
 * result equivalent to:
 * 使用收集器执行归约操作将产生等价的结果：
 * <pre>{@code
 *     R container = collector.supplier().get();
 *     for (T t : data)
 *         collector.accumulator().accept(container, t);
 *     return collector.finisher().apply(container);
 * }</pre>
 *
 * <p>However, the library is free to partition the input, perform the reduction
 * on the partitions, and then use the combiner function to combine the partial
 * results to achieve a parallel reduction.  (Depending on the specific reduction
 * operation, this may perform better or worse, depending on the relative cost
 * of the accumulator and combiner functions.)
 * 但是，库可以自由地对输入元素进行分区，对分区执行归约，
 * 然后使用组合函数将部分结果组合起来，以实现并行归约。
 * (根据具体的归约操作，这可能会执行得更好或更差，这取决于累加器和组合器函数的相对成本。)
 *
 * <p>Collectors are designed to be <em>composed</em>; many of the methods
 * in {@link Collectors} are functions that take a collector and produce
 * a new collector.
 * 收集器被设计成组合的，收集器中的许多方法都是接受一个收集器并生成一个新的收集器的函数。
 * For example, given the following collector that computes
 * the sum of the salaries of a stream of employees:
 * 例如，给定以下收集器，这个收集器计算一个员工数据流的工资总和：
 * <pre>{@code
 *     Collector<Employee, ?, Integer> summingSalaries
 *         = Collectors.summingInt(Employee::getSalary))
 * }</pre>
 *
 * If we wanted to create a collector to tabulate the sum of salaries by
 * department, we could reuse the "sum of salaries" logic using
 * {@link Collectors#groupingBy(Function, Collector)}:
 * 如果我们想创建一个收集器来按部门将工资总额制成表格，
 * 可以使用Collectors.groupingBy(Function, Collector)重用工资总额逻辑。
 *
 * <pre>{@code
 *     Collector<Employee, ?, Map<Department, Integer>> summingSalariesByDept
 *         = Collectors.groupingBy(Employee::getDepartment, summingSalaries);
 * }</pre>
 *
 * @see Stream#collect(Collector)
 * @see Collectors
 *
 * @param <T> the type of input elements to the reduction operation 归约操作的输入元素的类型
 * @param <A> the mutable accumulation type of the reduction operation (often
 *            hidden as an implementation detail) 归约操作的可变累加类型(通常作为实现细节隐藏)
 * @param <R> the result type of the reduction operation 归约操作的结果类型
 * @since 1.8
 */
public interface Collector<T, A, R> {

    // 结果提供者、累加器、组合器和完成器函数

    // 结果提供者函数-Supplier

    /**
     * A function that creates and returns a new mutable result container.
     * 创建一个新的可变的结果容器。
     * 结果提供者
     *
     * @return a function which returns a new, mutable result container
     */
    Supplier<A> supplier();

    // 累加器函数-BiConsumer

    /**
     * A function that folds a value into a mutable result container.
     * 将新的数据元素合并到可变的结果容器中。
     * 累加器函数
     *
     * @return a function which folds a value into a mutable result container
     */
    BiConsumer<A, T> accumulator();

    // 组合器函数-BinaryOperator

    /**
     * A function that accepts two partial results and merges them.  The
     * combiner function may fold state from one argument into the other and
     * return that, or may return a new result container.
     * 接受两个部分结果，并将其合并的函数。
     * 将两个部分结果容器合并为一个结果容器。
     * 这个组合函数可以将状态从一个参数合并到另一个参数并返回，也可以返回一个新的结果容器。
     * 组合器函数
     *
     * @return a function which combines two partial results into a combined
     * result
     */
    BinaryOperator<A> combiner();

    // 完成器函数-Function

    /**
     * Perform the final transformation from the intermediate accumulation type
     * {@code A} to the final result type {@code R}.
     * 从中间累加类型A到终结结果类型R，进行最终转换。
     * 在结果容器上执行可选的最终转换。
     * 完成器函数
     *
     * <p>If the characteristic {@code IDENTITY_TRANSFORM} is
     * set, this function may be presumed to be an identity transform with an
     * unchecked cast from {@code A} to {@code R}.
     *
     * @return a function which transforms the intermediate result to the final
     * result
     */
    Function<A, R> finisher();

    /**
     * Returns a {@code Set} of {@code Collector.Characteristics} indicating
     * the characteristics of this Collector.  This set should be immutable.
     * 结果收集器的特性集合。
     *
     * @return an immutable set of collector characteristics
     */
    Set<Characteristics> characteristics();

    // 静态工厂

    /**
     * Returns a new {@code Collector} described by the given {@code supplier},
     * {@code accumulator}, and {@code combiner} functions.  The resulting
     * {@code Collector} has the {@code Collector.Characteristics.IDENTITY_FINISH}
     * characteristic.
     * 返回由给定的结果提供者、累加器和组合器函数描述的新的收集器。
     * 生成的结果收集器具有等价和完成特性。
     *
     * @param supplier The supplier function for the new collector 结果提供者函数
     * @param accumulator The accumulator function for the new collector 累加器函数
     * @param combiner The combiner function for the new collector 组合器函数
     * @param characteristics The collector characteristics for the new
     *                        collector 收集器特性
     * @param <T> The type of input elements for the new collector 输入元素的类型
     * @param <R> The type of intermediate accumulation result, and final result,
     *           for the new collector 中间累加结果的类型和最终结果的类型
     * @throws NullPointerException if any argument is null
     * @return the new {@code Collector}
     */
    static<T, R> Collector<T, R, R> of(Supplier<R> supplier,
                                       BiConsumer<R, T> accumulator,
                                       BinaryOperator<R> combiner,
                                       Characteristics... characteristics) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(accumulator);
        Objects.requireNonNull(combiner);
        Objects.requireNonNull(characteristics);
        Set<Characteristics> cs = (characteristics.length == 0)
                                  ? Collectors.CH_ID
                                  : Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH,
                                                                           characteristics));
        // 收集器实现
        return new Collectors.CollectorImpl<>(supplier, accumulator, combiner, cs);
    }

    /**
     * Returns a new {@code Collector} described by the given {@code supplier},
     * {@code accumulator}, {@code combiner}, and {@code finisher} functions.
     * 返回由给定的结果提供者、累加器、组合器和完成器函数描述的新的收集器
     *
     * @param supplier The supplier function for the new collector 结果提供者函数
     * @param accumulator The accumulator function for the new collector 累加器函数
     * @param combiner The combiner function for the new collector 部分结果组合器函数
     * @param finisher The finisher function for the new collector 完成器函数
     * @param characteristics The collector characteristics for the new
     *                        collector
     * @param <T> The type of input elements for the new collector 输入元素的类型
     * @param <A> The intermediate accumulation type of the new collector 中间累加结果的类型
     * @param <R> The final result type of the new collector 最终结果的类型
     * @throws NullPointerException if any argument is null
     * @return the new {@code Collector}
     */
    static<T, A, R> Collector<T, A, R> of(Supplier<A> supplier,
                                          BiConsumer<A, T> accumulator,
                                          BinaryOperator<A> combiner,
                                          Function<A, R> finisher,
                                          Characteristics... characteristics) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(accumulator);
        Objects.requireNonNull(combiner);
        Objects.requireNonNull(finisher);
        Objects.requireNonNull(characteristics);
        Set<Characteristics> cs = Collectors.CH_NOID;
        if (characteristics.length > 0) {
            cs = EnumSet.noneOf(Characteristics.class);
            Collections.addAll(cs, characteristics);
            cs = Collections.unmodifiableSet(cs);
        }
        // 收集器实现
        return new Collectors.CollectorImpl<>(supplier, accumulator, combiner, finisher, cs);
    }

    /**
     * Characteristics indicating properties of a {@code Collector}, which can
     * be used to optimize reduction implementations.
     * 收集器的特性标志属性集，可以用于优化归约实现。
     */
    enum Characteristics {
        /**
         * Indicates that this collector is <em>concurrent</em>, meaning that
         * the result container can support the accumulator function being
         * called concurrently with the same result container from multiple
         * threads.
         * 并发性
         *
         * <p>If a {@code CONCURRENT} collector is not also {@code UNORDERED},
         * then it should only be evaluated concurrently if applied to an
         * unordered data source.
         */
        CONCURRENT,

        /**
         * Indicates that the collection operation does not commit to preserving
         * the encounter order of input elements.  (This might be true if the
         * result container has no intrinsic order, such as a {@link Set}.)
         * 不提交保留输入元素的遇到顺序。
         * 无序
         */
        UNORDERED,

        /**
         * Indicates that the finisher function is the identity function and
         * can be elided.  If set, it must be the case that an unchecked cast
         * from A to R will succeed.
         * 等价函数和完成器函数
         */
        IDENTITY_FINISH
    }
}
