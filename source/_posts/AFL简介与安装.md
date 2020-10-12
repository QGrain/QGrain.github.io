---
title: AFL简介
toc: true
date: 2020-08-25 22:14:25
tags: [AFL, Fuzzing]
index_img: /img/afl-window.png
---

## AFL简介

AFL即American Fuzzy Lop，是一种安全导向的**模糊测试工具**，采用一种新型的**编译时插桩**和**遗传算法**来自动生成测试样本，使用这些样本可触发目标二进制程序中新的内部状态，从而可提高模糊测试的代码覆盖率。

<!--more-->



<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/master/figure/20201012164706.png"/>

**AFL工作的基本流程如图所示：**

-  从源码编译程序时进行插桩，以记录代码覆盖率（Code Coverage） 
-  选择一些输入文件，作为初始测试集加入输入队列（queue） 
-  将队列中的文件按一定的策略进行“突变” （Mutation）
-  如果经过变异文件更新了覆盖范围，则将其保留添加到队列中 
-  上述过程会一直循环进行，期间触发了crash的文件会被记录下来 

`afl-fuzz` 会记录触发crash的`cmdline`以便研究者手动复现和`gdb`调试分析。

## AFL 安装

**采用源码安装AFL：**

```bash
# 从github下载最新的release v2.57b
wget https://github.com/google/AFL/archive/v2.57b.tar.gz -O afl-2.57b.tar.gz
# 官网自2017年11月就没有再更新，latest version 为2.52b
# wget http://lcamtuf.coredump.cx/afl/releases/afl-latest.tgz -O afl-2.52b.tar.gz

tar xvzf afl-2.57b.tar.gz
cd afl-2.57b

# make && make install
sudo make && sudo make install
```

 安装完成后，`afl-*`等二进制文件默认在`/usr/local/bin/`目录下 ，添加其到环境变量`PATH`即可，关于AFL的用法请见[AFL使用方法]()