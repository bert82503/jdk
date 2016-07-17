package jdk7.example.numeric;

/**
 * Underscores in Numeric Literals (数值常量中的下划线).
 * <p/>
 * 提高代码的可读性
 *
 * @author xingle
 * @since 2016年07月16日 23:50
 */
public class UnderscoresInNumericLiterals {

    // shows other ways you can use the underscore in numeric literals:
    private static final long creditCardNumber = 1234_5678_9012_34356L;

    private static final long socialSecurityNumber = 999_99_9999L;

    private static final float pi = 3.14_15F;

    private static final long hexBytes = 0xFF_EC_DE_5E;
    private static final long hexWords = 0xCAFE_BABE;

    private static final long maxLong = 0x7fff_ffff_ffff_ffffL;

    private static final byte nybbles = 0b0010_0101;
    private static final long bytes = 0b11010010_01101001_10010100_10010010;

}
