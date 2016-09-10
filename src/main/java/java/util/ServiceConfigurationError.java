/*
 * Copyright (c) 2005, 2006, Oracle and/or its affiliates. All rights reserved.
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

package java.util;


/**
 * Error thrown when something goes wrong while loading a service provider.
 *
 * <p>
 * 当加载服务提供者出现问题时，抛出的错误。
 *
 * <p> This error will be thrown in the following situations (在下列情况下将抛出此错误):
 *
 * <ul>
 *
 *   <li> The format of a provider-configuration file (提供者配置文件的格式) violates the <a
 *   href="ServiceLoader.html#format">specification</a>; </li>
 *
 *   <li> An {@link java.io.IOException IOException} occurs while reading a
 *   provider-configuration file (读取提供者配置文件时发生IO异常); </li>
 *
 *   <li> A concrete provider class named in a provider-configuration file
 *   cannot be found (无法找到具体的提供者类); </li>
 *
 *   <li> A concrete provider class is not a subclass of the service class (具体的提供者类不是服务类的子类);
 *   </li>
 *
 *   <li> A concrete provider class cannot be instantiated (具体的提供者类不能被实例化); or
 *
 *   <li> Some other kind of error occurs. </li>
 *
 * </ul>
 *
 * @author Mark Reinhold
 * @since 1.6
 */
// [SPI机制] 服务配置错误
public class ServiceConfigurationError extends Error {

    private static final long serialVersionUID = 74132770414881L;

    /**
     * Constructs a new instance with the specified message.
     *
     * @param  msg  The message, or <tt>null</tt> if there is no message
     *
     */
    public ServiceConfigurationError(String msg) {
        super(msg);
    }

    /**
     * Constructs a new instance with the specified message and cause.
     *
     * @param  msg  The message, or <tt>null</tt> if there is no message
     *
     * @param  cause  The cause, or <tt>null</tt> if the cause is nonexistent
     *                or unknown
     */
    public ServiceConfigurationError(String msg, Throwable cause) {
        super(msg, cause);
    }

}
