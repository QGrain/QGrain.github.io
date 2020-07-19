---
title: 【CTF例题分析】MISC杂项(一)
toc: true
date: 2019-10-15 23:04:28
tags: CTF
index_img: /img/ctf.jpg
---

CTF例题分析系列，MISC杂项方向(一)。本篇文章主要讲解三道从易到难的MISC方向的题目。目的在于让大家能够了解CTF的MISC类型的冰山一角。QAQ

# 1 一道练手的题目 <img src = "https://raw.githubusercontent.com/QGrain/picBed/master/img/star_icon.png" width = 30>

> 今天是菜小狗的生日，他收到了一个Linux系统光盘。
>
> - 要求拿到flag，提交格式为`flag{The_String_of_Flag}`
> - [附件下载地址](https://adworld.xctf.org.cn/media/task/attachments/630a886233764ec2a63f305f318c8baa)

<!--more-->

下载附件，发现该文件名很长，首先怀疑是否flag藏在文件名里***（后觉此想法极其稚嫩）***

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015171511.png)

**发现无法解码，便老老实实按照题目所期望的方向解题**

1. 查看文件类型，发现是一个`Linux rev 1.0 ext3 filesystem data` 类型的文件。

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015164116.png)

2. （WSL无法正常使用mount）尝试挂载该文件系统，但是得到`is not a block device` 错误提示

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015172410.png)

3. 使用`mount -o loop`  挂在环回文件系统到`./mnt/` 目录![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015172741.png)
4. 查找flag相关的文件，可以看到有一个名为`flag.txt` 的文件，查看其内容。

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015184534.png)

5. 字符串各个字符均为ASCII码可打印字符，且字符串以 `=` 结尾，怀疑是Base64编码，于是解码得到最终flag

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015184920.png)

**注：关于上面提到Base64编码的猜测，可以参考我这一篇笔记 [Base编码家族]([https://qgrain.github.io/2019/10/15/Base%E7%BC%96%E7%A0%81/](https://qgrain.github.io/2019/10/15/Base编码/))**



# 2 一道有点难度的题目 <img src = "https://raw.githubusercontent.com/QGrain/picBed/master/img/star_icon.png" width = 30><img src = "https://raw.githubusercontent.com/QGrain/picBed/master/img/star_icon.png" width = 30>

> 今天菜小狗为了打败菜小猫，学了一套如来十三掌
>
> - 要求拿到flag，提交格式为`flag{The_String_of_Flag}
> - [附件下载链接](https://adworld.xctf.org.cn/media/task/attachments/26b2be68dfb841b9914e97315505effb.docx)

下载附件发现是一个docx文档，其中有一段佛语...

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015225149.png)

1. 对于**与佛论禅系列**的加解密，我暂时没有深入了解，且先使用[蓝色的风之精灵](http://weibo.com/selphy/)制作的[与佛论禅加解密在线工具](http://www.keyfc.net/bbs/tools/tudoucode.aspx)将该密文解密

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015225556.png)

得到依旧是加过密的字符串`MzkuM3gvMUAwnzuvn3cgozMlMTuvqzAenJchMUAeqzWenzEmLJW9`

2. 由于每一个字符均为base64字符集的可打印字符，所以猜测是否是经过Base64加过密的。然而使用Base家族解密工具根本无法成功解密。说明应该由另一种加密算法加过密的。**由于加密串仍然为ASCII可编码打印的常规字符，遂尝试rot-13加解密算法**

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015225918.png)

3. 编写脚本，解密该字符串，注意rot-13加密算法无论加还是减结果都一样。

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015230007.png)

4. 最后送给Base64解码，得到flag

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015230129.png)



# 3 一道有点复杂的题目<img src = "https://raw.githubusercontent.com/QGrain/picBed/master/img/star_icon.png" width = 30><img src = "https://raw.githubusercontent.com/QGrain/picBed/master/img/star_icon.png" width = 30><img src = "https://raw.githubusercontent.com/QGrain/picBed/master/img/star_icon.png" width = 30>

题目附件: [链接](http://ctf5.shiyanbar.com/stega/hell/欢迎来到地狱.zip)

> 这是一道特别绕的题目

1. 解压附件，得到 **欢迎来到地狱** 的文件夹

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015191108.png)

2. 首先打开**地狱伊始.jpg**，发现图片格式不正确

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015191232.png)

3. 用winhex打开该图片的字节码，发现文件头部有缺失，缺少文件的前4个字节信息

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015192538.png)

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015192222.png)

在文件头部添加4个字节的内容 `FFD8 FFE0`, 图片修复成功，得到以下内容：

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015192044.png)

4. 下载网盘资源文件：`地狱之声.wav`

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015192859.png)

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015194913.png)

5. 根据波形图的变化，猜测是摩尔斯电码`-.- . -.-- .-.. . - ..- ... --. --- `

译码得到`keyletusgo`

6. 打开**第二层地狱.docx**，有一张图片和一些文字，但是看不出来有什么有价值的信息。于是打开word设置中的显示隐藏文字，得到提示**“图片采用了隐写技术”**

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015210549.png)

![1571144719692](C:\Users\zhiyu\AppData\Roaming\Typora\typora-user-images\1571144719692.png)

7. 使用隐写术加解密在线工具aTool，得到压缩文件的解压密码

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015212504.png)

8. 压缩文件夹打开后得到一张图片和一个文本文档，文本文档中的bit序列直接ASCII编码得到字符串**"ruokouling"**

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015213006.png)![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191015212829.png)

**迷惑！此题的后续文件似乎丢失了，所以这一题的分析就到这里。**

**这一道题并不是很官方，但也能以此看出CTF的题目有时需要很大的脑回路。**

***望诸君共勉***

