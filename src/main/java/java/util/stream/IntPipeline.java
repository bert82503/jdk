
package java.util.stream;

import java.util.IntSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;

/**
 * Abstract base class for an intermediate pipeline stage or pipeline source
 * stage implementing whose elements are of type {@code int}.
 * 元素为整数类型的中间流水线管道阶段或管道源阶段实现的抽象基类。
 *
 * @param <E_IN> type of elements in the upstream source
 *              上游数据源中元素的类型
 * @since 1.8
 */
abstract class IntPipeline<E_IN>
        extends AbstractPipeline<E_IN, Integer, IntStream>
        implements IntStream {

    /**
     * Constructor for the head of a stream pipeline.
     * 数据流管道的头节点的构造函数。
     *
     * @param source {@code Supplier<Spliterator>} describing the stream source
     *                                            描述数据流源的拆分器提供者
     * @param sourceFlags The source flags for the stream source, described in
     *        {@link StreamOpFlag}
     * @param parallel {@code true} if the pipeline is parallel
     */
    IntPipeline(Supplier<? extends Spliterator<Integer>> source,
                int sourceFlags, boolean parallel) {
        super(source, sourceFlags, parallel);
    }

    /**
     * Constructor for the head of a stream pipeline.
     * 数据流管道的头节点的构造函数。
     *
     * @param source {@code Spliterator} describing the stream source
     *                                  描述数据流源的拆分器
     * @param sourceFlags The source flags for the stream source, described in
     *        {@link StreamOpFlag}
     * @param parallel {@code true} if the pipeline is parallel
     */
    IntPipeline(Spliterator<Integer> source,
                int sourceFlags, boolean parallel) {
        super(source, sourceFlags, parallel);
    }

    /**
     * Constructor for appending an intermediate operation onto an existing
     * pipeline.
     * 将中间操作附加到现有流水线管道的构造函数。
     *
     * @param upstream the upstream element source
     *                 上游元素数据源
     * @param opFlags the operation flags for the new operation
     */
    IntPipeline(AbstractPipeline<?, E_IN, ?> upstream, int opFlags) {
        super(upstream, opFlags);
    }

    // 类型转换适配

    /**
     * Adapt a {@code Sink<Integer> to an {@code IntConsumer}, ideally simply
     * by casting.
     * 将接收结果的水槽调整为整数消费者，理想情况下简单地通过类型转换。
     */
    private static IntConsumer adapt(Sink<Integer> sink) {
        if (sink instanceof IntConsumer) {
            return (IntConsumer) sink;
        }
        else {
            if (Tripwire.ENABLED) {
                Tripwire.trip(AbstractPipeline.class,
                              "using IntStream.adapt(Sink<Integer> s)");
            }
            return sink::accept;
        }
    }

    /**
     * Adapt a {@code Spliterator<Integer>} to a {@code Spliterator.OfInt}.
     * 将整数拆分器适配为整数基本类型的拆分器。
     *
     * @implNote
     * The implementation attempts to cast to a Spliterator.OfInt, and throws an
     * exception if this cast is not possible.
     * 实现尝试将其转换为整数基本类型的拆分器，如果不能进行这种类型转换，则抛出异常。
     */
    private static Spliterator.OfInt adapt(Spliterator<Integer> s) {
        if (s instanceof Spliterator.OfInt) {
            return (Spliterator.OfInt) s;
        }
        else {
            if (Tripwire.ENABLED) {
                Tripwire.trip(AbstractPipeline.class,
                              "using IntStream.adapt(Spliterator<Integer> s)");
            }
            throw new UnsupportedOperationException("IntStream.adapt(Spliterator<Integer> s)");
        }
    }


    // Shape-specific methods

    @Override
    final StreamShape getOutputShape() {
        return StreamShape.INT_VALUE;
    }

    @Override
    final <P_IN> Node<Integer> evaluateToNode(PipelineHelper<Integer> helper,
                                              Spliterator<P_IN> spliterator,
                                              boolean flattenTree,
                                              IntFunction<Integer[]> generator) {
        // 收集整数节点列表
        return Nodes.collectInt(helper, spliterator, flattenTree);
    }

    @Override
    final <P_IN> Spliterator<Integer> wrap(PipelineHelper<Integer> ph,
                                           Supplier<Spliterator<P_IN>> supplier,
                                           boolean isParallel) {
        return new StreamSpliterators.IntWrappingSpliterator<>(ph, supplier, isParallel);
    }

    @Override
    @SuppressWarnings("unchecked")
    final Spliterator.OfInt lazySpliterator(Supplier<? extends Spliterator<Integer>> supplier) {
        return new StreamSpliterators.DelegatingSpliterator.OfInt((Supplier<Spliterator.OfInt>) supplier);
    }

    @Override
    final void forEachWithCancel(Spliterator<Integer> spliterator, Sink<Integer> sink) {
        Spliterator.OfInt spl = adapt(spliterator);
        IntConsumer adaptedSink = adapt(sink);
        do { } while (!sink.cancellationRequested() && spl.tryAdvance(adaptedSink));
    }

    @Override
    final Node.Builder<Integer> makeNodeBuilder(long exactSizeIfKnown,
                                                IntFunction<Integer[]> generator) {
        return Nodes.intBuilder(exactSizeIfKnown);
    }


    // IntStream
    // 整数基本类型的数据流

    @Override
    public final PrimitiveIterator.OfInt iterator() {
        // 拆分器的迭代器
        return Spliterators.iterator(spliterator());
    }

    @Override
    public final Spliterator.OfInt spliterator() {
        // 拆分器适配
        return adapt(super.spliterator());
    }

    // Stateless intermediate ops from IntStream
    // 整数基本类型的数据流的无状态的中间操作

    @Override
    public final LongStream asLongStream() {
        // 长整数流水线
        return new LongPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                                     StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<Integer> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedInt<Long>(sink) {
                    @Override
                    public void accept(int t) {
                        downstream.accept((long) t);
                    }
                };
            }
        };
    }

    @Override
    public final DoubleStream asDoubleStream() {
        return new DoublePipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                                       StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<Integer> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedInt<Double>(sink) {
                    @Override
                    public void accept(int t) {
                        downstream.accept((double) t);
                    }
                };
            }
        };
    }

    @Override
    public final Stream<Integer> boxed() {
        // 整数包装类的数据流
        // Integer::valueOf
        return mapToObj(Integer::valueOf);
    }

    // 类型转换映射
    // 函数->数据流

    @Override
    public final IntStream map(IntUnaryOperator mapper) {
        Objects.requireNonNull(mapper);
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                        StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedInt<Integer>(sink) {
                    @Override
                    public void accept(int t) {
                        // 类型映射-mapper.applyAsInt(t)
                        downstream.accept(mapper.applyAsInt(t));
                    }
                };
            }
        };
    }

    @Override
    public final <U> Stream<U> mapToObj(IntFunction<? extends U> mapper) {
        Objects.requireNonNull(mapper);
        // 对象引用的流水线的无状态操作
        return new ReferencePipeline.StatelessOp<Integer, U>(this, StreamShape.INT_VALUE,
                                                             StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<Integer> opWrapSink(int flags, Sink<U> sink) {
                return new Sink.ChainedInt<U>(sink) {
                    @Override
                    public void accept(int t) {
                        // 类型映射-mapper.apply(t)
                        downstream.accept(mapper.apply(t));
                    }
                };
            }
        };
    }

    @Override
    public final LongStream mapToLong(IntToLongFunction mapper) {
        Objects.requireNonNull(mapper);
        return new LongPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                                     StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<Integer> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedInt<Long>(sink) {
                    @Override
                    public void accept(int t) {
                        downstream.accept(mapper.applyAsLong(t));
                    }
                };
            }
        };
    }

    @Override
    public final DoubleStream mapToDouble(IntToDoubleFunction mapper) {
        Objects.requireNonNull(mapper);
        return new DoublePipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                                       StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<Integer> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedInt<Double>(sink) {
                    @Override
                    public void accept(int t) {
                        downstream.accept(mapper.applyAsDouble(t));
                    }
                };
            }
        };
    }

    @Override
    public final IntStream flatMap(IntFunction<? extends IntStream> mapper) {
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                        StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            @Override
            Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedInt<Integer>(sink) {
                    @Override
                    public void begin(long size) {
                        downstream.begin(-1);
                    }

                    @Override
                    public void accept(int t) {
                        try (IntStream result = mapper.apply(t)) {
                            // We can do better that this too; optimize for depth=0 case and just grab spliterator and forEach it
                            if (result != null) {
                                result.sequential().forEach(i -> downstream.accept(i));
                            }
                        }
                    }
                };
            }
        };
    }

    @Override
    public IntStream unordered() {
        if (!isOrdered()) {
            return this;
        }
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_ORDERED) {
            @Override
            Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return sink;
            }
        };
    }

    @Override
    public final IntStream filter(IntPredicate predicate) {
        Objects.requireNonNull(predicate);
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                        StreamOpFlag.NOT_SIZED) {
            @Override
            Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedInt<Integer>(sink) {
                    @Override
                    public void begin(long size) {
                        downstream.begin(-1);
                    }

                    @Override
                    public void accept(int t) {
                        // 谓词函数-predicate.test(t)
                        if (predicate.test(t)) {
                            downstream.accept(t);
                        }
                    }
                };
            }
        };
    }

    @Override
    public final IntStream peek(IntConsumer action) {
        Objects.requireNonNull(action);
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                        0) {
            @Override
            Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedInt<Integer>(sink) {
                    @Override
                    public void accept(int t) {
                        // 整数基本类型的消费者
                        action.accept(t);
                        downstream.accept(t);
                    }
                };
            }
        };
    }

    // Stateful intermediate ops from IntStream
    // 整数基本类型的数据流的有状态的中间操作

    @Override
    public final IntStream limit(long maxSize) {
        if (maxSize < 0) {
            throw new IllegalArgumentException(Long.toString(maxSize));
        }
        return SliceOps.makeInt(this, 0, maxSize);
    }

    @Override
    public final IntStream skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(Long.toString(n));
        }
        if (n == 0) {
            return this;
        } else {
            return SliceOps.makeInt(this, n, -1);
        }
    }

    @Override
    public final IntStream sorted() {
        return SortedOps.makeInt(this);
    }

    @Override
    public final IntStream distinct() {
        // While functional and quick to implement, this approach is not very efficient.
        // An efficient version requires an int-specific map/set implementation.
        return boxed().distinct().mapToInt(i -> i);
    }

    // Terminal ops from IntStream
    // 整数基本类型的数据流的终结操作

    @Override
    public void forEach(IntConsumer action) {
        evaluate(ForEachOps.makeInt(action, false));
    }

    @Override
    public void forEachOrdered(IntConsumer action) {
        evaluate(ForEachOps.makeInt(action, true));
    }

    @Override
    public final int sum() {
        // 归约求和
        return reduce(0, Integer::sum);
    }

    @Override
    public final OptionalInt min() {
        return reduce(Math::min);
    }

    @Override
    public final OptionalInt max() {
        return reduce(Math::max);
    }

    @Override
    public final long count() {
        return mapToLong(e -> 1L).sum();
    }

    @Override
    public final OptionalDouble average() {
        long[] avg = collect(() -> new long[2],
                             (ll, i) -> {
                                 ll[0]++;
                                 ll[1] += i;
                             },
                             (ll, rr) -> {
                                 ll[0] += rr[0];
                                 ll[1] += rr[1];
                             });
        return avg[0] > 0
               ? OptionalDouble.of((double) avg[1] / avg[0])
               : OptionalDouble.empty();
    }

    @Override
    public final IntSummaryStatistics summaryStatistics() {
        // 汇总数据
        return collect(IntSummaryStatistics::new, IntSummaryStatistics::accept,
                       IntSummaryStatistics::combine);
    }

    // 归约操作

    @Override
    public final int reduce(int identity, IntBinaryOperator op) {
        return evaluate(ReduceOps.makeInt(identity, op));
    }

    @Override
    public final OptionalInt reduce(IntBinaryOperator op) {
        return evaluate(ReduceOps.makeInt(op));
    }

    // 结果收集器

    @Override
    public final <R> R collect(Supplier<R> supplier,
                               ObjIntConsumer<R> accumulator,
                               BiConsumer<R, R> combiner) {
        BinaryOperator<R> operator = (left, right) -> {
            // 接收左右子树的结果
            combiner.accept(left, right);
            return left;
        };
        return evaluate(ReduceOps.makeInt(supplier, accumulator, operator));
    }

    @Override
    public final boolean anyMatch(IntPredicate predicate) {
        return evaluate(MatchOps.makeInt(predicate, MatchOps.MatchKind.ANY));
    }

    @Override
    public final boolean allMatch(IntPredicate predicate) {
        return evaluate(MatchOps.makeInt(predicate, MatchOps.MatchKind.ALL));
    }

    @Override
    public final boolean noneMatch(IntPredicate predicate) {
        return evaluate(MatchOps.makeInt(predicate, MatchOps.MatchKind.NONE));
    }

    @Override
    public final OptionalInt findFirst() {
        return evaluate(FindOps.makeInt(true));
    }

    @Override
    public final OptionalInt findAny() {
        return evaluate(FindOps.makeInt(false));
    }

    @Override
    public final int[] toArray() {
        return Nodes.flattenInt((Node.OfInt) evaluateToArrayNode(Integer[]::new))
                        .asPrimitiveArray();
    }

    // 继承IntPipeline

    /**
     * Source stage of an IntStream.
     * 整数基本类型的数据流的数据源阶段。
     *
     * @param <E_IN> type of elements in the upstream source
     *              上游数据源中元素的类型
     * @since 1.8
     */
    static class Head<E_IN> extends IntPipeline<E_IN> {
        /**
         * Constructor for the source stage of an IntStream.
         * 整数基本类型的数据流的源阶段的构造函数。
         *
         * @param source {@code Supplier<Spliterator>} describing the stream
         *               source
         *                                            描述数据流源的拆分器的提供者
         * @param sourceFlags the source flags for the stream source, described
         *                    in {@link StreamOpFlag}
         * @param parallel {@code true} if the pipeline is parallel
         */
        Head(Supplier<? extends Spliterator<Integer>> source,
             int sourceFlags, boolean parallel) {
            super(source, sourceFlags, parallel);
        }

        /**
         * Constructor for the source stage of an IntStream.
         * 整数基本类型的数据流的源阶段的构造函数。
         *
         * @param source {@code Spliterator} describing the stream source
         *                                  描述数据流源的拆分器
         * @param sourceFlags the source flags for the stream source, described
         *                    in {@link StreamOpFlag}
         * @param parallel {@code true} if the pipeline is parallel
         */
        Head(Spliterator<Integer> source,
             int sourceFlags, boolean parallel) {
            super(source, sourceFlags, parallel);
        }

        @Override
        final boolean opIsStateful() {
            throw new UnsupportedOperationException();
        }

        @Override
        final Sink<E_IN> opWrapSink(int flags, Sink<Integer> sink) {
            throw new UnsupportedOperationException();
        }

        // Optimized sequential terminal operations for the head of the pipeline

        @Override
        public void forEach(IntConsumer action) {
            if (!isParallel()) {
                adapt(sourceStageSpliterator()).forEachRemaining(action);
            }
            else {
                super.forEach(action);
            }
        }

        @Override
        public void forEachOrdered(IntConsumer action) {
            if (!isParallel()) {
                adapt(sourceStageSpliterator()).forEachRemaining(action);
            }
            else {
                super.forEachOrdered(action);
            }
        }
    }

    /**
     * Base class for a stateless intermediate stage of an IntStream.
     * 整数基本类型的数据流的无状态的中间阶段的基类。
     *
     * @param <E_IN> type of elements in the upstream source
     *              上游数据源中元素的类型
     * @since 1.8
     */
    abstract static class StatelessOp<E_IN> extends IntPipeline<E_IN> {
        /**
         * Construct a new IntStream by appending a stateless intermediate
         * operation to an existing stream.
         * 通过添加一个无状态的中间操作到一个现有的数据流来构造一个新的整数数据流。
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
     * Base class for a stateful intermediate stage of an IntStream.
     * 整数数据流的有状态的中间阶段的基类。
     *
     * @param <E_IN> type of elements in the upstream source
     *              上游数据源中元素的类型
     * @since 1.8
     */
    abstract static class StatefulOp<E_IN> extends IntPipeline<E_IN> {
        /**
         * Construct a new IntStream by appending a stateful intermediate
         * operation to an existing stream.
         * 通过向现有数据流追加有状态的中间操作来构造一个新的整数数据流。
         *
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
        abstract <P_IN> Node<Integer> opEvaluateParallel(PipelineHelper<Integer> helper,
                                                         Spliterator<P_IN> spliterator,
                                                         IntFunction<Integer[]> generator);
    }
}
