# NiX-Joy

NiX-Joy 的技术分享资料

## 索引

<details>
    <summary>2017</summary>
    <details>
        <summary>05</summary>
        <a href="markdown/Markdown基础.md">Markdown 基础</a>
    </details>
</details>

## Commit 信息格式

年月日加上文档名，比如 `2017/05/07 Markdown 基础`

## 文档格式

* 所有文档统一采用 Markdown 格式编写
* 文档应当符合[中文排版规范](https://github.com/sparanoid/chinese-copywriting-guidelines)和 [Markdown 编写规范](https://github.com/DavidAnson/markdownlint/blob/master/doc/Rules.md)
* 文档名应该对应一级标题名
* 为了方便复用和修改，参照现有文档添加贡献人员和 CHANGELOG

## 文档存放

* 文档统一存放到根目录下对应技术分类目录中，比如 `Markdown基础.md` 应该存放在 `./markdown/` 目录下
* 技术分类目录名尽量使用小写英文字母，比如 C++ 相关文档存在 `./cpp/` 目录下，C# 则是 `./csharp/`
* 如果文档附带有图片或者源代码，则单独建一个文件夹存放，比如 `./python/Python多线程/`，文档中图片和源码使用相对地址索引

## 添加索引

* 上传文档的同时同步为 README 添加文档索引
* 格式参照现有 README 源码中的格式
* 链接地址采用相对地址
* 如果是单文档，索引到文档地址，如果文档附带图片或源码，索引到对应的目录
* 索引目录以月份为最小粒度
* 年份按逆序排序（2018 年应该在 2017 年前面），月份按正序排序（1 月份应该在 2月份前面）

## 文档复用

* 如果你觉得某位讲过相同 topic 的前辈写的文档可以复用，可以直接复用文档
* 如果对原文档有修改，记得添加贡献人员和 CHANGELOG
* 记得添加索引，不必删除旧索引