/*
 * Copyright (c) 1997, 2018, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util;

/**
 * This class provides a skeletal implementation of the {@code Collection}
 * interface, to minimize the effort required to implement this interface. <p>
 *
 * 这个类提供了Collection接口的实现骨架，目的是为了尽可能地实现这个接口需要的时间。
 *
 * To implement an unmodifiable collection, the programmer needs only to
 * extend this class and provide implementations for the {@code iterator} and
 * {@code size} methods.  (The iterator returned by the {@code iterator}
 * method must implement {@code hasNext} and {@code next}.)<p>
 *
 * 为了实现一个不可修改的集合，程序只需要拓展这个类并提供iterator和size的实现方法。
 * （通过iterator方法返回的迭代器必须实现hasNext和next。）
 *
 * To implement a modifiable collection, the programmer must additionally
 * override this class's {@code add} method (which otherwise throws an
 * {@code UnsupportedOperationException}), and the iterator returned by the
 * {@code iterator} method must additionally implement its {@code remove}
 * method.<p>
 *
 * 为了实现一个可编辑的集合，程序员必须额外地覆盖此类的add方法（除此之外这个方法
 * 还会抛出一个UnsupportedOperationException的异常），并且通过iterator方法返回
 * 的迭代器必须额外地实现它的remove方法。
 *
 * The programmer should generally provide a void (no argument) and
 * {@code Collection} constructor, as per the recommendation in the
 * {@code Collection} interface specification.<p>
 *
 * 程序员通常应该提供一个空的（没有参数）和集合的构造器，作为每一个集合接口规范的
 * 建议。
 *
 * The documentation for each non-abstract method in this class describes its
 * implementation in detail.  Each of these methods may be overridden if
 * the collection being implemented admits a more efficient implementation.<p>
 *
 * 这个类中每一个非抽象方法的文档描述了它的具体实现。在集合需要另一种实现的情况下，
 * 这些个方法都能被覆写。
 *
 * This class is a member of the
 * <a href="{@docRoot}/java.base/java/util/package-summary.html#CollectionsFramework">
 * Java Collections Framework</a>.
 *
 * 这个类是Java集合框架中的一个成员。
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see Collection
 * @since 1.2
 */

public abstract class AbstractCollection<E> implements Collection<E> {
    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
	 *
	 * 唯一的构造方法。（对于子类构造方法的调用，一般是隐式的。）
	 *
     */
    protected AbstractCollection() {
    }

    // Query Operations
	// 查询操作

    /**
     * Returns an iterator over the elements contained in this collection.
	 * 返回一个覆盖该集合所有元素的iterator构造器。
     *
     * @return an iterator over the elements contained in this collection
     */
    public abstract Iterator<E> iterator();

    public abstract int size();

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * This implementation returns {@code size() == 0}.
	 * 这个实现返回size为0。
	 *
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * This implementation iterates over the elements in the collection,
     * checking each element in turn for equality with the specified element.
	 * 这个实现对该集合中的所有元素进行迭代，每次检查一个元素判断是否与具体元素相等。
     * 该方法用于判断集合中是否包含某一元素。当某一元素为null（NPE警告）时为了避免
     * equals方法报空指针，单独判断集合中是否包含null值。当元素不为空的时候，遍历集
     * 合找出与元素相等的元素（通过equals方法）。
     *
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public boolean contains(Object o) {
        Iterator<E> it = iterator();
        if (o==null) {
            while (it.hasNext())
                if (it.next()==null)
                    return true;
        } else {
            while (it.hasNext())
                if (o.equals(it.next()))
                    return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * This implementation returns an array containing all the elements
     * returned by this collection's iterator, in the same order, stored in
     * consecutive elements of the array, starting with index {@code 0}.
     * The length of the returned array is equal to the number of elements
     * returned by the iterator, even if the size of this collection changes
     * during iteration, as might happen if the collection permits
     * concurrent modification during iteration.  The {@code size} method is
     * called only as an optimization hint; the correct result is returned
     * even if the iterator returns a different number of elements.
	 *
	 * 这个实现通过集合的迭代器返回了一个包含所有元素的数组，以相同的顺序，存储在连续
	 * 的集合元素中，从0开始。返回数组的长度等于集合迭代器返回的元素的个数，就算在迭代
	 * 中长度进行了改变还是一样，这种情况可能发生在集合允许在迭代过程中进行修改。
	 * 这个size方法只会在优化提示的时候进行调用（这句话怎么翻译？），就算迭代器返回了
	 * 一个不同的元素数量也会返回正确的结果。
     * 该方法用于将集合转化成一个数组，数组的长度等于集合的size()方法返回值。首先定义一个
     * 以集合大小（size()方法返回的长度，这里为了区分叫做大小）为长度的Object数组。遍历数
     * 组，每次循环中将集合中的元素对数组中的元素进行赋值，一直到集合中没有元素的时候，这
     * 个时候会将现有数组复制成一个新的数据返回。（这里主要是为了防止集合在转换过程中变化）
     *
     * <p>This method is equivalent to:
     *
     *  <pre> {@code
     * List<E> list = new ArrayList<E>(size());
     * for (E e : this)
     *     list.add(e);
     * return list.toArray();
     * }</pre>
     */
    public Object[] toArray() {
        // Estimate size of array; be prepared to see more or fewer elements
        Object[] r = new Object[size()];
        Iterator<E> it = iterator();
        for (int i = 0; i < r.length; i++) {
            if (! it.hasNext()) // fewer elements than expected
                return Arrays.copyOf(r, i);
            r[i] = it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * This implementation returns an array containing all the elements
     * returned by this collection's iterator in the same order, stored in
     * consecutive elements of the array, starting with index {@code 0}.
     * If the number of elements returned by the iterator is too large to
     * fit into the specified array, then the elements are returned in a
     * newly allocated array with length equal to the number of elements
     * returned by the iterator, even if the size of this collection
     * changes during iteration, as might happen if the collection permits
     * concurrent modification during iteration.  The {@code size} method is
     * called only as an optimization hint; the correct result is returned
     * even if the iterator returns a different number of elements.
     *
     * 这个实现方法由迭代器以相同的顺序返回一个包含所有元素的数组，存储在连续的数组元素
     * 中，从下标0开始（大概意思就是说把一个集合转化成一个固定的数组，这个数组通过参数传入）。
     * 如果这个集合中的元素太多了在这个数组中放不下的话，那么会返回一个新分配的等同于集合长度
     * 的数组。就算集合的长度在改变也是一样的，这种情况一般发生在允许遍历中编辑的集合中。
     *
     * 首先获取集合的大小（size()方法），判断传入的参数数组的长度和原集合的大小，如果数组
     * 的长度大则使用数组的长度，否则使用和集合大小一样的一个新的数组。然后开始遍历集合，将
     * 集合的元素赋值给数组。这时候有三种情况：第一种情况参数数组的长度和集合的大小相同，则
     * 遍历数组结束的时候集合也遍历完成，这时候直接返回数组即可；第二种情况数组的长度大于集合
     * 的大小，这时候又要分三种情况，如果当前数组等价于参数数组的时候，往数组中填入null值，如
     * 果参数数组的长度小于当前数组的时候，将当前数组赋值成一个新的返回，其他情况下先将当前数
     * 组复制给参数数组，再在后面的值中插入null；第三种情况下如果数组遍历完了集合中还有元素，
     * 这个时候就将集合遍历全部复制给数组再返回。
     *
     * <p>This method is equivalent to:
     *
     *  <pre> {@code
     * List<E> list = new ArrayList<E>(size());
     * for (E e : this)
     *     list.add(e);
     * return list.toArray(a);
     * }</pre>
     *
     * @throws ArrayStoreException  {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        // Estimate size of array; be prepared to see more or fewer elements
        int size = size();
        T[] r = a.length >= size ? a :
                  (T[])java.lang.reflect.Array
                  .newInstance(a.getClass().getComponentType(), size);
        Iterator<E> it = iterator();

        for (int i = 0; i < r.length; i++) {
            if (! it.hasNext()) { // fewer elements than expected
                if (a == r) {
                    r[i] = null; // null-terminate
                } else if (a.length < i) {
                    return Arrays.copyOf(r, i);
                } else {
                    System.arraycopy(r, 0, a, 0, i);
                    if (a.length > i) {
                        a[i] = null;
                    }
                }
                return a;
            }
            r[i] = (T)it.next();
        }
        // more elements than expected
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
	 *
	 * 该数组的分配的最大尺寸。
	 * 一些虚拟机在数组中存了一些标语。
	 * 尝试分配更大的数组可能会引发OutOfMemoryError（OOM）。
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Reallocates the array being used within toArray when the iterator
     * returned more elements than expected, and finishes filling it from
     * the iterator.
	 *
	 * 当迭代器（iterator）返回的元素个数比你想要的数组大小大的时候，会对数组进行
	 * 重新分配，直到迭代器中的元素没了。主要是为了防止赋值的数组的大小小于集合中的
	 * 元素个数。此方法只有在执行toArray()方法过程时集合尺寸大小大于数组大小的时候。
	 * 代码中可以看到在数组大小不足时会进行扩容，扩容的规则为原大小*1.5+1。
	 * 当数组的大小大于MAX_ARRAY_SIZE时，执行hugeCapacity方法。hugeCapacity方法中
	 * 传入当前可容纳的数组大小+1。如果该参数已经变成负数了，则表示当前数据的大小已经
	 * 超过最大值。如果该参数为非负数，则将它与MAX_ARRAY_SIZE相比较，如果小于MAX_ARRAY_SIZE
	 * 则大小为MAX_ARRAY_SIZE，如果大于则大小为Integer的最大长度。
     *
     * @param r the array, replete with previously stored elements
     * @param it the in-progress iterator over this collection
     * @return array containing the elements in the given array, plus any
     *         further elements returned by the iterator, trimmed to size
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
        int i = r.length;
        while (it.hasNext()) {
            int cap = r.length;
            if (i == cap) {
                int newCap = cap + (cap >> 1) + 1;
                // overflow-conscious code
                if (newCap - MAX_ARRAY_SIZE > 0)
                    newCap = hugeCapacity(cap + 1);
                r = Arrays.copyOf(r, newCap);
            }
            r[i++] = (T)it.next();
        }
        // trim if overallocated
        return (i == r.length) ? r : Arrays.copyOf(r, i);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError
                ("Required array size too large");
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }

    // Modification Operations
	// 以下都是集合的修改操作

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * This implementation always throws an
     * {@code UnsupportedOperationException}.
	 *
	 * 该方法的实现总是抛出一个UnsupportedOperationException。
	 * 一般是为了防止像Arrays.asList()方法返回的集合对象进行修改等操作。
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     * @throws IllegalStateException         {@inheritDoc}
     */
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * This implementation iterates over the collection looking for the
     * specified element.  If it finds the element, it removes the element
     * from the collection using the iterator's remove method.
	 *
	 * 首先通过迭代器查找想要remove的元素，
	 * 如果找到了这个元素，则使用迭代器的remove方法进行remove。
     *
     * <p>Note that this implementation throws an
     * {@code UnsupportedOperationException} if the iterator returned by this
     * collection's iterator method does not implement the {@code remove}
     * method and this collection contains the specified object.
	 *
	 * 如果该集合的迭代器没有实现remove方法并且集合不为空的时候，会抛出
	 * UnsupportedOperationException。
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     */
    public boolean remove(Object o) {
        Iterator<E> it = iterator();
        if (o==null) {
            while (it.hasNext()) {
                if (it.next()==null) {
                    it.remove();
                    return true;
                }
            }
        } else {
            while (it.hasNext()) {
                if (o.equals(it.next())) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }


    // Bulk Operations

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * This implementation iterates over the specified collection,
     * checking each element returned by the iterator in turn to see
     * if it's contained in this collection.  If all elements are so
     * contained {@code true} is returned, otherwise {@code false}.
	 *
	 * 通过迭代器检查每个元素看它是不是在这个集合中包含了。
	 * 如果参数集合中所有的元素都包含了就返回true，否则返回false。
     *
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @see #contains(Object)
     */
    public boolean containsAll(Collection<?> c) {
        for (Object e : c)
            if (!contains(e))
                return false;
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * This implementation iterates over the specified collection, and adds
     * each object returned by the iterator to this collection, in turn.
	 *
	 * 这个方法对传入的参数进行迭代，将其中的每一个元素轮流添加到本集合中。
     *
     * <p>Note that this implementation will throw an
     * {@code UnsupportedOperationException} unless {@code add} is
     * overridden (assuming the specified collection is non-empty).
	 *
	 * 该实现会抛出UnsupportedOperationException除非add()方法被重写了
	 * （指定的集合不能为空）。
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     * @throws IllegalStateException         {@inheritDoc}
     *
     * @see #add(Object)
     */
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c)
            if (add(e))
                modified = true;
        return modified;
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * This implementation iterates over this collection, checking each
     * element returned by the iterator in turn to see if it's contained
     * in the specified collection.  If it's so contained, it's removed from
     * this collection with the iterator's {@code remove} method.
	 *
	 * 这个方法其实就是对集合进行遍历，如果当前集合包含该元素就将它进行remove
	 * （通过迭代器的remove方法）。
     *
     * <p>Note that this implementation will throw an
     * {@code UnsupportedOperationException} if the iterator returned by the
     * {@code iterator} method does not implement the {@code remove} method
     * and this collection contains one or more elements in common with the
     * specified collection.
	 *
	 * 如果在remove()没有被实现就抛出UnsupportedOperationException。
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     *
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<?> it = iterator();
        while (it.hasNext()) {
            if (c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * This implementation iterates over this collection, checking each
     * element returned by the iterator in turn to see if it's contained
     * in the specified collection.  If it's not so contained, it's removed
     * from this collection with the iterator's {@code remove} method.
	 *
	 * 遍历本集合，如果指定的集合中包含本集合中的元素，就将其remove
	 * （通过本集合迭代器的remove()方法）。
	 * removeAll()和retainAll()两个方法的区别，一个是将参数中的元素去除，
	 * 另一个则是如果参数中没有该元素就去除。
     *
     * <p>Note that this implementation will throw an
     * {@code UnsupportedOperationException} if the iterator returned by the
     * {@code iterator} method does not implement the {@code remove} method
     * and this collection contains one or more elements not present in the
     * specified collection.
	 *
	 * 如果迭代器没有实现remove()方法时抛出UnsupportedOperationException。
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     *
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * This implementation iterates over this collection, removing each
     * element using the {@code Iterator.remove} operation.  Most
     * implementations will probably choose to override this method for
     * efficiency.
	 *
	 * 通过迭代器的remove()方法对集合中的所有元素进行remove。
	 * 大多数具体实现为了效率都会对该方法进行覆写。
     *
     * <p>Note that this implementation will throw an
     * {@code UnsupportedOperationException} if the iterator returned by this
     * collection's {@code iterator} method does not implement the
     * {@code remove} method and this collection is non-empty.
	 *
	 * 集合迭代器没有实现remove()方法的时候抛出UnsupportedOperationException。
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     */
    public void clear() {
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }


    //  String conversion

    /**
     * Returns a string representation of this collection.  The string
     * representation consists of a list of the collection's elements in the
     * order they are returned by its iterator, enclosed in square brackets
     * ({@code "[]"}).  Adjacent elements are separated by the characters
     * {@code ", "} (comma and space).  Elements are converted to strings as
     * by {@link String#valueOf(Object)}.
	 *
	 * 将整个集合转换成String对象返回。String对象中包含所有的元素（通过迭代器获取）,
	 * 最外面为[]，里面的元素通过逗号隔离。
     *
     * @return a string representation of this collection
     */
    public String toString() {
        Iterator<E> it = iterator();
        if (! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            E e = it.next();
            sb.append(e == this ? "(this Collection)" : e);
            if (! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }

}
