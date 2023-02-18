---
title: Iptables(1)
toc: true
date: 2019-10-22 21:22:01
thumbnail: /image/thumbnails/iptables.jpg
tags: [Linux, iptables]
categories: [Notes]
index_img: /img/iptables.jpeg
---

## 1 概念

### 1.1 防火墙的概念

- 逻辑上讲，防火墙可以分为主机防火墙和网络防火墙
  1. 主机防火墙：保护单个主机
  2. 网络防火墙：往往处于网络入口，保护入口后面的本地局域网
- 物理上讲，防火墙可以分为硬件防火墙和软件防火墙
  1. 硬件防火墙：在硬件级别实现防护
  2. 软件防火墙：通过软件在硬件平台上实现防护

### 1.2 iptables的概念

iptables是一个linux操作系统中的命令行工具。用户通过iptables能够实施对真正防火墙netfilter的操作，如**网络地址转换，数据包内容修改，数据包过滤**等

<!--more-->

### 1.3 链和表的概念

#### 1.3.1 链

iptables 防火墙一共有五道关卡，五道关卡分别是`PREROUTING`，`INPUT`，`FORWARD`，`OUTPUT`，`POSTROUTING`

根据实际情况的不同，报文会经过不同的关卡，如下图所示：
![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191022194236.png)

防火墙的作用就在于对经过的报文匹配“规则”，然后执行相应的动作。所以当报文流经关卡时，会匹配关卡上的规则。然而在每一道关卡上，规则可能不止一条，于是多条规则按照一定的逻辑顺序串成了一条**链**。如下图所示：
![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191022194443.png)

#### 1.3.2 表

我们把具有相同功能的规则的集合叫做**“表”**，iptables定义了四种表 **(优先级由高到低)**：

- **raw表**：关闭nat表上启用的链接追踪机制；内核模块：iptables_raw
- **mangle表**：拆解，修改，重新封装报文；内核模块：iptables_mangle
- **nat表**：网络地址转换（network address translation）；内核模块：iptables_nat

- **filter表**：负责过滤功能；内核模块：iptables_filter



#### 1.3.3 表与链的关系

不同的链上的规则会根据功能不同而存放在不同的表中，不同的链上能存放的表也有所不同。以下是链与表的关系：

- **PREROUTING**：raw表，mangle表，nat表
- **INPUT**：mangle表，filter表
- **FORWARD**：mangle表，filter表
- **OUTPUT**：raw表，mangle表，nat表，filter表
- **POSTROUTING**：mangle表，nat表

**但是，我们在实际操作iptables的时候，往往是通过“表”作为操作入口，对规则进行定义的。**以下是表与链的关系：

- **raw**：PREROUTING，OUTPUT
- **mangle**：PREROUTING，INPUT，FORWARD，OUTPUT，POSTROUTING
- **nat**：PREROUTING，OUTPUT，POSTROUTING
- **filter**：INPUT，FORWARD，OUTPUT



**数据包流经主机的详细图解**

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191022194815.png)

## 2 iptables的规则管理

### 2.1 基本的规则操作

- **查看规则**：

  - -L：list，显示指定的参数，在后面跟上链的名字可以指定查看的规则链
  - -C：check，查找并显示指定的参数
  - -t：table，指定要匹配的表，（当没有指定时，默认为filter表）
  - -v：verbose，详细信息（多出pkts，bytes，target，prot，opt，in，out等信息）
  - -n：不对ip进行域名反解
  - --line-number：显示规则的序号
  - -x：显示精确的packets和bytes数目

  **最终命令为`iptables`与上述`option`的组合**

- **增加规则**：

  - `-t` + `指定的表` 指定要操作的表
  - `-I` + `指定的规则链`  insert，插入指定的表的规则链首
  - `-A` + `指定的规则链`  append，追加到指定的表的规则链尾
  - `-s`：源地址（default 为 0.0.0.0/0，也即all）
  - `-d`：目的地址（default 为0.0.0.0/0，也即all）
  - `-j`：taget字段，即ACCEPT、REJECT、DROP等动作

- **删除规则**：

  - 根据规则的编号删除：`iptables -t TABLE -D CHAIN seq` 其中TABLE是指定的表，CHAIN是指定的链，seq是要删除的规则的序号。
  - 根据具体的匹配条件与动作删除规则：如 `iptables -D INPUT -s 192.168.1.123 -j ACCEPT`
  - 删除某表的某条链中的所有规则：`iptables -t 表名 -F 链名`，**F** for flush（**慎重！**）
  - 修改规则：`-R 链名 序号 新的匹配条件`，但更建议删除原规则再新建一条规则。

- **保存规则**：

  - centos中，使用service iptables save 即可。规则会默认保存在 /etc/sysconfig/iptables文件中
  - 通用方法：将iptables-save 的流重定向到/etc/sysconfig/iptables 文件
  - 重载规则：iptables-restore < /etc/sysconfig/iptables (重载会覆盖现有的规则)



### 2.2 进阶规则操作—黑白名单

#### 2.2.1 黑名单

- **概念**：除了与名单上匹配的报文禁止通过以外，所有报文皆放行

- **实现方法**：
  我们可以注意到iptables的每一条规则链后面都显示这样的内容：
  ![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191022195523.png)

  `policy 策略` 表示该链对于报文的**默认策略**。所以当默认策略为**ACCEPT**时，链中的规则应该为**REJECT或DROP**，此时之只有匹配到的报文会被拒绝或丢掉，其余的报文皆被放行，从而实现黑名单。

- **命令**：`iptables -P 链名 ACCEPT`



#### 2.2.2 白名单

- **概念**：除了与名单上匹配的报文会放行，所有报文皆被禁止通过
- **实现方法**：将默认策略设置为**REJECT或DROP**，链中的规则设置为**ACCEPT**
- **命令**：`iptables -P 链名 REJECT or iptables -P 链名 DROP`



#### 2.2.3 白名单的改进

- **普通白名单的安全隐患**：当我们将默认策略设置为拒绝时，如果我们 `iptables -F` 清空了规则链，那么所有报文都会被拒绝，如果管理员正在远程ssh操作，此时ssh连接则会直接断开，且目标机器会与外部通信阻隔

- **改进**：将默认策略的拒绝改为接受，然后将**“拒绝所有的报文“**放在规则链的末尾。根据规则链的顺序匹配可知，符合匹配规则的报文放行，否则一律禁止通过

- **命令**：

  ```
  iptables -P 链名 ACCEPT     #更改默认策略
  iptabels -A 链名 -j REJECT  #在链尾追加全部拒绝的规则
  ```



**参考文档：**[iptables防火墙|朱双印博客](https://www.zsythink.net/archives/tag/iptables/)
