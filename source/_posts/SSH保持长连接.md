---
title: SSH保持长连接
toc: true
date: 2020-03-27 11:24:08
tags: [Linux, ssh]
categories:
	- [Configuration]
index_img: /img/ssh-logo.png
---

# SSH保持长连接

SSH登录服务器而较长时间不进行操作，连接会由服务器自动断开，导致控制台卡死。为了保持SSH的长连接，有以下两种方法：

<!--more-->

## 1 客户端主动保持连接

- 编辑`/etc/ssh/ssh_config`或者`~/.ssh/config`，追加以下内容

```bash
TCPKeepAlive=yes
# Client每隔 180 秒发送一次KeepAlive请求给Server，然后Server响应从而保持连接
ServerAliveInterval 180
# Client发出请求后，服务器端未响应次数达到3，就自动断开连接。正常情况下，Server基本会响应。
ServerAliveCountMax 3
```

## 2 服务端主动保持连接

- 编辑`/etc/ssh/sshd_config`，追加以下内容

```bash
# Server每隔 180 秒发送一次心跳数据包给Client，然后Client响应从而保持连接
ClientAliveInterval 180
# Server发出请求后，客户端未响应次数达到10，就自动断开连接。正常情况下，Client基本会响应
ClientAliveCountMax 10
```

- **重启ssh服务**以使配置生效

```bash
systemctl restart sshd
```

## 参考文章

[[1] SSH 保持连接](https://www.jianshu.com/p/d68b1bf3fc95)

[[2] SSH 保持连接 （解决Broken pipe）](https://blog.csdn.net/Earl_yuan/article/details/50454032)