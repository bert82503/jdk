
package java.util.stream;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;

/**
 * A sequence of elements supporting sequential and parallel aggregate
 * operations.  The following example illustrates an aggregate operation using
 * {@link Stream} and {@link IntStream}:
 *
 * <pre>{@code
 *     int sum = widgets.stream()
 *                      .filter(w -> w.getColor() == RED)
 *                      .mapToInt(w -> w.getWeight())
 *                      .sum();
 * }</pre>
 * 注意：数据流，支持顺序和并行聚合操作的元素序列。
 * 以下示例说明了使用Stream和IntStream的聚合操作：
 *
 * <p>In this example, {@code widgets} is a {@code Collection<Widget>}.  We create
 * a stream of {@code Widget} objects via {@link Collection#stream Collection.stream()},
 * filter it to produce a stream containing only the red widgets, and then
 * transform it into a stream of {@code int} values representing the weight of
 * each red widget. Then this stream is summed to produce a total weight.
 * 在这个示例中，小部件是一个集合。通过Collection.stream()创建一个小部件对象数据流，
 * 对其进行过滤以生成仅包含红色小部件的数据流，然后将其转换为表示每个红色小部件重量的整数值数据流，
 * 然后将这个数据流相加以产生总重量。
 *
 * <p>In addition to {@code Stream}, which is a stream of object references,
 * there are primitive specializations for {@link IntStream}, {@link LongStream},
 * and {@link DoubleStream}, all of which are referred to as "streams" and
 * conform to the characteristics and restrictions described here.
 * 除了作为对象引用数据流的Stream之外，还有IntStream、LongStream和DoubleStream的基本类型特殊实现，
 * 它们都被称为数据流，并且符合此处描述的特征和限制。
 *
 * <p>To perform a computation, stream
 * <a href="package-summary.html#StreamOps">operations</a> are composed into a
 * <em>stream pipeline</em>.  A stream pipeline consists of a source (which
 * might be an array, a collection, a generator function, an I/O channel,
 * etc), zero or more <em>intermediate operations</em> (which transform a
 * stream into another stream, such as {@link Stream#filter(Predicate)}), and a
 * <em>terminal operation</em> (which produces a result or side-effect, such
 * as {@link Stream#count()} or {@link Stream#forEach(Consumer)}).
 * Streams are lazy; computation on the source data is only performed when the
 * terminal operation is initiated, and source elements are consumed only
 * as needed.
 * 注意：为了执行计算，数据流操作被组合成数据流管道。
 * 数据流管道由数据源(可能是一个数组，一个集合，一个生成器函数，一个I/O通道等)，
 * 零个或多个中间操作(将数据流转换为另一个数据流，如谓词过滤器)，
 * 和一个终结操作(产生结果或副作用，如计数器或操作数消费者遍历器)组成。
 * 数据流是懒惰的，仅在终结操作发起时才对源数据进行计算，并且仅在需要时消费数据源元素。
 * 注意：数据流管道由数据源、零个或多个中间操作和一个终结操作组成。
 *
 * <p>Collections and streams, while bearing some superficial similarities,
 * have different goals.  Collections are primarily concerned with the efficient
 * management of, and access to, their elements.  By contrast, streams do not
 * provide a means to directly access or manipulate their elements, and are
 * instead concerned with declaratively describing their source and the
 * computational operations which will be performed in aggregate on that source.
 * However, if the provided stream operations do not offer the desired
 * functionality, the {@link #iterator()} and {@link #spliterator()} operations
 * can be used to perform a controlled traversal.
 * 集合和数据流，虽然有一些表面上的相似之处，但有不同的目标。
 * 集合主要关注对其元素的有效管理和访问。
 * 注意：相比之下，数据流不提供直接访问或操作其元素的方法，而是关注以声明方式描述其数据源以及将在该数据源上聚合执行的计算操作。
 * 但是，如果提供的数据流操作不提供所需的功能，则可以使用迭代器和拆分器操作来执行受控遍历。
 *
 * <p>A stream pipeline, like the "widgets" example above, can be viewed as
 * a <em>query</em> on the stream source.  Unless the source was explicitly
 * designed for concurrent modification (such as a {@link ConcurrentHashMap}),
 * unpredictable or erroneous behavior may result from modifying the stream
 * source while it is being queried.
 * 注意：数据流管道可以被视为对数据流源的查询。
 * 除非数据源明确设计用于并发修改，否则在查询数据流源时修改数据流源可能会导致不可预测或错误的行为。
 *
 * <p>Most stream operations accept parameters that describe user-specified
 * behavior, such as the lambda expression {@code w -> w.getWeight()} passed to
 * {@code mapToInt} in the example above.  To preserve correct behavior,
 * these <em>behavioral parameters</em>:
 * 大多数数据流操作接受描述用户指定行为的参数。为了保持正确的行为，这些行为参数：
 * <ul>
 * <li>must be <a href="package-summary.html#NonInterference">non-interfering</a>
 * (they do not modify the stream source); and</li>
 * 必须是无干扰的，它们不会修改数据流源；和
 * <li>in most cases must be <a href="package-summary.html#Statelessness">stateless</a>
 * (their result should not depend on any state that might change during execution
 * of the stream pipeline).</li>
 * 在大多数情况下必须是无状态的，它们的结果不应该依赖于在数据流管道执行期间可能更改的任何状态。
 * </ul>
 *
 * <p>Such parameters are always instances of a
 * <a href="../function/package-summary.html">functional interface</a> such
 * as {@link java.util.function.Function}, and are often lambda expressions or
 * method references.  Unless otherwise specified these parameters must be
 * <em>non-null</em>.
 * 这类参数始终是函数式接口的实例，并且通常是lambda表达式或方法引用。
 * 除非另有说明，否则这些参数必须为非空。
 *
 * <p>A stream should be operated on (invoking an intermediate or terminal stream
 * operation) only once.  This rules out, for example, "forked" streams, where
 * the same source feeds two or more pipelines, or multiple traversals of the
 * same stream.  A stream implementation may throw {@link IllegalStateException}
 * if it detects that the stream is being reused. However, since some stream
 * operations may return their receiver rather than a new stream object, it may
 * not be possible to detect reuse in all cases.
 * 一个数据流应该只被操作一次，调用一个中间或终结数据流操作。
 * 例如，这个规则排除了分叉数据流，其中相同的数据源提供两个或多个管道，或同一个数据流的多次遍历。
 * 如果数据流实现检测到数据流正在被重用，它可能会抛出非法状态异常。
 * 但是，由于某些数据流操作可能返回其接收者而不是新的数据流对象，因此可能无法在所有情况下检测重用。
 *
 * <p>Streams have a {@link #close()} method and implement {@link AutoCloseable},
 * but nearly all stream instances do not actually need to be closed after use.
 * Generally, only streams whose source is an IO channel (such as those returned
 * by {@link Files#lines(Path, Charset)}) will require closing.  Most streams
 * are backed by collections, arrays, or generating functions, which require no
 * special resource management.  (If a stream does require closing, it can be
 * declared as a resource in a {@code try}-with-resources statement.)
 * 数据流有一个关闭方法并实现AutoCloseable接口，但几乎所有数据流实例在使用后实际上不需要关闭。
 * 通常，只有数据源为IO通道的数据流才需要关闭，例如由Files.lines(Path, Charset)返回的数据流。
 * 大多数数据流由集合、数组或生成函数支持，不需要特殊的资源管理。
 * (如果数据流确实需要关闭，可以在try-with-resources语句中将其声明为资源。)
 *
 * <p>Stream pipelines may execute either sequentially or in
 * <a href="package-summary.html#Parallelism">parallel</a>.  This
 * execution mode is a property of the stream.  Streams are created
 * with an initial choice of sequential or parallel execution.  (For example,
 * {@link Collection#stream() Collection.stream()} creates a sequential stream,
 * and {@link Collection#parallelStream() Collection.parallelStream()} creates
 * a parallel one.)  This choice of execution mode may be modified by the
 * {@link #sequential()} or {@link #parallel()} methods, and may be queried with
 * the {@link #isParallel()} method.
 * 注意：数据流管道可以按顺序或并行执行。
 * 这种执行模式是数据流的属性。
 * 数据流是通过初始选择顺序或并行执行来创建的。
 * (例如，Collection.stream()创建一个顺序数据流，Collection.parallelStream()创建一个并行数据流。)
 * 这种执行模式的选择可以通过sequential()或parallel()方法进行修改，并且可以通过isParallel()方法查询。
 *
 * @param <T> the type of the stream elements 数据流元素的类型
 * @since 1.8
 * @see IntStream
 * @see LongStream
 * @see DoubleStream
 * @see <a href="package-summary.html">java.util.stream</a>
 */
public interface Stream<T> extends BaseStream<T, Stream<T>> {

    // 中间操作

    // 一元函数
    // 谓词函数-Predicate

    /**
     * Returns a stream consisting of the elements of this stream that match
     * the given predicate.
     * 返回一个数据流，由与给定谓词函数匹配的这个数据流的元素组成。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     * 这是一个中间操作。
     *
     * @param predicate a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                  <a href="package-summary.html#Statelessness">stateless</a>
     *                  predicate to apply to each element to determine if it
     *                  should be included
     * @return the new stream 新的数据流
     */
    Stream<T> filter(Predicate<? super T> predicate);

    // 一元函数-Function
    // 类型转换映射
    // 使用场景：一对一转换

    /**
     * Returns a stream consisting of the results of applying the given
     * function to the elements of this stream.
     * 返回一个数据流，由将给定函数应用于这个数据流的元素的结果组成。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     * 这是一个中间操作。
     *
     * @param <R> The element type of the new stream
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element
     * @return the new stream 新的数据流
     */
    <R> Stream<R> map(Function<? super T, ? extends R> mapper);

    // 一元函数<->数据流
    // ToIntFunction<->IntStream

    /**
     * Returns an {@code IntStream} consisting of the results of applying the
     * given function to the elements of this stream.
     * 返回一个整数数据流，由对这个数据流的元素应用给定一元函数的结果组成。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">
     * intermediate operation</a>.
     * 这是一个中间操作。
     *
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element
     * @return the new stream 新的数据流
     */
    IntStream mapToInt(ToIntFunction<? super T> mapper);

    /**
     * Returns a {@code LongStream} consisting of the results of applying the
     * given function to the elements of this stream.
     * 返回一个长整数数据流，由对这个数据流的元素应用给定一元函数的结果组成。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     * 这是一个中间操作。
     *
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element
     * @return the new stream 新的数据流
     */
    LongStream mapToLong(ToLongFunction<? super T> mapper);

    /**
     * Returns a {@code DoubleStream} consisting of the results of applying the
     * given function to the elements of this stream.
     * 返回一个浮点数数据流，由对这个数据流的元素应用给定一元函数的结果组成。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     * 这是一个中间操作。
     *
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element
     * @return the new stream 新的数据流
     */
    DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper);

    // 使用场景：一对多转换

    /**
     * Returns a stream consisting of the results of replacing each element of
     * this stream with the contents of a mapped stream produced by applying
     * the provided mapping function to each element.  Each mapped stream is
     * {@link java.util.stream.BaseStream#close() closed} after its contents
     * have been placed into this stream.  (If a mapped stream is {@code null}
     * an empty stream is used, instead.)
     * 返回一个数据流，由将提供的映射函数应用到每个元素而生成的映射数据流的内容替换这个数据流中的每个元素的结果组成。
     * 每个被映射的数据流在其内容被放置到这个数据流之后被关闭。(如果映射数据流为空，则使用空数据流。)
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     * 这是一个中间操作。
     *
     * @apiNote
     * The {@code flatMap()} operation has the effect of applying a one-to-many
     * transformation to the elements of the stream, and then flattening the
     * resulting elements into a new stream.
     * 这个操作的效果是对数据流的元素应用一对多转换，然后将结果元素扁平化为一个新的数据流。
     * (降低一维)
     *
     * <p><b>Examples. 示例</b>
     *
     * <p>If {@code orders} is a stream of purchase orders, and each purchase
     * order contains a collection of line items, then the following produces a
     * stream containing all the line items in all the orders:
     * 如果orders是一个采购订单数据流，并且每个采购订单包含行项目的集合，则以下函数将生成一个包含所有订单中所有行项目的数据流：
     * <pre>{@code
     *     orders.flatMap(order -> order.getLineItems().stream())...
     * }</pre>
     *
     * <p>If {@code path} is the path to a file, then the following produces a
     * stream of the {@code words} contained in that file:
     * 如果path是文件的路径，那么以下语句将生成包含这个文件中的单词的数据流：
     * <pre>{@code
     *     Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8);
     *     Stream<String> words = lines.flatMap(line -> Stream.of(line.split(" +")));
     * }</pre>
     * The {@code mapper} function passed to {@code flatMap} splits a line,
     * using a simple regular expression, into an array of words, and then
     * creates a stream of words from that array.
     * 传递给flatMap的映射函数使用一个简单的正则表达式将一行分割成一个单词数组，然后从这个数组创建一个单词数据流。
     *
     * @param <R> The element type of the new stream 新的数据流的元素类型
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element which produces a stream
     *               of new values
     *               一个无干扰、无状态的函数，应用于每个元素，产生新的值的数据流
     * @return the new stream 新的数据流
     */
    <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);

    // 一元函数<->数据流
    // Function<->IntStream

    /**
     * Returns an {@code IntStream} consisting of the results of replacing each
     * element of this stream with the contents of a mapped stream produced by
     * applying the provided mapping function to each element.  Each mapped
     * stream is {@link java.util.stream.BaseStream#close() closed} after its
     * contents have been placed into this stream.  (If a mapped stream is
     * {@code null} an empty stream is used, instead.)
     * 返回一个整数数据流，包含将这个数据流的每个元素替换为通过提供的映射函数应用于每个元素而生成的映射数据流的内容的结果。
     * 每个映射数据流在其内容被放入这个数据流后关闭。(如果映射数据流为空，则使用空数据流。)
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     * 这是一个中间操作。
     *
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element which produces a stream
     *               of new values
     * @return the new stream 新的数据流
     * @see #flatMap(Function)
     */
    IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper);

    /**
     * Returns an {@code LongStream} consisting of the results of replacing each
     * element of this stream with the contents of a mapped stream produced by
     * applying the provided mapping function to each element.  Each mapped
     * stream is {@link java.util.stream.BaseStream#close() closed} after its
     * contents have been placed into this stream.  (If a mapped stream is
     * {@code null} an empty stream is used, instead.)
     * 返回一个长整数数据流，包含将这个数据流的每个元素替换为通过提供的映射函数应用于每个元素而生成的映射数据流的内容的结果。
     * 每个映射数据流在其内容被放入这个数据流后关闭。(如果映射数据流为空，则使用空数据流。)
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     * 这是一个中间操作。
     *
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element which produces a stream
     *               of new values
     * @return the new stream 新的数据流
     * @see #flatMap(Function)
     */
    LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper);

    /**
     * Returns an {@code DoubleStream} consisting of the results of replacing
     * each element of this stream with the contents of a mapped stream produced
     * by applying the provided mapping function to each element.  Each mapped
     * stream is {@link java.util.stream.BaseStream#close() closed} after its
     * contents have placed been into this stream.  (If a mapped stream is
     * {@code null} an empty stream is used, instead.)
     * 返回一个浮点数数据流，包含将这个数据流的每个元素替换为通过提供的映射函数应用于每个元素而生成的映射数据流的内容的结果。
     * 每个映射数据流在其内容被放入这个数据流后关闭。(如果映射数据流为空，则使用空数据流。)
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     * 这是一个中间操作。
     *
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element which produces a stream
     *               of new values
     * @return the new stream 新的数据流
     * @see #flatMap(Function)
     */
    DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper);

    // 有状态的中间操作
    // 去重操作

    /**
     * Returns a stream consisting of the distinct elements (according to
     * {@link Object#equals(Object)}) of this stream.
     * 返回一个数据流，由这个数据流的不同元素组成。(根据Object.equals(Object))
     *
     * <p>For ordered streams, the selection of distinct elements is stable
     * (for duplicated elements, the element appearing first in the encounter
     * order is preserved.)  For unordered streams, no stability guarantees
     * are made.
     * 对于有序数据流，不同元素的选择是稳定的。(对于重复的元素，在相遇顺序中首先出现的元素将被保留)
     * 对于无序数据流，没有稳定性保证。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">stateful
     * intermediate operation</a>.
     * 这是一个有状态的中间操作。
     *
     * @apiNote
     * Preserving stability for {@code distinct()} in parallel pipelines is
     * relatively expensive (requires that the operation act as a full barrier,
     * with substantial buffering overhead), and stability is often not needed.
     * Using an unordered stream source (such as {@link #generate(Supplier)})
     * or removing the ordering constraint with {@link #unordered()} may result
     * in significantly more efficient execution for {@code distinct()} in parallel
     * pipelines, if the semantics of your situation permit.  If consistency
     * with encounter order is required, and you are experiencing poor performance
     * or memory utilization with {@code distinct()} in parallel pipelines,
     * switching to sequential execution with {@link #sequential()} may improve
     * performance.
     * 在并行数据流管道中保持distinct()的稳定性是相对昂贵的，并且通常不需要稳定性。(需要操作作为一个完整的屏障，有大量的缓存开销)
     * 使用无序数据流源或使用unordered()删除排序约束，可以在语义允许的情况下，显著提高并行管道中distinct()的执行效率。
     * 如果需要与遇到顺序保持一致，并且在并行管道中使用distinct()时遇到了较差的性能或内存使用情况，那么使用sequential()切换到顺序执行可能会提高性能。
     *
     * @return the new stream 新的数据流
     */
    Stream<T> distinct();

    // 排序操作

    /**
     * Returns a stream consisting of the elements of this stream, sorted
     * according to natural order.  If the elements of this stream are not
     * {@code Comparable}, a {@code java.lang.ClassCastException} may be thrown
     * when the terminal operation is executed.
     * 返回一个数据流，由这个数据流中的元素组成，按自然顺序排序。
     * 这个数据流的元素需要实现Comparable接口。
     * 如果这个数据流的元素不是Comparable，则在执行终结操作时可能会抛出类型转换异常。
     *
     * <p>For ordered streams, the sort is stable.  For unordered streams, no
     * stability guarantees are made.
     * 对于有序数据流，排序是稳定的。
     * 对于无序数据流，没有稳定性保证。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">stateful
     * intermediate operation</a>.
     * 这是一个有状态的中间操作。
     *
     * @return the new stream 新的数据流
     */
    Stream<T> sorted();

    /**
     * Returns a stream consisting of the elements of this stream, sorted
     * according to the provided {@code Comparator}.
     * 返回一个数据流，由这个数据流的元素组成，根据提供的比较器排序。
     *
     * <p>For ordered streams, the sort is stable.  For unordered streams, no
     * stability guarantees are made.
     * 对于有序数据流，排序是稳定的。
     * 对于无序数据流，没有稳定性保证。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">stateful
     * intermediate operation</a>.
     * 这是一个有状态的中间操作。
     *
     * @param comparator a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                   <a href="package-summary.html#Statelessness">stateless</a>
     *                   {@code Comparator} to be used to compare stream elements
     * @return the new stream 新的数据流
     */
    Stream<T> sorted(Comparator<? super T> comparator);

    // 操作数消费者操作-Consumer
    // 使用场景：记录中间状态日志

    /**
     * Returns a stream consisting of the elements of this stream, additionally
     * performing the provided action on each element as elements are consumed
     * from the resulting stream.
     * 返回一个数据流，由这个数据流的元素组成，当元素从结果数据流中被消费时，对每个元素执行所提供的操作。
     *
     * <p>This is an <a href="package-summary.html#StreamOps">intermediate
     * operation</a>.
     * 这是一个中间操作。
     *
     * <p>For parallel stream pipelines, the action may be called at
     * whatever time and in whatever thread the element is made available by the
     * upstream operation.  If the action modifies shared state,
     * it is responsible for providing the required synchronization.
     * 对于并行数据流管道，可以在上游操作使元素可用的任何时间和线程中调用这个操作。
     * 如果操作修改了共享状态，则它负责提供所需的同步。
     *
     * @apiNote This method exists mainly to support debugging, where you want
     * to see the elements as they flow past a certain point in a pipeline:
     * 这个方法的存在主要是为了支持调试，在调试时，你想看到元素数据流经管道中的某个特定点：
     * <pre>{@code
     *     Stream.of("one", "two", "three", "four")
     *         .filter(e -> e.length() > 3)
     *         .peek(e -> System.out.println("Filtered value: " + e))
     *         .map(String::toUpperCase)
     *         .peek(e -> System.out.println("Mapped value: " + e))
     *         .collect(Collectors.toList());
     * }</pre>
     *
     * @param action a <a href="package-summary.html#NonInterference">
     *                 non-interfering</a> action to perform on the elements as
     *                 they are consumed from the stream
     * @return the new stream 新的数据流
     */
    Stream<T> peek(Consumer<? super T> action);

    // 有状态的中间操作
    // 限制元素最大长度的操作

    /**
     * Returns a stream consisting of the elements of this stream, truncated
     * to be no longer than {@code maxSize} in length.
     * 返回一个数据流，由这个数据流的元素组成，其长度被截断为不超过maxSize。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * stateful intermediate operation</a>.
     * 这是一个短路状态的中间操作。
     *
     * @apiNote
     * While {@code limit()} is generally a cheap operation on sequential
     * stream pipelines, it can be quite expensive on ordered parallel pipelines,
     * especially for large values of {@code maxSize}, since {@code limit(n)}
     * is constrained to return not just any <em>n</em> elements, but the
     * <em>first n</em> elements in the encounter order.  Using an unordered
     * stream source (such as {@link #generate(Supplier)}) or removing the
     * ordering constraint with {@link #unordered()} may result in significant
     * speedups of {@code limit()} in parallel pipelines, if the semantics of
     * your situation permit.  If consistency with encounter order is required,
     * and you are experiencing poor performance or memory utilization with
     * {@code limit()} in parallel pipelines, switching to sequential execution
     * with {@link #sequential()} may improve performance.
     *
     * @param maxSize the number of elements the stream should be limited to
     * @return the new stream 新的数据流
     * @throws IllegalArgumentException if {@code maxSize} is negative
     */
    Stream<T> limit(long maxSize);

    // 忽略最前面N个元素的操作

    /**
     * Returns a stream consisting of the remaining elements of this stream
     * after discarding the first {@code n} elements of the stream.
     * If this stream contains fewer than {@code n} elements then an
     * empty stream will be returned.
     * 返回一个数据流，丢弃数据流的前n个元素后，由这个数据流的剩余元素组成。
     * 如果这个数据流包含的元素少于n个，则返回空数据流。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">stateful
     * intermediate operation</a>.
     * 这是一个有状态的中间操作。
     *
     * @apiNote
     * While {@code skip()} is generally a cheap operation on sequential
     * stream pipelines, it can be quite expensive on ordered parallel pipelines,
     * especially for large values of {@code n}, since {@code skip(n)}
     * is constrained to skip not just any <em>n</em> elements, but the
     * <em>first n</em> elements in the encounter order.  Using an unordered
     * stream source (such as {@link #generate(Supplier)}) or removing the
     * ordering constraint with {@link #unordered()} may result in significant
     * speedups of {@code skip()} in parallel pipelines, if the semantics of
     * your situation permit.  If consistency with encounter order is required,
     * and you are experiencing poor performance or memory utilization with
     * {@code skip()} in parallel pipelines, switching to sequential execution
     * with {@link #sequential()} may improve performance.
     *
     * @param n the number of leading elements to skip
     * @return the new stream 新的数据流
     * @throws IllegalArgumentException if {@code n} is negative
     */
    Stream<T> skip(long n);

    // 终结操作

    // 操作数消费者操作-Consumer
    // 使用场景：for-each遍历元素

    /**
     * Performs an action for each element of this stream.
     * 对这个数据流的每个元素执行这个行为操作。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     * 这是一个终结操作。
     *
     * <p>The behavior of this operation is explicitly nondeterministic.
     * For parallel stream pipelines, this operation does <em>not</em>
     * guarantee to respect the encounter order of the stream, as doing so
     * would sacrifice the benefit of parallelism.  For any given element, the
     * action may be performed at whatever time and in whatever thread the
     * library chooses.  If the action accesses shared state, it is
     * responsible for providing the required synchronization.
     * 这个操作的行为是明确的不确定性。
     * 对于并行数据流管道，这个操作并不保证遵守数据流的遇到顺序，因为这样做将牺牲并行性的好处。
     * 对于任何给定的元素，操作可以在库选择的任何时间和线程中执行。
     * 如果操作访问共享状态，则它负责提供所需的同步。
     *
     * @param action a <a href="package-summary.html#NonInterference">
     *               non-interfering</a> action to perform on the elements
     *               对元素执行的不干扰操作
     */
    void forEach(Consumer<? super T> action);

    /**
     * Performs an action for each element of this stream, in the encounter
     * order of the stream if the stream has a defined encounter order.
     * 如果数据流具有已定义的遇到顺序，则按照数据流的遇到顺序对这个数据流的每个元素执行操作。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     * 这是一个终结操作。
     *
     * <p>This operation processes the elements one at a time, in encounter
     * order if one exists.  Performing the action for one element
     * <a href="../concurrent/package-summary.html#MemoryVisibility"><i>happens-before</i></a>
     * performing the action for subsequent elements, but for any given element,
     * the action may be performed in whatever thread the library chooses.
     * 这个操作一次处理一个元素，如果存在，则按照遇到顺序处理。
     * 对一个元素执行操作，在对后续元素执行操作之前，但是对于任何给定的元素，操作可以在库选择的任何线程中执行。
     *
     * @param action a <a href="package-summary.html#NonInterference">
     *               non-interfering</a> action to perform on the elements
     *               对元素执行的不干扰操作
     * @see #forEach(Consumer)
     */
    void forEachOrdered(Consumer<? super T> action);

    // 转换为数组操作

    /**
     * Returns an array containing the elements of this stream.
     * 返回包含这个数据流元素的数组。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     * 这是一个终结操作。
     *
     * @return an array containing the elements of this stream
     */
    Object[] toArray();

    // 对象数组生成器

    /**
     * Returns an array containing the elements of this stream, using the
     * provided {@code generator} function to allocate the returned array, as
     * well as any additional arrays that might be required for a partitioned
     * execution or for resizing.
     * 返回包含这个数据流元素的数组，使用提供的生成器函数分配返回的数组，以及分区执行或调整大小可能需要的任何其他数组。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     * 这是一个终结操作。
     *
     * @apiNote
     * The generator function takes an integer, which is the size of the
     * desired array, and produces an array of the desired size.  This can be
     * concisely expressed with an array constructor reference:
     * 生成器函数接受一个整数，它是所需数组的大小，并生成一个所需大小的数组。这个可以用数组构造函数引用来简洁地表达：
     * <pre>{@code
     *     Person[] men = people.stream()
     *                          .filter(p -> p.getGender() == MALE)
     *                          .toArray(Person[]::new);
     * }</pre>
     *
     * @param <A> the element type of the resulting array
     * @param generator a function which produces a new array of the desired
     *                  type and the provided length
     * @return an array containing the elements in this stream
     * @throws ArrayStoreException if the runtime type of the array returned
     * from the array generator is not a supertype of the runtime type of every
     * element in this stream
     */
    <A> A[] toArray(IntFunction<A[]> generator);

    // 归约操作
    // 分而治之策略思想
    // MapReduce设计的一个理念就是“计算向数据靠拢”，而不是“数据向计算靠拢”，
    // 原因是，移动数据需要大量的网络传输开销。
    // 二元运算符-BinaryOperator

    /**
     * Performs a <a href="package-summary.html#Reduction">reduction</a> on the
     * elements of this stream, using the provided identity value and an
     * <a href="package-summary.html#Associativity">associative</a>
     * accumulation function, and returns the reduced value. This is equivalent
     * to:
     * <pre>{@code
     *     T result = identity;
     *     for (T element : this stream)
     *         result = accumulator.apply(result, element)
     *     return result;
     * }</pre>
     *
     * but is not constrained to execute sequentially.
     * 使用提供的标识值和关联累加函数，对这个数据流的元素执行归约，并返回归约后的值。
     * 但不限制按顺序执行。
     *
     * <p>The {@code identity} value must be an identity for the accumulator
     * function. This means that for all {@code t},
     * {@code accumulator.apply(identity, t)} is equal to {@code t}.
     * The {@code accumulator} function must be an
     * <a href="package-summary.html#Associativity">associative</a> function.
     * 标识值必须是累加器函数的标识值。
     * 这意味着对于所有的t，都是累加器。
     * 累加函数必须是一个关联函数。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     * 这是一个终结操作。
     *
     * @apiNote Sum, min, max, average, and string concatenation are all special
     * cases of reduction. Summing a stream of numbers can be expressed as:
     *
     * <pre>{@code
     *     Integer sum = integers.reduce(0, (a, b) -> a+b);
     * }</pre>
     *
     * or:
     *
     * <pre>{@code
     *     Integer sum = integers.reduce(0, Integer::sum);
     * }</pre>
     * 求和、最小值、最大值、平均值和字符串连接都是归约的特殊情况。
     *
     * <p>While this may seem a more roundabout way to perform an aggregation
     * compared to simply mutating a running total in a loop, reduction
     * operations parallelize more gracefully, without needing additional
     * synchronization and with greatly reduced risk of data races.
     * 虽然与简单地在循环中改变运行总数相比，这似乎是执行聚合的一种更迂回的方式，
     * 但归约操作的并行化更优雅，不需要额外的同步，并且大大降低了数据竞争的风险。
     *
     * @param identity the identity value for the accumulating function 累加函数的标识值
     * @param accumulator an <a href="package-summary.html#Associativity">associative</a>,
     *                    <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                    <a href="package-summary.html#Statelessness">stateless</a>
     *                    function for combining two values
     *                    一个关联的、不干扰的、无状态的函数，用来组合两个值
     * @return the result of the reduction
     */
    T reduce(T identity, BinaryOperator<T> accumulator);

    /**
     * Performs a <a href="package-summary.html#Reduction">reduction</a> on the
     * elements of this stream, using an
     * <a href="package-summary.html#Associativity">associative</a> accumulation
     * function, and returns an {@code Optional} describing the reduced value,
     * if any. This is equivalent to:
     * <pre>{@code
     *     boolean foundAny = false;
     *     T result = null;
     *     for (T element : this stream) {
     *         if (!foundAny) {
     *             foundAny = true;
     *             result = element;
     *         }
     *         else
     *             result = accumulator.apply(result, element);
     *     }
     *     return foundAny ? Optional.of(result) : Optional.empty();
     * }</pre>
     *
     * but is not constrained to execute sequentially.
     * 使用关联累加函数对这个数据流的元素进行归约，并返回一个可选的描述归约值。
     *
     * <p>The {@code accumulator} function must be an
     * <a href="package-summary.html#Associativity">associative</a> function.
     * 累加函数必须是一个关联函数。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     * 这是一个终结操作。
     *
     * @param accumulator an <a href="package-summary.html#Associativity">associative</a>,
     *                    <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                    <a href="package-summary.html#Statelessness">stateless</a>
     *                    function for combining two values
     *                    一个关联的、不干扰的、无状态的函数，用来组合两个值
     * @return an {@link Optional} describing the result of the reduction
     * @throws NullPointerException if the result of the reduction is null
     * @see #reduce(Object, BinaryOperator)
     * @see #min(Comparator)
     * @see #max(Comparator)
     */
    Optional<T> reduce(BinaryOperator<T> accumulator);

    // 二元函数+二元运算符

    /**
     * Performs a <a href="package-summary.html#Reduction">reduction</a> on the
     * elements of this stream, using the provided identity, accumulation and
     * combining functions.  This is equivalent to:
     * <pre>{@code
     *     U result = identity;
     *     for (T element : this stream)
     *         result = accumulator.apply(result, element)
     *     return result;
     * }</pre>
     *
     * but is not constrained to execute sequentially.
     * 使用提供的标识值、累加和组合函数，对这个数据流的元素执行归约。
     *
     * <p>The {@code identity} value must be an identity for the combiner
     * function.  This means that for all {@code u}, {@code combiner(identity, u)}
     * is equal to {@code u}.  Additionally, the {@code combiner} function
     * must be compatible with the {@code accumulator} function; for all
     * {@code u} and {@code t}, the following must hold:
     * <pre>{@code
     *     combiner.apply(u, accumulator.apply(identity, t)) == accumulator.apply(u, t)
     * }</pre>
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     * 这是一个终结操作。
     *
     * @apiNote Many reductions using this form can be represented more simply
     * by an explicit combination of {@code map} and {@code reduce} operations.
     * The {@code accumulator} function acts as a fused mapper and accumulator,
     * which can sometimes be more efficient than separate mapping and reduction,
     * such as when knowing the previously reduced value allows you to avoid
     * some computation.
     *
     * @param <U> The type of the result
     * @param identity the identity value for the combiner function
     * @param accumulator an <a href="package-summary.html#Associativity">associative</a>,
     *                    <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                    <a href="package-summary.html#Statelessness">stateless</a>
     *                    function for incorporating an additional element into a result
     * @param combiner an <a href="package-summary.html#Associativity">associative</a>,
     *                    <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                    <a href="package-summary.html#Statelessness">stateless</a>
     *                    function for combining two values, which must be
     *                    compatible with the accumulator function
     * @return the result of the reduction
     * @see #reduce(BinaryOperator)
     * @see #reduce(Object, BinaryOperator)
     */
    <U> U reduce(U identity,
                 BiFunction<U, ? super T, U> accumulator,
                 BinaryOperator<U> combiner);

    // 收集操作
    // 结果生产者+二元函数

    /**
     * Performs a <a href="package-summary.html#MutableReduction">mutable
     * reduction</a> operation on the elements of this stream.  A mutable
     * reduction is one in which the reduced value is a mutable result container,
     * such as an {@code ArrayList}, and elements are incorporated by updating
     * the state of the result rather than by replacing the result.  This
     * produces a result equivalent to:
     * <pre>{@code
     *     R result = supplier.get();
     *     for (T element : this stream)
     *         accumulator.accept(result, element);
     *     return result;
     * }</pre>
     * 对这个数据流的元素执行可变的归约操作。
     * 可变的归约是这样一种情况：被归约的值是一个可变的结果容器，元素是通过更新结果的状态而不是替换结果来合并的。
     *
     * <p>Like {@link #reduce(Object, BinaryOperator)}, {@code collect} operations
     * can be parallelized without requiring additional synchronization.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     * 这是一个终结操作。
     *
     * @apiNote There are many existing classes in the JDK whose signatures are
     * well-suited for use with method references as arguments to {@code collect()}.
     * For example, the following will accumulate strings into an {@code ArrayList}:
     * <pre>{@code
     *     List<String> asList = stringStream.collect(ArrayList::new, ArrayList::add,
     *                                                ArrayList::addAll);
     * }</pre>
     *
     * <p>The following will take a stream of strings and concatenates them into a
     * single string:
     * <pre>{@code
     *     String concat = stringStream.collect(StringBuilder::new, StringBuilder::append,
     *                                          StringBuilder::append)
     *                                 .toString();
     * }</pre>
     *
     * @param <R> type of the result
     * @param supplier a function that creates a new result container. For a
     *                 parallel execution, this function may be called
     *                 multiple times and must return a fresh value each time.
     * @param accumulator an <a href="package-summary.html#Associativity">associative</a>,
     *                    <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                    <a href="package-summary.html#Statelessness">stateless</a>
     *                    function for incorporating an additional element into a result
     * @param combiner an <a href="package-summary.html#Associativity">associative</a>,
     *                    <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                    <a href="package-summary.html#Statelessness">stateless</a>
     *                    function for combining two values, which must be
     *                    compatible with the accumulator function
     * @return the result of the reduction
     */
    <R> R collect(Supplier<R> supplier,
                  BiConsumer<R, ? super T> accumulator,
                  BiConsumer<R, R> combiner);

    // 收集器操作

    /**
     * Performs a <a href="package-summary.html#MutableReduction">mutable
     * reduction</a> operation on the elements of this stream using a
     * {@code Collector}.  A {@code Collector}
     * encapsulates the functions used as arguments to
     * {@link #collect(Supplier, BiConsumer, BiConsumer)}, allowing for reuse of
     * collection strategies and composition of collect operations such as
     * multiple-level grouping or partitioning.
     * 使用收集器对这个数据流的元素执行可变的归约操作。
     * 收集器封装了用作收集参数的函数，允许重用收集策略和收集操作的组合，比如多级分组或分区。
     *
     * <p>If the stream is parallel, and the {@code Collector}
     * is {@link Collector.Characteristics#CONCURRENT concurrent}, and
     * either the stream is unordered or the collector is
     * {@link Collector.Characteristics#UNORDERED unordered},
     * then a concurrent reduction will be performed (see {@link Collector} for
     * details on concurrent reduction.)
     * 如果数据流是并行的，收集器是并发的，并且数据流是无序的，或者收集器是无序的，
     * 那么将执行一个并发归约。(有关并发归约的详细信息，请参阅收集器。)
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     * 这是一个终结操作。
     *
     * <p>When executed in parallel, multiple intermediate results may be
     * instantiated, populated, and merged so as to maintain isolation of
     * mutable data structures.  Therefore, even when executed in parallel
     * with non-thread-safe data structures (such as {@code ArrayList}), no
     * additional synchronization is needed for a parallel reduction.
     * 当并行执行时，多个中间结果可能被实例化、填充和合并，以保持可变数据结构的隔离。
     * 因此，即使在与非线程安全的数据结构并行执行时，也不需要额外的同步来进行并行归约。
     *
     * @apiNote
     * The following will accumulate strings into an ArrayList:
     * <pre>{@code
     *     List<String> asList = stringStream.collect(Collectors.toList());
     * }</pre>
     *
     * <p>The following will classify {@code Person} objects by city:
     * <pre>{@code
     *     Map<String, List<Person>> peopleByCity
     *         = personStream.collect(Collectors.groupingBy(Person::getCity));
     * }</pre>
     *
     * <p>The following will classify {@code Person} objects by state and city,
     * cascading two {@code Collector}s together:
     * <pre>{@code
     *     Map<String, Map<String, List<Person>>> peopleByStateAndCity
     *         = personStream.collect(Collectors.groupingBy(Person::getState,
     *                                                      Collectors.groupingBy(Person::getCity)));
     * }</pre>
     *
     * @param <R> the type of the result
     * @param <A> the intermediate accumulation type of the {@code Collector}
     * @param collector the {@code Collector} describing the reduction
     * @return the result of the reduction
     * @see #collect(Supplier, BiConsumer, BiConsumer)
     * @see Collectors
     */
    <R, A> R collect(Collector<? super T, A, R> collector);

    // 查找元素最小值的操作

    /**
     * Returns the minimum element of this stream according to the provided
     * {@code Comparator}.  This is a special case of a
     * <a href="package-summary.html#Reduction">reduction</a>.
     * 根据提供的比较器返回这个数据流的最小值元素。
     * 这是归约的一个特殊情况。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal operation</a>.
     * 这是一个终结操作。
     *
     * @param comparator a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                   <a href="package-summary.html#Statelessness">stateless</a>
     *                   {@code Comparator} to compare elements of this stream
     * @return an {@code Optional} describing the minimum element of this stream,
     * or an empty {@code Optional} if the stream is empty
     * @throws NullPointerException if the minimum element is null
     */
    Optional<T> min(Comparator<? super T> comparator);

    // 查找元素最大值的操作

    /**
     * Returns the maximum element of this stream according to the provided
     * {@code Comparator}.  This is a special case of a
     * <a href="package-summary.html#Reduction">reduction</a>.
     * 根据提供的比较器返回这个数据流的最大值元素。
     * 这是归约的一个特殊情况。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     * 这是一个终结操作。
     *
     * @param comparator a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                   <a href="package-summary.html#Statelessness">stateless</a>
     *                   {@code Comparator} to compare elements of this stream
     * @return an {@code Optional} describing the maximum element of this stream,
     * or an empty {@code Optional} if the stream is empty
     * @throws NullPointerException if the maximum element is null
     */
    Optional<T> max(Comparator<? super T> comparator);

    // 元素计数器操作

    /**
     * Returns the count of elements in this stream.  This is a special case of
     * a <a href="package-summary.html#Reduction">reduction</a> and is
     * equivalent to:
     * <pre>{@code
     *     return mapToLong(e -> 1L).sum();
     * }</pre>
     * 返回这个数据流中的元素计数。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal operation</a>.
     * 这是一个终结操作。
     *
     * @return the count of elements in this stream
     */
    long count();

    // 短路的终结操作

    // 谓词函数-Predicate
    // 任意匹配的操作

    /**
     * Returns whether any elements of this stream match the provided
     * predicate.  May not evaluate the predicate on all elements if not
     * necessary for determining the result.  If the stream is empty then
     * {@code false} is returned and the predicate is not evaluated.
     * 返回这个数据流的任何元素是否与提供的谓词函数匹配。
     * 如果不需要确定结果，则不能对所有元素计算谓词函数。
     * 如果数据流为空，则返回false，并且不计算谓词函数。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     * 这是一个短路的终结操作。
     *
     * @apiNote
     * This method evaluates the <em>existential quantification</em> of the
     * predicate over the elements of the stream (for some x P(x)).
     *
     * @param predicate a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                  <a href="package-summary.html#Statelessness">stateless</a>
     *                  predicate to apply to elements of this stream
     *                  应用于这个数据流元素的无干扰、无状态的谓词函数
     * @return {@code true} if any elements of the stream match the provided
     * predicate, otherwise {@code false}
     */
    boolean anyMatch(Predicate<? super T> predicate);

    // 所有都匹配的操作

    /**
     * Returns whether all elements of this stream match the provided predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result.  If the stream is empty then {@code true} is
     * returned and the predicate is not evaluated.
     * 返回这个数据流的所有元素是否与提供的谓词函数匹配。
     * 如果不需要确定结果，则不能对所有元素计算谓词函数。
     * 如果数据流为空，则返回true，并且不计算谓词函数。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     * 这是一个短路的终结操作。
     *
     * @apiNote
     * This method evaluates the <em>universal quantification</em> of the
     * predicate over the elements of the stream (for all x P(x)).  If the
     * stream is empty, the quantification is said to be <em>vacuously
     * satisfied</em> and is always {@code true} (regardless of P(x)).
     *
     * @param predicate a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                  <a href="package-summary.html#Statelessness">stateless</a>
     *                  predicate to apply to elements of this stream
     *                  应用于这个数据流元素的无干扰、无状态的谓词函数
     * @return {@code true} if either all elements of the stream match the
     * provided predicate or the stream is empty, otherwise {@code false}
     */
    boolean allMatch(Predicate<? super T> predicate);

    // 所有都不匹配的操作

    /**
     * Returns whether no elements of this stream match the provided predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result.  If the stream is empty then {@code true} is
     * returned and the predicate is not evaluated.
     * 返回这个数据流中是否没有元素与提供的谓词函数匹配。
     * 如果不需要确定结果，则不能对所有元素计算谓词函数。
     * 如果数据流为空，则返回true，并且不计算谓词函数。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     * 这是一个短路的终结操作。
     *
     * @apiNote
     * This method evaluates the <em>universal quantification</em> of the
     * negated predicate over the elements of the stream (for all x ~P(x)).  If
     * the stream is empty, the quantification is said to be vacuously satisfied
     * and is always {@code true}, regardless of P(x).
     *
     * @param predicate a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                  <a href="package-summary.html#Statelessness">stateless</a>
     *                  predicate to apply to elements of this stream
     *                  应用于这个数据流元素的无干扰、无状态的谓词函数
     * @return {@code true} if either no elements of the stream match the
     * provided predicate or the stream is empty, otherwise {@code false}
     */
    boolean noneMatch(Predicate<? super T> predicate);

    // 查找第一个元素的操作

    /**
     * Returns an {@link Optional} describing the first element of this stream,
     * or an empty {@code Optional} if the stream is empty.  If the stream has
     * no encounter order, then any element may be returned.
     * 返回描述数据流的第一个元素的可选值容器，如果数据流为空，则返回空的可选值容器。
     * 如果数据流没有遇到顺序，则可以返回任何元素。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     * 这是一个短路的终结操作。
     *
     * @return an {@code Optional} describing the first element of this stream,
     * or an empty {@code Optional} if the stream is empty
     * @throws NullPointerException if the element selected is null
     */
    Optional<T> findFirst();

    // 查找任意元素的操作

    /**
     * Returns an {@link Optional} describing some element of the stream, or an
     * empty {@code Optional} if the stream is empty.
     * 返回描述数据流的某些元素的可选值容器，如果数据流为空，则返回空的可选值容器。
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     * 这是一个短路的终结操作。
     *
     * <p>The behavior of this operation is explicitly nondeterministic; it is
     * free to select any element in the stream.  This is to allow for maximal
     * performance in parallel operations; the cost is that multiple invocations
     * on the same source may not return the same result.  (If a stable result
     * is desired, use {@link #findFirst()} instead.)
     *
     * @return an {@code Optional} describing some element of this stream, or an
     * empty {@code Optional} if the stream is empty
     * @throws NullPointerException if the element selected is null
     * @see #findFirst()
     */
    Optional<T> findAny();

    // Static factories
    // 静态工厂方法

    // 数据流的构建器

    /**
     * Returns a builder for a {@code Stream}.
     * 返回数据流的构建器。
     *
     * @param <T> type of elements
     * @return a stream builder
     */
    static<T> Builder<T> builder() {
        return new Streams.StreamBuilderImpl<>();
    }

    // 空的数据流

    /**
     * Returns an empty sequential {@code Stream}.
     * 返回一个空的顺序数据流。
     *
     * @param <T> the type of stream elements
     * @return an empty sequential stream
     */
    static<T> Stream<T> empty() {
        return StreamSupport.stream(Spliterators.emptySpliterator(), false);
    }

    // 构建新的数据流

    /**
     * Returns a sequential {@code Stream} containing a single element.
     * 返回包含单个元素的顺序数据流。
     *
     * @param t the single element
     * @param <T> the type of stream elements
     * @return a singleton sequential stream
     */
    static<T> Stream<T> of(T t) {
        return StreamSupport.stream(new Streams.StreamBuilderImpl<>(t), false);
    }

    /**
     * Returns a sequential ordered stream whose elements are the specified values.
     * 返回顺序的有序数据流，其元素为指定的值。
     *
     * @param <T> the type of stream elements
     * @param values the elements of the new stream
     * @return the new stream
     */
    @SafeVarargs
    @SuppressWarnings("varargs") // Creating a stream from an array is safe
    static<T> Stream<T> of(T... values) {
        return Arrays.stream(values);
    }

    // 数据流迭代器

    /**
     * Returns an infinite sequential ordered {@code Stream} produced by iterative
     * application of a function {@code f} to an initial element {@code seed},
     * producing a {@code Stream} consisting of {@code seed}, {@code f(seed)},
     * {@code f(f(seed))}, etc.
     *
     * <p>The first element (position {@code 0}) in the {@code Stream} will be
     * the provided {@code seed}.  For {@code n > 0}, the element at position
     * {@code n}, will be the result of applying the function {@code f} to the
     * element at position {@code n - 1}.
     *
     * @param <T> the type of stream elements
     * @param seed the initial element
     * @param f a function to be applied to to the previous element to produce
     *          a new element
     * @return a new sequential {@code Stream}
     */
    static<T> Stream<T> iterate(final T seed, final UnaryOperator<T> f) {
        Objects.requireNonNull(f);
        final Iterator<T> iterator = new Iterator<T>() {
            @SuppressWarnings("unchecked")
            T t = (T) Streams.NONE;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public T next() {
                return t = (t == Streams.NONE) ? seed : f.apply(t);
            }

            @Override
            public void remove() {
                // empty
            }
        };
        // 有序的、不可变的
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iterator,
                Spliterator.ORDERED | Spliterator.IMMUTABLE), false);
    }

    // 数据流生成器
    // 结果生产者-Supplier

    /**
     * Returns an infinite sequential unordered stream where each element is
     * generated by the provided {@code Supplier}.  This is suitable for
     * generating constant streams, streams of random elements, etc.
     * 返回一个无限连续的无序数据流，其中每个元素都是由提供的结果提供者生成的。
     * 这个适用于生成常量数据流、随机元素数据流等。
     *
     * @param <T> the type of stream elements
     * @param s the {@code Supplier} of generated elements
     * @return a new infinite sequential unordered {@code Stream}
     */
    static<T> Stream<T> generate(Supplier<T> s) {
        Objects.requireNonNull(s);
        return StreamSupport.stream(
                new StreamSpliterators.InfiniteSupplyingSpliterator.OfRef<>(Long.MAX_VALUE, s), false);
    }

    // 数据流连接器

    /**
     * Creates a lazily concatenated stream whose elements are all the
     * elements of the first stream followed by all the elements of the
     * second stream.  The resulting stream is ordered if both
     * of the input streams are ordered, and parallel if either of the input
     * streams is parallel.  When the resulting stream is closed, the close
     * handlers for both input streams are invoked.
     * 创建一个延迟连接的数据流，其元素是第一个数据流的所有元素，后面是第二个数据流的所有元素。
     *
     * @implNote
     * Use caution when constructing streams from repeated concatenation.
     * Accessing an element of a deeply concatenated stream can result in deep
     * call chains, or even {@code StackOverflowException}.
     *
     * @param <T> The type of stream elements
     * @param a the first stream
     * @param b the second stream
     * @return the concatenation of the two input streams
     */
    static <T> Stream<T> concat(Stream<? extends T> a, Stream<? extends T> b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);

        @SuppressWarnings("unchecked")
        Spliterator<T> split = new Streams.ConcatSpliterator.OfRef<>(
                (Spliterator<T>) a.spliterator(), (Spliterator<T>) b.spliterator());
        Stream<T> stream = StreamSupport.stream(split, a.isParallel() || b.isParallel());
        return stream.onClose(Streams.composedClose(a, b));
    }

    /**
     * A mutable builder for a {@code Stream}.  This allows the creation of a
     * {@code Stream} by generating elements individually and adding them to the
     * {@code Builder} (without the copying overhead that comes from using
     * an {@code ArrayList} as a temporary buffer.)
     * 数据流的可变构建器。
     *
     * <p>A stream builder has a lifecycle, which starts in a building
     * phase, during which elements can be added, and then transitions to a built
     * phase, after which elements may not be added.  The built phase begins
     * when the {@link #build()} method is called, which creates an ordered
     * {@code Stream} whose elements are the elements that were added to the stream
     * builder, in the order they were added.
     *
     * @param <T> the type of stream elements
     *           数据流元素的类型
     * @see Stream#builder()
     * @since 1.8
     */
    interface Builder<T> extends Consumer<T> {

        /**
         * Adds an element to the stream being built.
         * 向正在构建的数据流添加元素。
         *
         * @throws IllegalStateException if the builder has already transitioned to
         * the built state
         */
        @Override
        void accept(T t);

        /**
         * Adds an element to the stream being built.
         * 向正在构建的数据流添加元素。
         *
         * @implSpec
         * The default implementation behaves as if:
         * <pre>{@code
         *     accept(t)
         *     return this;
         * }</pre>
         *
         * @param t the element to add
         * @return {@code this} builder
         * @throws IllegalStateException if the builder has already transitioned to
         * the built state
         */
        default Builder<T> add(T t) {
            accept(t);
            return this;
        }

        /**
         * Builds the stream, transitioning this builder to the built state.
         * An {@code IllegalStateException} is thrown if there are further attempts
         * to operate on the builder after it has entered the built state.
         * 构建数据流，将这个数据流的构建器转换为构建状态。
         * 在构建器进入构建状态之后，如果有进一步的操作尝试，则会抛出非法状态异常。
         *
         * @return the built stream
         * @throws IllegalStateException if the builder has already transitioned to
         * the built state
         */
        Stream<T> build();

    }
}
