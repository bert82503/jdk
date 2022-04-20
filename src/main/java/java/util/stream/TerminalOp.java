
package java.util.stream;

import java.util.Spliterator;

/**
 * An operation in a stream pipeline that takes a stream as input and produces
 * a result or side-effect.  A {@code TerminalOp} has an input type and stream
 * shape, and a result type.  A {@code TerminalOp} also has a set of
 * <em>operation flags</em> that describes how the operation processes elements
 * of the stream (such as short-circuiting or respecting encounter order; see
 * {@link StreamOpFlag}).
 * 数据流管道中的一种操作，它接受数据流作为输入并产生结果或副作用。
 * 注意：终结操作有输入类型、数据流形状和结果类型。
 * TerminalOp也有一组操作标志，用来描述操作如何处理数据流的元素。
 *
 * <p>A {@code TerminalOp} must provide a sequential and parallel implementation
 * of the operation relative to a given stream source and set of intermediate
 * operations.
 * TerminalOp必须提供相对于给定数据流源和中间操作集合的操作的顺序和并行实现。
 *
 * @param <E_IN> the type of input elements 输入元素的类型
 * @param <R>    the type of the result 结果的类型
 * @since 1.8
 */
interface TerminalOp<E_IN, R> {
    /**
     * Gets the shape of the input type of this operation.
     *
     * @implSpec The default returns {@code StreamShape.REFERENCE}.
     *
     * @return StreamShape of the input type of this operation
     */
    default StreamShape inputShape() { return StreamShape.REFERENCE; }

    /**
     * Gets the stream flags of the operation.  Terminal operations may set a
     * limited subset of the stream flags defined in {@link StreamOpFlag}, and
     * these flags are combined with the previously combined stream and
     * intermediate operation flags for the pipeline.
     *
     * @implSpec The default implementation returns zero.
     *
     * @return the stream flags for this operation
     * @see StreamOpFlag
     */
    default int getOpFlags() { return 0; }

    /**
     * Performs a parallel evaluation of the operation using the specified
     * {@code PipelineHelper}, which describes the upstream intermediate
     * operations.
     * 使用指定的数据流管道辅助者对操作执行并行计算，它描述了上游中间操作。
     *
     * @implSpec The default performs a sequential evaluation of the operation
     * using the specified {@code PipelineHelper}.
     *
     * @param helper the pipeline helper
     * @param spliterator the source spliterator
     * @return the result of the evaluation
     */
    default <P_IN> R evaluateParallel(PipelineHelper<E_IN> helper,
                                      Spliterator<P_IN> spliterator) {
        if (Tripwire.ENABLED) {
            Tripwire.trip(getClass(), "{0} triggering TerminalOp.evaluateParallel serial default");
        }
        return evaluateSequential(helper, spliterator);
    }

    /**
     * Performs a sequential evaluation of the operation using the specified
     * {@code PipelineHelper}, which describes the upstream intermediate
     * operations.
     * 使用指定的数据流管道辅助者对操作执行顺序求值，它描述了上游中间操作。
     *
     * @param helper the pipeline helper
     * @param spliterator the source spliterator
     * @return the result of the evaluation
     */
    <P_IN> R evaluateSequential(PipelineHelper<E_IN> helper,
                                Spliterator<P_IN> spliterator);
}
