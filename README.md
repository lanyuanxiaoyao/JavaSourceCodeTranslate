# JavaSourceCodeTranslate
Java源码阅读计划

**不管你是通过什么途径来到这个项目, 你都需要仔细阅读一下我对这个项目的一些想法**

# Q&A

**Q** 为什么会有这个项目  
**A** 在学习编程的过程中, 我们时常会去网上找到别人的博客和文章, 看别人的解析总结和教学, 可我们真的学到了这份知识了吗?  
事实上, 很多时候由于网文作者知识功底和文笔的原因, 一些网络文章总结出来的内容不一定非常准确和全面, 甚至是有遗漏的, 也有可能是因为侧重点的不同, 一些文章有选择地省略了一部分的内容, 再加上我们熟知的"复制黏贴一把梭", 作为读者的我们, 在这样的文章之上进行学习, 难免会建起空中楼阁. 如何解决学习二手知识带来的隐患, 方法几乎是只有一个, 那就是找到文章的本源, 也就是源码.  
比如, 我们都知道`ArrayList`是一个基于数组的集合类, 然后呢? 然后就没有然后了, 关于其原理与实现的思路, 或者说一些实现的关键点, 我们几乎是抓瞎的, 勉强说出一两个, 但也不成体系, 这样的知识, 无限接近于没有.  
通过阅读优秀的源码可以提升自己的代码能力, 关于这一点毋庸置疑, 但是我们也都知道, 阅读源码是一件无比枯燥的事情, 而生活中还有很多诱惑, 所以我们必须要找到一个借口来让我们可以静下心来踏踏实实地一行一行把源码啃完, 于是**翻译注释**就成为了阅读源码的借口.  
Java的源码是世界上公认的最优秀的源码之一, 其中关于各种数据类型以及算法的实现, 都是教科书级别的, 至于为什么是教科书级别的呢? 因为Java里有一些算法的实现, 干脆就是提出算法本身的创作者写的, 还有什么比这更高级别的学习资料呢? Java经过了多年的迭代, 其中数据结构与算法的实现都经过了时间的考验, 稳健无比, 一些设计模式的使用更是炉火纯青, 所以从Java源码能学到的东西, 绝对不止一星半点. 其次, Java源码的注释真的是详细到令人赞叹, 一些算法或者数据结构的要点, 十分直白地写在了类的开头, 面对如此良心的"教程", 我们怎么忍心拒绝, 所以直接阅读Java源码获得的知识, 远比在网上搜索一些博文来得要更准确.  
所以这个项目就是一个以翻译注释为借口的源码阅读计划.  
**纸上得来终觉浅, 绝知此事要躬行.**  

**Q** 为什么只有Java的集合类  
**A** 没有人可以否认Java的体系实在是太庞大了, 随便拿出一个包, 都可以出一本书了, 所以秉承着贪多嚼不烂的原则, 这个项目决定从一点一滴开始做起, 为什么将集合类作为一个开端, 主要是集合是我们平常代码里用得最多的数据结构, `ArrayList`, `HashMap`都是已经被用到烂的数据结构, 所以从最熟悉的部分入手, 会比一些不常用的类来得要实用一些, 当然还有一个原因就是如果你要参加面试, 集合类几乎是必问的问题类型了, 这也从侧面说明了集合类的重要性, 所以项目就从集合类开始了.  
当然也为了避免一些干扰, 我也把util包下一些无关集合的类都删掉了, 当集合类的阅读计划已经完成(能做完这个就已经很厉害了额...), 或者觉得可以开始下一阶段的时候, 再把下一阶段的源码补充进来.

**Q** 这个项目的进度怎么把控  
**A** 没有把控, 翻译只是一个借口, 我并没有想要推出一份Java中文API, 尽管这么做听起来很酷, 但这个"翻译"项目的最终目的只是帮助我们阅读源码来提高自己, 提高自己需要花多少时间, 谁也说不好, 所以也就不存在进度, 再说了, 本来就没有要输出什么明确的产品, 而且最重要最宝贵的财富是阅读源码的过程, 这个东西远远不是直接拿到一份现成的翻译成果可以相比的.  
另外, 阅读源码是一件非常繁琐而且枯燥的事情, **绝对不是短时间内可以完成的**, 所以早一天或者晚一天并不会产生质变, **重要的是坚持**, 每天只看几个方法, 一周也能有非常可观的积累, 有人说那总得有个安排吧, 我想说学习是自己的事情, 请自己把控.  

**Q** 既然自己阅读源码效果这么好, 为什么还要与人合作  
**A** 不可否认, 如果可以一个人通读Java的全部源码并且有自己的理解, 那当然是最好,但人的精力是有限的, 将自己所有的时间都放在这件事上并不现实, 而我们的知识也不知道什么时候会被用上, 比如突如其来的面试? 所以适当地提高效率是非常有益的事情, 合作就成了不二选择.  

**Q** 我该如何进行合作
**A** 上面解释了合作的原因, 但是我们最初说过, 这个项目是拒绝二手知识的, 这岂不是有了冲突?  
**鱼和熊掌不可兼得**, 效率与初衷产生了冲突是很常见的事情, 对于这个问题, 我觉得解决的方式出在合作的方式上, 一个合理的合作方式可以有效地平衡自己读源码和别人读源码之间的关系. 这个合理的合作方式就是: 将review别人的翻译当做第一要事. 如果我们只关心自己翻译的注释和看过的代码, 那么合作将毫无意义, 提高效率的前提就是在同样的时间里面, 你可以阅读到更多的源码, 通过翻译打破英语注释带来的隔阂, 帮助自己更快地了解代码的流程和逻辑, 这也是学习的一种, 而且在这个项目中有一个天然的优势, 那就是翻译注释在一定程度上摒除了个人的见解, 所以即使我们读的是别人翻译后的注释, 也不会影响我们对原意的把握, 这也是对二手知识隐患的一种处理方式吧.  
总得来说, 我们是通过阅读源码的注释来学习源码, 翻译源码是促进自己对源码理解的一种手段, 任何时候都要将学习放在首位, 而不是速度, 当然, 速度快效率高肯定是不会被嫌弃的.  

**Q** 我该如何加入到项目里
**A** 抱歉, 我并不打算随意地加入更多的合作者, 理由有几个, 一是时间有限, 精力有限, 按照目前的规模, 如果每个人翻译一个类的话, 那么在一段不短的时间里面, 我将需要同时跟进三个类的源码, 这对我来说是否是一个负担还是未知数, 所以目前也处于试水的阶段, 除了我以外的另外两个人, 是我在生活中的业内好友, 我对他们的责任心和技术能力都有非常强的信心, 我认为他们可以胜任这个项目想要完成的事, 不会轻易中断自己的学习, 话说到这里了, 也希望看到这里的你们两个人可以好好坚持下去, (滑稽.jpg).  
另一个原因是这个项目的模式更像一个学习小组, 如果小组里的人互相认识, 那当然可以更方便地交流和合作, 因为阅读源码这个事, 总得自己亲自动手才有作用. 所以如果你不认识我的话, 我更建议你fork这个项目, 或者直接自己搭一个仓库, 将自己的小伙伴带入其中, 行成一个新的学习小组, 也不再局限于Java源码, 还可以是Spring源码, C++源码之类的.

# 翻译
这里将尽可能地描述如何开展翻译源码注释的工作, 这是我自己总结的一些工作步骤, 旨在提高学习的效率, 统一工作的模式和风格.

1. 大段的多行注释, 在一整段注释的下方另起一段进行翻译
```java
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
transient Object[] elementData;
```
2. 翻译的排版尽可能与源码注释相匹配
3. 较短的一句话描述, 或是一个单词短语的注释, 可以在行末空格后直接补充
```java
/**
  * Constructs an empty list with the specified initial capacity.
  *
  * 构造一个指定的容量的空列表
  *
  * @param  initialCapacity  the initial capacity of the list 列表初始化的容量
  * @throws IllegalArgumentException if the specified initial capacity
  *         is negative 如果指定的容量是一个非法的值
  */
```
4. 代码内的双斜杠注释, 可以选择行末空格后直接补充, 如果遇到一段话分开两行的, 在整段话后使用原排版进行补充
```java
final int expectedModCount = modCount;
// ArrayList can be subclassed and given arbitrary behavior, but we can
// still deal with the common case where o is ArrayList precisely
// ArrayList可以被继承并随意增加行为, 但通常情况下, 如果o是ArrayList的话我们
// 仍然可以处理通用的部分.
boolean equal = (o.getClass() == ArrayList.class)
    ? equalsArrayList((ArrayList<?>) o)
    : equalsRange((List<?>) o, 0, size);
```
5. 对于觉得需要补充描述的大段注释, 在翻译后, 使用括号将主动补充的内容标记出来
```java
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
```