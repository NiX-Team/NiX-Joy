# Markdown 基础

一种用来写文档的标记语言，一般会转换成 HTML 进行显示。

## 标题

```markdown
# 一级标题
## 二级标题
### 三级标题
#### 四级标题
##### 五级标题
###### 六级标题
```

* 对应 HTML 中的 `<h1>` 到 `<h6>`
* `#` 号的数量代表标题的级别
* 文档必须以一级标题开始
* 可以使用成对的 `#` 号，比如 `# test #`，但是修改起来麻烦，不推荐那样做
* 同样也不推荐下划线形式的标题

## 分隔线

```markdown
----------
```

* 分隔线需要用大于等于三个的 `-` 号才可以被正常解析，为了可读性推荐十个
* 分隔线也可以使用大于等于三个的 `*` 或者 `_`，但是不推荐
* 一般一级标题自带分隔线

## 列表

```markdown
* 无序列表
* 无序列表
    * 无序子列表
    * 无序子列表
* 无序列表
```

```markdown
1. 有序列表 1
1. 有序列表 2
    1. 有序列表 2.1
    1. 有序列表 2.2
1. 有序列表 3
```

* 对应HTML中的 `<ul>` 和 `<ol>`
* 无序列表不仅可以使用 `*` 号，也可以使用 `+` 和 `-`，推荐使用 `*` 号
* 对于有序列表，能够影响它序号数字的只有第一个元素，所以一般统一使用 `1.`，方便修改

## 引用

```markdown
> 引用内容
>> 嵌套引用内容
```

* 对应 HTML 中的 `<blockquote>`
* 一般用于引用他人书籍、文章、文档

## 重点和删除

```markdown
*斜体*
**粗体**
***粗斜体***
~~删除~~
```

* 分别对应 HTML 中的 `<strong>`、`<em>` 和 `<del>`
* 根据不同语义使用不同的着重形式，不建议大面积使用

## 链接和图片

```markdown
[链接描述](https://链接 "title，对应 <a> 中的 title 属性")

<https://链接>

![图片描述](https://图片链接)


[链接描述][链接 id]
> [链接 id]: https://链接 "title，对应 <a> 中的 title 属性"

![图片描述][图片 id]
> [图片 id]: https://图片链接
```

* 对应 HTML 中的 `<a>` 和 `<img>`

## 代码

<pre>
`print('hello world!')`

```python
import this
```
</pre>

* 对应 HTML 中的 `<code>`
* 第一种形式用于代码行
* 代码块建议尽量使用第二种形式，因为可以添加代码高亮

## HTML 代码和转义

Markdown 兼容 HTML，所以可以直接在 Markdown 文档中写 HTML 代码。

Markdown 的转义有两种形式，一种是 HTML 符号实体，像 `&lt; &gt; &#35;` 之类的。另一种是斜杠转义符。

## 其他

* 有些编辑器支持 UML 和 LaTex 的格式转换，但是并不推荐使用，因为这些功能并不一定所有的编辑器都会支持
* 如果需要插入一些比较特殊的图片或者公式，推荐转换为图片（Graphviz、PowerDesigner、visio）之后再上传，或者直接用 HTML 代码
* VSCode 用户推荐使用 [markdownlint](https://marketplace.visualstudio.com/items?itemName=DavidAnson.vscode-markdownlint) 插件

## 贡献人员名单

名单按照字母顺序排序。

* [GanZiQim](https://github.com/ganziqim)

## CHANGELOG

* v1.0 2017/05/07 初稿（GanZiQim）
* v2.0 2018/05/28 规范化的 Markdown（GanZiQim）