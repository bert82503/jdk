
package java.lang;

/**
 * Thrown to indicate that the application has attempted to convert
 * a string to one of the numeric types, but that the string does not
 * have the appropriate format.
 * 数字格式异常：该字符串不具有适当的数字类型格式。
 *
 * @author  unascribed
 * @see     java.lang.Integer#toString()
 * @since   JDK1.0
 */
public
class NumberFormatException extends IllegalArgumentException {
    private static final long serialVersionUID = -2848938806368998894L;

    /**
     * Constructs a <code>NumberFormatException</code> with no detail message.
     */
    public NumberFormatException () {
        super();
    }

    /**
     * Constructs a <code>NumberFormatException</code> with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public NumberFormatException (String s) {
        super(s);
    }

    /**
     * Factory method for making a <code>NumberFormatException</code>
     * given the specified input which caused the error.
     * 静态工厂方法
     *
     * @param   s   the input causing the error
     */
    static NumberFormatException forInputString(String s) {
        return new NumberFormatException("For input string: \"" + s + "\"");
    }
}
