
package java.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An informative annotation type used to indicate that an interface
 * type declaration is intended to be a <i>functional interface</i> as
 * defined by the Java Language Specification.
 * 信息注解类型，表示接口类型声明是一个<i>函数式接口</i>。
 *
 * <p>
 * Conceptually, a functional interface has exactly one abstract
 * method.  Since {@linkplain java.lang.reflect.Method#isDefault()
 * default methods} have an implementation, they are not abstract.  If
 * an interface declares an abstract method overriding one of the
 * public methods of {@code java.lang.Object}, that also does
 * <em>not</em> count toward the interface's abstract method count
 * since any implementation of the interface will have an
 * implementation from {@code java.lang.Object} or elsewhere.
 * 从概念上讲，函数式接口都只有一个抽象方法。
 * 因为{@linkplain java.lang.reflect.Method#isDefault() 默认方法}具有实现，他们不是抽象的。
 *
 * <p>Note that instances of functional interfaces can be created with
 * lambda expressions, method references, or constructor references.
 * 请注意，可以使用 Lambda表达式、方法引用和构造器引用 创建函数式接口的实例。
 *
 * <p>If a type is annotated with this annotation type, compilers are
 * required to generate an error message unless:
 *
 * <ul>
 * <li> The type is an interface type and not an annotation type, enum, or class.
 * <li> The annotated type satisfies the requirements of a functional interface.
 * <li> 该类型是一个接口类型，而不是一个注解类型、枚举或类。
 * <li> 注解的类型满足函数式接口的要求。
 * </ul>
 *
 * <p>However, the compiler will treat any interface meeting the
 * definition of a functional interface as a functional interface
 * regardless of whether or not a {@code FunctionalInterface}
 * annotation is present on the interface declaration.
 * 即使接口未标注该注解，编译器也会处理任何满足函数式接口定义的接口作为函数式接口。
 *
 * @jls 4.3.2. The Class Object 类对象
 * @jls 9.8 Functional Interfaces 函数式接口
 * @jls 9.4.3 Interface Method Body 接口方法体
 * @since 1.8
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FunctionalInterface {
    //
}
