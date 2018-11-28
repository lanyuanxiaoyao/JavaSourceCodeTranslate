/*
 * Copyright (c) 1997, 2018, Oracle and/or its affiliates. All rights reserved.
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

package java.util;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import jdk.internal.misc.SharedSecrets;

/**
 * Resizable-array implementation of the {@code List} interface.  Implements
 * all optional list operations, and permits all elements, including
 * {@code null}.  In addition to implementing the {@code List} interface,
 * this class provides methods to manipulate the size of the array that is
 * used internally to store the list.  (This class is roughly equivalent to
 * {@code Vector}, except that it is unsynchronized.)
 *
 * ArrayList是一个实现了List接口的可变长数组. ArrayList实现了可选List接口的所有操作, 
 * 允许插入任意元素, 包括空值(null). 除了实现了List接口的方法, ArrayList还提供了操
 * 控内部数组的方法. (ArrayList和Vector差不多, 区别在于ArrayList不是同步(线程安全)
 * 的)
 *
 * <p>The {@code size}, {@code isEmpty}, {@code get}, {@code set},
 * {@code iterator}, and {@code listIterator} operations run in constant
 * time.  The {@code add} operation runs in <i>amortized constant time</i>,
 * that is, adding n elements requires O(n) time.  All of the other operations
 * run in linear time (roughly speaking).  The constant factor is low compared
 * to that for the {@code LinkedList} implementation.
 *
 * size(), isEmpty(), get(), set(), iterator()和 listIterator() 这几个方法的执行
 * 只需要花费常数级别的时间. add() 操作同样花费常数级别的时间, 这意味着增加n个元素只
 * 需要O(n)的时间复杂度. 所有其他操作都将在线性的时间复杂度内完成(粗略考虑的情况下).
 * 与LinkedList相比, ArrayList的常数因子要更小.
 *
 * <p>Each {@code ArrayList} instance has a <i>capacity</i>.  The capacity is
 * the size of the array used to store the elements in the list.  It is always
 * at least as large as the list size.  As elements are added to an ArrayList,
 * its capacity grows automatically.  The details of the growth policy are not
 * specified beyond the fact that adding an element has constant amortized
 * time cost.
 *
 * 每一个ArrayList实例都有一个容量, 这个容量表示的是list中存储的元素的数量, 其大小
 * 至少和list的大小相同. 当一个元素被添加进ArrayList的时候, 其容量会自动增长. 事实
 * 上, 容量的自动增长会花费常数级别的时间, 但在ArrayList中并没有指定容量自动增长的
 * 策略.
 *
 * <p>An application can increase the capacity of an {@code ArrayList} instance
 * before adding a large number of elements using the {@code ensureCapacity}
 * operation.  This may reduce the amount of incremental reallocation.
 *
 * 我们可以使用 ensureCapacity() 方法在准备添加大量的元素之前手动增加list的容量, 这
 * 样可以减少容量自动增长的执行次数.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access an {@code ArrayList} instance concurrently,
 * and at least one of the threads modifies the list structurally, it
 * <i>must</i> be synchronized externally.  (A structural modification is
 * any operation that adds or deletes one or more elements, or explicitly
 * resizes the backing array; merely setting the value of an element is not
 * a structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the list.
 *
 * (划重点)ArrayList的实现不是同步(线程安全)的. 如果有多个线程并发访问同一个ArrayList
 * 实例, 并且有一个线程修改了该实例的结构(内容), 你必须在ArrayList外部保证并发操
 * 作的线程安全. (一个结构化修改意为任意的增加或删除一个或多个元素的操作, 或显式
 * 调整存储数组的大小; 仅仅设置某个元素的值不是结构化修改.) (在外部保证并发操作的安全)
 * 通常是通过封装ArrayList, 在一些对象上进行同步来实现的.
 *
 * If no such object exists, the list should be "wrapped" using the
 * {@link Collections#synchronizedList Collections.synchronizedList}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the list:<pre>
 *   List list = Collections.synchronizedList(new ArrayList(...));</pre>
 *
 * 如果没有(在外部保证线程安全)这样的对象存在, 应该使用 Collections.synchronizedList()
 * 方法将这个list包装起来. 这个操作最好在list实例创建的时候就进行, 避免非同步
 * (线程安全)的操作访问该list.
 * List list = Collections.synchronizedList(new ArrayList(...));
 *
 * <p id="fail-fast">
 * The iterators returned by this class's {@link #iterator() iterator} and
 * {@link #listIterator(int) listIterator} methods are <em>fail-fast</em>:
 * if the list is structurally modified at any time after the iterator is
 * created, in any way except through the iterator's own
 * {@link ListIterator#remove() remove} or
 * {@link ListIterator#add(Object) add} methods, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of
 * concurrent modification, the iterator fails quickly and cleanly, rather
 * than risking arbitrary, non-deterministic behavior at an undetermined
 * time in the future.
 *
 * 通过iterator() 和 listIterator(int) 两个方法得到的list的迭代器是快速失败的: 
 * 如果list在迭代器创建之后的任何时候发生了结构化修改, 除了使用迭代器自身提供的
 * remove() 和 add(Object) 方法, 迭代器会抛出ConcurrentModificationException
 * 异常. 因此, 在面对并发修改, 迭代器会立刻(返回)失败, 而不是在未来某个不确定的时间
 * 出现有风险的, 不确定的行为.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:  <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * 请注意, 迭代器快速失败的行为并不能提供保证, 一般来说, 不可能在不对并发修改进行同步
 * 的情况下做出任何保证. 迭代器发生快速失败的时候会尽可能抛出ConcurrentModificationException.
 * 因此, 依赖这个异常来保证其正确性的程序可能会出错: 迭代器的快速失败行为仅仅应该被用于
 * 调试.
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/java.base/java/util/package-summary.html#CollectionsFramework">
 * Java Collections Framework</a>.
 *
 * @param <E> the type of elements in this list
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see     Collection
 * @see     List
 * @see     LinkedList
 * @see     Vector
 * @since   1.2
 *
 * @translator LanyuanXiaoyao
 * @date 2018.10.25
 */
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    private static final long serialVersionUID = 8683452581122892189L;

    /**
     * Default initial capacity.
     *
     * 默认的初始化容量
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Shared empty array instance used for empty instances.
     *
     * 用于表示空实例的空数组实例
     */
    private static final Object[] EMPTY_ELEMENTDATA = {};

    /**
     * Shared empty array instance used for default sized empty instances. We
     * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
     * first element is added.
     *
     * 用于表示默认空实例的空数组实例. 我们用这个属性来与EMPTY_ELEMENTDATA属性区分开
     * 来, 让我们可以知道第一次添加元素的时候需要增长多少容量.
     *
     * (尽管默认的初始化容量是10, 但是Java仍然不打算在还未使用list的时候就白白浪费
     * 掉这宝贵的内存, 所以就有了这个属性, 这个属性表示的是构造了list但还没使用之前
     * 的"空"list, 只有在第一个元素被添加进来的时候, 才会真正去申请容量大小的内存)
     */
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    /**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer. Any
     * empty ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * will be expanded to DEFAULT_CAPACITY when the first element is added.
     *
     * ArrayList里的元素都被存储在这个数组缓存里. ArrayList的容量就是这个数组缓存的
     * 容量. 每一个空的ArrayList, 并且elementData等于DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * 的时候, 会在第一次添加元素的时候自动把容量增加到DEFAULT_CAPACITY(默认的初始
     * 化容量)
     */
    transient Object[] elementData; // non-private to simplify nested class access 简化嵌套类访问的非私有方法

    /**
     * The size of the ArrayList (the number of elements it contains).
     *
     * ArrayList的大小(ArrayList包含的元素的数量)
     *
     * @serial
     */
    private int size;

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * 构造一个指定的容量的空list
     *
     * @param  initialCapacity  the initial capacity of the list list初始化的容量
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative 如果指定的容量是一个非法的值
     */
    public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            // 当指定的初始化容量的值小于0的时候, 抛出异常
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     *
     * 构造一个容量为10的空list
     */
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * 根据给定的集合构造一个包含其所有元素的ArrayList, 元素的顺序是按指定
     * 集合的迭代器返回的顺序来定.
     *
     * @param c the collection whose elements are to be placed into this list 将要被添加进list的元素的集合
     * @throws NullPointerException if the specified collection is null 如果指定的集合是null
     */
    public ArrayList(Collection<? extends E> c) {
        elementData = c.toArray();
        if ((size = elementData.length) != 0) {
            // defend against c.toArray (incorrectly) not returning Object[] 防止 c.toArray() (错误地)不返回Object[]对象
            // (see e.g. https://bugs.openjdk.java.net/browse/JDK-6260652)
            // 简单说明一下这个bug, 在JDK文档中定义了如果List子类使用了不带参数的toArray()方法, 
            // 那么就应该返回Object[]类型的数组, 但是如果使用某个类型的数组来构建一个新的ArrayList, 
            // 如String[], 那么使用toArray()返回的仍然是String[]类型数组, 这将导致在之后的代码如果
            // 试图向返回的数组中插入非String类型的值, 就会报错. 在之前的代码里, toArray()方法是
            // 使用ArrayList的clone()方法来构建返回的数组的.
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            // replace with empty array. 使用空数组代替
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }

    /**
     * Trims the capacity of this {@code ArrayList} instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of an {@code ArrayList} instance.
     *
     * 把ArrayList实例的容量设置为实例当前的实际大小. 程序可以使用这个操作来减少ArrayList
     * 实例占用的存储
     *
     * (这是因为ArrayList是一批一批来申请内存, 而不是需要一个再申请一个, 所以在后期, 每增长一次
     * 都会导致数组缓存中有大量的位置是空着的, 如果你的代码确定了这个ArrayList不再修改或者只增长
     * 少量的内容, 那么就可以使用这个方法来释放没有被使用的数组缓存的空间)
     */
    public void trimToSize() {
        modCount++;
        // 比较数组缓存的大小和实际存储元素的数量
        if (size < elementData.length) {
            // 如果当前list不为空的话, 那么就将当前的元素复制到一个实际大小的数组当中
            elementData = (size == 0)
              ? EMPTY_ELEMENTDATA
              : Arrays.copyOf(elementData, size);
        }
    }

    /**
     * Increases the capacity of this {@code ArrayList} instance, if
     * necessary, to ensure that it can hold at least the number of elements
     * specified by the minimum capacity argument.
     *
     * 增加ArrayList实例的容量, 在必要的情况下, 确保(增加后的容量)至少能装下指定的
     * 最小容量的元素
     *
     * (上面说的"必要的情况"其实就是指定的容量比实际元素的数量小的情况, 这样的情况
     * 下, 这个方法将直接返回, 不进行任何操作. 而且在实际元素数量没有达到默认初始化
     * 容量的情况下, 将直接扩容到默认初始化容量)
     *
     * @param minCapacity the desired minimum capacity 指定的最小容量
     */
    public void ensureCapacity(int minCapacity) {
        if (minCapacity > elementData.length
            && !(elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
                 && minCapacity <= DEFAULT_CAPACITY)) {
            modCount++;
            grow(minCapacity);
        }
    }

    /**
     * The maximum size of array to allocate (unless necessary).
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     *
     * 数组缓存的最大可申请的数组大小(除非在必要的情况下). 有的Java虚拟机
     * 会在数组的开头存放一些数据. 尝试分配更大数组会导致OutOfMemoryError:
     * 请求的数组大小超过虚拟机的限制.
     *
     * (有的Java虚拟机会在数组的开头存一些数据, 为了避免list申请缓存数组
     * 的时候超过虚拟机的限制, 最大可申请大小在整型数的基础上减掉了8)
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * 增加容量, 确保该容量至少可以装下由最小容量参数指定的数量的元素
     *
     * @param minCapacity the desired minimum capacity 指定的最小容量
     * @throws OutOfMemoryError if minCapacity is less than zero 如果参数小于0
     */
    private Object[] grow(int minCapacity) {
        return elementData = Arrays.copyOf(elementData,
                                           newCapacity(minCapacity));
    }

    private Object[] grow() {
        return grow(size + 1);
    }

    /**
     * Returns a capacity at least as large as the given minimum capacity.
     * Returns the current capacity increased by 50% if that suffices.
     * Will not return a capacity greater than MAX_ARRAY_SIZE unless
     * the given minimum capacity is greater than MAX_ARRAY_SIZE.
     *
     * 至少返回和指定的(最小)容量一样大的容量值.
     * 如果容量已满的话, 返回当前容量增长50%后的容量值.
     * 除非给定的最小容量值比MAX_ARRAY_SIZE的大小更大, 不然不会返回MAX_ARRAY_SIZE
     *
     * @param minCapacity the desired minimum capacity 指定的最小容量
     * @throws OutOfMemoryError if minCapacity is less than zero 如果参数小于0
     */
    private int newCapacity(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        // 位运算, 右移一位相当于除以2, 这里是表示默认增长的容量是在旧容量的基础上增加50%
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        // 如果默认增长的容量比指定的容量小, 那么就直接使用指定的容量, 否则使用默认增长后的容量
        if (newCapacity - minCapacity <= 0) {
            if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
                // 如果list是空的, 就在指定的容量和默认容量里挑一个大的
                return Math.max(DEFAULT_CAPACITY, minCapacity);
            if (minCapacity < 0) // overflow
                // 指定的新容量不能小于0
                throw new OutOfMemoryError();
            return minCapacity;
        }
        // 如果默认增长的容量比最大容量小, 就直接使用
        return (newCapacity - MAX_ARRAY_SIZE <= 0)
            ? newCapacity
            : hugeCapacity(minCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE)
            ? Integer.MAX_VALUE
            : MAX_ARRAY_SIZE;
    }

    /**
     * Returns the number of elements in this list.
     *
     * 返回list中存储的元素的数量.
     *
     * @return the number of elements in this list 返回list中存储的元素的数量.
     */
    public int size() {
        return size;
    }

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * 如果list中没有存储元素, 就返回true.
     *
     * @return {@code true} if this list contains no elements 如果list中没有存储元素, 就返回true.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns {@code true} if this list contains the specified element.
     * More formally, returns {@code true} if and only if this list contains
     * at least one element {@code e} such that
     * {@code Objects.equals(o, e)}.
     *
     * 如果list中包含了指定的元素就返回true.
     * 更准确地说, 当且仅当list中至少包含一个元素e满足Objects.equals(o, e)的时候返回true.
     *
     * @param o element whose presence in this list is to be tested 需要被判断是否被list包含的元素
     * @return {@code true} if this list contains the specified element 如果list中包含了指定的元素就返回true.
     */
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index {@code i} such that
     * {@code Objects.equals(o, get(i))},
     * or -1 if there is no such index.
     *
     * 返回指定元素在list中第一次出现的索引, 如果list没有包含这个元素就返回-1.
     * 更准确地说, 返回最小的索引i满足Objects.equals(o, get(i)), 如果没有这样的索引就返回-1.
     */
    public int indexOf(Object o) {
        return indexOfRange(o, 0, size);
    }

    int indexOfRange(Object o, int start, int end) {
        Object[] es = elementData;
        // 因为ArrayList允许存在null元素, 所以即使是null也要比对一轮, 非null元素就使用equals()方法来判断
        if (o == null) {
            for (int i = start; i < end; i++) {
                if (es[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                if (o.equals(es[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index {@code i} such that
     * {@code Objects.equals(o, get(i))},
     * or -1 if there is no such index.
     *
     * 返回指定元素在list中最后一次出现的索引, 如果如果list没有包含这个元素就返回-1.
     * 更准确地说, 返回最大的索引i满足Objects.equals(o, get(i)), 如果没有这样的索引就返回-1.
     */
    public int lastIndexOf(Object o) {
        return lastIndexOfRange(o, 0, size);
    }

    int lastIndexOfRange(Object o, int start, int end) {
        Object[] es = elementData;
        // 同indexOfRange方法
        if (o == null) {
            for (int i = end - 1; i >= start; i--) {
                if (es[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = end - 1; i >= start; i--) {
                if (o.equals(es[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns a shallow copy of this {@code ArrayList} instance.  (The
     * elements themselves are not copied.)
     *
     * 返回一个当前ArrayList的深复制. (不复制list包含的元素本身)
     *
     * @return a clone of this {@code ArrayList} instance
     */
    public Object clone() {
        try {
            ArrayList<?> v = (ArrayList<?>) super.clone();
            v.elementData = Arrays.copyOf(elementData, size);
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            // 自从我们实现了Cloneable接口, 这个异常就不应该发生了
            throw new InternalError(e);
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
     * 在内存中申请一个新的数组). 你可以自由地修改返回的数组.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * 这个方法是扮演着数组和集合之间桥梁角色的API.
     *
     * @return an array containing all of the elements in this list in
     *         proper sequence 按照合适的序列返回一个包含list中存储的所有元
     *         素的数组.
     */
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element); the runtime type of the returned
     * array is that of the specified array.  If the list fits in the
     * specified array, it is returned therein.  Otherwise, a new array is
     * allocated with the runtime type of the specified array and the size of
     * this list.
     *
     * 按照正确的顺序(从第一个到最后一个)返回一个包含list中存储的所有元素的数组; 运行时
     * 返回的数组的类型就是(参数)指定的数组的类型. 如果(参数)指定的数组能放得下list(的全部元素), 
     * 那么就会直接返回这个数组(并将list的元素放入数组中).
     * 否则, 就会申请一个新的数组, 其类型与指定的数组类型相同, 大小则是list的大小.
     *
     * <p>If the list fits in the specified array with room to spare
     * (i.e., the array has more elements than the list), the element in
     * the array immediately following the end of the collection is set to
     * {@code null}.  (This is useful in determining the length of the
     * list <i>only</i> if the caller knows that the list does not contain
     * any null elements.)
     *
     * 如果(参数)指定的数组能放得下list, 并且数组的空间还有剩余(数组的元素比list的要多), 那么
     * 数组中在集合长度之后紧接着的一个数组元素将被设为null. (这仅在调用者确认list中没有包含null
     * 元素的情况下, 确定list长度的时候非常管用.)
     *
     * @param a the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     *
     *          如果数组足够大, 可以放下整个list的元素, 则用这个数组存储list的元素.
     *          否则, 就申请一个新的, 类型与a数组类型相同的数组, 来存储list的元素.
     *
     * @return an array containing the elements of the list
     *
     *          一个包含list所有元素的数组
     *
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this list
     *
     *          如果(参数)指定的数组的类型不是list里所有元素的父类型
     *
     * @throws NullPointerException if the specified array is null 如果(参数)指定的数组为null
     *
     * (这个方法是为了得到一个指定类型的, 存储所有list元素的数组, 参数是一个数组,
     * 如果传入的数组的大小比list大, 就直接用这个数组存list的元素, 并将list结尾
     * 的下一个数组元素设为null. 如果这个数组不如list大, 就直接new一个新的数组,
     * 用来放list的元素, 当然啦, 这个数组的大小就和list是一样的了.
     * 所以如果传入的数组比list要大的话, 那么方法返回的数组就是传入的数组, 大小就
     * 是原来的大小, 而不是list的大小.)
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        // 判断传入的数组的大小是不是比list要小
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        // 将list的后一个元素设为null
        if (a.length > size)
            a[size] = null;
        return a;
    }

    // Positional Access Operations 位置访问操作(通过下标获取数组元素的方法们)

    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }

    @SuppressWarnings("unchecked")
    static <E> E elementAt(Object[] es, int index) {
        return (E) es[index];
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * 返回list中指定位置(下标)的元素
     *
     * @param  index index of the element to return 需要返回的元素的位置(下标)
     * @return the element at the specified position in this list list中指定位置(下标)的元素
     * @throws IndexOutOfBoundsException {@inheritDoc} 越界, 指定的位置(下标)超过了数组的大小
     */
    public E get(int index) {
        // 检查指定的位置(下标)是否越界
        Objects.checkIndex(index, size);
        return elementData(index);
    }

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * 用指定的元素替换list中指定位置(下标)的元素
     *
     * @param index index of the element to replace 需要替换的位置(下标)
     * @param element element to be stored at the specified position 在指定的位置(下标)要存储的元素
     * @return the element previously at the specified position 指定位置(下标)替换之前的元素
     * @throws IndexOutOfBoundsException {@inheritDoc} 越界, 指定的位置(下标)超过了数组的大小
     */
    public E set(int index, E element) {
        Objects.checkIndex(index, size);
        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }

    /**
     * This helper method split out from add(E) to keep method
     * bytecode size under 35 (the -XX:MaxInlineSize default value),
     * which helps when add(E) is called in a C1-compiled loop.
     *
     * 这个辅助方法是从add(E)方法中分离出来的, 为的是确保(add(E))方法
     * 的字节码大小在35(-XX:MaxInlineSize参数的默认值)以下, 使得add(E)
     * 方法可以在一个编译循环中被调用.
     *
     * (在Java虚拟机中, 有一些代码片段是经常用到而且体积非常小的, 比如Java
     * Bean中的getter和setter代码, 为了提高方法执行的效率, 这些方法会被
     * Java虚拟机内联编译到代码当中. 在Java虚拟机默认的设置当中, 只有方法
     * 的代码长度在35个字节以下, 才会被虚拟机做内联处理, 于是为了保证add(E)
     * 方法可以被虚拟机做内联处理, 才将这个方法中的操作拆分出来.)
     *
     * (关于内联
     * 调用某个方法的过程在虚拟机中实际上是将汇编代码的执行顺序转移到某段内存
     * 当中, 等到执行完毕之后, 再切换回到原来的位置, 继续向下执行, 这种转移操
     * 作要求在转去前要保护现场并记忆执行的地址. 转回后先要恢复现场, 并按原来保
     * 存地址继续执行. 也就是通常说的压栈和出栈。因此, 函数调用要有一定的时间
     * 和空间方面的开销. 那么对于那些函数体代码不是很大，又频繁调用的函数来说,
     * 这个时间和空间的消耗会很大.
     * 内联处理实际上就是将存在别的地方的方法的代码, 直接替换到原来正常顺序中
     * 调用该方法的位置上, 这样就不存在跳转的问题了. 类似于C语言中的define.)
     *
     */
    private void add(E e, Object[] elementData, int s) {
        if (s == elementData.length)
            elementData = grow();
        elementData[s] = e;
        size = s + 1;
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
        modCount++;
        add(e, elementData, size);
        return true;
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * 插入指定的元素到list指定的位置上. 向右移动指定位置上和其后续(如果有)的所
     * 有元素
     *
     * @param index index at which the specified element is to be inserted 指定元素将要被插入的位置(索引)
     * @param element element to be inserted 将要被插入的元素
     * @throws IndexOutOfBoundsException {@inheritDoc} 数组越界
     */
    public void add(int index, E element) {
        // 边界检查, 数组越界的异常会在这里被抛出
        rangeCheckForAdd(index);
        modCount++;
        final int s;
        Object[] elementData;
        // 容量不够了就先增加容量
        if ((s = size) == (elementData = this.elementData).length)
            elementData = grow();
        // 将指定下标后的元素复制到下标+1的位置上
        System.arraycopy(elementData, index,
                         elementData, index + 1,
                         s - index);
        elementData[index] = element;
        size = s + 1;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * 移除list中指定位置的元素. 向左移动该位置之后的所有元素.
     *
     * @param index the index of the element to be removed 将要被移出的元素的位置(下标)
     * @return the element that was removed from the list 被移出list的元素
     * @throws IndexOutOfBoundsException {@inheritDoc} 数组越界
     */
    public E remove(int index) {
        Objects.checkIndex(index, size);
        final Object[] es = elementData;

        @SuppressWarnings("unchecked") E oldValue = (E) es[index];
        fastRemove(es, index);

        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof List)) {
            return false;
        }

        final int expectedModCount = modCount;
        // ArrayList can be subclassed and given arbitrary behavior, but we can
        // still deal with the common case where o is ArrayList precisely
        // ArrayList可以被继承并随意增加行为, 但通常情况下, 如果o是ArrayList的话我们
        // 仍然可以处理通用的部分.
        boolean equal = (o.getClass() == ArrayList.class)
            ? equalsArrayList((ArrayList<?>) o)
            : equalsRange((List<?>) o, 0, size);

        checkForComodification(expectedModCount);
        return equal;
    }

    /**
     * 该方法比较的是ArrayList和其他List的子类是否相同, 通过获取其他List的迭代器来进行比较
     */
    boolean equalsRange(List<?> other, int from, int to) {
        final Object[] es = elementData;
        // 判断list是否在并发环境下被更改了
        if (to > es.length) {
            throw new ConcurrentModificationException();
        }
        var oit = other.iterator();
        // 将两个数组逐个比较
        for (; from < to; from++) {
            if (!oit.hasNext() || !Objects.equals(es[from], oit.next())) {
                return false;
            }
        }
        return !oit.hasNext();
    }

    /**
     * 该方法比较的是ArrayList和ArrayList
     */
    private boolean equalsArrayList(ArrayList<?> other) {
        final int otherModCount = other.modCount;
        final int s = size;
        boolean equal;
        // 先比较大小
        if (equal = (s == other.size)) {
            final Object[] otherEs = other.elementData;
            final Object[] es = elementData;
            if (s > es.length || s > otherEs.length) {
                throw new ConcurrentModificationException();
            }
            // 再逐个元素进行比较
            for (int i = 0; i < s; i++) {
                if (!Objects.equals(es[i], otherEs[i])) {
                    equal = false;
                    break;
                }
            }
        }
        other.checkForComodification(otherModCount);
        return equal;
    }

    private void checkForComodification(final int expectedModCount) {
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int expectedModCount = modCount;
        int hash = hashCodeRange(0, size);
        checkForComodification(expectedModCount);
        return hash;
    }

    int hashCodeRange(int from, int to) {
        final Object[] es = elementData;
        if (to > es.length) {
            throw new ConcurrentModificationException();
        }
        int hashCode = 1;
        for (int i = from; i < to; i++) {
            Object e = es[i];
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        }
        return hashCode;
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If the list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * {@code i} such that
     * {@code Objects.equals(o, get(i))}
     * (if such an element exists).  Returns {@code true} if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * 移除list中第一次出现(与指定元素相同)的元素, 如果其存在的话. 如果list中没有包含
     * 该元素的话, 那么list不会被修改. 准确地来说, 是移除最小索引的元素.
     *
     * @param o element to be removed from this list, if present 从list中被移除的元素, 如果存在的话
     * @return {@code true} if this list contained the specified element 如果list包含指定的元素, 返回true
     */
    public boolean remove(Object o) {
        final Object[] es = elementData;
        final int size = this.size;
        int i = 0;
        // 这个用法其实挺少见人用了, 是指定跳出内部循环的标记
        found: {
            // 分为指定元素为null和不为null的两种流程, 因为ArrayList允许元素为null
            if (o == null) {
                for (; i < size; i++)
                    if (es[i] == null)
                        break found;
            } else {
                for (; i < size; i++)
                    if (o.equals(es[i]))
                        break found;
            }
            return false;
        }
        fastRemove(es, i);
        return true;
    }

    /**
     * Private remove method that skips bounds checking and does not
     * return the value removed.
     *
     * 私有的remove方法, 这个方法跳过了边界检查, 也不会返回被移除的元素.
     */
    private void fastRemove(Object[] es, int i) {
        modCount++;
        final int newSize;
        if ((newSize = size - 1) > i)
            System.arraycopy(es, i + 1, es, i, newSize - i);
        es[size = newSize] = null;
    }

    /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns.
     *
     * 移除list中的全部元素. 这个方法调用后会变成空的list.
     */
    public void clear() {
        modCount++;
        final Object[] es = elementData;
        // 并没有简单地将list置为空, 而是将每一个元素都设为null
        // 这里主要是考虑到list将会被复用, 而不释放已经申请到的数组空间
        for (int to = size, i = size = 0; i < to; i++)
            es[i] = null;
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the
     * specified collection's Iterator.  The behavior of this operation is
     * undefined if the specified collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified collection is this list, and this
     * list is nonempty.)
     *
     * 追加指定Collection集合的所有元素到list的末尾, 他们的顺序是按照指定的Collection
     * 集合的迭代器返回顺序来定的. 这个方法没有定义的一个操作是在方法执行的过程中, 如果
     * 指定的Collection集合被修改了. (这意味着, 如果指定的Collection集合就是该list
     * 并且list非空, 那么这种调用的行为是没有定义的.)
     *
     * (翻译起来比较拗口, 实际上上面说的情况是指, 这个方法实际上是有隐患的, 如果你想要
     * 传进addAll方法的Collection在addAll方法没有执行完的时候就被修改了, 那么该方法
     * 将出现不可预料的后果, 最简单的例子就是使用addAll方法add自己, 但我自己简单尝试了
     * 一下, 好像结果是正确的, 并没有发生异常)
     *
     * @param c collection containing elements to be added to this list 将要被添加进list中的元素的Collection集合
     * @return {@code true} if this list changed as a result of the call 如果list被该方法的调用改变了, 就返回true
     * @throws NullPointerException if the specified collection is null 如果指定的Collection集合是null
     */
    public boolean addAll(Collection<? extends E> c) {
        Object[] a = c.toArray();
        modCount++;
        int numNew = a.length;
        // 如果传进来的Collection为空, 那么list将不会被修改
        if (numNew == 0)
            return false;
        Object[] elementData;
        final int s;
        // 如果剩余的数组空间不足以放下传入的Collection的全部元素, 就增长numNew个数组空间
        // 值得关注的是, 这里的增长并不是增长到刚好可以当下Collection的全部元素, 而是直接
        // 增长Collection的长度个元素的位置, 大概是为之后可能出现的add预留一点位置吧
        if (numNew > (elementData = this.elementData).length - (s = size))
            elementData = grow(s + numNew);
        System.arraycopy(a, 0, elementData, s, numNew);
        size = s + numNew;
        return true;
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * 在指定的位置插入指定Collection集合的全部元素到当前list中. 向右移动当前位置的
     * 的元素以及当前位置之后的元素(如果存在)(增加其下标). 新的元素会按照指定的Collection
     * 迭代器返回的顺序出现在list当中.
     *
     * @param index index at which to insert the first element from the
     *              specified collection 将要插入的指定的Collection集合的第一个元素所在的位置(下标)
     * @param c collection containing elements to be added to this list 包含将要插入到当前list中所有元素的集合
     * @return {@code true} if this list changed as a result of the call 如果list被改变了, 将会返回true作为调用的结果
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null 如果指定的集合为null
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckForAdd(index);

        Object[] a = c.toArray();
        modCount++;
        int numNew = a.length;
        if (numNew == 0)
            return false;
        Object[] elementData;
        final int s;
        // 判断是否需要增加数组缓存的容量
        if (numNew > (elementData = this.elementData).length - (s = size))
            elementData = grow(s + numNew);

        // 计算需要移动的距离, 一个简单的逻辑, 如果index等于size的话, 那么就不需要移动, 直接将
        // 新的集合加在list的后面即可, 如果index比size小的话, 则按照等于的情况往前推算即可
        int numMoved = s - index;
        // 所谓的将元素右移, 实际上是将要移动的所有元素复制到下标+1的位置上, 而不是通过循环一个个移动
        if (numMoved > 0)
            System.arraycopy(elementData, index,
                             elementData, index + numNew,
                             numMoved);
        System.arraycopy(a, 0, elementData, index, numNew);
        size = s + numNew;
        return true;
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
     * @throws IndexOutOfBoundsException if {@code fromIndex} or
     *         {@code toIndex} is out of range
     *         ({@code fromIndex < 0 ||
     *          toIndex > size() ||
     *          toIndex < fromIndex})
     * 数组越界的异常, 有几种情况, 如果fromIndex或者toIndex超出数组范围, 或者
     * fromIndex小于0, 或者toIndex大于size(数组的大小), 或者toIndex小于fromIndex
     */
    protected void removeRange(int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IndexOutOfBoundsException(
                    outOfBoundsMsg(fromIndex, toIndex));
        }
        modCount++;
        shiftTailOverGap(elementData, fromIndex, toIndex);
    }

    /** 
    * Erases the gap from lo to hi, by sliding down following elements. 
    * 
    * 通过向前滑动的方式来擦除(下标为)lo(low)到hi(high)之间的空缺
    */
    private void shiftTailOverGap(Object[] es, int lo, int hi) {
        System.arraycopy(es, hi, es, lo, size - hi);
        for (int to = size, i = (size -= hi - lo); i < to; i++)
            es[i] = null;
    }

    /**
     * A version of rangeCheck used by add and addAll.
     *
     * 一个被用于add和addAll的边界检查的版本
     */
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     *
     * 构建一个索引越界异常的详细信息. 在错误代码的许多可能的重构中, 这个
     * 描述在服务器和客户端的虚拟机中有更好的表现.
     */
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    /**
     * A version used in checking (fromIndex > toIndex) condition
     *
     * 一个用于检查条件fromIndex大于toIndex的(异常信息重构的)版本
     */
    private static String outOfBoundsMsg(int fromIndex, int toIndex) {
        return "From Index: " + fromIndex + " > To Index: " + toIndex;
    }

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection.
     *
     * 移除list中所有被包含在指定集合中的元素.
     *
     * @param c collection containing elements to be removed from this list 包含着将被从list中移除的元素的集合
     * @return {@code true} if this list changed as a result of the call 如果这个方法的调用改变了list, 就返回true
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection 如果list中有一个元素的类型与指定集合中的类型不相容
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements 
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null 如果list中包含一个null元素并且指定集合中不允许存在null元素, 或者指定的集合为null
     * @see Collection#contains(Object)
     */
    public boolean removeAll(Collection<?> c) {
        return batchRemove(c, false, 0, size);
    }

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection.  In other words, removes from this list all
     * of its elements that are not contained in the specified collection.
     *
     * 仅保留list中被指定集合包含的元素. 换句话说, 移除list中所有没有被包含在
     * 指定集合中的元素.
     *
     * @param c collection containing elements to be retained in this list 包含list中将要被保留下来的元素的集合
     * @return {@code true} if this list changed as a result of the call 如果这个方法的调用改变了list, 就返回true
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection 如果list中有一个元素的类型与指定集合中的类型不相容
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null 如果list中包含一个null元素并且指定集合中不允许存在null元素, 或者指定的集合为null
     * @see Collection#contains(Object)
     */
    public boolean retainAll(Collection<?> c) {
        return batchRemove(c, true, 0, size);
    }

    /**
     * 批量移除元素的方法
     */
    boolean batchRemove(Collection<?> c, boolean complement,
                        final int from, final int end) {
        // 检查集合当中有没有值为null的元素, 如果有就抛出空指针异常
        Objects.requireNonNull(c);
        final Object[] es = elementData;
        int r;
        // Optimize for initial run of survivors 关于保留元素的运行初始化的优化
        // 这里是为了找到list中第一个存在于目标集合中的元素, 这个位置代表运行开始的标志, 如果是retain, 那么从这个位置开始移除元素
        // 如果是remove, 那么就从这个位置开始保留元素, 需要仔细体会`!= complement`和`== complement`这两个比较的作用
        // 实际上这个方法总是遍历了from和end之间的元素, 通过complement变量来标记当前遍历到的元素是否保留
        for (r = from;; r++) {
            if (r == end)
                return false;
            if (c.contains(es[r]) != complement)
                break;
        }
        int w = r++;
        try {
            for (Object e; r < end; r++)
                if (c.contains(e = es[r]) == complement)
                    es[w++] = e;
        } catch (Throwable ex) {
            // Preserve behavioral compatibility with AbstractCollection,
            // even if c.contains() throws.
            // 保持与AbstractCollection行为的兼容性, 即使是c.contains()抛出了异常
            System.arraycopy(es, r, es, w, end - r);
            w += end - r;
            throw ex;
        } finally {
            modCount += end - w;
            shiftTailOverGap(es, w, end);
        }
        return true;
    }

    /**
     * Saves the state of the {@code ArrayList} instance to a stream
     * (that is, serializes it).
     *
     * 保存ArrayList实例的状态到一个(输出)流当中(意思就是, 进行序列化操作)
     *
     * @param s the stream (输出)流
     * @throws java.io.IOException if an I/O error occurs 如果发生了I/O错误
     * @serialData The length of the array backing the {@code ArrayList}
     *             instance is emitted (int), followed by all of its elements
     *             (each an {@code Object}) in the proper order.
     *             先(向流中)传入ArrayList实例的数组长度(整型), 然后紧跟着按照恰
     *             当的顺序传入list数组中的元素
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        // Write out element count, and any hidden stuff
        int expectedModCount = modCount;
        s.defaultWriteObject();

        // Write out size as capacity for behavioral compatibility with clone()
        // 将size值作为容量写进流里, 保证与clone()方法行为的兼容性
        // clone()方法就是使用实际存储的元素的数量来作为容量的
        s.writeInt(size);

        // Write out all elements in the proper order. 按照正确的顺序写出全部的元素
        for (int i=0; i<size; i++) {
            s.writeObject(elementData[i]);
        }

        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Reconstitutes the {@code ArrayList} instance from a stream (that is,
     * deserializes it).
     *
     * 从一个流中重新构建一个ArrayList实例(意思就是, 进行反序列化操作)
     *
     * @param s the stream 流
     * @throws ClassNotFoundException if the class of a serialized object
     *         could not be found 如果一个反序列化对象的类型无法被找到
     * @throws java.io.IOException if an I/O error occurs 如果发生I/O错误
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {

        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // Read in capacity 读进容量信息
        s.readInt(); // ignored 忽略(这个容量值对构建ArrayList的操作没有帮助, 在这里直接读取但不处理, 可以理解为跳过)

        if (size > 0) {
            // like clone(), allocate array based upon size not capacity
            // 像clone()一样, 基于size值而不是capacity值来申请内存
            SharedSecrets.getJavaObjectInputStreamAccess().checkArray(s, Object[].class, size);
            Object[] elements = new Object[size];

            // Read in all elements in the proper order.
            // 按正确的顺序读入全部元素
            for (int i = 0; i < size; i++) {
                elements[i] = s.readObject();
            }

            elementData = elements;
        } else if (size == 0) {
            elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new java.io.InvalidObjectException("Invalid size: " + size);
        }
    }

    /**
     * 以下分关于迭代器的方法里, 注意区分ListIterator和普通Iterator, 其中ListIterator
     * 是List子类特有的迭代器, Iterator是通用迭代器
     */

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * The specified index indicates the first element that would be
     * returned by an initial call to {@link ListIterator#next next}.
     * An initial call to {@link ListIterator#previous previous} would
     * return the element with the specified index minus one.
     *
     * 从list中指定的位置(下标)返回元素的List迭代器(按正确的顺序). 指定的下标指明了
     * 通过ListIterator的next()方法第一次调用的时候返回的第一个元素.
     * 第一次调用ListIterator的previous()方法将返回指定下标减一对应的元素.
     *
     * <p>The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public ListIterator<E> listIterator(int index) {
        rangeCheckForAdd(index);
        return new ListItr(index);
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence).
     *
     * 返回一个list中所有元素的List迭代器(按正确的顺序)
     *
     * <p>The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     *
     * @see #listIterator(int)
     */
    public ListIterator<E> listIterator() {
        return new ListItr(0);
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * 按正确的顺序返回一个list所有元素的迭代器
     *
     * <p>The returned iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     *
     * @return an iterator over the elements in this list in proper sequence 按正确的顺序返回一个list所有元素的迭代器
     */
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * An optimized version of AbstractList.Itr 一个AbstractList.Itr的优化版本
     */
    private class Itr implements Iterator<E> {
        int cursor;       // index of next element to return 返回下一个元素的下标
        int lastRet = -1; // index of last element returned; -1 if no such 返回上一个元素的下标, 如果没有就返回-1
        int expectedModCount = modCount;

        // prevent creating a synthetic constructor 防止创建一个合成的构造方法(意思是防止子类覆盖父类的构造方法)
        Itr() {}

        public boolean hasNext() {
            // 如果cursor已经到了list的最后一个元素的下标, 就表示已经没有下一个元素了
            return cursor != size;
        }

        @SuppressWarnings("unchecked")
        public E next() {
            checkForComodification();
            int i = cursor;
            // 先判断下标是否越界
            if (i >= size)
                throw new NoSuchElementException();
            Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i + 1;
            return (E) elementData[lastRet = i];
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                ArrayList.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        /**
         * 支持lambda表达式的遍历剩下所有元素的方法
         */
        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            final int size = ArrayList.this.size;
            int i = cursor;
            if (i < size) {
                final Object[] es = elementData;
                if (i >= es.length)
                    throw new ConcurrentModificationException();
                for (; i < size && modCount == expectedModCount; i++)
                    action.accept(elementAt(es, i));
                // update once at end to reduce heap write traffic
                // 结尾的时候更新一次, 减少堆的写入
                cursor = i;
                lastRet = i - 1;
                checkForComodification();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    /**
     * An optimized version of AbstractList.ListItr 一个AbstractList.ListItr的优化版本
     *
     * (这里没有什么特别的操作, 就是在每一个操作的时候, 注意检查当前操作的位置是否是空或者越界)
     */
    private class ListItr extends Itr implements ListIterator<E> {
        ListItr(int index) {
            super();
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        @SuppressWarnings("unchecked")
        public E previous() {
            checkForComodification();
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i;
            return (E) elementData[lastRet = i];
        }

        public void set(E e) {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                ArrayList.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(E e) {
            checkForComodification();

            try {
                int i = cursor;
                ArrayList.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * Returns a view of the portion of this list between the specified
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.  (If
     * {@code fromIndex} and {@code toIndex} are equal, the returned list is
     * empty.)  The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations.
     *
     * 返回指定的fromIndex(包含fromIndex)和toIndex(不包含toIndex)之间的list的一部分
     * 视图. (如果fromIndex和toIndex相等, 返回的list是空的). 返回的list是基于当前list
     * 的, 所以在当前list发生的非结构化修改都会反映到返回的list中, 反过来也是一样.
     * 返回的list支持全部可选list的操作.
     *
     * (这里说的subList会影响到原list的元素, 并不是subList持有了原有list中元素的引用,
     * 然后直接在原list元素的引用上进行操作, 而是在subList内部提供的方法里使用ArrayList
     * 的方法对操作进行了转换, 详见下面subList里的各个操作)
     *
     * <p>This method eliminates the need for explicit range operations (of
     * the sort that commonly exist for arrays).  Any operation that expects
     * a list can be used as a range operation by passing a subList view
     * instead of a whole list.  For example, the following idiom
     * removes a range of elements from a list:
     * <pre>
     *      list.subList(from, to).clear();
     * </pre>
     * Similar idioms may be constructed for {@link #indexOf(Object)} and
     * {@link #lastIndexOf(Object)}, and all of the algorithms in the
     * {@link Collections} class can be applied to a subList.
     *
     * 这个方法消除了对显式范围操作的需求(通常数组有排序的). 任何希望对list的某个范围
     * 进行的操作都可以通过传递一个子list视图来代替整个list进行操作. 举个例子, 下面的
     * 语句表示从list中移除某个范围内的所有元素:
     * list.subList(from, to).clear();
     * 类似的语句出现在indexOf(Object)和lastIndexOf(Object)中, 并且所有在Collections类
     * 中的算法都可以接受一个子list视图
     *
     * <p>The semantics of the list returned by this method become undefined if
     * the backing list (i.e., this list) is <i>structurally modified</i> in
     * any way other than via the returned list.  (Structural modifications are
     * those that change the size of this list, or otherwise perturb it in such
     * a fashion that iterations in progress may yield incorrect results.)
     *
     * 如果当前list通过除了返回子list外的任何方式进行了结构化的修改, 那么通过这个方法
     * 返回的子list的语义都会变成未定义. (结构化的修改指的是那些会改变list大小, 或是那些
     * 会干扰当前正在进行的迭代使其可能产生不正确的结果的操作)
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public List<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size);
        return new SubList<>(this, fromIndex, toIndex);
    }

    /**
     * SubList提供了和ArrayList几乎相同的主要操作, 主要是增删改查, SubList持有了原list的引用,
     * 将每一个在SubList上进行的操作, 转换为在原list上的操作, 达到对SubList的操作映射到原list
     * 上的效果.
     */
    private static class SubList<E> extends AbstractList<E> implements RandomAccess {
        private final ArrayList<E> root;
        private final SubList<E> parent;
        private final int offset;
        private int size;

        /**
         * Constructs a sublist of an arbitrary ArrayList. 从任意一个ArrayList中创建子list的构造方法
         */
        public SubList(ArrayList<E> root, int fromIndex, int toIndex) {
            this.root = root;
            this.parent = null;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = root.modCount;
        }

        /**
         * Constructs a sublist of another SubList. 从另一个子list中创建一个子list的构造方法
         */
        private SubList(SubList<E> parent, int fromIndex, int toIndex) {
            this.root = parent.root;
            this.parent = parent;
            this.offset = parent.offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = root.modCount;
        }

        public E set(int index, E element) {
            Objects.checkIndex(index, size);
            checkForComodification();
            E oldValue = root.elementData(offset + index);
            root.elementData[offset + index] = element;
            return oldValue;
        }

        public E get(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            return root.elementData(offset + index);
        }

        public int size() {
            checkForComodification();
            return size;
        }

        public void add(int index, E element) {
            rangeCheckForAdd(index);
            checkForComodification();
            root.add(offset + index, element);
            updateSizeAndModCount(1);
        }

        public E remove(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            E result = root.remove(offset + index);
            updateSizeAndModCount(-1);
            return result;
        }

        protected void removeRange(int fromIndex, int toIndex) {
            checkForComodification();
            root.removeRange(offset + fromIndex, offset + toIndex);
            updateSizeAndModCount(fromIndex - toIndex);
        }

        public boolean addAll(Collection<? extends E> c) {
            return addAll(this.size, c);
        }

        public boolean addAll(int index, Collection<? extends E> c) {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if (cSize==0)
                return false;
            checkForComodification();
            root.addAll(offset + index, c);
            updateSizeAndModCount(cSize);
            return true;
        }

        public void replaceAll(UnaryOperator<E> operator) {
            root.replaceAllRange(operator, offset, offset + size);
        }

        public boolean removeAll(Collection<?> c) {
            return batchRemove(c, false);
        }

        public boolean retainAll(Collection<?> c) {
            return batchRemove(c, true);
        }

        private boolean batchRemove(Collection<?> c, boolean complement) {
            checkForComodification();
            int oldSize = root.size;
            boolean modified =
                root.batchRemove(c, complement, offset, offset + size);
            if (modified)
                updateSizeAndModCount(root.size - oldSize);
            return modified;
        }

        public boolean removeIf(Predicate<? super E> filter) {
            checkForComodification();
            int oldSize = root.size;
            boolean modified = root.removeIf(filter, offset, offset + size);
            if (modified)
                updateSizeAndModCount(root.size - oldSize);
            return modified;
        }

        public Object[] toArray() {
            checkForComodification();
            return Arrays.copyOfRange(root.elementData, offset, offset + size);
        }

        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            checkForComodification();
            if (a.length < size)
                return (T[]) Arrays.copyOfRange(
                        root.elementData, offset, offset + size, a.getClass());
            System.arraycopy(root.elementData, offset, a, 0, size);
            if (a.length > size)
                a[size] = null;
            return a;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof List)) {
                return false;
            }

            boolean equal = root.equalsRange((List<?>)o, offset, offset + size);
            checkForComodification();
            return equal;
        }

        public int hashCode() {
            int hash = root.hashCodeRange(offset, offset + size);
            checkForComodification();
            return hash;
        }

        public int indexOf(Object o) {
            int index = root.indexOfRange(o, offset, offset + size);
            checkForComodification();
            return index >= 0 ? index - offset : -1;
        }

        public int lastIndexOf(Object o) {
            int index = root.lastIndexOfRange(o, offset, offset + size);
            checkForComodification();
            return index >= 0 ? index - offset : -1;
        }

        public boolean contains(Object o) {
            return indexOf(o) >= 0;
        }

        public Iterator<E> iterator() {
            return listIterator();
        }

        public ListIterator<E> listIterator(int index) {
            checkForComodification();
            rangeCheckForAdd(index);

            return new ListIterator<E>() {
                int cursor = index;
                int lastRet = -1;
                int expectedModCount = root.modCount;

                public boolean hasNext() {
                    return cursor != SubList.this.size;
                }

                @SuppressWarnings("unchecked")
                public E next() {
                    checkForComodification();
                    int i = cursor;
                    if (i >= SubList.this.size)
                        throw new NoSuchElementException();
                    Object[] elementData = root.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i + 1;
                    return (E) elementData[offset + (lastRet = i)];
                }

                public boolean hasPrevious() {
                    return cursor != 0;
                }

                @SuppressWarnings("unchecked")
                public E previous() {
                    checkForComodification();
                    int i = cursor - 1;
                    if (i < 0)
                        throw new NoSuchElementException();
                    Object[] elementData = root.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i;
                    return (E) elementData[offset + (lastRet = i)];
                }

                public void forEachRemaining(Consumer<? super E> action) {
                    Objects.requireNonNull(action);
                    final int size = SubList.this.size;
                    int i = cursor;
                    if (i < size) {
                        final Object[] es = root.elementData;
                        if (offset + i >= es.length)
                            throw new ConcurrentModificationException();
                        for (; i < size && modCount == expectedModCount; i++)
                            action.accept(elementAt(es, offset + i));
                        // update once at end to reduce heap write traffic
                        cursor = i;
                        lastRet = i - 1;
                        checkForComodification();
                    }
                }

                public int nextIndex() {
                    return cursor;
                }

                public int previousIndex() {
                    return cursor - 1;
                }

                public void remove() {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try {
                        SubList.this.remove(lastRet);
                        cursor = lastRet;
                        lastRet = -1;
                        expectedModCount = root.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                public void set(E e) {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try {
                        root.set(offset + lastRet, e);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                public void add(E e) {
                    checkForComodification();

                    try {
                        int i = cursor;
                        SubList.this.add(i, e);
                        cursor = i + 1;
                        lastRet = -1;
                        expectedModCount = root.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                final void checkForComodification() {
                    if (root.modCount != expectedModCount)
                        throw new ConcurrentModificationException();
                }
            };
        }

        public List<E> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new SubList<>(this, fromIndex, toIndex);
        }

        /**
         * 提供给add方法使用的下标范围检查, 如果传入的index值小于0或大于
         * 内置数据的大小, 就会抛出下标越界的异常
         */
        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private String outOfBoundsMsg(int index) {
            return "Index: "+index+", Size: "+this.size;
        }

        /**
         * 并发环境下subList被修改就会抛出异常
         */
        private void checkForComodification() {
            if (root.modCount != modCount)
                throw new ConcurrentModificationException();
        }

        private void updateSizeAndModCount(int sizeChange) {
            SubList<E> slist = this;
            do {
                slist.size += sizeChange;
                slist.modCount = root.modCount;
                slist = slist.parent;
            } while (slist != null);
        }

        public Spliterator<E> spliterator() {
            checkForComodification();

            // ArrayListSpliterator not used here due to late-binding
            // SubList里没有使用ArrayList的Spliterator因为它是延迟绑定的
            return new Spliterator<E>() {
                private int index = offset; // current index, modified on advance/split
                private int fence = -1; // -1 until used; then one past last index
                private int expectedModCount; // initialized when fence set

                private int getFence() { // initialize fence to size on first use
                    int hi; // (a specialized variant appears in method forEach)
                    if ((hi = fence) < 0) {
                        expectedModCount = modCount;
                        hi = fence = offset + size;
                    }
                    return hi;
                }

                public ArrayList<E>.ArrayListSpliterator trySplit() {
                    int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
                    // ArrayListSpliterator can be used here as the source is already bound
                    return (lo >= mid) ? null : // divide range in half unless too small
                        root.new ArrayListSpliterator(lo, index = mid, expectedModCount);
                }

                public boolean tryAdvance(Consumer<? super E> action) {
                    Objects.requireNonNull(action);
                    int hi = getFence(), i = index;
                    if (i < hi) {
                        index = i + 1;
                        @SuppressWarnings("unchecked") E e = (E)root.elementData[i];
                        action.accept(e);
                        if (root.modCount != expectedModCount)
                            throw new ConcurrentModificationException();
                        return true;
                    }
                    return false;
                }

                public void forEachRemaining(Consumer<? super E> action) {
                    Objects.requireNonNull(action);
                    int i, hi, mc; // hoist accesses and checks from loop
                    ArrayList<E> lst = root;
                    Object[] a;
                    if ((a = lst.elementData) != null) {
                        if ((hi = fence) < 0) {
                            mc = modCount;
                            hi = offset + size;
                        }
                        else
                            mc = expectedModCount;
                        if ((i = index) >= 0 && (index = hi) <= a.length) {
                            for (; i < hi; ++i) {
                                @SuppressWarnings("unchecked") E e = (E) a[i];
                                action.accept(e);
                            }
                            if (lst.modCount == mc)
                                return;
                        }
                    }
                    throw new ConcurrentModificationException();
                }

                public long estimateSize() {
                    return getFence() - index;
                }

                public int characteristics() {
                    return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
                }
            };
        }
    }

    /**
     * 支持lambda表达式的遍历方法
     * 
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;
        final Object[] es = elementData;
        final int size = this.size;
        for (int i = 0; modCount == expectedModCount && i < size; i++)
            action.accept(elementAt(es, i));
        if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
    }

    /**
     * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
     * and <em>fail-fast</em> {@link Spliterator} over the elements in this
     * list.
     *
     * 在当前list上创建一个全部元素的延迟绑定和快速失败的Spliterator
     *
     * <p>The {@code Spliterator} reports {@link Spliterator#SIZED},
     * {@link Spliterator#SUBSIZED}, and {@link Spliterator#ORDERED}.
     * Overriding implementations should document the reporting of additional
     * characteristic values.
     *
     * @return a {@code Spliterator} over the elements in this list
     * @since 1.8
     */
    @Override
    public Spliterator<E> spliterator() {
        return new ArrayListSpliterator(0, -1, 0);
    }

    /** Index-based split-by-two, lazily initialized Spliterator */
    final class ArrayListSpliterator implements Spliterator<E> {

        /*
         * If ArrayLists were immutable, or structurally immutable (no
         * adds, removes, etc), we could implement their spliterators
         * with Arrays.spliterator. Instead we detect as much
         * interference during traversal as practical without
         * sacrificing much performance. We rely primarily on
         * modCounts. These are not guaranteed to detect concurrency
         * violations, and are sometimes overly conservative about
         * within-thread interference, but detect enough problems to
         * be worthwhile in practice. To carry this out, we (1) lazily
         * initialize fence and expectedModCount until the latest
         * point that we need to commit to the state we are checking
         * against; thus improving precision.  (This doesn't apply to
         * SubLists, that create spliterators with current non-lazy
         * values).  (2) We perform only a single
         * ConcurrentModificationException check at the end of forEach
         * (the most performance-sensitive method). When using forEach
         * (as opposed to iterators), we can normally only detect
         * interference after actions, not before. Further
         * CME-triggering checks apply to all other possible
         * violations of assumptions for example null or too-small
         * elementData array given its size(), that could only have
         * occurred due to interference.  This allows the inner loop
         * of forEach to run without any further checks, and
         * simplifies lambda-resolution. While this does entail a
         * number of checks, note that in the common case of
         * list.stream().forEach(a), no checks or other computation
         * occur anywhere other than inside forEach itself.  The other
         * less-often-used methods cannot take advantage of most of
         * these streamlinings.
         *
         * 如果ArrayList是不可变的, 或是在结构上是不可变的(没有add, remove等的操作),
         * 我们可以通过Arrays.spliterator实现该list的Spliterator. 否则, 我们会在遍历
         * 期间尽可能多的检测干扰, 并且减少性能的损耗. 我们依靠modCounts变量来实现这一
         * 点. 这样的做法并不能保证检测到并发冲突, 而且有时候对待线程内的干扰过于保守,
         * 但这在实际环境中足以发现足够多的问题, 是值得的. 为了做到这一点, 我们
         * (1) 延迟初始化fence和expectedModCount, 直到我们需要提交最新的检查状态, 从而
         * 提高精度. (这不适合创建非延迟加载值的Spliterator的SubList)
         * (2) 我们只在forEach方法(对性能最敏感的方法)的结尾执行一次ConcurrentModificationException
         * 检查, 当使用forEach方法(而不是迭代器)的时候, 我们通常仅在操作之后检查干扰, 
         * 而不是在操作之前.
         * 进一步的CME触发检查适用于所有其他可能的冲突, 例如null或者比size值小得多的
         * elementData数组, 这些情况仅可能在受到干扰的情况下发生. 这就允许forEach的内部循环
         * 无需进一步的检查就可以运行, 并且简化了lambda表达式的复杂度. 尽管这需要大量的检查,
         * 但请注意, 在通常的情况下, 如list.stream().forEach(a), 除了forEach本身以外, 任何
         * 地方都不会发生检查或其他的计算. 其他不常用的方法无法使用大多数这一类的流操作.
         */

        private int index; // current index, modified on advance/split 当切割或advance是的当前索引(下标)
        private int fence; // -1 until used; then one past last index 在被使用之前值为-1, 被使用之后指向最后一个索引(下标)
        private int expectedModCount; // initialized when fence set 在fence被使用的时候初始化

        /** 
         * Creates new spliterator covering the given range. 创建新的覆盖指定范围的spliterator
         */
        ArrayListSpliterator(int origin, int fence, int expectedModCount) {
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }

        private int getFence() { // initialize fence to size on first use 在第一次被调用的时候初始化fence
            int hi; // (a specialized variant appears in method forEach)
            if ((hi = fence) < 0) {
                expectedModCount = modCount;
                hi = fence = size;
            }
            return hi;
        }

        public ArrayListSpliterator trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid) ? null : // divide range in half unless too small
                new ArrayListSpliterator(lo, index = mid, expectedModCount);
        }

        public boolean tryAdvance(Consumer<? super E> action) {
            if (action == null)
                throw new NullPointerException();
            int hi = getFence(), i = index;
            if (i < hi) {
                index = i + 1;
                @SuppressWarnings("unchecked") E e = (E)elementData[i];
                action.accept(e);
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            int i, hi, mc; // hoist accesses and checks from loop
            Object[] a;
            if (action == null)
                throw new NullPointerException();
            if ((a = elementData) != null) {
                if ((hi = fence) < 0) {
                    mc = modCount;
                    hi = size;
                }
                else
                    mc = expectedModCount;
                if ((i = index) >= 0 && (index = hi) <= a.length) {
                    for (; i < hi; ++i) {
                        @SuppressWarnings("unchecked") E e = (E) a[i];
                        action.accept(e);
                    }
                    if (modCount == mc)
                        return;
                }
            }
            throw new ConcurrentModificationException();
        }

        public long estimateSize() {
            return getFence() - index;
        }

        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }

    // A tiny bit set implementation 一个小型bit set的实现

    private static long[] nBits(int n) {
        return new long[((n - 1) >> 6) + 1];
    }
    private static void setBit(long[] bits, int i) {
        bits[i >> 6] |= 1L << i;
    }
    private static boolean isClear(long[] bits, int i) {
        return (bits[i >> 6] & (1L << i)) == 0;
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return removeIf(filter, 0, size);
    }

    /**
     * Removes all elements satisfying the given predicate, from index
     * i (inclusive) to index end (exclusive).
     * 
     * 移除从索引(下标)为i(包含)到索引(下标)为end(不包含)之间所有满足lambda
     * 表达式的元素.
     */
    boolean removeIf(Predicate<? super E> filter, int i, final int end) {
        Objects.requireNonNull(filter);
        int expectedModCount = modCount;
        final Object[] es = elementData;
        // Optimize for initial run of survivors 对幸存者初始化运行的优化
        for (; i < end && !filter.test(elementAt(es, i)); i++)
            ;
        // Tolerate predicates that reentrantly access the collection for
        // read (but writers still get CME), so traverse once to find
        // elements to delete, a second pass to physically expunge.
        // 允许lambda表达式重新访问集合进行读取(但写入的时候仍然会收到ConcurrentModificationException
        // 异常), 因此遍历一次找到要删除的元素, 再遍历第二次进行物理删除
        if (i < end) {
            final int beg = i;
            final long[] deathRow = nBits(end - beg);
            deathRow[0] = 1L;   // set bit 0
            for (i = beg + 1; i < end; i++)
                if (filter.test(elementAt(es, i)))
                    setBit(deathRow, i - beg);
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            modCount++;
            int w = beg;
            for (i = beg; i < end; i++)
                if (isClear(deathRow, i - beg))
                    es[w++] = es[i];
            shiftTailOverGap(es, w, end);
            return true;
        } else {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            return false;
        }
    }

    /**
     * 将list中的全部元素使用operator表达式的结果替换一遍
     */
    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        replaceAllRange(operator, 0, size);
        modCount++;
    }

    private void replaceAllRange(UnaryOperator<E> operator, int i, int end) {
        Objects.requireNonNull(operator);
        final int expectedModCount = modCount;
        final Object[] es = elementData;
        for (; modCount == expectedModCount && i < end; i++)
            es[i] = operator.apply(elementAt(es, i));
        if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
    }

    /**
     * 排序
     */
    @Override
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super E> c) {
        final int expectedModCount = modCount;
        Arrays.sort((E[]) elementData, 0, size, c);
        if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
        modCount++;
    }

    void checkInvariants() {
        // assert size >= 0;
        // assert size == elementData.length || elementData[size] == null;
    }
}
