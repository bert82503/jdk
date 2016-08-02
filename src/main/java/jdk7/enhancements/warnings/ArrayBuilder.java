package jdk7.enhancements.warnings;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Variable Arguments Methods (变量参数/可变参数方法)。
 *
 * @author xingle
 * @since 2016年08月02日 11:02
 */
public class ArrayBuilder {

    /// Potential Vulnerabilities of Varargs Methods (可变参数方法的潜在漏洞)
    // Possible heap pollution from parameterized vararg type
    public static <T> void addToList(List<T> listArg, T... elements) {
        Collections.addAll(listArg, elements);
    }

    public static void faultyMethod(List<String>... lists) {
        Object[] objectArray = lists; // Valid - This statement can potentially introduce heap pollution
        objectArray[0] = Arrays.asList(42);
        String s = lists[0].get(0); // ClassCastException thrown here
    }


    /// Suppressing Warnings from Varargs Methods (抑制可变参数方法的警告)
    @SafeVarargs // asserts that the implementation of the method will not improperly handle the varargs formal parameter
    public static <T> void addToList3(List<T> listArg, T... elements) {
        Collections.addAll(listArg, elements);
    }

    @SuppressWarnings({"unchecked", "varargs"}) // does not suppress warnings generated from the method's call site
    public static <T> void addToList2(List<T> listArg, T... elements) {
        Collections.addAll(listArg, elements);
    }

}
