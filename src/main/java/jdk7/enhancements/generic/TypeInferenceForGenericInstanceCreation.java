package jdk7.enhancements.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Type Inference for Generic Instance Creation (泛型实例创建的类型推断).
 * <p/>
 * 使用类型参数的空集(<>)来替换泛型类型的构造器需要的类型参数，只要编译器可以从上下文推断出类型参数。
 * 这双尖括号被称为 砖石。
 *
 * @author xingle
 * @since 2016年07月17日 16:22
 */
public class TypeInferenceForGenericInstanceCreation {

    /// In Java SE 7, you can substitute the parameterized type of the constructor
    // with an empty set of type parameters (<>):
    // 构造器的参数化类型
    Map<String, List<String>> myMap = new HashMap<>(); // 区别点

    // consider the following variable declaration:
    // 不推荐这样使用！
//    private Map<String, List<String>> myMap = new HashMap<String, List<String>>();

//    Map<String, List<String>> myMap = new HashMap(); // unchecked conversion warning


    /// limited type inference for generic instance creation
    private void example() {
        List<String> list = new ArrayList<>();
        list.add("A");

        // The following statement should fail since addAll expects Collection<? extends String>
//        list.addAll(new ArrayList<>());

        // The following statements compile:
        List<? extends String> list2 = new ArrayList<>();
        list.addAll(list2);
    }


    /// Type Inference and Generic Constructors of Generic and Non-Generic Classes
    class MyClass<X> {
        <T> MyClass(T t) {
            // ...
        }
    }

    private void example2() {
        // the compiler in Java SE 7 can infer the actual type parameters of
        // the generic class being instantiated if you use the diamond (<>)
        MyClass<Integer> myObject = new MyClass<>("");

        // Consider the following instantiation of the class MyClass, valid in Java SE 7 and prior releases:
//        MyClass<Integer> myClass = new MyClass<Integer>("");
    }

}
