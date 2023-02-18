---
title: 批量窗口管理工具screenctl
toc: true
date: 2023-01-26 21:27:04
index_img: /image/thumbnails/wallhaven-mkm.jpg
categories:
	- [Tools]
tags: [DevOps, Tricks]
---

自己写的一个批量管理linux screen的工具screenctl，支持批量创建删除，命令执行，Web UI管理等功能。（Collaborators wanted）

<!--more-->

好吧，Web UI 还没做出来。🤣

## 1 安装

- INFO：
  - 项目开源：https://github.com/QGrain/screenctl/
  - Pypi主页：https://pypi.org/project/screenctl/

- 安装

```bash
# 安装screen
sudo apt install screen

# 安装screenctl (刚上传pypi不久，可能其他源还没有更新)
pip install screenctl -i https://pypi.org/simple
```

- `screenctl -h` 查看帮助

```bash
usage: screenctl [-h] [-c CONF] [-v] action

screenctl 0.0.4, Controller for screen

positional arguments:
  action                create, delete, stat, server

optional arguments:
  -h, --help            show this help message and exit
  -c CONF, --conf CONF  path to configuration
  -v, --verbose         show verbose output
```

## 2 使用

- 批量创建

```bash
# 批量创建screen并按照指定配置文件执行一条命令（常用于Fuzz）
screenctl create -c job.json

# job.json示例如下: 
{
    "screen_name1": "echo \"name1\" >> name1.log",
    "screen_name2": "timeout 12h /PATH/TO/afl-fuzz -i in -o out PROGRAM ARGS @@",
    "screen_name3": "ping -c 100 baidu.com"
}

# 批量查看screen状态
screenctl stat -c job.json
```

- 批量删除

```bash
# 批量删除job.json中的所有窗口
screenctl delete -c job.json

# TODO: 支持自定义删除
```

- **Web UI (TODO)，抽时间写 🕊🕊🕊**

```bash
# TODO: 启动Web UI来批量管理screen，界面类似于supervisor
# 支持UI界面批量/单独操作screen的启动与删除，以及其中的执行命令和输出回显
screenctl server -c job.json

# 🕊咕子咕子
```

## 3 实现原理

### 批量管理模块

调用`screen`罢了hhh

```bash
# 创建screen
screen -dm NAME

# 在screen中执行命令
screen -x -S NAME -X stuff CMD

# 删除screen
screen -r NAME -X quit
```

### xxxxxxxxxx \documentclass{article}  % 选择模版，使用Latex自带的article模版\author{my name}\title{Title}\usepackage{graphicx}   % 插入图片用到的宏包\usepackage{multirow}   % 插入表格用到的宏包​\begin{document} \maketitle    \section{Hello China} China is in East Asia. ​\subsection{Hello Hubei} Hubei Province is located in central China.​    \subsubsection{Hello Wuhan}         \paragraph{HUST}is Huazhong University of Science and Technology.         \subparagraph{Dian Group} is a student innovation technology team.​    \subsection{Test}        \centering        \begin{tabular}{|c|c|c|c|}         \hline         t & e & s & t\\         \hline         1 & 2 & 3 & 4\\         \hline         \end{tabular}​\end{document} tex

To be completed