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
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent;

import java.util.Collection;
import java.util.Queue;

/**
 * A {@link Queue} that additionally supports operations that wait for
 * the queue to become non-empty when retrieving an element, and wait
 * for space to become available in the queue when storing an element.
 *
 * BlockingQueue在Queue的基础上增加了一个操作：
 *      1、当从队列中取对象的时候，等待queue变成非空（再取）；
 *      2、当往队列中加对象的时候，等待queue有空间（再加）。
 *
 * （这段话是BlockingQueue的特性，也是了解它的第一步。后面的翻译待有了一定的考虑和资料册查阅再重新加上。
 *   这是一个标准接口，后面会细分各种不同子特性的BlockingQueue：
 *   比如利用可重入锁实现的ArrayBlockingQueue以及利用链表的LinedBlockingQueue等。
 *   这算开了个小小的头。这个源码翻译（其实是为了阅读源码）的计划我觉得是个很不错的技术沉淀的机会。重在积累，贵在坚持。
 *  ）
 *
 * <p>{@code BlockingQueue} methods come in four forms, with different ways
 * of handling operations that cannot be satisfied immediately, but may be
 * satisfied at some point in the future:
 * one throws an exception, the second returns a special value (either
 * {@code null} or {@code false}, depending on the operation), the third
 * blocks the current thread indefinitely until the operation can succeed,
 * and the fourth blocks for only a given maximum time limit before giving
 * up.  These methods are summarized in the following table:
 *
 * BlockingQueue的方法有四种形式，通过不同的方式来处理如下那些不能被立即满足，但是在未来的某个时刻能够被满足的操作。
 * 第一种是抛出异常；
 * 第二种返回一个特殊值（要么是null,要么是false），第二种方式取决于你选的是哪种操作；
 * 第三种一直阻塞当前的线程，直到操作被成功执行；
 * 第四种也是线程被阻塞，但是阻塞时间并不是永久的，而是有个被给定的最大时间。
 * 这些操作被总结到如下的表格当中：
 *
 * <table class="plain">
 * <caption>Summary of BlockingQueue methods</caption>
 *  <tr>
 *    <td></td>
 *    <th scope="col" style="font-weight:normal; font-style:italic">Throws exception</th>
 *    <th scope="col" style="font-weight:normal; font-style:italic">Special value</th>
 *    <th scope="col" style="font-weight:normal; font-style:italic">Blocks</th>
 *    <th scope="col" style="font-weight:normal; font-style:italic">Times out</th>
 *  </tr>
 *  <tr>
 *    <th scope="row" style="text-align:left">Insert</th>
 *    <td>{@link #add(Object) add(e)}</td>
 *    <td>{@link #offer(Object) offer(e)}</td>
 *    <td>{@link #put(Object) put(e)}</td>
 *    <td>{@link #offer(Object, long, TimeUnit) offer(e, time, unit)}</td>
 *  </tr>
 *  <tr>
 *    <th scope="row" style="text-align:left">Remove</th>
 *    <td>{@link #remove() remove()}</td>
 *    <td>{@link #poll() poll()}</td>
 *    <td>{@link #take() take()}</td>
 *    <td>{@link #poll(long, TimeUnit) poll(time, unit)}</td>
 *  </tr>
 *  <tr>
 *    <th scope="row" style="text-align:left">Examine</th>
 *    <td>{@link #element() element()}</td>
 *    <td>{@link #peek() peek()}</td>
 *    <td style="font-style: italic">not applicable</td>
 *    <td style="font-style: italic">not applicable</td>
 *  </tr>
 * </table>
 *
 * 上面的HTML表格字符串显示可能不够直观，直观的表格如下图：
 *
 *         Throws-exception  Special—value   Blocks        Times out
 * Insert      add(e)          offer(e)      put(e)       offer(e, time, unit)
 * Remove      remove()	       poll()        take()       poll(time, unit)
 * Examine     element()       peek()     not-applicable  not applicable
 *
 *
 * <p>A {@code BlockingQueue} does not accept {@code null} elements.
 * Implementations throw {@code NullPointerException} on attempts
 * to {@code add}, {@code put} or {@code offer} a {@code null}.  A
 * {@code null} is used as a sentinel value to indicate failure of
 * {@code poll} operations.
 *
 * BlockingQueue是不能放置null值的，当你尝试add/put/offer null的时候，会抛出NullPointerException。
 * null在BlockingQueue被当做一个"哨兵值"，用来表示poll操作的失败。
 *
 * <p>A {@code BlockingQueue} may be capacity bounded. At any given
 * time it may have a {@code remainingCapacity} beyond which no
 * additional elements can be {@code put} without blocking.
 * A {@code BlockingQueue} without any intrinsic capacity constraints always
 * reports a remaining capacity of {@code Integer.MAX_VALUE}.
 *
 * BlockQueue应该是有容量大小限制的。在任何给定的时间里，它可能有一个剩余容量的概念，这个是个什么样的东西呢，
 * 就是说，当超过这个容量的时候，元素能被加入进去，并且不会被阻塞。
 * BlockingQueue在没有任何固有容量大小约束的时候，默认大小为Integer.MAX_VALUE (也就是2147483647，一般这么大是没啥意义的)
 *
 * <p>{@code BlockingQueue} implementations are designed to be used
 * primarily for producer-consumer queues, but additionally support
 * the {@link Collection} interface.  So, for example, it is
 * possible to remove an arbitrary element from a queue using
 * {@code remove(x)}. However, such operations are in general
 * <em>not</em> performed very efficiently, and are intended for only
 * occasional use, such as when a queued message is cancelled.
 *
 * BlockingQueue接口是被用来设计生产者消费者队列的首选，但是除此之外还需要支持Collection接口。
 * 所以，比如，有可能通过使用remove()方法从队列中移除一个任意的元素。
 * 但是，这样的操作通常执行得不是很有效，而且只打算偶尔使用，比如在取消队列消息时。
 *
 * <p>{@code BlockingQueue} implementations are thread-safe.  All
 * queuing methods achieve their effects atomically using internal
 * locks or other forms of concurrency control. However, the
 * <em>bulk</em> Collection operations {@code addAll},
 * {@code containsAll}, {@code retainAll} and {@code removeAll} are
 * <em>not</em> necessarily performed atomically unless specified
 * otherwise in an implementation. So it is possible, for example, for
 * {@code addAll(c)} to fail (throwing an exception) after adding
 * only some of the elements in {@code c}.
 *
 * BlockingQueue接口实现是线程安全的。所有的队列方法，以原子性来实现它们的效果，通过使用内部的锁或者其它形式的并发控制。
 * 然而，跟容器容量有关的操作：addAll,containsAll,retainAll,retainAll是没有必要做到原子性的，除非在实现中另外指定。
 * 所以，比如，addAll(c)只可能在加入一些元素到c失败之后，才失败（抛出一个异常）。
 *
 * <p>A {@code BlockingQueue} does <em>not</em> intrinsically support
 * any kind of &quot;close&quot; or &quot;shutdown&quot; operation to
 * indicate that no more items will be added.  The needs and usage of
 * such features tend to be implementation-dependent. For example, a
 * common tactic is for producers to insert special
 * <em>end-of-stream</em> or <em>poison</em> objects, that are
 * interpreted accordingly when taken by consumers.
 *
 * BlockingQueue从本质上并不支持close或者shutdown操作以表示不能再计加入更多的元素。
 * 这些特性的需求和使用通常需要依赖于相应的实现。比如，一种比较通常的策略是让生产者插入特殊的 结束流，或者特殊的有害对象（用来close Queue），
 * 当消费者采取这些措施时，就会作出相应的解释。
 *
 * <p>
 * Usage example, based on a typical producer-consumer scenario.
 * Note that a {@code BlockingQueue} can safely be used with multiple
 * producers and multiple consumers.
 *
 * demo,基于一个典型的生产者-消费者场景。请注意，BlockingQueue可以安全地被多个生产者和消费者使用。
 *
 * <pre> {@code
 * class Producer implements Runnable {
 *   private final BlockingQueue queue;
 *   Producer(BlockingQueue q) { queue = q; }
 *   public void run() {
 *     try {
 *       while (true) { queue.put(produce()); }
 *     } catch (InterruptedException ex) { ... handle ...}
 *   }
 *   Object produce() { ... }
 * }
 *
 * class Consumer implements Runnable {
 *   private final BlockingQueue queue;
 *   Consumer(BlockingQueue q) { queue = q; }
 *   public void run() {
 *     try {
 *       while (true) { consume(queue.take()); }
 *     } catch (InterruptedException ex) { ... handle ...}
 *   }
 *   void consume(Object x) { ... }
 * }
 *
 * class Setup {
 *   void main() {
 *     BlockingQueue q = new SomeQueueImplementation();
 *     Producer p = new Producer(q);
 *     Consumer c1 = new Consumer(q);
 *     Consumer c2 = new Consumer(q);
 *     new Thread(p).start();
 *     new Thread(c1).start();
 *     new Thread(c2).start();
 *   }
 * }}</pre>
 *
 * 代码没啥好解释的，这里实现的是 take和put 方法，这个是会被阻塞的方法；
 *
 * <p>Memory consistency effects: As with other concurrent
 * collections, actions in a thread prior to placing an object into a
 * {@code BlockingQueue}
 * <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * actions subsequent to the access or removal of that element from
 * the {@code BlockingQueue} in another thread.
 *
 * 内存一致性影响：和其它并发的Collections一样，在一个线程中去放置一个元素到BlockingQueue发生在前，
 * 或者在另外一个线程里访问或者删除这个元素发生在之后。
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/java.base/java/util/package-summary.html#CollectionsFramework">
 * Java Collections Framework</a>.
 *
 * 这个接口（BlockingQueue）是Java Collections框架大家族的一员。
 * @since 1.5
 * @author Doug Lea
 * @param <E> the type of elements held in this queue
 */
public interface BlockingQueue<E> extends Queue<E> {
    /**
     * Inserts the specified element into this queue if it is possible to do
     * so immediately without violating capacity restrictions, returning
     * {@code true} upon success and throwing an
     * {@code IllegalStateException} if no space is currently available.
     * When using a capacity-restricted queue, it is generally preferable to
     * use {@link #offer(Object) offer}.
     *
     * 插入指定的元素到一个队列里面，在不违背队列容量限制的情况下还是可以操作的，并且返回true，当队列没有剩余空间可用了就抛出一个IllegalStateException的异常；
     * 当使用一个有容量大小限制的队列的时候，通常更倾向于使用offer()来替代add() （因为offer失败会返回false，而add失败会抛异常）
     *
     * @param e the element to add
     *        参数是一个要加入的元素
     * @return {@code true} (as specified by {@link Collection#add})
     *          返回true（由Collection的add方法所指定）
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to capacity restrictions
     *         抛出异常：IllegalStateException，如果这个元素由于队列容量限制而不能被加入，这个是状态非法异常
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     *         抛出异常：ClassCastException，如果指定元素的类阻止它被添加到此队列中，意思就是你要加的对象的类型跟这个队列指定的对象类型不一致
     * @throws NullPointerException if the specified element is null
     *         抛出异常：NullPointerException，你加入null到这个队列的时候会抛出这个异常
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
     *         抛出异常：IllegalArgumentException，如果指定元素的某些属性阻止它被添加到此队列中，这个是参数非法异常
     */
    boolean add(E e);

    /**
     * Inserts the specified element into this queue if it is possible to do
     * so immediately without violating capacity restrictions, returning
     * {@code true} upon success and {@code false} if no space is currently
     * available.  When using a capacity-restricted queue, this method is
     * generally preferable to {@link #add}, which can fail to insert an
     * element only by throwing an exception.
     *
     * 插入一个元素到目标队列中，如果没有违反这个队列的容量限制的话，会立即得到执行。
     * 返回true，当成功插入元素到队列的时候。返回false，当容量不足以让目标元素插入的时候。
     * 当使用一个有容量限制的队列的时候，offer()方法是比add()方法更好的选择，因为add()在操作失败的时候会抛出异常。
     *
     *  下面的参数类似上面的add方法，之后return不同
     * @param e the element to add
     * @return {@code true} if the element was added to this queue, else
     *         {@code false}
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
     */
    boolean offer(E e);

    /**
     * Inserts the specified element into this queue, waiting if necessary
     * for space to become available.
     *
     * 插入目标元素到队列当中，（当队列空间不足的时候），线程等待，直到有可用空间为止。
     *
     * @param e the element to add
     *          要插入的元素
     *
     * @throws InterruptedException if interrupted while waiting
     *         抛出异常：InterruptedException，如果线程等待的时候被中断了
     *
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     *         抛出异常：ClassCastException，插入的元素不符合队列要求的对象类型
     *
     * @throws NullPointerException if the specified element is null
     *         抛出异常：NullPointerException，插入元素是null
     *
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
     */
    void put(E e) throws InterruptedException;

    /**
     * Inserts the specified element into this queue, waiting up to the
     * specified wait time if necessary for space to become available.
     *
     * 插入目标元素到这个队列当中，如果有需要的话（就是说队列还没有空闲空间的时候），等待特定时间，也就是这个调用这个方法的时候指定的时间。
     * 这个方法和上面的 offer(e) 方法类似，唯一的不同在于这个方法可以指定等待的最大时间，timeout是个类型为long的数字表示时间，unit是时间单位，即指定timeout的单位
     *
     * @param e the element to add
     * @param timeout how long to wait before giving up, in units of
     *        {@code unit}
     * @param unit a {@code TimeUnit} determining how to interpret the
     *        {@code timeout} parameter
     * @return {@code true} if successful, or {@code false} if
     *         the specified waiting time elapses before space is available
     * @throws InterruptedException if interrupted while waiting
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
     */
    boolean offer(E e, long timeout, TimeUnit unit)
            throws InterruptedException;

    /**
     * Retrieves and removes the head of this queue, waiting if necessary
     * until an element becomes available.
     *
     * 检索并删除此队列的head元素（也就是取队列头的意思），如果有必要的话（就是说队列还没有空闲空间的时候），将等待直到有元素可用为止，也就是如果队列为空的话，它会等到里面有元素再拿
     *
     * @return the head of this queue
     * 返回队列头
     *
     * @throws InterruptedException if interrupted while waiting
     * 抛出异常：InterruptedException，如果在等待的时候线程被中断的话
     */
    E take() throws InterruptedException;

    /**
     * Retrieves and removes the head of this queue, waiting up to the
     * specified wait time if necessary for an element to become available.
     *
     * 检索并删除此队列的head元素（也就是取队列头的意思），如果有必要的话，将等待特定时间，到队列当中有元素可以拿的时候为止，最长等待时间为指定的timeout的值
     *
     * @param timeout how long to wait before giving up, in units of
     *        {@code unit}
     * 这个是最长等待时间
     *
     * @param unit a {@code TimeUnit} determining how to interpret the
     *        {@code timeout} parameter
     * timeout的单位
     *
     * @return the head of this queue, or {@code null} if the
     *         specified waiting time elapses before an element is available
     * 返回队列头，或者null（就是等了最长等待时间，都没有拿到目标数据的时候）
     *
     * @throws InterruptedException if interrupted while waiting
     * 抛出异常：InterruptedException，如果等待的时候线程被中断
     */
    E poll(long timeout, TimeUnit unit)
            throws InterruptedException;

    /**
     * Returns the number of additional elements that this queue can ideally
     * (in the absence of memory or resource constraints) accept without
     * blocking, or {@code Integer.MAX_VALUE} if there is no intrinsic
     * limit.
     *
     * 返回队列在不阻塞的时候剩余能容纳的元素数量（在没有内存或者资源约束的情况下）。如果没有指定限制，则使用Integer的MAX_VALUE值
     *
     * <p>Note that you <em>cannot</em> always tell if an attempt to insert
     * an element will succeed by inspecting {@code remainingCapacity}
     * because it may be the case that another thread is about to
     * insert or remove an element.
     *
     * 注意：你不能通过检查还多少容量的方式来检查元素的插入是否成功，因为可能会出现一个线程插入，但另外一个线程删除的情况
     *
     * @return the remaining capacity
     * 返回剩余容量
     */
    int remainingCapacity();

    /**
     * Removes a single instance of the specified element from this queue,
     * if it is present.  More formally, removes an element {@code e} such
     * that {@code o.equals(e)}, if this queue contains one or more such
     * elements.
     *
     * 如果目标实例元素存在的话，从这个队列当中移除目标实例元素。更正式的说就是，移除的是 o.equals(e) 的元素e，如果这个队列中有这样的一个或者多个元素的话。
     *
     * Returns {@code true} if this queue contained the specified element
     * (or equivalently, if this queue changed as a result of the call).
     *
     * 返回true，当这个队列当中有目标元素的话。（或者true，如果这个这个队列当做调用结果而更改的话）
     *
     * @param o element to be removed from this queue, if present
     * @return {@code true} if this queue changed as a result of the call
     * @throws ClassCastException if the class of the specified element
     *         is incompatible with this queue
     * (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * 抛出异常：ClassCastException，当传入的元素类型和队列要求的元素不一致的时候
     *
     * @throws NullPointerException if the specified element is null
     * (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * 抛出异常：NullPointerException，当传入的指定元素为null的时候
     */
    boolean remove(Object o);

    /**
     * Returns {@code true} if this queue contains the specified element.
     * More formally, returns {@code true} if and only if this queue contains
     * at least one element {@code e} such that {@code o.equals(e)}.
     *
     * 返回true，当这个队列包含传入的这个特定元素的时候。更正式的说，返回true，当这个队列中存在至少一个符合 o.equals(e) 的元素e.
     *
     * 参数和异常类似remove()
     *
     * @param o object to be checked for containment in this queue
     * @return {@code true} if this queue contains the specified element
     * @throws ClassCastException if the class of the specified element
     *         is incompatible with this queue
     * (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null
     * (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    boolean contains(Object o);

    /**
     * Removes all available elements from this queue and adds them
     * to the given collection.  This operation may be more
     * efficient than repeatedly polling this queue.  A failure
     * encountered while attempting to add elements to
     * collection {@code c} may result in elements being in neither,
     * either or both collections when the associated exception is
     * thrown.  Attempts to drain a queue to itself result in
     * {@code IllegalArgumentException}. Further, the behavior of
     * this operation is undefined if the specified collection is
     * modified while the operation is in progress.
     *
     * 从该队列中移除所有的可用元素，并将它们添加到给定集合当中。这个操作可能会重复轮训这个队列更有效。
     * 当试图向这个集合c中添加元素的时候会失败，并且可能会导致目标元素既不在Collection c当中，也不在老的队列中。
     * 相关联的异常会在两边同时抛出。
     * 尝试去drain一个队列去它自己的话，会导致IllegalArgumentException异常。
     * 更进一步地说，如果在操作过程中修改了指定的集合，则此操作的行为是未定义的（不可预知的）
     *
     * @param c the collection to transfer elements into
     *          集合c，放目标元素的
     * @return the number of elements transferred
     *          返回传输了的元素的数量
     * @throws UnsupportedOperationException if addition of elements
     *         is not supported by the specified collection
     *         抛出异常：UnsupportedOperationException，当目标集合不支持加的这个操作的时候抛出
     *
     * @throws ClassCastException if the class of an element of this queue
     *         prevents it from being added to the specified collection
     *         抛出异常：ClassCastException，当加的元素是目标集合不支持的时候抛出
     *
     * @throws NullPointerException if the specified collection is null
     *         抛出异常：NullPointerException，当传进去的集合是null的时候抛出
     *
     * @throws IllegalArgumentException if the specified collection is this
     *         queue, or some property of an element of this queue prevents
     *         it from being added to the specified collection
     *         如上
     */
    int drainTo(Collection<? super E> c);

    /**
     * Removes at most the given number of available elements from
     * this queue and adds them to the given collection.  A failure
     * encountered while attempting to add elements to
     * collection {@code c} may result in elements being in neither,
     * either or both collections when the associated exception is
     * thrown.  Attempts to drain a queue to itself result in
     * {@code IllegalArgumentException}. Further, the behavior of
     * this operation is undefined if the specified collection is
     * modified while the operation is in progress.
     *
     * 这个方法和上面那个类似，唯一的区别在于这个可以指定移除元素的数量，即该方法用于从该队列移中移除最多给定数量的可用元素到目标集合当中。
     *
     * @param c the collection to transfer elements into
     * @param maxElements the maximum number of elements to transfer
     * @return the number of elements transferred
     * @throws UnsupportedOperationException if addition of elements
     *         is not supported by the specified collection
     * @throws ClassCastException if the class of an element of this queue
     *         prevents it from being added to the specified collection
     * @throws NullPointerException if the specified collection is null
     * @throws IllegalArgumentException if the specified collection is this
     *         queue, or some property of an element of this queue prevents
     *         it from being added to the specified collection
     */
    int drainTo(Collection<? super E> c, int maxElements);
}
