## Makefile的使用

* Makefile 定义：是用于项目构建的一个文件。
* Make是一个用于项目构建的程序，通过在目录中找到makefile文件来构建，vs工具中使用的是nmake。

		make 的一个简单示例
		main.out:class1.o class2.o
			gcc -o main.o main.c
		class1.o:class1.c
			gcc -o class1.o class1.c
		class2.o:class2.c class3.o
			gcc -o class2.o class2.c
		class3.o:class3.c
			gcc -o class3.o class3.c
* make构造的原理：根据时间戳来构造，被依赖的文件如果更新修改时间晚于目标，则重新构建。 make clean  

		make中的伪目标
		clean:
			rm -fr *.o
* make中使用变量：
		
		aim:=*.c
		result:
			gcc -c $(aim)
		
## linux内核的部分内容
* linux系统启动过程：内核引导，运行init，系统的初始化，建立终端，用户登录系统。
* 核心：在linux系统中一切皆是文件,通过shell（命令解释器）来对各种文件操作。
* 下面是一个简单的shell命令解释器  [链接地址](https://mp.weixin.qq.com/s/0a_T8UKFNiaQueYrUs1TCw)
* [学习资源地址](https://github.com/ljrkernel/linuxmooc)

		#include <stdio.h>
		#include <stdlib.h>
		#include <unistd.h>
		#include <sys/wait.h>
		
		#define CMD_BUF_LEN	512
		char cmd[CMD_BUF_LEN] = {0};
		
		void fork_and_exec(char *cmd, int pin, int pout)
		{
			if (fork() == 0) {
				if (pin != -1) {
					dup2 (pin, 0);
					close(pin);
				}
				if (pout != -1) {
					dup2 (pout, 1);
					close(pout);
				}
				作用：执行当前操作系统的指令
				system(cmd);
				exit(0);
			}
			if (pin != -1)
				close(pin);
			if (pout != -1)
				close(pout);
		}

		int execute_cmd(char *cmd, int in)
		{
			int status;
			char *p = cmd;
			int pipefd[2];
		
			while (*p) {
				switch (*p) {
				case '|':
					*p++ = 0;
					pipe(pipefd);
					fork_and_exec(cmd, in, pipefd[1]);
					execute_cmd(p, pipefd[0]);//这里是核心代码，递归创建子进程
					return 0;
				default:
					p++;
				}
			}
			fork_and_exec(cmd, in, -1);
			while(waitpid(-1, &status, WNOHANG) != -1);
			return 0;
		}

		int main(int argc, char **argv)
		{
			while (1) {
				printf("tiny sh>>");
				gets(cmd);
				if (!strcmp(cmd, "q")) {
					exit(0);
				} else {
					execute_cmd(cmd, -1);
				}
			}
			return 0;
		}


## Qt的使用（快速入门）
	* Qt是一个跨平台的C++的图形用户界面的应用程序开发框架，可以实现GUI编程，也可以用于服务器开发，使用特殊代码生成扩展以及一些宏。
	  并支持组件化编程，实现了C++标准库的大部分内容，无论是Web开发，移动端，或是嵌入式都可以作为一个灵活的工具。
	* 虽然是使用C++的编写的，但是语法除了基础的语法与C++相同外，内容已经是完全超越了C++，而且只要有了面向对象的基础都可以快速掌握。
	* 从使用的角度上，更像是跨平台的C#。
* 开发环境搭建(Qt4), [资源链接](https://pan.baidu.com/s/1PlM3TpIt6oiz-zG5xHMIiw&shfl=sharepset)
* [学习链接](http://www.qter.org/)
