
package java.util.stream;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Supplier;

/**
 * Low-level utility methods for creating and manipulating streams.
 * 用于创建和操作数据流的底层实用程序方法。
 *
 * <p>This class is mostly for library writers presenting stream views
 * of data structures; most static stream methods intended for end users are in
 * the various {@code Stream} classes.
 * 这个类主要是为库编写者提供数据结构的数据流视图。
 * 大多数面向终端用户的静态方法都在各种数据流类中。
 *
 * @since 1.8
 */
public final class StreamSupport {

    // Suppresses default constructor, ensuring non-instantiability.
    private StreamSupport() {
        // empty
    }

    // 数据流的静态工厂方法

    /**
     * Creates a new sequential or parallel {@code Stream} from a
     * {@code Spliterator}.
     * 从拆分器创建一个新的顺序或并行数据流。
     *
     * <p>The spliterator is only traversed, split, or queried for estimated
     * size after the terminal operation of the stream pipeline commences.
     * 拆分器只在数据流管道的终结操作开始后被遍历、分割或查询估计的大小。
     *
     * <p>It is strongly recommended the spliterator report a characteristic of
     * {@code IMMUTABLE} or {@code CONCURRENT}, or be
     * <a href="../Spliterator.html#binding">late-binding</a>.  Otherwise,
     * {@link #stream(java.util.function.Supplier, int, boolean)} should be used
     * to reduce the scope of potential interference with the source.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     * 强烈建议，拆分器报告不可变或并发的特性，或者是延迟绑定的特性。
     * 否则，应该使用stream(Supplier, int, boolean)来减少与数据源的潜在干扰范围。
     * 有关更多细节，请参阅非干扰。
     *
     * @param <T> the type of stream elements
     *           数据流元素的类型
     * @param spliterator a {@code Spliterator} describing the stream elements
     *                    描述数据流元素的拆分器
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     *                 如果为真，则返回的数据流是一个并行数据流；
     *                 如果为假，则返回的数据流是顺序数据流。
     * @return a new sequential or parallel {@code Stream}
     * 一个新的顺序或并行数据流
     */
    public static <T> Stream<T> stream(Spliterator<T> spliterator, boolean parallel) {
        Objects.requireNonNull(spliterator);
        // 引用数据流的流水线-ReferencePipeline.Head
        return new ReferencePipeline.Head<>(spliterator,
                                            StreamOpFlag.fromCharacteristics(spliterator),
                                            parallel);
    }

    /**
     * Creates a new sequential or parallel {@code Stream} from a
     * {@code Supplier} of {@code Spliterator}.
     * 从拆分器创建一个新的顺序或并行数据流。
     *
     * <p>The {@link Supplier#get()} method will be invoked on the supplier no
     * more than once, and only after the terminal operation of the stream pipeline
     * commences.
     * 对结果提供者调用Supplier.get()方法不超过一次，并且只在数据流管道的终结操作开始之后才调用。
     *
     * <p>For spliterators that report a characteristic of {@code IMMUTABLE}
     * or {@code CONCURRENT}, or that are
     * <a href="../Spliterator.html#binding">late-binding</a>, it is likely
     * more efficient to use {@link #stream(java.util.Spliterator, boolean)}
     * instead.
     * 对于报告不可变或并发特性的拆分器，或延迟绑定的拆分器，使用stream(Spliterator, boolean)可能更有效。
     *
     * <p>The use of a {@code Supplier} in this form provides a level of
     * indirection that reduces the scope of potential interference with the
     * source.  Since the supplier is only invoked after the terminal operation
     * commences, any modifications to the source up to the start of the
     * terminal operation are reflected in the stream result.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     * 这种形式的结果提供者的使用提供了一种间接层，归约了对数据源的潜在干扰范围。
     * 由于结果提供者仅在终结操作开始后才被调用，因此在终结操作开始之前对数据源的任何修改都会反映在数据流结果中。
     * 有关更多细节，请参阅不干扰。
     *
     * @param <T> the type of stream elements
     *           数据流元素的类型
     * @param supplier a {@code Supplier} of a {@code Spliterator}
     *                 拆分器的提供者
     * @param characteristics Spliterator characteristics of the supplied
     *        {@code Spliterator}.  The characteristics must be equal to
     *        {@code supplier.get().characteristics()}, otherwise undefined
     *        behavior may occur when terminal operation commences.
     *                        所提供的拆分器的拆分器特性。
     *                        这些特征必须等于supplier.get().characteristics()，否则在终结操作开始时可能会出现未定义的行为。
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     *                 如果为真，则返回的数据流是一个并行数据流；
     *                 如果为假，则返回的数据流是一个顺序数据流。
     * @return a new sequential or parallel {@code Stream}
     * 一种新的顺序或并行数据流
     * @see #stream(java.util.Spliterator, boolean)
     */
    public static <T> Stream<T> stream(Supplier<? extends Spliterator<T>> supplier,
                                       int characteristics,
                                       boolean parallel) {
        Objects.requireNonNull(supplier);
        // 引用数据流的流水线-ReferencePipeline.Head
        return new ReferencePipeline.Head<>(supplier,
                                            StreamOpFlag.fromCharacteristics(characteristics),
                                            parallel);
    }

    /**
     * Creates a new sequential or parallel {@code IntStream} from a
     * {@code Spliterator.OfInt}.
     * 从Spliterator.OfInt中创建一个新的顺序或并行的整数数据流。
     *
     * <p>The spliterator is only traversed, split, or queried for estimated size
     * after the terminal operation of the stream pipeline commences.
     * 拆分器只在数据流管道的终结操作开始后被遍历、分割或查询估计的大小。
     *
     * <p>It is strongly recommended the spliterator report a characteristic of
     * {@code IMMUTABLE} or {@code CONCURRENT}, or be
     * <a href="../Spliterator.html#binding">late-binding</a>.  Otherwise,
     * {@link #intStream(java.util.function.Supplier, int, boolean)} should be
     * used to reduce the scope of potential interference with the source.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     * 强烈建议，拆分器报告不可变或并发的特性，或者是延迟绑定的特性。
     * 否则，应该使用intStream(Supplier, int, boolean)来减少数据源的潜在干扰范围。
     * 有关更多细节，请参阅不干扰。
     *
     * @param spliterator a {@code Spliterator.OfInt} describing the stream elements
     *                    描述数据流元素的拆分器
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     * @return a new sequential or parallel {@code IntStream}
     * 一个新的顺序或并行的整数数据流
     */
    public static IntStream intStream(Spliterator.OfInt spliterator, boolean parallel) {
        // 整数数据流的流水线-IntPipeline.Head
        return new IntPipeline.Head<>(spliterator,
                                      StreamOpFlag.fromCharacteristics(spliterator),
                                      parallel);
    }

    /**
     * Creates a new sequential or parallel {@code IntStream} from a
     * {@code Supplier} of {@code Spliterator.OfInt}.
     * 从Spliterator.OfInt中创建一个新的顺序或并行的整数数据流。
     *
     * <p>The {@link Supplier#get()} method will be invoked on the supplier no
     * more than once, and only after the terminal operation of the stream pipeline
     * commences.
     *
     * <p>For spliterators that report a characteristic of {@code IMMUTABLE}
     * or {@code CONCURRENT}, or that are
     * <a href="../Spliterator.html#binding">late-binding</a>, it is likely
     * more efficient to use {@link #intStream(java.util.Spliterator.OfInt, boolean)}
     * instead.
     * <p>The use of a {@code Supplier} in this form provides a level of
     * indirection that reduces the scope of potential interference with the
     * source.  Since the supplier is only invoked after the terminal operation
     * commences, any modifications to the source up to the start of the
     * terminal operation are reflected in the stream result.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     *
     * @param supplier a {@code Supplier} of a {@code Spliterator.OfInt}
     * @param characteristics Spliterator characteristics of the supplied
     *        {@code Spliterator.OfInt}.  The characteristics must be equal to
     *        {@code supplier.get().characteristics()}, otherwise undefined
     *        behavior may occur when terminal operation commences.
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     * @return a new sequential or parallel {@code IntStream}
     * @see #intStream(java.util.Spliterator.OfInt, boolean)
     */
    public static IntStream intStream(Supplier<? extends Spliterator.OfInt> supplier,
                                      int characteristics,
                                      boolean parallel) {
        // 整数数据流的流水线-IntPipeline.Head
        return new IntPipeline.Head<>(supplier,
                                      StreamOpFlag.fromCharacteristics(characteristics),
                                      parallel);
    }

    /**
     * Creates a new sequential or parallel {@code LongStream} from a
     * {@code Spliterator.OfLong}.
     * 从Spliterator.OfLong中创建一个新的顺序或并行的长整数数据流。
     *
     * <p>The spliterator is only traversed, split, or queried for estimated
     * size after the terminal operation of the stream pipeline commences.
     *
     * <p>It is strongly recommended the spliterator report a characteristic of
     * {@code IMMUTABLE} or {@code CONCURRENT}, or be
     * <a href="../Spliterator.html#binding">late-binding</a>.  Otherwise,
     * {@link #longStream(java.util.function.Supplier, int, boolean)} should be
     * used to reduce the scope of potential interference with the source.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     *
     * @param spliterator a {@code Spliterator.OfLong} describing the stream elements
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     * @return a new sequential or parallel {@code LongStream}
     */
    public static LongStream longStream(Spliterator.OfLong spliterator,
                                        boolean parallel) {
        // 长整数数据流的流水线-LongPipeline.Head
        return new LongPipeline.Head<>(spliterator,
                                       StreamOpFlag.fromCharacteristics(spliterator),
                                       parallel);
    }

    /**
     * Creates a new sequential or parallel {@code LongStream} from a
     * {@code Supplier} of {@code Spliterator.OfLong}.
     * 从Spliterator.OfLong中创建一个新的顺序或并行的长整数数据流。
     *
     * <p>The {@link Supplier#get()} method will be invoked on the supplier no
     * more than once, and only after the terminal operation of the stream pipeline
     * commences.
     *
     * <p>For spliterators that report a characteristic of {@code IMMUTABLE}
     * or {@code CONCURRENT}, or that are
     * <a href="../Spliterator.html#binding">late-binding</a>, it is likely
     * more efficient to use {@link #longStream(java.util.Spliterator.OfLong, boolean)}
     * instead.
     * <p>The use of a {@code Supplier} in this form provides a level of
     * indirection that reduces the scope of potential interference with the
     * source.  Since the supplier is only invoked after the terminal operation
     * commences, any modifications to the source up to the start of the
     * terminal operation are reflected in the stream result.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     *
     * @param supplier a {@code Supplier} of a {@code Spliterator.OfLong}
     * @param characteristics Spliterator characteristics of the supplied
     *        {@code Spliterator.OfLong}.  The characteristics must be equal to
     *        {@code supplier.get().characteristics()}, otherwise undefined
     *        behavior may occur when terminal operation commences.
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     * @return a new sequential or parallel {@code LongStream}
     * @see #longStream(java.util.Spliterator.OfLong, boolean)
     */
    public static LongStream longStream(Supplier<? extends Spliterator.OfLong> supplier,
                                        int characteristics,
                                        boolean parallel) {
        // 长整数数据流的流水线-LongPipeline.Head
        return new LongPipeline.Head<>(supplier,
                                       StreamOpFlag.fromCharacteristics(characteristics),
                                       parallel);
    }

    /**
     * Creates a new sequential or parallel {@code DoubleStream} from a
     * {@code Spliterator.OfDouble}.
     *
     * <p>The spliterator is only traversed, split, or queried for estimated size
     * after the terminal operation of the stream pipeline commences.
     *
     * <p>It is strongly recommended the spliterator report a characteristic of
     * {@code IMMUTABLE} or {@code CONCURRENT}, or be
     * <a href="../Spliterator.html#binding">late-binding</a>.  Otherwise,
     * {@link #doubleStream(java.util.function.Supplier, int, boolean)} should
     * be used to reduce the scope of potential interference with the source.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     *
     * @param spliterator A {@code Spliterator.OfDouble} describing the stream elements
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     * @return a new sequential or parallel {@code DoubleStream}
     */
    public static DoubleStream doubleStream(Spliterator.OfDouble spliterator,
                                            boolean parallel) {
        return new DoublePipeline.Head<>(spliterator,
                                         StreamOpFlag.fromCharacteristics(spliterator),
                                         parallel);
    }

    /**
     * Creates a new sequential or parallel {@code DoubleStream} from a
     * {@code Supplier} of {@code Spliterator.OfDouble}.
     *
     * <p>The {@link Supplier#get()} method will be invoked on the supplier no
     * more than once, and only after the terminal operation of the stream pipeline
     * commences.
     *
     * <p>For spliterators that report a characteristic of {@code IMMUTABLE}
     * or {@code CONCURRENT}, or that are
     * <a href="../Spliterator.html#binding">late-binding</a>, it is likely
     * more efficient to use {@link #doubleStream(java.util.Spliterator.OfDouble, boolean)}
     * instead.
     * <p>The use of a {@code Supplier} in this form provides a level of
     * indirection that reduces the scope of potential interference with the
     * source.  Since the supplier is only invoked after the terminal operation
     * commences, any modifications to the source up to the start of the
     * terminal operation are reflected in the stream result.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     *
     * @param supplier A {@code Supplier} of a {@code Spliterator.OfDouble}
     * @param characteristics Spliterator characteristics of the supplied
     *        {@code Spliterator.OfDouble}.  The characteristics must be equal to
     *        {@code supplier.get().characteristics()}, otherwise undefined
     *        behavior may occur when terminal operation commences.
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     * @return a new sequential or parallel {@code DoubleStream}
     * @see #doubleStream(java.util.Spliterator.OfDouble, boolean)
     */
    public static DoubleStream doubleStream(Supplier<? extends Spliterator.OfDouble> supplier,
                                            int characteristics,
                                            boolean parallel) {
        return new DoublePipeline.Head<>(supplier,
                                         StreamOpFlag.fromCharacteristics(characteristics),
                                         parallel);
    }
}
