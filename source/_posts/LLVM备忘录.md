---
title: LLVM备忘录
toc: true
date: 2023-03-02 22:35:08
tags:
categories:
index_img:
---

记录LLVM使用心得，备忘录

<!--more-->

## 源码安装LLVM

- 软件依赖：

| Package                                           | Version   | Notes                                                        |
| ------------------------------------------------- | --------- | ------------------------------------------------------------ |
| [cmake](http://cmake.org/)                        | >=3.13.4  | Makefile/workspace generator                                 |
| [GCC](http://gcc.gnu.org/)                        | >=7.1.0   | C/C++ compiler                                               |
| [python](http://www.python.org/)                  | >=3.6     | Automated test suite(Only needed for automatic test suite in `llvm/test`) |
| [zlib](http://zlib.net/)                          | >=1.2.3.4 | Compression library(Optional)                                |
| [GNU Make](http://savannah.gnu.org/projects/make) | >=3.79.1  | Makefile/build processor(Optional)                           |

- 编译安装

```bash
#!/bin/bash

set -e

# dependencies
sudo apt update
sudo apt install -y cmake ninja-build

# download source
version=$1 # like 13.0.1
wget https://github.com/llvm/llvm-project/releases/download/llvmorg-$version/llvm-project-$version.src.tar.xz
tar xvJf llvm-project-$version.src.tar.xz
mkdir -p llvm-project-$version.src/build
cd llvm-project-$version.src/build

cmake -G Ninja\
		-DCMAKE_BUILD_TYPE="Release" \
		-DLLVM_ENABLE_PROJECTS="clang;lld;lldb;compiler-rt" \
		-DLLVM_INSTALL_UTILS=ON \
		-DLLVM_ENABLE_RTTI=ON \
		-DLLVM_ENABLE_RUNTIMES="libcxx;libcxxabi" ../llvm
		
ninja -j8
echo -e "\nninja finish, return value: $?"
echo -e "\nplease: sudo ninja install"
# sudo ninja install
```

## 常用分析Pass

### 打印相关信息Pass

### CFG Pass
