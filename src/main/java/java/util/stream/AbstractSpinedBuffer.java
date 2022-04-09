
package java.util.stream;

/**
 * Base class for a data structure for gathering elements into a buffer and then
 * iterating them. Maintains an array of increasingly sized arrays, so there is
 * no copying cost associated with growing the data structure.
 * 用于将元素收集到缓冲区，并对其进行迭代的数据结构的基类。
 *
 * @since 1.8
 */
abstract class AbstractSpinedBuffer {
    /**
     * Minimum power-of-two for the first chunk.
     */
    public static final int MIN_CHUNK_POWER = 4;

    /**
     * Minimum size for the first chunk.
     */
    public static final int MIN_CHUNK_SIZE = 1 << MIN_CHUNK_POWER;

    /**
     * Max power-of-two for chunks.
     */
    public static final int MAX_CHUNK_POWER = 30;

    /**
     * Minimum array size for array-of-chunks.
     */
    public static final int MIN_SPINE_SIZE = 8;


    /**
     * log2 of the size of the first chunk.
     * 第一个块大小的log2。
     */
    protected final int initialChunkPower;

    /**
     * Index of the *next* element to write; may point into, or just outside of,
     * the current chunk.
     * 下一个要写入的元素的索引。
     */
    protected int elementIndex;

    /**
     * Index of the *current* chunk in the spine array, if the spine array is
     * non-null.
     * spine数组中的当前块的索引。
     */
    protected int spineIndex;

    /**
     * Count of elements in all prior chunks.
     * 所有先前块中的元素的计数。
     */
    protected long[] priorElementCount;

    /**
     * Construct with an initial capacity of 16.
     * 初始容量为16的建造。
     */
    protected AbstractSpinedBuffer() {
        this.initialChunkPower = MIN_CHUNK_POWER;
    }

    /**
     * Construct with a specified initial capacity.
     * 以指定的初始容量建造。
     *
     * @param initialCapacity The minimum expected number of elements
     */
    protected AbstractSpinedBuffer(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
        }

        this.initialChunkPower = Math.max(MIN_CHUNK_POWER,
                                          Integer.SIZE - Integer.numberOfLeadingZeros(initialCapacity - 1));
    }

    /**
     * Is the buffer currently empty?
     * 缓冲区当前是空的么？
     */
    public boolean isEmpty() {
        return (spineIndex == 0) && (elementIndex == 0);
    }

    /**
     * How many elements are currently in the buffer?
     * 当前缓冲区中有多少个元素？
     */
    public long count() {
        return (spineIndex == 0)
               ? elementIndex
               : priorElementCount[spineIndex] + elementIndex;
    }

    /**
     * How big should the nth chunk be?
     * 第n块应该是多大？
     */
    protected int chunkSize(int n) {
        int power = (n == 0 || n == 1)
                    ? initialChunkPower
                    : Math.min(initialChunkPower + n - 1, AbstractSpinedBuffer.MAX_CHUNK_POWER);
        return 1 << power;
    }

    /**
     * Remove all data from the buffer.
     * 从缓冲区中删除所有数据。
     */
    public abstract void clear();
}
