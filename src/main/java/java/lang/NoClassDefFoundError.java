
package java.lang;

/**
 * Thrown if the Java Virtual Machine or a <code>ClassLoader</code> instance
 * tries to load in the definition of a class (as part of a normal method call
 * or as part of creating a new instance using the <code>new</code> expression)
 * and no definition of the class could be found.
 * 如果JVM或类加载器实例尝试加载类的定义
 * (作为普通方法调用的一部分或作为使用new表达式创建新实例的一部分)，
 * 并且无法找到类的定义，则抛出该异常。
 * <p>
 * The searched-for class definition existed when the currently
 * executing class was compiled, but the definition can no longer be
 * found.
 * 搜索的类定义在编译当前正在执行的类时存在，但无法再找到该定义。
 *
 * @author  unascribed
 * @since   JDK1.0
 */
public
class NoClassDefFoundError extends LinkageError {
    private static final long serialVersionUID = 9095859863287012458L;

    /**
     * Constructs a <code>NoClassDefFoundError</code> with no detail message.
     */
    public NoClassDefFoundError() {
        super();
    }

    /**
     * Constructs a <code>NoClassDefFoundError</code> with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public NoClassDefFoundError(String s) {
        super(s);
    }
}
