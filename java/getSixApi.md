1. 将一个list<Object> 合并  类似于SQL里的group by。可以将某个字段相同的对象归类

    实例：Map<String,List<T>> result = StreamEx.of(List<T>).groupingBy(T.getName()); 将List<T>集合里name相同的对象聚集到一起。
    one.util.streamex.StreamEx;
```xml
<dependency>
  <groupId>one.util</groupId>
  <artifactId>streamex</artifactId>
  <version>0.6.7</version>
</dependency>    
```
