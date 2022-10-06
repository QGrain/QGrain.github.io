---
title: AFLGO安装与使用
toc: true
date: 2020-10-21 14:25:11
tags: [AFL, Fuzz]
index_img: /img/blur-sight.jpg
---

[AFLGO](https://github.com/aflgo/aflgo)是基于[AFL](https://github.com/google/AFL)改进而来的一种定向灰盒模糊测试工具。

<!--more-->

AFLGo基于llvm实现了函数调用图CG和控制流图CFG的获取，结合对程序CG/CFG定义了一种基于目标distance衡量的种子调度策略，使得Fuzzer能够更快生成可抵达目的位置的测试用例。定向灰盒模糊测试(Directed Greybox Fuzzing)常常用于补丁测试，漏洞复现等等具备特定待测目标的软件测试任务场景。

## 1 安装AFLGO

- 安装llvm和clang，官方文档要求是3.8或者4.0，经过自己测试6.0和11.0和也可以使用，**确保环境$PATH或$LLVM_CONFIG变量已经添加**
- 执行[官方一键安装脚本](https://raw.githubusercontent.com/aflgo/aflgo/master/scripts/build/aflgo-build.sh)，此脚本疑似有问题（截止至2020年10月），其中llvm-4.0和clang-4.0会安装失败
- 因此依照[官方README](https://github.com/aflgo/aflgo)的指示，我编写了一键安装**llvm 11.0**以及AFLGo的[脚本](https://gitee.com/QGrain/aflgo-build/tree/master)：

```bash
curl https://gitee.com/QGrain/aflgo-build/raw/master/aflgo-build.sh | bash
```

## 2 常见开源库和软件

**Google**已经总结了[三百多款常见的开源软件](https://github.com/google/oss-fuzz/tree/master/projects)用于模糊测试，每一个工具的目录结构如下：

> /PATH/TO/TOOL
> │─  **build.sh**
> │─  **Dockerfile**
> │─  **project.yaml**
> └─  **my-api-repo**
>         │─  do_stuff_fuzzer.cpp
>         │─  do_stuff_fuzzer.dict
>         │─  do_stuff_unittest.cpp
>         │─  Makefile
>         │─  my_api.cpp
>         │─  my_api.h
>         │─  README.md
>         │─  standalone_fuzz_target_runner.cpp
>     └─  **do_stuff_test_data**
>         │─  410c23d234e7f97a2dd6265eb2909324deb8c13a
>         │─  7a74862169c3375f4149daff75187cbca7372a38

并不是所有的工具都提供了相关的代码和语料，但是每一款工具都给出了

- **project.yaml：**记录了项目基本信息，例如：

```yaml
homepage: "https://www.gnu.org/software/binutils/"
language: c++
primary_contact: "bug-binutils@gnu.org"
auto_ccs :
  - "p.antoine@catenacyber.fr"
  - "nickc@redhat.com"
  - "amodra@gmail.com"
  - "david@adalogics.com"
fuzzing_engines:
  - libfuzzer
  - afl
  - honggfuzz
  - dataflow
sanitizers:
  - address
  - undefined
  - memory
  - dataflow
```

- **Dockerfile：**定义实验环境容器
- **build.sh：**用于构建实验环境容器

撰写了一个脚本解析出了所有支持`afl`引擎的开源工具列表：

> binutils brotli brunsli bzip2
> c-ares capstone cjson cmark
> graphicsmagick grok harfbuzz
> hermes hostap
> jbig2dec json-c
> lcms libcbor libexif libfdk-aac libidn2 libldac libpcap libplist libspectre libtasn1 libteken libwebp libxml2 libyaml libyuv lz4
> miniz monero mupdf
> nanopb ndpi nestegg nghttp2 ntp
> openjpeg openthread opus ots
> pcre2 pffft proxygen
> qubes-os
> rnp
> speex stb 
> tidy-html5 tor tremor
> unicorn usrsctp
> vorbis
> wireshark woff2 wolfssl wuffs
> xz
> yajl-ruby
> zlib zlib-ng zstd

然后选取了其中较为常用，轻量级的几款工具来做AFLGO Fuzzing测试

### 2.1 bzip2

- 介绍： bzip2是一款比传统的[gzip](https://zh.wikipedia.org/wiki/Gzip)或者[ZIP](https://zh.wikipedia.org/wiki/ZIP)的压缩效率更高但是压缩速度较慢的压缩工具，其算法可以排名到前百分之十到十五。 
- 源码地址： https://sourceware.org/git/bzip2.git
- oss-fuzz项目地址：https://github.com/google/oss-fuzz/tree/master/projects/bzip2

### 2.2 binutils

- 介绍： binutils 是一组开发工具，包括连接器，汇编器和其他用于目标文件和档案的工具。 
- 源码地址：https://github.com/bminor/binutils-gdb
- oss-fuzz项目地址：https://github.com/google/oss-fuzz/tree/master/projects/binutils

### 2.3 cJSON

- 介绍： JSON是使用C语言编写，用来创建、解析JSON文件的库。 
- 源码地址：https://github.com/DaveGamble/cJSON
- oss-fuzz项目地址：https://github.com/google/oss-fuzz/tree/master/projects/cjson

### 2.4 libpcap

- 介绍： libpcap（Packet Capture Library），即数据包捕获函数库，是Unix/Linux平台下的网络数据包捕获函数库。它是一个独立于系统的用户层包捕获的API接口，为底层网络监测提供了一个可移植的框架。 
- 源码地址：https://github.com/the-tcpdump-group/libpcap
- oss-fuzz项目地址：https://github.com/google/oss-fuzz/tree/master/projects/libpcap

## 3 参考文档

[值得推荐的C/C++开源框架和库](https://blog.csdn.net/iw1210/article/details/52093742)

[开源软件分类列表](https://blog.csdn.net/h_mich/article/details/7402059)

[软件分类-开源中国OSCHINA](https://www.oschina.net/project/tags)