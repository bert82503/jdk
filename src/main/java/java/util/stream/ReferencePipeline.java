
package java.util.stream;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * Abstract base class for an intermediate pipeline stage or pipeline source
 * stage implementing whose elements are of type {@code U}.
 * 元素类型为U的中间数据流管道阶段或管道源阶段实现的抽象基类。
 *
 * @param <P_IN> type of elements in the upstream source
 *              上游数据源中元素的类型
 * @param <P_OUT> type of elements in produced by this stage
 *               在这一阶段产生的元素类型
 *
 * @since 1.8
 */
abstract class ReferencePipeline<P_IN, P_OUT>
        extends AbstractPipeline<P_IN, P_OUT, Stream<P_OUT>>
        implements Stream<P_OUT>  {

    /**
     * Constructor for the head of a stream pipeline.
     * 数据流管道的头节点的构造函数。
     *
     * @param source {@code Supplier<Spliterator>} describing the stream source
     * @param sourceFlags the source flags for the stream source, described in
     *        {@link StreamOpFlag}
     * @param parallel {@code true} if the pipeline is parallel
     */
    ReferencePipeline(Supplier<? extends Spliterator<?>> source,
                      int sourceFlags, boolean parallel) {
        super(source, sourceFlags, parallel);
    }

    /**
     * Constructor for the head of a stream pipeline.
     * 数据流管道的头节点的构造函数。
     *
     * @param source {@code Spliterator} describing the stream source
     * @param sourceFlags The source flags for the stream source, described in
     *        {@link StreamOpFlag}
     * @param parallel {@code true} if the pipeline is parallel
     */
    ReferencePipeline(Spliterator<?> source,
                      int sourceFlags, boolean parallel) {
        super(source, sourceFlags, parallel);
    }

    /**
     * Constructor for appending an intermediate operation onto an existing
     * pipeline.
     * 将中间操作追加到现有流水线的构造函数。
     *
     * @param upstream the upstream element source.
     *                 上游元素数据源
     */
    ReferencePipeline(AbstractPipeline<?, P_IN, ?> upstream, int opFlags) {
        super(upstream, opFlags);
    }

    // Shape-specific methods

    @Override
    final StreamShape getOutputShape() {
        // 对象引用
        return StreamShape.REFERENCE;
    }

    @Override
    final <P_IN> Node<P_OUT> evaluateToNode(PipelineHelper<P_OUT> helper,
                                        Spliterator<P_IN> spliterator,
                                        boolean flattenTree,
                                        IntFunction<P_OUT[]> generator) {
        // 将数据流管道输出的元素收集到保存这个形状元素的节点中
        return Nodes.collect(helper, spliterator, flattenTree, generator);
    }

    @Override
    final <P_IN> Spliterator<P_OUT> wrap(PipelineHelper<P_OUT> ph,
                                     Supplier<Spliterator<P_IN>> supplier,
                                     boolean isParallel) {
        // 创建一个拆分器，它包装了一个数据源拆分器，与这个数据流形状兼容，以及与流水线助手相关的操作
        return new StreamSpliterators.WrappingSpliterator<>(ph, supplier, isParallel);
    }

    @Override
    final Spliterator<P_OUT> lazySpliterator(Supplier<? extends Spliterator<P_OUT>> supplier) {
        // 延迟拆分器
        return new StreamSpliterators.DelegatingSpliterator<>(supplier);
    }

    @Override
    final void forEachWithCancel(Spliterator<P_OUT> spliterator, Sink<P_OUT> sink) {
        do { } while (!sink.cancellationRequested() && spliterator.tryAdvance(sink));
    }

    @Override
    final Node.Builder<P_OUT> makeNodeBuilder(long exactSizeIfKnown, IntFunction<P_OUT[]> generator) {
        return Nodes.builder(exactSizeIfKnown, generator);
    }


    // BaseStream
    // 数据流的基本接口

    @Override
    public final Iterator<P_OUT> iterator() {
        // 拆分器的迭代器
        return Spliterators.iterator(spliterator());
    }


    // Stream
    // 数据流

    // Stateless intermediate operations from Stream
    // 来自数据流的无状态的中间操作

    @Override
    public Stream<P_OUT> unordered() {
        if (!isOrdered()) {
            return this;
        }
        // 无状态的操作
        return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_ORDERED) {
            @Override
            Sink<P_OUT> opWrapSink(int flags, Sink<P_OUT> sink) {
                return sink;
            }
        };
    }

    // 谓词函数-Predicate
    // 过滤层

    @Override
    public final Stream<P_OUT> filter(Predicate<? super P_OUT> predicate) {
        Objects.requireNonNull(predicate);
        return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE,
                                     StreamOpFlag.NOT_SIZED) {
            @Override
            Sink<P_OUT> opWrapSink(int flags, Sink<P_OUT> sink) {
                return new Sink.ChainedReference<P_OUT, P_OUT>(sink) {
                    @Override
                    public void begin(long size) {
                        downstream.begin(-1);
                    }

                    @Override
                    public void accept(P_OUT u) {
                        if (predicate.test(u)) {
                            // 谓词函数为真，下游数据流接收
                            downstream.accept(u);
                        }
                    }
                };
            }
        };
    }

    // 类型映射函数-Function

    @Override
    @SuppressWarnings("unchecked")
    public final <R> Stream<R> map(Function<? super P_OUT, ? extends R> mapper) {
        Objects.requireNonNull(mapper);
        return new StatelessOp<P_OUT, R>(this, StreamShape.REFERENCE,
                                     StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<P_OUT> opWrapSink(int flags, Sink<R> sink) {
                return new Sink.ChainedReference<P_OUT, R>(sink) {
                    @Override
                    public void accept(P_OUT u) {
                        // 下游数据流接收类型映射函数的结果
                        downstream.accept(mapper.apply(u));
                    }
                };
            }
        };
    }

    @Override
    public final IntStream mapToInt(ToIntFunction<? super P_OUT> mapper) {
        Objects.requireNonNull(mapper);
        return new IntPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE,
                                              StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<P_OUT> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedReference<P_OUT, Integer>(sink) {
                    @Override
                    public void accept(P_OUT u) {
                        downstream.accept(mapper.applyAsInt(u));
                    }
                };
            }
        };
    }

    @Override
    public final LongStream mapToLong(ToLongFunction<? super P_OUT> mapper) {
        Objects.requireNonNull(mapper);
        return new LongPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE,
                                      StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<P_OUT> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedReference<P_OUT, Long>(sink) {
                    @Override
                    public void accept(P_OUT u) {
                        downstream.accept(mapper.applyAsLong(u));
                    }
                };
            }
        };
    }

    @Override
    public final DoubleStream mapToDouble(ToDoubleFunction<? super P_OUT> mapper) {
        Objects.requireNonNull(mapper);
        return new DoublePipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE,
                                        StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<P_OUT> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedReference<P_OUT, Double>(sink) {
                    @Override
                    public void accept(P_OUT u) {
                        downstream.accept(mapper.applyAsDouble(u));
                    }
                };
            }
        };
    }

    // 降维类型映射

    @Override
    public final <R> Stream<R> flatMap(Function<? super P_OUT, ? extends Stream<? extends R>> mapper) {
        Objects.requireNonNull(mapper);
        // We can do better than this, by polling cancellationRequested when stream is infinite
        return new StatelessOp<P_OUT, R>(this, StreamShape.REFERENCE,
                                     StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            @Override
            Sink<P_OUT> opWrapSink(int flags, Sink<R> sink) {
                return new Sink.ChainedReference<P_OUT, R>(sink) {
                    @Override
                    public void begin(long size) {
                        downstream.begin(-1);
                    }

                    @Override
                    public void accept(P_OUT u) {
                        try (Stream<? extends R> result = mapper.apply(u)) {
                            // We can do better that this too; optimize for depth=0 case and just grab spliterator and forEach it
                            if (result != null) {
                                result.sequential().forEach(downstream);
                            }
                        }
                    }
                };
            }
        };
    }

    @Override
    public final IntStream flatMapToInt(Function<? super P_OUT, ? extends IntStream> mapper) {
        Objects.requireNonNull(mapper);
        // We can do better than this, by polling cancellationRequested when stream is infinite
        return new IntPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE,
                                              StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            @Override
            Sink<P_OUT> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedReference<P_OUT, Integer>(sink) {
                    IntConsumer downstreamAsInt = downstream::accept;
                    @Override
                    public void begin(long size) {
                        downstream.begin(-1);
                    }

                    @Override
                    public void accept(P_OUT u) {
                        try (IntStream result = mapper.apply(u)) {
                            // We can do better that this too; optimize for depth=0 case and just grab spliterator and forEach it
                            if (result != null) {
                                result.sequential().forEach(downstreamAsInt);
                            }
                        }
                    }
                };
            }
        };
    }

    @Override
    public final DoubleStream flatMapToDouble(Function<? super P_OUT, ? extends DoubleStream> mapper) {
        Objects.requireNonNull(mapper);
        // We can do better than this, by polling cancellationRequested when stream is infinite
        return new DoublePipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE,
                                                     StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            @Override
            Sink<P_OUT> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedReference<P_OUT, Double>(sink) {
                    DoubleConsumer downstreamAsDouble = downstream::accept;
                    @Override
                    public void begin(long size) {
                        downstream.begin(-1);
                    }

                    @Override
                    public void accept(P_OUT u) {
                        try (DoubleStream result = mapper.apply(u)) {
                            // We can do better that this too; optimize for depth=0 case and just grab spliterator and forEach it
                            if (result != null) {
                                result.sequential().forEach(downstreamAsDouble);
                            }
                        }
                    }
                };
            }
        };
    }

    @Override
    public final LongStream flatMapToLong(Function<? super P_OUT, ? extends LongStream> mapper) {
        Objects.requireNonNull(mapper);
        // We can do better than this, by polling cancellationRequested when stream is infinite
        return new LongPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE,
                                                   StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            @Override
            Sink<P_OUT> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedReference<P_OUT, Long>(sink) {
                    LongConsumer downstreamAsLong = downstream::accept;
                    @Override
                    public void begin(long size) {
                        downstream.begin(-1);
                    }

                    @Override
                    public void accept(P_OUT u) {
                        try (LongStream result = mapper.apply(u)) {
                            // We can do better that this too; optimize for depth=0 case and just grab spliterator and forEach it
                            if (result != null) {
                                result.sequential().forEach(downstreamAsLong);
                            }
                        }
                    }
                };
            }
        };
    }

    @Override
    public final Stream<P_OUT> peek(Consumer<? super P_OUT> action) {
        Objects.requireNonNull(action);
        return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE,
                                     0) {
            @Override
            Sink<P_OUT> opWrapSink(int flags, Sink<P_OUT> sink) {
                return new Sink.ChainedReference<P_OUT, P_OUT>(sink) {
                    @Override
                    public void accept(P_OUT u) {
                        action.accept(u);
                        downstream.accept(u);
                    }
                };
            }
        };
    }

    // Stateful intermediate operations from Stream
    // 来自数据流的有状态的中间操作

    @Override
    public final Stream<P_OUT> distinct() {
        // 去重
        return DistinctOps.makeRef(this);
    }

    @Override
    public final Stream<P_OUT> sorted() {
        // 排序
        return SortedOps.makeRef(this);
    }

    @Override
    public final Stream<P_OUT> sorted(Comparator<? super P_OUT> comparator) {
        return SortedOps.makeRef(this, comparator);
    }

    @Override
    public final Stream<P_OUT> limit(long maxSize) {
        if (maxSize < 0) {
            throw new IllegalArgumentException(Long.toString(maxSize));
        }
        return SliceOps.makeRef(this, 0, maxSize);
    }

    @Override
    public final Stream<P_OUT> skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(Long.toString(n));
        }
        if (n == 0) {
            return this;
        } else {
            return SliceOps.makeRef(this, n, -1);
        }
    }

    // Terminal operations from Stream
    // 来自数据流的终结操作

    @Override
    public void forEach(Consumer<? super P_OUT> action) {
        evaluate(ForEachOps.makeRef(action, false));
    }

    @Override
    public void forEachOrdered(Consumer<? super P_OUT> action) {
        evaluate(ForEachOps.makeRef(action, true));
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <A> A[] toArray(IntFunction<A[]> generator) {
        // Since A has no relation to U (not possible to declare that A is an upper bound of U)
        // there will be no static type checking.
        // Therefore use a raw type and assume A == U rather than propagating the separation of A and U
        // throughout the code-base.
        // The runtime type of U is never checked for equality with the component type of the runtime type of A[].
        // Runtime checking will be performed when an element is stored in A[], thus if A is not a
        // super type of U an ArrayStoreException will be thrown.
        @SuppressWarnings("rawtypes")
        IntFunction rawGenerator = (IntFunction) generator;
        return (A[]) Nodes.flatten(evaluateToArrayNode(rawGenerator), rawGenerator)
                              .asArray(rawGenerator);
    }

    @Override
    public final Object[] toArray() {
        return toArray(Object[]::new);
    }

    @Override
    public final boolean anyMatch(Predicate<? super P_OUT> predicate) {
        return evaluate(MatchOps.makeRef(predicate, MatchOps.MatchKind.ANY));
    }

    @Override
    public final boolean allMatch(Predicate<? super P_OUT> predicate) {
        return evaluate(MatchOps.makeRef(predicate, MatchOps.MatchKind.ALL));
    }

    @Override
    public final boolean noneMatch(Predicate<? super P_OUT> predicate) {
        return evaluate(MatchOps.makeRef(predicate, MatchOps.MatchKind.NONE));
    }

    @Override
    public final Optional<P_OUT> findFirst() {
        return evaluate(FindOps.makeRef(true));
    }

    @Override
    public final Optional<P_OUT> findAny() {
        return evaluate(FindOps.makeRef(false));
    }

    // 归约操作

    @Override
    public final P_OUT reduce(final P_OUT identity, final BinaryOperator<P_OUT> accumulator) {
        return evaluate(ReduceOps.makeRef(identity, accumulator, accumulator));
    }

    @Override
    public final Optional<P_OUT> reduce(BinaryOperator<P_OUT> accumulator) {
        return evaluate(ReduceOps.makeRef(accumulator));
    }

    @Override
    public final <R> R reduce(R identity, BiFunction<R, ? super P_OUT, R> accumulator, BinaryOperator<R> combiner) {
        return evaluate(ReduceOps.makeRef(identity, accumulator, combiner));
    }

    // 收集器

    @Override
    @SuppressWarnings("unchecked")
    public final <R, A> R collect(Collector<? super P_OUT, A, R> collector) {
        A container;
        if (isParallel()
                && (collector.characteristics().contains(Collector.Characteristics.CONCURRENT))
                && (!isOrdered() || collector.characteristics().contains(Collector.Characteristics.UNORDERED))) {
            container = collector.supplier().get();
            BiConsumer<A, ? super P_OUT> accumulator = collector.accumulator();
            forEach(u -> accumulator.accept(container, u));
        }
        else {
            container = evaluate(ReduceOps.makeRef(collector));
        }
        return collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)
               ? (R) container
               : collector.finisher().apply(container);
    }

    @Override
    public final <R> R collect(Supplier<R> supplier,
                               BiConsumer<R, ? super P_OUT> accumulator,
                               BiConsumer<R, R> combiner) {
        return evaluate(ReduceOps.makeRef(supplier, accumulator, combiner));
    }

    @Override
    public final Optional<P_OUT> max(Comparator<? super P_OUT> comparator) {
        return reduce(BinaryOperator.maxBy(comparator));
    }

    @Override
    public final Optional<P_OUT> min(Comparator<? super P_OUT> comparator) {
        return reduce(BinaryOperator.minBy(comparator));

    }

    @Override
    public final long count() {
        return mapToLong(e -> 1L).sum();
    }


    //

    /**
     * Source stage of a ReferencePipeline.
     * 引用流水线的数据源阶段。
     *
     * @param <E_IN> type of elements in the upstream source
     *              上游数据源中元素的类型
     * @param <E_OUT> type of elements in produced by this stage
     *               在这一阶段产生的元素类型
     * @since 1.8
     */
    static class Head<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
        /**
         * Constructor for the source stage of a Stream.
         *
         * @param source {@code Supplier<Spliterator>} describing the stream
         *               source
         * @param sourceFlags the source flags for the stream source, described
         *                    in {@link StreamOpFlag}
         */
        Head(Supplier<? extends Spliterator<?>> source,
             int sourceFlags, boolean parallel) {
            super(source, sourceFlags, parallel);
        }

        /**
         * Constructor for the source stage of a Stream.
         *
         * @param source {@code Spliterator} describing the stream source
         * @param sourceFlags the source flags for the stream source, described
         *                    in {@link StreamOpFlag}
         */
        Head(Spliterator<?> source,
             int sourceFlags, boolean parallel) {
            super(source, sourceFlags, parallel);
        }

        @Override
        final boolean opIsStateful() {
            throw new UnsupportedOperationException();
        }

        @Override
        final Sink<E_IN> opWrapSink(int flags, Sink<E_OUT> sink) {
            throw new UnsupportedOperationException();
        }

        // Optimized sequential terminal operations for the head of the pipeline

        @Override
        public void forEach(Consumer<? super E_OUT> action) {
            if (!isParallel()) {
                sourceStageSpliterator().forEachRemaining(action);
            }
            else {
                super.forEach(action);
            }
        }

        @Override
        public void forEachOrdered(Consumer<? super E_OUT> action) {
            if (!isParallel()) {
                sourceStageSpliterator().forEachRemaining(action);
            }
            else {
                super.forEachOrdered(action);
            }
        }
    }

    /**
     * Base class for a stateless intermediate stage of a Stream.
     * 数据流的无状态中间阶段的基类。
     *
     * @param <E_IN> type of elements in the upstream source
     *              上游数据源中元素的类型
     * @param <E_OUT> type of elements in produced by this stage
     *               在这一阶段产生的元素类型
     * @since 1.8
     */
    abstract static class StatelessOp<E_IN, E_OUT>
            extends ReferencePipeline<E_IN, E_OUT> {
        /**
         * Construct a new Stream by appending a stateless intermediate
         * operation to an existing stream.
         *
         * @param upstream The upstream pipeline stage
         * @param inputShape The stream shape for the upstream pipeline stage
         * @param opFlags Operation flags for the new stage
         */
        StatelessOp(AbstractPipeline<?, E_IN, ?> upstream,
                    StreamShape inputShape,
                    int opFlags) {
            super(upstream, opFlags);
            assert upstream.getOutputShape() == inputShape;
        }

        @Override
        final boolean opIsStateful() {
            return false;
        }
    }

    /**
     * Base class for a stateful intermediate stage of a Stream.
     * 数据流的有状态中间阶段的基类。
     *
     * @param <E_IN> type of elements in the upstream source
     *              上游数据源中元素的类型
     * @param <E_OUT> type of elements in produced by this stage
     *               在这一阶段产生的元素类型
     * @since 1.8
     */
    abstract static class StatefulOp<E_IN, E_OUT>
            extends ReferencePipeline<E_IN, E_OUT> {
        /**
         * Construct a new Stream by appending a stateful intermediate operation
         * to an existing stream.
         * @param upstream The upstream pipeline stage
         * @param inputShape The stream shape for the upstream pipeline stage
         * @param opFlags Operation flags for the new stage
         */
        StatefulOp(AbstractPipeline<?, E_IN, ?> upstream,
                   StreamShape inputShape,
                   int opFlags) {
            super(upstream, opFlags);
            assert upstream.getOutputShape() == inputShape;
        }

        @Override
        final boolean opIsStateful() {
            return true;
        }

        @Override
        abstract <P_IN> Node<E_OUT> opEvaluateParallel(PipelineHelper<E_OUT> helper,
                                                       Spliterator<P_IN> spliterator,
                                                       IntFunction<E_OUT[]> generator);
    }
}
