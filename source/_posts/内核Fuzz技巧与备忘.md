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

## 常用脚本

- 查看crashs目录结果

```shell
#!/bin/bash

set -e

print_help() {
	echo -e "Usage: ./get_result.sh /path/to/crashs_dir"
}

if [[ ! -n "$1" ]]
then
	print_help
else
    ls $1 | while read crash
    do
        echo -e "\n======== $crash ========"
        desc=`cat $1/$crash/description`
        echo -e "$desc"
        repro=`ls $1/$crash | grep "repro.prog" | wc -l`
        echo -e "Repro: $repro"
    done
    echo -e "\nDone!"
fi
```

- TBD

## qemu+gdb调试内核

**通过qemu启动待调试的内核：**

```shell
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

**Trouble shooting**

- `Cannot insert breakpoint 1. Cannot access memory at address 0xffffffff8610ae1b`
  - 用硬件断点`hbreak`而不是软件断点`break`
