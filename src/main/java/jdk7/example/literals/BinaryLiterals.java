package jdk7.example.literals;

/**
 * Binary Literals (二进制常量).
 *
 * @author xingle
 * @since 2016年07月09日 00:58
 */
public class BinaryLiterals {

    /**
     * Binary literals can make relationships among data more apparent (使数据之间的关系更明显)
     * than they would be in hexadecimal or octal.
     * For example, each successive number in the following array is rotated by one bit:
     */
    public static final int[] phases = {
            0b00110001,
            0b01100010,
            0b11000100,
            0b10001001,
            0b00010011,
            0b00100110,
            0b01001100,
            0b10011000
    };

    /**
     * use binary literals to make a bitmap more readable (使位图更具可读性):
     */
    // 幸福的脸, 笑脸
    public static final short[] HAPPY_FACE = {
            (short) 0b0000011111100000,
            (short) 0b0000100000010000,
            (short) 0b0001000000001000,
            (short) 0b0010000000000100,
            (short) 0b0100000000000010,
            (short) 0b1000011001100001,
            (short) 0b1000011001100001,
            (short) 0b1000000000000001,
            (short) 0b1000000000000001,
            (short) 0b1001000000001001,
            (short) 0b1000100000010001,
            (short) 0b0100011111100010,
            (short) 0b0010000000000100,
            (short) 0b0001000000001000,
            (short) 0b0000100000010000,
            (short) 0b0000011111100000
    };

}
