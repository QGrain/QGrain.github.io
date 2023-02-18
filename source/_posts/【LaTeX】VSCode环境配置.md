---
title: 【LaTeX】VSCode环境配置
toc: true
date: 2020-01-23 23:06:29
tags: [LaTeX, VSCode]
categories: [Configuration]
index_img: /img/latex-logo-1.png
---

LaTeX + VSCode，新姿势增长了，奇怪的知识也增加了！

<!--more-->

## 1 安装TeXlive

- 前往[官网](https://www.tug.org/texlive/)下载安装程序：
  - [Windows安装包](http://mirror.ctan.org/systems/texlive/tlnet/install-tl-windows.exe)
  - [Unix安装包](http://mirror.ctan.org/systems/texlive/tlnet/install-tl-unx.tar.gz)
  - [MacTeX安装包](https://www.tug.org/mactex/mactex-download.html)
- 前往国内镜像站下载：[华中大镜像站](http://mirrors.hust.edu.cn/CTAN/systems/texlive/Images/)， [清华大学镜像站](https://mirrors.tuna.tsinghua.edu.cn/CTAN/systems/texlive/Images/)， [中科大镜像站](https://mirrors.ustc.edu.cn/CTAN/systems/texlive/Images/)

- **添加环境变量**，并确认可用性

```bash
λ tex -v

TeX 3.14159265 (TeX Live 2019/W32TeX)
kpathsea version 6.3.1
Copyright 2019 D.E. Knuth.
There is NO warranty.  Redistribution of this software is
covered by the terms of both the TeX copyright and
the Lesser GNU General Public License.
For more information about these matters, see the file
named COPYING and the TeX source.
Primary author of TeX: D.E. Knuth.
```



## 2 配置VSCode

### 2.1 安装LaTeX-Workshop插件

![](https://raw.githubusercontent.com/QGrain/picgo-bed/master/figure/20200304155204.png)

### 2.2 设置VSCode配置文件

`settings.json`

```JSON
{
	"latex-workshop.latex.recipes": [
        {
            "name": "lualatex->bibtex->lualatex*2",
            "tools": [
                "lualatex",
                "bibtex",
                "lualatex",
                "lualatex",
            ]
        }
    ],
    "latex-workshop.latex.tools": [
        {
            "name": "lualatex",
            "command": "lualatex",
            "args": [
                "-synctex=1",
                "-interaction=nonstopmode",
                "-file-line-error",
                "%DOCFILE%"
            ]
        },
        {
            "name": "bibtex",
            "command": "bibtex",
            "args": [
                "%DOCFILE%"
            ]
        },
    ],
    "latex-workshop.view.pdf.viewer": "tab",
    # "latex-workshop.latex.autoBuild.run": "never"
}
```

### 2.3 使用说明

- 当你在VSCode编辑`.tex`格式的文件时，LaTeX-Workshop插件会开始工作

- 编译生成pdf的指令为`Ctrl + Alt + B`

- `Ctrl + S`保存文件的同时会默认执行编译，若想去除此机制，可以在json设置文件中的加入：

  ```json
  	"latex-workshop.latex.autoBuild.run": "never"
  ```

## 3 用LaTeX编译第一个pdf

新建`hello.tex`文件

```tex
\documentclass{article}  % 选择模版，使用Latex自带的article模版
\author{my name}
\title{Title}
\usepackage{graphicx}   % 插入图片用到的宏包
\usepackage{multirow}   % 插入表格用到的宏包

\begin{document} 
\maketitle    
\section{Hello China} China is in East Asia. 

\subsection{Hello Hubei} Hubei Province is located in central China.

    \subsubsection{Hello Wuhan} 
        \paragraph{HUST}is Huazhong University of Science and Technology. 
        \subparagraph{Dian Group} is a student innovation technology team.

    \subsection{Test}
        \centering
        \begin{tabular}{|c|c|c|c|} 
        \hline 
        t & e & s & t\\ 
        \hline 
        1 & 2 & 3 & 4\\ 
        \hline 
        \end{tabular}

\end{document} 
```
