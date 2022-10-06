---
title: Zeek安装
toc: true
date: 2020-03-12 23:19:54
tags: [IDS, Zeek]
index_img: /img/zeek-logo.png
---

Zeek的安装是一个大坑  (* ￣︿￣)，本文将介绍Zeek的安装教程，然后附上了我的**踩坑记录**和一键安装脚本。

<!--more-->

## 1 先决条件

### 1.1 必须的依赖

**运行Zeek所必须的包：**

- Libpcap (http://www.tcpdump.org)
- OpenSSL libraries (http://www.openssl.org)
- BIND8 library
- Libz
- Bash (for BroControl)
- Python >= 2.6 (for BroControl)

**从源码安装所必须的包：**

- CMake >= 2.8 (http://www.cmake.org)
- Make
- C/C++ Compiler with C++11 support (GCC 4.8+ or Clang 3.3+)
- SWIG (http://www.swig.org)
- Bison (GNU Parser Generator)
- Flex (Fast Lexical Analyzer)
- Libpcap headers
- OpenSSL headers
- zlib headers
- Python

**通过包管理安装上述依赖：**

- RPM/RedHat-based Linux：

  ```bash
  sudo yum install cmake make gcc gcc-c++ flex bison libpcap-devel openssl-devel python-devel swig zlib-devel
  ```

- DEB/Debian-based Linux：

  ```Bash
  sudo apt-get install cmake make gcc g++ flex bison libpcap-dev libssl-dev python-dev swig zlib1g-dev
  ```

- FreeBSD： FreeBSD的最小安装已经包含了部分依赖，除了以下需要手动安装

  ```bash
  sudo pkg install bash cmake swig bison python py27-sqlite3
  ```

### 1.2 可选的依赖

- C++ Actor Framework (CAF) version 0.14 ([http://actor-framework.org](http://actor-framework.org/))
- LibGeoIP (用于IP地理定位)
- sendmail (让Bro和BroControl能够发送邮件)
- curl
- gperftools (采用了tcmalloc来改善内存和CPU的使用)
- jemalloc (http://www.canonware.com/jemalloc/)
- PF_RING (Linux only)
- ipsumdump (for trace-summary; http://www.cs.ucla.edu/~kohler/ipsumdump)

## 2 安装Zeek

### 2.1 使用预构建的二进制发行包

- **官网下载：**

  有关二进制版本和安装说明的信息，请参见[bro下载页面](https://www.bro.org/download/index.html)以获取当前受支持/目标平台。

- 默认安装路径是`/opt/bro`

### 2.2 源码安装

- **官网下载**

  Zeek的二进制发行包是和源码绑定在一起的，均可在[bro下载页面](https://www.bro.org/download/index.html)获取

- **通过git下载**

  ```bash
  git clone --recursive git://github.com/zeek/zeek.git
  ```

  注意：如果选择非递归克隆，则为最小克隆，许多其他依赖的子模块需要额外获取。

  ```bash
  # ./configure --help 
  ./configure
  make
  make install
  # 默认安装路径为/usr/local/bro
  ```

- **添加环境变量**

## 3 踩坑记录

### 3.1 源码安装

- **一定要选择git clone --recursive**，否则以下submodule将需要额外从git安装
  - zeek/cmake
  - zeek/broker
  - zeek/broker/cmake
  - zeek/broker/3rdparty
  - zeek/broker/3rdparty/caf (mv from zeek/broker/aux/caf)
- **zeek>=3.0有对于以下软件包有更高版本的要求**
  - **cmake >= 3.0**
  - **C/C++ Compiler with C++17 support (GCC 7+ or Clang 4+)**

### 3.2 一键安装脚本

[开源仓库auto-install-zeek](https://github.com/QGrain/auto-install-zeek) :rainbow: