---
title: KVM的安装与使用
toc: true
date: 2020-05-25 21:58:44
tags: [KVM, Virtualization]
categories: [Tutorial]
index_img: /img/kvm.png
---

 基于内核的虚拟机（Kernel-based Virtual Machine，简称KVM），是一种用于Linux内核中的虚拟化基础设施。KVM目前支持Intel VT及AMD-V的原生虚拟技术 

<!--more-->

## 1 KVM简介

- **一些概念**：
  -  是x86架构且硬件支持虚拟化技术（如 intel VT 或 AMD-V）的Linux全虚拟化解决方案
  -  它包含一个为处理器提供底层虚拟化 可加载的核心模块kvm.ko（kvm-intel.ko或kvm-AMD.ko） 
  -  KVM还需要一个经过修改的QEMU软件（qemu-kvm），作为虚拟机上层控制和界面 
  -  在主流的Linux内核，如2.6.20以上的内核均已包含了KVM核心

- **QEMU-KVM**：
  -  在Linux系统中，仅加载了KVM内核模块是不够的，用户无法直接控制内核模块去做事情，还必须有一个用户空间的工具。因此KVM的开发者选择了已成型的开源虚拟化软件QEMU，形成可控制KVM内核模块的用户空间工具QEMU-KVM
- **libvirt**：
  -  Libvirt 是一套提供了多种语言接口的API，为各种虚拟化工具提供一套方便可靠的编程接口，不仅支持 KVM，而且支持 Xen 等其他虚拟机。Libvirt 不仅提供了 API，还自带一套基于文本的管理虚拟机的命令 virsh，可通过使用 virsh 命令来使用 libvirt 的全部功能

## 2 KVM安装

### 2.1 检查虚拟化支持

KVM是面向具备`Intel VT`和`AMD-V`虚拟化硬件的技术，因此首先需要check你的系统是否支持虚拟化：

```bash
egrep '(vmx|svm)' /proc/cpuinfo
```

能看到`flag *******`的输出即表明支持虚拟化，其中vmx对应Inter VT，svm对应ADM-V

### 2.2 安装相关软件包

理论上KVM的最小安装就是`qemu-kvm`和`virtinst`，但考虑到管理上的便捷性，我们需要libvirt-daemon作为守护进程和virt-manager的GUI管理界面，以及`brctl`：

```bash
# 更新镜像源（如果有必要）
sudo apt-get update

# 安装相关软件包
sudo apt install qemu qemu-kvm virtinst virt-manager bridge-utils
```

管理`libvirtd.service`

```bash
# systemd管理
sudo systemctl [start|stop|restart|enbale|disable|status] libvirtd
```

## 3 KVM使用

 {% note primary %} 

所有的KVM的管理都可以从`virt-manager`图形化界面来操作，因此下面主要介绍命令行接口

 {% endnote %} 

### 3.1 创建KVM虚拟机

### 3.2 管理KVM虚拟机

### 3.3 配置虚拟机网络

 {% note danger %} 

To be completed... : )

 {% endnote %} 

## 4 参考

[[1] 基于内核的虚拟机(KVM) 维基百科](https://zh.wikipedia.org/zh-hans/%E5%9F%BA%E4%BA%8E%E5%86%85%E6%A0%B8%E7%9A%84%E8%99%9A%E6%8B%9F%E6%9C%BA)

[[2] kvm 虚拟化概述及 virt-manager 安装虚拟机](https://my.oschina.net/u/2343310/blog/1498463)

[[3] Debian 9 安装 KVM 虚拟机](https://it.ismy.fun/2018/09/18/debian-9-kvm/)

