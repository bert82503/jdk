
/**
 * <em>Functional interfaces</em> provide target types for lambda expressions
 * and method references.  Each functional interface has a single abstract
 * method, called the <em>functional method</em> for that functional interface,
 * to which the lambda expression's parameter and return types are matched or
 * adapted.  Functional interfaces can provide a target type in multiple
 * contexts, such as assignment context, method invocation, or cast context:
 *
 * <pre>{@code
 *     // Assignment context 赋值上下文/方法引用
 *     Predicate<String> p = String::isEmpty;
 *
 *     // Method invocation context 方法调用上下文
 *     stream.filter(e -> e.getSize() > 10)...
 *
 *     // Cast context 转换上下文
 *     stream.map((ToIntFunction) e -> e.getSize())...
 * }</pre>
 * <em>函数式接口</em>提供lambda表达式和方法引用的目标类型。
 * 每个函数式接口有一个单一的抽象方法，称为<em>函数方法</em>。
 * 函数方法可以匹配或适配为lambda表达式的参数和返回类型。
 * 函数式接口可以在多个上下文中提供一个目标类型，如赋值上下文、方法调用、转换上下文。
 *
 * <p>The interfaces in this package are general purpose functional interfaces
 * used by the JDK, and are available to be used by user code as well.  While
 * they do not identify a complete set of function shapes to which lambda
 * expressions might be adapted, they provide enough to cover common
 * requirements. Other functional interfaces provided for specific purposes,
 * such as {@link java.io.FileFilter}, are defined in the packages where they
 * are used.
 * 本包中的接口是通用的函数式接口。
 * 虽然并不确定是一套完整的功能集，适配各种lambda表达式形式，但它们提供足够的共同要求。
 * 其它提供特定用途的函数式接口定义在使用的包中。
 *
 * <p>The interfaces in this package are annotated with
 * {@link java.lang.FunctionalInterface}. This annotation is not a requirement
 * for the compiler to recognize an interface as a functional interface, but
 * merely an aid to capture design intent and enlist the help of the compiler in
 * identifying accidental violations of design intent.
 * 本包中的接口都使用@{@link java.lang.FunctionalInterface}进行注解。
 * 对编译器而言，该注解并不是必须的，它会识别接口作为函数式接口，
 * 但仅仅有助于捕捉设计意图，并在识别设计意图的意外侵犯中寻求编译器的帮助。
 *
 * <p>Functional interfaces often represent abstract concepts like functions,
 * actions, or predicates.  In documenting functional interfaces, or referring
 * to variables typed as functional interfaces, it is common to refer directly
 * to those abstract concepts, for example using "this function" instead of
 * "the function represented by this object".  When an API method is said to
 * accept or return a functional interface in this manner, such as "applies the
 * provided function to...", this is understood to mean a <i>non-null</i>
 * reference to an object implementing the appropriate functional interface,
 * unless potential nullity is explicitly specified.
 * 函数式接口往往代表抽象的概念，如函数、操作、谓词。
 * 在记录函数式接口或引用函数式接口类型作为变量中，这是常见的直接引用这些抽象的概念。
 * 当一个API方法接受或返回一个函数式接口，这是指一个非null对象的引用，该对象实现相应的函数式接口。
 *
 * <p>The functional interfaces in this package follow an extensible naming
 * convention, as follows:
 *
 * <ul>
 *     <li>There are several basic function shapes, including
 *     {@link java.util.function.Function} (unary function from {@code T} to {@code R}),
 *     {@link java.util.function.Consumer} (unary function from {@code T} to {@code void}),
 *     {@link java.util.function.Predicate} (unary function from {@code T} to {@code boolean}),
 *     and {@link java.util.function.Supplier} (nilary function to {@code T}).
 *     </li>
 *
 *     <li>Function shapes have a natural arity based on how they are most
 *     commonly used.  The basic shapes can be modified by an arity prefix to
 *     indicate a different arity, such as
 *     {@link java.util.function.BiFunction} (binary function from {@code T} and
 *     {@code U} to {@code R}).
 *     </li>
 *
 *     <li>There are additional derived function shapes which extend the basic
 *     function shapes, including {@link java.util.function.UnaryOperator}
 *     (extends {@code Function}) and {@link java.util.function.BinaryOperator}
 *     (extends {@code BiFunction}).
 *     </li>
 *
 *     <li>Type parameters of functional interfaces can be specialized to
 *     primitives with additional type prefixes.  To specialize the return type
 *     for a type that has both generic return type and generic arguments, we
 *     prefix {@code ToXxx}, as in {@link java.util.function.ToIntFunction}.
 *     Otherwise, type arguments are specialized left-to-right, as in
 *     {@link java.util.function.DoubleConsumer}
 *     or {@link java.util.function.ObjIntConsumer}.
 *     (The type prefix {@code Obj} is used to indicate that we don't want to
 *     specialize this parameter, but want to move on to the next parameter,
 *     as in {@link java.util.function.ObjIntConsumer}.)
 *     These schemes can be combined, as in {@code IntToDoubleFunction}.
 *     </li>
 *
 *     <li>If there are specialization prefixes for all arguments, the arity
 *     prefix may be left out (as in {@link java.util.function.ObjIntConsumer}).
 *     </li>
 * </ul>
 * 本包中的函数式接口遵循一个可扩展的命名约定：
 * <ul>
 *     <li>几种基本的函数形式，包括
 *     {@link java.util.function.Supplier}(生产T的函数)、
 *     {@link java.util.function.Function}(从T到R的一元函数)、
 *     {@link java.util.function.Consumer}(从T到void的一元函数)、
 *     {@link java.util.function.Predicate}(从T到boolean的一元函数)</li>
 *
 *     <li>函数形式有基于最常用的天然数量。基本的形式可以修改一个实参数量的前缀或指示一个不同的数量。
 *     如{@link java.util.function.BiFunction}(从T、U到R的二元函数)</li>
 *
 *     <li>有其它派生类的函数形式，它们扩展了基本的函数形式。包括
 *     {@link java.util.function.UnaryOperator}(一元运算符，继承自Function)、
 *     {@link java.util.function.BinaryOperator}(二元运算符，继承自BiFunction)</li>
 *
 *     <li>函数式接口的类型参数可以专门为使用其它类型前缀的基本类型。
 *     专门类型的返回类型，泛型返回类型和泛型参数，在{@link java.util.function.ToIntFunction}中前缀为ToXxx。
 *     类型参数是专门从左到右的，如{@link java.util.function.DoubleConsumer}或{@link java.util.function.ObjIntConsumer}。
 *     这些方案是可以被组合的，如IntToDoubleFunction。</li>
 *
 *     <li>如果所有的参数都有专业化的前缀，则任意的前缀可能被排除。</li>
 * </ul>
 *
 * @see java.lang.FunctionalInterface
 * @since 1.8
 */
package java.util.function;
