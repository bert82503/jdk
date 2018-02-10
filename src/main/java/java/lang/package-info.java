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
 * 最重要的类是 {@link java.lang.Object}，其是类层次结构树的根；
 * {@link java.lang.Class} 表示运行时的类型实例。
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
 * {@link java.lang.Boolean}、{@link java.lang.Integer}、{@link java.lang.Long}
 * 表示基本类型值的包装类；
 * {@link java.lang.Void} 是一个无实例化类。
 *
 * <p>The class {@code Math} provides commonly used mathematical
 * functions such as sine, cosine, and square root. The classes {@code
 * String}, {@code StringBuffer}, and {@code StringBuilder} similarly
 * provide commonly used operations on character strings.
 * <p>
 * {@link java.lang.Math} 提供常用的数学函数；
 * {@link java.lang.String}、{@link java.lang.StringBuffer} 和
 * {@link java.lang.StringBuilder} 提供常用的字符串操作。
 *
 * <p>Classes {@code ClassLoader}, {@code Process}, {@code
 * ProcessBuilder}, {@code Runtime}, {@code SecurityManager}, and
 * {@code System} provide "system operations" that manage the dynamic
 * loading of classes, creation of external processes, host
 * environment inquiries such as the time of day, and enforcement of
 * security policies.
 * <p>
 * {@link java.lang.ClassLoader}、
 * {@link java.lang.Process}、{@link java.lang.ProcessBuilder}、
 * {@link java.lang.Runtime}、
 * {@link java.lang.SecurityManager}、{@link java.lang.System}
 * 提供管理 类型动态加载、外部进程创建、主机环境查询和安全策略执行 的系统操作。
 *
 * <p>Class {@code Throwable} encompasses objects that may be thrown
 * by the {@code throw} statement. Subclasses of {@code Throwable}
 * represent errors and exceptions.
 * <p>
 * {@link java.lang.Throwable} 包含可能由 {@code throw} 语句抛出的对象，
 * 其子类表示错误和异常，{@link java.lang.Error}、{@link java.lang.Exception}。
 *
 * <a name="charenc"></a>
 * <h3>Character Encodings/字符编码</h3>
 *
 * The specification of the {@link java.nio.charset.Charset
 * java.nio.charset.Charset} class describes the naming conventions
 * for character encodings as well as the set of standard encodings
 * that must be supported by every implementation of the Java
 * platform.
 * <p>
 * {@link java.nio.charset.Charset} 字符集类的规范描述了字符编码的命名规范及标准编码集。
 *
 * @since JDK1.0
 */
package java.lang;
