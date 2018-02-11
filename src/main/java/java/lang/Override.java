/*
 * Copyright (c) 2003, 2006, Oracle and/or its affiliates. All rights reserved.
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

package java.lang;

import java.lang.annotation.*;

/**
 * Indicates that a method declaration is intended to override a
 * method declaration in a supertype. If a method is annotated with
 * this annotation type compilers are required to generate an error
 * message unless at least one of the following conditions hold:
 * <p>
 * 指示被注解的方法声明覆盖了超类型中的一个方法声明。
 * 如果一个方法用此注解类型，编译器需要生成一条错误消息，至少满足以下条件之一：
 *
 * <ul><li>
 * The method does override or implement a method declared in a
 * supertype/此方法覆盖或实现超类中声明的方法.
 * </li><li>
 * The method has a signature that is override-equivalent to that of
 * any public method declared in {@linkplain Object}/方法拥有签名，它是覆盖Object中等价的任何公共方法.
 * </li></ul>
 *
 * @author  Peter von der Ah&eacute;
 * @author  Joshua Bloch
 * @jls 9.6.1.4 Override
 * @since 1.5
 */
// 覆盖父类中的方法声明
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Override {
    //// 第36条：坚持使用Override注解
}
