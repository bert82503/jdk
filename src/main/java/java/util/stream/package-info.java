
/**
 * Classes to support functional-style operations on streams of elements, such
 * as map-reduce transformations on collections.
 * 数据流类，支持对元素数据流的函数式操作。
 * 例如，集合上的map-reduce转换。
 * For example:
 *
 * <pre>{@code
 *     int sum = widgets.stream()
 *                      .filter(b -> b.getColor() == RED)
 *                      .mapToInt(b -> b.getWeight())
 *                      .sum();
 * }</pre>
 *
 * <p>Here we use {@code widgets}, a {@code Collection<Widget>},
 * as a source for a stream, and then perform a filter-map-reduce on the stream
 * to obtain the sum of the weights of the red widgets.  (Summation is an
 * example of a <a href="package-summary.html#Reduction">reduction</a>
 * operation.)
 * 在这里，我们使用小部件(一个集合)作为数据流的源，然后在数据流上执行过滤-映射-归约(filter-map-reduce)，
 * 以获得红色小部件的权重之和。(求和是简化运算的一个例子。)
 *
 * <p>The key abstraction introduced in this package is <em>stream</em>.  The
 * classes {@link java.util.stream.Stream}, {@link java.util.stream.IntStream},
 * {@link java.util.stream.LongStream}, and {@link java.util.stream.DoubleStream}
 * are streams over objects and the primitive {@code int}, {@code long} and
 * {@code double} types.  Streams differ from collections in several ways:
 * 注意：这个包引入的关键抽象是数据流(stream)。
 * 类Stream、IntStream、LongStream和DoubleStream是对象和基本类型int、long、double上的数据流。
 * 数据流在几个方面不同于集合：
 *
 * <ul>
 *     <li>No storage.  A stream is not a data structure that stores elements;
 *     instead, it conveys elements from a source such as a data structure,
 *     an array, a generator function, or an I/O channel, through a pipeline of
 *     computational operations.</li>
 *     1.没有存储。数据流不是存储元素的数据结构；相反，它通过计算操作的管道传递来自数据源的元素，
 *     例如数据结构、数组、生成器函数或I/O通道。
 *     <li>Functional in nature.  An operation on a stream produces a result,
 *     but does not modify its source.  For example, filtering a {@code Stream}
 *     obtained from a collection produces a new {@code Stream} without the
 *     filtered elements, rather than removing elements from the source
 *     collection.</li>
 *     2.函数式是本质。数据流上的操作产生一个结果，但不修改其数据源。
 *     例如，过滤从集合中获得的Stream会生成一个没有过滤元素的新Stream，而不是从数据源集合中移除元素。
 *     (存在临时中间结果的元素复制)
 *     <li>Laziness-seeking.  Many stream operations, such as filtering, mapping,
 *     or duplicate removal, can be implemented lazily, exposing opportunities
 *     for optimization.  For example, "find the first {@code String} with
 *     three consecutive vowels" need not examine all the input strings.
 *     Stream operations are divided into intermediate ({@code Stream}-producing)
 *     operations and terminal (value- or side-effect-producing) operations.
 *     Intermediate operations are always lazy.</li>
 *     3.惰性寻找。许多数据流操作，如过滤、映射或重复移除，可以延迟实现，从而暴露出优化的机会。
 *     数据流操作分为中间(产生数据流)操作和终结(产生值或副作用)操作。中间操作总是惰性的。
 *     (中间操作和终结操作)
 *     <li>Possibly unbounded.  While collections have a finite size, streams
 *     need not.  Short-circuiting operations such as {@code limit(n)} or
 *     {@code findFirst()} can allow computations on infinite streams to
 *     complete in finite time.</li>
 *     4.可能是无界的。集合的大小是有限的，而数据流则不需要。
 *     像limit(n)或findFirst()这样的短路操作可以允许在有限的时间内完成无限数据流上的计算。
 *     <li>Consumable. The elements of a stream are only visited once during
 *     the life of a stream. Like an {@link java.util.Iterator}, a new stream
 *     must be generated to revisit the same elements of the source.
 *     </li>
 *     4.消耗品，可消费的。数据流的元素在其生命周期中只会被访问一次。
 *     像一个迭代器，必须生成一个新的数据流以重新访问数据源中的相同元素。
 * </ul>
 *
 * Streams can be obtained in a number of ways.
 * 数据流可以通过多种方式获得。
 * Some examples include:
 * <ul>
 *     <li>From a {@link java.util.Collection} via the {@code stream()} and
 *     {@code parallelStream()} methods;</li>
 *     从一个集合通过stream()和parallelStream()方法；
 *     <li>From an array via {@link java.util.Arrays#stream(Object[])};</li>
 *     从一个数组通过Arrays.stream(Object[])；
 *     <li>From static factory methods on the stream classes, such as
 *     {@link java.util.stream.Stream#of(Object[])},
 *     {@link java.util.stream.IntStream#range(int, int)}
 *     or {@link java.util.stream.Stream#iterate(Object, UnaryOperator)};</li>
 *     从数据流类的静态工厂方法，如Stream.of(Object[])、IntStream.range(int, int)、Stream.iterate(Object, UnaryOperator)；
 *     <li>The lines of a file can be obtained from {@link java.io.BufferedReader#lines()};</li>
 *     文件的行数据流可以从BufferedReader.lines()中获取；
 *     <li>Streams of file paths can be obtained from methods in {@link java.nio.file.Files};</li>
 *     文件路径的数据流可以通过Files中的方法获得；
 *     <li>Streams of random numbers can be obtained from {@link java.util.Random#ints()};</li>
 *     随机数的数据流可以从Random.ints()获得；
 *     <li>Numerous other stream-bearing methods in the JDK, including
 *     {@link java.util.BitSet#stream()},
 *     {@link java.util.regex.Pattern#splitAsStream(java.lang.CharSequence)},
 *     and {@link java.util.jar.JarFile#stream()}.</li>
 *     JDK中许多其他的包含数据流的方法，包括BitSet.stream()、Pattern.splitAsStream(CharSequence)、JarFile.stream()。
 * </ul>
 *
 * <p>Additional stream sources can be provided by third-party libraries using
 * <a href="package-summary.html#StreamSources">these techniques</a>.
 * 使用这些技术的第三方库可以提供额外的数据流源。
 *
 * <h2><a name="StreamOps">Stream operations and pipelines 数据流操作和管道</a></h2>
 *
 * <p>Stream operations are divided into <em>intermediate</em> and
 * <em>terminal</em> operations, and are combined to form <em>stream
 * pipelines</em>.  A stream pipeline consists of a source (such as a
 * {@code Collection}, an array, a generator function, or an I/O channel);
 * followed by zero or more intermediate operations such as
 * {@code Stream.filter} or {@code Stream.map}; and a terminal operation such
 * as {@code Stream.forEach} or {@code Stream.reduce}.
 * 注意：数据流操作分为中间操作和终结操作，并结合形成数据流管道。
 * 一个数据流管道由一个数据源组成，如一个集合、一个数组、一个生成器函数，或者一个I/O通道；
 * 后面跟着零个或多个中间操作，如Stream.filter、Stream.map；
 * 和一个终结操作，如Stream.forEach、Stream.reduce。
 * (一个数据流管道由一个数据源组成，后面跟着零个或多个中间操作，和一个终结操作。)
 *
 * <p>Intermediate operations return a new stream.  They are always
 * <em>lazy</em>; executing an intermediate operation such as
 * {@code filter()} does not actually perform any filtering, but instead
 * creates a new stream that, when traversed, contains the elements of
 * the initial stream that match the given predicate.  Traversal
 * of the pipeline source does not begin until the terminal operation of the
 * pipeline is executed.
 * 注意：中间操作返回一个新的数据流。
 * 他们总是很懒惰，执行中间操作，如filter()，实际上并不执行任何过滤，而是创建一个新的数据流；
 * 当遍历时，这个数据流包含与给定谓词函数匹配的初始数据流的元素。
 * 管道数据源的遍历直到管道的终结操作执行后才开始。
 *
 * <p>Terminal operations, such as {@code Stream.forEach} or
 * {@code IntStream.sum}, may traverse the stream to produce a result or a
 * side-effect. After the terminal operation is performed, the stream pipeline
 * is considered consumed, and can no longer be used; if you need to traverse
 * the same data source again, you must return to the data source to get a new
 * stream.  In almost all cases, terminal operations are <em>eager</em>,
 * completing their traversal of the data source and processing of the pipeline
 * before returning.  Only the terminal operations {@code iterator()} and
 * {@code spliterator()} are not; these are provided as an "escape hatch" to enable
 * arbitrary client-controlled pipeline traversals in the event that the
 * existing operations are not sufficient to the task.
 * 注意：终结操作，如Stream.forEach或IntStream.sum，可以遍历数据流以产生一个结果或副作用。
 * 终结操作完成后，视为数据流管道消费完毕，不再使用。
 * 如果需要再次遍历相同的数据源，则必须返回到数据源以获得新的数据流。
 * 在几乎所有情况下，终结操作都是急迫的，在返回之前完成对数据源的遍历和对数据流管道的处理。
 *
 * <p> Processing streams lazily allows for significant efficiencies; in a
 * pipeline such as the filter-map-sum example above, filtering, mapping, and
 * summing can be fused into a single pass on the data, with minimal
 * intermediate state. Laziness also allows avoiding examining all the data
 * when it is not necessary; for operations such as "find the first string
 * longer than 1000 characters", it is only necessary to examine just enough
 * strings to find one that has the desired characteristics without examining
 * all of the strings available from the source. (This behavior becomes even
 * more important when the input stream is infinite and not merely large.)
 * 延迟处理数据流允许显著的效率。在一个管道中，过滤、映射和求和可以融合到数据的单个传递中，中间状态最小。
 * 懒惰还会让你在不必要的时候避免检查所有的数据；
 * 对于像"查找第一个超过1000个字符的字符串"这样的操作，只需要检查足够多的字符串，以找到一个具有所需特征的字符串，而不需要检查数据源中所有可用的字符串。
 * (当输入数据流是无限的而不仅仅是很大的时候，这种行为变得更加重要。)
 *
 * <p>Intermediate operations are further divided into <em>stateless</em>
 * and <em>stateful</em> operations. Stateless operations, such as {@code filter}
 * and {@code map}, retain no state from previously seen element when processing
 * a new element -- each element can be processed
 * independently of operations on other elements.  Stateful operations, such as
 * {@code distinct} and {@code sorted}, may incorporate state from previously
 * seen elements when processing new elements.
 * 注意：中间操作进一步分为无状态操作和有状态操作。
 * 在处理新元素时，无状态操作不会保留前面看到的元素的状态，如filter和map——
 * 每个元素都可以独立于对其他元素的操作进行处理。
 * 在处理新元素时，有状态操作可以合并以前看到的元素的状态，如distinct和sorted。
 *
 * <p>Stateful operations may need to process the entire input
 * before producing a result.  For example, one cannot produce any results from
 * sorting a stream until one has seen all elements of the stream.  As a result,
 * under parallel computation, some pipelines containing stateful intermediate
 * operations may require multiple passes on the data or may need to buffer
 * significant data.  Pipelines containing exclusively stateless intermediate
 * operations can be processed in a single pass, whether sequential or parallel,
 * with minimal data buffering.
 * 有状态操作可能需要在产生结果之前处理整个输入。
 * 例如，在看到数据流的所有元素之前，不能对数据流进行排序产生任何结果。
 * 因此，在并行计算下，一些包含有状态中间操作的管道可能需要多次传递数据，或者可能需要缓冲重要数据。
 * 包含专门无状态中间操作的管道可以在一次传递中处理，无论是顺序的还是并行的，使用最小的数据缓冲。
 *
 * <p>Further, some operations are deemed <em>short-circuiting</em> operations.
 * An intermediate operation is short-circuiting if, when presented with
 * infinite input, it may produce a finite stream as a result.  A terminal
 * operation is short-circuiting if, when presented with infinite input, it may
 * terminate in finite time.  Having a short-circuiting operation in the pipeline
 * is a necessary, but not sufficient, condition for the processing of an infinite
 * stream to terminate normally in finite time.
 * 此外，一些操作被认为是短路操作。
 * 当输入为无限时，中间操作可能产生有限的数据流作为结果，这就是短路。
 * 当有无限输入时，终结操作可能在有限的时间内终止，那么终结操作就是短路。
 * 在数据流管道中有一个短路操作是必要的，但不是充分的条件，处理一个无限数据流通常在有限的时间结束。
 *
 * <h3>Parallelism 并行性</h3>
 *
 * <p>Processing elements with an explicit {@code for-}loop is inherently serial.
 * Streams facilitate parallel execution by reframing the computation as a pipeline of
 * aggregate operations, rather than as imperative operations on each individual
 * element.  All streams operations can execute either in serial or in parallel.
 * The stream implementations in the JDK create serial streams unless parallelism is
 * explicitly requested.
 * 使用显式for循环处理元素本质上是串行的。
 * 注意：数据流通过将计算重构为聚合操作的数据流管道，而不是作为对每个单独元素的命令式操作，从而促进了并行执行。
 * 所有数据流操作可以串行或并行执行，除非明确要求并行性，否则JDK中的数据流实现会创建串行数据流。
 * For example, {@code Collection} has methods
 * {@link java.util.Collection#stream} and {@link java.util.Collection#parallelStream},
 * which produce sequential and parallel streams respectively; other
 * stream-bearing methods such as {@link java.util.stream.IntStream#range(int, int)}
 * produce sequential streams but these streams can be efficiently parallelized by
 * invoking their {@link java.util.stream.BaseStream#parallel()} method.
 * 例如，Collection有Collection.stream和Collection.parallelStream方法，分别产生顺序数据流和并行数据流；
 * 其他数据流承载方法，如IntStream.range(int, int)产生顺序数据流，但这些数据流可以通过调用BaseStream.parallel()方法来有效地并行化。
 * To execute the prior "sum of weights of widgets" query in parallel, we would
 * do:
 *
 * <pre>{@code
 *     int sumOfWeights = widgets.}<code><b>parallelStream()</b></code>{@code
 *                               .filter(b -> b.getColor() == RED)
 *                               .mapToInt(b -> b.getWeight())
 *                               .sum();
 * }</pre>
 *
 * <p>The only difference between the serial and parallel versions of this
 * example is the creation of the initial stream, using "{@code parallelStream()}"
 * instead of "{@code stream()}".  When the terminal operation is initiated,
 * the stream pipeline is executed sequentially or in parallel depending on the
 * orientation of the stream on which it is invoked.  Whether a stream will execute in serial or
 * parallel can be determined with the {@code isParallel()} method, and the
 * orientation of a stream can be modified with the
 * {@link java.util.stream.BaseStream#sequential()} and
 * {@link java.util.stream.BaseStream#parallel()} operations.  When the terminal
 * operation is initiated, the stream pipeline is executed sequentially or in
 * parallel depending on the mode of the stream on which it is invoked.
 * 这个例子的串行和并行版本之间的唯一区别是初始数据流的创建，使用parallelStream()而不是stream()。
 * 当终结操作启动时，根据调用它的数据流的方向，数据流管道顺序或并行执行。
 * 可以使用isParallel()方法确定数据流是串行执行还是并行执行，可以使用BaseStream.sequential()和BaseStream.parallel()操作修改数据流的方向。
 * 当终结操作启动时，根据调用它的数据流的模式，数据流管道是顺序执行还是并行执行。
 *
 * <p>Except for operations identified as explicitly nondeterministic, such
 * as {@code findAny()}, whether a stream executes sequentially or in parallel
 * should not change the result of the computation.
 * 除findAny()等显式不确定的操作外，数据流是顺序执行还是并行执行，都不应该改变计算的结果。
 *
 * <p>Most stream operations accept parameters that describe user-specified
 * behavior, which are often lambda expressions.  To preserve correct behavior,
 * these <em>behavioral parameters</em> must be <em>non-interfering</em>, and in
 * most cases must be <em>stateless</em>.  Such parameters are always instances
 * of a <a href="../function/package-summary.html">functional interface</a> such
 * as {@link java.util.function.Function}, and are often lambda expressions or
 * method references.
 * 注意：大多数数据流操作接收描述用户指定行为的参数，这些参数通常是lambda表达式。
 * 为了保持正确的行为，这些行为参数必须是不干扰的，并且在大多数情况下必须是无状态的。
 * 这样的参数总是函数式接口的实例，如Function，这些通常是lambda表达式或方法引用。
 *
 * <h3><a name="NonInterference">Non-interference 无干扰</a></h3>
 *
 * Streams enable you to execute possibly-parallel aggregate operations over a
 * variety of data sources, including even non-thread-safe collections such as
 * {@code ArrayList}. This is possible only if we can prevent
 * <em>interference</em> with the data source during the execution of a stream
 * pipeline.  Except for the escape-hatch operations {@code iterator()} and
 * {@code spliterator()}, execution begins when the terminal operation is
 * invoked, and ends when the terminal operation completes.  For most data
 * sources, preventing interference means ensuring that the data source is
 * <em>not modified at all</em> during the execution of the stream pipeline.
 * The notable exception to this are streams whose sources are concurrent
 * collections, which are specifically designed to handle concurrent modification.
 * Concurrent stream sources are those whose {@code Spliterator} reports the
 * {@code CONCURRENT} characteristic.
 * 数据流使您能够在各种数据源上执行可能并行的聚合操作，甚至包括非线程安全的集合，如ArrayList。
 * 只有当我们能够在数据流管道的执行过程中防止对数据源的干扰时，这才可能实现。
 * 除了逃生舱口操作iterator()和spliterator()之外，执行在调用终结操作时开始，在终结操作完成时结束。
 * 对于大多数数据源，防止干扰意味着确保在数据流管道执行期间，数据源没有被修改。
 * 值得注意的例外是数据源是并发集合的数据流，它们是专门设计来处理并发修改的。
 * 并发的数据流源是那些拆分器报告并发特性的。
 *
 * <p>Accordingly, behavioral parameters in stream pipelines whose source might
 * not be concurrent should never modify the stream's data source.
 * A behavioral parameter is said to <em>interfere</em> with a non-concurrent
 * data source if it modifies, or causes to be
 * modified, the stream's data source.  The need for non-interference applies
 * to all pipelines, not just parallel ones.  Unless the stream source is
 * concurrent, modifying a stream's data source during execution of a stream
 * pipeline can cause exceptions, incorrect answers, or non-conformant behavior.
 * 因此，数据流管道中的行为参数，在数据流源可能不是并发的，不应该修改数据流的数据源。
 * 如果非并发数据源修改或导致被修改数据流的数据源，则行为参数会干扰这个数据源。
 * 不受干扰的要求适用于所有管道，而不仅仅是并发管道。
 * 除非数据流源是并发的，否则在数据流管道执行期间修改数据流的数据源可能会导致异常、不正确的答案或不一致的行为。
 *
 * For well-behaved stream sources, the source can be modified before the
 * terminal operation commences and those modifications will be reflected in
 * the covered elements.
 * 对于行为良好的数据流源，可以在终结操作开始之前修改数据源，这些修改将反映在被覆盖的元素中。
 * For example, consider the following code:
 *
 * <pre>{@code
 *     List<String> l = new ArrayList(Arrays.asList("one", "two"));
 *     Stream<String> sl = l.stream();
 *     l.add("three");
 *     String s = sl.collect(joining(" "));
 * }</pre>
 *
 * First a list is created consisting of two strings: "one"; and "two". Then a
 * stream is created from that list. Next the list is modified by adding a third
 * string: "three". Finally the elements of the stream are collected and joined
 * together. Since the list was modified before the terminal {@code collect}
 * operation commenced the result will be a string of "one two three". All the
 * streams returned from JDK collections, and most other JDK classes,
 * are well-behaved in this manner; for streams generated by other libraries, see
 * <a href="package-summary.html#StreamSources">Low-level stream
 * construction</a> for requirements for building well-behaved streams.
 * 对于其他库生成的数据流，请参见构建行为良好的数据流的需求的底层数据流构造。
 *
 * <h3><a name="Statelessness">Stateless behaviors 无状态行为</a></h3>
 *
 * Stream pipeline results may be nondeterministic or incorrect if the behavioral
 * parameters to the stream operations are <em>stateful</em>.  A stateful lambda
 * (or other object implementing the appropriate functional interface) is one
 * whose result depends on any state which might change during the execution
 * of the stream pipeline.
 * 注意：如果数据流操作的行为参数是有状态的，那么数据流管道的结果可能是不确定的或不正确的。
 * 有状态的lambda的结果取决于在数据流管道执行期间可能改变的任何状态。(或其他实现适当功能接口的对象)
 * An example of a stateful lambda is the parameter
 * to {@code map()} in:
 *
 * <pre>{@code
 *     Set<Integer> seen = Collections.synchronizedSet(new HashSet<>());
 *     stream.parallel().map(e -> { if (seen.add(e)) return 0; else return e; })...
 * }</pre>
 *
 * Here, if the mapping operation is performed in parallel, the results for the
 * same input could vary from run to run, due to thread scheduling differences,
 * whereas, with a stateless lambda expression the results would always be the
 * same.
 * 在这里，如果并行执行映射操作，由于线程调度的差异，相同输入的结果可能会因运行的不同而不同，
 * 然而使用无状态lambda表达式，结果总是相同的。
 *
 * <p>Note also that attempting to access mutable state from behavioral parameters
 * presents you with a bad choice with respect to safety and performance; if
 * you do not synchronize access to that state, you have a data race and
 * therefore your code is broken, but if you do synchronize access to that
 * state, you risk having contention undermine the parallelism you are seeking
 * to benefit from.  The best approach is to avoid stateful behavioral
 * parameters to stream operations entirely; there is usually a way to
 * restructure the stream pipeline to avoid statefulness.
 * 还要注意的是，试图从行为参数中访问可变状态会让你在安全性和性能方面做出糟糕的选择；
 * 如果不同步对这个状态的访问，就会出现数据竞争，因此代码就会崩溃。
 * 但如果同步对这个状态的访问，就会有争用破坏并行性的风险，而并行性正式您希望从中受益的。
 * 最好的方法是完全避免数据流操作的有状态行为参数；通常有一种方法可以重构数据流管道以避免有状态。
 *
 * <h3>Side-effects 副作用</h3>
 *
 * Side-effects in behavioral parameters to stream operations are, in general,
 * discouraged, as they can often lead to unwitting violations of the
 * statelessness requirement, as well as other thread-safety hazards.
 * 注意：数据流操作的行为参数的副作用通常是不鼓励的，因为它们经常会导致不知情地违反无状态需求，以及其他线程安全隐患。
 *
 * <p>If the behavioral parameters do have side-effects, unless explicitly
 * stated, there are no guarantees as to the
 * <a href="../concurrent/package-summary.html#MemoryVisibility"><i>visibility</i></a>
 * of those side-effects to other threads, nor are there any guarantees that
 * different operations on the "same" element within the same stream pipeline
 * are executed in the same thread.  Further, the ordering of those effects
 * may be surprising.  Even when a pipeline is constrained to produce a
 * <em>result</em> that is consistent with the encounter order of the stream
 * source (for example, {@code IntStream.range(0,5).parallel().map(x -> x*2).toArray()}
 * must produce {@code [0, 2, 4, 6, 8]}), no guarantees are made as to the order
 * in which the mapper function is applied to individual elements, or in what
 * thread any behavioral parameter is executed for a given element.
 * 如果行为参数确实有副作用，除非明确声明，否则不能保证这些副作用对其他线程的可见性，
 * 也不能保证对同一个数据流管道中同一元素的不同操作在同一线程中执行。
 * 此外，这些影响的顺序可能令人惊讶，即使管道被限制为生成与数据流源遇到顺序一致的结果，
 * 不保证映射函数应用于单个元素的顺序，也不保证在哪个线程中执行给定元素的任何行为参数。
 *
 * <p>Many computations where one might be tempted to use side effects can be more
 * safely and efficiently expressed without side-effects, such as using
 * <a href="package-summary.html#Reduction">reduction</a> instead of mutable
 * accumulators. However, side-effects such as using {@code println()} for debugging
 * purposes are usually harmless.  A small number of stream operations, such as
 * {@code forEach()} and {@code peek()}, can operate only via side-effects;
 * these should be used with care.
 * 许多可能会使用副作用的计算可以更安全、更有效地表示，而不会产生副作用，例如使用归约而不是可变累加器。
 * 但是，使用println()进行调式的副作用通常是无害的。
 * 少数数据流操作只能通过副作用进行操作，如forEach()何peek()，这些应该小心使用。
 *
 * <p>As an example of how to transform a stream pipeline that inappropriately
 * uses side-effects to one that does not, the following code searches a stream
 * of strings for those matching a given regular expression, and puts the
 * matches in a list.
 * 作为如何将不适当使用副作用的数据流管道转换为不使用副作用的数据流管道的示例，
 * 下面的代码在字符串流中搜索匹配给定正则表达式的字符串，并将匹配的字符串放入列表中。
 *
 * <pre>{@code
 *     ArrayList<String> results = new ArrayList<>();
 *     stream.filter(s -> pattern.matcher(s).matches())
 *           .forEach(s -> results.add(s));  // Unnecessary use of side-effects! 不必要的副作用！
 * }</pre>
 *
 * This code unnecessarily uses side-effects.  If executed in parallel, the
 * non-thread-safety of {@code ArrayList} would cause incorrect results, and
 * adding needed synchronization would cause contention, undermining the
 * benefit of parallelism.  Furthermore, using side-effects here is completely
 * unnecessary; the {@code forEach()} can simply be replaced with a reduction
 * operation that is safer, more efficient, and more amenable to
 * parallelization:
 * 这段代码不必要地使用了副作用。如果并行执行，ArrayList的非线程安全性将导致不正确的结果，添加需要的同步将导致争用，削弱并行的好处。
 * 此外，在这里使用副作用是完全没有必要的，forEach()可以简单地用一个更安全、更高效、更适合并行化的归约操作来替换。
 *
 * <pre>{@code
 *     List<String> results =
 *         stream.filter(s -> pattern.matcher(s).matches())
 *               .collect(Collectors.toList());  // No side-effects! 没有副作用！
 * }</pre>
 *
 * <h3><a name="Ordering">Ordering 排序</a></h3>
 *
 * <p>Streams may or may not have a defined <em>encounter order</em>.  Whether
 * or not a stream has an encounter order depends on the source and the
 * intermediate operations.  Certain stream sources (such as {@code List} or
 * arrays) are intrinsically ordered, whereas others (such as {@code HashSet})
 * are not.  Some intermediate operations, such as {@code sorted()}, may impose
 * an encounter order on an otherwise unordered stream, and others may render an
 * ordered stream unordered, such as {@link java.util.stream.BaseStream#unordered()}.
 * Further, some terminal operations may ignore encounter order, such as
 * {@code forEach()}.
 * 注意：数据流可能有也可能没有定义的<em>遇到顺序</em>。
 * 数据流是否具有遇到顺序取决于数据源操作和中间操作。
 * 某些数据流源本质上是有序的，如列表或数组，而其他数据流源则不是，如映射和集合。
 * 一些中间操作可能会对无序数据流施加遇到顺序，而其他操作可能会将有序数据流呈现为无序，如BaseStream.unordered()。
 * 此外，一些终结操作可能会忽略遇到顺序，如forEach()。
 *
 * <p>If a stream is ordered, most operations are constrained to operate on the
 * elements in their encounter order; if the source of a stream is a {@code List}
 * containing {@code [1, 2, 3]}, then the result of executing {@code map(x -> x*2)}
 * must be {@code [2, 4, 6]}.  However, if the source has no defined encounter
 * order, then any permutation of the values {@code [2, 4, 6]} would be a valid
 * result.
 * 如果数据流是有序的，那么大多数操作都被限制为按照遇到的顺序操作元素。
 * 如果一个数据流源是一个包含[1,2,3]的列表，那么执行map(x -> x*2)的结果必须是[2,4,6]。
 * 但是，如果数据流源没有定义遇到顺序，那么值[2,4,6]的任何排列都是有效的结果。
 *
 * <p>For sequential streams, the presence or absence of an encounter order does
 * not affect performance, only determinism.  If a stream is ordered, repeated
 * execution of identical stream pipelines on an identical source will produce
 * an identical result; if it is not ordered, repeated execution might produce
 * different results.
 * 对于顺序数据流，是否存在遇到顺序并不影响性能，而只是确定性。
 * 如果数据流是有序的，在相同的数据源上重复执行相同的数据流管道将产生相同的结果；
 * 如果没有排序，重复执行可能会产生不同的结果。
 *
 * <p>For parallel streams, relaxing the ordering constraint can sometimes enable
 * more efficient execution.  Certain aggregate operations,
 * such as filtering duplicates ({@code distinct()}) or grouped reductions
 * ({@code Collectors.groupingBy()}) can be implemented more efficiently if ordering of elements
 * is not relevant.  Similarly, operations that are intrinsically tied to encounter order,
 * such as {@code limit()}, may require
 * buffering to ensure proper ordering, undermining the benefit of parallelism.
 * In cases where the stream has an encounter order, but the user does not
 * particularly <em>care</em> about that encounter order, explicitly de-ordering
 * the stream with {@link java.util.stream.BaseStream#unordered() unordered()} may
 * improve parallel performance for some stateful or terminal operations.
 * However, most stream pipelines, such as the "sum of weight of blocks" example
 * above, still parallelize efficiently even under ordering constraints.
 * 对于并行数据流，放松排序约束有时可以实现更高效地执行。
 * 如果元素的排序不相关，则可以更有效地实现某些聚合操作，如过滤重复项distinct()或分组归约Collectors.groupingBy()。
 * 类似地，本质上与遇到顺序相关的操作，如limit()，可能需要缓冲以确保正确的顺序，这削弱了并行性的好处。
 * 如果数据流有遇到顺序，但用户并不特别关心遇到顺序，那么使用unordered()显式地取消数据流的顺序可能会提高某些有状态或终结操作的并行性能。
 * 然而，大多数数据流管道，即使在排序约束下仍然可以高效地并行化。
 *
 * <h2><a name="Reduction">Reduction operations 归约操作</a></h2>
 *
 * A <em>reduction</em> operation (also called a <em>fold</em>) takes a sequence
 * of input elements and combines them into a single summary result by repeated
 * application of a combining operation, such as finding the sum or maximum of
 * a set of numbers, or accumulating elements into a list.  The streams classes have
 * multiple forms of general reduction operations, called
 * {@link java.util.stream.Stream#reduce(java.util.function.BinaryOperator) reduce()}
 * and {@link java.util.stream.Stream#collect(java.util.stream.Collector) collect()},
 * as well as multiple specialized reduction forms such as
 * {@link java.util.stream.IntStream#sum() sum()}, {@link java.util.stream.IntStream#max() max()},
 * or {@link java.util.stream.IntStream#count() count()}.
 * 归约操作接受一系列输入元素，并通过重复应用组合操作将它们组合成单个汇总结果。
 * 例如查找一组数字的和或最大值，或将元素累加到一个列表中。
 * 数据流类有多种形式的通用归约操作，称为reduce()和collect()，以及多种专门化的归约形式，如sum()、max()或count()。
 *
 * <p>Of course, such operations can be readily implemented as simple sequential
 * loops, as in:
 * <pre>{@code
 *    int sum = 0;
 *    for (int x : numbers) {
 *       sum += x;
 *    }
 * }</pre>
 * However, there are good reasons to prefer a reduce operation
 * over a mutative accumulation such as the above.  Not only is a reduction
 * "more abstract" -- it operates on the stream as a whole rather than individual
 * elements -- but a properly constructed reduce operation is inherently
 * parallelizable, so long as the function(s) used to process the elements
 * are <a href="package-summary.html#Associativity">associative</a> and
 * <a href="package-summary.html#NonInterfering">stateless</a>.
 * 然而，有充分的理由选择归约操作，而不是像上面那样的突变累积。
 * 不仅归约更抽象——它作为一个整体而不是单个元素对数据流进行操作——
 * 而且正确构造的归约操作在本质上是可并行的，只要用于处理元素的函数是关联的和无状态的。
 * For example, given a stream of numbers for which we want to find the sum, we
 * can write:
 * <pre>{@code
 *    int sum = numbers.stream().reduce(0, (x,y) -> x+y);
 * }</pre>
 * or:
 * <pre>{@code
 *    int sum = numbers.stream().reduce(0, Integer::sum);
 * }</pre>
 *
 * <p>These reduction operations can run safely in parallel with almost no
 * modification:
 * 这些归约操作几乎不需要任何修改就可以安全地并行运行：
 * <pre>{@code
 *    int sum = numbers.parallelStream().reduce(0, Integer::sum);
 * }</pre>
 *
 * <p>Reduction parallellizes well because the implementation
 * can operate on subsets of the data in parallel, and then combine the
 * intermediate results to get the final correct answer.  (Even if the language
 * had a "parallel for-each" construct, the mutative accumulation approach would
 * still required the developer to provide
 * thread-safe updates to the shared accumulating variable {@code sum}, and
 * the required synchronization would then likely eliminate any performance gain from
 * parallelism.)  Using {@code reduce()} instead removes all of the
 * burden of parallelizing the reduction operation, and the library can provide
 * an efficient parallel implementation with no additional synchronization
 * required.
 * 归约具有良好的并行性，因为其实现可以对数据的子集进行并行操作，然后将中间结果组合起来得到最终的正确答案。
 * (即使语言有一个每个并行的构造，可变的累加方法仍然需要开发人员提供线程安全的对共享累加变量总和的更新，并且所需的同步可能会消除并行带来的任何性能增益。)
 * 相反，使用reduce()消除了并行化归约操作的所有负担，库可以提供一个高效的并行实现，而不需要额外的同步。
 *
 * <p>The "widgets" examples shown earlier shows how reduction combines with
 * other operations to replace for loops with bulk operations.  If {@code widgets}
 * is a collection of {@code Widget} objects, which have a {@code getWeight} method,
 * we can find the heaviest widget with:
 * <pre>{@code
 *     OptionalInt heaviest = widgets.parallelStream()
 *                                   .mapToInt(Widget::getWeight)
 *                                   .max();
 * }</pre>
 *
 * <p>In its more general form, a {@code reduce} operation on elements of type
 * {@code <T>} yielding a result of type {@code <U>} requires three parameters:
 * 在其更一般的形式中，对类型元素的归约操作产生类型的结果，需要三个参数：
 * <pre>{@code
 * <U> U reduce(U identity,
 *              BiFunction<U, ? super T, U> accumulator,
 *              BinaryOperator<U> combiner);
 * }</pre>
 * Here, the <em>identity</em> element is both an initial seed value for the reduction
 * and a default result if there are no input elements. The <em>accumulator</em>
 * function takes a partial result and the next element, and produces a new
 * partial result. The <em>combiner</em> function combines two partial results
 * to produce a new partial result.  (The combiner is necessary in parallel
 * reductions, where the input is partitioned, a partial accumulation computed
 * for each partition, and then the partial results are combined to produce a
 * final result.)
 * 这里，标识元素既是简化的初始种子值，也是没有输入元素的默认结果。
 * 累加器函数接受一个部分结果和下一个元素，并产生一个新的部分结果。
 * 合并函数将两个局部结果组合起来产生一个新的局部结果。
 * (合并器在并行归约中是必要的，其中输入被分割，为每个分割计算部分累加，然后部分结果被合并以产生最终结果。)
 *
 * <p>More formally, the {@code identity} value must be an <em>identity</em> for
 * the combiner function. This means that for all {@code u},
 * {@code combiner.apply(identity, u)} is equal to {@code u}. Additionally, the
 * {@code combiner} function must be <a href="package-summary.html#Associativity">associative</a> and
 * must be compatible with the {@code accumulator} function: for all {@code u}
 * and {@code t}, {@code combiner.apply(u, accumulator.apply(identity, t))} must
 * be {@code equals()} to {@code accumulator.apply(u, t)}.
 * 更正式的说法是，标识值必须是组合函数的标识。
 * 这意味着对于所有的u，都是合成器，combiner.apply(identity, u) = u。
 * 此外，组合函数必须是结合的，必须与累加函数兼容：对于所有u和t，都是组合函数。
 *
 * <p>The three-argument form is a generalization of the two-argument form,
 * incorporating a mapping step into the accumulation step.  We could
 * re-cast the simple sum-of-weights example using the more general form as
 * follows:
 * <pre>{@code
 *     int sumOfWeights = widgets.stream()
 *                               .reduce(0,
 *                                       (sum, b) -> sum + b.getWeight())
 *                                       Integer::sum);
 * }</pre>
 * though the explicit map-reduce form is more readable and therefore should
 * usually be preferred. The generalized form is provided for cases where
 * significant work can be optimized away by combining mapping and reducing
 * into a single function.
 * 尽管显式的映射-归约形式更易于阅读，因此通常应该优先使用。
 * 一般的形式提供了一些情况，这些情况下，通过组合映射和归约为一个单一函数，可以优化大量工作。
 *
 * <h3><a name="MutableReduction">Mutable reduction 可变的归约</a></h3>
 *
 * A <em>mutable reduction operation</em> accumulates input elements into a
 * mutable result container, such as a {@code Collection} or {@code StringBuilder},
 * as it processes the elements in the stream.
 * 当处理数据流中的元素时，<em>可变的归约操作</em>将输入元素累积到可变的结果容器中，
 * 如Collection或StringBuilder。
 *
 * <p>If we wanted to take a stream of strings and concatenate them into a
 * single long string, we <em>could</em> achieve this with ordinary reduction:
 * 如果我们想要获取一个字符串流并将它们连接成一个单独的长字符串，我们可以通过普通归约实现：
 * <pre>{@code
 *     String concatenated = strings.reduce("", String::concat)
 * }</pre>
 *
 * <p>We would get the desired result, and it would even work in parallel.  However,
 * we might not be happy about the performance!  Such an implementation would do
 * a great deal of string copying, and the run time would be <em>O(n^2)</em> in
 * the number of characters.  A more performant approach would be to accumulate
 * the results into a {@link java.lang.StringBuilder}, which is a mutable
 * container for accumulating strings.  We can use the same technique to
 * parallelize mutable reduction as we do with ordinary reduction.
 * 我们会得到想要的结果，它甚至会并行工作。
 * 但是，我们可能对性能不满意。这样的实现将进行大量的字符串复制，运行时间为字符数的O(n^2)。
 * 更高效的方法是将结果累积到StringBuilder中，这是一个用于累积字符串的可变容器。
 * 我们可以使用与普通归约相同的技术来并行化不变归约。
 *
 * <p>The mutable reduction operation is called
 * {@link java.util.stream.Stream#collect(Collector) collect()},
 * as it collects together the desired results into a result container such
 * as a {@code Collection}.
 * A {@code collect} operation requires three functions:
 * a supplier function to construct new instances of the result container, an
 * accumulator function to incorporate an input element into a result
 * container, and a combining function to merge the contents of one result
 * container into another.
 * 可变的归约操作称为collect()，因为它将所需的结果收集到一个结果容器中，比如Collection。
 * 收集操作需要三个函数：构建结果容器的新实例的提供者函数，将输入元素合并到结果容器中的累加器函数，
 * 以及将一个结果容器的内容合并到另一个结果容器中的组合函数。
 * The form of this is very similar to the general
 * form of ordinary reduction:
 * 这个形式与普通归约的一般形式非常相似：
 * <pre>{@code
 * <R> R collect(Supplier<R> supplier,
 *               BiConsumer<R, ? super T> accumulator,
 *               BiConsumer<R, R> combiner);
 * }</pre>
 * <p>As with {@code reduce()}, a benefit of expressing {@code collect} in this
 * abstract way is that it is directly amenable to parallelization: we can
 * accumulate partial results in parallel and then combine them, so long as the
 * accumulation and combining functions satisfy the appropriate requirements.
 * 与reduce()一样，以这种抽象的方式表示collect的一个好处是，它直接服从于并行化：
 * 我们可以并行地积累部分结果，然后将它们组合起来，只要积累和组合函数满足适当的需求。
 * For example, to collect the String representations of the elements in a
 * stream into an {@code ArrayList}, we could write the obvious sequential
 * for-each form:
 * <pre>{@code
 *     ArrayList<String> strings = new ArrayList<>();
 *     for (T element : stream) {
 *         strings.add(element.toString());
 *     }
 * }</pre>
 * Or we could use a parallelizable collect form:
 * <pre>{@code
 *     ArrayList<String> strings = stream.collect(() -> new ArrayList<>(),
 *                                                (c, e) -> c.add(e.toString()),
 *                                                (c1, c2) -> c1.addAll(c2));
 * }</pre>
 * or, pulling the mapping operation out of the accumulator function, we could
 * express it more succinctly as:
 * <pre>{@code
 *     List<String> strings = stream.map(Object::toString)
 *                                  .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
 * }</pre>
 * Here, our supplier is just the {@link java.util.ArrayList#ArrayList()
 * ArrayList constructor}, the accumulator adds the stringified element to an
 * {@code ArrayList}, and the combiner simply uses {@link java.util.ArrayList#addAll addAll}
 * to copy the strings from one container into the other.
 *
 * <p>The three aspects of {@code collect} -- supplier, accumulator, and
 * combiner -- are tightly coupled.  We can use the abstraction of a
 * {@link java.util.stream.Collector} to capture all three aspects.
 * 收集的三个方面——提供者、累加器和合并器函数——是紧密耦合的，我们可以使用收集器的抽象来捕获这三个方面。
 * The above example for collecting strings into a {@code List} can be rewritten
 * using a standard {@code Collector} as:
 * <pre>{@code
 *     List<String> strings = stream.map(Object::toString)
 *                                  .collect(Collectors.toList());
 * }</pre>
 *
 * <p>Packaging mutable reductions into a Collector has another advantage:
 * composability.  The class {@link java.util.stream.Collectors} contains a
 * number of predefined factories for collectors, including combinators
 * that transform one collector into another.
 * 将可变归约封装到收集器中还有另一个优点：可组合性。
 * Collectors类包含许多预定义的收集器工厂，包括将一个收集器转换为另一个收集器的组合函数。
 * For example, suppose we have a
 * collector that computes the sum of the salaries of a stream of
 * employees, as follows:
 *
 * <pre>{@code
 *     Collector<Employee, ?, Integer> summingSalaries
 *         = Collectors.summingInt(Employee::getSalary);
 * }</pre>
 *
 * (The {@code ?} for the second type parameter merely indicates that we don't
 * care about the intermediate representation used by this collector.)
 * If we wanted to create a collector to tabulate the sum of salaries by
 * department, we could reuse {@code summingSalaries} using
 * {@link java.util.stream.Collectors#groupingBy(java.util.function.Function, java.util.stream.Collector) groupingBy}:
 *
 * <pre>{@code
 *     Map<Department, Integer> salariesByDept
 *         = employees.stream().collect(Collectors.groupingBy(Employee::getDepartment,
 *                                                            summingSalaries));
 * }</pre>
 *
 * <p>As with the regular reduction operation, {@code collect()} operations can
 * only be parallelized if appropriate conditions are met.  For any partially
 * accumulated result, combining it with an empty result container must
 * produce an equivalent result.  That is, for a partially accumulated result
 * {@code p} that is the result of any series of accumulator and combiner
 * invocations, {@code p} must be equivalent to
 * {@code combiner.apply(p, supplier.get())}.
 * 与常规归约操作一样，collect()操作只能在满足适当条件的情况下被并行化。
 * 对于任何部分累积的结果，将其与空的结果容器组合必须产生一个等效的结果。
 * 也就是说，对于一个部分累加的结果p，它是任何一系列累加器和组合器调用的结果，p必须等于组合器。
 *
 * <p>Further, however the computation is split, it must produce an equivalent
 * result.
 * 此外，无论计算是分裂的，它必须产生一个等效的结果。
 * For any input elements {@code t1} and {@code t2}, the results
 * {@code r1} and {@code r2} in the computation below must be equivalent:
 * 对于任意输入元素t1和t2，下面的计算的结果r1和r2必须相等：
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
 * }</pre>
 *
 * <p>Here, equivalence generally means according to {@link java.lang.Object#equals(Object)}.
 * but in some cases equivalence may be relaxed to account for differences in
 * order.
 * 在这里，等价通常意味着根据Object.equals(Object)。
 * 但在某些情况下，为了说明顺序上的差异，等效性可以放宽。
 *
 * <h3><a name="ConcurrentReduction">Reduction, concurrency, and ordering 归约、并发性和排序</a></h3>
 *
 * With some complex reduction operations, for example a {@code collect()} that
 * produces a {@code Map}, such as:
 * <pre>{@code
 *     Map<Buyer, List<Transaction>> salesByBuyer
 *         = txns.parallelStream()
 *               .collect(Collectors.groupingBy(Transaction::getBuyer));
 * }</pre>
 * it may actually be counterproductive to perform the operation in parallel.
 * This is because the combining step (merging one {@code Map} into another by
 * key) can be expensive for some {@code Map} implementations.
 * 并行执行操作实际上可能会适得其反。这是因为对于某些Map实现来说，合并步骤可能非常昂贵。(通过键将一个Map合并到另一个Map)
 *
 * <p>Suppose, however, that the result container used in this reduction
 * was a concurrently modifiable collection -- such as a
 * {@link java.util.concurrent.ConcurrentHashMap}. In that case, the parallel
 * invocations of the accumulator could actually deposit their results
 * concurrently into the same shared result container, eliminating the need for
 * the combiner to merge distinct result containers. This potentially provides
 * a boost to the parallel execution performance. We call this a
 * <em>concurrent</em> reduction.
 * 但是，假设这个归约中使用的结果容器是一个可并发修改的集合，如ConcurrentHashMap。
 * 在这种情况下，对累加器的并行调用实际上可以将它们的结果并发地存储到同一个共享结果容器中，从而消除了合并器合并不同结果容器的需要。
 * 这可能会提高并行执行性能，我们称之为并发归约。
 *
 * <p>A {@link java.util.stream.Collector} that supports concurrent reduction is
 * marked with the {@link java.util.stream.Collector.Characteristics#CONCURRENT}
 * characteristic.  However, a concurrent collection also has a downside.  If
 * multiple threads are depositing results concurrently into a shared container,
 * the order in which results are deposited is non-deterministic. Consequently,
 * a concurrent reduction is only possible if ordering is not important for the
 * stream being processed.
 * 支持并发归约的收集器被标记为并发特性。然而，并发收集也有缺点。
 * 如果多个线程并发地将结果存储到一个共享容器中，那么存储结果的顺序是不确定的。
 * 因此，只有在顺序对正在处理的数据流不重要的情况下，才可能进行并发归约。
 * The {@link java.util.stream.Stream#collect(Collector)}
 * implementation will only perform a concurrent reduction if
 * Stream.collect(Collector)实现只在以下情况下执行并发归约
 * <ul>
 * <li>The stream is parallel;</li>
 * 数据流是平行的；
 * <li>The collector has the
 * {@link java.util.stream.Collector.Characteristics#CONCURRENT} characteristic,
 * and;</li>
 * 收集器具有并发特性；
 * <li>Either the stream is unordered, or the collector has the
 * {@link java.util.stream.Collector.Characteristics#UNORDERED} characteristic.</li>
 * 要么数据流是无序的，要么收集器具有无序特性。
 * </ul>
 * You can ensure the stream is unordered by using the
 * {@link java.util.stream.BaseStream#unordered()} method.
 * 您可以使用BaseStream.unordered()方法来确保数据流是无序的。
 * For example:
 * <pre>{@code
 *     Map<Buyer, List<Transaction>> salesByBuyer
 *         = txns.parallelStream()
 *               .unordered()
 *               .collect(groupingByConcurrent(Transaction::getBuyer));
 * }</pre>
 * (where {@link java.util.stream.Collectors#groupingByConcurrent} is the
 * concurrent equivalent of {@code groupingBy}).
 *
 * <p>Note that if it is important that the elements for a given key appear in
 * the order they appear in the source, then we cannot use a concurrent
 * reduction, as ordering is one of the casualties of concurrent insertion.
 * We would then be constrained to implement either a sequential reduction or
 * a merge-based parallel reduction.
 * 请注意，如果给定键的元素必须按照它们在数据源中出现的顺序，要么我们就不能使用并发归约，因为顺序是并发插入的一个问题。
 * 然后，我们将受到限制，要么实现顺序归约，要么实现基于合并的并行归约。
 *
 * <h3><a name="Associativity">Associativity 关联性</a></h3>
 *
 * An operator or function {@code op} is <em>associative</em> if the following
 * holds:
 * 如果满足以下条件，则运算符或函数op是关联的：
 * <pre>{@code
 *     (a op b) op c == a op (b op c)
 * }</pre>
 * The importance of this to parallel evaluation can be seen if we expand this
 * to four terms:
 * <pre>{@code
 *     a op b op c op d == (a op b) op (c op d)
 * }</pre>
 * So we can evaluate {@code (a op b)} in parallel with {@code (c op d)}, and
 * then invoke {@code op} on the results.
 *
 * <p>Examples of associative operations include numeric addition, min, and
 * max, and string concatenation.
 * 关联运算的示例包括数字加法、最小值和最大值以及字符串拼接。
 *
 * <h2><a name="StreamSources">Low-level stream construction 底层数据流构建</a></h2>
 *
 * So far, all the stream examples have used methods like
 * {@link java.util.Collection#stream()} or {@link java.util.Arrays#stream(Object[])}
 * to obtain a stream.  How are those stream-bearing methods implemented?
 * 到目前为止，所有数据流示例都使用Collection.stream()或Arrays.stream(Object[])等方法来获取数据流。
 * 这些数据流承载方法是如何实现的？
 *
 * <p>The class {@link java.util.stream.StreamSupport} has a number of
 * low-level methods for creating a stream, all using some form of a
 * {@link java.util.Spliterator}. A spliterator is the parallel analogue of an
 * {@link java.util.Iterator}; it describes a (possibly infinite) collection of
 * elements, with support for sequentially advancing, bulk traversal, and
 * splitting off some portion of the input into another spliterator which can
 * be processed in parallel.  At the lowest level, all streams are driven by a
 * spliterator.
 * StreamSupport类有许多用于创建数据流的底层方法，它们都使用某种形式的拆分器Spliterator。
 * 拆分器是迭代器的并行类似物，它描述了一个(可能是无限的)元素集合，支持顺序推进、批量遍历，以及将输入的某些部分拆分到另一个可以并行处理的拆分器中。
 * 在最低级别，所有数据流都由拆分器驱动。
 *
 * <p>There are a number of implementation choices in implementing a
 * spliterator, nearly all of which are tradeoffs between simplicity of
 * implementation and runtime performance of streams using that spliterator.
 * The simplest, but least performant, way to create a spliterator is to
 * create one from an iterator using
 * {@link java.util.Spliterators#spliteratorUnknownSize(java.util.Iterator, int)}.
 * While such a spliterator will work, it will likely offer poor parallel
 * performance, since we have lost sizing information (how big is the
 * underlying data set), as well as being constrained to a simplistic
 * splitting algorithm.
 * 在实现拆分器时有许多实现选择，几乎所有这些选择都是在实现的简单性和使用拆分器的数据流的运行时性能之间进行权衡。
 * 创建拆分器的最简单但性能最低的方法是使用Spliterators.spliteratorUnknownSize(Iterator, int)从迭代器创建一个。
 * 虽然这样的拆分器会起作用，但它可能会提供较差的并行性能，因为我们丢失了大小信息(底层数据集有多大)，并且受限于简单的拆分算法。
 *
 * <p>A higher-quality spliterator will provide balanced and known-size
 * splits, accurate sizing information, and a number of other
 * {@link java.util.Spliterator#characteristics() characteristics} of the
 * spliterator or data that can be used by implementations to optimize
 * execution.
 * 更高质量的拆分器将提供平衡且已知大小的拆分、准确的大小信息，以及拆分器或数据的许多其他特征，实现可用于优化执行。
 *
 * <p>Spliterators for mutable data sources have an additional challenge;
 * timing of binding to the data, since the data could change between the time
 * the spliterator is created and the time the stream pipeline is executed.
 * Ideally, a spliterator for a stream would report a characteristic of
 * {@code IMMUTABLE} or {@code CONCURRENT}; if not it should be
 * <a href="../Spliterator.html#binding"><em>late-binding</em></a>. If a source
 * cannot directly supply a recommended spliterator, it may indirectly supply
 * a spliterator using a {@code Supplier}, and construct a stream via the
 * {@code Supplier}-accepting versions of
 * {@link java.util.stream.StreamSupport#stream(Supplier, int, boolean) stream()}.
 * The spliterator is obtained from the supplier only after the terminal
 * operation of the stream pipeline commences.
 * 可变数据源的拆分器有一个额外的挑战，绑定到数据的时间，因为数据可能会在创建拆分器的时间和执行数据流管道的时间之间发生变化。
 * 理想情况下，数据流的拆分器将报告不可变或并发的特征；如果不是，它应该是后期绑定。
 * 如果数据源不能直接提供推荐的拆分器，它可以使用提供者间接提供拆分器，并通过提供者接受版本的stream()构造数据流。
 * 只有在数据流管道的终结操作开始后，才能从提供者处获得拆分器。
 *
 * <p>These requirements significantly reduce the scope of potential
 * interference between mutations of the stream source and execution of stream
 * pipelines. Streams based on spliterators with the desired characteristics,
 * or those using the Supplier-based factory forms, are immune to
 * modifications of the data source prior to commencement of the terminal
 * operation (provided the behavioral parameters to the stream operations meet
 * the required criteria for non-interference and statelessness).  See
 * <a href="package-summary.html#NonInterference">Non-Interference</a>
 * for more details.
 * 这些要求显著减少了数据流可变和数据流管道执行之间的潜在干扰范围。
 * 基于具有所需特性的拆分器的数据流，或使用基于提供者的工厂表单的数据流，在终结操作开始之前不受数据源修改的影响。
 * (前提是数据流操作的行为参数满足不干扰和无状态)
 * 有关详细信息，请参阅不干扰。
 *
 * @since 1.8
 */
package java.util.stream;

import java.util.function.UnaryOperator;
