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