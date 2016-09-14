/*
 * Copyright (c) 2003, 2010, Oracle and/or its affiliates. All rights reserved.
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

package java.io;

/**
 * A {@code Closeable} is a source or destination of data that can be closed.
 * The close method is invoked to release resources that the object is
 * holding (such as open files).
 * <p>
 * 可以关闭的数据的源头或目的地。
 * 调用 close 方法来释放对象持有的资源。
 *
 * @since 1.5
 */
// [资源释放] 可释放的资源：可以关闭的数据的源头或目的地
public interface Closeable extends AutoCloseable {

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     * <p>
     * 关闭该流并释放与之关联的任何系统资源。
     * 如果流已关闭然后调用此方法，则无效。
     *
     * @throws IOException if an I/O error occurs (I/O 发生错误)
     */
    void close() throws IOException;
}
