package jdk7.enhancements.warnings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 堆内存污染示例。
 *
 * @author xingle
 * @since 2016年08月02日 11:07
 */
public class HeapPollutionExample {

    public static void main(String[] args) {
        List<String> stringListA = new ArrayList<>();
        List<String> stringListB = new ArrayList<>();

        ArrayBuilder.addToList(stringListA, "Seven", "Eight", "Nine");
        ArrayBuilder.addToList(stringListA, "Ten", "Eleven", "Twelve");
        List<List<String>> listOfStringLists = new ArrayList<>();
        ArrayBuilder.addToList(listOfStringLists, stringListA, stringListB);
        ArrayBuilder.addToList2(listOfStringLists, stringListA, stringListB);
        ArrayBuilder.addToList3(listOfStringLists, stringListA, stringListB); // no warning

        ArrayBuilder.faultyMethod(Arrays.asList("Hello!"), Arrays.asList("World!"));
    }

}
