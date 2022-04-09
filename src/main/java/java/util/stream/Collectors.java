
package java.util.stream;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * Implementations of {@link Collector} that implement various useful reduction
 * operations, such as accumulating elements into collections, summarizing
 * elements according to various criteria, etc.
 * 实现了各种有用的归约操作的收集器。
 * 例如将元素累加到集合中，根据不同的标准汇总元素，等等。
 *
 * <p>The following are examples of using the predefined collectors to perform
 * common mutable reduction tasks:
 * 以下是使用预定义收集器来执行常见可变的归约任务的示例：
 *
 * <pre>{@code
 *     // Accumulate names into a List
 *     // 数组列表收集器-Collectors.toList()
 *     List<String> list = people.stream().map(Person::getName).collect(Collectors.toList());
 *
 *     // Accumulate names into a TreeSet
 *     // 树集合收集器-Collectors.toCollection(TreeSet::new)
 *     Set<String> set = people.stream().map(Person::getName).collect(Collectors.toCollection(TreeSet::new));
 *
 *     // Convert elements to strings and concatenate them, separated by commas
 *     // 连接字符串收集器-Collectors.joining(", ")
 *     String joined = things.stream()
 *                           .map(Object::toString)
 *                           .collect(Collectors.joining(", "));
 *
 *     // Compute sum of salaries of employee
 *     // 整数求和收集器-Collectors.summingInt(Employee::getSalary)
 *     int total = employees.stream()
 *                          .collect(Collectors.summingInt(Employee::getSalary)));
 *
 *     // Group employees by department
 *     // 分组收集器-Collectors.groupingBy(Employee::getDepartment)
 *     Map<Department, List<Employee>> byDept
 *         = employees.stream()
 *                    .collect(Collectors.groupingBy(Employee::getDepartment));
 *
 *     // Compute sum of salaries by department
 *     // 分组收集器-Collectors.groupingBy(Employee::getDepartment) + 整数求和收集器-Collectors.summingInt(Employee::getSalary)
 *     Map<Department, Integer> totalByDept
 *         = employees.stream()
 *                    .collect(Collectors.groupingBy(Employee::getDepartment,
 *                                                   Collectors.summingInt(Employee::getSalary)));
 *
 *     // Partition students into passing and failing
 *     // 分区收集器-Collectors.partitioningBy(s -> s.getGrade() >= PASS_THRESHOLD)
 *     Map<Boolean, List<Student>> passingFailing =
 *         students.stream()
 *                 .collect(Collectors.partitioningBy(s -> s.getGrade() >= PASS_THRESHOLD));
 *
 * }</pre>
 *
 * @since 1.8
 */
public final class Collectors {

    /**
     * 并发，无序，等价-完成
     */
    static final Set<Collector.Characteristics> CH_CONCURRENT_ID
            = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT,
                                                     Collector.Characteristics.UNORDERED,
                                                     Collector.Characteristics.IDENTITY_FINISH));
    /**
     * 并发，无序
     */
    static final Set<Collector.Characteristics> CH_CONCURRENT_NOID
            = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT,
                                                     Collector.Characteristics.UNORDERED));
    /**
     * 等价，完成
     */
    static final Set<Collector.Characteristics> CH_ID
            = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
    /**
     * 无序，等价-完成
     */
    static final Set<Collector.Characteristics> CH_UNORDERED_ID
            = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED,
                                                     Collector.Characteristics.IDENTITY_FINISH));
    static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();

    private Collectors() { }

    /**
     * Returns a merge function, suitable for use in
     * {@link Map#merge(Object, Object, BiFunction) Map.merge()} or
     * {@link #toMap(Function, Function, BinaryOperator) toMap()}, which always
     * throws {@code IllegalStateException}.  This can be used to enforce the
     * assumption that the elements being collected are distinct.
     *
     * @param <T> the type of input arguments to the merge function
     * @return a merge function which always throw {@code IllegalStateException}
     */
    private static <T> BinaryOperator<T> throwingMerger() {
        return (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
    }

    @SuppressWarnings("unchecked")
    private static <I, R> Function<I, R> castingIdentity() {
        return i -> (R) i;
    }

    /**
     * Simple implementation class for {@code Collector}.
     * 收集器的简单实现类。
     *
     * @param <T> the type of elements to be collected
     *           要收集的元素类型
     * @param <R> the type of the result
     *           结果的类型
     */
    static class CollectorImpl<T, A, R> implements Collector<T, A, R> {
        /**
         * 结果提供者函数
         */
        private final Supplier<A> supplier;
        /**
         * 累加器函数
         */
        private final BiConsumer<A, T> accumulator;
        /**
         * 组合器函数
         */
        private final BinaryOperator<A> combiner;
        /**
         * 完成器函数
         */
        private final Function<A, R> finisher;
        /**
         * 特征集
         */
        private final Set<Characteristics> characteristics;

        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A, T> accumulator,
                      BinaryOperator<A> combiner,
                      Function<A,R> finisher,
                      Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }

        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A, T> accumulator,
                      BinaryOperator<A> combiner,
                      Set<Characteristics> characteristics) {
            this(supplier, accumulator, combiner, castingIdentity(), characteristics);
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }
    }

    // 集合收集器

    /**
     * Returns a {@code Collector} that accumulates the input elements into a
     * new {@code Collection}, in encounter order.  The {@code Collection} is
     * created by the provided factory.
     * 返回一个收集器，它将输入元素按照遇到的顺序累加到一个新的集合中。
     * 集合由提供的工厂创建。
     *
     * @param <T> the type of the input elements
     * @param <C> the type of the resulting {@code Collection}
     * @param collectionFactory a {@code Supplier} which returns a new, empty
     * {@code Collection} of the appropriate type
     *                          结果提供者，它返回一个适当类型的新的空集合
     * @return a {@code Collector} which collects all the input elements into a
     * {@code Collection}, in encounter order
     * 返回一个收集器，将所有输入元素按遇到的顺序收集到一个集合中
     */
    public static <T, C extends Collection<T>>
    Collector<T, ?, C> toCollection(Supplier<C> collectionFactory) {
        // 集合子类工厂-collectionFactory
        // Collection
        return new CollectorImpl<>(collectionFactory, Collection<T>::add,
                                   (r1, r2) -> { r1.addAll(r2); return r1; },
                                   CH_ID);
    }

    /**
     * Returns a {@code Collector} that accumulates the input elements into a
     * new {@code List}. There are no guarantees on the type, mutability,
     * serializability, or thread-safety of the {@code List} returned; if more
     * control over the returned {@code List} is required, use {@link #toCollection(Supplier)}.
     * 返回将输入元素累加到新的列表中的收集器。
     * 不保证返回的列表的类型、可变性、可序列化性或线程安全性；
     * 如果需要对返回的列表进行更多地控制，请使用toCollection(Supplier)。
     *
     * @param <T> the type of the input elements
     * @return a {@code Collector} which collects all the input elements into a
     * {@code List}, in encounter order
     */
    public static <T>
    Collector<T, ?, List<T>> toList() {
        // ArrayList::new
        return new CollectorImpl<>((Supplier<List<T>>) ArrayList::new, List::add,
                                   (left, right) -> { left.addAll(right); return left; },
                                   CH_ID);
    }

    /**
     * Returns a {@code Collector} that accumulates the input elements into a
     * new {@code Set}. There are no guarantees on the type, mutability,
     * serializability, or thread-safety of the {@code Set} returned; if more
     * control over the returned {@code Set} is required, use
     * {@link #toCollection(Supplier)}.
     * 返回将输入元素累加到新的集合中的收集器。
     * 不保证返回的列表的类型、可变性、可序列化性或线程安全性；
     * 如果需要对返回的列表进行更多地控制，请使用toCollection(Supplier)。
     *
     * <p>This is an {@link Collector.Characteristics#UNORDERED unordered}
     * Collector.
     * 这是一个无序的收集器。
     *
     * @param <T> the type of the input elements
     * @return a {@code Collector} which collects all the input elements into a
     * {@code Set}
     */
    public static <T>
    Collector<T, ?, Set<T>> toSet() {
        // HashSet::new
        return new CollectorImpl<>((Supplier<Set<T>>) HashSet::new, Set::add,
                                   (left, right) -> { left.addAll(right); return left; },
                                   CH_UNORDERED_ID);
    }

    // 字符串连接收集器

    /**
     * Returns a {@code Collector} that concatenates the input elements into a
     * {@code String}, in encounter order.
     * 返回一个收集器，它将输入元素按照遇到的顺序连接成一个字符串。
     *
     * @return a {@code Collector} that concatenates the input elements into a
     * {@code String}, in encounter order
     * 一个收集器，将输入元素按照遇到的顺序连接成一个字符串
     */
    public static Collector<CharSequence, ?, String> joining() {
        // StringBuilder::new
        // CharSequence -> StringBuilder -> String
        return new CollectorImpl<CharSequence, StringBuilder, String>(
                StringBuilder::new, StringBuilder::append,
                (r1, r2) -> { r1.append(r2); return r1; },
                StringBuilder::toString, CH_NOID);
    }

    /**
     * Returns a {@code Collector} that concatenates the input elements,
     * separated by the specified delimiter, in encounter order.
     * 返回一个收集器，它按照遇到的顺序连接输入元素，由指定的分隔符分隔。
     *
     * @param delimiter the delimiter to be used between each element
     *                  每个元素之间使用的分隔符
     * @return A {@code Collector} which concatenates CharSequence elements,
     * separated by the specified delimiter, in encounter order
     * 一个收集器，它按照遇到的顺序连接字符序列元素，由指定的分隔符分隔
     */
    public static Collector<CharSequence, ?, String> joining(CharSequence delimiter) {
        return joining(delimiter, "", "");
    }

    /**
     * Returns a {@code Collector} that concatenates the input elements,
     * separated by the specified delimiter, with the specified prefix and
     * suffix, in encounter order.
     * 返回一个收集器，它按照遇到的顺序连接输入元素，由指定的分隔符分隔。
     *
     * @param delimiter the delimiter to be used between each element
     * @param  prefix the sequence of characters to be used at the beginning
     *                of the joined result
     * @param  suffix the sequence of characters to be used at the end
     *                of the joined result
     * @return A {@code Collector} which concatenates CharSequence elements,
     * separated by the specified delimiter, in encounter order
     * 一个收集器，它按照遇到的顺序连接字符序列元素，由指定的分隔符分隔
     */
    public static Collector<CharSequence, ?, String> joining(CharSequence delimiter,
                                                             CharSequence prefix,
                                                             CharSequence suffix) {
        // () -> new StringJoiner(delimiter, prefix, suffix)
        return new CollectorImpl<>(
                () -> new StringJoiner(delimiter, prefix, suffix),
                StringJoiner::add, StringJoiner::merge,
                StringJoiner::toString, CH_NOID);
    }

    /**
     * {@code BinaryOperator<Map>} that merges the contents of its right
     * argument into its left argument, using the provided merge function to
     * handle duplicate keys.
     * 将其右参数的内容合并到其左参数中，使用提供的合并函数来处理重复的键。
     *
     * @param <K> type of the map keys
     * @param <V> type of the map values
     * @param <M> type of the map
     * @param mergeFunction A merge function suitable for
     * {@link Map#merge(Object, Object, BiFunction) Map.merge()}
     *                      合并函数
     * @return a merge function for two maps
     */
    private static <K, V, M extends Map<K,V>>
    BinaryOperator<M> mapMerger(BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            for (Map.Entry<K,V> e : m2.entrySet()) {
                m1.merge(e.getKey(), e.getValue(), mergeFunction);
            }
            return m1;
        };
    }

    // 类型转换映射收集器

    /**
     * Adapts a {@code Collector} accepting elements of type {@code U} to one
     * accepting elements of type {@code T} by applying a mapping function to
     * each input element before accumulation.
     * 通过在累加之前对每个输入元素应用映射函数，将一个接受U类型元素的收集器适配为一个接受T类型的元素。
     *
     * @apiNote
     * The {@code mapping()} collectors are most useful when used in a
     * multi-level reduction, such as downstream of a {@code groupingBy} or
     * {@code partitioningBy}.
     * 在多级归约中使用映射函数mapping()收集器是最有用的，比如下游的groupingBy或partitioningBy。
     * For example, given a stream of
     * {@code Person}, to accumulate the set of last names in each city:
     * 例如，给定人的数据流，将每个城市的姓氏集合累加起来：
     * <pre>{@code
     *     Map<City, Set<String>> lastNamesByCity
     *         = people.stream().collect(groupingBy(Person::getCity,
     *                                              mapping(Person::getLastName, toSet())));
     * }</pre>
     *
     * @param <T> the type of the input elements
     * @param <U> type of elements accepted by downstream collector
     * @param <A> intermediate accumulation type of the downstream collector
     * @param <R> result type of collector
     * @param mapper a function to be applied to the input elements
     *               要应用于输入元素的函数
     * @param downstream a collector which will accept mapped values
     *                   一个接受映射值的收集器
     * @return a collector which applies the mapping function to the input
     * elements and provides the mapped results to the downstream collector
     */
    public static <T, U, A, R>
    Collector<T, ?, R> mapping(Function<? super T, ? extends U> mapper,
                               Collector<? super U, A, R> downstream) {
        // 下游的累加器函数
        BiConsumer<A, ? super U> downstreamAccumulator = downstream.accumulator();
        // 下游收集器-downstream
        return new CollectorImpl<>(downstream.supplier(),
                                   (r, t) -> downstreamAccumulator.accept(r, mapper.apply(t)),
                                   downstream.combiner(), downstream.finisher(),
                                   downstream.characteristics());
    }

    /**
     * Adapts a {@code Collector} to perform an additional finishing
     * transformation.
     * 调整收集器以执行额外的完成转换。
     * For example, one could adapt the {@link #toList()}
     * collector to always produce an immutable list with:
     * <pre>{@code
     *     List<String> people
     *         = people.stream().collect(collectingAndThen(toList(), Collections::unmodifiableList));
     * }</pre>
     *
     * @param <T> the type of the input elements
     * @param <A> intermediate accumulation type of the downstream collector
     * @param <R> result type of the downstream collector
     * @param <RR> result type of the resulting collector
     * @param downstream a collector
     * @param finisher a function to be applied to the final result of the downstream collector
     * @return a collector which performs the action of the downstream collector,
     * followed by an additional finishing step
     */
    public static <T, A, R, RR>
    Collector<T, A, RR> collectingAndThen(Collector<T, A, R> downstream,
                                          Function<R, RR> finisher) {
        Set<Collector.Characteristics> characteristics = downstream.characteristics();
        if (characteristics.contains(Collector.Characteristics.IDENTITY_FINISH)) {
            if (characteristics.size() == 1) {
                characteristics = Collectors.CH_NOID;
            } else {
                characteristics = EnumSet.copyOf(characteristics);
                characteristics.remove(Collector.Characteristics.IDENTITY_FINISH);
                characteristics = Collections.unmodifiableSet(characteristics);
            }
        }
        // 下游收集器
        return new CollectorImpl<>(downstream.supplier(),
                                   downstream.accumulator(),
                                   downstream.combiner(),
                                   downstream.finisher().andThen(finisher),
                                   characteristics);
    }

    // 元素数量计数器收集器

    /**
     * Returns a {@code Collector} accepting elements of type {@code T} that
     * counts the number of input elements.  If no elements are present, the
     * result is 0.
     * 返回一个接受类型为T的元素的收集器，它计算输入元素的数量。
     * 如果不存在任何元素，则结果为0。
     *
     * @implSpec
     * This produces a result equivalent to:
     * <pre>{@code
     *     reducing(0L, e -> 1L, Long::sum)
     * }</pre>
     *
     * @param <T> the type of the input elements
     * @return a {@code Collector} that counts the input elements
     */
    public static <T> Collector<T, ?, Long>
    counting() {
        // 数量归约
        return reducing(0L, e -> 1L, Long::sum);
    }

    // 最小值、最大值比较收集器

    /**
     * Returns a {@code Collector} that produces the minimal element according
     * to a given {@code Comparator}, described as an {@code Optional<T>}.
     * 返回根据给定比较器生成最小元素的收集器，描述为Optional<T>。
     *
     * @implSpec
     * This produces a result equivalent to:
     * <pre>{@code
     *     reducing(BinaryOperator.minBy(comparator))
     * }</pre>
     *
     * @param <T> the type of the input elements
     * @param comparator a {@code Comparator} for comparing elements
     * @return a {@code Collector} that produces the minimal value
     */
    public static <T> Collector<T, ?, Optional<T>>
    minBy(Comparator<? super T> comparator) {
        // 二元运算符的归约操作
        return reducing(BinaryOperator.minBy(comparator));
    }

    /**
     * Returns a {@code Collector} that produces the maximal element according
     * to a given {@code Comparator}, described as an {@code Optional<T>}.
     * 返回根据给定比较器生成最大元素的收集器，描述为Optional<T>。
     *
     * @implSpec
     * This produces a result equivalent to:
     * <pre>{@code
     *     reducing(BinaryOperator.maxBy(comparator))
     * }</pre>
     *
     * @param <T> the type of the input elements
     * @param comparator a {@code Comparator} for comparing elements
     * @return a {@code Collector} that produces the maximal value
     */
    public static <T> Collector<T, ?, Optional<T>>
    maxBy(Comparator<? super T> comparator) {
        // 二元运算符的归约操作
        return reducing(BinaryOperator.maxBy(comparator));
    }

    // 值求和收集器

    /**
     * Returns a {@code Collector} that produces the sum of a integer-valued
     * function applied to the input elements.  If no elements are present,
     * the result is 0.
     * 返回一个收集器，它产生应用于输入元素的整数值函数的总和。
     * 如果不存在任何元素，则结果为0。
     *
     * @param <T> the type of the input elements
     * @param mapper a function extracting the property to be summed
     *               提取求和的属性的函数
     * @return a {@code Collector} that produces the sum of a derived property
     */
    public static <T> Collector<T, ?, Integer>
    summingInt(ToIntFunction<? super T> mapper) {
        // 求和值的数组引用
        return new CollectorImpl<>(
                () -> new int[1],
                (a, t) -> a[0] += mapper.applyAsInt(t),
                (a, b) -> { a[0] += b[0]; return a; },
                a -> a[0], CH_NOID);
    }

    /**
     * Returns a {@code Collector} that produces the sum of a long-valued
     * function applied to the input elements.  If no elements are present,
     * the result is 0.
     * 返回一个收集器，它产生应用于输入元素的长整数值函数的总和。
     * 如果不存在任何元素，则结果为0。
     *
     * @param <T> the type of the input elements
     * @param mapper a function extracting the property to be summed
     * @return a {@code Collector} that produces the sum of a derived property
     */
    public static <T> Collector<T, ?, Long>
    summingLong(ToLongFunction<? super T> mapper) {
        // 求和值的数组引用
        return new CollectorImpl<>(
                () -> new long[1],
                (a, t) -> a[0] += mapper.applyAsLong(t),
                (a, b) -> { a[0] += b[0]; return a; },
                a -> a[0], CH_NOID);
    }

    /**
     * Returns a {@code Collector} that produces the sum of a double-valued
     * function applied to the input elements.  If no elements are present,
     * the result is 0.
     *
     * <p>The sum returned can vary depending upon the order in which
     * values are recorded, due to accumulated rounding error in
     * addition of values of differing magnitudes. Values sorted by increasing
     * absolute magnitude tend to yield more accurate results.  If any recorded
     * value is a {@code NaN} or the sum is at any point a {@code NaN} then the
     * sum will be {@code NaN}.
     *
     * @param <T> the type of the input elements
     * @param mapper a function extracting the property to be summed
     * @return a {@code Collector} that produces the sum of a derived property
     */
    public static <T> Collector<T, ?, Double>
    summingDouble(ToDoubleFunction<? super T> mapper) {
        /*
         * In the arrays allocated for the collect operation, index 0
         * holds the high-order bits of the running sum, index 1 holds
         * the low-order bits of the sum computed via compensated
         * summation, and index 2 holds the simple sum used to compute
         * the proper result if the stream contains infinite values of
         * the same sign.
         */
        return new CollectorImpl<>(
                () -> new double[3],
                (a, t) -> { sumWithCompensation(a, mapper.applyAsDouble(t));
                            a[2] += mapper.applyAsDouble(t);},
                (a, b) -> { sumWithCompensation(a, b[0]);
                            a[2] += b[2];
                            return sumWithCompensation(a, b[1]); },
                a -> computeFinalSum(a),
                CH_NOID);
    }

    /**
     * Incorporate a new double value using Kahan summation /
     * compensation summation.
     * 使用Kahan求和/补偿求和合并一个新的双精度值。
     *
     * High-order bits of the sum are in intermediateSum[0], low-order
     * bits of the sum are in intermediateSum[1], any additional
     * elements are application-specific.
     *
     * @param intermediateSum the high-order and low-order words of the intermediate sum
     * @param value the name value to be included in the running sum
     */
    static double[] sumWithCompensation(double[] intermediateSum, double value) {
        double tmp = value - intermediateSum[1];
        double sum = intermediateSum[0];
        double velvel = sum + tmp; // Little wolf of rounding error
        intermediateSum[1] = (velvel - sum) - tmp;
        intermediateSum[0] = velvel;
        return intermediateSum;
    }

    /**
     * If the compensated sum is spuriously NaN from accumulating one
     * or more same-signed infinite values, return the
     * correctly-signed infinity stored in the simple sum.
     */
    static double computeFinalSum(double[] summands) {
        // Better error bounds to add both terms as the final sum
        double tmp = summands[0] + summands[1];
        double simpleSum = summands[summands.length - 1];
        if (Double.isNaN(tmp) && Double.isInfinite(simpleSum)) {
            return simpleSum;
        } else {
            return tmp;
        }
    }

    // 算术平均值收集器

    /**
     * Returns a {@code Collector} that produces the arithmetic mean of an integer-valued
     * function applied to the input elements.  If no elements are present,
     * the result is 0.
     * 返回一个收集器，生成应用于输入元素的整数值函数的算术平均值。
     * 如果不存在元素，则结果为0。
     *
     * @param <T> the type of the input elements
     * @param mapper a function extracting the property to be summed
     * @return a {@code Collector} that produces the sum of a derived property
     */
    public static <T> Collector<T, ?, Double>
    averagingInt(ToIntFunction<? super T> mapper) {
        // () -> new long[2]
        // a[0]用于求和，a[1]用于计数
        return new CollectorImpl<>(
                () -> new long[2],
                (a, t) -> { a[0] += mapper.applyAsInt(t); a[1]++; },
                (a, b) -> { a[0] += b[0]; a[1] += b[1]; return a; },
                a -> (a[1] == 0) ? 0.0d : (double) a[0] / a[1], CH_NOID);
    }

    /**
     * Returns a {@code Collector} that produces the arithmetic mean of a long-valued
     * function applied to the input elements.  If no elements are present,
     * the result is 0.
     * 返回一个收集器，生成应用于输入元素的长整数值函数的算术平均值。
     * 如果不存在元素，则结果为0。
     *
     * @param <T> the type of the input elements
     * @param mapper a function extracting the property to be summed
     * @return a {@code Collector} that produces the sum of a derived property
     */
    public static <T> Collector<T, ?, Double>
    averagingLong(ToLongFunction<? super T> mapper) {
        return new CollectorImpl<>(
                () -> new long[2],
                (a, t) -> { a[0] += mapper.applyAsLong(t); a[1]++; },
                (a, b) -> { a[0] += b[0]; a[1] += b[1]; return a; },
                a -> (a[1] == 0) ? 0.0d : (double) a[0] / a[1], CH_NOID);
    }

    /**
     * Returns a {@code Collector} that produces the arithmetic mean of a double-valued
     * function applied to the input elements.  If no elements are present,
     * the result is 0.
     *
     * <p>The average returned can vary depending upon the order in which
     * values are recorded, due to accumulated rounding error in
     * addition of values of differing magnitudes. Values sorted by increasing
     * absolute magnitude tend to yield more accurate results.  If any recorded
     * value is a {@code NaN} or the sum is at any point a {@code NaN} then the
     * average will be {@code NaN}.
     *
     * @implNote The {@code double} format can represent all
     * consecutive integers in the range -2<sup>53</sup> to
     * 2<sup>53</sup>. If the pipeline has more than 2<sup>53</sup>
     * values, the divisor in the average computation will saturate at
     * 2<sup>53</sup>, leading to additional numerical errors.
     *
     * @param <T> the type of the input elements
     * @param mapper a function extracting the property to be summed
     * @return a {@code Collector} that produces the sum of a derived property
     */
    public static <T> Collector<T, ?, Double>
    averagingDouble(ToDoubleFunction<? super T> mapper) {
        /*
         * In the arrays allocated for the collect operation, index 0
         * holds the high-order bits of the running sum, index 1 holds
         * the low-order bits of the sum computed via compensated
         * summation, and index 2 holds the number of values seen.
         */
        return new CollectorImpl<>(
                () -> new double[4],
                (a, t) -> { sumWithCompensation(a, mapper.applyAsDouble(t)); a[2]++; a[3]+= mapper.applyAsDouble(t);},
                (a, b) -> { sumWithCompensation(a, b[0]); sumWithCompensation(a, b[1]); a[2] += b[2]; a[3] += b[3]; return a; },
                a -> (a[2] == 0) ? 0.0d : (computeFinalSum(a) / a[2]),
                CH_NOID);
    }

    // 归约操作收集器

    /**
     * Returns a {@code Collector} which performs a reduction of its
     * input elements under a specified {@code BinaryOperator} using the
     * provided identity.
     * 返回一个收集器，使用提供的等价函数在指定的二元运算符下执行其输入元素的归约操作。
     *
     * @apiNote
     * The {@code reducing()} collectors are most useful when used in a
     * multi-level reduction, downstream of {@code groupingBy} or
     * {@code partitioningBy}.  To perform a simple reduction on a stream,
     * use {@link Stream#reduce(Object, BinaryOperator)}} instead.
     * 归约收集器在groupingBy或partitioningBy的下游多级归约中使用最为有用。
     * 要对数据流执行简单的归约操作，请使用数据流。
     *
     * @param <T> element type for the input and output of the reduction
     *           归约的输入和输出的元素类型
     * @param identity the identity value for the reduction (also, the value
     *                 that is returned when there are no input elements)
     *                 归约的等价函数
     * @param op a {@code BinaryOperator<T>} used to reduce the input elements
     *           二元运算符，用于减少输入元素
     * @return a {@code Collector} which implements the reduction operation
     *
     * @see #reducing(BinaryOperator)
     * @see #reducing(Object, Function, BinaryOperator)
     */
    public static <T> Collector<T, ?, T>
    reducing(T identity, BinaryOperator<T> op) {
        return new CollectorImpl<>(
                boxSupplier(identity),
                (a, t) -> { a[0] = op.apply(a[0], t); },
                (a, b) -> { a[0] = op.apply(a[0], b[0]); return a; },
                a -> a[0],
                CH_NOID);
    }

    @SuppressWarnings("unchecked")
    private static <T> Supplier<T[]> boxSupplier(T identity) {
        return () -> (T[]) new Object[] { identity };
    }

    /**
     * Returns a {@code Collector} which performs a reduction of its
     * input elements under a specified {@code BinaryOperator}.  The result
     * is described as an {@code Optional<T>}.
     * 返回一个收集器，在指定的二元运算符下执行其输入元素的归约。
     * 结果被描述为一个可选的值容器。
     *
     * @apiNote
     * The {@code reducing()} collectors are most useful when used in a
     * multi-level reduction, downstream of {@code groupingBy} or
     * {@code partitioningBy}.  To perform a simple reduction on a stream,
     * use {@link Stream#reduce(BinaryOperator)} instead.
     * 归约收集器在groupingBy或partitioningBy的下游多级归约中使用最为有用。
     * 要对数据流执行简单的归约操作，请使用数据流。
     *
     * <p>For example, given a stream of {@code Person}, to calculate tallest
     * person in each city:
     * <pre>{@code
     *     Comparator<Person> byHeight = Comparator.comparing(Person::getHeight);
     *     Map<City, Person> tallestByCity
     *         = people.stream().collect(groupingBy(Person::getCity, reducing(BinaryOperator.maxBy(byHeight))));
     * }</pre>
     *
     * @param <T> element type for the input and output of the reduction
     * @param op a {@code BinaryOperator<T>} used to reduce the input elements
     *           二元运算符，用于归约输入元素
     * @return a {@code Collector} which implements the reduction operation
     *
     * @see #reducing(Object, BinaryOperator)
     * @see #reducing(Object, Function, BinaryOperator)
     */
    public static <T> Collector<T, ?, Optional<T>>
    reducing(BinaryOperator<T> op) {
        // 可选的结果消费者
        class OptionalBox implements Consumer<T> {
            T value = null;
            boolean present = false;

            @Override
            public void accept(T t) {
                if (present) {
                    value = op.apply(value, t);
                }
                else {
                    value = t;
                    present = true;
                }
            }
        }

        // 中间结果-OptionalBox
        return new CollectorImpl<T, OptionalBox, Optional<T>>(
                OptionalBox::new, OptionalBox::accept,
                (a, b) -> { if (b.present) a.accept(b.value); return a; },
                a -> Optional.ofNullable(a.value), CH_NOID);
    }

    /**
     * Returns a {@code Collector} which performs a reduction of its
     * input elements under a specified mapping function and
     * {@code BinaryOperator}. This is a generalization of
     * {@link #reducing(Object, BinaryOperator)} which allows a transformation
     * of the elements before reduction.
     * 返回一个收集器，在指定的映射函数和二元运算符下执行其输入元素的归约操作。
     * 这是归约的泛化，允许在归约之前对元素进行转换。
     *
     * @apiNote
     * The {@code reducing()} collectors are most useful when used in a
     * multi-level reduction, downstream of {@code groupingBy} or
     * {@code partitioningBy}.  To perform a simple map-reduce on a stream,
     * use {@link Stream#map(Function)} and {@link Stream#reduce(Object, BinaryOperator)}
     * instead.
     *
     * <p>For example, given a stream of {@code Person}, to calculate the longest
     * last name of residents in each city:
     * <pre>{@code
     *     Comparator<String> byLength = Comparator.comparing(String::length);
     *     Map<City, String> longestLastNameByCity
     *         = people.stream().collect(groupingBy(Person::getCity,
     *                                              reducing(Person::getLastName, BinaryOperator.maxBy(byLength))));
     * }</pre>
     *
     * @param <T> the type of the input elements
     * @param <U> the type of the mapped values
     * @param identity the identity value for the reduction (also, the value
     *                 that is returned when there are no input elements)
     * @param mapper a mapping function to apply to each input value
     * @param op a {@code BinaryOperator<U>} used to reduce the mapped values
     * @return a {@code Collector} implementing the map-reduce operation
     *
     * @see #reducing(Object, BinaryOperator)
     * @see #reducing(BinaryOperator)
     */
    public static <T, U>
    Collector<T, ?, U> reducing(U identity,
                                Function<? super T, ? extends U> mapper,
                                BinaryOperator<U> op) {
        return new CollectorImpl<>(
                boxSupplier(identity),
                (a, t) -> { a[0] = op.apply(a[0], mapper.apply(t)); },
                (a, b) -> { a[0] = op.apply(a[0], b[0]); return a; },
                a -> a[0], CH_NOID);
    }

    // 分组操作收集器

    /**
     * Returns a {@code Collector} implementing a "group by" operation on
     * input elements of type {@code T}, grouping elements according to a
     * classification function, and returning the results in a {@code Map}.
     * 返回一个对T类型的输入元素实现分组操作的收集器，根据分类函数对元素进行分组，并在映射关系中返回结果。
     *
     * <p>The classification function maps elements to some key type {@code K}.
     * The collector produces a {@code Map<K, List<T>>} whose keys are the
     * values resulting from applying the classification function to the input
     * elements, and whose corresponding values are {@code List}s containing the
     * input elements which map to the associated key under the classification
     * function.
     * 分类函数元素映射到一些关键类型K收集器映射生产的Map<K, List<T>>，应用分类函数输入元素，
     * 和相应的值的列表包含的输入元素映射到相关的关联键。
     *
     * <p>There are no guarantees on the type, mutability, serializability, or
     * thread-safety of the {@code Map} or {@code List} objects returned.
     * 不能保证返回的映射或列表对象的类型、可变性、可序列化性或线程安全性。
     * @implSpec
     * This produces a result similar to:
     * <pre>{@code
     *     groupingBy(classifier, toList());
     * }</pre>
     *
     * @implNote
     * The returned {@code Collector} is not concurrent.  For parallel stream
     * pipelines, the {@code combiner} function operates by merging the keys
     * from one map into another, which can be an expensive operation.  If
     * preservation of the order in which elements appear in the resulting {@code Map}
     * collector is not required, using {@link #groupingByConcurrent(Function)}
     * may offer better parallel performance.
     * 返回的收集器不是并发的。对于并行数据流管道，组合器函数的操作方式是将一个映射中的键合并到另一个映射中，这可能是一个代价很高的操作。
     * 如果不需要保持元素在结果映射收集器中出现的顺序，则使用groupingByConcurrent(Function)可以提供更好的并行性能。
     *
     * @param <T> the type of the input elements
     * @param <K> the type of the keys 键的类型
     * @param classifier the classifier function mapping input elements to keys
     *                   将输入元素映射到键的分类器函数
     * @return a {@code Collector} implementing the group-by operation
     *
     * @see #groupingBy(Function, Collector)
     * @see #groupingBy(Function, Supplier, Collector)
     * @see #groupingByConcurrent(Function)
     */
    public static <T, K> Collector<T, ?, Map<K, List<T>>>
    groupingBy(Function<? super T, ? extends K> classifier) {
        // 下游收集器-toList()
        return groupingBy(classifier, toList());
    }

    /**
     * Returns a {@code Collector} implementing a cascaded "group by" operation
     * on input elements of type {@code T}, grouping elements according to a
     * classification function, and then performing a reduction operation on
     * the values associated with a given key using the specified downstream
     * {@code Collector}.
     * 返回一个收集器，对T类型的输入元素执行级联的分组操作，根据分类器函数对元素进行分组，
     * 然后使用指定的下游收集器对与给定键关联的值执行归约操作。
     *
     * <p>The classification function maps elements to some key type {@code K}.
     * The downstream collector operates on elements of type {@code T} and
     * produces a result of type {@code D}. The resulting collector produces a
     * {@code Map<K, D>}.
     * 分类器函数将元素映射到某个关键类型K，下游收集器对类型T的元素进行操作，并产生类型D的结果。
     * 结果收集器产生映射关系。
     *
     * <p>There are no guarantees on the type, mutability,
     * serializability, or thread-safety of the {@code Map} returned.
     * 不能保证返回的映射对象的类型、可变性、可序列化性或线程安全性。
     *
     * <p>For example, to compute the set of last names of people in each city:
     * <pre>{@code
     *     Map<City, Set<String>> namesByCity
     *         = people.stream().collect(groupingBy(Person::getCity,
     *                                              mapping(Person::getLastName, toSet())));
     * }</pre>
     *
     * @implNote
     * The returned {@code Collector} is not concurrent.  For parallel stream
     * pipelines, the {@code combiner} function operates by merging the keys
     * from one map into another, which can be an expensive operation.  If
     * preservation of the order in which elements are presented to the downstream
     * collector is not required, using {@link #groupingByConcurrent(Function, Collector)}
     * may offer better parallel performance.
     * 返回的收集器不是并发的。对于并行数据流管道，组合器函数的操作方式是将一个映射中的键合并到另一个映射中，这可能是一个代价很高的操作。
     * 如果不需要保持元素在结果映射收集器中出现的顺序，则使用groupingByConcurrent(Function)可以提供更好的并行性能。
     *
     * @param <T> the type of the input elements
     * @param <K> the type of the keys 键的类型
     * @param <A> the intermediate accumulation type of the downstream collector
     * @param <D> the result type of the downstream reduction
     * @param classifier a classifier function mapping input elements to keys
     *                   将输入元素映射到键的分类器函数
     * @param downstream a {@code Collector} implementing the downstream reduction
     *                   实现下游归约操作的收集器
     * @return a {@code Collector} implementing the cascaded group-by operation
     * @see #groupingBy(Function)
     *
     * @see #groupingBy(Function, Supplier, Collector)
     * @see #groupingByConcurrent(Function, Collector)
     */
    public static <T, K, A, D>
    Collector<T, ?, Map<K, D>> groupingBy(Function<? super T, ? extends K> classifier,
                                          Collector<? super T, A, D> downstream) {
        // HashMap::new
        return groupingBy(classifier, HashMap::new, downstream);
    }

    /**
     * Returns a {@code Collector} implementing a cascaded "group by" operation
     * on input elements of type {@code T}, grouping elements according to a
     * classification function, and then performing a reduction operation on
     * the values associated with a given key using the specified downstream
     * {@code Collector}.  The {@code Map} produced by the Collector is created
     * with the supplied factory function.
     * 返回一个收集器，对T类型的输入元素执行级联的分组操作，根据分类器函数对元素进行分组，
     * 然后使用指定的下游收集器对与给定键关联的值执行归约操作。
     * 由收集器生成的映射是用提供的工厂函数创建的。
     *
     * <p>The classification function maps elements to some key type {@code K}.
     * The downstream collector operates on elements of type {@code T} and
     * produces a result of type {@code D}. The resulting collector produces a
     * {@code Map<K, D>}.
     * 分类函数将元素映射到某个关联键类型K，下游收集器对类型T的元素进行操作，并产生类型D的结果。
     * 结果收集器产生映射。
     *
     * <p>For example, to compute the set of last names of people in each city,
     * where the city names are sorted:
     * <pre>{@code
     *     Map<City, Set<String>> namesByCity
     *         = people.stream().collect(groupingBy(Person::getCity, TreeMap::new,
     *                                              mapping(Person::getLastName, toSet())));
     * }</pre>
     *
     * @implNote
     * The returned {@code Collector} is not concurrent.  For parallel stream
     * pipelines, the {@code combiner} function operates by merging the keys
     * from one map into another, which can be an expensive operation.  If
     * preservation of the order in which elements are presented to the downstream
     * collector is not required, using {@link #groupingByConcurrent(Function, Supplier, Collector)}
     * may offer better parallel performance.
     * 返回的收集器不是并发的。对于并行数据流管道，组合器函数的操作方式是将一个映射中的键合并到另一个映射中，这可能是一个代价很高的操作。
     * 如果不需要保持元素在结果映射收集器中出现的顺序，则使用groupingByConcurrent(Function)可以提供更好的并行性能。
     *
     * @param <T> the type of the input elements
     * @param <K> the type of the keys
     * @param <A> the intermediate accumulation type of the downstream collector
     * @param <D> the result type of the downstream reduction
     * @param <M> the type of the resulting {@code Map}
     * @param classifier a classifier function mapping input elements to keys
     *                   将输入元素映射到键的分类器函数
     * @param downstream a {@code Collector} implementing the downstream reduction
     *                   实现下游归约操作的收集器
     * @param mapFactory a function which, when called, produces a new empty
     *                   {@code Map} of the desired type
     * @return a {@code Collector} implementing the cascaded group-by operation
     *
     * @see #groupingBy(Function, Collector)
     * @see #groupingBy(Function)
     * @see #groupingByConcurrent(Function, Supplier, Collector)
     */
    public static <T, K, D, A, M extends Map<K, D>>
    Collector<T, ?, M> groupingBy(Function<? super T, ? extends K> classifier,
                                  Supplier<M> mapFactory,
                                  Collector<? super T, A, D> downstream) {
        // 下游收集器-downstream
        Supplier<A> downstreamSupplier = downstream.supplier();
        BiConsumer<A, ? super T> downstreamAccumulator = downstream.accumulator();
        BiConsumer<Map<K, A>, T> accumulator = (m, t) -> {
            K key = Objects.requireNonNull(classifier.apply(t), "element cannot be mapped to a null key");
            A container = m.computeIfAbsent(key, k -> downstreamSupplier.get());
            downstreamAccumulator.accept(container, t);
        };
        // 映射合并函数
        BinaryOperator<Map<K, A>> merger = Collectors.<K, A, Map<K, A>>mapMerger(downstream.combiner());
        @SuppressWarnings("unchecked")
        Supplier<Map<K, A>> mangledFactory = (Supplier<Map<K, A>>) mapFactory;

        if (downstream.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new CollectorImpl<>(mangledFactory, accumulator, merger, CH_ID);
        }
        else {
            @SuppressWarnings("unchecked")
            Function<A, A> downstreamFinisher = (Function<A, A>) downstream.finisher();
            // 完成器函数
            Function<Map<K, A>, M> finisher = intermediate -> {
                intermediate.replaceAll((k, v) -> downstreamFinisher.apply(v));
                @SuppressWarnings("unchecked")
                M castResult = (M) intermediate;
                return castResult;
            };
            return new CollectorImpl<>(mangledFactory, accumulator, merger, finisher, CH_NOID);
        }
    }

    /**
     * Returns a concurrent {@code Collector} implementing a "group by"
     * operation on input elements of type {@code T}, grouping elements
     * according to a classification function.
     * 返回一个并发收集器，对T类型的输入元素实现分组操作，根据分类函数对元素进行分组。
     *
     * <p>This is a {@link Collector.Characteristics#CONCURRENT concurrent} and
     * {@link Collector.Characteristics#UNORDERED unordered} Collector.
     * 这是一个并发的无序收集器。
     *
     * <p>The classification function maps elements to some key type {@code K}.
     * The collector produces a {@code ConcurrentMap<K, List<T>>} whose keys are the
     * values resulting from applying the classification function to the input
     * elements, and whose corresponding values are {@code List}s containing the
     * input elements which map to the associated key under the classification
     * function.
     *
     * <p>There are no guarantees on the type, mutability, or serializability
     * of the {@code Map} or {@code List} objects returned, or of the
     * thread-safety of the {@code List} objects returned.
     * @implSpec
     * This produces a result similar to:
     * <pre>{@code
     *     groupingByConcurrent(classifier, toList());
     * }</pre>
     *
     * @param <T> the type of the input elements
     * @param <K> the type of the keys
     * @param classifier a classifier function mapping input elements to keys
     * @return a concurrent, unordered {@code Collector} implementing the group-by operation
     *
     * @see #groupingBy(Function)
     * @see #groupingByConcurrent(Function, Collector)
     * @see #groupingByConcurrent(Function, Supplier, Collector)
     */
    public static <T, K>
    Collector<T, ?, ConcurrentMap<K, List<T>>>
    groupingByConcurrent(Function<? super T, ? extends K> classifier) {
        // ConcurrentHashMap::new
        // 下游收集器-toList()
        return groupingByConcurrent(classifier, ConcurrentHashMap::new, toList());
    }

    /**
     * Returns a concurrent {@code Collector} implementing a cascaded "group by"
     * operation on input elements of type {@code T}, grouping elements
     * according to a classification function, and then performing a reduction
     * operation on the values associated with a given key using the specified
     * downstream {@code Collector}.
     * 返回一个并发收集器，对T类型的输入元素实现级联的分组操作，
     * 根据分类函数对元素进行分组，然后使用指定的下游收集器对与给定键关联的值执行归约操作。
     *
     * <p>This is a {@link Collector.Characteristics#CONCURRENT concurrent} and
     * {@link Collector.Characteristics#UNORDERED unordered} Collector.
     * 这是一个并发的无序收集器。
     *
     * <p>The classification function maps elements to some key type {@code K}.
     * The downstream collector operates on elements of type {@code T} and
     * produces a result of type {@code D}. The resulting collector produces a
     * {@code Map<K, D>}.
     * 分类函数将元素映射到某个键类型K，下游收集器对类型T的元素进行操作，并产生类型D的结果。
     * 结果收集器产生映射。
     *
     * <p>For example, to compute the set of last names of people in each city,
     * where the city names are sorted:
     * <pre>{@code
     *     ConcurrentMap<City, Set<String>> namesByCity
     *         = people.stream().collect(groupingByConcurrent(Person::getCity,
     *                                                        mapping(Person::getLastName, toSet())));
     * }</pre>
     *
     * @param <T> the type of the input elements
     * @param <K> the type of the keys
     * @param <A> the intermediate accumulation type of the downstream collector
     * @param <D> the result type of the downstream reduction
     * @param classifier a classifier function mapping input elements to keys
     * @param downstream a {@code Collector} implementing the downstream reduction
     * @return a concurrent, unordered {@code Collector} implementing the cascaded group-by operation
     *
     * @see #groupingBy(Function, Collector)
     * @see #groupingByConcurrent(Function)
     * @see #groupingByConcurrent(Function, Supplier, Collector)
     */
    public static <T, K, A, D>
    Collector<T, ?, ConcurrentMap<K, D>> groupingByConcurrent(Function<? super T, ? extends K> classifier,
                                                              Collector<? super T, A, D> downstream) {
        // ConcurrentHashMap::new
        return groupingByConcurrent(classifier, ConcurrentHashMap::new, downstream);
    }

    /**
     * Returns a concurrent {@code Collector} implementing a cascaded "group by"
     * operation on input elements of type {@code T}, grouping elements
     * according to a classification function, and then performing a reduction
     * operation on the values associated with a given key using the specified
     * downstream {@code Collector}.  The {@code ConcurrentMap} produced by the
     * Collector is created with the supplied factory function.
     * 返回一个并发收集器，对T类型的输入元素实现级联的分组操作，
     * 根据分类函数对元素进行分组，然后使用指定的下游收集器对与给定键关联的值执行归约操作。
     * 由收集器生成的并发映射是用提供的工厂函数创建的。
     *
     * <p>This is a {@link Collector.Characteristics#CONCURRENT concurrent} and
     * {@link Collector.Characteristics#UNORDERED unordered} Collector.
     * 这是一个并发的无序收集器。
     *
     * <p>The classification function maps elements to some key type {@code K}.
     * The downstream collector operates on elements of type {@code T} and
     * produces a result of type {@code D}. The resulting collector produces a
     * {@code Map<K, D>}.
     *
     * <p>For example, to compute the set of last names of people in each city,
     * where the city names are sorted:
     * <pre>{@code
     *     ConcurrentMap<City, Set<String>> namesByCity
     *         = people.stream().collect(groupingBy(Person::getCity, ConcurrentSkipListMap::new,
     *                                              mapping(Person::getLastName, toSet())));
     * }</pre>
     *
     *
     * @param <T> the type of the input elements
     * @param <K> the type of the keys
     * @param <A> the intermediate accumulation type of the downstream collector
     *           下游收集器的中间累加类型
     * @param <D> the result type of the downstream reduction
     *           下游归约的结果类型
     * @param <M> the type of the resulting {@code ConcurrentMap}
     * @param classifier a classifier function mapping input elements to keys
     *                   将输入元素映射到键的分类器函数
     * @param downstream a {@code Collector} implementing the downstream reduction
     * @param mapFactory a function which, when called, produces a new empty
     *                   {@code ConcurrentMap} of the desired type
     * @return a concurrent, unordered {@code Collector} implementing the cascaded group-by operation
     *
     * @see #groupingByConcurrent(Function)
     * @see #groupingByConcurrent(Function, Collector)
     * @see #groupingBy(Function, Supplier, Collector)
     */
    public static <T, K, A, D, M extends ConcurrentMap<K, D>>
    Collector<T, ?, M> groupingByConcurrent(Function<? super T, ? extends K> classifier,
                                            Supplier<M> mapFactory,
                                            Collector<? super T, A, D> downstream) {
        // 下游收集器
        Supplier<A> downstreamSupplier = downstream.supplier();
        BiConsumer<A, ? super T> downstreamAccumulator = downstream.accumulator();
        BinaryOperator<ConcurrentMap<K, A>> merger = Collectors.<K, A, ConcurrentMap<K, A>>mapMerger(downstream.combiner());
        @SuppressWarnings("unchecked")
        Supplier<ConcurrentMap<K, A>> mangledFactory = (Supplier<ConcurrentMap<K, A>>) mapFactory;
        BiConsumer<ConcurrentMap<K, A>, T> accumulator;
        if (downstream.characteristics().contains(Collector.Characteristics.CONCURRENT)) {
            accumulator = (m, t) -> {
                K key = Objects.requireNonNull(classifier.apply(t), "element cannot be mapped to a null key");
                A resultContainer = m.computeIfAbsent(key, k -> downstreamSupplier.get());
                downstreamAccumulator.accept(resultContainer, t);
            };
        }
        else {
            accumulator = (m, t) -> {
                K key = Objects.requireNonNull(classifier.apply(t), "element cannot be mapped to a null key");
                A resultContainer = m.computeIfAbsent(key, k -> downstreamSupplier.get());
                synchronized (resultContainer) {
                    downstreamAccumulator.accept(resultContainer, t);
                }
            };
        }

        if (downstream.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new CollectorImpl<>(mangledFactory, accumulator, merger, CH_CONCURRENT_ID);
        }
        else {
            @SuppressWarnings("unchecked")
            Function<A, A> downstreamFinisher = (Function<A, A>) downstream.finisher();
            Function<ConcurrentMap<K, A>, M> finisher = intermediate -> {
                intermediate.replaceAll((k, v) -> downstreamFinisher.apply(v));
                @SuppressWarnings("unchecked")
                M castResult = (M) intermediate;
                return castResult;
            };
            return new CollectorImpl<>(mangledFactory, accumulator, merger, finisher, CH_CONCURRENT_NOID);
        }
    }

    // 分区操作收集器

    /**
     * Returns a {@code Collector} which partitions the input elements according
     * to a {@code Predicate}, and organizes them into a
     * {@code Map<Boolean, List<T>>}.
     * 返回一个收集器，根据谓词函数对输入元素进行分区，并将它们组织成一个映射。
     *
     * There are no guarantees on the type, mutability,
     * serializability, or thread-safety of the {@code Map} returned.
     * 不能保证返回的映射对象的类型、可变性、可序列化性或线程安全性。
     *
     * @param <T> the type of the input elements
     * @param predicate a predicate used for classifying input elements
     *                  用于对输入元素进行分类的谓词函数
     * @return a {@code Collector} implementing the partitioning operation
     *
     * @see #partitioningBy(Predicate, Collector)
     */
    public static <T>
    Collector<T, ?, Map<Boolean, List<T>>> partitioningBy(Predicate<? super T> predicate) {
        // 下游收集器-toList()
        return partitioningBy(predicate, toList());
    }

    /**
     * Returns a {@code Collector} which partitions the input elements according
     * to a {@code Predicate}, reduces the values in each partition according to
     * another {@code Collector}, and organizes them into a
     * {@code Map<Boolean, D>} whose values are the result of the downstream
     * reduction.
     * 返回一个收集器，根据谓词函数对输入元素进行分区，根据另一个收集器对每个分区中的值进行归约，
     * 并将它们组织成映射，其值是下游归约操作的结果。
     *
     * <p>There are no guarantees on the type, mutability,
     * serializability, or thread-safety of the {@code Map} returned.
     * 不能保证返回的映射对象的类型、可变性、可序列化性或线程安全性。
     *
     * @param <T> the type of the input elements
     * @param <A> the intermediate accumulation type of the downstream collector
     *           下游收集器的中间累加结果的类型
     * @param <D> the result type of the downstream reduction
     *           下游归约操作的结果类型
     * @param predicate a predicate used for classifying input elements
     *                  用于对输入元素进行分类的谓词函数
     * @param downstream a {@code Collector} implementing the downstream
     *                   reduction
     * @return a {@code Collector} implementing the cascaded partitioning
     *         operation
     *
     * @see #partitioningBy(Predicate)
     */
    public static <T, D, A>
    Collector<T, ?, Map<Boolean, D>> partitioningBy(Predicate<? super T> predicate,
                                                    Collector<? super T, A, D> downstream) {
        // 下游收集器的累加器函数
        BiConsumer<A, ? super T> downstreamAccumulator = downstream.accumulator();
        BiConsumer<Partition<A>, T> accumulator = (result, t) ->
                downstreamAccumulator.accept(predicate.test(t) ? result.forTrue : result.forFalse, t);
        // 下游收集器的组合器函数
        BinaryOperator<A> op = downstream.combiner();
        BinaryOperator<Partition<A>> merger = (left, right) ->
                new Partition<>(op.apply(left.forTrue, right.forTrue),
                                op.apply(left.forFalse, right.forFalse));
        // 部分分区结果的提供者
        Supplier<Partition<A>> supplier = () ->
                new Partition<>(downstream.supplier().get(),
                                downstream.supplier().get());
        if (downstream.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new CollectorImpl<>(supplier, accumulator, merger, CH_ID);
        }
        else {
            Function<Partition<A>, Map<Boolean, D>> finisher = par ->
                    new Partition<>(downstream.finisher().apply(par.forTrue),
                                    downstream.finisher().apply(par.forFalse));
            return new CollectorImpl<>(supplier, accumulator, merger, finisher, CH_NOID);
        }
    }

    // 映射操作收集器

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code Map} whose keys and values are the result of applying the provided
     * mapping functions to the input elements.
     * 返回一个收集器，将元素累加到一个映射中，其键和值是将提供的映射函数应用于输入元素的结果。
     *
     * <p>If the mapped keys contains duplicates (according to
     * {@link Object#equals(Object)}), an {@code IllegalStateException} is
     * thrown when the collection operation is performed.  If the mapped keys
     * may have duplicates, use {@link #toMap(Function, Function, BinaryOperator)}
     * instead.
     * 如果映射的键包含重复项(根据Object.equals(Object))，则在执行收集操作时会引发非法状态异常。
     * 如果映射的键可能有重复项，请改用toMap(Function, Function, BinaryOperator)。
     *
     * @apiNote
     * It is common for either the key or the value to be the input elements.
     * In this case, the utility method
     * {@link java.util.function.Function#identity()} may be helpful.
     * 键或值作为输入元素是很常见的。在这种情况下，等价函数可能会有所帮助。
     * For example, the following produces a {@code Map} mapping
     * students to their grade point average:
     * <pre>{@code
     *     Map<Student, Double> studentToGPA =
     *         students.stream().collect(toMap(Functions.identity(),
     *                                         student -> computeGPA(student)));
     * }</pre>
     * And the following produces a {@code Map} mapping a unique identifier to
     * students:
     * <pre>{@code
     *     Map<String, Student> studentIdToStudent =
     *         students.stream().collect(toMap(Student::getId,
     *                                         Functions.identity());
     * }</pre>
     *
     * @implNote
     * The returned {@code Collector} is not concurrent.  For parallel stream
     * pipelines, the {@code combiner} function operates by merging the keys
     * from one map into another, which can be an expensive operation.  If it is
     * not required that results are inserted into the {@code Map} in encounter
     * order, using {@link #toConcurrentMap(Function, Function)}
     * may offer better parallel performance.
     * 返回的收集器不是并发的。对于并行数据流管道，组合器函数通过将键从一个映射合并到另一个映射来操作，这可能是一项昂贵的操作。
     * 如果不需要将结果按遇到顺序插入映射，使用toConcurrentMap(Function, Function)可能会提供更好的并行性能。
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the key mapping function
     * @param <U> the output type of the value mapping function
     * @param keyMapper a mapping function to produce keys
     *                  生成键的映射函数
     * @param valueMapper a mapping function to produce values
     *                    生成值的映射函数
     * @return a {@code Collector} which collects elements into a {@code Map}
     * whose keys and values are the result of applying mapping functions to
     * the input elements
     *
     * @see #toMap(Function, Function, BinaryOperator)
     * @see #toMap(Function, Function, BinaryOperator, Supplier)
     * @see #toConcurrentMap(Function, Function)
     */
    public static <T, K, U>
    Collector<T, ?, Map<K,U>> toMap(Function<? super T, ? extends K> keyMapper,
                                    Function<? super T, ? extends U> valueMapper) {
        // 合并函数-throwingMerger()，如果映射的键包含重复项(根据Object.equals(Object))，则在执行收集操作时会引发非法状态异常
        // 映射对象提供者-HashMap::new
        return toMap(keyMapper, valueMapper, throwingMerger(), HashMap::new);
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code Map} whose keys and values are the result of applying the provided
     * mapping functions to the input elements.
     * 返回一个收集器，将元素累加到一个映射中，其键和值是将提供的映射函数应用于输入元素的结果。
     *
     * <p>If the mapped
     * keys contains duplicates (according to {@link Object#equals(Object)}),
     * the value mapping function is applied to each equal element, and the
     * results are merged using the provided merging function.
     * 如果映射的键包含重复项(根据Object.equals(Object))，则将值的映射函数应用于每个相等的元素，
     * 并使用提供的合并器函数合并结果。
     *
     * @apiNote
     * There are multiple ways to deal with collisions between multiple elements
     * mapping to the same key.  The other forms of {@code toMap} simply use
     * a merge function that throws unconditionally, but you can easily write
     * more flexible merge policies.
     * 有多种方法可以处理映射到同一个键的多个元素之间的冲突。
     * toMap的其他形式只是使用无条件抛出的合并函数，但您可以轻松编写更灵活的合并策略。
     * For example, if you have a stream
     * of {@code Person}, and you want to produce a "phone book" mapping name to
     * address, but it is possible that two persons have the same name, you can
     * do as follows to gracefully deals with these collisions, and produce a
     * {@code Map} mapping names to a concatenated list of addresses:
     * <pre>{@code
     *     Map<String, String> phoneBook =
     *         people.stream().collect(toMap(Person::getName,
     *                                       Person::getAddress,
     *                                       (s, a) -> s + ", " + a));
     * }</pre>
     *
     * @implNote
     * The returned {@code Collector} is not concurrent.  For parallel stream
     * pipelines, the {@code combiner} function operates by merging the keys
     * from one map into another, which can be an expensive operation.  If it is
     * not required that results are merged into the {@code Map} in encounter
     * order, using {@link #toConcurrentMap(Function, Function, BinaryOperator)}
     * may offer better parallel performance.
     * 返回的收集器不是并发的。对于并行数据流管道，组合器函数通过将键从一个映射合并到另一个映射来操作，这可能是一项昂贵的操作。
     * 如果不需要将结果按遇到顺序合并到映射中，使用toConcurrentMap(Function, Function, BinaryOperator)
     * 可能会提供更好的并行性能。
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the key mapping function 键映射函数的输出类型
     * @param <U> the output type of the value mapping function 值映射函数的输出类型
     * @param keyMapper a mapping function to produce keys
     *                  生成键的映射函数
     * @param valueMapper a mapping function to produce values
     *                    生成值的映射函数
     * @param mergeFunction a merge function, used to resolve collisions between
     *                      values associated with the same key, as supplied
     *                      to {@link Map#merge(Object, Object, BiFunction)}
     *                      合并函数，用于解决与同一键关联的值之间的冲突
     * @return a {@code Collector} which collects elements into a {@code Map}
     * whose keys are the result of applying a key mapping function to the input
     * elements, and whose values are the result of applying a value mapping
     * function to all input elements equal to the key and combining them
     * using the merge function
     * 返回一个收集器，将元素收集到一个映射中，其键是将键的映射函数应用于输入元素的结果，
     * 其值是将值的映射函数应用于等于键的所有输入元素并使用合并函数组合它们的结果。
     *
     * @see #toMap(Function, Function)
     * @see #toMap(Function, Function, BinaryOperator, Supplier)
     * @see #toConcurrentMap(Function, Function, BinaryOperator)
     */
    public static <T, K, U>
    Collector<T, ?, Map<K, U>> toMap(Function<? super T, ? extends K> keyMapper,
                                    Function<? super T, ? extends U> valueMapper,
                                    BinaryOperator<U> mergeFunction) {
        // mergeFunction-合并函数
        // 映射对象提供者-HashMap::new
        return toMap(keyMapper, valueMapper, mergeFunction, HashMap::new);
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code Map} whose keys and values are the result of applying the provided
     * mapping functions to the input elements.
     * 返回一个收集器，将元素累加到一个映射中，其键和值是将提供的映射函数应用于输入元素的结果。
     *
     * <p>If the mapped
     * keys contains duplicates (according to {@link Object#equals(Object)}),
     * the value mapping function is applied to each equal element, and the
     * results are merged using the provided merging function.  The {@code Map}
     * is created by a provided supplier function.
     * 如果映射的键包含重复项(根据Object.equals(Object))，则将值的映射函数应用于每个相等的元素，
     * 并使用提供的合并函数合并结果。
     * 映射对象由提供的结果提供者函数创建。
     *
     * @implNote
     * The returned {@code Collector} is not concurrent.  For parallel stream
     * pipelines, the {@code combiner} function operates by merging the keys
     * from one map into another, which can be an expensive operation.  If it is
     * not required that results are merged into the {@code Map} in encounter
     * order, using {@link #toConcurrentMap(Function, Function, BinaryOperator, Supplier)}
     * may offer better parallel performance.
     * 返回一个收集器，将元素收集到一个映射中，其键是将键的映射函数应用于输入元素的结果，
     * 其值是将值的映射函数应用于等于键的所有输入元素并使用合并函数组合它们的结果。
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the key mapping function
     * @param <U> the output type of the value mapping function
     * @param <M> the type of the resulting {@code Map}
     * @param keyMapper a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @param mergeFunction a merge function, used to resolve collisions between
     *                      values associated with the same key, as supplied
     *                      to {@link Map#merge(Object, Object, BiFunction)}
     *                      合并函数，用于解决与同一键关联的值之间的冲突
     * @param mapSupplier a function which returns a new, empty {@code Map} into
     *                    which the results will be inserted
     *                    一个函数，返回一个新的空映射对象，结果将被插入其中
     * @return a {@code Collector} which collects elements into a {@code Map}
     * whose keys are the result of applying a key mapping function to the input
     * elements, and whose values are the result of applying a value mapping
     * function to all input elements equal to the key and combining them
     * using the merge function
     * 返回一个收集器，将元素收集到一个映射中，其键是将键的映射函数应用于输入元素的结果，
     * 其值是将值的映射函数应用于等于键的所有输入元素并使用合并函数组合它们的结果。
     *
     * @see #toMap(Function, Function)
     * @see #toMap(Function, Function, BinaryOperator)
     * @see #toConcurrentMap(Function, Function, BinaryOperator, Supplier)
     */
    public static <T, K, U, M extends Map<K, U>>
    Collector<T, ?, M> toMap(Function<? super T, ? extends K> keyMapper,
                                Function<? super T, ? extends U> valueMapper,
                                BinaryOperator<U> mergeFunction,
                                Supplier<M> mapSupplier) {
        // 合并函数-mergeFunction
        // 累加器函数-accumulator
        BiConsumer<M, T> accumulator
                = (map, element) -> map.merge(keyMapper.apply(element),
                                              valueMapper.apply(element), mergeFunction);
        // 映射对象提供者-mapSupplier
        return new CollectorImpl<>(mapSupplier, accumulator, mapMerger(mergeFunction), CH_ID);
    }

    // 并发映射操作收集器

    /**
     * Returns a concurrent {@code Collector} that accumulates elements into a
     * {@code ConcurrentMap} whose keys and values are the result of applying
     * the provided mapping functions to the input elements.
     * 返回一个并发收集器，将元素累加到一个并发映射中，其键和值是将提供的映射函数应用于输入元素的结果。
     *
     * <p>If the mapped keys contains duplicates (according to
     * {@link Object#equals(Object)}), an {@code IllegalStateException} is
     * thrown when the collection operation is performed.  If the mapped keys
     * may have duplicates, use
     * {@link #toConcurrentMap(Function, Function, BinaryOperator)} instead.
     * 如果映射的键包含重复项(根据Object.equals(Object))，则在执行收集操作时会引发非法状态异常。
     * 如果映射的键可能有重复项，请改用toConcurrentMap(Function, Function, BinaryOperator)。
     *
     * @apiNote
     * It is common for either the key or the value to be the input elements.
     * In this case, the utility method
     * {@link java.util.function.Function#identity()} may be helpful.
     * 键或值作为输入元素是很常见的。
     * 在这种情况下，等价函数可能会有所帮助。
     * For example, the following produces a {@code Map} mapping
     * students to their grade point average:
     * <pre>{@code
     *     Map<Student, Double> studentToGPA
     *         students.stream().collect(toMap(Functions.identity(),
     *                                         student -> computeGPA(student)));
     * }</pre>
     * And the following produces a {@code Map} mapping a unique identifier to
     * students:
     * <pre>{@code
     *     Map<String, Student> studentIdToStudent
     *         students.stream().collect(toConcurrentMap(Student::getId,
     *                                                   Functions.identity());
     * }</pre>
     *
     * <p>This is a {@link Collector.Characteristics#CONCURRENT concurrent} and
     * {@link Collector.Characteristics#UNORDERED unordered} Collector.
     * 这是一个并发的无序收集器。
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the key mapping function
     * @param <U> the output type of the value mapping function
     * @param keyMapper the mapping function to produce keys
     *                  生成键的映射函数
     * @param valueMapper the mapping function to produce values
     *                    生成值的映射函数
     * @return a concurrent, unordered {@code Collector} which collects elements into a
     * {@code ConcurrentMap} whose keys are the result of applying a key mapping
     * function to the input elements, and whose values are the result of
     * applying a value mapping function to the input elements
     * 一个并发的无序收集器，将元素收集到并发映射中，其键是键的映射函数应用于输入元素的结果，
     * 其值是将值的映射函数应用于输入元素的结果。
     *
     * @see #toMap(Function, Function)
     * @see #toConcurrentMap(Function, Function, BinaryOperator)
     * @see #toConcurrentMap(Function, Function, BinaryOperator, Supplier)
     */
    public static <T, K, U>
    Collector<T, ?, ConcurrentMap<K,U>> toConcurrentMap(Function<? super T, ? extends K> keyMapper,
                                                        Function<? super T, ? extends U> valueMapper) {
        // 合并函数-throwingMerger()，如果映射的键包含重复项(根据Object.equals(Object))，则在执行收集操作时会引发非法状态异常
        // 映射对象提供者-ConcurrentHashMap::new
        return toConcurrentMap(keyMapper, valueMapper, throwingMerger(), ConcurrentHashMap::new);
    }

    /**
     * Returns a concurrent {@code Collector} that accumulates elements into a
     * {@code ConcurrentMap} whose keys and values are the result of applying
     * the provided mapping functions to the input elements.
     * 返回一个并发收集器，将元素累加到一个并发映射中，其键和值是将提供的映射函数应用于输入元素的结果。
     *
     * <p>If the mapped keys contains duplicates (according to {@link Object#equals(Object)}),
     * the value mapping function is applied to each equal element, and the
     * results are merged using the provided merging function.
     * 如果映射的键包含重复项(根据Object.equals(Object))，则将值的映射函数应用于每个相等的元素，
     * 并使用提供的合并函数合并结果。
     * 映射对象由提供的结果提供者函数创建。
     *
     * @apiNote
     * There are multiple ways to deal with collisions between multiple elements
     * mapping to the same key.  The other forms of {@code toConcurrentMap} simply use
     * a merge function that throws unconditionally, but you can easily write
     * more flexible merge policies.
     * 有多种方法可以处理映射到同一个键的多个元素之间的冲突。
     * toMap的其他形式只是使用无条件抛出的合并函数，但您可以轻松编写更灵活的合并策略。
     * For example, if you have a stream
     * of {@code Person}, and you want to produce a "phone book" mapping name to
     * address, but it is possible that two persons have the same name, you can
     * do as follows to gracefully deals with these collisions, and produce a
     * {@code Map} mapping names to a concatenated list of addresses:
     * <pre>{@code
     *     Map<String, String> phoneBook
     *         people.stream().collect(toConcurrentMap(Person::getName,
     *                                                 Person::getAddress,
     *                                                 (s, a) -> s + ", " + a));
     * }</pre>
     *
     * <p>This is a {@link Collector.Characteristics#CONCURRENT concurrent} and
     * {@link Collector.Characteristics#UNORDERED unordered} Collector.
     * 这是一个并发的无序收集器。
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the key mapping function
     * @param <U> the output type of the value mapping function
     * @param keyMapper a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @param mergeFunction a merge function, used to resolve collisions between
     *                      values associated with the same key, as supplied
     *                      to {@link Map#merge(Object, Object, BiFunction)}
     *                      合并函数，用于解决与同一键关联的值之间的冲突
     * @return a concurrent, unordered {@code Collector} which collects elements into a
     * {@code ConcurrentMap} whose keys are the result of applying a key mapping
     * function to the input elements, and whose values are the result of
     * applying a value mapping function to all input elements equal to the key
     * and combining them using the merge function
     * 一个并发的无序收集器，将元素收集到并发映射中，其键是键的映射函数应用于输入元素的结果，
     * 其值是将值的映射函数应用于输入元素的结果。
     *
     * @see #toConcurrentMap(Function, Function)
     * @see #toConcurrentMap(Function, Function, BinaryOperator, Supplier)
     * @see #toMap(Function, Function, BinaryOperator)
     */
    public static <T, K, U>
    Collector<T, ?, ConcurrentMap<K,U>>
    toConcurrentMap(Function<? super T, ? extends K> keyMapper,
                    Function<? super T, ? extends U> valueMapper,
                    BinaryOperator<U> mergeFunction) {
        // 合并函数-mergeFunction
        // 映射对象提供者-ConcurrentHashMap::new
        return toConcurrentMap(keyMapper, valueMapper, mergeFunction, ConcurrentHashMap::new);
    }

    /**
     * Returns a concurrent {@code Collector} that accumulates elements into a
     * {@code ConcurrentMap} whose keys and values are the result of applying
     * the provided mapping functions to the input elements.
     * 返回一个并发收集器，将元素累加到一个并发映射中，其键和值是将提供的映射函数应用于输入元素的结果。
     *
     * <p>If the mapped keys contains duplicates (according to {@link Object#equals(Object)}),
     * the value mapping function is applied to each equal element, and the
     * results are merged using the provided merging function.  The
     * {@code ConcurrentMap} is created by a provided supplier function.
     * 如果映射的键包含重复项(根据Object.equals(Object))，则将值的映射函数应用于每个相等的元素，
     * 并使用提供的合并函数合并结果。
     * 映射对象由提供的结果提供者函数创建。
     *
     * <p>This is a {@link Collector.Characteristics#CONCURRENT concurrent} and
     * {@link Collector.Characteristics#UNORDERED unordered} Collector.
     * 这是一个并发的无序收集器。
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the key mapping function
     * @param <U> the output type of the value mapping function
     * @param <M> the type of the resulting {@code ConcurrentMap}
     * @param keyMapper a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @param mergeFunction a merge function, used to resolve collisions between
     *                      values associated with the same key, as supplied
     *                      to {@link Map#merge(Object, Object, BiFunction)}
     *                      合并函数，用于解决与同一键关联的值之间的冲突
     * @param mapSupplier a function which returns a new, empty {@code Map} into
     *                    which the results will be inserted
     *                    一个函数，返回一个新的空映射对象，结果将被插入其中
     * @return a concurrent, unordered {@code Collector} which collects elements into a
     * {@code ConcurrentMap} whose keys are the result of applying a key mapping
     * function to the input elements, and whose values are the result of
     * applying a value mapping function to all input elements equal to the key
     * and combining them using the merge function
     * 一个并发的无序收集器，将元素收集到并发映射中，其键是键的映射函数应用于输入元素的结果，
     * 其值是将值的映射函数应用于输入元素的结果。
     *
     * @see #toConcurrentMap(Function, Function)
     * @see #toConcurrentMap(Function, Function, BinaryOperator)
     * @see #toMap(Function, Function, BinaryOperator, Supplier)
     */
    public static <T, K, U, M extends ConcurrentMap<K, U>>
    Collector<T, ?, M> toConcurrentMap(Function<? super T, ? extends K> keyMapper,
                                       Function<? super T, ? extends U> valueMapper,
                                       BinaryOperator<U> mergeFunction,
                                       Supplier<M> mapSupplier) {
        // 合并函数-mergeFunction
        // 累加器函数-accumulator
        BiConsumer<M, T> accumulator
                = (map, element) -> map.merge(keyMapper.apply(element),
                                              valueMapper.apply(element), mergeFunction);
        // 映射对象提供者-mapSupplier
        return new CollectorImpl<>(mapSupplier, accumulator, mapMerger(mergeFunction), CH_CONCURRENT_ID);
    }

    // 汇总统计操作函数

    /**
     * Returns a {@code Collector} which applies an {@code int}-producing
     * mapping function to each input element, and returns summary statistics
     * for the resulting values.
     * 返回一个收集器，将一个产生整数值的映射函数应用于每个输入元素，
     * 并返回结果值的汇总统计信息。
     *
     * @param <T> the type of the input elements
     * @param mapper a mapping function to apply to each element
     * @return a {@code Collector} implementing the summary-statistics reduction
     * 实现汇总统计归约的收集器
     *
     * @see #summarizingDouble(ToDoubleFunction)
     * @see #summarizingLong(ToLongFunction)
     */
    public static <T>
    Collector<T, ?, IntSummaryStatistics> summarizingInt(ToIntFunction<? super T> mapper) {
        // 结果提供者-IntSummaryStatistics::new
        return new CollectorImpl<T, IntSummaryStatistics, IntSummaryStatistics>(
                IntSummaryStatistics::new,
                (r, t) -> r.accept(mapper.applyAsInt(t)),
                (l, r) -> { l.combine(r); return l; }, CH_ID);
    }

    /**
     * Returns a {@code Collector} which applies an {@code long}-producing
     * mapping function to each input element, and returns summary statistics
     * for the resulting values.
     * 返回一个收集器，将一个产生长整数值的映射函数应用于每个输入元素，
     * 并返回结果值的汇总统计信息。
     *
     * @param <T> the type of the input elements
     * @param mapper the mapping function to apply to each element
     * @return a {@code Collector} implementing the summary-statistics reduction
     * 实现汇总统计归约的收集器
     *
     * @see #summarizingDouble(ToDoubleFunction)
     * @see #summarizingInt(ToIntFunction)
     */
    public static <T>
    Collector<T, ?, LongSummaryStatistics> summarizingLong(ToLongFunction<? super T> mapper) {
        // 结果提供者-LongSummaryStatistics::new
        return new CollectorImpl<T, LongSummaryStatistics, LongSummaryStatistics>(
                LongSummaryStatistics::new,
                (r, t) -> r.accept(mapper.applyAsLong(t)),
                (l, r) -> { l.combine(r); return l; }, CH_ID);
    }

    /**
     * Returns a {@code Collector} which applies an {@code double}-producing
     * mapping function to each input element, and returns summary statistics
     * for the resulting values.
     * 返回一个收集器，将一个产生数值的映射函数应用于每个输入元素，
     * 并返回结果值的汇总统计信息。
     *
     * @param <T> the type of the input elements
     * @param mapper a mapping function to apply to each element
     * @return a {@code Collector} implementing the summary-statistics reduction
     * 实现汇总统计归约的收集器
     *
     * @see #summarizingLong(ToLongFunction)
     * @see #summarizingInt(ToIntFunction)
     */
    public static <T>
    Collector<T, ?, DoubleSummaryStatistics> summarizingDouble(ToDoubleFunction<? super T> mapper) {
        // 结果提供者-DoubleSummaryStatistics::new
        return new CollectorImpl<T, DoubleSummaryStatistics, DoubleSummaryStatistics>(
                DoubleSummaryStatistics::new,
                (r, t) -> r.accept(mapper.applyAsDouble(t)),
                (l, r) -> { l.combine(r); return l; }, CH_ID);
    }

    /**
     * Implementation class used by partitioningBy.
     * 分区操作收集器使用的实现类。
     */
    private static final class Partition<T>
            extends AbstractMap<Boolean, T>
            implements Map<Boolean, T> {
        /**
         * 匹配结果为true的结果集
         */
        final T forTrue;
        /**
         * 匹配结果为false的结果集
         */
        final T forFalse;

        Partition(T forTrue, T forFalse) {
            this.forTrue = forTrue;
            this.forFalse = forFalse;
        }

        @Override
        public Set<Map.Entry<Boolean, T>> entrySet() {
            return new AbstractSet<Map.Entry<Boolean, T>>() {
                @Override
                public Iterator<Map.Entry<Boolean, T>> iterator() {
                    // SimpleImmutableEntry-一个维护不可变键和值的映射条目
                    Map.Entry<Boolean, T> falseEntry = new SimpleImmutableEntry<>(false, forFalse);
                    Map.Entry<Boolean, T> trueEntry = new SimpleImmutableEntry<>(true, forTrue);
                    return Arrays.asList(falseEntry, trueEntry).iterator();
                }

                @Override
                public int size() {
                    return 2;
                }
            };
        }
    }
}
