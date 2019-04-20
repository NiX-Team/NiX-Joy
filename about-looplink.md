# 题目：如何判断一个单链表是有环

```
typedef struct Node{
  int value;
  struct Node *next;
}Node;

int exitLoop(const Node *head)
{
    Node *fast=head,*low=head;
    if(low==NULL) return 0;
    while(low!=NULL&&fast->next!=NULL)
    {
        slow=slow->next;
        fast=fast->next->next;
        if(slow==fast)
            return 1;
    }
}
```

# 题目：找出单链表中环的入口点
```
设slow指针走了s步，fast走了2s步，r为环的长度，a为头指针到环入口的距离,x为环入口到slow指针和fast指针相遇的位置。

1：fast走的步数=s+n*r步,
故2s=s+n*r;故s=n*r;

2：a+x=s=n*r，则a+x=(n-1)*r+(L-a);
故a=(n-1)*r+(L-a-x)
```
**结论：从链表起点head开始到入口点的距离a，与从相遇点到入口点的距离相等**  
```
Node* findLoopStart(Node *head)
{
  Node *fast,*slow;
  slow=fast=head;
  while(slow!=NULL&&fast->next!=NULL)
  {
      slow=slow->next;
      fast=fast->next->next;
      if(slow==fast) break;
  }
  if(slow==NULL||fast->next==NULL) return NULL;
  
  Node *ptr1=head;
  Node *ptr2=slow;
  while(ptr1!=ptr2)
  {
      ptr1=ptr1->next;
      ptr2=ptr2->next;
  }
  return ptr1;
}
```
# 题目：得出单链表环的长度
## 思路1：slow指针和fast指针的相遇节点存入临时变量temp中，然后继续slow=slow->next,当slow=temp时，经过的步数就是环的长度。
## 思路2：slow指针和fast指针第一次相遇后，继续slow=slow->next;fast=fast->next->next;当第二次相遇时，经过步数就是环的长度。

# 题目：求出环的长度
## Num-2和Num-3已经将起点到入口的长度和环的长度求出，直接相加即可

思路：设置两个指针point1和point2。初始化分别指向**第一个**和**第二个**元素。之后进行
```
point1=point1->next->next;
point2=point2->next->next;
```
注意：或point2->next->next==NULL时，指针应放慢速度,即point2=point2->next;

```
int exitElem(Node *head,Node *e)
{
  Node *point1,*point2;
  point1=head,point2=head->next;
  while(1)
  {
      if(equals(e,point1)||equals(e,point2))
      {
          return 1;
      }
      else
      {
          if(point2->next->next!=NULL)
              point2=point2->next->next;
          else
          {
              if(equals(e,point2))
                  return 1;
              else
                  return 0;
          }
          point1=point1->next->next;
      }
  }
}
```
