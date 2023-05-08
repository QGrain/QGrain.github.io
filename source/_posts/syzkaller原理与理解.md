---
title: syzkaller原理与理解
toc: true
date: 2023-05-08 23:27:05
tags: [Kernel, Fuzz, Syzkaller]
categories: [Fuzz]
index_img: /image/thumbnails/fantasy-wallpaper-nature.jpg
---

结合源码和实践，深入syzkaller原理

<!--more-->

## 0 前言

学习和使用syzkaller有一段时间了，一直耐不下心来仔细地读syzkaller源码，而看其他源码阅读笔记之类的博客又难以形成深刻的印象和理解，所以还是下定决心以自己的方法来理解syzkaller这个大项目，并且以记录博文的形式来督促自己。（哈哈哈）

## 1 代码架构

<p class="note note-primary">要深入理解一个庞大的项目，源码阅读是必不可少的</p>

首先来看看syzkaller的代码架构，目前为止syzkalle最新的commit为2023-05-06的[90c93c4](https://github.com/google/syzkaller/commit/90c93c40627cb0ac3c2c7cb99d807fd4c137adcb)，其代码分布如下：

```bash
--------------------------------------------------------------------------------
Language                      files          blank        comment           code
--------------------------------------------------------------------------------
Go                             3582         118754         270825        1002983
JSON                             19              1              0          77237
Markdown                        282           8538              0          25390
C/C++ Header                     43           1483           1057          11522
YAML                            149            517            691           6521
Assembly                         34           1900            408           6288
Bourne Shell                     44            543            837           3171
C++                               4            172            199           1847
Protocol Buffers                 13            379             51           1380
make                             44            361             98           1251
HTML                             15             58              0           1211
Python                            8            220            186           1036
Bourne Again Shell                4             52             90            455
C                                 4             40             26            334
CSS                               2             68              0            315
Dockerfile                       10             56             97            218
zsh                               1             14              3            191
diff                              2             16             65            157
yacc                              1             21              3            141
TOML                              4             48            104            124
JavaScript                        2             11              4            106
AsciiDoc                          1             22              0             53
--------------------------------------------------------------------------------
SUM:                           4268         133274         274744        1141931
--------------------------------------------------------------------------------
```

哇趣，乍一看一百多万行代码头都大了。但查看按照功能模块目录划分的代码规模，就会发现其中八十多万行都是来自vendor管理机制的包的代码。

```bash
.
|-- Makefile
|-- dashboard          (22699 Go)
|-- docs               (6919 Markdown)
|-- executor           (12953 C/C++, 280 Assembly, 257 Go)
|-- pkg                (61924 Go)
|-- prog               (12015 Go)
|-- sys                (13332 Go)
|-- syz-ci             (2282 Go)
|-- syz-fuzzer         (1379 Go)
|-- syz-hub            (975 Go)
|-- syz-manager        (3218 Go)
|-- syz-runner         (125 Go)
|-- syz-verifier       (1693 Go)
|-- tools              (16556 Go, 1017 Shell, 685 C/C++, 485 Py)
|-- vendor             (859047 Go ... )
|-- vm                 (7481 Go)
```

