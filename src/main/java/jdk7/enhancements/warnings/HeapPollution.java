package jdk7.enhancements.warnings;

import java.util.ArrayList;
import java.util.List;

/**
 * 堆内存污染。
 *
 * <p>编译时，非具体化类型会经历一个叫做"类型擦除"的过程，
 * 类型擦除期间编译器会移除与类型参数和类型变量相关的信息。
 *
 * <p>原因：保持向后兼容
 *
 * @author xingle
 * @since 2016年08月02日 10:33
 */
public class HeapPollution {

    // Consider the following example:
    public static void main(String[] args) {
        List numberList = new ArrayList<Number>();
        List<String> stringList = numberList; // unchecked warning
        numberList.add(0, new Integer(42)); // another unchecked warning
        String s = stringList.get(0); // ClassCastException is thrown
    }

}
