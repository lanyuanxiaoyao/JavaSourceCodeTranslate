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

import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The root interface in the <i>collection hierarchy</i>.  A collection
 * represents a group of objects, known as its <i>elements</i>.  Some
 * collections allow duplicate elements and others do not.  Some are ordered
 * and others unordered.  The JDK does not provide any <i>direct</i>
 * implementations of this interface: it provides implementations of more
 * specific subinterfaces like {@code Set} and {@code List}.  This interface
 * is typically used to pass collections around and manipulate them where
 * maximum generality is desired.
 *
 * (Collection)是集合体系的根接口. 一个集合表示一组对象, 被称为集合的元素. 一些	
 * 集合允许出现重复的元素, 而另一些不允许. 一些集合是有序的, 而另一些是无序的.	
 * JDK没有直接提供这个接口的任何实现方式: 它提供了许多具体的子接口, 比如Set和List.	
 * 这个接口通常用于传递集合并对其进行操作, 这需要接口提供最大的通用方法.
 *
 * <p><i>Bags</i> or <i>multisets</i> (unordered collections that may contain
 * duplicate elements) should implement this interface directly.
 *
 * Bags 或 multisets(可包含重复元素的无序集合) 应该直接实现该接口.
 *
 * <p>All general-purpose {@code Collection} implementation classes (which
 * typically implement {@code Collection} indirectly through one of its
 * subinterfaces) should provide two "standard" constructors: a void (no
 * arguments) constructor, which creates an empty collection, and a
 * constructor with a single argument of type {@code Collection}, which
 * creates a new collection with the same elements as its argument.  In
 * effect, the latter constructor allows the user to copy any collection,
 * producing an equivalent collection of the desired implementation type.
 * There is no way to enforce this convention (as interfaces cannot contain
 * constructors) but all of the general-purpose {@code Collection}
 * implementations in the Java platform libraries comply.
 *
 * 所有通用接口Collection的实现类(通常是通过实现了Collection的子接口间接实现	
 * Collection接口)都应该提供两个"标准"构造方法: 一个空(没有参数)构造方法, 用来	
 * 创建一个空集合, 和一个只有一个类型为Collection的参数的构造方法, 用该参数集合	
 * 中完全相同的元素创建一个新的集合. 后者可以让用户复制任意一个集合, 生成所需	
 * 实现类的等效集合. 然而(在Java中)没办法强制执行这个约定(因为接口不能包含构造方法),	
 * 但在Java官方库所有通用接口Collection的实现类都符合这个约定. 
 *
 * <p>Certain methods are specified to be
 * <i>optional</i>. If a collection implementation doesn't implement a
 * particular operation, it should define the corresponding method to throw
 * {@code UnsupportedOperationException}. Such methods are marked "optional
 * operation" in method specifications of the collections interfaces.
 *
 * 某些方法是被指定为可选的. 如果一个集合的实现类没有实现一个可选操作, 它应该定义	
 * 相应的方法来抛出UnsupportedOperationException异常. 这些方法在集合接口规范中	
 * 被标记为"可选操作".
 *
 * <p><a id="optional-restrictions"></a>Some collection implementations
 * have restrictions on the elements that they may contain.
 * For example, some implementations prohibit null elements,
 * and some have restrictions on the types of their elements.  Attempting to
 * add an ineligible element throws an unchecked exception, typically
 * {@code NullPointerException} or {@code ClassCastException}.  Attempting
 * to query the presence of an ineligible element may throw an exception,
 * or it may simply return false; some implementations will exhibit the former
 * behavior and some will exhibit the latter.  More generally, attempting an
 * operation on an ineligible element whose completion would not result in
 * the insertion of an ineligible element into the collection may throw an
 * exception or it may succeed, at the option of the implementation.
 * Such exceptions are marked as "optional" in the specification for this
 * interface.
 *
 * 一些集合的实现类对它们可能会包含的元素有限制. 举个例子, 一些实现类禁止(包含)null元素,	
 * 一些实现类对它们(包含)的元素类型有限制. 试图添加一个非法的元素会使集合抛出一个	
 * unchecked的异常, 通常是NullPointerException或者ClassCastException.	
 * 试图查询一个非法元素是否存在集合里, 会使集合抛出一个异常, 或者简单地返回一个false;	
 * 一些实现类会是前者(抛异常), 一些实现类会是后者(返回false). 简单来说, 试图在集合里	
 * 操作一个非法元素, 其结果不会导致非法元素插入到集合中, 这可能会导致抛出一个异常, 或者会	
 * 操作成功, 这取决于实现类. 因此异常在这个接口的规范中被标记为可选的.
 *
 * <p>It is up to each collection to determine its own synchronization
 * policy.  In the absence of a stronger guarantee by the
 * implementation, undefined behavior may result from the invocation
 * of any method on a collection that is being mutated by another
 * thread; this includes direct invocations, passing the collection to
 * a method that might perform invocations, and using an existing
 * iterator to examine the collection.
 *
 * 集合(实现类)的同步策略由它们自己决定. 在没有强力(的同步策略)保证的实现类, 	
 * 被其他线程调用任何一个方法导致集合被改变都会导致未知的行为(后果); 包括直接调用, 	
 * 传递集合给一个可能会调用(集合)的方法, 并使用现有的迭代器来检查集合.
 *
 * <p>Many methods in Collections Framework interfaces are defined in
 * terms of the {@link Object#equals(Object) equals} method.  For example,
 * the specification for the {@link #contains(Object) contains(Object o)}
 * method says: "returns {@code true} if and only if this collection
 * contains at least one element {@code e} such that
 * {@code (o==null ? e==null : o.equals(e))}."  This specification should
 * <i>not</i> be construed to imply that invoking {@code Collection.contains}
 * with a non-null argument {@code o} will cause {@code o.equals(e)} to be
 * invoked for any element {@code e}.  Implementations are free to implement
 * optimizations whereby the {@code equals} invocation is avoided, for
 * example, by first comparing the hash codes of the two elements.  (The
 * {@link Object#hashCode()} specification guarantees that two objects with
 * unequal hash codes cannot be equal.)  More generally, implementations of
 * the various Collections Framework interfaces are free to take advantage of
 * the specified behavior of underlying {@link Object} methods wherever the
 * implementor deems it appropriate.
 *
 * Collections接口框架里的许多方法都是基于equals方法定义的. 举个例子, contains(Object o)
 * 方法在规范里是这么说的: "当且仅当这个集合中存在至少一个元素e, 使得
 * `o==null ? e==null : o.equals(e)`, 那么返回true." 这个规范不应该被解释为对于任意
 * 一个非空参数o都将使得对于任意的元素e调用o.equals(e). 实现类可以自由地优化实现(equals方法)
 * 从而避免equals方法的调用, 举个例子, 可以首先比较两个元素的哈希值. (规范将保证两个不同对象
 * 的哈希值不相等.) 一般来说, 只要实现者认为合适, 大多数Collections接口框架的实现类都可以自
 * 由地利用底层Object的指定方法.
 *
 * <p>Some collection operations which perform recursive traversal of the
 * collection may fail with an exception for self-referential instances where
 * the collection directly or indirectly contains itself. This includes the
 * {@code clone()}, {@code equals()}, {@code hashCode()} and {@code toString()}
 * methods. Implementations may optionally handle the self-referential scenario,
 * however most current implementations do not do so.
 *
 * 当集合直接或者间接包含自身的时候, 一些集合的操作在执行递归遍历集合的时候可能会失败并
 * 抛出引用自身实例的异常. (出现这种情况的方法)包括clone(), equals(), hashcode() 和
 * toString()方法. 实现类可以选择如何处理自引用的方式, 然而大部分(集合实现类)当前没有
 * 处理这种情况.
 *
 * <h2><a id="view">View Collections</a></h2>
 *
 * 视图集合
 *
 * <p>Most collections manage storage for elements they contain. By contrast, <i>view
 * collections</i> themselves do not store elements, but instead they rely on a
 * backing collection to store the actual elements. Operations that are not handled
 * by the view collection itself are delegated to the backing collection. Examples of
 * view collections include the wrapper collections returned by methods such as
 * {@link Collections#checkedCollection Collections.checkedCollection},
 * {@link Collections#synchronizedCollection Collections.synchronizedCollection}, and
 * {@link Collections#unmodifiableCollection Collections.unmodifiableCollection}.
 * Other examples of view collections include collections that provide a
 * different representation of the same elements, for example, as
 * provided by {@link List#subList List.subList},
 * {@link NavigableSet#subSet NavigableSet.subSet}, or
 * {@link Map#entrySet Map.entrySet}.
 * Any changes made to the backing collection are visible in the view collection.
 * Correspondingly, any changes made to the view collection &mdash; if changes
 * are permitted &mdash; are written through to the backing collection.
 * Although they technically aren't collections, instances of
 * {@link Iterator} and {@link ListIterator} can also allow modifications
 * to be written through to the backing collection, and in some cases,
 * modifications to the backing collection will be visible to the Iterator
 * during iteration.
 *
 * 大多数的集合负责管理他们包含的元素的存储空间. 相比之下, 视图集合本身不存储元素, 而是
 * 依赖于支持集合来存储实际元素. 视图集合自身未处理的操作被委托给了支持集合. 视图集合的
 * 例子包括一些方法返回的包装集合, 如Collections.checkedCollection, 
 * Collections.synchronizedCollection, Collections.unmodifiableCollection.
 * 视图集合的其他例子包括一些为相同元素提供了不同表示的集合, 如List.subList,
 * NavigableSet.subSet 或者 Map.entrySet. 对支持集合的任何改变都在视图集合中可见. 相
 * 应地, 任何在视图集合上的合法操作都会被写进支持集合当中. 虽然它们(视图集合)在技术上来说
 * 不是集合, Iterator 和 ListIterator 也可以使修改被写进支持集合中, 某些情况下, 迭代器
 * 在迭代的过程中可以看到对支持集合的修改.
 *
 * <h2><a id="unmodifiable">Unmodifiable Collections</a></h2>
 *
 * 不可修改集合
 *
 * <p>Certain methods of this interface are considered "destructive" and are called
 * "mutator" methods in that they modify the group of objects contained within
 * the collection on which they operate. They can be specified to throw
 * {@code UnsupportedOperationException} if this collection implementation
 * does not support the operation. Such methods should (but are not required
 * to) throw an {@code UnsupportedOperationException} if the invocation would
 * have no effect on the collection. For example, consider a collection that
 * does not support the {@link #add add} operation. What will happen if the
 * {@link #addAll addAll} method is invoked on this collection, with an empty
 * collection as the argument? The addition of zero elements has no effect,
 * so it is permissible for this collection simply to do nothing and not to throw
 * an exception. However, it is recommended that such cases throw an exception
 * unconditionally, as throwing only in certain cases can lead to
 * programming errors.
 *
 * 这个接口中的一些方法被认为是"破坏性的", 我们称之为"mutator"方法, 因为这些方法的操作
 * 修改了集合包含的对象组. 如果这个集合的实现类没有支持这些操作, 可以指定它们抛出
 * UnsupportedOperationException异常. 这些方法应该(但不强制)抛出
 * UnsupportedOperationException异常, 当调用(这些方法)对集合没有任何影响. 举个例子,
 * 考虑一个不支持add操作的集合. 当调用这个集合的addAll方法并使用一个空集合作为参数的
 * 时候会发生什么呢? 新增0个元素没有(对集合)造成影响(修改), 因此, 这就允许这个集合简单
 * 地什么也不做, 也不抛出任何异常. 然而, 建议在这种情况下无条件抛出异常, 只有在某些情况
 * 下会导致程序错误.
 *
 * <p>An <i>unmodifiable collection</i> is a collection, all of whose
 * mutator methods (as defined above) are specified to throw
 * {@code UnsupportedOperationException}. Such a collection thus cannot be
 * modified by calling any methods on it. For a collection to be properly
 * unmodifiable, any view collections derived from it must also be unmodifiable.
 * For example, if a List is unmodifiable, the List returned by
 * {@link List#subList List.subList} is also unmodifiable.
 *
 * 不可修改集合是一个指定所有mutator方法(如上述定义)抛出UnsupportedOperationException异常
 * 的集合. 因此这样的一个集合不能通过调用这些方法来修改, 任何从这个集合衍生的视图集合也必须
 * 是不可修改的. 举个例子, 如果一个List是不可修改的, 这个List返回的List.subList(子List)也是
 * 不可修改的.
 *
 * <p>An unmodifiable collection is not necessarily immutable. If the
 * contained elements are mutable, the entire collection is clearly
 * mutable, even though it might be unmodifiable. For example, consider
 * two unmodifiable lists containing mutable elements. The result of calling
 * {@code list1.equals(list2)} might differ from one call to the next if
 * the elements had been mutated, even though both lists are unmodifiable.
 * However, if an unmodifiable collection contains all immutable elements,
 * it can be considered effectively immutable.
 *
 * 一个不可修改集合不一定是不可变的. 如果包含的元素是可变的, 那整个集合显然是可变的, 
 * 即使它可能是不可修改的. 举个例子, 考虑两个包含可变元素的不可修改的list. 调用
 * list1.equals(list2)的结果可能因为一次调用而和下一次调用(list1.equals(list2))
 * 的结果不同, 如果元素已经改变了, 即使两个list都是不可修改的. 然而, 如果一个不可修改
 * 集合包含的元素都是不可变的, 那么它可以被等效看作是不可变的.
 *
 * <h2><a id="unmodview">Unmodifiable View Collections</a></h2>
 *
 * 不可修改视图集合
 *
 * <p>An <i>unmodifiable view collection</i> is a collection that is unmodifiable
 * and that is also a view onto a backing collection. Its mutator methods throw
 * {@code UnsupportedOperationException}, as described above, while
 * reading and querying methods are delegated to the backing collection.
 * The effect is to provide read-only access to the backing collection.
 * This is useful for a component to provide users with read access to
 * an internal collection, while preventing them from modifying such
 * collections unexpectedly. Examples of unmodifiable view collections
 * are those returned by the
 * {@link Collections#unmodifiableCollection Collections.unmodifiableCollection},
 * {@link Collections#unmodifiableList Collections.unmodifiableList}, and
 * related methods.
 *
 * 不可修改视图集合是一个不可修改集合, 同时也是支持集合上的一个视图. 它的mutator
 * 方法会抛出UnsupportedOperationException异常, 如上所述, 读取和查询方法被委托
 * 给了支持集合. 其效果就是对支持集合提供了只读访问. 这对组件来说非常有用, 可以为
 * 用户提供对内部集合的读访问权限, 同时防止他们意外修改了这个集合. 不可修改视图集合
 * 的例子有Collections.unmodifiableCollection, Collections.unmodifiableList和
 * 相关方法返回的(视图集合).
 *
 * <p>Note that changes to the backing collection might still be possible,
 * and if they occur, they are visible through the unmodifiable view. Thus,
 * an unmodifiable view collection is not necessarily immutable. However,
 * if the backing collection of an unmodifiable view is effectively immutable,
 * or if the only reference to the backing collection is through an
 * unmodifiable view, the view can be considered effectively immutable.
 *
 * 请注意, 对支持集合的修改仍然是可能的, 并且如果发生了, 它们(这些修改)在不可修改视图
 * 中是可见的. 因此, 不可修改视图集合不一定是不可变的. 然而, 如果不可修改视图的支持
 * 集合明显是不可变的, 或者如果对支持集合的引用仅通过一个不可修改视图, 那么这个视图就
 * 可以被等效看作是不可变的.
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/java.base/java/util/package-summary.html#CollectionsFramework">
 * Java Collections Framework</a>.
 *
 * @implSpec
 * The default method implementations (inherited or otherwise) do not apply any
 * synchronization protocol.  If a {@code Collection} implementation has a
 * specific synchronization protocol, then it must override default
 * implementations to apply that protocol.
 *
 * 默认方法的实现(继承或其他方式)不应使用任何同步协议. 如果Collection实现了特定的同步协议,
 * 那么它必须重写默认的实现来使用这个协议.
 *
 * @param <E> the type of elements in this collection 集合中元素的类型
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see     Set
 * @see     List
 * @see     Map
 * @see     SortedSet
 * @see     SortedMap
 * @see     HashSet
 * @see     TreeSet
 * @see     ArrayList
 * @see     LinkedList
 * @see     Vector
 * @see     Collections
 * @see     Arrays
 * @see     AbstractCollection
 * @since 1.2
 *
 * @translator LanyuanXiaoyao	
 * @date 2018.11.27
 */

public interface Collection<E> extends Iterable<E> {
    // Query Operations 查询操作

    /**
     * Returns the number of elements in this collection.  If this collection
     * contains more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     *
     * 返回集合中元素的数量. 如果集合包含大于Integer.MAX_VALUE数量的元素, 就返回
     * Integer.MAX_VALUE.
     *
     * @return the number of elements in this collection 集合中元素的数量
     */
    int size();

    /**
     * Returns {@code true} if this collection contains no elements.
     *
     * 如果集合中没有包含任何元素, 就返回true
     *
     * @return {@code true} if this collection contains no elements 如果集合中没有包含任何元素, 就返回true
     */
    boolean isEmpty();

    /**
     * Returns {@code true} if this collection contains the specified element.
     * More formally, returns {@code true} if and only if this collection
     * contains at least one element {@code e} such that
     * {@code Objects.equals(o, e)}.
     *
     * 如果集合中没有包含指定的元素, 就返回true. 准确来说, 当且仅当集合中包含至少一个
     * 元素e, 使得Objects.equals(o, e), 就返回true.
     *
     * @param o element whose presence in this collection is to be tested 将要被检查是否存在于当前集合的元素
     * @return {@code true} if this collection contains the specified
     *         element 如果集合中没有包含指定的元素, 就返回true
     * @throws ClassCastException if the type of the specified element
     *         is incompatible with this collection 如果指定元素的类型和当前集合的类型不相容
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *         collection does not permit null elements 如果指定元素是null并且当前集合不允许有null元素
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    boolean contains(Object o);

    /**
     * Returns an iterator over the elements in this collection.  There are no
     * guarantees concerning the order in which the elements are returned
     * (unless this collection is an instance of some class that provides a
     * guarantee).
     *
     * 返回一个包含集合所有元素的迭代器. 关于返回的元素的顺序没有任何保证(除非当前集合
     * 提供了保证).
     *
     * @return an {@code Iterator} over the elements in this collection 一个包含集合所有元素的迭代器
     */
    Iterator<E> iterator();

    /**
     * Returns an array containing all of the elements in this collection.
     * If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order. The returned array's {@linkplain Class#getComponentType
     * runtime component type} is {@code Object}.
     *
     * 返回一个包含当前集合所有元素的数组. 如果当前集合通过迭代器对它包含的元素的顺序
     * 作出了任何保证, 这个方法返回的元素也必须是按(和迭代器)相同的顺序. 返回的数组的
     * 元素的类型是Object.
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this collection.  (In other words, this method must
     * allocate a new array even if this collection is backed by an array).
     * The caller is thus free to modify the returned array.
     *
     * 返回的数组是"安全"的, 因为集合没有持有(数组元素)的引用. (换句话说, 这个方法
     * 必须申请一个新的数组即使这个集合是基于一个数组的). 调用者因此可以自由地修改
     * 返回的数组.
     *
     * @apiNote
     * This method acts as a bridge between array-based and collection-based APIs.
     * It returns an array whose runtime type is {@code Object[]}.
     * Use {@link #toArray(Object[]) toArray(T[])} to reuse an existing
     * array, or use {@link #toArray(IntFunction)} to control the runtime type
     * of the array.
     *
     * 这个方法是扮演着数组和集合之间的桥梁的API. (这个方法)返回的数组的运行时类型是Object[].
     * 使用toArray(T[])方法可以重用一个已经存在的数组, 或是使用toArray(IntFunction)方法来
     * 控制数组的运行时类型.
     *
     * @return an array, whose {@linkplain Class#getComponentType runtime component
     * type} is {@code Object}, containing all of the elements in this collection
     * 
     * 一个数组, 元素运行时类型是Object, 包含所有集合里的元素.
     */
    Object[] toArray();

    /**
     * Returns an array containing all of the elements in this collection;
     * the runtime type of the returned array is that of the specified array.
     * If the collection fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this collection.
     *
     * 返回一个包含当前集合所有元素的数组; 其返回的数组的运行时类型就是指定的数组(的类型).
     * 如果指定的数组能够放得下集合(的全部元素), 如果(参数)指定的数组能放得下list(的全部
     * 元素), 那么就会直接返回这个数组(并将list的元素放入数组中). 否则, 就会申请一个新的
     * 数组, 其类型与指定的数组类型相同, 大小则是list的大小.
     *
     * <p>If this collection fits in the specified array with room to spare
     * (i.e., the array has more elements than this collection), the element
     * in the array immediately following the end of the collection is set to
     * {@code null}.  (This is useful in determining the length of this
     * collection <i>only</i> if the caller knows that this collection does
     * not contain any {@code null} elements.)
     *
     * 如果(参数)指定的数组能放得下list, 并且数组的空间还有剩余(数组的元素比list的要
     * 多), 那么数组中在集合长度之后紧接着的一个数组元素将被设为null. (这仅在调用者确认
     * list中没有包含null元素的情况下, 确定list长度的时候非常管用.)
     *
     * <p>If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     *
     * 如果当前集合通过迭代器对它包含的元素的顺序作出了任何保证, 这个方法返回的元素也必
     * 须是按(和迭代器)相同的顺序.
     *
     * @apiNote
     * This method acts as a bridge between array-based and collection-based APIs.
     * It allows an existing array to be reused under certain circumstances.
     * Use {@link #toArray()} to create an array whose runtime type is {@code Object[]},
     * or use {@link #toArray(IntFunction)} to control the runtime type of
     * the array.
     *
     * 这个方法是扮演着数组和集合之间的桥梁的API. (这个方法)返回的数组的运行时类型是Object[].
     * 使用toArray(T[])方法可以重用一个已经存在的数组, 或是使用toArray(IntFunction)方法来
     * 控制数组的运行时类型.
     *
     * <p>Suppose {@code x} is a collection known to contain only strings.
     * The following code can be used to dump the collection into a previously
     * allocated {@code String} array:
     *
     * 假设x是一个已知的数组包含的唯一的字符串. 下面的代码常被用于将集合(中的元素)装进
     * 一个已经创建好的字符串数组里.
     *
     * <pre>
     *     String[] y = new String[SIZE];
     *     ...
     *     y = x.toArray(y);</pre>
     *
     * <p>The return value is reassigned to the variable {@code y}, because a
     * new array will be allocated and returned ifg the collection {@code x} has
     * too many elements to fit into the existing array {@code y}.
     *
     * 返回值被重新分配给变量y, 因为如果集合x包含了太多元素以至于无法放进已有数组y, 
     * 那么将会重新申请一个新的数组来返回.
     *
     * <p>Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     *
     * 请注意toArray(new Object[0])与方法toArray()是完全相同的.
     *
     * @param <T> the component type of the array to contain the collection 包含集合的数组元素的类型
     * @param a the array into which the elements of this collection are to be
     *        stored, if it is big enough; otherwise, a new array of the same
     *        runtime type is allocated for this purpose. 
     *
     *        如果数组足够大, 可以放下整个list的元素, 则用这个数组存储list的元素.
     *        否则, 就申请一个新的, 类型与a数组类型相同的数组, 来存储list的元素.
     *
     * @return an array containing all of the elements in this collection 一个包含集合所有元素的数组
     * @throws ArrayStoreException if the runtime type of any element in this
     *         collection is not assignable to the {@linkplain Class#getComponentType
     *         runtime component type} of the specified array 如果(参数)指定的数组的类型不是list里所有元素的父类型
     * @throws NullPointerException if the specified array is null 如果(参数)指定的数组为null
     */
    <T> T[] toArray(T[] a);

    /**
     * Returns an array containing all of the elements in this collection,
     * using the provided {@code generator} function to allocate the returned array.
     *
     * 使用提供的generator方法申请返回的数组来返回一个包含当前集合所有元素的数组.
     *
     * <p>If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     *
     * 如果当前集合通过迭代器对它包含的元素的顺序作出了任何保证, 这个方法返回的元素也必
     * 须是按(和迭代器)相同的顺序.
     *
     * @apiNote
     * This method acts as a bridge between array-based and collection-based APIs.
     * It allows creation of an array of a particular runtime type. Use
     * {@link #toArray()} to create an array whose runtime type is {@code Object[]},
     * or use {@link #toArray(Object[]) toArray(T[])} to reuse an existing array.
     *
     * 这个方法是扮演着数组和集合之间的桥梁的API. (这个方法)允许创建一个特定的运行时类型的
     * 数组. 使用toArray()创建一个运行时类型为Object[]的数组, 或者使用toArray(T[])来复用
     * 一个已经存在的数组.
     *
     * <p>Suppose {@code x} is a collection known to contain only strings.
     * The following code can be used to dump the collection into a newly
     * allocated array of {@code String}:
     *
     * 假设x是一个已知的数组包含的唯一的字符串. 下面的代码常被用于将集合(中的元素)装进
     * 一个已经新建的(元素类型)为字符串的数组里.
     *
     * <pre>
     *     String[] y = x.toArray(String[]::new);</pre>
     *
     * @implSpec
     * The default implementation calls the generator function with zero
     * and then passes the resulting array to {@link #toArray(Object[]) toArray(T[])}.
     *
     * 默认实现调用构造器(使用的参数)是0, 并且使用toArray(T[])作为结果数组.
     *
     * (实际上是一个提供给lambda表达式使用的扩展方法)
     *
     * @param <T> the component type of the array to contain the collection 包含集合的数组元素的类型
     * @param generator a function which produces a new array of the desired
     *                  type and the provided length 一个根据需要的类型构建一个新数组, 并提供其长度的方法
     * @return an array containing all of the elements in this collection 一个包含集合所有元素的数组
     * @throws ArrayStoreException if the runtime type of any element in this
     *         collection is not assignable to the {@linkplain Class#getComponentType
     *         runtime component type} of the generated array
     *
     *         如果集合中的任何一个元素的运行时状态不能被转换为getComponentType方法获得的运行时类型
     *
     * @throws NullPointerException if the generator function is null 如果构造方法是null
     * @since 11
     */
    default <T> T[] toArray(IntFunction<T[]> generator) {
        return toArray(generator.apply(0));
    }

    // Modification Operations 修改操作

    /**
     * Ensures that this collection contains the specified element (optional
     * operation).  Returns {@code true} if this collection changed as a
     * result of the call.  (Returns {@code false} if this collection does
     * not permit duplicates and already contains the specified element.)<p>
     *
     * 确保集合包含指定的元素(可选操作). 如果集合被(这个方法)的调用改变了, 就返回
     * true. 如果这个集合不允许重复元素并且已经包含了指定元素, 就返回false.
     *
     * Collections that support this operation may place limitations on what
     * elements may be added to this collection.  In particular, some
     * collections will refuse to add {@code null} elements, and others will
     * impose restrictions on the type of elements that may be added.
     * Collection classes should clearly specify in their documentation any
     * restrictions on what elements may be added.<p>
     *
     * 支持这个操作的集合可能会对添加什么元素到这个集合里进行限制. 特别的, 一些集合
     * 会拒绝添加null元素, 并且另一些集合会限制可能被添加到集合里的元素的类型. 
     * 集合类应该在文档里清晰地指明可能被添加到集合里的元素的限制.
     *
     * If a collection refuses to add a particular element for any reason
     * other than that it already contains the element, it <i>must</i> throw
     * an exception (rather than returning {@code false}).  This preserves
     * the invariant that a collection always contains the specified element
     * after this call returns.
     *
     * 如果集合因为任何原因拒绝添加一个特别的元素, 除了该集合已经包含该元素, 它都必须
     * 抛出一个异常(而不是返回false). 这样可以维护一个集合在(add)方法调用后总是会包含
     * 这个特别的元素(的结果).
     *
     * @param e element whose presence in this collection is to be ensured 要确保在该集合中存在的元素
     * @return {@code true} if this collection changed as a result of the
     *         call 如果集合被该方法的调用改变了, 就返回true
     * @throws UnsupportedOperationException if the {@code add} operation
     *         is not supported by this collection 如果这个集合不支持add操作
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this collection 如果指定元素由于类型在被添加到这个集合的时候被拒绝
     * @throws NullPointerException if the specified element is null and this
     *         collection does not permit null elements 如果指定的元素是null并且这个集合不允许存在null元素
     * @throws IllegalArgumentException if some property of the element
     *         prevents it from being added to this collection 如果元素的某些属性(导致)被拒绝添加进这个集合
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to insertion restrictions 如果由于插入限制导致元素不能在这个时候被添加
     */
    boolean add(E e);

    /**
     * Removes a single instance of the specified element from this
     * collection, if it is present (optional operation).  More formally,
     * removes an element {@code e} such that
     * {@code Objects.equals(o, e)}, if
     * this collection contains one or more such elements.  Returns
     * {@code true} if this collection contained the specified element (or
     * equivalently, if this collection changed as a result of the call).
     *
     * 从这个集合中移除一个指定的元素, 如果存在的话(可选操作). 更准确来说, 移除一个
     * 元素e, 满足Objects.equals(o, e), 如果这个集合包含一个或多个该元素. 如果
     * 这个集合包含指定的元素(即当这个集合被这个方法的调用改变了), 就返回true.
     *
     * @param o element to be removed from this collection, if present 将被从这个集合中移除的元素, 如果存在
     * @return {@code true} if an element was removed as a result of this call 如果一个元素因为这个方法的调用从集合里被移除了
     * @throws ClassCastException if the type of the specified element
     *         is incompatible with this collection 如果指定元素的类型与这个集合不相符
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *         collection does not permit null elements 如果指定的元素是null并且这个集合不允许存在null元素
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws UnsupportedOperationException if the {@code remove} operation
     *         is not supported by this collection 如果这个集合不支持remove操作
     */
    boolean remove(Object o);


    // Bulk Operations 批量操作

    /**
     * Returns {@code true} if this collection contains all of the elements
     * in the specified collection.
     *
     * 如果这个集合包含指定集合里的所有元素, 就返回true
     *
     * @param  c collection to be checked for containment in this collection 需要被检查在这个集合中的包含状况的集合
     * @return {@code true} if this collection contains all of the elements
     *         in the specified collection 如果这个集合包含指定集合里的所有元素, 就返回true
     * @throws ClassCastException if the types of one or more elements
     *         in the specified collection are incompatible with this
     *         collection 如果指定集合有一个或多个元素的类型与这个集合不相符
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified collection contains one
     *         or more null elements and this collection does not permit null
     *         elements
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null. 如果指定集合存在一个或多个元素是null并且这个集合不允许存在null元素或指定的集合是null
     * @see    #contains(Object)
     */
    boolean containsAll(Collection<?> c);

    /**
     * Adds all of the elements in the specified collection to this collection
     * (optional operation).  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified collection is this collection, and this collection is
     * nonempty.)
     *
     * 添加指定集合里的所有元素到当前集合中(可选操作). 这个方法没有定义的一个操作是在方
     * 法执行的过程中, 如果指定的Collection集合被修改了. (这意味着, 如果指定的Collection
     * 集合就是该集合并且集合非空, 那么这种调用的行为是没有定义的.)
     *
     * @param c collection containing elements to be added to this collection 将要被添加进当前集合中的元素的Collection集合
     * @return {@code true} if this collection changed as a result of the call 如果集合被该方法的调用改变了, 就返回true
     * @throws UnsupportedOperationException if the {@code addAll} operation
     *         is not supported by this collection 如果这个集合不支持addAll操作
     * @throws ClassCastException if the class of an element of the specified
     *         collection prevents it from being added to this collection 如果指定集合中的一个元素的类型被当前集合拒绝添加
     * @throws NullPointerException if the specified collection contains a
     *         null element and this collection does not permit null elements,
     *         or if the specified collection is null 如果指定集合包含一个元素是null并且这个集合不允许存在null元素或指定的集合是null
     * @throws IllegalArgumentException if some property of an element of the
     *         specified collection prevents it from being added to this
     *         collection 如果元素的某些属性(导致)被拒绝添加进这个集合
     * @throws IllegalStateException if not all the elements can be added at
     *         this time due to insertion restrictions 如果由于插入限制导致不是全部元素能在这个时候被添加
     * @see #add(Object)
     */
    boolean addAll(Collection<? extends E> c);

    /**
     * Removes all of this collection's elements that are also contained in the
     * specified collection (optional operation).  After this call returns,
     * this collection will contain no elements in common with the specified
     * collection.
     *
     * 移除这个集合中所有包含在指定集合中的元素(可选操作). 在这个方法被调用之后, 这个集合
     * 将不再包含有与制定集合公有的元素.
     *
     * @param c collection containing elements to be removed from this collection 将要被从当前集合中移除的元素的Collection集合
     * @return {@code true} if this collection changed as a result of the
     *         call 如果集合被该方法的调用改变了, 就返回true
     * @throws UnsupportedOperationException if the {@code removeAll} method
     *         is not supported by this collection 如果这个集合不支持removeAll操作
     * @throws ClassCastException if the types of one or more elements
     *         in this collection are incompatible with the specified
     *         collection 如果指定集合有一个或多个元素的类型与这个集合不相符
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this collection contains one or more
     *         null elements and the specified collection does not support
     *         null elements
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null 如果指定集合包含一个或多个元素是null并且这个集合不允许存在null元素或指定的集合是null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    boolean removeAll(Collection<?> c);

    /**
     * Removes all of the elements of this collection that satisfy the given
     * predicate.  Errors or runtime exceptions thrown during iteration or by
     * the predicate are relayed to the caller.
     *
     * 移除当前集合中满足给定的表达式的元素. 在迭代过程中表达式中发生的错误和抛出的运
     * 行时异常将被发送被调用者. 
     *
     * @implSpec
     * The default implementation traverses all elements of the collection using
     * its {@link #iterator}.  Each matching element is removed using
     * {@link Iterator#remove()}.  If the collection's iterator does not
     * support removal then an {@code UnsupportedOperationException} will be
     * thrown on the first matching element.
     *
     * 默认实现使用迭代器来遍历集合中的所有元素. 每个匹配的元素将使用remove()方法移除.
     * 如果集合的迭代器不支持移除(操作), 那么在第一个匹配的元素的地方将抛出一个
     * UnsupportedOperationException异常.
     *
     * @param filter a predicate which returns {@code true} for elements to be
     *        removed 一个(如果匹配)要移除的元素就返回true的表达式
     * @return {@code true} if any elements were removed 如果有任何一个元素被移除
     * @throws NullPointerException if the specified filter is null 如果指定的过滤器(表达式)是null
     * @throws UnsupportedOperationException if elements cannot be removed
     *         from this collection.  Implementations may throw this exception if a
     *         matching element cannot be removed or if, in general, removal is not
     *         supported. 如果元素不能从集合中移除. 实现类应当在一个元素不能被移除或者说移除操作不支持的时候抛出这个异常.
     * @since 1.8
     */
    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        // 使用标志位来表示迭代有没有成功
        boolean removed = false;
        final Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }

    /**
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation).  In other words, removes from
     * this collection all of its elements that are not contained in the
     * specified collection.
     *
     * 只保留仅在当前集合中并同时被包含在指定集合中的元素(可选操作). 换句话说, 移除当
     * 前集合中所有没有被包含在指定集合中的元素.
     *
     * @param c collection containing elements to be retained in this collection 包含要在当前集合中被保留的元素的集合
     * @return {@code true} if this collection changed as a result of the call 如果集合被该方法的调用改变了, 就返回true
     * @throws UnsupportedOperationException if the {@code retainAll} operation
     *         is not supported by this collection 如果这个集合不支持retainAll操作
     * @throws ClassCastException if the types of one or more elements
     *         in this collection are incompatible with the specified
     *         collection 如果指定集合有一个或多个元素的类型与这个集合不相符
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this collection contains one or more
     *         null elements and the specified collection does not permit null
     *         elements 如果指定集合包含一个或多个元素是null并且这个集合不允许存在null元素或指定的集合是null
     *         (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    boolean retainAll(Collection<?> c);

    /**
     * Removes all of the elements from this collection (optional operation).
     * The collection will be empty after this method returns.
     *
     * 从当前集合中移除全部元素(可选操作). 在这个方法返回之后, 集合将会空空如也.
     *
     * @throws UnsupportedOperationException if the {@code clear} operation
     *         is not supported by this collection 如果这个集合不支持clear操作
     */
    void clear();


    // Comparison and hashing

    /**
     * Compares the specified object with this collection for equality. <p>
     *
     * While the {@code Collection} interface adds no stipulations to the
     * general contract for the {@code Object.equals}, programmers who
     * implement the {@code Collection} interface "directly" (in other words,
     * create a class that is a {@code Collection} but is not a {@code Set}
     * or a {@code List}) must exercise care if they choose to override the
     * {@code Object.equals}.  It is not necessary to do so, and the simplest
     * course of action is to rely on {@code Object}'s implementation, but
     * the implementor may wish to implement a "value comparison" in place of
     * the default "reference comparison."  (The {@code List} and
     * {@code Set} interfaces mandate such value comparisons.)<p>
     *
     * The general contract for the {@code Object.equals} method states that
     * equals must be symmetric (in other words, {@code a.equals(b)} if and
     * only if {@code b.equals(a)}).  The contracts for {@code List.equals}
     * and {@code Set.equals} state that lists are only equal to other lists,
     * and sets to other sets.  Thus, a custom {@code equals} method for a
     * collection class that implements neither the {@code List} nor
     * {@code Set} interface must return {@code false} when this collection
     * is compared to any list or set.  (By the same logic, it is not possible
     * to write a class that correctly implements both the {@code Set} and
     * {@code List} interfaces.)
     *
     * @param o object to be compared for equality with this collection
     * @return {@code true} if the specified object is equal to this
     * collection
     *
     * @see Object#equals(Object)
     * @see Set#equals(Object)
     * @see List#equals(Object)
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this collection.  While the
     * {@code Collection} interface adds no stipulations to the general
     * contract for the {@code Object.hashCode} method, programmers should
     * take note that any class that overrides the {@code Object.equals}
     * method must also override the {@code Object.hashCode} method in order
     * to satisfy the general contract for the {@code Object.hashCode} method.
     * In particular, {@code c1.equals(c2)} implies that
     * {@code c1.hashCode()==c2.hashCode()}.
     *
     * @return the hash code value for this collection
     *
     * @see Object#hashCode()
     * @see Object#equals(Object)
     */
    int hashCode();

    /**
     * Creates a {@link Spliterator} over the elements in this collection.
     *
     * 创建一个(作用在)当前集合所有元素上的Spliterator(分割器).
     *
     * Implementations should document characteristic values reported by the
     * spliterator.  Such characteristic values are not required to be reported
     * if the spliterator reports {@link Spliterator#SIZED} and this collection
     * contains no elements.
     *
     * 实现类应该记录由spliterator报告的特征值. 如果spliterator报告SIZED值并且集合不
     * 包含任何元素, 则不需要报告此类特征值.
     *
     * <p>The default implementation should be overridden by subclasses that
     * can return a more efficient spliterator.  In order to
     * preserve expected laziness behavior for the {@link #stream()} and
     * {@link #parallelStream()} methods, spliterators should either have the
     * characteristic of {@code IMMUTABLE} or {@code CONCURRENT}, or be
     * <em><a href="Spliterator.html#binding">late-binding</a></em>.
     * If none of these is practical, the overriding class should describe the
     * spliterator's documented policy of binding and structural interference,
     * and should override the {@link #stream()} and {@link #parallelStream()}
     * methods to create streams using a {@code Supplier} of the spliterator,
     * as in:
     *
     * 默认实现应该被子类重写, 这些子类可以返回更有效率的spliterator. 为了保持对stream()
     * 和parallelStream()方法预期的延迟(加载)的行为, spliterator应该同时拥有不可变, 可并发, 
     * 和延迟加载这几个特征. 如果这些都不采用, 那么继承的类应该描述spliterator记录的约束和
     * 结构化(修改)干扰的策略, 并且应该重写stream()和parallelStream()方法, 使用spliterator
     * 的Supplier来创建流, 如下所示:
     *
     * <pre>{@code
     *     Stream<E> s = StreamSupport.stream(() -> spliterator(), spliteratorCharacteristics)
     * }</pre>
     *
     * <p>These requirements ensure that streams produced by the
     * {@link #stream()} and {@link #parallelStream()} methods will reflect the
     * contents of the collection as of initiation of the terminal stream
     * operation.
     *
     * 这些要求确保stream()和parallelStream()方法提供的流将会在终端启动流操作
     * 的时候(准确)反映集合的内容.
     *
     * @implSpec
     * The default implementation creates a
     * <em><a href="Spliterator.html#binding">late-binding</a></em> spliterator
     * from the collection's {@code Iterator}.  The spliterator inherits the
     * <em>fail-fast</em> properties of the collection's iterator.
     * <p>
     * The created {@code Spliterator} reports {@link Spliterator#SIZED}.
     *
     * @implNote
     * The created {@code Spliterator} additionally reports
     * {@link Spliterator#SUBSIZED}.
     *
     * <p>If a spliterator covers no elements then the reporting of additional
     * characteristic values, beyond that of {@code SIZED} and {@code SUBSIZED},
     * does not aid clients to control, specialize or simplify computation.
     * However, this does enable shared use of an immutable and empty
     * spliterator instance (see {@link Spliterators#emptySpliterator()}) for
     * empty collections, and enables clients to determine if such a spliterator
     * covers no elements.
     *
     * @return a {@code Spliterator} over the elements in this collection
     * @since 1.8
     */
    @Override
    default Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, 0);
    }

    /**
     * Returns a sequential {@code Stream} with this collection as its source.
     *
     * 返回一个以当前集合作为数据源的顺序流.
     *
     * <p>This method should be overridden when the {@link #spliterator()}
     * method cannot return a spliterator that is {@code IMMUTABLE},
     * {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()}
     * for details.)
     *
     * 当spliterator()方法因为不可变, 并发或延迟加载而不能返回spliterator的时候, 这个
     * 方法应该要被重写.
     *
     * @implSpec
     * The default implementation creates a sequential {@code Stream} from the
     * collection's {@code Spliterator}.
     *
     * 默认实现从集合的Spliterator创建一个顺序流.
     *
     * @return a sequential {@code Stream} over the elements in this collection 一个当前集合所有元素的顺序流
     * @since 1.8
     */
    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Returns a possibly parallel {@code Stream} with this collection as its
     * source.  It is allowable for this method to return a sequential stream.
     *
     * 返回一个以当前集合作为数据源的可能是并发的流. 这个方法允许返回一个顺序流.
     *
     * <p>This method should be overridden when the {@link #spliterator()}
     * method cannot return a spliterator that is {@code IMMUTABLE},
     * {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()}
     * for details.)
     *
     * 当spliterator()方法因为不可变, 并发或延迟加载而不能返回spliterator的时候, 这个
     * 方法应该要被重写.
     *
     * @implSpec
     * The default implementation creates a parallel {@code Stream} from the
     * collection's {@code Spliterator}.
     *
     * 默认实现从集合的Spliterator创建一个并发流.
     *
     * @return a possibly parallel {@code Stream} over the elements in this
     * collection 一个当前集合所有元素的可能并发的流
     * @since 1.8
     */
    default Stream<E> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
}
