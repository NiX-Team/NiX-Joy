# spring

### spring 架构图

![](http://img.it610.com/image/product/bc5dc3c6bf0940b0af0ec7abcb181837.jpg)

### spring核心架包

* spring-core-版本号.RELEASE.jar
 
它包含Spring框架的核心工具类，Spring其他组件都要用到这个包里的类，是其他组件的核心。

* spring-beans-版本号.RELEASE.jar

所有应用都要用到的，它包含访问配置文件、创建管理bean以及进行ioc或者di操作相关的所有类

* spring-context-版本号.RELEASE.jar

Spring提供在基础ioc功能上的扩展服务，此外还有很多企业级服务的支持，如邮件服务、任务调度、JNDI定位、EJB集成、远程访问、缓存以及各种视图层框架的封装等

* spring-expression-版本号.RELEASE.jar

它定义了spring的表达式语言

### sprig的主要技术

* ioc反转控制

* aop面向切面变成

### spring的优势

* 方便解耦、简化开  
Spring就是一个大工厂，可以将所有的对象创建和依赖关系维护交给Spring

* AOP编程的支持  
Spring 提高面向切面的编程，可以方便的实现对程序进行权限拦截、运行监控等功能

* 声明式事务的支持  
只需要通过配置就可以完成对事物的管理，无需手动编程

* 方便程序的测试  
Spring 对JUnit4支持，可以通过注解方便的测试Spring程序

* 方便集成各种优秀的框架  
Spring 不排斥各种优秀的开源框架，其内部提供了各种优秀的框架（如 Struts2、Hibernate、MyBaits、Quartz等）的直接支持

* 降低JavaEE API的使用难度  
Spring对javaEE开发中非常难用的一些API（JDBC、JavaMail、远程调用等），都提供了一些封装，使API应用难度大大降低。

## 贡献人名单

* [forestMr](https://github.com/forestMr)

## CHANGELOG

* v1.0 2018/11/29