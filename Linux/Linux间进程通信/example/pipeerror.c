#include<stdio.h>
#include<unistd.h>
#include<sys/types.h>

void f();

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
        close(pipe_fd[0]);
        close(pipe_fd[1]);
        sleep(10);
    }
    else
    {
        long flag;
        sleep(1);
        close(pipe_fd[0]);
        sprintf(w_buf, "%s", "hello");
        flag = write(pipe_fd[1], w_buf, 5);
        if(flag == -1)
        {
            printf("error\n");
        }
        else
        {
            printf("parent write over\n");
        }
        close(pipe_fd[1]);
        printf("parent close fd[1]\n");
    }
    return 0;
}