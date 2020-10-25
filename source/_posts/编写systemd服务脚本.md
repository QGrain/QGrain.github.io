---
title: 编写systemd服务脚本
toc: true
date: 2020-05-12 11:21:17
tags: [Linux]
categories:
	- [Configuration]
index_img: /img/logo-linux.jpg
---

# 编写systemd服务脚本

如何编写systemd服务脚本来实现服务的自启动，启动，停止和重启管理

<!--more-->

 ## 1 背景介绍

- RHEL6/CentOS6采用`/etc/init.d/xxx`脚本进行服务管理，但是7+版本之后由init管理升级为了由systemd管理，相应地服务管理方式也变更为由systemctl管理的service

- RHEL7/CentOS7的`/etc/rc.d/rc.local`建议创建自己的systemd服务或udev规则来进行开机自启脚本管理，建议如下：

```bash
#!/bin/bash
# THIS FILE IS ADDED FOR COMPATIBILITY PURPOSES
#
# It is highly advisable to create own systemd services or udev rules
# to run scripts during boot instead of using this file.
#
# In contrast to previous versions due to parallel execution during boot
# this script will NOT be run after all other services.
#
# Please note that you must run 'chmod +x /etc/rc.d/rc.local' to ensure
# that this script will be executed during boot.
```

- 因此我们应该顺应时代(工具)的变迁，学习systemd的用法🐕

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/master/figure/bg2016030703.png"/>

## 2 服务脚本写法

- CentOS7的service脚本一般存放在`/etc/systemd/`, `/usr/lib/systemd`路径下，前者包含着多个`*.target.wants`如`multi-user.target.wants`等；而后者为安装软件生成service的目录，一般编写自己的service可以放在此目录下。目录下又有**system**和**user**之分：
	- **/usr/lib/systemd/system/**，系统服务，开机不需要用户登录即可运行的服务
	- **/usr/lib/system/user/**，用户服务，需要用户登录后才能运行的服务
- 每一个服务脚本文件以.service结尾，由三个区块组成: [Unit], [Service]和[Install]，以下是一个编写样例：

```service
[Unit]   
Description=test   		# 简单描述服务
After=network.target    # 描述服务类别，表示本服务需要在network服务启动后在启动
Before=xxx.service      # 表示需要在某些服务启动之前启动，After和Before字段只涉及启动顺序，不涉及依赖关系。

[Service] 
Type=forking     		# 表示后台运行模式。
User=user        		# 设置服务运行的用户
Group=user       		# 设置服务运行的用户组
WorkingDirectory=PATH	# 设置服务运行的路径(cwd)
KillMode=control-group  # 定义systemd如何停止服务
Restart=no        		# 定义服务进程退出后，systemd的重启方式，默认是不重启
ExecStart=/start.sh    	# 服务启动命令，命令需要绝对路径
   
[Install]   
WantedBy=multi-user.target  # 多用户
```

- 完成service脚本编写后，需要执行以下命令以重启生效：

```bash
# 重新加载所有的systemd服务
sudo systemctl daemon-reload

# 管理服务 [使能开启启动|启动|停止|重启|查看状态]
sudo systemctl [enable|start|stop|restart|status] xxx.service
```

## 3 区块参数解释

### [Unit]区块：启动顺序与依赖关系

**服务描述：**

- Description：给出当前服务的简单描述
- Documentation：给出文档位置

**启动顺序：**

- After：定义xxx.service应该在哪些target或service服务之后启动
- Before：定义xxx.service应该在哪些target或service服务之前启动

**依赖关系：**

- Wants：表示xxx.service与定义的服务存在“弱依赖”关系，即指定的服务启动失败或停止运行不影响xxx的允行
- Requires：则表示"强依赖"关系，即指定服务启动失败或异常退出，那么xxx也必须退出；反之xxx启动则指定服务也会启动

### [Service]区块：启动行为定义

**启动命令：**

- EnvironmentFile：指定当前服务的环境参数文件(路径)，如`EnviromentFile=-/etc/sysconfig/xxx`，连词号表示抑制错误，即发生错误时，不影响其他命令的执行
- Environment：后面接多个不同的shell变量，如Environment=DATA_DIR=/dir/data
- User：设置服务运行的用户
- Group：设置服务运行的用户组
- WorkingDirectory：设置服务运行的路径
- Exec*：各种与执行相关的命令
	- ExecStart：定义启动服务时执行的命令
	- ExecStop：定义停止服务时执行的命令 
	- ExecStartPre：定义启动服务前执行的命令 
	- ExecStartPost：定义启动服务后执行的命令
	- ExecStopPost：定义停止服务后执行的命令
	- ExecReload：定义重启服务时执行的命令 

**启动类型：**

- Type：字段定义启动类型，可以设置的值如下
	- simple（默认值）：`ExecStart`字段启动的进程为主进程
	- forking：`ExecStart`字段将以`fork()`方式启动，此时父进程将会退出，子进程将成为主进程
	- oneshot：类似于`simple`，但只**执行一次**，Systemd 会等它执行完，才启动其他服务
	- dbus：类似于`simple`，但会等待 D-Bus 信号后启动
	- notify：类似于`simple`，启动结束后会发出通知信号，然后 Systemd 再启动其他服务
	- idle：类似于`simple`，但是要等到其他任务都执行完，才会启动该服务。一种使用场合是为让该服务的输出，不与其他服务的输出相混合
- RemainAfterExit：设为`yes`，表示进程退出以后，服务仍然保持执行

**重启行为：**

- KillMode：定义 Systemd 如何停止服务，可以设置的值如下
	- control-group（default）：当前控制组里面的所有子进程，都会被杀掉
	- process：只杀主进程
	- mixed：主进程将收到 SIGTERM 信号，子进程收到 SIGKILL 信号
	- none：没有进程会被杀掉，只是执行服务的 stop 命令
- Restart：定义了服务退出后，Systemd 的重启方式，可以设置的值如下（对于守护进程，推荐设为`on-failure`。对于那些允许发生错误退出的服务，可以设为`on-abnormal`）
	- no（default）：退出后不会重启
	- on-success：只有正常退出时（退出状态码为0），才会重启
	- on-failure：非正常退出时（退出状态码非0），包括被信号终止和超时，才会重启
	- on-abnormal：只有被信号终止和超时，才会重启
	- on-abort：只有在收到没有捕捉到的信号终止时，才会重启
	- on-watchdog：超时退出，才会重启
	- always：不管是什么退出原因，总是重启
- RestartSec：表示 Systemd 重启服务之前，需要等待的秒数

### [Install]区块：服务安装定义

- WantedBy：表示该服务所在的 Target

Target的含义是服务组，如`WantedBy=multi-user.target`指的是该服务所属于`multi-user.target`。当执行`systemctl enable xxx.service`命令时，`xxx.service`的符号链接就会被创建在`/etc/systemd/system/multi-user.target`目录下。

可以通过`systemctl get-default`命令查看系统默认启动的target，一般为`multi-user`或者是`graphical`。因此配置好相应的WantedBy字段，可以实现服务的开机启动。

## 4 参考文章

[[1]【阮一峰的网络日志】Systemd 入门教程：实战篇](https://www.ruanyifeng.com/blog/2016/03/systemd-tutorial-part-two.html)

[[2]  Centos7 自定义systemctl服务脚本](https://www.cnblogs.com/wang-yc/p/8876155.html)

[[3]  编写systemd下服务脚本](https://blog.csdn.net/u010127879/article/details/38018825)