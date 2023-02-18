---
title: 攻防世界-MISC新手区-Writeup
toc: true
thumbnail: /image/thumbnails/Icons_CTF-686x428.jpg
index_img: /image/thumbnails/Icons_CTF-686x428.jpg
date: 2020-04-12 00:51:06
tags: [CTF, Writeup]
categories: [CTF]
---

XCTF线上练习网站[攻防世界](https://adworld.xctf.org.cn/)，MISC方向的新手区Writeup。第一次做CTF练习题，第一次写Writeup(/▽＼)

<!--more-->

## this_is_flag

**题目描述：**Most flags are in the form flag{xxx}, for example:flag{th1s_!s_a_d4m0_4la9}

**题目分析：**显然可能的选项只有两个：`flag{xxx}`和`flag{th1s_!s_a_d4m0_4la9}`

```txt
flag{th1s_!s_a_d4m0_4la9}
```

**题目总结：**作为新手区的第一题它还是很友好的，并借此告诉了我们以下两点：

- CTF的flag一般长这样: flag{xxx}，并要注意提交的格式
- CTF的flag往往就在你意想不到的地方

## pdf

**题目描述：**菜猫给了菜狗一张图，说图下面什么都没有。[附件下载地址](https://adworld.xctf.org.cn/media/task/attachments/ad00be3652ac4301a71dedd2708f78b8.pdf)

**题目分析：**顾题思意，菜猫说图下什么都没有，这是本题**明面上的唯一线索**，因此怀疑图片下藏有flag

- 进入Aadobe Acrobat编辑模式，将图片挪开，即可得到flag
- 通过[pandoc](https://github.com/QGrain/Document-Transformer)将pdf转word也可以移开得到flag

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/master/figure/20200411214041.png" width=460>

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/master/figure/20200411215126.png" width=460>

```txt
flag{security_through_obscurity}
```

**题目总结：**正如flag里所说，隐晦的地方往往可能藏有答案，比如图片的下面，甚至是里面（后面会遇到）

## 如来十三掌

**题目描述：**菜狗为了打败菜猫，学了一套如来十三掌。[附件下载地址](https://adworld.xctf.org.cn/media/task/attachments/833e81c19b2b4726986bd6a606d64f3c.docx)

**题目分析：**下载附件后发现是一个docx：

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/master/figure/20200411224232.png" width = 600>

这段蜜汁繁体字让人不由得联想到著名的“与佛论禅”：一个加解密[网站](http://keyfc.net/bbs/tools/tudoucode.aspx):

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/master/figure/20200411224839.png" width=400>

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/master/figure/20200411225124.png" width=400>

按`普渡众生`中的提示，在密文前加入`佛曰：`成功解密，得到以下加密字符串：

```txt
MzkuM3gvMUAwnzuvn3cgozMlMTuvqzAenJchMUAeqzWenzEmLJW9
```

仍然不是flag格式，怀疑仍需要解密，因此尝试常见的几种不需要密钥的加解密，此处联系题目提到的如来十三掌，因此尝试rot13解码：

```txt
zmxhz3tizhnjamhia3ptbmzyzghidmnrawpuzhnrdmjramrzywj9
```

最终通过base64解码得到flag：

```txt
flag{bdscjhbkzmnfrdhbvckijndskvbkjdsab}
```

**题目总结：**

- **吐血体验，正所谓“活久见”，做的题目多了，自然也熟练了**
- 也明白了MISC喜欢结合古典/现代加密方式去隐藏密钥，奇怪的知识增长了.jpg

## give_you_flag

**题目描述：**菜狗找到了文件中的彩蛋很开心，给菜猫发了个表情包。[附件下载地址](https://adworld.xctf.org.cn/media/task/attachments/4b0799f9a4d649f09a882b6b1130bb70.gif)

**题目分析：**下载的附件先尝试一下打开看看是否能得到什么信息，结果发现gif的最后一帧有一张二维码一闪而过，除非你是**多年练就的神手速**，不然是截不到的。

因此得另寻他法，此处推荐一个隐写解析的强力工具[StegSolve](https://github.com/zardus/ctf-tools/tree/master/stegsolve)，通过Frame Browser功能轻松得到二维码：

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/master/figure/frame50.png" align="middle" />

那么下一步的目的就很明确了，补齐二维码的定位标识。下载定位标识的图片，通过[在线ps](https://ps.gaoding.com/)贴上去即可：

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/master/figure/frame50-fixed.png" align=middle>

```txt
flag{e7d478cf6b915f50ab1277f78502a2c5}
```

**题目总结：**

- 只要工具用得好，不愁flag没得找
- 要想做好MISC，害的学好PS

## 坚持60s

**题目描述：**

**题目分析：**

**题目总结：**

## gif

Done, to be completed

## 掀桌子

Done, to be completed

## ext3

Done, to be completed

## stegano

Done, to be completed

## SimepleRAR

Done, to be completed

## base64stego

todo

## 功夫再高也怕菜刀

todo