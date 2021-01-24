---
title: KVM虚拟机的四种网络模式
toc: true
date: 2020-12-24 22:56:40
tags: [Virtualization]
index_img: /img/kvm.png
---

因为前段时间项目需要在KVM架构的网络靶场中进行演示，于是便学习了KVM虚拟机有关网络架构的知识。

<!--more-->

## 1 KVM简介

 Libvirt虚拟网络使用虚拟网络交换机的概念。虚拟网络交换机是在主机物理机器服务器上运行的软件结构，虚拟机（客户机）通过它连接到该物理主机服务器。客户机的网络流量通过这个交换机被引导： 

<img src="https://gitee.com/QGrain/picgo-bed/raw/master/img/20210121220618.png"/>

 Linux主机物理机服务器将虚拟网络交换机表示为网络接口。当libvirtd守护进程（libvirtd）首次安装并启动时，表示虚拟网络交换机的默认网络接口是virbr0。 

<img src="https://gitee.com/QGrain/picgo-bed/raw/master/img/20210121220706.png"/>

 这个virbr0接口可以通过ip命令，像其他任何接口一样用命令查看 



## 2 网络模式



### 2.1 桥接模式

### 2.2 NAT模式

### 2.3 路由模式

### 2.4 隔离模式



## 3 参考

