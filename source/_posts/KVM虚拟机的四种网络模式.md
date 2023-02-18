---
title: KVM虚拟机的四种网络模式
toc: true
date: 2020-06-24 22:56:40
tags: [Virtualization, KVM]
categories: [Notes]
index_img: /img/kvm.png
---

因为前段时间项目需要在KVM架构的网络靶场中进行演示，于是学习并整理了KVM虚拟机有关网络架构的知识。

<!--more-->

## 1 KVM简介

 Libvirt虚拟网络使用虚拟网络交换机的概念。虚拟网络交换机是在主机物理机器服务器上运行的软件结构，虚拟机（客户机）通过它连接到该物理主机服务器。客户机的网络流量通过这个交换机被引导： 

<img src="https://gitee.com/QGrain/picgo-bed/raw/master/img/20210121220618.png"/>

 Linux主机物理机服务器将虚拟网络交换机表示为网络接口。当libvirt的守护进程（libvirtd）首次安装并启动时，表示虚拟网络交换机的默认网络接口是virbr0。 

<img src="https://gitee.com/QGrain/picgo-bed/raw/master/img/20210121220706.png"/>

 可以通过`ip`或者`ifconfig`查看到此Interface： 

<img src="https://gitee.com/QGrain/picgo-bed/raw/master/img/20210126202816.png"/>

## 2 网络模式

### 2.1 桥接模式

在使用桥接模式时，所有虚拟机都好像与主机物理机器在同一个子网内。同一物理网络中的所有其他物理机器都知道这些虚拟机，并可以访问这些虚拟机。桥接操作在OSI网络模型的第2层。

在中间件（hypervisor ）中可以使用多个物理接口，通过绑定把他们连接在一起，然后把它添加的网桥，同时虚拟机也被添加到这个网桥，绑定操作有多种模式，只有少数这些模式可以与虚拟客户机正在使用的桥接器配合使用。 

<img src="https://gitee.com/QGrain/picgo-bed/raw/master/img/20210126203108.png"/>



### 2.2 NAT模式

默认情况下，虚拟网络交换机以NAT模式运行。他们使用IP伪装而不是SNAT（Source-NAT）或DNAT（Destination-NAT）。IP伪装使得连接的guest虚拟机可以使用主机物理机器IP地址与任何外部网络进行通信。默认情况下，虚拟网络交换机在NAT模式下运行时，放置在主机物理机外部的计算机无法与其中的guest虚拟机进行通信，如下图所示： 

<img src="https://gitee.com/QGrain/picgo-bed/raw/master/img/20210126203149.png"/>

DNS和DHCP
IP地址可以通过DHCP分配给客户机。为此，可以将地址池分配给虚拟网络交换机。Libvirt使用这个dnsmasq程序。dnsmasq的一个实例是由libvirt为每个需要它的虚拟网络交换机自动配置和启动的。 

<img src="https://gitee.com/QGrain/picgo-bed/raw/master/img/20210126204251.png"/>

### 2.3 路由模式

当使用路由模式时，虚拟交换机连接到连接到主机物理机器的物理LAN，在不使用NAT的情况下来回传输流量。虚拟交换机可以检查所有流量，并使用网络数据包中包含的信息来做出路由决策。使用此模式时，所有虚拟机都位于其自己的子网中，通过虚拟交换机进行路由。这种情况并不总是理想的，因为物理网络上的其他主机物理机器不通过手工配置的路由信息是没法发现这些虚拟机，并且不能访问虚拟机。路由模式在OSI网络模型的第三层运行。 

<img src="https://gitee.com/QGrain/picgo-bed/raw/master/img/20210126204315.png"/>

### 2.4 隔离模式

使用隔离模式时，连接到虚拟交换机的虚拟机可以相互通信，也可以与主机物理机通信，但其通信不会传到主机物理机外，也不能从主机物理机外部接收通信。在这种模式下使用dnsmasq对于诸如DHCP的基本功能是必需的。但是，即使该网络与任何物理网络隔离，DNS名称仍然被解析的。因此，DNS名称能解析但ICMP回应请求（ping）命令失败这种情况可能会出现。 

<img src="https://gitee.com/QGrain/picgo-bed/raw/master/img/20210126204331.png"/>

### 2.5 默认模式

libvirtd第一次被安装时，它将包含配置在NAT模式下的初始虚拟网络交换机。使用此配置，以便安装的guest虚拟机可以通过主机物理机与外部网络进行通信。下图显示了这个默认配置libvirtd： 

<img src="https://gitee.com/QGrain/picgo-bed/raw/master/img/20210126204455.png"/>

## 3 参考

 {% note danger %} 

本篇博文有待完善，还需添加自己的实验部分orz

 {% endnote %} 

[[1] kvm虚拟机的四种网络模式](https://blog.csdn.net/gsl371/article/details/78662258?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522160750263919725271090221%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=160750263919725271090221&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~baidu_landing_v2~default-2-78662258.nonecase&utm_term=kvm%E8%99%9A%E6%8B%9F%E6%9C%BA%E7%BD%91%E7%BB%9C&spm=1018.2118.3001.4449)

[[2] KVM虚拟化之四种网络模型](https://blog.csdn.net/ccschan/article/details/88095718?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-13.control&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-13.control)