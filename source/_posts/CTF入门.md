---
title: CTF入门
toc: true
date: 2019-10-14 22:09:17
thumbnail: /image/thumbnails/solidity_CTF.jpg
tags: [CTF]
categories: [Tutorial]
index_img: /img/ctf.jpg
---

## 1 CTF简介

CTF（Capture The Flag）中文一般译作夺旗赛，在网络安全领域中指的是网络安全技术人员之间进行技术竞技的一种比赛形式。CTF起源于1996年DEFCON全球黑客大会，以代替之前黑客们通过互相发起真实攻击进行技术比拼的方式。发展至今，已经成为全球范围网络安全圈流行的竞赛形式，而DEFCON作为CTF赛制的发源地，DEFCON CTF也成为了目前全球最高技术水平和影响力的CTF竞赛，类似于CTF赛场中的“世界杯” 。

<!--more-->

## 2 CTF比赛模式

### 2.1 解题模式(Jeopardy)

在解题模式CTF赛制中，参赛队伍可以通过互联网或者现场网络参与，这种模式的CTF竞赛与ACM编程竞赛、信息学奥赛比较类似，以解决网络安全技术挑战题目的分值和时间来排名，通常用于在线选拔赛。

不同的是这个解题模式一般会设置 **一血** 、 **二血** 、 **三血** ，也即最先完成的前三支队伍会获得额外分值，所以这不仅是对首先解出题目的队伍的分值鼓励，也是一种团队能力的间接体现。

当然还有一种流行的计分规则是设置每道题目的初始分数后，根据该题的成功解答队伍数，来逐渐降低该题的分值，也就是说如果解答这道题的人数越多，那么这道题的分值就越低。最后会下降到一个保底分值后便不再下降。

题目类型主要包含 **Web 网络攻防** 、 **RE 逆向工程** 、 **Pwn 二进制漏洞利用** 、 **Crypto 密码攻击** 、 **Mobile 移动安全** 以及 **Misc 安全杂项** 这六个类别。

举例，比如 

### 2.2 攻防模式(Attack and Defense)

#### 2.2.1 概述

攻防模式常见于线下决赛。在攻防模式中，初始时刻，所有参赛队伍拥有相同的系统环境（包含若干服务，可能位于不同的机器上），常称gamebox，参赛队伍挖掘网络服务漏洞并攻击对手服务获取 flag 来得分，修补自身服务漏洞进行防御从而防止扣分（一般来说防御只能避免丢分，当然有的比赛在防御上可以得分）

攻防模式CTF赛制可以实时通过得分反映出比赛情况，最终也以得分直接分出胜负，是一种竞争激烈，具有**很强观赏性**和**高度透明性**的网络安全赛制。在这种赛制中，不仅仅是比参赛队员的智力和技术，也比体力（因为比赛一般都会持续48小时及以上），同时也比团队之间的分工配合与合作。

一般比赛的具体环境会在**开赛前一天或者当天开赛前半小时**由比赛主办方给出（是一份几页的小文档）。在这一段时间内，你需要根据主办方提供的文档**熟悉环境并做好防御**。

在比赛开始前半小时，这半小时内是无法进行攻击的，各支队伍都会加紧熟悉比赛网络环境，并做好防御准备。至于敌方 Gamebox 的 IP 地址，则需要靠你自己在给出网段中发现。

如果是分为上午下午两场攻防赛的话，那么上午和下午的 Gamebox 漏洞服务会更换（避免比赛中途休息时选手交流），但管理时要用的 IP 地址等信息不会改变。也就是 **下午会换新题** 。

一般情况下，主办方会提供网线，**但并不会提供网线转接口，所以需要自备。**

#### 2.2.2 基本规则

攻防模式一般的规则如下

- 战队初始分数均为 x 分
- 比赛以 5/10 分钟为一个回合，每回合主办方会更新已放出服务的 Flag
- 每回合内，一个战队的一个服务被渗透攻击成功（被拿 Flag 并提交），则扣除一定分数，攻击成功的战队平分这些分数。
- 每回合内，如果战队能够维护自己的服务正常运行，则分数不会减少（如果防御成功加分则会加分）；
- 如果一个服务宕机或异常无法通过测试，则可能会扣分，服务正常的战队平分这些分。往往服务异常会扣除较多的分数。
- 如果该回合内所有战队的服务都异常，则认为是不可抗拒因素造成，分数都不减少。
- 每回合内，服务异常和被拿 Flag 可以同时发生，即战队在一个回合内单个服务可能会扣除两者叠加的分数。
- 禁止队伍使用通用防御方法
- 请参赛队伍在比赛开始时对所有服务进行备份，若因自身原因导致服务永久损坏或丢失，无法恢复，主办方不提供重置服务
- 禁止对赛题以外的比赛平台发起攻击，包括但不限于在 gamebox 提权 root、利用主办方平台漏洞等，违规者立刻被取消参赛资格
- 参赛队伍如果发现其他队伍存在违规行为，请立刻举报，主办方会严格审核并作出相应判罚。

#### 2.2.3 网络环境

文档上一般都会有比赛环境的 **网络拓扑图** （如下图），每支队伍会维护若干的 **Gamebox（己方服务器）** ，Gamebox 上部署有存在漏洞的服务。

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191014164950.png)

文档上会包括选手，攻防环境，主办方三者的环境。

选手需要在个人电脑上配置或者 DHCP 自动获取

- IP 地址
- 网关
- 掩码 DNS 服务器地址

攻防环境

- Gamebox 所处地址，包括己方和其他队伍的地址。
- 比赛一般会提供队伍的 id 与对应 ip 的映射表，以便于让选手指定恰当的攻防策略。

主办方环境

- 比赛答题平台
- 提交 flag 接口
- 流量访问接口

#### 2.2.4 访问 Gamebox

参赛文档中会给出队伍登录 gamebox 的方式，一般来说如下

- 用户名为 ctf
- 一般会通过 ssh 登录，登录方式为密码或者私钥。

自然，在登录上战队机器后**应该修改所有的默认密码**，同时不应该设置弱密码。

#### 2.2.5 比赛的一些策略 

1. 在比赛过程中，不宜死耗在一道题上，由于一血的优势性，在比赛过程中更应该全面了解赛题难度，先从 **简单题** 开始进行分析，步步为营。
2. 比赛过程中，两极会严重分化。应该着力打击和自己实力相当和比自己队伍更强的队伍，尤其是分数相差无几的情况下，更要严防严守。
3. 比赛中 NPC 会不定时发出攻击流量。从攻击流量中可以得到 payload。
4. 一定要把 NPC 往死里打。
5. 在开赛初可以将所有的管理密码都设置为同一个密码，这样方便队员登录管理。在初期将所有文件备份下来供队内分享。

### 2.3 混合模式(Mix)

结合了解题模式与攻防模式的CTF赛制，比如参赛队伍通过解题可以获取一些初始分数，然后通过攻防对抗进行得分增减的零和游戏，最终以得分高低分出胜负。采用混合模式CTF赛制的典型代表如iCTF国际CTF竞赛。

### 2.4 战争分享模式(Belluminar)

#### 2.4.1 赛制介绍

如官网介绍这样，BELLUMINAR CTF 赛制由受邀参赛队伍相互出题挑战，并在比赛结束后分享赛题的出题思路，学习过程以及解题思路等。战队评分依据出题得分，解题得分和分享得分，进行综合评价并得出最终的排名。

#### 2.4.2 出题阶段

首先各个受邀参赛队伍都必须在正式比赛前出 2 道 Challange。参赛队伍将有 12 周的时间准备 Challenge。出 Challenge 的积分占总分的 30%。

传统的 BELLUMINAR 赛制要求出的两道 Challenge 中一道 Challenge 必须是在 Linux 平台，另外一个 Challenge 则为非 Linux 平台。两个 Challenge 的类型没有做出限制。因此队伍可以尽情展现自己的技术水平。

为使比赛 Challenge 类型比较均衡，也有采用队伍抽签出 Challenge 的方式抽取自己的 Challenge，这要求队伍能力水平更为全面，因此为了不失平衡性，也会将两道 Challenge 的计入不同分值（比如要求其中一道 Challenge 分值为 200，而另外一道分值则为 100）。

#### 2.4.3 提交部署

题目提交截止之前，各个队伍需要提交完整的文档以及解题 Writeup，文档中要求详细标明题目分值，题面，出题负责人，考察知识点列表以及题目源码。而解题 Writeup 中则需要包含操作环境，完整解题过程以及解题代码。

题目提交之后主办方会对题目和解题代码进行测试，如果期间出现问题则需要该题负责人配合以解决问题。最终放到比赛平台上。

#### 2.4.4 解题竞技

进入比赛后，各支队伍可以看到所有其他团队出的题目并发起挑战，但是不能解答本队出的题目，不设 First Blood 奖励，根据解题积分进行排名。解题积分占总分的 60%。

比赛结束后，队伍休息，并准备制作分享 PPT（也可以在出题阶段准备好）。分享会时，各队派 2 名队员上台分享出题解题思路，学习过程以及考察知识点等。

#### 2.4.5 计分规则

出题积分（占总分 30%）有 50% 由评委根据题目提交的详细程度，完整度，提交时间等进评分，另外 50% 则根据比赛结束后的最终解题情况进行评分。计分公式示例： Score = MaxScore -- | N -- Expect＿N | 。N 代表解出该题的队伍数量，而 Expect＿N 则是这道题预期解出的题目数量。只有当题目难度适中，解题队伍数量越接近预期数量 Expect＿N，则这道题的出题队伍得到的出题积分越高。

解题积分（占总积分 60%）在计算时不考虑 First Blood 奖励。

分享积分（占 10%）由评委和其他队伍根据其技术分享内容进行评分得出（考虑分享时间以及其他限制），会计算平均值。

## 3 CTF各大题型简介

### 3.1 MISC(安全杂项)

全称Miscellaneous。题目涉及流量分析、电子取证、人肉搜索、数据分析、大数据统计等等，覆盖面比较广。我们平时看到的社工类题目；给你一个流量包让你分析的题目；取证分析题目，都属于这类题目。主要考查参赛选手的各种基础综合知识，考察范围比较广。

### 3.2 REVERSE(逆向)

题目涉及到软件逆向、破解技术等，要求有较强的反汇编、反编译扎实功底。需要掌握汇编，堆栈、寄存器方面的知识。有好的逻辑思维能力。主要考查参赛选手的逆向分析能力。此类题目也是线下比赛的考察重点。

### 3.3 WEB(web类)

WEB应用在今天越来越广泛，也是CTF夺旗竞赛中的主要题型，题目涉及到常见的Web漏洞，诸如注入、XSS、文件包含、代码审计、上传等漏洞。这些题目都不是简单的注入、上传题目，至少会有一层的安全过滤，需要选手想办法绕过。且Web题目是国内比较多也是大家比较喜欢的题目。因为大多数人开始安全都是从web日站开始的。

### 3.4 PWN(溢出)

PWN在黑客俚语中代表着攻破，取得权限，在CTF比赛中它代表着溢出类的题目，其中常见类型溢出漏洞有栈溢出、堆溢出。在CTF比赛中，线上比赛会有，但是比例不会太重，进入线下比赛，逆向和溢出则是战队实力的关键。主要考察参数选手漏洞挖掘和利用能力。

### 3.5 CRYPTO(密码学)

全称Cryptography。题目考察各种加解密技术，包括古典加密技术、现代加密技术甚至出题者自创加密技术。实验吧“角斗场”中，这样的题目汇集的最多。这部分主要考查参赛选手密码学相关知识点。

### 3.6 STEGA(隐写)

全称Steganography。题目的Flag会隐藏到图片、音频、视频等各类数据载体中供参赛选手获取。载体就是图片、音频、视频等，可能是修改了这些载体来隐藏flag，也可能将flag隐藏在这些载体的二进制空白位置。有时候需要你侦探精神足够的强，才能发现。此类题目主要考查参赛选手的对各种隐写工具、隐写算法的熟悉程度。实验吧“角斗场”的隐写题目是比较全的，以上说到的都有涵盖。新手们可以从此入门。

### 3.7 PPC(编程类)

全称Professionally Program Coder。题目涉及到程序编写、编程算法实现。算法的逆向编写，批量处理等，有时候用编程去处理问题，会方便的多。当然PPC相比ACM来说，还是较为容易的。这部分主要考察选手的快速编程能力。

## 4 CTF分工合作

**常规方向**

- **A方向：PWN + RE + CRYPTO**
- **B方向：WEB + STEGA + MISC**

- MISC，PPC 所有人都需要有一个基本的了解和掌握

## 5 CTF学习参考

### 5.1 知识地图

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191014145659.png)

### 5.2 参考书籍

- A方向：
  - 逆向工程核心原理
  - 恶意代码分析实战
  - RE for Beginners
  - IDA Pro权威指南
  - 加密与解密
- B方向：
  - Web应用安全权威指南

### 5.3 相关网站

#### 5.3.1 学习路线

- [知道创宇研发技能表 v3.1](http://blog.knownsec.com/Knownsec_RD_Checklist/index.html)
- [漏洞银行 (BUGBANK) 技能树](https://skills.bugbank.cn/)
- [安全技能树简版 by 余弦](http://evilcos.me/security_skill_tree_basic/index.html)
- [安全类思维导图 by phith0n](https://github.com/phith0n/Mind-Map)
- [信息安全从业者书单推荐](https://github.com/riusksk/secbook)

#### 5.3.2 在线学习

- [攻防世界](https://adworld.xctf.org.cn): 新手入门推荐的刷题网站，包含CTF题型中的大部分

- [i 春秋 - 专业的网络安全 | 信息安全在线学习培训平台](http://www.ichunqiu.com/)
- [实验吧－让实验更简单！](http://www.shiyanbar.com/)
- [看雪知识库](https://www.kanxue.com/chm.htm)

#### 5.3.3 信息资讯

- [FreeBuf.COM | 关注黑客与极客](http://www.freebuf.com/)
- [安全客 - 有思想的安全新媒体](https://www.anquanke.com/)
- [嘶吼 RoarTalk – 回归最本质的信息安全](http://www.4hou.com/)
- [Sec-News 安全文摘](https://wiki.ioin.in/)
- [互联网安全媒体FreeBuf](https://www.freebuf.com)
- 知道创宇
- 15PB 信息安全教育
- t00ls.net

#### 5.3.4 技术论坛

- [吾爱破解](http://www.52pojie.cn/)
- [看雪论坛](http://bbs.pediy.com/)
- [先知社区](https://xz.aliyun.com/)
- [i 春秋论坛](https://bbs.ichunqiu.com/)

#### 5.3.5 CTF赛事

- [XCTF 社区](https://www.xctf.org.cn/): 全球赛事资讯，积分排名，技术分享
- [CTFtime](https://ctftime.org/)
- [CTF Rank](https://ctfrank.org/)

#### 5.3.6 CTF OJ

- [XCTF OJ](http://oj.xctf.org.cn/)
- [CTF 大本营](https://www.ichunqiu.com/competition)
- [pwnhub](https://pwnhub.cn/index)
- [南邮网络攻防训练平台](http://ctf.nuptsast.com/)
- [HackingLab 网络信息安全攻防学习平台](http://hackinglab.cn/)
- [BugkuCTF](http://ctf.bugku.com/)
- [WeChall](https://www.wechall.net/)
- [Sniper OJ](http://www.sniperoj.com/)
- [Jarvis OJ](https://www.jarvisoj.com/)
- [CTF Learn](https://ctflearn.com/)
- [Hackme CTF](https://hackme.inndy.tw/scoreboard/)
- [Practice CTF List](http://captf.com/practice-ctf/)

#### 5.3.7 本地靶场

- SQLi: <https://github.com/Audi-1/sqli-labs>
- DVWA: <https://github.com/ethicalhack3r/DVWA>
- metsploitable3: <https://github.com/rapid7/metasploitable3/>
- Webgoat: <https://github.com/WebGoat/WebGoat>
- Juiceshop: <https://github.com/bkimminich/juice-shop>

#### 5.3.8 CTF 工具

- [看雪工具](https://tools.pediy.com/)
- [吾爱破解工具](https://down.52pojie.cn/Tools/)
- [CTF 在线工具 by CTFcode](http://ctf.ssleye.com/)
- [CTF 在线工具箱 by bugku](http://tool.bugku.com/)
- [CTF 工具资源库 by HBCTF team](https://ctftools.com/down/)
- [ctf-tools by zardus](https://github.com/zardus/ctf-tools)
- [The Cyber Swiss Army Knife](https://gchq.github.io/CyberChef/)

#### 5.3.9 CTF Writup

- [CTFs Writeup 集锦](https://github.com/ctfs)
- [CTF solution by p4 team](https://github.com/p4-team/ctf)

#### 5.3.10 漏洞复现

Vulhub: <https://github.com/vulhub/vulhub>
