---
title: 【渗透】meterpreter命令详解
toc: true
date: 2019-11-14 20:20:47
tags: [Penetration, Kali]
thumbnail: /image/thumbnails/cyberpunk1.jpg
index_img: /image/thumbnails/cyberpunk1.jpg
categories: [Security]
---

## 1 简介

**meterpreter**是一个为攻击者提供**交互性shell**以便于在目标机器上执行代码的命令行工具。

它通过注入特定的payload到目标机器内存中的DLL，来实现控制。

<!--more-->

## 2 命令

- `help`：获取帮助文档，里面包含了所有的meterpreter的命令的功能

### 2.1 Core Commands

> meterpreter > ?  帮助菜单
>
> meterpreter > background 将当前session置于后台
>
> meterpreter > bgkill kill掉后台某meterpreter的会话
>
> meterpreter > bgrun 以后台线程的身份执行一个meterpreter的脚本
>
> meterpreter > channel 显示或控制所有活动的channel
>
> meterpreter > [read|write] 从channel中读出数据|向channel中写入数据
>
> meterpreter > close 关闭一个channel
>
> meterpreter > [enable|disable]_unicode_encoding
>
> meterpreter > [set|get]_timeouts
>
> meterpreter > [uuid|guid]
>
> meterpreter > info
>
> meterpreter > irb
>
> meterpreter > load
>
> meterpreter > machine_id
>
> meterpreter > migrate
>
> meterpreter > pivot
>
> meterpreter > resource
>
> meterpreter > run
>
> meterpreter > sessions
>
> meterpreter > transport



### 2.2 File System Commands

```shell
meterpreter > cat
meterpreter > cd
meterpreter > checksum
meterpreter > [cp|lcd]
meterpreter > dir
meterpreter > [ls|lls]
meterpreter > [download|upload]
meterpreter > edit
meterpreter > [getlwd|getwd]
meterpreter > [pwd|lpwd]
meterpreter > [mkdir|rmdir]
meterpreter > mv
meterpreter > rm
meterpreter > search
meterpreter > show_mount
```



### 2.3 Networking Commands

- arp
- getproxy
- ifconfig
- ipconfig
- netstat
- portfwd
- resolve
- route



### 2.4 System Commands

- clearev
- drop_token
- execute
- getenv
- getpid
- getprivs
- getsid
- getuid
- kill
- localtime
- pgrep
- pkill
- ps
- reboot
- reg
- rev2self
- shell
- shutdown
- steal_token
- suspend
- sysinfo



### 2.5 User interface Commands

- enumdesktops
- getdesktop
- idletime
- keyscan_[dump|start|stop]
- screenshot
- setdesktop
- uictl



### 2.6 Webcam Commands

- record_mic
- webcam_chat
- webcam_list
- webcam_snap
- webcam_stream



### 2.7 Audio Poutput Commands

- play



### 2.8 Elevate Commands

- getsystem



### 2.9 Password database Commands

- hashdump



### 2.10 Timestomp Commands

- timestomp