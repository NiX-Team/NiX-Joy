# 线程状态转换图
![image](http://images2015.cnblogs.com/blog/721070/201704/721070-20170421155802696-1378852793.png)

* 调用 obj.wait() 的线程需要先获取 obj 的内置锁（monitor），wait() 会释放 obj 的内置锁并进入 waiting 状态，所以 wait()/notify() 都要与 synchronized 一起使用

## 阻塞与等待的区别
### 阻塞
当一个线程试图获取对象锁（非 java.util.concurrent.Lock，即 synchronized），而该锁被其他线程持有，则该线程进入阻塞状态。

它的特点是使用简单，由 JVM 调度器来决定唤醒自己，不需要由另一个线程来显示唤醒自己，不响应中断

### 等待
当一个线程等待另一个线程通知调度器或某个条件时，该线程进入等待状态。

它的特点是需要等待另一个线程显示地唤醒自己，实现灵活，语义更灵活，可响应中断。

例如调用：

* Object().wait()
* Thread.join()
* 等待 Lock 或者 Condition

# 主要操作
## start()
新启一个线程执行其 run() 方法，一个线程只能 start 一次。主要通过调用 native start0() 来实现

```java
public synchronized void start() {
    // 判断线程是否第一次启动
    if (threadStatus != 0)
        throw new IllegalThreadStateException();

    group.add(this);

    boolean started = false;
    try {
        // 启动线程
        start0();
        started = true;
    } finally {
        try {
            if (!started) {
                group.threadStartFailed(this);
            }
        } catch (Throwable ignore) {
            /* do nothing. If start0 threw a Throwable then
              it will be passed up the call stack */
        }
    }
}

private native void start0();
```

## run()
run() 不需要用户来调用的，当通过 start() 方法启动一个线程之后，当该线程获得了 CPU 执行时间，便进入 run() 方法去执行具体的任务。注意，继承 Thread 类必须重写 run 方法，在 run 中定义具体要执行的任务

## sleep()
sleep() 方法有两个重载版本：

```java
public static native void sleep(long millis) throws InterruptedException;

public static void sleep(long millis, int nanos) throws InterruptedException;
```

sleep() 相当于让线程睡眠，交出 CPU，让 CPU 去执行其他的任务

但是有一点需要注意，sleep() 方法不会释放锁，也就是说如果当前线程持有某个对象的锁，则即使调用了 sleep() 方法，其他线程也无法访问这个对象

## yield()
调用 yield() 方法会让当前线程交出 CPU 权限，让 CPU 去执行其他的线程。它跟 sleep() 方法类似，同样不会释放锁。但是 yield() 不能控制具体的交出 CPU 的时间。

另外，yield() 方法只能让拥有相同优先级的线程有获取 CPU 执行时间的机会

注意，调用 yield() 并不会让线程进入阻塞状态，而是让线程重回就绪状态，它只需要等待重新获取 CPU  时间片，这一点与 sleep() 方法不一样

## join()
join() 方法有三个重载版本：

```java
public final void join() throws InterruptedException;

public final synchronized void join(long millis) throws InterruptedException;

public final synchronized void join(long millis, int nanos) throws InterruptedException
```

join() 实际利用 wait()，只不过不需要等待 notify()/notifyAll()，且不受其影响。

它结束的条件是

1. 等待时间到
2. 目标线程已经 run 完（通过 isAlive() 来判断）

```java
public final synchronized void join(long millis)
    throws InterruptedException {
    long base = System.currentTimeMillis();
    long now = 0;

    if (millis < 0) {   
        throw new IllegalArgumentException("timeout value is negative");
    }

    // 时间为0则需要一直等到目标线程 run 完
    if (millis == 0) {
        while (isAlive()) {
            wait(0);
        }
    } else {
        // 如果目标线程未 run 完且阻塞时间未到，那么调用线程会一直等待
        while (isAlive()) {
            long delay = millis - now;
            if (delay <= 0) {
                break;
            }
            wait(delay);
            now = System.currentTimeMillis() - base;
        }
    }
}
```

调用 jion() 方法的线程会获取 Thread 对象锁，从而可以安全的调用 wait() 方法。有了 wait() 方法，也有对应的 notify()方法，但是这个方法不是在 java 代码中调用，而是在 JVM 中去调用

```c
//一个c++函数：
void JavaThread::exit(bool destroy_vm, ExitType exit_type) ；

//这家伙是啥，就是一个线程执行完毕之后，jvm会做的事，做清理啊收尾工作，
//里面有一个贼不起眼的一行代码，眼神不好还看不到的呢，就是这个：

ensure_join(this);

//翻译成中文叫 确保_join(这个)；代码如下：

static void ensure_join(JavaThread* thread) {
    Handle threadObj(thread, thread->threadObj());
    
    ObjectLocker lock(threadObj, thread);
    
    thread->clear_pending_exception();
    
    java_lang_Thread::set_thread_status(threadObj(), java_lang_Thread::TERMINATED);
    
    java_lang_Thread::set_thread(threadObj(), NULL);
    
    //thread就是当前线程，是啥是啥？就是刚才说的b线程啊。
    lock.notify_all(thread);
    
    thread->clear_pending_exception();
}
```

## interrupt()
此操作会中断等待中的线程，并将线程的中断标志位。如果线程在运行态则不会受此影响

可以通过以下三种方式来判断中断：

1. isInterrupted()：只读取线程的中断标志位，并不会重置
2. interrupted()：读取并重置线程的中断标记位
3. throw InterruptException：抛出该异常的同时，会重置中断标记位

## suspend()/resume()
suspend() 用于挂起线程，直到被 resume()，才会苏醒

但调用 suspend() 的线程和调用 resume() 的线程，可能会因为争锁的问题而发生死锁，所以不推荐使用
