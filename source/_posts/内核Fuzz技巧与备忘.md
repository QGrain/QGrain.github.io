---
title: 内核Fuzz技巧与备忘
toc: true
date: 2023-02-01 16:26:29
tags: [Kernel, Fuzz]
categories: [Fuzz]
index_img: /image/thumbnails/cube.jpg
hide: false
---

留个备忘录方便查看做kernel fuzzing中遇到的问题和解决技巧（持续更新中ing）

<!--more-->

## 1 常用脚本

- 一键安装syzkaller

```bash
#!/bin/bash

set -e 

sudo apt update
sudo apt install -y debootstrap qemu qemu-kvm
sudo apt install -y git make build-essential openssh-server bc
sudo apt install -y libssl-dev libelf-dev
sudo apt install -y flex bison libc6-dev libc6-dev-i386 linux-libc-dev libgmp3-dev libmpfr-dev libmpc-dev

pushd ~
wget https://dl.google.com/go/go1.19.6.linux-amd64.tar.gz
tar -zxvf go1.19.6.linux-amd64.tar.gz
echo "export GOPATH=~/go" >> ~/.bashrc
echo "export PATH=\$GOPATH/bin:\$PATH" >> ~/.bashrc
source ~/.bashrc

git clone https://github.com/google/syzkaller.git
cd syzkaller
make -j

echo -e "\nDone!"
```

- 查看crashs目录结果

```shell
#!/bin/bash

set -e

crash_dir=$1
dumb_mode=$2

print_help() {
        echo -e "Usage: ./get_result.sh /path/to/crashs_dir"
        exit 0
}

# customized
# $1=desc, $2=repro
dumb_filter() {
        if [[ $2 -gt 0 ]]
        then
                echo 0
        else
                echo 1
        fi
}

if [[ ! -n "$crash_dir" ]]
then
        print_help
fi

cnt=0
for crash in `ls $crash_dir`
do
        desc=`cat $crash_dir/$crash/description`
        syz_repro=`ls $crash_dir/$crash | grep "repro.prog" | wc -l`
        c_repro=`ls $crash_dir/$crash | grep "repro.cprog" | wc -l`
        repro=`echo "$syz_repro + $c_repro" | bc`
        if [[ $repro -gt 0 ]]
        then
                cnt=`echo "$cnt+1" | bc`
        fi
        dumb_ret=$(dumb_filter "$desc" "$repro")
        if [[ $dumb_mode == "dumb" && $dumb_ret -eq "1" ]]
        then
                continue
        fi
        echo -e "\n============= $crash ============="
        echo -e "$desc"
        echo -e "C Repro: ${c_repro}, Syz Repro: ${syz_repro}"
done
```

- TBD

## 2 qemu内复现poc



## 3 qemu+gdb调试内核

**通过qemu启动待调试的内核：**`./debug.sh`

```shell
# debug.sh
# 基于syzkaller的create-image.sh，开启nokaslr
KERNEL=../linux-6.1.12
IMAGE=./stretch.img

qemu-system-x86_64 \
  -kernel $KERNEL/arch/x86/boot/bzImage \
  -append "console=ttyS0 root=/dev/sda nokaslr slub_debug=P kmemleak=on"\
  -hda $IMAGE \
  -net user,hostfwd=tcp::16112-:22 -net nic \
  -enable-kvm \
  -cpu host \
  -nographic \
  -serial mon:stdio \
  -m 1G \
  -s \
  -S \
  -smp 1 \
  -pidfile kernel.debug.pid \
  2>&1 | tee kernel.debug.log
```

相关参数解释：

- `-s`：监听gdb 1234端口
- `-S`：启动后挂起，等待连接（optional）
- `-nographic`：不启动图形界面，与`console=ttyS0`组合使用，将调试信息输出到`ttyS0`

**gdb连接进行调试**（我安装的是gdb 10.2，不存在部分博客中提到需要修改gdb源码再编译的问题）：

```bash
gdb vmlinux --eval-command="target remote tcp::1234"
```

**常用命令**：

- 下软件断点：b，下硬件断点：hb，删除第N个断点：delete N
- 打印源码：l，打印内存：x（`x/nfu addr`”。含义为以`f`格式打印从`addr`开始的`n`个长度单元为`u`的内存值。如 x/16xw）
- 查看堆栈：bt，步进：n，步入：s，继续运行：c

**详细调试技巧请见[gdb调试内核技巧总结]()**

**Trouble shooting**

- `Cannot insert breakpoint 1. Cannot access memory at address 0xffffffff8610ae1b`
  - 用硬件断点`hbreak`而不是软件断点`break`

## 4 Troubleshooting

### 内核编译

- 遇到`FATAL: modpost: vmlinux.o is truncated.`

```bash
  LD      vmlinux.o
  OBJCOPY modules.builtin.modinfo
objcopy: warning: vmlinux.o has a corrupt section with a size (3b7bb65e) larger than the file size
objcopy: warning: vmlinux.o has a corrupt section with a size (59e797c0) larger than the file size
objcopy: warning: vmlinux.o has a corrupt section with a size (20ce7197) larger than the file size
objcopy: warning: vmlinux.o has a corrupt section with a size (168af608) larger than the file size
  GEN     modules.builtin
  GEN     .vmlinux.objs
  MODPOST Module.symvers
FATAL: modpost: vmlinux.o is truncated. sechdrs[i].sh_offset=370247648 > sizeof(*hrd)=64
make[2]: *** [scripts/Makefile.modpost:144: Module.symvers] Error 1
make[1]: *** [/root/kernels/linux-6.5-rc3/Makefile:1984: modpost] Error 2
```

- 原因可能是.config开启了某些较新的模块而编译器版本过低，建议升级编译器（gcc ≥ 12，clang ≥ 15）
