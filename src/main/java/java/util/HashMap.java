
package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.Serializable;

/**
 * Hash table based implementation of the <tt>Map</tt> interface.  This
 * implementation provides all of the optional map operations, and permits
 * <tt>null</tt> values and the <tt>null</tt> key.  (The <tt>HashMap</tt>
 * class is roughly equivalent to <tt>Hashtable</tt>, except that it is
 * unsynchronized and permits nulls.)  This class makes no guarantees as to
 * the order of the map; in particular, it does not guarantee that the order
 * will remain constant over time.
 * <p>基于哈希表实现。
 * 此实现提供了所有可选的映射操作，并允许null值和null键。
 * 此类不保证映射的顺序，也不保证该顺序将随时间保持恒定。
 *
 * <p>This implementation provides constant-time performance for the basic
 * operations (<tt>get</tt> and <tt>put</tt>), assuming the hash function
 * disperses the elements properly among the buckets.  Iteration over
 * collection views requires time proportional to the "capacity" of the
 * <tt>HashMap</tt> instance (the number of buckets) plus its size (the number
 * of key-value mappings).  Thus, it's very important not to set the initial
 * capacity too high (or the load factor too low) if iteration performance is
 * important.
 * <p>此实现提供基此操作的常量时间性能(get、put)，假设散列函数在这些桶之间合理地分散元素。
 * 集合视图迭代性能：HashMap实例的容量(桶的数量)+键-值映射条目的数量。
 * 因此，如果迭代性能很重要，不要将初始容量设置的太高(或负载因子太低)。
 *
 * <p>An instance of <tt>HashMap</tt> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>.  The
 * <i>capacity</i> is the number of buckets in the hash table, and the initial
 * capacity is simply the capacity at the time the hash table is created.  The
 * <i>load factor</i> is a measure of how full the hash table is allowed to
 * get before its capacity is automatically increased.  When the number of
 * entries in the hash table exceeds the product of the load factor and the
 * current capacity, the hash table is <i>rehashed</i> (that is, internal data
 * structures are rebuilt) so that the hash table has approximately twice the
 * number of buckets.
 * <p>HashMap实例有两个影响其性能的参数：初始容量、负载因子。
 * 容量是指哈希表中桶的数量，初始容量只是创建哈希表时的容量。
 * 负载因子是衡量哈希表在容量自动增加之前是否允许获取的量度。
 * 当哈希表中的条目数量超过负载因子和当前容量的乘积时，哈希表会被<b>重新哈希(rehashed，重建内部数据结构)</b>，
 * 这使得哈希表具有大约两倍的桶数量。
 *
 * <p>As a general rule, the default load factor (.75) offers a good tradeoff
 * between time and space costs.  Higher values decrease the space overhead
 * but increase the lookup cost (reflected in most of the operations of the
 * <tt>HashMap</tt> class, including <tt>get</tt> and <tt>put</tt>).  The
 * expected number of entries in the map and its load factor should be taken
 * into account when setting its initial capacity, so as to minimize the
 * number of rehash operations.  If the initial capacity is greater
 * than the maximum number of entries divided by the load factor, no
 * rehash operations will ever occur.
 * <p>作为一般规则，默认的负载因子(.75)在时间和空间成此之间提供了良好的折衷。
 * 较高的值会减少空间开销，但会增加查找成此。
 * 在设置其初始容量时，应考虑映射中的预期条目数和负载因子，以便最小化重新散列操作的数量。
 * 如果初始容量大于最大条目数/负载因子，将不会发生重新散列操作。
 *
 * <p>If many mappings are to be stored in a <tt>HashMap</tt> instance,
 * creating it with a sufficiently large capacity will allow the mappings to
 * be stored more efficiently than letting it perform automatic rehashing as
 * needed to grow the table.
 * <p>如果很多映射条目要存储在HashMap实例中，那么创建具有足够大容量的映射将会更有效地存储。
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a hash map concurrently, and at least one of
 * the threads modifies the map structurally, it <i>must</i> be
 * synchronized externally.  (A structural modification is any operation
 * that adds or deletes one or more mappings; merely changing the value
 * associated with a key that an instance already contains is not a
 * structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map.
 *
 * If no such object exists, the map should be "wrapped" using the
 * {@link Collections#synchronizedMap Collections.synchronizedMap}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the map:<pre>
 *   Map m = Collections.synchronizedMap(new HashMap(...));</pre>
 * <p><strong>请注意，此实现是不同步的。</strong>
 * 如果多个线程并发地访问哈希表映射，并且至少一个线程在结构上修改映射，那么它必须在外部同步。
 *
 * 最好在创建时完成包装，以防止意外地不同步访问映射：
 *   Map m = Collections.synchronizedMap(new HashMap(...));
 * <p>
 *
 * <p>The iterators returned by all of this class's "collection view methods"
 * are <i>fail-fast</i>: if the map is structurally modified at any time after
 * the iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> method, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.
 * <p>该类的"集合视图方法"返回的迭代器都是快速失败的(fail-fast)：
 * 如果在创建迭代器之后的任何时候修改映射的结构，除了迭代器自己的remove方法之外，
 * 迭代器将抛出一个"并发修改异常({@link ConcurrentModificationException})"。
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 * <p>注意：迭代器的快速失败行为不能被保证，因为一般来说，
 * 在不同步的并发修改的情况下，不可能做出任何硬的保证。
 * 快速失败迭代器会尽力而为地抛出ConcurrentModificationException。
 * 所以，编写依赖于异常正确性的程序是错误的：<b>迭代器的快速失败行为应该仅用于检测错误</b>。
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author  Doug Lea
 * @author  Josh Bloch
 * @author  Arthur van Hoff
 * @author  Neal Gafter
 * @see     Object#hashCode()
 * @see     Collection
 * @see     Map
 * @see     TreeMap
 * @see     Hashtable
 * @since   1.2
 */
// 核心实现 哈希表映射
public class HashMap<K,V>
    extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable
{

    /**
     * The default initial capacity - MUST be a power of two.
     * 默认的初始容量 - 必须是2的幂。
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     * 最大容量
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     * 默认的负载因子
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * An empty table instance to share when the table is not inflated.
     * 空表实例
     */
    static final Entry<?,?>[] EMPTY_TABLE = {};

    // 实例字段
    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     * 哈希表/桶，必要时可调整大小。长度必须始终为2的幂。(初始值为空表)
     */
    transient Entry<K,V>[] table = (Entry<K,V>[]) EMPTY_TABLE;

    /**
     * The number of key-value mappings contained in this map.
     * 此映射包含的键-值映射条目的数量
     */
    transient int size;

    /**
     * The next size value at which to resize (capacity * load factor).
     * 容量阈值：用于调整大小的下一个大小值（容量*负载因子）
     * @serial
     */
    // If table == EMPTY_TABLE then this is the initial capacity at which the
    // table will be created when inflated.
    int threshold;

    /**
     * The load factor for the hash table.
     * 哈希表的负载因子
     * @serial
     */
    final float loadFactor;

    /**
     * The number of times this HashMap has been structurally modified
     * Structural modifications are those that change the number of mappings in
     * the HashMap or otherwise modify its internal structure (e.g.,
     * rehash).  This field is used to make iterators on Collection-views of
     * the HashMap fail-fast.  (See ConcurrentModificationException).
     * 结构修改的次数，用于对集合视图执行快速失败。
     */
    transient int modCount;

    /**
     * The default threshold of map capacity above which alternative hashing is
     * used for String keys. Alternative hashing reduces the incidence of
     * collisions due to weak hash code calculation for String keys.
     * <p>映射容量的默认阈值。
     * 高于该值时，对字符串键使用备用的哈希。
     * <p/>
     * This value may be overridden by defining the system property
     * {@code jdk.map.althashing.threshold}. A property value of {@code 1}
     * forces alternative hashing to be used at all times whereas
     * {@code -1} value ensures that alternative hashing is never used.
     */
    static final int ALTERNATIVE_HASHING_THRESHOLD_DEFAULT = Integer.MAX_VALUE;

    /**
     * holds values which can't be initialized until after VM is booted.
     * 保存直到VM引导后才能初始化的值
     */
    private static class Holder {

        /**
         * Table capacity above which to switch to use alternative hashing.
         */
        static final int ALTERNATIVE_HASHING_THRESHOLD;

        static {
            String altThreshold = java.security.AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction(
                    "jdk.map.althashing.threshold"));

            int threshold;
            try {
                threshold = (null != altThreshold)
                        ? Integer.parseInt(altThreshold)
                        : ALTERNATIVE_HASHING_THRESHOLD_DEFAULT;

                // disable alternative hashing if -1
                if (threshold == -1) {
                    threshold = Integer.MAX_VALUE;
                }

                if (threshold < 0) {
                    throw new IllegalArgumentException("value must be positive integer.");
                }
            } catch(IllegalArgumentException failed) {
                throw new Error("Illegal value for 'jdk.map.althashing.threshold'", failed);
            }

            ALTERNATIVE_HASHING_THRESHOLD = threshold;
        }
    }

    /**
     * A randomizing value associated with this instance that is applied to
     * hash code of keys to make hash collisions harder to find. If 0 then
     * alternative hashing is disabled.
     * 与此实例相关联的随机值，应用于密钥的散列码，使<b>哈希碰撞</b>更难找到。
     * 如果为0，则禁用备用散列。
     */
    transient int hashSeed = 0;


    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and load factor.
     *
     * @param  initialCapacity the initial capacity (初始容量)
     * @param  loadFactor      the load factor (负载因子)
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     */
    // 核心实现 构造具有指定的初始容量和负载因子的空哈希表映射
    public HashMap(int initialCapacity, float loadFactor) {
        // 参数有效性检查
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);

        this.loadFactor = loadFactor;
        this.threshold = initialCapacity; // 容量阈值
        init();
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @param  initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    // 核心方法 构造具有指定的初始容量的空哈希表映射
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    public HashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs a new <tt>HashMap</tt> with the same mappings as the
     * specified <tt>Map</tt>.  The <tt>HashMap</tt> is created with
     * default load factor (0.75) and an initial capacity sufficient to
     * hold the mappings in the specified <tt>Map</tt>.
     *
     * @param   m the map whose mappings are to be placed in this map
     * @throws  NullPointerException if the specified map is null
     */
    public HashMap(Map<? extends K, ? extends V> m) {
        this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY),
                DEFAULT_LOAD_FACTOR);
        inflateTable(threshold);

        putAllForCreate(m);
    }

    private static int roundUpToPowerOf2(int number) {
        // assert number >= 0 : "number must be non-negative";
        return number >= MAXIMUM_CAPACITY
                ? MAXIMUM_CAPACITY
                : (number > 1) ? Integer.highestOneBit((number - 1) << 1) : 1;
    }

    /**
     * Inflates the table.
     * 扩展哈希表
     */
    private void inflateTable(int toSize) {
        // Find a power of 2 >= toSize
        int capacity = roundUpToPowerOf2(toSize); // 容量

        threshold = (int) Math.min(capacity * loadFactor, MAXIMUM_CAPACITY + 1);
        table = new Entry[capacity];
        initHashSeedAsNeeded(capacity);
    }

    // internal utilities (内部工具方法)

    /**
     * Initialization hook for subclasses. This method is called
     * in all constructors and pseudo-constructors (clone, readObject)
     * after HashMap has been initialized but before any entries have
     * been inserted.  (In the absence of this method, readObject would
     * require explicit knowledge of subclasses.)
     */
    void init() {
    }

    /**
     * Initialize the hashing mask value. We defer initialization until we
     * really need it.
     * 初始化散列掩码值
     */
    final boolean initHashSeedAsNeeded(int capacity) {
        boolean currentAltHashing = hashSeed != 0;
        boolean useAltHashing = sun.misc.VM.isBooted() &&
                (capacity >= Holder.ALTERNATIVE_HASHING_THRESHOLD);
        boolean switching = currentAltHashing ^ useAltHashing;
        if (switching) {
            hashSeed = useAltHashing
                ? sun.misc.Hashing.randomHashSeed(this)
                : 0;
        }
        return switching;
    }

    /**
     * Retrieve object hash code and applies a supplemental hash function to the
     * result hash, which defends against poor quality hash functions.  This is
     * critical because HashMap uses power-of-two length hash tables, that
     * otherwise encounter collisions for hashCodes that do not differ
     * in lower bits. Note: Null keys always map to hash 0, thus index 0.
     * 该哈希函数防御质量差的哈希函数。
     * 至关重要：因为HashMap使用2的乘方长度的哈希表，否则会遇到在低位方面没有差异的散列码冲突。
     * 注意：null值的键总是映射到散列0，因此索引为0。
     */
    final int hash(Object k) {
        int h = hashSeed;
        if (0 != h && k instanceof String) { // 键是字符串类型
            return sun.misc.Hashing.stringHash32((String) k);
        }

        h ^= k.hashCode();

        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        // 碰撞冲突
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    /**
     * Returns index for hash code h.
     * 返回散列码h的桶索引。
     */
    static int indexFor(int h, int length) {
        // assert Integer.bitCount(length) == 1 : "length must be a non-zero power of 2";
        return h & (length-1);
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    // 核心方法 返回此映射中的键-值映射条目的数量
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    // 核心方法 判断此映射是否包含键-值映射条目
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     * <p>获取规则：(key==null ? k==null : key.equals(k))
     *
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     * <p>null的返回值不一定表示映射不包含键的映射条目，还有可能映射将键明确映射到null。
     *
     * @see #put(Object, Object)
     */
    // 核心方法 返回指定键映射到的值/null
    @Override
    public V get(Object key) {
        if (key == null)
            return getForNullKey();

        Entry<K,V> entry = getEntry(key);
        return null == entry ? null : entry.getValue();
    }

    /**
     * Offloaded version of get() to look up null keys.  Null keys map
     * to index 0.  This null case is split out into separate methods
     * for the sake of performance in the two most commonly used
     * operations (get and put), but incorporated with conditionals in
     * others.
     * null值的键映射到桶索引0
     */
    private V getForNullKey() {
        if (size == 0) {
            return null;
        }
        for (Entry<K,V> e = table[0]; e != null; e = e.next) { // 桶索引-0
            if (e.key == null)
                return e.value;
        }
        return null;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the
     * specified key.
     * <p>如果此映射包含指定键的映射条目，则返回true。
     *
     * @param   key   The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     */
    @Override
    public boolean containsKey(Object key) {
        return getEntry(key) != null;
    }

    /**
     * Returns the entry associated with the specified key in the
     * HashMap.  Returns null if the HashMap contains no mapping
     * for the key.
     * <p>返回HashMap中与指定键相关联的条目/null
     */
    final Entry<K,V> getEntry(Object key) {
        if (size == 0) {
            return null;
        }

        int hash = (key == null) ? 0 : hash(key); // 键的散列码
        int i = indexFor(hash, table.length); // 桶索引（bucketIndex）
        for (Entry<K,V> e = table[i]; e != null; e = e.next) { // 桶链表
            Object k;
            if (e.hash == hash &&
                ((k = e.key) == key || (key != null && key.equals(k))))
                return e;
        }
        return null;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     * <p>如果映射先前包含键的映射条目，则替换旧值。
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    // 核心方法 将指定的值与指定的键相关联
    @Override
    public V put(K key, V value) {
        if (table == EMPTY_TABLE) {
            inflateTable(threshold); // 首次插入时进行初始化哈希表
        }
        if (key == null) {
            return putForNullKey(value);
        }
        int hash = hash(key); // 键哈希
        int i = indexFor(hash, table.length); // 桶索引
        for (Entry<K,V> e = table[i]; e != null; e = e.next) { // 桶链表
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k))) { // 找到已存在的条目
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        modCount++;
        addEntry(hash, key, value, i);
        return null;
    }

    /**
     * Offloaded version of put for null keys
     */
    private V putForNullKey(V value) {
        for (Entry<K,V> e = table[0]; e != null; e = e.next) { // 桶索引-0
            if (e.key == null) { // null键已存在
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }
        modCount++;
        addEntry(0, null, value, 0); // 键哈希-0、桶索引-0
        return null;
    }

    /**
     * This method is used instead of put by constructors and
     * pseudoconstructors (clone, readObject).  It does not resize the table,
     * check for comodification, etc.  It calls createEntry rather than
     * addEntry.
     * 本方法用于替换构造函数和伪构造函数。
     * 它不会调整表的大小，检查重复修改等。
     */
    private void putForCreate(K key, V value) {
        int hash = (null == key ? 0 : hash(key)); // 键的散列码
        int i = indexFor(hash, table.length); // 桶索引（bucketIndex）

        // 已存在，则替换为新值；
        /*
         * Look for preexisting entry for key.  This will never happen for
         * clone or deserialize.  It will only happen for construction if the
         * input Map is a sorted map whose ordering is inconsistent w/ equals.
         * 查找键对应的预先存在的条目。
         */
        for (Entry<K,V> e = table[i]; e != null; e = e.next) { // 桶链表
            Object k;
            if (e.hash == hash &&
                ((k = e.key) == key || (key != null && key.equals(k)))) {
                e.value = value;
                return;
            }
        }

        // 否则，创建新条目
        createEntry(hash, key, value, i);
    }

    private void putAllForCreate(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) // 映射条目集合
            putForCreate(e.getKey(), e.getValue());
    }

    /**
     * Rehashes the contents of this map into a new array with a
     * larger capacity.  This method is called automatically when the
     * number of keys in this map reaches its threshold.
     * <p>将此映射的内容重新散列到具有更大容量的新数组。
     * 当此映射中的键数量达到其容量阈值时，就会自动调用此方法。
     *
     * If current capacity is MAXIMUM_CAPACITY, this method does not
     * resize the map, but sets threshold to Integer.MAX_VALUE.
     * This has the effect of preventing future calls.
     *
     * @param newCapacity the new capacity, MUST be a power of two;
     *        must be greater than current capacity unless current
     *        capacity is MAXIMUM_CAPACITY (in which case value
     *        is irrelevant).
     */
    void resize(int newCapacity) {
        Entry[] oldTable = table; // 旧的哈希表
        int oldCapacity = oldTable.length; // 旧的容量
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity]; // 申请扩容的内存
        transfer(newTable, initHashSeedAsNeeded(newCapacity));
        table = newTable;
        threshold = (int)Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY + 1);
    }

    /**
     * Transfers all entries from current table to newTable.
     * 将当前哈希表中的所有条目传输到新的哈希表
     */
    void transfer(Entry[] newTable, boolean rehash) {
        int newCapacity = newTable.length; // 新的容量
        for (Entry<K,V> e : table) {
            while (null != e) { // 桶有数据
                Entry<K,V> next = e.next;
                if (rehash) { // 是否重新散列
                    e.hash = null == e.key ? 0 : hash(e.key);
                }
                // 整个桶链表一起挪动
                int i = indexFor(e.hash, newCapacity); // 桶索引
                e.next = newTable[i];
                newTable[i] = e;
                e = next;
            }
        }
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     * These mappings will replace any mappings that this map had for
     * any of the keys currently in the specified map.
     * <p>将指定映射中的所有映射条目复制到此映射。
     *
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map is null
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        int numKeysToBeAdded = m.size(); // 待增加的键数量
        if (numKeysToBeAdded == 0)
            return;

        if (table == EMPTY_TABLE) { // 初始化哈希表
            inflateTable((int) Math.max(numKeysToBeAdded * loadFactor, threshold));
        }

        /*
         * Expand the map if the map if the number of mappings to be added
         * is greater than or equal to threshold.  This is conservative; the
         * obvious condition is (m.size() + size) >= threshold, but this
         * condition could result in a map with twice the appropriate capacity,
         * if the keys to be added overlap with the keys already in this map.
         * By using the conservative calculation, we subject ourself
         * to at most one extra resize.
         */
        if (numKeysToBeAdded > threshold) {
            int targetCapacity = (int)(numKeysToBeAdded / loadFactor + 1);
            if (targetCapacity > MAXIMUM_CAPACITY)
                targetCapacity = MAXIMUM_CAPACITY;
            int newCapacity = table.length;
            while (newCapacity < targetCapacity)
                newCapacity <<= 1; // 扩容一倍
            if (newCapacity > table.length)
                resize(newCapacity);
        }

        // 遍历映射条目集合，并使用put插入条目
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param  key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    @Override
    public V remove(Object key) {
        Entry<K,V> e = removeEntryForKey(key);
        return (e == null ? null : e.value);
    }

    /**
     * Removes and returns the entry associated with the specified key
     * in the HashMap.  Returns null if the HashMap contains no mapping
     * for this key.
     * 移除并返回与指定键关联的条目
     */
    final Entry<K,V> removeEntryForKey(Object key) {
        if (size == 0) {
            return null;
        }
        int hash = (key == null) ? 0 : hash(key); // 键哈希
        int i = indexFor(hash, table.length); // 桶索引
        Entry<K,V> prev = table[i]; // 桶链表头部
        Entry<K,V> e = prev;

        while (e != null) {
            Entry<K,V> next = e.next;
            Object k;
            if (e.hash == hash &&
                ((k = e.key) == key || (key != null && key.equals(k)))) {
                modCount++;
                size--; // 条目数量-1
                if (prev == e) // 表头条目
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    /**
     * Special version of remove for EntrySet using {@code Map.Entry.equals()}
     * for matching.
     */
    final Entry<K,V> removeMapping(Object o) {
        if (size == 0 || !(o instanceof Map.Entry))
            return null;

        Map.Entry<K,V> entry = (Map.Entry<K,V>) o;
        Object key = entry.getKey();
        int hash = (key == null) ? 0 : hash(key); // 键哈希
        int i = indexFor(hash, table.length); // 桶索引
        Entry<K,V> prev = table[i]; // 桶链表表头
        Entry<K,V> e = prev;

        while (e != null) {
            Entry<K,V> next = e.next;
            if (e.hash == hash && e.equals(entry)) {
                modCount++;
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    @Override
    public void clear() {
        modCount++;
        Arrays.fill(table, null);
        size = 0;
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value
     */
    @Override
    public boolean containsValue(Object value) {
        if (value == null)
            return containsNullValue();

        Entry[] tab = table;
        for (Entry e : tab)
            for (; e != null; e = e.next)
                if (value.equals(e.value))
                    return true;
        return false;
    }

    /**
     * Special-case code for containsValue with null argument
     */
    private boolean containsNullValue() {
        Entry[] tab = table;
        for (Entry e : tab)
            for (; e != null; e = e.next)
                if (e.value == null)
                    return true;
        return false;
    }

    /**
     * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and
     * values themselves are not cloned.
     *
     * @return a shallow copy of this map
     */
    @Override
    public Object clone() {
        HashMap<K,V> result = null;
        try {
            result = (HashMap<K,V>)super.clone();
        } catch (CloneNotSupportedException e) {
            // assert false;
        }
        if (result.table != EMPTY_TABLE) {
            result.inflateTable(Math.min(
                (int) Math.min(
                    size * Math.min(1 / loadFactor, 4.0f),
                    // we have limits...
                    HashMap.MAXIMUM_CAPACITY),
               table.length));
        }
        result.entrySet = null;
        result.modCount = 0;
        result.size = 0;
        result.init();
        result.putAllForCreate(this);

        return result;
    }

    // 核心类 映射条目
    static class Entry<K,V> implements Map.Entry<K,V> {
        // 单向链表
        /** 不可变的键 */
        final K key;
        /** 值 */
        V value;
        /** 下一个条目 */
        Entry<K,V> next;
        /** 键的散列码 */
        int hash;

        /**
         * Creates new entry.
         */
        Entry(int h, K k, V v, Entry<K,V> n) {
            value = v;
            next = n;
            key = k;
            hash = h;
        }

        @Override
        public final K getKey() {
            return key;
        }

        @Override
        public final V getValue() {
            return value;
        }

        @Override
        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        @Override
        public final boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;

            Map.Entry e = (Map.Entry)o;
            Object k1 = getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2)))
                    return true;
            }
            return false;
        }

        @Override
        public final int hashCode() {
            return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
        }

        @Override
        public final String toString() {
            return getKey() + "=" + getValue();
        }

        /**
         * This method is invoked whenever the value in an entry is
         * overwritten by an invocation of put(k,v) for a key k that's already
         * in the HashMap.
         */
        void recordAccess(HashMap<K,V> m) {
        }

        /**
         * This method is invoked whenever the entry is
         * removed from the table.
         */
        void recordRemoval(HashMap<K,V> m) {
        }
    }

    /**
     * Adds a new entry with the specified key, value and hash code to
     * the specified bucket.  It is the responsibility of this
     * method to resize the table if appropriate.
     * <p>将指定键、值和散列码的新条目添加到指定的存储桶。
     * 如果合适，此方法的责任是调整表的大小。
     *
     * Subclass overrides this to alter the behavior of put method.
     */
    void addEntry(int hash, K key, V value, int bucketIndex) {
        if ((size >= threshold) && (null != table[bucketIndex])) { // 扩容哈希表
            resize(2 * table.length); // 扩大一倍
            hash = (null != key) ? hash(key) : 0; // 键哈希
            bucketIndex = indexFor(hash, table.length); // 重新计算桶索引
        }

        createEntry(hash, key, value, bucketIndex);
    }

    /**
     * Like addEntry except that this version is used when creating entries
     * as part of Map construction or "pseudo-construction" (cloning,
     * deserialization).  This version needn't worry about resizing the table.
     * 用于作为构造函数和伪构造函数的一部分来创建条目，不需要担心调整表的大小。
     *
     * Subclass overrides this to alter the behavior of HashMap(Map),
     * clone, and readObject.
     */
    void createEntry(int hash, K key, V value, int bucketIndex) {
        Entry<K,V> e = table[bucketIndex]; // 桶链表
        table[bucketIndex] = new Entry<>(hash, key, value, e); // 插入到表头（新数据作为热点数据）
        size++;
    }

    // 哈希表迭代器
    private abstract class HashIterator<E> implements Iterator<E> {
        Entry<K,V> next;        // next entry to return (下一个条目)
        int expectedModCount;   // For fast-fail (快速失败)
        int index;              // current slot (桶/槽索引)
        Entry<K,V> current;     // current entry (当前条目)

        HashIterator() {
            expectedModCount = modCount;
            if (size > 0) { // advance to first entry
                Entry[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
        }

        @Override
        public final boolean hasNext() {
            return next != null;
        }

        final Entry<K,V> nextEntry() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            Entry<K,V> e = next;
            if (e == null)
                throw new NoSuchElementException();

            if ((next = e.next) == null) {
                Entry[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
            current = e;
            return e;
        }

        @Override
        public void remove() {
            if (current == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            Object k = current.key;
            current = null;
            HashMap.this.removeEntryForKey(k);
            expectedModCount = modCount;
        }
    }

    // 值迭代器
    private final class ValueIterator extends HashIterator<V> {
        @Override
        public V next() {
            return nextEntry().value;
        }
    }

    // 键迭代器
    private final class KeyIterator extends HashIterator<K> {
        @Override
        public K next() {
            return nextEntry().getKey();
        }
    }

    // 映射条目迭代器
    private final class EntryIterator extends HashIterator<Map.Entry<K,V>> {
        @Override
        public Map.Entry<K,V> next() {
            return nextEntry();
        }
    }

    // Subclass overrides these to alter behavior of views' iterator() method
    Iterator<K> newKeyIterator()   {
        return new KeyIterator();
    }
    Iterator<V> newValueIterator()   {
        return new ValueIterator();
    }
    Iterator<Map.Entry<K,V>> newEntryIterator()   {
        return new EntryIterator();
    }


    // Views (视图)

    /** 映射条目集合 */
    private transient Set<Map.Entry<K,V>> entrySet = null;

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     */
    @Override
    public Set<K> keySet() {
        Set<K> ks = keySet;
        return (ks != null ? ks : (keySet = new KeySet()));
    }

    // 键集合
    private final class KeySet extends AbstractSet<K> {
        @Override
        public Iterator<K> iterator() {
            return newKeyIterator();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean contains(Object o) {
            return containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return HashMap.this.removeEntryForKey(o) != null;
        }

        @Override
        public void clear() {
            HashMap.this.clear();
        }
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     */
    @Override
    public Collection<V> values() {
        Collection<V> vs = values;
        return (vs != null ? vs : (values = new Values()));
    }

    // 值列表
    private final class Values extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return newValueIterator();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }

        @Override
        public void clear() {
            HashMap.this.clear();
        }
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation, or through the
     * <tt>setValue</tt> operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations.  It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map
     */
    // 核心方法 返回映射条目的集合视图
    @Override
    public Set<Map.Entry<K,V>> entrySet() {
        return entrySet0();
    }

    private Set<Map.Entry<K,V>> entrySet0() {
        Set<Map.Entry<K,V>> es = entrySet;
        return es != null ? es : (entrySet = new EntrySet());
    }

    // 映射条目集合
    private final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        @Override
        public Iterator<Map.Entry<K,V>> iterator() {
            return newEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;

            Map.Entry<K,V> e = (Map.Entry<K,V>) o;
            Entry<K,V> candidate = getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
        }

        @Override
        public boolean remove(Object o) {
            return removeMapping(o) != null;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public void clear() {
            HashMap.this.clear();
        }
    }

    // Serializable (序列化与反序列化)

    /**
     * Save the state of the <tt>HashMap</tt> instance to a stream (i.e.,
     * serialize it).
     * <p>保存HashMap实例的状态到对象输出流（序列化）。
     *
     * @serialData The <i>capacity</i> of the HashMap (the length of the
     *             bucket array) is emitted (int), followed by the
     *             <i>size</i> (an int, the number of key-value
     *             mappings), followed by the key (Object) and value (Object)
     *             for each key-value mapping.  The key-value mappings are
     *             emitted in no particular order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws IOException
    {
        // Write out the threshold, loadfactor, and any hidden stuff
        s.defaultWriteObject();

        // Write out number of buckets (桶数量)
        if (table==EMPTY_TABLE) {
            s.writeInt(roundUpToPowerOf2(threshold));
        } else {
           s.writeInt(table.length);
        }

        // Write out size (number of Mappings) (条目数量)
        s.writeInt(size);

        // Write out keys and values (alternating)
        if (size > 0) {
            for(Map.Entry<K,V> e : entrySet0()) {
                s.writeObject(e.getKey());
                s.writeObject(e.getValue());
            }
        }
    }

    private static final long serialVersionUID = 362498820763181265L;

    /**
     * Reconstitute the {@code HashMap} instance from a stream (i.e.,
     * deserialize it).
     * <p>从对象输入流中重组HashMap实例（反序列化）。
     */
    private void readObject(java.io.ObjectInputStream s)
         throws IOException, ClassNotFoundException
    {
        // Read in the threshold (ignored), loadfactor, and any hidden stuff
        s.defaultReadObject();
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new InvalidObjectException("Illegal load factor: " +
                                               loadFactor);
        }

        // set other fields that need values
        table = (Entry<K,V>[]) EMPTY_TABLE;

        // Read in number of buckets (桶数量)
        s.readInt(); // ignored.

        // Read number of mappings (条目数量)
        int mappings = s.readInt();
        if (mappings < 0)
            throw new InvalidObjectException("Illegal mappings count: " +
                                               mappings);

        // capacity chosen by number of mappings and desired load (if >= 0.25)
        int capacity = (int) Math.min(
                    mappings * Math.min(1 / loadFactor, 4.0f),
                    // we have limits...
                    HashMap.MAXIMUM_CAPACITY);

        // allocate the bucket array (分配桶数组)
        if (mappings > 0) {
            inflateTable(capacity);
        } else {
            threshold = capacity;
        }

        init();  // Give subclass a chance to do its thing.

        // Read the keys and values, and put the mappings in the HashMap
        for (int i = 0; i < mappings; i++) {
            K key = (K) s.readObject();
            V value = (V) s.readObject();
            putForCreate(key, value);
        }
    }

    // These methods are used when serializing HashSets (这些方法在序列化HashSet时使用)
    int capacity() {
        return table.length;
    }

    float loadFactor() {
        return loadFactor;
    }
}
