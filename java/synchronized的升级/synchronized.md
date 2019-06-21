# 对象的存储
对象头、实例数据、对齐填充

## 对象头结构
* 对象标记（markOop _mark也就我们后面提到的Mark word）
* 元数据

## 虚拟机对象头源码
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181218213746309.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNjM3MDYx,size_16,color_FFFFFF,t_70)

## 虚拟机markOop部分源码
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181218213451349.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNjM3MDYx,size_16,color_FFFFFF,t_70)

## Mark Word的内容
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181218214446781.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181218214514767.png)

## 锁的类型
* 无锁
* 偏向锁
* 轻量级锁
* 重量级锁

## 无锁
当使用了同步块但是没有线程去访问对象头锁状态为无锁。

## 如何获取偏向锁
1. 获取目标对象的对象头
2. 根据锁标识进行判断
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190105122829734.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNjM3MDYx,size_16,color_FFFFFF,t_70)
CAS操作是为了保证线程安全

## 如何获取轻量级锁
1. 在当前线程的栈帧里面创建一个存储LockRecord的空间
2. 对象头中的Mark Word复制到锁记录，Displaced Mark Word
3. CAS,将线程栈帧中锁记录地址CAS到对象头  

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190105130924130.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNjM3MDYx,size_16,color_FFFFFF,t_70)

## 如何获取重量级锁
当竞争激烈程度达到某种水平，对象头锁的状态变成重量级锁。在轻量级锁中频繁的cas操作会消耗很多cpu,当升级为重量级锁后只需要让没有获得锁的线程挂起然后重新竞争。

## 贡献人员名单

名单按照字母顺序排序。

* [forestMr](https://github.com/forestMr)

## CHANGELOG

* v1.0 2019/01/05 