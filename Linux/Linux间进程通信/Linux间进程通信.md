# Linux进程间通信

## 前言

### 进程的概念

1. 进程的定义
    * 进程是程序的一次执行
    * 进程是一个程序及其数据在处理机上顺序执行时所发生的活动
    * 进程是具有独立功能的程序在一个数据集合上运行的过程，它是系统进行资源分配和调度的一个独立单位
    * **进程不等于程序**
2. 进程的基本状态
    * 就绪状态
    * 执行状态
    * 阻塞状态

### 进程通信的概念

* 进程用户空间是独立的，一般而言是不能相互访问的。但很多情况下进程间需要互相通信，来完成系统的某项功能。进程通过与内核及其他进程之间的互相通信来协调它们的行为。

### 进程通信的应用场景

* 数据传输：一个进程需要将它的数据发送给另一个进程，发送的数据量在一个字节到几兆字节之间。
* 共享数据：多个进程想要操作共享数据，一个进程对共享数据的修改，别的进程应该立即看到
* 通知事件：一个进程需要向另一个或一组进程发送消息，通知它（它们）发生了某种事件（如进程终止时需要通知父进程）
* 进程控制：有些进程希望完全控制另外一个进程的执行，此时控制进程希望能够拦截另一个进程的所有的异常并能够及时知道它的状态的改变

### 进程通信的方式

1. 管道
    * 普通管道：用于具有亲缘关系进程间的通信，单工通信
    * 有名管道：可在许多不相关的进程间进行通信

### 管道

1. 管道相关的关键概念
    * 管道是半双工的，数据只能向一个方向流动；需要双方通信时，需要建立起两个管道
    * 只能用于父子进程或者兄弟进程之间（具有亲缘关系的进程）
    * 单独构成一种独立的文件系统：管道对于管道两端的进程而言，就是一个文件，但它不是普通的文件，它不属于某种文件系统，而是自立门户，单独构成一种文件系统，并且只存在与内存中。
    * 数据的读出和写入：一个进程向管道中写的内容被管道另一端的进程读出。写入的内容每次都添加在管道缓冲区的末尾，并且每次都是从缓冲区的头部读出数据。
1. 管道的创建

    ```c
    #include<unistd.h>
    int pipe(int fd[2])
    ```

    * 该函数创建的管道的两端处于一个进程中间，在实际应用中没有太大意义，因此，一个进程在由pipe()创建管道后，一般再fork一个子进程，然后通过管道实现父子进程间的通信。

1. 管道的读写规则

    * 管道两端可分别用描述字fd[0]以及fd[1]来描述，需要注意的是，管道的两端是固定了任务的。即一端只能用于读，由描述字fd[0]表示，称其为管道读端；另一端则只能用于写，由描述字fd[1]来表示，称其为管道写端。如果试图从管道写端读取数据，或者向管道读端写入数据都将导致错误发生。

    ```c
    #include<stdio.h>
    #include<unistd.h>
    #include<sys/types.h>

    int main()
    {
        int pipe_fd[2];
        pid_t pid;
        char r_buf[100];
        char w_buf[100];

        if(pipe(pipe_fd) < 0) // 创建管道
        {
            printf("pipe create error\n");
        }

        pid = fork(); // 创建子进程
        if(pid == -1)
        {
            printf("fork error\n");
        }
        else if(pid == 0)
        {
            close(pipe_fd[1]);
            read(pipe_fd[0], r_buf, 100);
            printf("the data from the pipe is %s\n", r_buf);
        }
        else
        {
            close(pipe_fd[0]);
            sprintf(w_buf, "%s", "hello");
            if(write(pipe_fd[1], w_buf, 5) != -1)
            {
                printf("parent write over\n");
            }
            close(pipe_fd[1]);
            printf("parent close fd[1]\n");
        }

        return 0;
    }
    ```

    * 如果管道的写端不存在，则认为已经读到了数据的末尾，读函数返回的读出字节数为0
    * 当管道的写端存在时，如果请求的字节数目大于PIPE_BUF，则返回管道中现有的数据字节数，如果请求的字节数目不大于PIPE_BUF，则返回管道中现有数据字节数（此时，管道中数据量小于请求的数据量）；或者返回请求的字节数（此时，管道中数据量不小于请求的数据量）。
    * 向管道中写入数据时，linux将不保证写入的原子性，管道缓冲区一有空闲区域，写进程就会试图向管道写入数据。如果读进程不读走管道缓冲区中的数据，那么写操作将一直阻塞。
    * 只有在管道的读端存在时，向管道中写入数据才有意义。否则，向管道中写入数据的进程将收到内核传来的SIFPIPE信号，应用程序可以处理该信号，也可以忽略（默认动作则是应用程序终止）。

    ```c
    // 子进程
    close(pipe_fd[0]);
    close(pipe_fd[1]);
    sleep(5);
    ```

    ```c
    // 父进程
    sleep(1);
    close(pipe_fd[0]);
    write(pipe_fd[1], w_buf, strlen(w_buf));
    ```

### 有名管道

* 有名管道相关概念

    *管道应用的一个重大限制是它没有名字，因此，只能用于具有亲缘关系的进程间通信，在有名管道（named pipe或FIFO）提出后，该限制得到了克服。FIFO不同于管道之处在于它提供一个路径名与之关联，以FIFO的文件形式存在于文件系统中。这样，即使与FIFO的创建进程不存在亲缘关系的进程，只要可以访问该路径，就能够彼此通过FIFO相互通信（能够访问该路径的进程以及FIFO的创建进程之间），因此，通过FIFO不相关的进程也能交换数据。值得注意的是，FIFO严格遵循先进先出（first in first out），对管道及FIFO的读总是从开始处返回数据，对它们的写则把数据添加到末尾。它们不支持诸如lseek()等文件定位操作。

* 有名管道的创建

    ```c
    #include<sys/types.h>
    #include<sys/stat.h>
    int mkfifo(const char* pathname, mode_t mode)
    ```

    该函数的第一个参数是一个普通的路径名，也就是创建后FIFO的名字。第二个参数与打开普通文件的open()函数中的mode 参数相同。如果mkfifo的第一个参数是一个已经存在的路径名时，会返回EEXIST错误，所以一般典型的调用代码首先会检查是否返回该错误，如果确实返回该错误，那么只要调用打开FIFO的函数就可以了。一般文件的I/O函数都可以用于FIFO，如close、read、write等等。

* 有名管道的读写规则

    **从FIFO中读取数据：**

    * 约定：如果一个进程为了从FIFO中读取数据而阻塞打开FIFO，那么称该进程内的读操作为设置了阻塞标志的读操作。

    * 如果有进程写打开FIFO，且当前FIFO内没有数据，则对于设置了阻塞标志的读操作来说，将一直阻塞。对于没有设置阻塞标志读操作来说则返回-1，当前errno值为EAGAIN，提醒以后再试。
    对于设置了阻塞标志的读操作说，造成阻塞的原因有两种：当前FIFO内有数据，但有其它进程在读这些数据；另外就是FIFO内没有数据。解阻塞的原因则是FIFO中有新的数据写入，不论信写入数据量的大小，也不论读操作请求多少数据量。

    * 读打开的阻塞标志只对本进程第一个读操作施加作用，如果本进程内有多个读操作序列，则在第一个读操作被唤醒并完成读操作后，其它将要执行的读操作将不再阻塞，即使在执行读操作时，FIFO中没有数据也一样（此时，读操作返回0）。

    * 如果没有进程写打开FIFO，则设置了阻塞标志的读操作会阻塞。

    * **注：如果FIFO中有数据，则设置了阻塞标志的读操作不会因为FIFO中的字节数小于请求读的字节数而阻塞，此时，读操作会返回FIFO中现有的数据量。**

    向FIFO中写入数据：

    * 约定：如果一个进程为了向FIFO中写入数据而阻塞打开FIFO，那么称该进程内的写操作为设置了阻塞标志的写操作。

        对于设置了阻塞标志的写操作：

    * 当要写入的数据量不大于PIPE_BUF时，linux将保证写入的原子性。如果此时管道空闲缓冲区不足以容纳要写入的字节数，则进入睡眠，直到当缓冲区中能够容纳要写入的字节数时，才开始进行一次性写操作。

    * 当要写入的数据量大于PIPE_BUF时，linux将不再保证写入的原子性。FIFO缓冲区一有空闲区域，写进程就会试图向管道写入数据，写操作在写完所有请求写的数据后返回。

        对于没有设置阻塞标志的写操作：

    * 当要写入的数据量大于PIPE_BUF时，linux将不再保证写入的原子性。在写满所有FIFO空闲缓冲区后，写操作返回。

    * 当要写入的数据量不大于PIPE_BUF时，linux将保证写入的原子性。如果当前FIFO空闲缓冲区能够容纳请求写入的字节数，写完后成功返回；如果当前FIFO空闲缓冲区不能够容纳请求写入的字节数，则返回EAGAIN错误，提醒以后再写；

### 示例代码

读取数据

```c
#include <stdio.h>  
#include <stdlib.h>  
#include <string.h>  
#include <fcntl.h>  
#include <unistd.h>
#include <sys/types.h>  
#include <sys/stat.h>  
  
#define FIFO_NAME "test.txt"  
#define BUFFER_SIZE 100 
  
int main()
{
    int pipe_fd;
    int res;

    int open_mode = O_RDONLY;
    char buffer[BUFFER_SIZE + 1];
    int bytes = 0;

    printf("Process %d opeining FIFO O_RDONLY\n", getpid());
    pipe_fd = open(FIFO_NAME, open_mode);
    printf("Process %d result %d\n", getpid(), pipe_fd);

    if (pipe_fd != -1)
    {
        do{
            res = read(pipe_fd, buffer, BUFFER_SIZE);
            bytes += res;
        }while(res > 0);
        close(pipe_fd);
    }
    else
    {
        exit(EXIT_FAILURE);
    }
    printf("Process %d finished, %d bytes read\n", getpid(), bytes);
    exit(EXIT_SUCCESS);
}
```

写入数据

```c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
  
#define FIFO_NAME "test.txt"
  
int main()
{
    int pipe_fd;
    int res;
    int open_mode = O_WRONLY;
  
    int bytes = 0;
    char buffer[100];
  
    if (access(FIFO_NAME, F_OK) == -1)
    {
        res = mkfifo(FIFO_NAME, 0777);
        if (res != 0)
        {
            printf("Could not create fifo\n");
            exit(1);
        }
    }

    printf("Process %d opening FIFO O_WRONLY\n", getpid());
    pipe_fd = open(FIFO_NAME, open_mode);
    printf("Process %d result %d\n", getpid(), pipe_fd);

    sprintf(buffer, "%s", "a;skdf;as;dkjfak");
    if (pipe_fd != -1)
    {
        res = write(pipe_fd, buffer, strlen(buffer));
        if (res == -1)
        {
            printf("Write error on pipe\n");
            exit(1);
        }
        bytes = res;
        printf("%d\n",bytes);
        close(pipe_fd);
    }
    else
    {
        exit(1);
    }
    printf("Process %d finish\n", getpid());
    exit(0);
}
```

## 贡献人员名单

名字按字母顺序排序

* [coolzhang666](https://github.com/coolzhang666)

## CHANGELOG

* v1.0 2018/10/21 初版