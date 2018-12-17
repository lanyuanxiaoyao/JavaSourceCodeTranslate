/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group.  Adapted and released, under explicit permission,
 * from JDK ArrayList.java which carries the following copyright:
 *
 * Copyright 1997 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 */

package java.util.concurrent;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import jdk.internal.misc.SharedSecrets;

/**
 * A thread-safe variant of {@link java.util.ArrayList} in which all mutative
 * operations ({@code add}, {@code set}, and so on) are implemented by
 * making a fresh copy of the underlying array.
 *
 * 一个在所有修改方法(如add, set等)中通过构建一个底层数组的新副本来实现线程安全的
 * ArrayList.
 *
 * <p>This is ordinarily too costly, but may be <em>more</em> efficient
 * than alternatives when traversal operations vastly outnumber
 * mutations, and is useful when you cannot or don't want to
 * synchronize traversals, yet need to preclude interference among
 * concurrent threads.  The "snapshot" style iterator method uses a
 * reference to the state of the array at the point that the iterator
 * was created. This array never changes during the lifetime of the
 * iterator, so interference is impossible and the iterator is
 * guaranteed not to throw {@code ConcurrentModificationException}.
 * The iterator will not reflect additions, removals, or changes to
 * the list since the iterator was created.  Element-changing
 * operations on iterators themselves ({@code remove}, {@code set}, and
 * {@code add}) are not supported. These methods throw
 * {@code UnsupportedOperationException}.
 *
 * 这(种操作)通常成本较高, 但当遍历操作远多于修改操作的时候, 它可能比XX更高效, 
 * 并且当你不能或不想进行同步遍历, 但需要排除并发线程间的干扰时非常有用. "快照"
 * 风格的迭代器方法在创建迭代器的时候使用对数组状态的引用. 这个数组在迭代器的
 * 生命周期内不会被改变, 所以干扰(数组的状态)是不可能的, 并且保证迭代器不会抛出
 * ConcurrentModificationException异常. 这个迭代器从被创建开始, 就不会反映list
 * 的增加, 移除或者修改. 不支持对迭代器本身进行元素修改操作(remove, set和add).
 * 这些方法会抛出UnsupportedOperationException异常.
 *
 * <p>All elements are permitted, including {@code null}. 所有元素都允许(被添加), 包括null
 *
 * <p>Memory consistency effects: As with other concurrent
 * collections, actions in a thread prior to placing an object into a
 * {@code CopyOnWriteArrayList}
 * <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * actions subsequent to the access or removal of that element from
 * the {@code CopyOnWriteArrayList} in another thread.
 *
 * 内存一致性的效果: 与其他并发集合一样, 上一个进程添加对象到CopyOnWriteArrayList的操作会
 * 在后一个进程访问或从CopyOnWriteArrayList中移除元素之前发生
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/java.base/java/util/package-summary.html#CollectionsFramework">
 * Java Collections Framework</a>.
 *
 * 这个类是Java集合框架中的一员
 *
 * @since 1.5
 * @author Doug Lea
 * @param <E> the type of elements held in this list
 *
 * @translator LanyuanXiaoyao
 * @date 2018.10.25
 */
public class CopyOnWriteArrayList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 8673264195747942595L;

    /**
     * The lock protecting all mutators.  (We have a mild preference
     * for builtin monitors over ReentrantLock when either will do.)
     *
     * 这个锁保护所有的mutators. (我们在一定程度上倾向于ReentrantLock的内置监视器)
     */
    final transient Object lock = new Object();

    /** The array, accessed only via getArray/setArray. 该数组只能通过getArray/setArray访问*/
    private transient volatile Object[] array;

    /**
     * Gets the array.  Non-private so as to also be accessible
     * from CopyOnWriteArraySet class.
     *
     * 获取数组. 不是私有的(方法), 便于从CopyOnWriteArraySet类中访问.
     */
    final Object[] getArray() {
        return array;
    }

    /**
     * Sets the array.
     *
     * 设置数组.
     */
    final void setArray(Object[] a) {
        array = a;
    }

    /**
     * Creates an empty list.
     *
     * 创建一个空数组.
     */
    public CopyOnWriteArrayList() {
        setArray(new Object[0]);
    }

    /**
     * Creates a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * 创建一个包含指定集合中元素的list, 其顺序是集合的迭代器返回的顺序.
     *
     * @param c the collection of initially held elements 初始持有元素的集合
     * @throws NullPointerException if the specified collection is null 如果指定的集合是null
     */
    public CopyOnWriteArrayList(Collection<? extends E> c) {
        Object[] es;
        if (c.getClass() == CopyOnWriteArrayList.class)
            es = ((CopyOnWriteArrayList<?>)c).getArray();
        else {
            es = c.toArray();
            // defend against c.toArray (incorrectly) not returning Object[] 防止 c.toArray() (错误地)不返回Object[]对象
            // (see e.g. https://bugs.openjdk.java.net/browse/JDK-6260652)
            // 简单说明一下这个bug, 在JDK文档中定义了如果List子类使用了不带参数的toArray()方法, 
            // 那么就应该返回Object[]类型的数组, 但是如果使用某个类型的数组来构建一个新的ArrayList, 
            // 如String[], 那么使用toArray()返回的仍然是String[]类型数组, 这将导致在之后的代码如果
            // 试图向返回的数组中插入非String类型的值, 就会报错. 在之前的代码里, toArray()方法是
            // 使用ArrayList的clone()方法来构建返回的数组的.
            if (es.getClass() != Object[].class)
                es = Arrays.copyOf(es, es.length, Object[].class);
        }
        setArray(es);
    }

    /**
     * Creates a list holding a copy of the given array.
     *
     * 创建一个持有给定数组副本的list.
     *
     * @param toCopyIn the array (a copy of this array is used as the
     *        internal array) 数组(这个数组的副本被用于内部数组)
     * @throws NullPointerException if the specified array is null
     */
    public CopyOnWriteArrayList(E[] toCopyIn) {
        setArray(Arrays.copyOf(toCopyIn, toCopyIn.length, Object[].class));
    }

    /**
     * Returns the number of elements in this list.
     *
     * 返回list中元素的数量
     *
     * @return the number of elements in this list list中元素的数量
     */
    public int size() {
        return getArray().length;
    }

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * 如果当前list没有包含任何元素, 就返回true
     *
     * @return {@code true} if this list contains no elements 如果当前list没有包含任何元素, 就返回true
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * static version of indexOf, to allow repeated calls without
     * needing to re-acquire array each time.
     *
     * indexOf的静态版本, 用于允许重复调用而不需要每次重新获取数组.
     *
     * @param o element to search for 查找的元素
     * @param es the array 数组
     * @param from first index to search 查找的第一个索引(下标)
     * @param to one past last index to search 查找的最后一个索引(下标)
     * @return index of element, or -1 if absent 元素的索引(下标), 如果不存在就返回-1
     */
    private static int indexOfRange(Object o, Object[] es, int from, int to) {
        // 分为查找的是null和不是null两种情况
        if (o == null) {
            for (int i = from; i < to; i++)
                if (es[i] == null)
                    return i;
        } else {
            for (int i = from; i < to; i++)
                if (o.equals(es[i]))
                    return i;
        }
        return -1;
    }

    /**
     * static version of lastIndexOf.
     *
     * lastIndexOf的静态版本
     *
     * @param o element to search for 查找的元素
     * @param es the array 数组
     * @param from index of first element of range, last element to search 范围内的第一个元素的索引(下标), 查找(过程)的最后一个元素
     * @param to one past last element of range, first element to search 范围内最后一个元素的索引(下标), 查找(过程)的第一个元素
     * @return index of element, or -1 if absent 元素的索引(下标), 如果不存在就返回-1
     */
    private static int lastIndexOfRange(Object o, Object[] es, int from, int to) {
        // 分为查找的是null和不是null两种情况
        if (o == null) {
            for (int i = to - 1; i >= from; i--)
                if (es[i] == null)
                    return i;
        } else {
            for (int i = to - 1; i >= from; i--)
                if (o.equals(es[i]))
                    return i;
        }
        return -1;
    }

    /**
     * Returns {@code true} if this list contains the specified element.
     * More formally, returns {@code true} if and only if this list contains
     * at least one element {@code e} such that {@code Objects.equals(o, e)}.
     *
     * 如果当前list包含指定的元素就返回true. 更准确地说, 当且仅当list包含至少一个元素
     * e, 使得Objects.equals(o, e), 就返回true.
     *
     * @param o element whose presence in this list is to be tested 需要被检测是否存在于当前list的元素
     * @return {@code true} if this list contains the specified element 如果当前list包含指定的元素就返回true
     */
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * {@inheritDoc}
     */
    public int indexOf(Object o) {
        Object[] es = getArray();
        return indexOfRange(o, es, 0, es.length);
    }

    /**
     * Returns the index of the first occurrence of the specified element in
     * this list, searching forwards from {@code index}, or returns -1 if
     * the element is not found.
     * More formally, returns the lowest index {@code i} such that
     * {@code i >= index && Objects.equals(get(i), e)},
     * or -1 if there is no such index.
     *
     * 返回指定元素在list中第一次出现的索引, 从index的位置开始向后搜索, 如果list没
     * 有包含这个元素就返回-1. 更准确地说, 返回最小的索引i, 满足i >= index并且Objects.equals(o, get(i)), 
     * 如果没有这样的索引就返回-1.
     *
     * @param e element to search for 需要查找的元素
     * @param index index to start searching from 查找起始的索引(下标)
     * @return the index of the first occurrence of the element in
     *         this list at position {@code index} or later in the list;
     *         {@code -1} if the element is not found. 元素在当前list第一次出现在位置index或之后的索引, 如果元素没有找到就返回-1
     * @throws IndexOutOfBoundsException if the specified index is negative 如果指定的索引是负数
     */
    public int indexOf(E e, int index) {
        Object[] es = getArray();
        return indexOfRange(e, es, index, es.length);
    }

    /**
     * {@inheritDoc}
     */
    public int lastIndexOf(Object o) {
        Object[] es = getArray();
        return lastIndexOfRange(o, es, 0, es.length);
    }

    /**
     * Returns the index of the last occurrence of the specified element in
     * this list, searching backwards from {@code index}, or returns -1 if
     * the element is not found.
     * More formally, returns the highest index {@code i} such that 
     * {@code i <= index && Objects.equals(get(i), e)},
     * or -1 if there is no such index.
     *
     * 返回指定元素在list中最后一次出现的索引, 从index的位置开始向前搜索, 如果如果list没有包含这个元素就返回-1.
     * 更准确地说, 返回最大的索引i, 满足i <= index并且Objects.equals(o, get(i)), 如果没有这样的索引就返回-1.
     *
     * @param e element to search for 需要查找的元素
     * @param index index to start searching backwards from 查找起始的索引(下标)
     * @return the index of the last occurrence of the element at position
     *         less than or equal to {@code index} in this list;
     *         -1 if the element is not found. 元素在当前list第一次出现在位置index或之前的索引, 如果元素没有找到就返回-1
     * @throws IndexOutOfBoundsException if the specified index is greater
     *         than or equal to the current size of this list 如果指定的索引大于或等于当前的size
     */
    public int lastIndexOf(E e, int index) {
        Object[] es = getArray();
        return lastIndexOfRange(e, es, 0, index + 1);
    }

    /**
     * Returns a shallow copy of this list.  (The elements themselves
     * are not copied.)
     *
     * 返回当前list的浅复制. (元素本身没有被复制)
     *
     * @return a clone of this list 当前list的拷贝
     */
    public Object clone() {
        try {
            @SuppressWarnings("unchecked")
            CopyOnWriteArrayList<E> clone =
                (CopyOnWriteArrayList<E>) super.clone();
            clone.resetLock();
            // Unlike in readObject, here we cannot visibility-piggyback on the
            // volatile write in setArray().
            // 与readObject不同, 这里我们不能使用setArray()中的volatile保证写入可见性
            VarHandle.releaseFence();
            return clone;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable 这里不应发生, 当我们是可关闭的
            throw new InternalError();
        }
    }

    /**
     * Returns an array containing all of the elements in this list
     * in proper sequence (from first to last element).
     *
     * 按照合适的序列(从第一个到最后一个)返回一个包含list中存储的所有元素的数组.
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * 返回的数组是安全的, 其中的元素不会存在list维护的引用(换句话说, 这个方法必须
     * 在内存中申请一个新的数组). 调用者可以自由地修改返回的数组.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * 这个方法是扮演着数组和集合之间桥梁角色的API.
     *
     * @return an array containing all the elements in this list 一个包含当前list中所有元素的数组
     */
    public Object[] toArray() {
        return getArray().clone();
    }

    /**
     * Returns an array containing all of the elements in this list in
     * proper sequence (from first to last element); the runtime type of
     * the returned array is that of the specified array.  If the list fits
     * in the specified array, it is returned therein.  Otherwise, a new
     * array is allocated with the runtime type of the specified array and
     * the size of this list.
     *
     * 按照正确的顺序(从第一个到最后一个)返回一个包含list中存储的所有元素的数组; 运行时
     * 返回的数组的类型就是(参数)指定的数组的类型. 如果(参数)指定的数组能放得下list(的全部元素), 
     * 那么就会直接返回这个数组(并将list的元素放入数组中).
     * 否则, 就会申请一个新的数组, 其类型与指定的数组类型相同, 大小则是list的大小.
     *
     * <p>If this list fits in the specified array with room to spare
     * (i.e., the array has more elements than this list), the element in
     * the array immediately following the end of the list is set to
     * {@code null}.  (This is useful in determining the length of this
     * list <i>only</i> if the caller knows that this list does not contain
     * any null elements.)
     *
     * 如果(参数)指定的数组能放得下list, 并且数组的空间还有剩余(数组的元素比list的要多), 那么
     * 数组中在集合长度之后紧接着的一个数组元素将被设为null. (这仅在调用者确认list中没有包含null
     * 元素的情况下, 确定list长度的时候非常管用.)
     *
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     *
     * 与toArray()方法类似, 这个方法是扮演着数组和集合之间桥梁角色的API. 进一步说, 这
     * 个方法允许精确地控制输出数组的运行时类型, 并且在某些情况下, 这被用来节省申请内存
     * 耗费时间的开销.
     *
     * <p>Suppose {@code x} is a list known to contain only strings.
     * The following code can be used to dump the list into a newly
     * allocated array of {@code String}:
     *
     * 假设x是一个已知的仅包含字符串的数组. 下面的代码可以被用来将list转化为
     * 一个新申请的String数组:
     *
     * <pre> {@code String[] y = x.toArray(new String[0]);}</pre>
     *
     * Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     *
     * 注意, toArray(new Object[0])与toArray()是完全相同的.
     *
     * @param a the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     *
     *          如果数组足够大, 可以放下整个list的元素, 则用这个数组存储list的元素.
     *          否则, 就申请一个新的, 类型与a数组类型相同的数组, 来存储list的元素.
     *
     * @return an array containing all the elements in this list 一个包含list所有元素的数组
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this list 如果(参数)指定的数组的类型不是list里所有元素的父类型
     * @throws NullPointerException if the specified array is null 如果(参数)指定的数组为null
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        Object[] es = getArray();
        int len = es.length;
        if (a.length < len)
            return (T[]) Arrays.copyOf(es, len, a.getClass());
        else {
            System.arraycopy(es, 0, a, 0, len);
            if (a.length > len)
                a[len] = null;
            return a;
        }
    }

    // Positional Access Operations 位置访问操作(通过下标获取数组元素的方法们)

    @SuppressWarnings("unchecked")
    static <E> E elementAt(Object[] a, int index) {
        return (E) a[index];
    }

    static String outOfBounds(int index, int size) {
        return "Index: " + index + ", Size: " + size;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E get(int index) {
        return elementAt(getArray(), index);
    }

    /**
     * 用synchronized关键字来保持这个方法在并发环境下的同步(在下面的代码中这也是主要的保持同步的方式)
     */

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * 用指定的元素替换list中指定位置(下标)的元素
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E set(int index, E element) {
        synchronized (lock) {
            Object[] es = getArray();
            E oldValue = elementAt(es, index);

            if (oldValue != element) {
                es = es.clone();
                es[index] = element;
                setArray(es);
            }
            return oldValue;
        }
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * 添加指定的元素到list的末尾
     *
     * @param e element to be appended to this list 将要被添加到list末尾的元素
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        synchronized (lock) {
            Object[] es = getArray();
            int len = es.length;
            es = Arrays.copyOf(es, len + 1);
            es[len] = e;
            setArray(es);
            return true;
        }
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * 插入指定的元素到list指定的位置上. 向右移动指定位置上和其后续(如果有)的所
     * 有元素
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int index, E element) {
        synchronized (lock) {
            Object[] es = getArray();
            int len = es.length;
            if (index > len || index < 0)
                throw new IndexOutOfBoundsException(outOfBounds(index, len));
            Object[] newElements;
            int numMoved = len - index;
            if (numMoved == 0)
                newElements = Arrays.copyOf(es, len + 1);
            else {
                newElements = new Object[len + 1];
                System.arraycopy(es, 0, newElements, 0, index);
                System.arraycopy(es, index, newElements, index + 1,
                                 numMoved);
            }
            newElements[index] = element;
            setArray(newElements);
        }
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).  Returns the element that was removed from the list.
     *
     * 移除list中指定位置的元素. 向左移动该位置之后的所有元素. 返回从list中被
     * 移除的元素.
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E remove(int index) {
        synchronized (lock) {
            Object[] es = getArray();
            int len = es.length;
            E oldValue = elementAt(es, index);
            int numMoved = len - index - 1;
            Object[] newElements;
            if (numMoved == 0)
                newElements = Arrays.copyOf(es, len - 1);
            else {
                newElements = new Object[len - 1];
                System.arraycopy(es, 0, newElements, 0, index);
                System.arraycopy(es, index + 1, newElements, index,
                                 numMoved);
            }
            setArray(newElements);
            return oldValue;
        }
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If this list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * {@code i} such that {@code Objects.equals(o, get(i))}
     * (if such an element exists).  Returns {@code true} if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call). 
     *
     * 移除list中第一次出现(与指定元素相同)的元素, 如果其存在的话. 如果list中没有包含
     * 该元素的话, 那么list不会被修改. 准确地来说, 是移除最小索引i, 使得其满足
     * Objects.equals(o, get(i))(如果这个元素存在). 如果这个集合包含指定的元素
     * (即当这个集合被这个方法的调用改变了), 就返回true.
     *
     * @param o element to be removed from this list, if present 从list中被移除的元素, 如果存在的话
     * @return {@code true} if this list contained the specified element 如果list包含指定的元素, 返回true
     */
    public boolean remove(Object o) {
        Object[] snapshot = getArray();
        int index = indexOfRange(o, snapshot, 0, snapshot.length);
        return index >= 0 && remove(o, snapshot, index);
    }

    /**
     * A version of remove(Object) using the strong hint that given
     * recent snapshot contains o at the given index.
     *
     * 一个使用包含给定索引的快照的强提示的remove(Object)的版本
     */
    private boolean remove(Object o, Object[] snapshot, int index) {
        synchronized (lock) {
            Object[] current = getArray();
            int len = current.length;
            // 判断快照和当前的数组是否相同, 来判断是否被其他线程修改过
            if (snapshot != current) findIndex: {
                int prefix = Math.min(index, len);
                for (int i = 0; i < prefix; i++) {
                    if (current[i] != snapshot[i]
                        && Objects.equals(o, current[i])) {
                        index = i;
                        break findIndex;
                    }
                }
                if (index >= len)
                    return false;
                if (current[index] == o)
                    break findIndex;
                index = indexOfRange(o, current, index, len);
                if (index < 0)
                    return false;
            }
            Object[] newElements = new Object[len - 1];
            System.arraycopy(current, 0, newElements, 0, index);
            System.arraycopy(current, index + 1,
                             newElements, index,
                             len - index - 1);
            setArray(newElements);
            return true;
        }
    }

    /**
     * Removes from this list all of the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by {@code (toIndex - fromIndex)} elements.
     * (If {@code toIndex==fromIndex}, this operation has no effect.)
     *
     * 移除list中所有下标在fromIndex(包含)和toIndex(不包含)之间的元素.
     * 向左移动后面的所有元素(减少其下标). 这个方法的调用会减少list(toIndex - fromIndex)
     * 个元素. (如果toIndex==fromIndex), 这个操作将没有任何效果.
     *
     * @param fromIndex index of first element to be removed 第一个被移除的元素的索引
     * @param toIndex index after last element to be removed 最后一个被移除的元素的下一个索引
     * @throws IndexOutOfBoundsException if fromIndex or toIndex out of range
     *         ({@code fromIndex < 0 || toIndex > size() || toIndex < fromIndex})
     * 数组越界的异常, 有几种情况, 如果fromIndex或者toIndex超出数组范围, 或者
     * fromIndex小于0, 或者toIndex大于size(数组的大小), 或者toIndex小于fromInde
     */
    void removeRange(int fromIndex, int toIndex) {
        synchronized (lock) {
            Object[] es = getArray();
            int len = es.length;

            if (fromIndex < 0 || toIndex > len || toIndex < fromIndex)
                throw new IndexOutOfBoundsException();
            int newlen = len - (toIndex - fromIndex);
            int numMoved = len - toIndex;
            if (numMoved == 0)
                setArray(Arrays.copyOf(es, newlen));
            else {
                Object[] newElements = new Object[newlen];
                System.arraycopy(es, 0, newElements, 0, fromIndex);
                System.arraycopy(es, toIndex, newElements,
                                 fromIndex, numMoved);
                setArray(newElements);
            }
        }
    }

    /**
     * Appends the element, if not present.
     *
     * 如果(指定的元素)不存在, 就追加(这个元素到list中).
     *
     * @param e element to be added to this list, if absent 如果(指定的元素)不存在, 就添加到当前list中
     * @return {@code true} if the element was added 如果元素被添加, 就返回true
     */
    public boolean addIfAbsent(E e) {
        Object[] snapshot = getArray();
        return indexOfRange(e, snapshot, 0, snapshot.length) < 0
            && addIfAbsent(e, snapshot);
    }

    /**
     * A version of addIfAbsent using the strong hint that given
     * recent snapshot does not contain e.
     *
     * 一个当给定的快照不包含e时使用的addIfAbsent版本
     */
    private boolean addIfAbsent(E e, Object[] snapshot) {
        synchronized (lock) {
            Object[] current = getArray();
            int len = current.length;
            if (snapshot != current) {
                // Optimize for lost race to another addXXX operation
                // 对与其他addXXX操作竞争失败情况的优化
                int common = Math.min(snapshot.length, len);
                for (int i = 0; i < common; i++)
                    if (current[i] != snapshot[i]
                        && Objects.equals(e, current[i]))
                        return false;
                if (indexOfRange(e, current, common, len) >= 0)
                        return false;
            }
            Object[] newElements = Arrays.copyOf(current, len + 1);
            newElements[len] = e;
            setArray(newElements);
            return true;
        }
    }

    /**
     * Returns {@code true} if this list contains all of the elements of the
     * specified collection.
     *
     * 如果当前list包含指定集合里的全部元素, 就返回true
     *
     * @param c collection to be checked for containment in this list 要检测是否包含在当前list中的集合
     * @return {@code true} if this list contains all of the elements of the
     *         specified collection 如果当前list包含指定集合里的全部元素, 就返回true
     * @throws NullPointerException if the specified collection is null 如果指定集合为null
     * @see #contains(Object)
     */
    public boolean containsAll(Collection<?> c) {
        Object[] es = getArray();
        int len = es.length;
        for (Object e : c) {
            if (indexOfRange(e, es, 0, len) < 0)
                return false;
        }
        return true;
    }

    /**
     * Removes from this list all of its elements that are contained in
     * the specified collection. This is a particularly expensive operation
     * in this class because of the need for an internal temporary array.
     *
     * 移除list中所有被包含在指定集合中的元素. 这是在这个类中一个非常耗费资源的操作,
     * 因为(这个操作)需要一个临时的内部数组.
     *
     * @param c collection containing elements to be removed from this list 包含着将被从list中移除的元素的集合
     * @return {@code true} if this list changed as a result of the call 如果这个方法的调用改变了list, 就返回true
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection 如果list中有一个元素的类型与指定集合中的类型不相容
     * (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     * (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null 如果list中包含一个null元素并且指定集合中不允许存在null元素, 或者指定的集合为null
     * @see #remove(Object)
     */
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return bulkRemove(e -> c.contains(e));
    }

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection.  In other words, removes from this list all of
     * its elements that are not contained in the specified collection.
     *
     * 仅保留list中被指定集合包含的元素. 换句话说, 移除list中所有没有被包含在
     * 指定集合中的元素.
     *
     * @param c collection containing elements to be retained in this list 包含list中将要被保留下来的元素的集合
     * @return {@code true} if this list changed as a result of the call 如果这个方法的调用改变了list, 就返回true
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection 如果list中有一个元素的类型与指定集合中的类型不相容
     * (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     * (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null 如果list中包含一个null元素并且指定集合中不允许存在null元素, 或者指定的集合为null
     * @see #remove(Object)
     */
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return bulkRemove(e -> !c.contains(e));
    }

    /**
     * Appends all of the elements in the specified collection that
     * are not already contained in this list, to the end of
     * this list, in the order that they are returned by the
     * specified collection's iterator.
     *
     * 根据指定集合的迭代器返回的顺序, 在当前list的末尾, 追加指定集合中所有
     * 还未被包含在当前集合中的元素.
     *
     * @param c collection containing elements to be added to this list 包含将要被添加进当前list的集合
     * @return the number of elements added 被添加的元素的数量
     * @throws NullPointerException if the specified collection is null 如果指定的集合为null
     * @see #addIfAbsent(Object)
     */
    public int addAllAbsent(Collection<? extends E> c) {
        Object[] cs = c.toArray();
        if (cs.length == 0)
            return 0;
        synchronized (lock) {
            Object[] es = getArray();
            int len = es.length;
            int added = 0;
            // uniquify and compact elements in cs
            for (int i = 0; i < cs.length; ++i) {
                Object e = cs[i];
                if (indexOfRange(e, es, 0, len) < 0 &&
                    indexOfRange(e, cs, 0, added) < 0)
                    cs[added++] = e;
            }
            if (added > 0) {
                Object[] newElements = Arrays.copyOf(es, len + added);
                System.arraycopy(cs, 0, newElements, len, added);
                setArray(newElements);
            }
            return added;
        }
    }

    /**
     * Removes all of the elements from this list.
     * The list will be empty after this call returns.
     *
     * 移除list中的全部元素. 这个方法调用后会变成空的list.
     */
    public void clear() {
        synchronized (lock) {
            setArray(new Object[0]);
        }
    }

    /**
     * Appends all of the elements in the specified collection to the end
     * of this list, in the order that they are returned by the specified
     * collection's iterator.
     *
     * 根据指定集合的迭代器返回的顺序, 追加指定集合中的全部元素到当前list的末尾.
     *
     * @param c collection containing elements to be added to this list 将要被添加进list中的元素的Collection集合
     * @return {@code true} if this list changed as a result of the call 如果list被该方法的调用改变了, 就返回true
     * @throws NullPointerException if the specified collection is null 如果指定的Collection集合是null
     * @see #add(Object)
     */
    public boolean addAll(Collection<? extends E> c) {
        Object[] cs = (c.getClass() == CopyOnWriteArrayList.class) ?
            ((CopyOnWriteArrayList<?>)c).getArray() : c.toArray();
        if (cs.length == 0)
            return false;
        synchronized (lock) {
            Object[] es = getArray();
            int len = es.length;
            Object[] newElements;
            if (len == 0 && cs.getClass() == Object[].class)
                newElements = cs;
            else {
                newElements = Arrays.copyOf(es, len + cs.length);
                System.arraycopy(cs, 0, newElements, len, cs.length);
            }
            setArray(newElements);
            return true;
        }
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in this list in the order that they are returned by the
     * specified collection's iterator.
     *
     *
     * 在指定的位置插入指定Collection集合的全部元素到当前list中. 向右移动当前位置的
     * 的元素以及当前位置之后的元素(如果存在)(增加其下标). 新的元素会按照指定的Collection
     * 迭代器返回的顺序出现在list当中.
     *
     * @param index index at which to insert the first element
     *        from the specified collection 将要插入的指定的Collection集合的第一个元素所在的位置(下标)
     * @param c collection containing elements to be added to this list 包含将要插入到当前list中所有元素的集合
     * @return {@code true} if this list changed as a result of the call 如果list被改变了, 将会返回true作为调用的结果
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null 如果指定的集合为null
     * @see #add(int,Object)
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        Object[] cs = c.toArray();
        synchronized (lock) {
            Object[] es = getArray();
            int len = es.length;
            if (index > len || index < 0)
                throw new IndexOutOfBoundsException(outOfBounds(index, len));
            if (cs.length == 0)
                return false;
            int numMoved = len - index;
            Object[] newElements;
            if (numMoved == 0)
                newElements = Arrays.copyOf(es, len + cs.length);
            else {
                newElements = new Object[len + cs.length];
                System.arraycopy(es, 0, newElements, 0, index);
                System.arraycopy(es, index,
                                 newElements, index + cs.length,
                                 numMoved);
            }
            System.arraycopy(cs, 0, newElements, index, cs.length);
            setArray(newElements);
            return true;
        }
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        for (Object x : getArray()) {
            @SuppressWarnings("unchecked") E e = (E) x;
            action.accept(e);
        }
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    public boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        return bulkRemove(filter);
    }

    // A tiny bit set implementation

    private static long[] nBits(int n) {
        return new long[((n - 1) >> 6) + 1];
    }
    private static void setBit(long[] bits, int i) {
        bits[i >> 6] |= 1L << i;
    }
    private static boolean isClear(long[] bits, int i) {
        return (bits[i >> 6] & (1L << i)) == 0;
    }

    private boolean bulkRemove(Predicate<? super E> filter) {
        synchronized (lock) {
            return bulkRemove(filter, 0, getArray().length);
        }
    }

    boolean bulkRemove(Predicate<? super E> filter, int i, int end) {
        // assert Thread.holdsLock(lock);
        final Object[] es = getArray();
        // Optimize for initial run of survivors 对未移除的元素的初始化运行的优化
        for (; i < end && !filter.test(elementAt(es, i)); i++)
            ;
        if (i < end) {
            final int beg = i;
            final long[] deathRow = nBits(end - beg);
            int deleted = 1;
            deathRow[0] = 1L;   // set bit 0
            for (i = beg + 1; i < end; i++)
                if (filter.test(elementAt(es, i))) {
                    setBit(deathRow, i - beg);
                    deleted++;
                }
            // Did filter reentrantly modify the list? 过滤器是否可以重入地修改list?
            if (es != getArray())
                throw new ConcurrentModificationException();
            final Object[] newElts = Arrays.copyOf(es, es.length - deleted);
            int w = beg;
            for (i = beg; i < end; i++)
                if (isClear(deathRow, i - beg))
                    newElts[w++] = es[i];
            System.arraycopy(es, i, newElts, w, es.length - i);
            setArray(newElts);
            return true;
        } else {
            if (es != getArray())
                throw new ConcurrentModificationException();
            return false;
        }
    }

    public void replaceAll(UnaryOperator<E> operator) {
        synchronized (lock) {
            replaceAllRange(operator, 0, getArray().length);
        }
    }

    void replaceAllRange(UnaryOperator<E> operator, int i, int end) {
        // assert Thread.holdsLock(lock);
        Objects.requireNonNull(operator);
        final Object[] es = getArray().clone();
        for (; i < end; i++)
            es[i] = operator.apply(elementAt(es, i));
        setArray(es);
    }

    public void sort(Comparator<? super E> c) {
        synchronized (lock) {
            sortRange(c, 0, getArray().length);
        }
    }

    @SuppressWarnings("unchecked")
    void sortRange(Comparator<? super E> c, int i, int end) {
        // assert Thread.holdsLock(lock);
        final Object[] es = getArray().clone();
        Arrays.sort(es, i, end, (Comparator<Object>)c);
        setArray(es);
    }

    /**
     * Saves this list to a stream (that is, serializes it).
     *
     * 保存ArrayList实例的状态到一个(输出)流当中(意思就是, 进行序列化操作)
     *
     * @param s the stream
     * @throws java.io.IOException if an I/O error occurs 如果发生了I/O错误
     * @serialData The length of the array backing the list is emitted
     *               (int), followed by all of its elements (each an Object)
     *               in the proper order.
     *               先(向流中)传入ArrayList实例的数组长度(整型), 然后紧跟着按照恰
     *               当的顺序传入list数组中的元素
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {

        s.defaultWriteObject();

        Object[] es = getArray();
        // Write out array length 输出数组的长度
        s.writeInt(es.length);

        // Write out all elements in the proper order. 按顺序输出全部元素
        for (Object element : es)
            s.writeObject(element);
    }

    /**
     * Reconstitutes this list from a stream (that is, deserializes it).
     *
     * 从一个流中重新构建一个ArrayList实例(意思就是, 进行反序列化操作)
     *
     * @param s the stream
     * @throws ClassNotFoundException if the class of a serialized object
     *         could not be found 如果一个反序列化对象的类型无法被找到
     * @throws java.io.IOException if an I/O error occurs 如果发生I/O错误
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {

        s.defaultReadObject();

        // bind to new lock 绑定一个新的锁
        resetLock();

        // Read in array length and allocate array 读入数组长度并申请数组空间
        int len = s.readInt();
        SharedSecrets.getJavaObjectInputStreamAccess().checkArray(s, Object[].class, len);
        Object[] es = new Object[len];

        // Read in all elements in the proper order. 按顺序读入全部的元素
        for (int i = 0; i < len; i++)
            es[i] = s.readObject();
        setArray(es);
    }

    /**
     * Returns a string representation of this list.  The string
     * representation consists of the string representations of the list's
     * elements in the order they are returned by its iterator, enclosed in
     * square brackets ({@code "[]"}).  Adjacent elements are separated by
     * the characters {@code ", "} (comma and space).  Elements are
     * converted to strings as by {@link String#valueOf(Object)}.
     *
     * 返回当前list的一个字符串表示. 这个字符串包含了list中元素的字符串表示, 顺序是
     * 迭代器返回的顺序, 使用方括号"[]"包裹起来. 相邻的元素使用符号", "(逗号和空格)
     * (区分开). 元素使用String.valueOf(Object)方法来转换到字符串.
     *
     * @return a string representation of this list 当前list的字符串表示
     */
    public String toString() {
        return Arrays.toString(getArray());
    }

    /**
     * Compares the specified object with this list for equality.
     * Returns {@code true} if the specified object is the same object
     * as this object, or if it is also a {@link List} and the sequence
     * of elements returned by an {@linkplain List#iterator() iterator}
     * over the specified list is the same as the sequence returned by
     * an iterator over this list.  The two sequences are considered to
     * be the same if they have the same length and corresponding
     * elements at the same position in the sequence are <em>equal</em>.
     * Two elements {@code e1} and {@code e2} are considered
     * <em>equal</em> if {@code Objects.equals(e1, e2)}.
     *
     * 比较指定对象与当前list是否相等. 如果指定对象和当前的对象相同, 或者如果
     * (指定队列)也是一个List并且通过迭代器返回的元素序列与当前list的迭代器返回
     * 的元素序列相同, 就返回true. 两个序列, 如果他们有相同的长度并且相应的元素在
     * 序列里相同的位置, 那么他们被认为是相等的. 两个元素e1和e2, 如果Objects.equals(e1, e2),
     * 那么他们被认为是相等的.
     *
     * @param o the object to be compared for equality with this list 要与当前list比较是否相等的对象
     * @return {@code true} if the specified object is equal to this list 如果指定的对象与当前list相等, 就返回true
     */
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof List))
            return false;

        List<?> list = (List<?>)o;
        Iterator<?> it = list.iterator();
        for (Object element : getArray())
            if (!it.hasNext() || !Objects.equals(element, it.next()))
                return false;
        return !it.hasNext();
    }

    private static int hashCodeOfRange(Object[] es, int from, int to) {
        int hashCode = 1;
        for (int i = from; i < to; i++) {
            Object x = es[i];
            hashCode = 31 * hashCode + (x == null ? 0 : x.hashCode());
        }
        return hashCode;
    }

    /**
     * Returns the hash code value for this list.
     *
     * 返回当前list的哈希值.
     *
     * <p>This implementation uses the definition in {@link List#hashCode}.
     *
     * 当前的实现使用的是List.hashCode()的定义.
     *
     * @return the hash code value for this list 当前list的哈希值
     */
    public int hashCode() {
        Object[] es = getArray();
        return hashCodeOfRange(es, 0, es.length);
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * 按适当的顺序返回当前list中元素的迭代器.
     *
     * <p>The returned iterator provides a snapshot of the state of the list
     * when the iterator was constructed. No synchronization is needed while
     * traversing the iterator. The iterator does <em>NOT</em> support the
     * {@code remove} method.
     *
     * 返回的迭代器在构建的时候提供了一个list的状态的快照
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    public Iterator<E> iterator() {
        return new COWIterator<E>(getArray(), 0);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The returned iterator provides a snapshot of the state of the list
     * when the iterator was constructed. No synchronization is needed while
     * traversing the iterator. The iterator does <em>NOT</em> support the
     * {@code remove}, {@code set} or {@code add} methods.
     */
    public ListIterator<E> listIterator() {
        return new COWIterator<E>(getArray(), 0);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The returned iterator provides a snapshot of the state of the list
     * when the iterator was constructed. No synchronization is needed while
     * traversing the iterator. The iterator does <em>NOT</em> support the
     * {@code remove}, {@code set} or {@code add} methods.
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public ListIterator<E> listIterator(int index) {
        Object[] es = getArray();
        int len = es.length;
        if (index < 0 || index > len)
            throw new IndexOutOfBoundsException(outOfBounds(index, len));

        return new COWIterator<E>(es, index);
    }

    /**
     * Returns a {@link Spliterator} over the elements in this list.
     *
     * <p>The {@code Spliterator} reports {@link Spliterator#IMMUTABLE},
     * {@link Spliterator#ORDERED}, {@link Spliterator#SIZED}, and
     * {@link Spliterator#SUBSIZED}.
     *
     * <p>The spliterator provides a snapshot of the state of the list
     * when the spliterator was constructed. No synchronization is needed while
     * operating on the spliterator.
     *
     * @return a {@code Spliterator} over the elements in this list
     * @since 1.8
     */
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator
            (getArray(), Spliterator.IMMUTABLE | Spliterator.ORDERED);
    }

    static final class COWIterator<E> implements ListIterator<E> {
        /** Snapshot of the array */
        private final Object[] snapshot;
        /** Index of element to be returned by subsequent call to next.  */
        private int cursor;

        COWIterator(Object[] es, int initialCursor) {
            cursor = initialCursor;
            snapshot = es;
        }

        public boolean hasNext() {
            return cursor < snapshot.length;
        }

        public boolean hasPrevious() {
            return cursor > 0;
        }

        @SuppressWarnings("unchecked")
        public E next() {
            if (! hasNext())
                throw new NoSuchElementException();
            return (E) snapshot[cursor++];
        }

        @SuppressWarnings("unchecked")
        public E previous() {
            if (! hasPrevious())
                throw new NoSuchElementException();
            return (E) snapshot[--cursor];
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        /**
         * Not supported. Always throws UnsupportedOperationException.
         * @throws UnsupportedOperationException always; {@code remove}
         *         is not supported by this iterator.
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * Not supported. Always throws UnsupportedOperationException.
         * @throws UnsupportedOperationException always; {@code set}
         *         is not supported by this iterator.
         */
        public void set(E e) {
            throw new UnsupportedOperationException();
        }

        /**
         * Not supported. Always throws UnsupportedOperationException.
         * @throws UnsupportedOperationException always; {@code add}
         *         is not supported by this iterator.
         */
        public void add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            final int size = snapshot.length;
            int i = cursor;
            cursor = size;
            for (; i < size; i++)
                action.accept(elementAt(snapshot, i));
        }
    }

    /**
     * Returns a view of the portion of this list between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * The returned list is backed by this list, so changes in the
     * returned list are reflected in this list.
     *
     * <p>The semantics of the list returned by this method become
     * undefined if the backing list (i.e., this list) is modified in
     * any way other than via the returned list.
     *
     * @param fromIndex low endpoint (inclusive) of the subList
     * @param toIndex high endpoint (exclusive) of the subList
     * @return a view of the specified range within this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public List<E> subList(int fromIndex, int toIndex) {
        synchronized (lock) {
            Object[] es = getArray();
            int len = es.length;
            int size = toIndex - fromIndex;
            if (fromIndex < 0 || toIndex > len || size < 0)
                throw new IndexOutOfBoundsException();
            return new COWSubList(es, fromIndex, size);
        }
    }

    /**
     * Sublist for CopyOnWriteArrayList.
     */
    private class COWSubList implements List<E>, RandomAccess {
        private final int offset;
        private int size;
        private Object[] expectedArray;

        COWSubList(Object[] es, int offset, int size) {
            // assert Thread.holdsLock(lock);
            expectedArray = es;
            this.offset = offset;
            this.size = size;
        }

        private void checkForComodification() {
            // assert Thread.holdsLock(lock);
            if (getArray() != expectedArray)
                throw new ConcurrentModificationException();
        }

        private Object[] getArrayChecked() {
            // assert Thread.holdsLock(lock);
            Object[] a = getArray();
            if (a != expectedArray)
                throw new ConcurrentModificationException();
            return a;
        }

        private void rangeCheck(int index) {
            // assert Thread.holdsLock(lock);
            if (index < 0 || index >= size)
                throw new IndexOutOfBoundsException(outOfBounds(index, size));
        }

        private void rangeCheckForAdd(int index) {
            // assert Thread.holdsLock(lock);
            if (index < 0 || index > size)
                throw new IndexOutOfBoundsException(outOfBounds(index, size));
        }

        public Object[] toArray() {
            final Object[] es;
            final int offset;
            final int size;
            synchronized (lock) {
                es = getArrayChecked();
                offset = this.offset;
                size = this.size;
            }
            return Arrays.copyOfRange(es, offset, offset + size);
        }

        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            final Object[] es;
            final int offset;
            final int size;
            synchronized (lock) {
                es = getArrayChecked();
                offset = this.offset;
                size = this.size;
            }
            if (a.length < size)
                return (T[]) Arrays.copyOfRange(
                        es, offset, offset + size, a.getClass());
            else {
                System.arraycopy(es, offset, a, 0, size);
                if (a.length > size)
                    a[size] = null;
                return a;
            }
        }

        public int indexOf(Object o) {
            final Object[] es;
            final int offset;
            final int size;
            synchronized (lock) {
                es = getArrayChecked();
                offset = this.offset;
                size = this.size;
            }
            int i = indexOfRange(o, es, offset, offset + size);
            return (i == -1) ? -1 : i - offset;
        }

        public int lastIndexOf(Object o) {
            final Object[] es;
            final int offset;
            final int size;
            synchronized (lock) {
                es = getArrayChecked();
                offset = this.offset;
                size = this.size;
            }
            int i = lastIndexOfRange(o, es, offset, offset + size);
            return (i == -1) ? -1 : i - offset;
        }

        public boolean contains(Object o) {
            return indexOf(o) >= 0;
        }

        public boolean containsAll(Collection<?> c) {
            final Object[] es;
            final int offset;
            final int size;
            synchronized (lock) {
                es = getArrayChecked();
                offset = this.offset;
                size = this.size;
            }
            for (Object o : c)
                if (indexOfRange(o, es, offset, offset + size) < 0)
                    return false;
            return true;
        }

        public boolean isEmpty() {
            return size() == 0;
        }

        public String toString() {
            return Arrays.toString(toArray());
        }

        public int hashCode() {
            final Object[] es;
            final int offset;
            final int size;
            synchronized (lock) {
                es = getArrayChecked();
                offset = this.offset;
                size = this.size;
            }
            return hashCodeOfRange(es, offset, offset + size);
        }

        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (!(o instanceof List))
                return false;
            Iterator<?> it = ((List<?>)o).iterator();

            final Object[] es;
            final int offset;
            final int size;
            synchronized (lock) {
                es = getArrayChecked();
                offset = this.offset;
                size = this.size;
            }

            for (int i = offset, end = offset + size; i < end; i++)
                if (!it.hasNext() || !Objects.equals(es[i], it.next()))
                    return false;
            return !it.hasNext();
        }

        public E set(int index, E element) {
            synchronized (lock) {
                rangeCheck(index);
                checkForComodification();
                E x = CopyOnWriteArrayList.this.set(offset + index, element);
                expectedArray = getArray();
                return x;
            }
        }

        public E get(int index) {
            synchronized (lock) {
                rangeCheck(index);
                checkForComodification();
                return CopyOnWriteArrayList.this.get(offset + index);
            }
        }

        public int size() {
            synchronized (lock) {
                checkForComodification();
                return size;
            }
        }

        public boolean add(E element) {
            synchronized (lock) {
                checkForComodification();
                CopyOnWriteArrayList.this.add(offset + size, element);
                expectedArray = getArray();
                size++;
            }
            return true;
        }

        public void add(int index, E element) {
            synchronized (lock) {
                checkForComodification();
                rangeCheckForAdd(index);
                CopyOnWriteArrayList.this.add(offset + index, element);
                expectedArray = getArray();
                size++;
            }
        }

        public boolean addAll(Collection<? extends E> c) {
            synchronized (lock) {
                final Object[] oldArray = getArrayChecked();
                boolean modified =
                    CopyOnWriteArrayList.this.addAll(offset + size, c);
                size += (expectedArray = getArray()).length - oldArray.length;
                return modified;
            }
        }

        public boolean addAll(int index, Collection<? extends E> c) {
            synchronized (lock) {
                rangeCheckForAdd(index);
                final Object[] oldArray = getArrayChecked();
                boolean modified =
                    CopyOnWriteArrayList.this.addAll(offset + index, c);
                size += (expectedArray = getArray()).length - oldArray.length;
                return modified;
            }
        }

        public void clear() {
            synchronized (lock) {
                checkForComodification();
                removeRange(offset, offset + size);
                expectedArray = getArray();
                size = 0;
            }
        }

        public E remove(int index) {
            synchronized (lock) {
                rangeCheck(index);
                checkForComodification();
                E result = CopyOnWriteArrayList.this.remove(offset + index);
                expectedArray = getArray();
                size--;
                return result;
            }
        }

        public boolean remove(Object o) {
            synchronized (lock) {
                checkForComodification();
                int index = indexOf(o);
                if (index == -1)
                    return false;
                remove(index);
                return true;
            }
        }

        public Iterator<E> iterator() {
            return listIterator(0);
        }

        public ListIterator<E> listIterator() {
            return listIterator(0);
        }

        public ListIterator<E> listIterator(int index) {
            synchronized (lock) {
                checkForComodification();
                rangeCheckForAdd(index);
                return new COWSubListIterator<E>(
                    CopyOnWriteArrayList.this, index, offset, size);
            }
        }

        public List<E> subList(int fromIndex, int toIndex) {
            synchronized (lock) {
                checkForComodification();
                if (fromIndex < 0 || toIndex > size || fromIndex > toIndex)
                    throw new IndexOutOfBoundsException();
                return new COWSubList(expectedArray, fromIndex + offset, toIndex - fromIndex);
            }
        }

        public void forEach(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            int i, end; final Object[] es;
            synchronized (lock) {
                es = getArrayChecked();
                i = offset;
                end = i + size;
            }
            for (; i < end; i++)
                action.accept(elementAt(es, i));
        }

        public void replaceAll(UnaryOperator<E> operator) {
            synchronized (lock) {
                checkForComodification();
                replaceAllRange(operator, offset, offset + size);
                expectedArray = getArray();
            }
        }

        public void sort(Comparator<? super E> c) {
            synchronized (lock) {
                checkForComodification();
                sortRange(c, offset, offset + size);
                expectedArray = getArray();
            }
        }

        public boolean removeAll(Collection<?> c) {
            Objects.requireNonNull(c);
            return bulkRemove(e -> c.contains(e));
        }

        public boolean retainAll(Collection<?> c) {
            Objects.requireNonNull(c);
            return bulkRemove(e -> !c.contains(e));
        }

        public boolean removeIf(Predicate<? super E> filter) {
            Objects.requireNonNull(filter);
            return bulkRemove(filter);
        }

        private boolean bulkRemove(Predicate<? super E> filter) {
            synchronized (lock) {
                final Object[] oldArray = getArrayChecked();
                boolean modified = CopyOnWriteArrayList.this.bulkRemove(
                    filter, offset, offset + size);
                size += (expectedArray = getArray()).length - oldArray.length;
                return modified;
            }
        }

        public Spliterator<E> spliterator() {
            synchronized (lock) {
                return Spliterators.spliterator(
                        getArrayChecked(), offset, offset + size,
                        Spliterator.IMMUTABLE | Spliterator.ORDERED);
            }
        }

    }

    private static class COWSubListIterator<E> implements ListIterator<E> {
        private final ListIterator<E> it;
        private final int offset;
        private final int size;

        COWSubListIterator(List<E> l, int index, int offset, int size) {
            this.offset = offset;
            this.size = size;
            it = l.listIterator(index + offset);
        }

        public boolean hasNext() {
            return nextIndex() < size;
        }

        public E next() {
            if (hasNext())
                return it.next();
            else
                throw new NoSuchElementException();
        }

        public boolean hasPrevious() {
            return previousIndex() >= 0;
        }

        public E previous() {
            if (hasPrevious())
                return it.previous();
            else
                throw new NoSuchElementException();
        }

        public int nextIndex() {
            return it.nextIndex() - offset;
        }

        public int previousIndex() {
            return it.previousIndex() - offset;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void set(E e) {
            throw new UnsupportedOperationException();
        }

        public void add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            while (hasNext()) {
                action.accept(it.next());
            }
        }
    }

    /** Initializes the lock; for use when deserializing or cloning. */
    private void resetLock() {
        Field lockField = java.security.AccessController.doPrivileged(
            (java.security.PrivilegedAction<Field>) () -> {
                try {
                    Field f = CopyOnWriteArrayList.class
                        .getDeclaredField("lock");
                    f.setAccessible(true);
                    return f;
                } catch (ReflectiveOperationException e) {
                    throw new Error(e);
                }});
        try {
            lockField.set(this, new Object());
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
    }
}
