---
title: syzkaller安装与使用
toc: true
date: 2022-10-31 20:09:22
tags: [Kernel, Fuzz]
categories:
	- [Security]
index_img: /image/thumbnails/fantasy-wallpaper-nature.jpg
---

本文记录了我的syzkaller安装**踩坑记录**以及其基本使用方法。

syzkaller是2015年Google提出的一款主要用Go编写的基于覆盖率的内核fuzzer。<!--more-->2017年它的持续性fuzzing平台syzbot部署上线，迄今为止已经挖掘并报告了超过4000个内核漏洞。



> 强烈不建议在虚拟机中安装syzkaller并进行内核fuzz，有条件尽量在配置较好的服务器上进行（亲测orz



配置代理的一个大坑，保证http_proxy等代理对sudo生效（不论当前用户是否为root，都需要加以下内容）

```bash
#/etc/sudoers
Defaults env_keep += "http_proxy https_proxy ftp_proxy"  
```

## 1 安装依赖

- 安装基本软件依赖

```bash
sudo apt update
sudo apt install -y debootstrap qemu qemu-kvm
sudo apt install -y git make build-essential openssh-server
sudo apt install -y libssl-dev libelf-dev
sudo apt install -y flex bison libc6-dev libc6-dev-i386 linux-libc-dev libgmp3-dev libmpfr-dev libmpc-dev 
```

- 安装Go

```bash
cd ~
wget https://dl.google.com/go/go1.17.6.linux-amd64.tar.gz
tar -zxvf go1.17.6.linux-amd64.tar.gz 
export GOPATH=~/go
export GOROOT=~/go
export PATH=$GOPATH/bin:$PATH

# 测试是否安装成功，顺便检查一下go env
go env
```

## 2 安装syzkaller

- 源码编译安装syzkaller

```bash
git clone https://github.com/google/syzkaller.git
cd syzkaller

# 这一步对内存有要求。在虚拟机中分配4G以上内存+4G swap可以成功编译。
make
# make无报错且在syzkaller/bin目录下看到相关二进制即安装成功
```

## 3 编译内核

- 下载内核源码

在 https://mirrors.edge.kernel.org/pub/linux/kernel/ 选择想要测试的内核版本

```bash
wget https://mirrors.edge.kernel.org/pub/linux/kernel/v4.x/linux-4.9.331.tar.xz
tar xvJf linux-4.9.331.tar.xz
```

- 编译内核

```bash
# 生成编译配置
cd linux-4.9.331
make CC="/usr/bin/gcc" defconfig
make CC="/usr/bin/gcc" kvm_guest.config

# 在.config文件追加如下内容
CONFIG_KCOV=y
CONFIG_DEBUG_INFO=y
CONFIG_KASAN=y
CONFIG_KASAN_INLINE=y
CONFIG_CONFIGFS_FS=y
CONFIG_SECURITYFS=y

# 若想避免后续运行syzkaller的时候出现[FAILED] Failed to start Raise network interfaces.的错误，再追加以下两行
CONFIG_CMDLINE_BOOL=y
CONFIG_CMDLINE="net.ifnames=0"

# 补充选项
CONFIG_DEBUG_KMEMLEAK=y
# CONFIG_KCOV_ENABLE_COMPARISONS=y

# 然后使得上述追加内容生效，注意这六个选项的位置不再位于文件尾部
make CC="/usr/bin/gcc" olddefconfig

# 最后开始编译内核（对内存有一定要求，我在虚拟机中编译失败，在台式物理机中5min编译完毕）
make CC="/usr/bin/gcc" -j6

# 期间并无报错且最后看到如下输出即编译成功
# Kernel: arch/x86/boot/bzImage is ready  (#1)
```

- 注：以下是一次运行日志的开头部分，有几个配置提示not enabled，是否有必要加入.config文件

```bash
2022/12/30 03:01:42 code coverage           : enabled
2022/12/30 03:01:42 comparison tracing      : CONFIG_KCOV_ENABLE_COMPARISONS is not enabled
2022/12/30 03:01:42 extra coverage          : enabled
2022/12/30 03:01:42 delay kcov mmap         : enabled
2022/12/30 03:01:42 setuid sandbox          : enabled
2022/12/30 03:01:42 namespace sandbox       : /proc/self/ns/user does not exist
2022/12/30 03:01:42 Android sandbox         : enabled
2022/12/30 03:01:42 fault injection         : CONFIG_FAULT_INJECTION is not enabled
2022/12/30 03:01:42 leak checking           : CONFIG_DEBUG_KMEMLEAK is not enabled
2022/12/30 03:01:42 net packet injection    : /dev/net/tun does not exist
2022/12/30 03:01:42 net device setup        : enabled
2022/12/30 03:01:42 concurrency sanitizer   : /sys/kernel/debug/kcsan does not exist
2022/12/30 03:01:42 devlink PCI setup       : PCI device 0000:00:10.0 is not available
2022/12/30 03:01:42 NIC VF setup            : PCI device 0000:00:11.0 is not available
2022/12/30 03:01:42 USB emulation           : /dev/raw-gadget does not exist
2022/12/30 03:01:42 hci packet injection    : /dev/vhci does not exist
2022/12/30 03:01:42 wifi device emulation   : /sys/class/mac80211_hwsim/ does not exist
2022/12/30 03:01:42 802.15.4 emulation      : /sys/bus/platform/devices/mac802154_hwsim does not exist
2022/12/30 03:01:42 corpus                  : 0 (deleted 0 broken)
2022/12/30 03:01:44 seeds                   : 165/685
```



## 4 创建虚拟机

- 创建image

```bash
# 在创建一个image目录
mkdir image && cd image
cp ../syzkaller/tools/create-image.sh ./
chmod u+x create-image.sh
./create-image.sh

# 看到如下warning是正常现象，说明没有debian-archive-keyring，如果网络正常可以按照其替代方案切换到mirror https://deb.debian.org/debian并继续执行
# I: Keyring file not available at /usr/share/keyrings/debian-archive-keyring.gpg; switching to https mirror https://deb.debian.org/debian

# 也可以通过安装debian-archive-keyring来避免该warning（可选）
sudo apt install -y debian-archive-keyring

# 执行create-image.sh完毕后看到目录有如下内容即为成功
chroot  create-image.sh  stretch.id_rsa  stretch.id_rsa.pub  stretch.img
```

- 安装qemu虚拟机

```bash
# 在当前image目录创建boot.sh，注意路径的指向要正确
qemu-system-x86_64 \
 -kernel ../kernels/linux-4.9.331/arch/x86/boot/bzImage \
 -append "console=ttyS0 root=/dev/sda debug earlyprintk=serial slub_debug=QUZ"\
 -hda ./stretch.img \
 -net user,hostfwd=tcp::10021-:22 -net nic   \
 -enable-kvm \
 -nographic \
 -m 2560M \
 -smp 2 \
 -pidfile vm.pid \
 2>&1 | tee vm.log
 
# 运行boot.sh启动虚拟机，以root用户无密码登录
chmod u+x boot.sh
./boot.sh

# 然后测试qemu虚拟机的ssh服务是否成功启动
ssh -i stretch.id_rsa -p 10021 -o "StrictHostKeyChecking no" root@localhost
```

## 5 运行syzkaller

- 创建syzkaller配置文件

**注意：为了避免出现[FAILED] Failed to start Raise network interfaces.的错误，要在下述配置文件加入"cmdline": "net.ifnames=0"或者在.config中追加CMDLINE相关的两条配置项**

```bash
# 在syzkaller目录下创建workdir
cd syzkaller
mkdir workdir

# 在syzkaller目录下创建my.cfg，各选项均采用绝对路径
{
    "target": "linux/amd64",
    "http": "127.0.0.1:56741",
    "workdir": "/home/zzy/kernel-fuzz/syzkaller/workdir",
    "kernel_obj": "/home/zzy/kernel-fuzz/kernels/linux-4.9.331",
    "image": "/home/zzy/kernel-fuzz/image/stretch.img",
    "sshkey": "/home/zzy/kernel-fuzz/image/stretch.id_rsa",
    "syzkaller": "/home/zzy/kernel-fuzz/syzkaller",
    "procs": 8,
    "type": "qemu",
    "vm": {
        "count": 8,
        "kernel": "/home/zzy/kernel-fuzz/kernels/linux-4.9.331/arch/x86/boot/bzImage",
        "cmdline": "net.ifnames=0",
        "cpu": 2,
        "mem": 2048
    }
}
```

- 开始内核模糊测试

```bash
./bin/syz-manager -config my.cfg

# 如果出现Is another process using the image [/home/zzy/kernel-fuzz/image/stretch.img]?提示
# 则说明之前boot.sh开启的qemu虚拟机尚未关机，进入到该虚拟机执行关机命令poweroff即可
```

- 在 http://localhost:56741/ 查看fuzz的进度和结果

## 6 参考

[[1] Setup: Ubuntu host, QEMU vm, x86-64 kernel](https://github.com/google/syzkaller/blob/master/docs/linux/setup_ubuntu-host_qemu-vm_x86-64-kernel.md)

[[2] 零基础syzkaller挖掘Linux内核漏洞](https://zhuanlan.zhihu.com/p/506059739)

[[3] Syzkaller安装 Fuzz Qemu amd64 Kernel](http://pwn4.fun/2019/05/31/Syzkaller%E5%AE%89%E8%A3%85%20Fuzz%20Qemu%20amd64%20Kernel/)

[[4] syzkaller官方troubleshooting](https://github.com/google/syzkaller/blob/master/docs/linux/troubleshooting.md)