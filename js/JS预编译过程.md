# JS预编译过程
## 1.全局编译过程
* a : 生成GO对象
* b : 将变量声明的名作为GO对象的属性名放入GO对象，初始值为undefined
* c : 在函数体里找函数声明，值赋予函数体  

例:
```javaScript
   console.log(a);
   var a = 3;
   function b(){
   }
   console.log(a);
   var c = function(){
   }
```
编译过程:  
a : 
```javaScript
GO{   
}
```
b :  
```javaScript
GO{  
    a:undefined,  
    b:undefined,  
    c:undefined  
}
```
c :
```javaScript
GO{  
    a:undefined,  
    b:function b(){},  
    c:function(){}  
}
```
执行结果：undefined 3
## 2.函数调用时编译过程
* a : 生成AO对象
* b : 将形参和变量声明的名作为AO对象的属性名放入AO对象，初始值为undefined
* c : 将形参值和实参值相统一
* d : 在函数体里找函数声明，值赋予函数体  

例：
```javaScript
    function test(a,b){
        console.log(a);
        var a = 5;
        function b(){}
        console.log(b);
        var c = function(){}
        function d(){}
    }
    test(1,2);
```
编译过程:  
a :  
```javaScript
    AO{
    }
```
b :  
```javaScript
    AO{
        a:undefined,  
        b:undefined,  
        c:undefined,  
        d:undefined
    }
```
c : 
```javaScript
    AO{
        a:1,  
        b:2,  
        c:undefined,  
        d:undefined
    }
```
d : 
```javaScript
    AO{
        a:1,  
        b:2,  
        c:function(){},  
        d:function d(){}
    }
```
AO预编译的过程跟GO类似，只是多了形参名作为AO属性名，将形参实参值统一这个过程。
## 贡献人员名单

名单按照字母顺序排序。

* [zhangzhimiao](https://github.com/zhangzhimiao)

## CHANGELOG

* v1.0 2018/10/28 无图版