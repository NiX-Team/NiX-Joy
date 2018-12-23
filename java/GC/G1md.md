# <center>**java垃圾回收机制——G1**<center>#
## 一：如何判断对象是否存活
* **引用计数算法（Recference Counting）**<br>
	* **算法基本思路:**给对象添加一个引用计数器，每当有一个地方引用它，计数器加1；当引用失效，计数器值减1；任何时刻计数器值为0，则认为对象是不再被使用的； <br>
	* **缺点**：很难解决对象之间相互循环引用的问题。
## 
	ReferenceCountingGC objA = new ReferenceCountingGC();
	ReferenceCountingGC objB = new ReferenceCountingGC();
	objA.instance = objB;
	objB.instance = objA;
	objA = null;
	objB = null;
 
* **可达性分析算法（Reachability Analysis）**<br>
	* **算法基本思路：**通过一系列"GC Roots"对象作为起始点，开始向下搜索；搜索所走过和路径称为引用链（Reference Chain）；当一个对象到GC Roots没有任何引用链相连时（从GC Roots到这个对象不可达），则证明该对象是不可用的；
	* **缺点：**分析过程需要GC停顿（引用关系不能发生变化），即停顿所有Java执行线程；
![](D:\GC\GC_root.jpg)
##
	1、对象引用在JVM层次以及在java语言层面的引用
	2、虚拟机中对象可达性分析的实现

## 二：如何清理垃圾
* **标记-清除算法（Mark-Sweep）**：<br>
	采用可达性分析，对垃圾进行标记，然后清除
* **复制算法:(copy)**<br>
	把内存划分为大小相等的两块，每次只使用其中一块；当一块内存用完了，就将还存活的对象复制到另一块上（而后使用这一块）；再把已使用过的那块内存空间一次清理掉，而后重复；  
* **标记-整理算法:(Mark-Compact)**<br>
	同标记-清除算法，首先对垃圾进行标记，但后续不是直接对可回收对象进行清理，而是让所有存活的对象都向一端移动；然后直接清理掉端边界以外的内存；
* **分代收集算法：（Generational Collection）**<br>
	只是根据对象存活周期的不同将内存划分为几块；这样就可以根据各个年代的特点采用最适当的收集算法；一般把Java堆分为新生代和老年代；<br>
		* **新生代:**每次垃圾收集都有大批对象死去，只有少量存活；所以可采用复制算法；<br>
		* **老年代:**对象存活率高，没有额外的空间可以分配担保；使用"标记-清理"或"标记-整理"算法；
		
## 三：垃圾收集器：

* **并行垃圾收集器与并发垃圾收集器**
	* **并行（Parallel）** 指多条垃圾收集线程并行工作，但此时用户线程仍然处于等待状态；<br><br>如ParNew、Parallel Scavenge、Parallel Old；

	* **并发（Concurrent）**指用户线程与垃圾收集线程同时执行（但不一定是并行的，可能会交替执行）；用户程序在继续运行，而垃圾收集程序线程运行于另一个CPU上；<br><br>如CMS、G1（也有并行）；

* **java垃圾收集器的演变：**
	* **1、Serial/Serial Old：**　Serial/Serial Old收集器是最基本最古老的收集器，它是一个单线程收集器，并且在它进行垃圾收集时，必须暂停所有用户线程。Serial收集器是针对新生代的收集器，采用的是Copying算法，Serial Old收集器是针对老年代的收集器，采用的是Mark-Compact算法。它的优点是实现简单高效，但是缺点是会给用户带来停顿。<br>[JDK1.3.1]
![](D:\GC\S_old.jpg)
	* **2、ParNew：**		ParNew收集器是Serial收集器的多线程版本，使用多个线程进行垃圾收集。
![](D:\GC\并行.jpg)
	* **3、Parallel Scavenge**：	Parallel Scavenge收集器是一个新生代的多线程收集器（并行收集器），它在回收期间不需要暂停其他用户线程，其采用的是Copying算法，该收集器与前两个收集器有所不同，它主要是为了达到一个可控的吞吐量。
		* 吞吐量=运行用户代码时间/（运行用户代码时间+垃圾收集时间）；
	* **4、Parallel Old**：	Parallel Old是Parallel Scavenge收集器的老年代版本（并行收集器），使用多线程和Mark-Compact算法。
	* **5、CMS：**	CMS（Current Mark Sweep）收集器是一种以获取最短回收停顿时间为目标的收集器，它是一种并发收集器，采用的是Mark-Sweep算法。<br>[JDK1.5]
![](D:\GC\并发标记清除.jpg)
	* **G1：**	G1收集器是当今收集器技术发展最前沿的成果，它是一款面向服务端应用的收集器，它能充分利用多CPU、多核环境。因此它是一款并行与并发收集器，并且它能建立可预测的停顿时间模型。<br>[JDK1.7]<br>
![](D:\GC\G1.jpg)

* **Minor GC和Full GC的区别**
	* **Minor GC：**又称新生代GC，指发生在新生代的垃圾收集动作；因为Java对象大多是朝生夕灭，所以Minor GC非常频繁，一般回收速度也比较快；
	* **Full GC：**又称Major GC或老年代GC，指发生在老年代的GC；出现Full GC经常会伴随至少一次的Minor GC，Major GC速度一般比Minor GC慢10倍以上；

## 四、G1垃圾收集器
**2、堆内存被划分为多个大小相等的内存块（Region），每个Region是逻辑连续的一段内存，结构如下：**
![](D:\GC\G1分区.png)<br>
Region大小区间只能是1M、2M、4M、8M、16M和32M,即2的方幂。<br><br>
* **eden space:**对象创建后将首先保存在此区域，此空间中将会对数据进行循环遍历，判断是否为垃圾，若成为垃圾，将其标记。当该区域数据内存达到阈值，例如80%总内存，便启用minor gc。将会对其中的数据进行判断,如过内存没有被标记，且存活了足够多的循环。该对象就会被保存到老生代中。其余存活对象将会放置在survivor区域中。<br>
	*例如：添加命令-XX:MaxTenuringThreshold=5表示当生存次数达到了5便自动进入老生代 <br><br><br>
* **survivor:**保存由eden中存活的对象，当一次minor gc完成后，原eden space被释放，变为没有被使用过的region。同时该survivor区域变为eden space。重复循环<br><br><br>
* **old region:**保存存活时间久的对象，当越来越多的对象晋升到老年代old region时，其中使用的内存达到一个自己设置的阈值，启动mixed gc,回收部分的老生代和一个eden space。如果对象内存分配速度过快，mixed gc来不及回收，导致老年代被填满，就会触发一次full gc，G1的full gc算法就是单线程执行的serial old gc，会导致异常长时间的暂停时间，需要进行不断的调优，尽可能的避免full gc.<br><br><br>
* **Humongous region:**存放大对象的区域，比如达到一个region内存的50%【可以自己设置】的就叫大对象。该区域可以包括几个连续的region


## 贡献人员名单
* gongquan
