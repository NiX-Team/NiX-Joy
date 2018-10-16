# Servlet重定向问题

**两种转发对象的方式** 
 
<strong>
  1.通过HttpServletRequest.getRequestDispatcher();

  2.通过HttpServletResponse.sendRedriect();
</strong>  
  
Servlet中有两种方式获得转发对象(RequestDispatcher)：一种是通过HttpServletRequest的getRequestDispatcher()方法获得，一种是通过ServletContext的getRequestDispatcher()方法获得；

## 重定向的方法只有一种：HttpServletResponse的sendRedirect()方法。

这三个方法的参数都是一个URL形式的字符串，但在使用相对路径或绝对路径上有所区别。

★ HttpServletResponse.sendRedirect(String)

参数可以指定为相对路径、绝对路径或其它Web应用(相对路径前不加'/'，绝对路径前加'/')。

假设通过http://localhost/myApp/cool/bar.do请求到达该方法所属的Servlet。

相对路径：response.sendRedirect("foo/stuff.do")

容器相对于原来请求URL的目录加参数来生成完整的URL——http://localhost/myApp/cool/foo/stuff.do。

绝对路径：response.sendRedirect("/foo/stuff.do")

容器相对于Web应用本身加参数建立完整的URL——http://localhost/foo/stuff.do。

其它Web应用：response.sendRedirect("http://www.iteye.com")

容器直接定向到该URL。

 

★ HttpServletRequest.getRequestDispatcher(String)

参数可以指定为相对路径或绝对路径。

相对路径情况下生成的完整URL与重定向方法相同。

绝对路径与重定向不同，容器将相对于Web应用的根目录加参数生成完整的URL，即：

request.getRequestDispatcher("/foo/stuff.do")生成的URL是http://localhost/myApp/foo/stuff.do。

 

★ ServletContext.getRequestDispatcher(String)

参数只能指定为绝对路径，生成的完整URL与HttpServletRequest.getRequestDispatcher(String)相同。

## 贡献人员名单

名单按照字母顺序排序。

* [zhangzhimiao](https://github.com/zhangzhimiao)

## CHANGELOG

* v1.0 2018/10/07 无图版

原文地址：http://gemini.iteye.com/blog/80317