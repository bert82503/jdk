
package java.lang;

/**
 * Thrown when an application tries to load in a class through its
 * string name using:
 * <ul>
 * <li>The <code>forName</code> method in class <code>Class</code>.
 * <li>The <code>findSystemClass</code> method in class
 *     <code>ClassLoader</code> .
 * <li>The <code>loadClass</code> method in class <code>ClassLoader</code>.
 * </ul>
 * 类未找到异常：表示当应用程序尝试通过其字符串名称加载类时抛出的异常，使用：
 * <ul>
 *     <li>{@link Class#forName(String)}</li>
 *     <li>{@link ClassLoader#findSystemClass(String)}</li>
 *     <li>{@link ClassLoader#loadClass(String, boolean)}</li>
 * </ul>
 * <p>
 * but no definition for the class with the specified name could be found.
 * (但没有找到指定名称的类型定义。)
 *
 * <p>As of release 1.4, this exception has been retrofitted to conform to
 * the general purpose exception-chaining mechanism.  The "optional exception
 * that was raised while loading the class" that may be provided at
 * construction time and accessed via the {@link #getException()} method is
 * now known as the <i>cause</i>, and may be accessed via the {@link
 * Throwable#getCause()} method, as well as the aforementioned "legacy method."
 * 通用的异常链机制
 *
 * @author  unascribed
 * @see     java.lang.Class#forName(java.lang.String)
 * @see     java.lang.ClassLoader#findSystemClass(java.lang.String)
 * @see     java.lang.ClassLoader#loadClass(java.lang.String, boolean)
 * @since   JDK1.0
 */
public class ClassNotFoundException extends ReflectiveOperationException {
    /**
     * use serialVersionUID from JDK 1.1.X for interoperability (互操作性)
     */
     private static final long serialVersionUID = 9176873029745254542L;

    /**
     * This field holds the exception ex if the
     * ClassNotFoundException(String s, Throwable ex) constructor was
     * used to instantiate the object.
     * 本字段持有异常实例
     * @serial
     * @since 1.2
     */
    private Throwable ex;

    /**
     * Constructs a <code>ClassNotFoundException</code> with no detail message.
     */
    public ClassNotFoundException() {
        // Disallow initCause
        super((Throwable)null);
    }

    /**
     * Constructs a <code>ClassNotFoundException</code> with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public ClassNotFoundException(String s) {
        // Disallow initCause
        super(s, null);
    }

    /**
     * Constructs a <code>ClassNotFoundException</code> with the
     * specified detail message and optional exception that was
     * raised while loading the class.
     *
     * @param s the detail message
     * @param ex the exception that was raised while loading the class
     * @since 1.2
     */
    public ClassNotFoundException(String s, Throwable ex) {
        // Disallow initCause
        super(s, null);
        this.ex = ex;
    }

    /**
     * Returns the exception that was raised if an error occurred while
     * attempting to load the class. Otherwise, returns <tt>null</tt>.
     *
     * <p>This method predates the general-purpose exception chaining facility.
     * The {@link Throwable#getCause()} method is now the preferred means of
     * obtaining this information.
     *
     * @return the <code>Exception</code> that was raised while loading a class
     * @since 1.2
     */
    public Throwable getException() {
        return ex;
    }

    /**
     * Returns the cause of this exception (the exception that was raised
     * if an error occurred while attempting to load the class; otherwise
     * <tt>null</tt>).
     *
     * @return  the cause of this exception.
     * @since   1.4
     */
    @Override
    public Throwable getCause() {
        return ex;
    }

}
