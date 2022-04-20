
package java.util.stream;

import java.util.Iterator;
import java.util.Spliterator;

/**
 * Base interface for streams, which are sequences of elements supporting
 * sequential and parallel aggregate operations.  The following example
 * illustrates an aggregate operation using the stream types {@link Stream}
 * and {@link IntStream}, computing the sum of the weights of the red widgets:
 *
 * <pre>{@code
 *     int sum = widgets.stream()
 *                      .filter(w -> w.getColor() == RED)
 *                      .mapToInt(w -> w.getWeight())
 *                      .sum();
 * }</pre>
 * 注意：数据流的基本接口，它是支持顺序和并行聚合操作的元素序列。
 * 下面的例子演示了一个使用数据流类型Stream和IntStream的聚合操作，计算红色小部件的权重之和：
 *
 * <p>See the class documentation for {@link Stream} and the package documentation
 * for <a href="package-summary.html">java.util.stream</a> for additional
 * specification of streams, stream operations, stream pipelines, and
 * parallelism, which governs the behavior of all stream types.
 * 请参阅Stream的类文档和java.util.stream的包文档，了解数据流、数据流操作、数据流管道和并行性的其他规范，
 * 这些规范管理着所有数据流类型的行为。
 *
 * @param <T> the type of the stream elements 数据流元素的类型
 * @param <S> the type of of the stream implementing {@code BaseStream} 数据流的类型
 * @since 1.8
 * @see Stream
 * @see IntStream
 * @see LongStream
 * @see DoubleStream
 * @see <a href="package-summary.html">java.util.stream</a>
 */
public interface BaseStream<T, S extends BaseStream<T, S>>
        extends AutoCloseable {

    // 元素访问
    // 迭代器，拆分器

    /**
     * Returns an iterator for the elements of this stream.
     * 返回这个数据流元素的迭代器。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     * 这是一个终结操作。
     *
     * @return the element iterator for this stream
     */
    Iterator<T> iterator();

    /**
     * Returns a spliterator for the elements of this stream.
     * 返回这个数据流元素的拆分器。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     * 这是一个终结操作。
     *
     * @return the element spliterator for this stream
     */
    Spliterator<T> spliterator();

    /**
     * Returns whether this stream, if a terminal operation were to be executed,
     * would execute in parallel.  Calling this method after invoking an
     * terminal stream operation method may yield unpredictable results.
     * 如果要执行一个终结操作，则返回这个数据流是否将并行执行。
     * 在调用终结数据流操作方法后，调用这个方法可能会产生不可预知的结果。
     *
     * @return {@code true} if this stream would execute in parallel if executed
     */
    boolean isParallel();

    // 数据流并行性
    // 顺序数据流、并行数据流、无序数据流

    /**
     * Returns an equivalent stream that is sequential.  May return
     * itself, either because the stream was already sequential, or because
     * the underlying stream state was modified to be sequential.
     * 返回一个等价的顺序数据流。
     * 可能返回本身，可能是因为数据流已经是顺序的，也可能是因为数据流的底层状态被修改为顺序的。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     * 这是一个中间操作。
     *
     * @return a sequential stream 一个顺序数据流
     */
    S sequential();

    /**
     * Returns an equivalent stream that is parallel.  May return
     * itself, either because the stream was already parallel, or because
     * the underlying stream state was modified to be parallel.
     * 返回一个等价的并行数据流。
     * 可能返回本身，可能是因为数据流已经是并行的，也可能因为底层数据流状态被修改为并行的。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     * 这是一个中间操作。
     *
     * @return a parallel stream 一个并行数据流
     */
    S parallel();

    /**
     * Returns an equivalent stream that is
     * <a href="package-summary.html#Ordering">unordered</a>.  May return
     * itself, either because the stream was already unordered, or because
     * the underlying stream state was modified to be unordered.
     * 返回一个等价的无序数据流。
     * 可能返回本身，可能是因为数据流已经是无序的，也可能是因为底层数据流状态被修改为无序的。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     * 这是一个中间操作。
     *
     * @return an unordered stream 一个无序数据流
     */
    S unordered();

    // 关闭释放数据流资源

    /**
     * Returns an equivalent stream with an additional close handler.  Close
     * handlers are run when the {@link #close()} method
     * is called on the stream, and are executed in the order they were
     * added.  All close handlers are run, even if earlier close handlers throw
     * exceptions.  If any close handler throws an exception, the first
     * exception thrown will be relayed to the caller of {@code close()}, with
     * any remaining exceptions added to that exception as suppressed exceptions
     * (unless one of the remaining exceptions is the same exception as the
     * first exception, since an exception cannot suppress itself.)  May
     * return itself.
     * 返回具有附加关闭处理程序的等价数据流。
     * 在数据流上调用关闭方法时运行关闭处理程序，并按照添加它们的顺序执行。
     * 运行所有关闭处理程序，即使较早的关闭处理程序抛出异常。
     * 如果任何关闭处理程序抛出异常，第一个抛出的异常将被转发给关闭方法的调用者，
     * 而任何剩余的异常将作为被抑制的异常添加到这个异常中，可能会返回本身。
     * (除非剩余的异常中有一个与第一个异常相同的异常，因为异常不能抑制自己。)
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     * 这是一个中间操作。
     *
     * @param closeHandler A task to execute when the stream is closed 当数据流关闭时要执行的任务
     * @return a stream with a handler that is run if the stream is closed
     */
    S onClose(Runnable closeHandler);

    /**
     * Closes this stream, causing all close handlers for this stream pipeline
     * to be called.
     * 关闭这个数据流，从而调用这个数据流管道的所有关闭处理程序。
     *
     * @see AutoCloseable#close()
     */
    @Override
    void close();
}
