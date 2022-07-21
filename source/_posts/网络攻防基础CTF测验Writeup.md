---
title: 网络攻防基础CTF测验Writeup
toc: true
date: 2022-05-23 22:08:22
index_img: /image/thumbnails/Icons_CTF-686x428.jpg
tags: [CTF, Writeup]
---

国科大2022年春季学期《网络攻防基础》课程CTF测验writeup。

<!--more-->

> 《网络攻防基础》是网络空间安全学科研究生的专业核心课，主讲教师有国家计算机网络入侵防范中心主任张玉清教授、信工所六室龚晓锐高级工程师和信工所国重吴槟副研究员。本课程讲授软件安全的基本原理、软件防御机制与攻击手段的博弈演进、软件脆弱性（漏洞）原理分析，以及确保软件安全性的最佳实践方法。课程以软件安全国际知名学者Gary McGraw的三部著作为教材，让同学通过课程学习与动手实践，深入理解软件的安全内构(building security in)本质与方法，提升在开发过程中确保软件安全性的专业技能；同时，理解软件的漏洞利用与防范在网络空间攻防对抗中的关键地位，掌握围绕软件攻防的“白帽”与“黑帽”思维方法和基本技术，为进一步研习网络攻防奠定基础。

本次CTF测验共分9个小组，每组各出一题，其中我们组出的是一道docker逃逸的misc题。

## Crypto RSA

### 1 题目分析

> **题目：**
>
> - [cipher.txt](/download/course-CTF/cipher.txt)：密文文件
> - [rsa.py](/download/course-CTF/rsa.py)：代码文件

阅读题目rsa.py代码文件可以得到以下信息：

- 公钥（n, e）已知，而p、q、d未知
- 明文是一张bmp图片的txt格式，经过libns2n转换之后加密为密文cipher.txt

### 2 解题过程

#### 破解私钥d

由于n不太大，尝试直接分解。在[factordb在线分解得到的结果](http://www.factordb.com/index.php?query=12928016222583163285621599577461443538921550432522968254134024525052961389976575215720661492239277563694030199496398953014500031012932214275965402552478971)并没有成功，因此转而尝试yafu分解。

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2022/202206232242645.png" style="zoom: 50%;" />

成功得到p和q，然后使用gmpy2求模逆元得到d：

```python
# e = 65537
# p = 113701434566953302296018327231919974281008636789018283600308658084922136632147
# q = 113701434566953302296018327231919974281008636789018283600308658084922136631993
d = gmpy2.invert(e, (p-1) * (q-1))
# d = 1766291671224035950065700331363806177388399874464968761882845653559427747468578334624297389288081148192811346008992354282683301619956371502784830383326817
```

#### 解密明文m

现已知密钥（n，d），直接逐行对`cipher.txt`进行解密即可得到`flag.bmp.txt`如下：

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2022/202206232254070.png" style="zoom:70%;" />

结合`42 4D`文件头以及文件名中的提示，还原得到`flag.bmp`如下：

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2022/202206232257469.png" style="zoom:60%;" />

看到三个二维码定位角，使用**StegSolve**在红色通道Red Plane 0看到二维码，扫码即得到了flag。

> **题解：**
>
> - [rsa-solution.py](/download/course-CTF/rsa-solution.py)，解题脚本
> - [Crypto-RSA.pptx](/download/course-CTF/Crypto-RSA.pptx)，题目讲解

## Reverse babyandroid

> **题目：**
>
> - [babyAndroid.apk](/download/course-CTF/babyAndroid.apk)：题目文件

### 1 逆向分析

直接用**jadx**反编译`babyAndroid.apk`，看到主要代码在`com.example.myapplication`里：

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2022/202206252334586.png"/>





> **题解：**
>
> - [bbandroid-solution.java](/download/course-CTF/bbandroid-solution.java)：解题脚本

## Reverse baguatu

> **题目文件：**
>
> - [cipher.txt](/download/course-CTF/cipher.txt)：密文文件
> - [rsa.py](/download/course-CTF/rsa.py)：代码文件





> **题目文件：**
>
> - [cipher.txt](/download/course-CTF/cipher.txt)：密文文件
> - [rsa.py](/download/course-CTF/rsa.py)：代码文件



## Web news

SQL数字型注入+md5校验绕过+assert命令执行的一道缝合怪，没啥难度。由于我写博客的时候已经是课程实验之后的第N天（老拖延症）服务器都关了，所以我就只挑其中两个手上有图的知识点记录一下。

### 1 md5校验绕过



### 2 assert命令执行



## Misc 密不透风的Docker

> **题目文件：**
>
> - [cipher.txt](/download/course-CTF/cipher.txt)：密文文件
> - [rsa.py](/download/course-CTF/rsa.py)：代码文件





> **题目文件：**
>
> - [cipher.txt](/download/course-CTF/cipher.txt)：密文文件
> - [rsa.py](/download/course-CTF/rsa.py)：代码文件

