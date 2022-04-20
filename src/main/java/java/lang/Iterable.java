
package java.lang;

import java.util.Iterator;

/**
 * Implementing this interface allows an object to be the target of
 * the "foreach" statement.
 * 可迭代的类型，实现此接口允许对象为"foreach"语句的目标。
 * 使用场景：遍历集合因素
 *
 * @param <T> the type of elements returned by the iterator (由迭代器返回的元素类型)
 *
 * @since 1.5
 */
public interface Iterable<T> {

    /**
     * Returns an iterator over a set of elements of type T.
     * 返回一个迭代器。
     *
     * @return an Iterator.
     */
    Iterator<T> iterator();
}
