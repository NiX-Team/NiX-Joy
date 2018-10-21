#include<stdio.h>
#include<unistd.h>
#include<sys/types.h>
#include<errno.h>

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