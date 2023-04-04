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
| [cmake](http://cmake.org/)                        | >=3.13.4  | Makefile/workspace generator                                 |
| [GCC](http://gcc.gnu.org/)                        | >=7.1.0   | C/C++ compiler                                               |
| [python](http://www.python.org/)                  | >=3.6     | Automated test suite(Only needed for automatic test suite in `llvm/test`) |
| [zlib](http://zlib.net/)                          | >=1.2.3.4 | Compression library(Optional)                                |
| [GNU Make](http://savannah.gnu.org/projects/make) | >=3.79.1  | Makefile/build processor(Optional)                           |

### 编译安装

**一键安装脚本：**

```bash
#!/bin/bash

set -e

print_help()
{
    echo -e "Usage: ./build_llvm-project.sh LLVM_VERSION\n"
    exit 1
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
sudo apt install -y cmake ninja-build

# download source
INSTALL_DIR=llvm-project-$version.install
SRC_DIR=llvm-project-$version.src
wget https://github.com/llvm/llvm-project/releases/download/llvmorg-$version/$SRC_DIR.tar.xz
tar xvJf $SRC_DIR.tar.xz
mkdir $INSTALL_DIR
mkdir -p $SRC_DIR/build
cd $SRC_DIR/build

cmake -G Ninja\
                -DCMAKE_BUILD_TYPE="Release" \
                -DLLVM_ENABLE_PROJECTS="clang;lld;lldb;compiler-rt" \
                -DLLVM_INSTALL_UTILS=ON \
                -DLLVM_ENABLE_RTTI=ON \
                -DCMAKE_INSTALL_PREFIX=$cwd/$INSTALL_DIR \
                -DLLVM_ENABLE_RUNTIMES="libcxx;libcxxabi" ../llvm

echo -e "\ncmake finish\n"
ninja -j8
echo -e "\nninja finish, return value: $?"
echo -e "\nplease: sudo ninja install"
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

## 3 常用分析Pass

### 打印相关信息Pass

### CFG Pass

## 4 参考

[[1] LLVM官方文档](https://llvm.org/docs/index.html)（其中值得看的几个子链接[Getting Started with the LLVM System](https://llvm.org/docs/GettingStarted.html)，[LLVM Tutorial: Table of Contents](https://llvm.org/docs/tutorial/index.html)，[LLVM Programmer’s Manual](https://llvm.org/docs/ProgrammersManual.html)，[Writing an LLVM Pass](https://llvm.org/docs/WritingAnLLVMPass.html)）

[[2] LLVM Analysis Pass 实验（一）基础操作汇总](https://zhuanlan.zhihu.com/p/594998469)

[[3] LLVM Analysis Pass 实验（二）控制流（Control Flow）可视化](https://zhuanlan.zhihu.com/p/596465125)

[[4] zhqli的博客: LLVM教程](https://zhqli.com/category/7c7d2b9e8389cc541dc5a615e05bcf1e)

[[5] 浅学 Clang（一）：clang 指令介绍](https://juejin.cn/post/7102477449421652005)

[[6] miniSysY 编译实验 | 北航软院](https://buaa-se-compiling.github.io/miniSysY-tutorial/)
