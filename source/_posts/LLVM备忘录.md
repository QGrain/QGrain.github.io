---
title: LLVM备忘录
toc: true
date: 2023-03-02 22:35:08
tags: [LLVM]
categories:
  - [Static Analysis]
index_img: /image/thumbnails/dragon.jpg
---

LLVM备忘录、笔记兼心得

<!--more-->

## 1 源码安装LLVM

### 软件依赖：

| Package                                           | Version   | Notes                                                        |
| ------------------------------------------------- | --------- | ------------------------------------------------------------ |
| [cmake](http://cmake.org/)                        | >=3.20.0  | Makefile/workspace generator                                 |
| [GCC](http://gcc.gnu.org/)                        | >=7.1.0   | C/C++ compiler                                               |
| [python](http://www.python.org/)                  | >=3.6     | Automated test suite(Only needed for automatic test suite in `llvm/test`) |
| [zlib](http://zlib.net/)                          | >=1.2.3.4 | Compression library(Optional)                                |
| [GNU Make](http://savannah.gnu.org/projects/make) | >=3.79.1  | Makefile/build processor(Optional)                           |

### 编译安装

**更新cmake：**

```bash
# 卸载cmake
sudo apt remove -y cmake
sudo apt install -y libssl-dev

wget https://cmake.org/files/v3.23/cmake-3.23.0.tar.gz
tar zxvf cmake-3.23.0.tar.gz
cd cmake-3.23.0
./configure
make -j8
sudo make install
```

**老版本一键安装脚本：build_llvm-old.sh**

```bash
#!/bin/bash
# Install old version llvm-project from source. Two reasons why use this script:
# 1. llvm-project before 9.0.1 does not have a tarball of llvm-project. We need to download them seperately.
# 2. Older llvm-project (before 9.0.1?) uses some features which would be changed or deprecated in C++17. So we'd better use gcc-5 to compile llvm.

set -e

cwd=`pwd`
ver=$1
packages=(llvm cfe compiler-rt libcxx libcxxabi lld clang-tools-extra)
base_url=https://releases.llvm.org/$ver

src_dir=llvm-project-$ver.src
install_dir=llvm-project-$ver.install

mkdir $src_dir $install_dir

# download and extract
pushd $src_dir
for pack in ${packages[@]}
do
        wget $base_url/$pack-$ver.src.tar.xz
        tar xvJf $pack-$ver.src.tar.xz
done
echo -e "\ndownload and extract finish"

mv llvm-$ver.src llvm
mv cfe-$ver.src clang
mv compiler-rt-$ver.src compiler-rt
mv libcxx-$ver.src libcxx
mv libcxxabi-$ver.src libcxxabi
mv lld-$ver.src lld
mv clang-tools-extra-$ver.src clang-tools-extra


# build in $src_dir
mkdir build
cd build

cmake -G Ninja\
                -DCMAKE_BUILD_TYPE=Release \
                # -DLLVM_ENABLE_PROJECTS="clang;lld;compiler-rt" \
                -DLLVM_TARGETS_TO_BUILD="X86" \
                -DCMAKE_INSTALL_PREFIX=$cwd/$install_dir \
                -DLLVM_ENABLE_RUNTIMES="libcxx;libcxxabi" ../llvm

# cmake -G Ninja -DLIBCXX_ENABLE_SHARED=OFF -DLIBCXX_ENABLE_STATIC_ABI_LIBRARY=ON -DCMAKE_BUILD_TYPE=Release -DLLVM_TARGETS_TO_BUILD="X86" -DLLVM_BINUTILS_INCDIR=/usr/include ../llvm

echo -e "\ncmake finish\n"
ninja -j16
echo -e "\nninja finish, return value: $?"
echo -e "\nplease: cd $cwd$src_dir/build && ninja install"
```



**一键安装脚本：build_llvm-project.sh**

```bash
#!/bin/bash

set -e

print_help()
{
    echo -e "Usage: ./build_llvm-project.sh LLVM_VERSION\n"
    echo -e "[Notice] LLVM_VERSION's format is X.Y.Z, *-rcN versions are not supported\n"
    exit 1
}


get_url()
{
    OLD_IFS=$IFS
    IFS="."
    arr=($1)
    IFS=$OLD_IFS

    ver_val=0
    for i in $(seq 0 2)
    do
        ver_val=`echo "$ver_val * 10 + ${arr[$i]}" | bc`
    done

    new_ver=1101
    old_ver=901
    if [[ $ver_val -ge $new_ver ]]
    then
        echo "https://github.com/llvm/llvm-project/releases/download/llvmorg-$1/llvm-project-$1.src.tar.xz"
    elif [[ $ver_val -ge $old_ver ]]
    then
        echo "https://github.com/llvm/llvm-project/releases/download/llvmorg-$1/llvm-project-$1.tar.xz"
    else
    	echo "Old version < 9.0.1, not supported (TODO). Exit."
    	exit 1
    fi
}


cwd=`pwd`
version=$1 # like 13.0.1

if [[ ! $version ]]
then
    echo -e "version is NULL\n"
    print_help
fi

# dependencies
sudo apt update
sudo apt install -y cmake ninja-build libedit-dev python3-dev swig

# download source
INSTALL_DIR=llvm-project-$version.install
URL=`get_url $version`
SRC_DIR=$(basename $URL .tar.xz)

if [[ ! -e $SRC_DIR.tar.xz ]]; then
	wget $URL
fi

if [[ ! -d $SRC_DIR ]]; then
	tar xvJf $SRC_DIR.tar.xz
fi

if [[ ! -d $INSTALL_DIR ]]; then
	mkdir $INSTALL_DIR
	mkdir -p $SRC_DIR/build
fi

cd $SRC_DIR/build

cmake -G Ninja\
                -DCMAKE_BUILD_TYPE="Release" \
                -DLLVM_ENABLE_PROJECTS="clang;lld;compiler-rt" \ # Notice, compiler-rt has trouble with old ver of llvm
                -DLLVM_TARGETS_TO_BUILD="X86" \
                -DLLVM_INSTALL_UTILS=ON \
                -DCMAKE_INSTALL_PREFIX=$cwd/$INSTALL_DIR \
                -DLLVM_ENABLE_RUNTIMES="libcxx;libcxxabi" ../llvm

echo -e "\ncmake finish\n"
ninja -j8
echo -e "\nninja finish, return value: $?"
echo -e "\nplease: cd $SRC_DIR/build && ninja install"
# sudo ninja install
```

**部分参数说明：**

- `-DCMAKE_BUILD_TYPE`：有Release，Debug和RelWithDebInfo三种选项，分别对应-O3，-O0和-O2三种优化。

- `-DLLVM_ENABLE_UTILS=ON`：将utility binaries如`FileCheck`和`not`等安装到`CMAKE_INSTALL_PREFIX`

- `-DLLVM_ENABLE_DUMP=ON`：允许非Debug编译模式下的LLDB动态调试LLVM时可以调用Value对象的dump方法
- `-DLLVM_ENABLE_ASSERTIONS=ON`：启用ASSERT功能，当clang crash时方便调试排查，但会影响clang性能
- `-DCMAKE_INSTALL_PREFIX=$cwd/$INSTALL_DIR`：将llvm-project安装到指定目录，否则会安装到默认的`/usr/local`中。指定此选项之后需要手动将`$INSTALL_DIR/bin`和`$INSTALL_DIR/include`添加到环境变量`PATH`中

### 自动化多版本管理器llvmmrg

## 2 常用命令

### clang

**文件转换图**

<div align=center><img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2023/202304042211483.png" style="zoom: 60%;" /></div>
- `-ccc-print-phrases`：打印编译阶段，指定`-save-temps`参数即可保存以下各阶段的中间文件

```bash
$ clang -ccc-print-phases foo.c
            +- 0: input, "foo.c", c
         +- 1: preprocessor, {0}, cpp-output
      +- 2: compiler, {1}, ir
   +- 3: backend, {2}, assembler
+- 4: assembler, {3}, object
5: linker, {4}, image
```

- `-E`：执行预处理阶段，`clang -E foo.c -o foo.i`
  - 处理所有`#`开头的预编译指令，包括删除并展开`#define`对应宏定义，处理`#if/#ifdef/#else/#endif`，插入`#include/#import`包含的文件
  - 删除所有注释`//`和`/**/`
  - 添加行号和文件名标识，如`# 1 foo.m `
- `-S`：进行编译，包括上述阶段以及LLVM生成和优化阶段，编译生成一个汇编文件，`clang -S foo.c/foo.i -o foo.s`
  - 词法分析：将源代码的字符分割成token（关键字，标识符，变量，特殊符号等）
  - 语法分析：生成抽象语法树AST
  - 静态分析：分析类型声明和匹配问题等
  - 中间语言生成：根据 AST 自顶向下遍历逐步翻译成 LLVM IR
  - 目标代码生成与优化：根据中间语言生成依赖具体机器的汇编语言，并优化汇编语言
- `-c`：运行前面所有的阶段并生成目标文件，`clang -c foo.c -o foo.o`
- `-###`：打印此次编译所执行的命令及选项，但实际不进行编译运行
- `-fsyntax-only -Xclang -ast-dump`：打印语法树AST
- ``-E -Xclang -dump-tokens`：解析并打印tokens，可用作词法分析

### opt

- `--print-passes`：打印可用passes

- ` -enable-new-pm=0 --dot-cfg foo.ll`：生成dot格式的cfg，是以函数为粒度生成`.*.dot`文件的
- `-enable-new-pm=0 --dot-callgraph foo.ll`：生成dot格式的cg

### dot

- `dot -Tpng .main.dot -o main.cfg.png`：将dot转为png

## 3 常用分析Pass

To be completed

## 4 基于LLVM的静态分析工具

### crix/ndi/IPPO

它们基本都是由The Systems Security Group at University of Minnesota（[umnsec](https://github.com/umnsec)）开发的静态分析工具，用于挖掘特定的内核漏洞，代码架构几乎一样：

- Crix: Detecting Missing-Check Bugs in OS Kernels
  - Usenix Security 2019
  - https://github.com/umnsec/crix
- Detecting Missed Security Operations Through Differential Checking of Object-based Similar Paths
  - CCS 2021
  - https://github.com/dinghaoliu/IPPO

- NDI: Non-Distinguishable Inconsistencies as a Deterministic Oracle for Detecting Security Bugs
  - CCS 2022
  - https://github.com/umnsec/ndi

安装时可能遇到以下坑：

- zlib

```bash
# 需要ZLIB_ROOT
export ZLIB_ROOT=

# 并修改CMakeFileList.txt
```



### deadline



## 5 参考

[[1] LLVM官方文档](https://llvm.org/docs/index.html)（其中值得看的几个子链接[Getting Started with the LLVM System](https://llvm.org/docs/GettingStarted.html)，[LLVM Tutorial: Table of Contents](https://llvm.org/docs/tutorial/index.html)，[LLVM Programmer’s Manual](https://llvm.org/docs/ProgrammersManual.html)，[Writing an LLVM Pass](https://llvm.org/docs/WritingAnLLVMPass.html)）

[[2] LLVM Analysis Pass 实验（一）基础操作汇总](https://zhuanlan.zhihu.com/p/594998469)

[[3] LLVM Analysis Pass 实验（二）控制流（Control Flow）可视化](https://zhuanlan.zhihu.com/p/596465125)

[[4] zhqli的博客: LLVM教程](https://zhqli.com/category/7c7d2b9e8389cc541dc5a615e05bcf1e)

[[5] 浅学 Clang（一）：clang 指令介绍](https://juejin.cn/post/7102477449421652005)

[[6] miniSysY 编译实验 | 北航软院](https://buaa-se-compiling.github.io/miniSysY-tutorial/)
