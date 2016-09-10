/*
 * Copyright (c) 1998, 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/**
 * Provides classes that are fundamental to the design of the Java
 * programming language. The most important classes are {@code
 * Object}, which is the root of the class hierarchy, and {@code
 * Class}, instances of which represent classes at run time.
 * <p>
 * 提供 Java 编程语言设计的基础类。
 * 最重要的类是 {@link java.lang.Object}，其是类层次结构的根。
 * {@link java.lang.Class} 表示运行时的类实例。
 *
 * <p>Frequently it is necessary to represent a value of primitive
 * type as if it were an object. The wrapper classes {@code Boolean},
 * {@code Character}, {@code Integer}, {@code Long}, {@code Float},
 * and {@code Double} serve this purpose.  An object of type {@code
 * Double}, for example, contains a field whose type is double,
 * representing that value in such a way that a reference to it can be
 * stored in a variable of reference type.  These classes also provide
 * a number of methods for converting among primitive values, as well
 * as supporting such standard methods as equals and hashCode.  The
 * {@code Void} class is a non-instantiable class that holds a
 * reference to a {@code Class} object representing the type void.
 * <p>
 * 基本类型的包装类
 *
 * <p>The class {@code Math} provides commonly used mathematical
 * functions such as sine, cosine, and square root. The classes {@code
 * String}, {@code StringBuffer}, and {@code StringBuilder} similarly
 * provide commonly used operations on character strings.
 * <p>
 * {@link java.lang.String} 和 {@link java.lang.StringBuilder} 提供公共的字符串操作
 *
 * <p>Classes {@code ClassLoader}, {@code Process}, {@code
 * ProcessBuilder}, {@code Runtime}, {@code SecurityManager}, and
 * {@code System} provide "system operations" that manage the dynamic
 * loading of classes, creation of external processes, host
 * environment inquiries such as the time of day, and enforcement of
 * security policies.
 * <p>
 * {@link java.lang.ClassLoader}、{@link java.lang.Runtime}、
 * {@link java.lang.SecurityManager}、{@link java.lang.System} 提供系统操作，
 * 管理类型的动态加载、主机环境调查、安全策略的执行。
 *
 * <p>Class {@code Throwable} encompasses objects that may be thrown
 * by the {@code throw} statement. Subclasses of {@code Throwable}
 * represent errors and exceptions.
 * <p>
 * {@link java.lang.Throwable} 的子类表示错误和异常。
 *
 * <a name="charenc"></a>
 * <h3>Character Encodings (字符编码)</h3>
 *
 * The specification of the {@link java.nio.charset.Charset
 * java.nio.charset.Charset} class describes the naming conventions
 * for character encodings as well as the set of standard encodings
 * that must be supported by every implementation of the Java
 * platform.
 * <p>
 * {@link java.nio.charset.Charset} 类的规范描述字符编码的命名规范。
 *
 * @since JDK1.0
 */
package java.lang;
