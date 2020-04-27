
package java.lang;

import java.io.IOException;

/**
 * An object to which <tt>char</tt> sequences and values can be appended.  The
 * <tt>Appendable</tt> interface must be implemented by any class whose
 * instances are intended to receive formatted output from a {@link
 * java.util.Formatter}.
 * 一个可追加的字符序列和字符值的对象。
 * Appendable接口必须由用于接收来自{@link java.util.Formatter Formatter}格式化输出的实例的任何类实现。
 *
 * <p> The characters to be appended should be valid Unicode characters as
 * described in <a href="Character.html#unicode">Unicode Character
 * Representation</a>.  Note that supplementary characters may be composed of
 * multiple 16-bit <tt>char</tt> values.
 * 要追加的字符应该是有效的Unicode字符集，如<a href="Character.html#unicode">Unicode字符表示</a>中所述。
 *
 * <p> Appendables are not necessarily safe for multi-threaded access.  Thread
 * safety is the responsibility of classes that extend and implement this
 * interface.
 * 对于多线程访问，追加的对象不一定安全。
 * 线程安全是扩展和实现此接口的类的责任。
 *
 * <p> Since this interface may be implemented by existing classes
 * with different styles of error handling there is no guarantee that
 * errors will be propagated to the invoker.
 * 不能保证错误将传播给调用者。
 *
 * @since 1.5
 */
public interface Appendable {

    // 追加操作

    /// 追加字符序列

    /**
     * Appends the specified character sequence to this <tt>Appendable</tt>.
     *
     * <p> Depending on which class implements the character sequence
     * <tt>csq</tt>, the entire sequence may not be appended.  For
     * instance, if <tt>csq</tt> is a {@link java.nio.CharBuffer} then
     * the subsequence to append is defined by the buffer's position and limit.
     *
     * @param  csq
     *         The character sequence to append.  If <tt>csq</tt> is
     *         <tt>null</tt>, then the four characters <tt>"null"</tt> are
     *         appended to this Appendable.
     *         要追加的字符序列
     *
     * @return  A reference to this <tt>Appendable</tt>
     *
     * @throws  IOException
     *          If an I/O error occurs
     */
    Appendable append(CharSequence csq) throws IOException;

    /**
     * Appends a subsequence of the specified character sequence to this
     * <tt>Appendable</tt>.
     *
     * <p> An invocation of this method of the form <tt>out.append(csq, start,
     * end)</tt> when <tt>csq</tt> is not <tt>null</tt>, behaves in
     * exactly the same way as the invocation
     *
     * <pre>
     *     out.append(csq.subSequence(start, end)) </pre>
     *
     * @param  csq
     *         The character sequence from which a subsequence will be
     *         appended.  If <tt>csq</tt> is <tt>null</tt>, then characters
     *         will be appended as if <tt>csq</tt> contained the four
     *         characters <tt>"null"</tt>.
     *
     * @param  start
     *         The index of the first character in the subsequence
     *
     * @param  end
     *         The index of the character following the last character in the
     *         subsequence
     *
     * @return  A reference to this <tt>Appendable</tt>
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>start</tt> or <tt>end</tt> are negative, <tt>start</tt>
     *          is greater than <tt>end</tt>, or <tt>end</tt> is greater than
     *          <tt>csq.length()</tt>
     *
     * @throws  IOException
     *          If an I/O error occurs
     */
    Appendable append(CharSequence csq, int start, int end) throws IOException;

    /// 追加字符

    /**
     * Appends the specified character to this <tt>Appendable</tt>.
     *
     * @param  c
     *         The character to append
     *         要追加的字符
     *
     * @return  A reference to this <tt>Appendable</tt>
     *
     * @throws  IOException
     *          If an I/O error occurs
     */
    Appendable append(char c) throws IOException;
}
