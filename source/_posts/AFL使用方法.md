---
title: AFL使用方法
toc: true
date: 2020-8-28 20:13:03
tags: [AFL, Fuzzing]
index_img: /img/city-blur.jpg
---

了解AFL基本概念后，本文主要介绍如何使用AFL进行模糊测试

<!--more-->

## 1 AFL安装

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

 安装完成后，`afl-*`等二进制文件默认在`/usr/local/bin/`目录下 ，添加其到环境变量`PATH`即可

## 2 AFL使用

### 2.1 打桩

