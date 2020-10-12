---
title: AFL简介
toc: true
date: 2020-08-25 22:14:25
tags: [AFL, Fuzzing]
index_img: /img/afl-window.png
---

**模糊测试（Fuzzing）**技术作为**漏洞挖掘**最有效的手段之一，近年来一直是众多安全研究人员发现漏洞的首选技术。  **AFL**、LibFuzzer、honggfuzz等操作简单友好的工具相继出现，也极大地降低了模糊测试的门槛。 

<!--more-->

AFL即American Fuzzy Lop，是由安全研究员Micha · Zalewski（[@lcamtuf](https://twitter.com/lcamtuf)）开发的一款基于覆盖引导（Coverage-guided）的模糊测试工具，采用一种新型的**编译时插桩**和**遗传算法**来自动生成测试样本，使用这些样本可触发目标二进制程序中新的内部状态，从而可提高模糊测试的代码覆盖率。

## 1 AFL工作流



<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/master/figure/20201012164706.png"/>

**AFL工作的基本流程如图所示：**

-  从源码编译程序时进行插桩，以记录代码覆盖率（Code Coverage） 
-  选择一些输入文件，作为初始测试集加入输入队列（queue） 
-  将队列中的文件按一定的策略进行“突变” （Mutation）
-  如果经过变异文件更新了覆盖范围，则将其保留添加到队列中 
-  上述过程会一直循环进行，期间触发了crash的文件会被记录下来 

`afl-fuzz` 会记录触发crash的`cmdline`以便研究者手动复现和`gdb`调试分析。

## 2 代码插桩

## 3 构建语料库

## 4 

