---
title: Zeek脚本语言(一)
toc: true
date: 2020-03-16 23:23:40
tags: [IDS, Zeek]
categories: [Tutorial]
index_img: /img/zeek-logo.png
---

本文将主要介绍和讲解Zeek脚本语言的**数据类型**，**基于语法**和**属性**

<!--more-->

## 数据类型

### 基础数据类型

**Zeek有以下内建数据类型**

| 名称                                                         | 描述                           |
| ------------------------------------------------------------ | ------------------------------ |
| [`bool`](https://docs.zeek.org/en/master/script-reference/types.html#type-bool) | 布尔型                         |
| [`count`](https://docs.zeek.org/en/master/script-reference/types.html#type-count)， [`int`](https://docs.zeek.org/en/master/script-reference/types.html#type-int)， [`double`](https://docs.zeek.org/en/master/script-reference/types.html#type-double) | 数值类型                       |
| [`time`](https://docs.zeek.org/en/master/script-reference/types.html#type-time)， [`interval`](https://docs.zeek.org/en/master/script-reference/types.html#type-interval) | 时间类型                       |
| [`string`](https://docs.zeek.org/en/master/script-reference/types.html#type-string) | 字符串                         |
| [`pattern`](https://docs.zeek.org/en/master/script-reference/types.html#type-pattern) | 正则表达式                     |
| [`port`](https://docs.zeek.org/en/master/script-reference/types.html#type-port)， [`addr`](https://docs.zeek.org/en/master/script-reference/types.html#type-addr)， [`subnet`](https://docs.zeek.org/en/master/script-reference/types.html#type-subnet) | 网络类型                       |
| [`enum`](https://docs.zeek.org/en/master/script-reference/types.html#type-enum) | 枚举（用户定义类型）           |
| [`table`](https://docs.zeek.org/en/master/script-reference/types.html#type-table)， [`set`](https://docs.zeek.org/en/master/script-reference/types.html#type-set)， [`vector`](https://docs.zeek.org/en/master/script-reference/types.html#type-vector)， [`record`](https://docs.zeek.org/en/master/script-reference/types.html#type-record) | 容器类型                       |
| [`function`](https://docs.zeek.org/en/master/script-reference/types.html#type-function)， [`event`](https://docs.zeek.org/en/master/script-reference/types.html#type-event)， [`hook`](https://docs.zeek.org/en/master/script-reference/types.html#type-hook) | 可执行类型                     |
| [`file`](https://docs.zeek.org/en/master/script-reference/types.html#type-file) | 文件类型（仅用于写入）         |
| [`opaque`](https://docs.zeek.org/en/master/script-reference/types.html#type-opaque) | 不透明类型（用于某些内建功能） |
| [`any`](https://docs.zeek.org/en/master/script-reference/types.html#type-any) | 任何类型（用于函数或容器）     |

**以下是每一种数据类型的详细说明**

- **`bool`**：拥有`T`和`F`两种取值。支持比较运算（==，!=），逻辑运算和绝对值运算（|T|=1，|F|=0，类型为`count`）
- **`int`**：64位有符号整型。支持算术，比较，逻辑，赋值和绝对值运算（运算结果类型为`count`）
- **`count`**：64位无符号整型。支持的运算符和`int`相同，其中一元加减运算的结果的类型为`int`
- **`double`**：双精度浮点型。支持的运算符与`int`相同
- **`time`**：表示绝对时间的时间类型。`time`类型仅能通过`double_to_time`, `current_time`, `network_time`内建函数来赋值。`time`类型支持比较运算符。`time`类型相减能够产生`interval`类型数据，`time`类型的绝对值是`double`类型
- **`interval`**：表示相对时间的时间类型。其格式为数字常数+时间单位，时间单位有`usec`, `msec`, `sec`, `min`, `hr`和`day`。以下几种均为正确的表达形式：`3.5 min`, `3.5min`, `3.5mins`, `-12 hr`
- **`string`**：字符串类型。由双引号包括，脚本中不支持多行字符串
  - 支持`+`拼接，`=`和`+=`来赋值。支持pp比较运算符。取绝对值运算可以计算出字符串的长度。支持`in`和`!in`来判断字符串包含关系
  - 支持`\`转义如：`\\`, `\n`, `\t`, `\v`, `\b`, `\r`, `\f`, `\a`, `\onn`(n为8进制数码), `\xhh`(h为16进制数码)。当Zeek无法识别转义字符串时将会忽略`\`，如`\g`将会变成`g`
  - 支持下标法访问字符串中的字符。但是不可以对下表法表示的字符串进行赋值修改，即它们是只读的
- **`pattern`**：正则表达式类型。`pattern`常量是通过两个正斜杠`/`来创建的，并采用与[flex词法分析器](http://westes.github.io/flex/manual/Patterns.html)语法

- **`port`**：表示传输层端口的数据类型。`port`常量由一个无符号整数和端口类型（`\tcp`, `\udp`, `\icmp` or `\unknow`）组成
  - `port`类型支持比较运算符，且比较顺序为 `unknown` < `tcp` < `udp` < `icmp`，比如`65535/tcp` < `0/udp` 
  - `get_port_transport_proto`和`port_to_count`是Zeek内建的函数，分别能够从`port`型数据提取传输协议（后面的协议字符串）和端口号（前面的`count`型端口号）
- **`addr`**：表示IP地址的类型。支持ipv4和ipv6，且支持其常规的表示方式
  - 支持比较运算符，比较大小时当作正常数值进行比较。如`192.168.99.254` < `192.168.100.0`
  - 可以通过`/`来产生`subnet`型数据。并可以用`in`来判断一个`addr`是否属于`subnet`
  - 一个域名数据可能对应多个IP地址，因此常用`set[addr]`来表示
- **`subnet`**：表示子网的类型。由`addr`和`/network_prefix_size`组成。如`192.168.100.0/24`和 `[fe80::]/64`。`subnet`类型仅支持`==`和`!=` 比较运算符
- **`enum`**：枚举类型。且枚举类型的value不具备深层次的结构。仅支持`==`, `!=` 和`=`运算符

### 高级数据类型

**Zeek还有以下内建数据类型**

| 名称                                                         | 描述                           |
| ------------------------------------------------------------ | ------------------------------ |
| [`table`](https://docs.zeek.org/en/master/script-reference/types.html#type-table)， [`set`](https://docs.zeek.org/en/master/script-reference/types.html#type-set)， [`vector`](https://docs.zeek.org/en/master/script-reference/types.html#type-vector)， [`record`](https://docs.zeek.org/en/master/script-reference/types.html#type-record) | 容器类型                       |
| [`function`](https://docs.zeek.org/en/master/script-reference/types.html#type-function)， [`event`](https://docs.zeek.org/en/master/script-reference/types.html#type-event)， [`hook`](https://docs.zeek.org/en/master/script-reference/types.html#type-hook) | 可执行类型                     |
| [`file`](https://docs.zeek.org/en/master/script-reference/types.html#type-file) | 文件类型（仅用于写入）         |
| [`opaque`](https://docs.zeek.org/en/master/script-reference/types.html#type-opaque) | 不透明类型（用于某些内建功能） |
| [`any`](https://docs.zeek.org/en/master/script-reference/types.html#type-any) | 任何类型（用于函数或容器）     |

- **`table`**：表示映射关系的表类型。被映射的值称为`index`或者`indices`，映射的结果称为`yield`。是一种非常高效的索引类型，其内部实质为一个单哈希查找表
- **`set`**：

## 基本语法

### 定义变量

### 定义函数

### 运算符

### 条件语句

### 循环语句

## 属性