# Elasticsearch 入门

## 1. 概述
        Elasticsearch 是一个分布式、可扩展、实时的搜索与数据分析引擎。 它能从项目一开始就赋予你的数据以搜索、分析和探索的能力，这是通常没有预料到的。 它存在还因为原始数据如果只是躺在磁盘里面根本就毫无用处。 它能让你以一个之前从未有过的速度和规模，去探索你的数据。 它被用作全文检索、结构化搜索、分析以及这三个功能的组合：

* Wikipedia 使用 Elasticsearch 提供带有高亮片段的全文搜索，还有 search-as-you-type 和 did-you-mean 的建议。
卫报 使用 Elasticsearch 将网络社交数据结合到访客日志中，实时的给它的编辑们提供公众对于新文章的反馈。
* Stack Overflow 将地理位置查询融入全文检索中去，并且使用 more-like-this 接口去查找相关的问题与答案。
* GitHub 使用 Elasticsearch 对1300亿行代码进行查询。

        Elasticsearch 将所有的功能打包成一个单独的服务，这样你可以通过程序去访问它提供的简单的 RESTful API 服务， 不论你是使用自己喜欢的编程语言还是直接使用命令行（去充当这个客户端）。

        Elasticsearch 不仅仅只是全文搜索，我们还将介绍结构化搜索、数据分析、复杂的语言处理、地理位置和对象间关联关系等。 我们还将探讨如何给数据建模来充分利用 Elasticsearch 的水平伸缩性，以及在生产环境中如何配置和监视你的集群。

### 1.1 lucene与elastic

        Elasticsearch 是一个开源的搜索引擎，建立在一个全文搜索引擎库 Apache Lucene™ 基础之上。 Lucene 可能是目前存在的，不论开源还是私有的，拥有最先进，高性能和全功能搜索引擎功能的库。

        但是 Lucene 仅仅只是一个库。为了利用它，你需要编写 java 程序，并在你的 java 程序里面直接集成 Lucene 包。 更坏的情况是，你需要对信息检索有一定程度的理解才能明白 Lucene 是怎么工作的。Lucene 是 很 复杂的。

        Elasticsearch 也是使用 Java 编写的，它的内部使用 Lucene 做索引与搜索，但是它的目标是使全文检索变得简单， 通过隐藏 Lucene 的复杂性，取而代之的提供一套简单一致的 RESTful API。

        然而，Elasticsearch 不仅仅是 Lucene，并且也不仅仅只是一个全文搜索引擎。 它可以被下面这样准确的形容：

* 一个分布式的实时文档存储，每个字段 可以被索引与搜索
* 一个分布式实时分析搜索引擎
* 能胜任上百个服务节点的扩展，并支持 PB 级别的结构化或者非结构化数据
        
### 1.2 java与elastic

        如果你正在使用 Java，在代码中你可以使用 Elasticsearch 内置的两个客户端：

        节点客户端（Node client）
        节点客户端作为一个非数据节点加入到本地集群中。换句话说，它本身不保存任何数据，但是它知道数据在集群中的哪个节点中，并且可以把请求转发到正确的节点。
        传输客户端（Transport client）
        轻量级的传输客户端可以可以将请求发送到远程集群。它本身不加入集群，但是它可以将请求转发到集群中的一个节点上。
        两个 Java 客户端都是通过 9300 端口并使用本地 Elasticsearch 传输 协议和集群交互。集群中的节点通过端口 9300 彼此通信。如果这个端口没有打开，节点将无法形成一个集群。

## 2. 基本概念

### 2.1 存储格式
    Elasticsearch 使用 JavaScript Object Notation 或者 JSON 作为文档的序列化格式。JSON 序列化被大多数编程语言所支持，并且已经成为 NoSQL 领域的标准格式。 它简单、简洁、易于阅读。

考虑一下这个 JSON 文档，它代表了一个 user 对象：

        {
            "email":      "john@smith.com",
            "first_name": "John",
            "last_name":  "Smith",
            "info": {
                "bio":         "Eco-warrior and defender of the weak",
                "age":         25,
                "interests": [ "dolphins", "whales" ]
            },
            "join_date": "2014/05/01"
        }
        
虽然原始的 user 对象很复杂，但这个对象的结构和含义在 JSON 版本中都得到了体现和保留。在 Elasticsearch 中将对象转化为 JSON 并做索引要比在一个扁平的表结构中做相同的事情简单的多。

### 2.2 Node 与 Cluster
        Elastic 本质上是一个分布式数据库，允许多台服务器协同工作，每台服务器可以运行多个 Elastic 实例。

        单个 Elastic 实例称为一个节点（node）。一组节点构成一个集群（cluster）。

### 2.3 Index(database)
        Elastic 会索引所有字段，经过处理后写入一个反向索引（Inverted Index）。查找数据的时候，直接查找该索引。

        所以，Elastic 数据管理的顶层单位就叫做 Index（索引）。它是单个数据库的同义词。每个 Index （即数据库）的名字必须是小写。

        下面的命令可以查看当前节点的所有 Index。


        $ curl -X GET 'http://localhost:9200/_cat/indices?v'

### 2.4 Document(Raw)
        Index 里面单条的记录称为 Document（文档）。许多条 Document 构成了一个 Index。

        Document 使用 JSON 格式表示，下面是一个例子。


        {
        "user": "张三",
        "title": "工程师",
        "desc": "数据库管理"
        }
        同一个 Index 里面的 Document，不要求有相同的结构（scheme），但是最好保持相同，这样有利于提高搜索效率。

### 2.5 Type(Table)
        Document 可以分组，比如weather这个 Index 里面，可以按城市分组（北京和上海），也可以按气候分组（晴天和雨天）。这种分组就叫做 Type，它是虚拟的逻辑分组，用来过滤 Document。

        不同的 Type 应该有相似的结构（schema），举例来说，id字段不能在这个组是字符串，在另一个组是数值。这是与关系型数据库的表的一个区别。性质完全不同的数据（比如products和logs）应该存成两个 Index，而不是一个 Index 里面的两个 Type（虽然可以做到）。

        下面的命令可以列出每个 Index 所包含的 Type。


> $ curl 'localhost:9200/_mapping?pretty=true'

        根据规划，Elastic 6.x 版只允许每个 Index 包含一个 Type，7.x 版将会彻底移除 Type。


## 3. 深入搜索

### 3.1 结构化搜索
        结构化搜索（Structured search） 是指有关探询那些具有内在结构数据的过程。比如日期、时间和数字都是结构化的：它们有精确的格式，我们可以对这些格式进行逻辑操作。比较常见的操作包括比较数字或时间的范围，或判定两个值的大小。

        文本也可以是结构化的。如彩色笔可以有离散的颜色集合： 红（red） 、 绿（green） 、 蓝（blue） 。一个博客可能被标记了关键词 分布式（distributed） 和 搜索（search） 。电商网站上的商品都有 UPCs（通用产品码 Universal Product Codes）或其他的唯一标识，它们都需要遵从严格规定的、结构化的格式。

        在结构化查询中，我们得到的结果 总是 非是即否，要么存于集合之中，要么存在集合之外。结构化查询不关心文件的相关度或评分；它简单的对文档包括或排除处理。

**精确值查找**

        当进行精确值查找时， 我们会使用过滤器（filters）。过滤器很重要，因为它们执行速度非常快，不会计算相关度（直接跳过了整个评分阶段）而且很容易被缓存。我们会在本章后面的 过滤器缓存 中讨论过滤器的性能优势，不过现在只要记住：请尽可能多的使用过滤式查询。

        term 查询数字
        我们首先来看最为常用的 term 查询， 可以用它处理数字（numbers）、布尔值（Booleans）、日期（dates）以及文本（text）。

        让我们以下面的例子开始介绍，创建并索引一些表示产品的文档，文档里有字段 `price` 和 `productID` （ `价格` 和 `产品ID` ）：
        POST /my_store/products/_bulk
        { "index": { "_id": 1 }}
        { "price" : 10, "productID" : "XHDK-A-1293-#fJ3" }
        { "index": { "_id": 2 }}
        { "price" : 20, "productID" : "KDKE-B-9947-#kL5" }
        { "index": { "_id": 3 }}
        { "price" : 30, "productID" : "JODL-X-1937-#pV7" }
        { "index": { "_id": 4 }}
        { "price" : 30, "productID" : "QQPX-R-3956-#aD8" }
        我们想要做的是查找具有某个价格的所有产品，有关系数据库背景的人肯定熟悉 SQL，如果我们将其用 SQL 形式表达，会是下面这样：

        SELECT document
        FROM   products
        WHERE  price = 20
        在 Elasticsearch 的查询表达式（query DSL）中，我们可以使用 term 查询达到相同的目的。 term 查询会查找我们指定的精确值。作为其本身， term 查询是简单的。它接受一个字段名以及我们希望查找的数值：

        {
            "term" : {
                "price" : 20
            }
        }
        通常当查找一个精确值的时候，我们不希望对查询进行评分计算。只希望对文档进行包括或排除的计算，所以我们会使用 constant_score 查询以非评分模式来执行 term 查询并以一作为统一评分。

        最终组合的结果是一个 constant_score 查询，它包含一个 term 查询：

        GET /my_store/products/_search
        {
            "query" : {
                "constant_score" : { 
                    "filter" : {
                        "term" : { 
                            "price" : 20
                        }
                    }
                }
            }
        }

### 3.2 全文搜索

        我们已经介绍了搜索结构化数据的简单应用示例，现在来探寻 全文搜索（full-text search） ：怎样在全文字段中搜索到最相关的文档。

        全文搜索两个最重要的方面是：

        相关性（Relevance）
        它是评价查询与其结果间的相关程度，并根据这种相关程度对结果排名的一种能力，这种计算方式可以是 TF/IDF 方法（参见 相关性的介绍）、地理位置邻近、模糊相似，或其他的某些算法。
        分析（Analysis）
        它是将文本块转换为有区别的、规范化的 token 的一个过程，（参见 分析的介绍） 目的是为了（a）创建倒排索引以及（b）查询倒排索引。
        一旦谈论相关性或分析这两个方面的问题时，我们所处的语境是关于查询的而不是过滤。

**匹配查询**

        匹配查询 match 是个 核心 查询。无论需要查询什么字段， match 查询都应该会是首选的查询方式。 它是一个高级 全文查询 ，这表示它既能处理全文字段，又能处理精确字段。

        这就是说， match 查询主要的应用场景就是进行全文搜索，我们以下面一个简单例子来说明全文搜索是如何工作的：

        索引一些数据
        首先，我们使用 bulk API 创建一些新的文档和索引：

        DELETE /my_index 

        PUT /my_index
        { "settings": { "number_of_shards": 1 }} 

        POST /my_index/my_type/_bulk
        { "index": { "_id": 1 }}
        { "title": "The quick brown fox" }
        { "index": { "_id": 2 }}
        { "title": "The quick brown fox jumps over the lazy dog" }
        { "index": { "_id": 3 }}
        { "title": "The quick brown fox jumps over the quick dog" }
        { "index": { "_id": 4 }}
        { "title": "Brown fox brown dog" }


        删除已有的索引。



        稍后，我们会在 被破坏的相关性！ 中解释只为这个索引分配一个主分片的原因。

        单个词查询
        我们用第一个示例来解释使用 match 查询搜索全文字段中的单个词：

        GET /my_index/my_type/_search
        {
            "query": {
                "match": {
                    "title": "QUICK!"
                }
            }
        }
        Elasticsearch 执行上面这个 match 查询的步骤是：

        检查字段类型 。

        标题 title 字段是一个 string 类型（ analyzed ）已分析的全文字段，这意味着查询字符串本身也应该被分析。

        分析查询字符串 。

        将查询的字符串 QUICK! 传入标准分析器中，输出的结果是单个项 quick 。因为只有一个单词项，所以 match 查询执行的是单个底层 term 查询。

        查找匹配文档 。

        用 term 查询在倒排索引中查找 quick 然后获取一组包含该项的文档，本例的结果是文档：1、2 和 3 。

        为每个文档评分 。

        用 term 查询计算每个文档相关度评分 _score ，这是种将 词频（term frequency，即词 quick 在相关文档的 title 字段中出现的频率）和反向文档频率（inverse document frequency，即词 quick 在所有文档的 title 字段中出现的频率），以及字段的长度（即字段越短相关度越高）相结合的计算方式。参见 相关性的介绍 。

        这个过程给我们以下（经缩减）结果：

        "hits": [
        {
            "_id":      "1",
            "_score":   0.5, 
            "_source": {
            "title": "The quick brown fox"
            }
        },
        {
            "_id":      "3",
            "_score":   0.44194174, 
            "_source": {
            "title": "The quick brown fox jumps over the quick dog"
            }
        },
        {
            "_id":      "2",
            "_score":   0.3125, 
            "_source": {
            "title": "The quick brown fox jumps over the lazy dog"
            }
        }
        ]


        文档 1 最相关，因为它的 title 字段更短，即 quick 占据内容的一大部分。
        文档 3 比 文档 2 更具相关性，因为在文档 2 中 quick 出现了两次。

## 4. 探索原理
* [神一样的存在](https://www.cnblogs.com/dreamroute/p/8484457.html)

## 参考资料
* [阮一峰的博客](http://www.ruanyifeng.com/blog/2017/08/elasticsearch.html)
* [Elasticsearch: 权威指南](https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/index.html)
* [阮一峰的博客](http://www.ruanyifeng.com/blog/2017/08/elasticsearch.html)

## 贡献人员名单

名单按照字母顺序排序。

* [Gasen](https://github.com/GasenLi)

## CHANGELOG

* v1.0 2019/02/24 第一版