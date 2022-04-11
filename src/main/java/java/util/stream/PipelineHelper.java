
package java.util.stream;

import java.util.Spliterator;
import java.util.function.IntFunction;

/**
 * Helper class for executing <a href="package-summary.html#StreamOps">
 * stream pipelines</a>, capturing all of the information about a stream
 * pipeline (output shape, intermediate operations, stream flags, parallelism,
 * etc) in one place.
 * 用于执行数据流管道的辅助类，在一个地方捕获关于数据流管道的所有信息(输出形状、中间操作、流标志、并行度等)。
 *
 * <p>
 * A {@code PipelineHelper} describes the initial segment of a stream pipeline,
 * including its source, intermediate operations, and may additionally
 * incorporate information about the terminal (or stateful) operation which
 * follows the last intermediate operation described by this
 * {@code PipelineHelper}. The {@code PipelineHelper} is passed to the
 * {@link TerminalOp#evaluateParallel(PipelineHelper, java.util.Spliterator)},
 * {@link TerminalOp#evaluateSequential(PipelineHelper, java.util.Spliterator)},
 * and {@link AbstractPipeline#opEvaluateParallel(PipelineHelper, java.util.Spliterator,
 * java.util.function.IntFunction)}, methods, which can use the
 * {@code PipelineHelper} to access information about the pipeline such as
 * head shape, stream flags, and size, and use the helper methods
 * such as {@link #wrapAndCopyInto(Sink, Spliterator)},
 * {@link #copyInto(Sink, Spliterator)}, and {@link #wrapSink(Sink)} to execute
 * pipeline operations.
 *
 * @param <P_OUT> type of output elements from the pipeline
 *               数据流管道输出元素的类型
 * @since 1.8
 */
abstract class PipelineHelper<P_OUT> {

    /**
     * Gets the stream shape for the source of the pipeline segment.
     * 获取流水线管道阶段的数据源的数据流形状。
     *
     * @return the stream shape for the source of the pipeline segment.
     */
    abstract StreamShape getSourceShape();

    /**
     * Gets the combined stream and operation flags for the output of the described
     * pipeline.  This will incorporate stream flags from the stream source, all
     * the intermediate operations and the terminal operation.
     * 获取所述数据流管道输出的组合数据流和操作标志。
     * 这将包含来自数据流源的流标记、所有中间操作和终结操作。
     *
     * @return the combined stream and operation flags
     * @see StreamOpFlag
     */
    abstract int getStreamAndOpFlags();

    /**
     * Returns the exact output size of the portion of the output resulting from
     * applying the pipeline stages described by this {@code PipelineHelper} to
     * the the portion of the input described by the provided
     * {@code Spliterator}, if known.  If not known or known infinite, will
     * return {@code -1}.
     * 如果已知，则返回将这个流水线辅助者所描述的数据流管道阶段应用到所提供的
     * 拆分器所描述的输入部分所产生的输出部分的确切输出大小。
     * 如果未知或已知无穷大，将返回-1。
     *
     * @apiNote
     * The exact output size is known if the {@code Spliterator} has the
     * {@code SIZED} characteristic, and the operation flags
     * {@link StreamOpFlag#SIZED} is known on the combined stream and operation
     * flags.
     *
     * @param spliterator the spliterator describing the relevant portion of the
     *        source data
     *                    描述源数据相关部分的拆分器
     * @return the exact size if known, or -1 if infinite or unknown
     */
    abstract<P_IN> long exactOutputSizeIfKnown(Spliterator<P_IN> spliterator);

    /**
     * Applies the pipeline stages described by this {@code PipelineHelper} to
     * the provided {@code Spliterator} and send the results to the provided
     * {@code Sink}.
     * 将这个数据流管道辅助者描述的数据流管道阶段应用到所提供的拆分器，并将结果发送到所提供的接收结果的水槽。
     *
     * @implSpec
     * The implementation behaves as if:
     * <pre>{@code
     *     intoWrapped(wrapSink(sink), spliterator);
     * }</pre>
     *
     * @param sink the {@code Sink} to receive the results
     *             接收结果的水槽
     * @param spliterator the spliterator describing the source input to process
     *                    描述要处理的数据源输入的拆分器
     */
    abstract<P_IN, S extends Sink<P_OUT>> S wrapAndCopyInto(S sink, Spliterator<P_IN> spliterator);

    /**
     * Pushes elements obtained from the {@code Spliterator} into the provided
     * {@code Sink}.  If the stream pipeline is known to have short-circuiting
     * stages in it (see {@link StreamOpFlag#SHORT_CIRCUIT}), the
     * {@link Sink#cancellationRequested()} is checked after each
     * element, stopping if cancellation is requested.
     * 将从拆分器获得的元素推送到提供的接收结果的水槽中。
     * 如果已知数据流管道中有短路阶段，则在每个元素之后检查，如果请求取消则停止。
     *
     * @implSpec
     * This method conforms to the {@code Sink} protocol of calling
     * {@code Sink.begin} before pushing elements, via {@code Sink.accept}, and
     * calling {@code Sink.end} after all elements have been pushed.
     *
     * @param wrappedSink the destination {@code Sink}
     * @param spliterator the source {@code Spliterator}
     */
    abstract<P_IN> void copyInto(Sink<P_IN> wrappedSink, Spliterator<P_IN> spliterator);

    /**
     * Pushes elements obtained from the {@code Spliterator} into the provided
     * {@code Sink}, checking {@link Sink#cancellationRequested()} after each
     * element, and stopping if cancellation is requested.
     * 将从拆分器获得的元素推送到所提供的接收结果的水槽中，在每个元素之后检查取消请求，
     * 并在请求取消时停止。
     *
     * @implSpec
     * This method conforms to the {@code Sink} protocol of calling
     * {@code Sink.begin} before pushing elements, via {@code Sink.accept}, and
     * calling {@code Sink.end} after all elements have been pushed or if
     * cancellation is requested.
     *
     * @param wrappedSink the destination {@code Sink}
     * @param spliterator the source {@code Spliterator}
     */
    abstract <P_IN> void copyIntoWithCancel(Sink<P_IN> wrappedSink, Spliterator<P_IN> spliterator);

    /**
     * Takes a {@code Sink} that accepts elements of the output type of the
     * {@code PipelineHelper}, and wrap it with a {@code Sink} that accepts
     * elements of the input type and implements all the intermediate operations
     * described by this {@code PipelineHelper}, delivering the result into the
     * provided {@code Sink}.
     * 接受流水线辅助者输出类型元素的接收结果的水槽，并将其包装为接收输入类型元素的接收结果的水槽，
     * 并实现流水线辅助者描述的所有中间操作，将结果交付到所提供的接收结果的水槽中。
     *
     * @param sink the {@code Sink} to receive the results
     * @return a {@code Sink} that implements the pipeline stages and sends
     *         results to the provided {@code Sink}
     */
    abstract<P_IN> Sink<P_IN> wrapSink(Sink<P_OUT> sink);

    /**
     * 包装拆分器。
     *
     * @param spliterator 拆分器
     * @param <P_IN>      流水线输入元素的类型
     * @return 流水线输出的拆分器
     */
    abstract<P_IN> Spliterator<P_OUT> wrapSpliterator(Spliterator<P_IN> spliterator);

    /**
     * Constructs a @{link Node.Builder} compatible with the output shape of
     * this {@code PipelineHelper}.
     * 构造一个节点构建者，与流水线辅助者的输出形状兼容。
     *
     * @param exactSizeIfKnown if >=0 then a builder will be created that has a
     *        fixed capacity of exactly sizeIfKnown elements; if < 0 then the
     *        builder has variable capacity.  A fixed capacity builder will fail
     *        if an element is added after the builder has reached capacity.
     * @param generator a factory function for array instances
     * @return a {@code Node.Builder} compatible with the output shape of this
     *         {@code PipelineHelper}
     */
    abstract Node.Builder<P_OUT> makeNodeBuilder(long exactSizeIfKnown,
                                                 IntFunction<P_OUT[]> generator);

    /**
     * Collects all output elements resulting from applying the pipeline stages
     * to the source {@code Spliterator} into a {@code Node}.
     * 将对数据源拆分器应用管道阶段所产生的所有输出元素收集到一个节点中。
     *
     * @implNote
     * If the pipeline has no intermediate operations and the source is backed
     * by a {@code Node} then that {@code Node} will be returned (or flattened
     * and then returned). This reduces copying for a pipeline consisting of a
     * stateful operation followed by a terminal operation that returns an
     * array, such as:
     * <pre>{@code
     *     stream.sorted().toArray();
     * }</pre>
     *
     * @param spliterator the source {@code Spliterator}
     * @param flatten if true and the pipeline is a parallel pipeline then the
     *        {@code Node} returned will contain no children, otherwise the
     *        {@code Node} may represent the root in a tree that reflects the
     *        shape of the computation tree.
     * @param generator a factory function for array instances
     * @return the {@code Node} containing all output elements
     */
    abstract<P_IN> Node<P_OUT> evaluate(Spliterator<P_IN> spliterator,
                                        boolean flatten,
                                        IntFunction<P_OUT[]> generator);
}
