/*
 * Copyright (c) 1995, 1997, Oracle and/or its affiliates. All rights reserved.
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

/**
 * Thrown to indicate that the Java Virtual Machine is broken or has
 * run out of resources necessary for it to continue operating.
 * <p></p>
 * 表示JVM终止或耗尽了继续操作必需的资源时抛出。
 *
 * @author  Frank Yellin
 * @since   JDK1.0
 */
// 虚拟机错误
public abstract
class VirtualMachineError extends Error {
    /**
     * Constructs a <code>VirtualMachineError</code> with no detail message.
     */
    public VirtualMachineError() {
        super();
    }

    /**
     * Constructs a <code>VirtualMachineError</code> with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public VirtualMachineError(String s) {
        super(s);
    }
}
