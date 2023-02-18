---
title: NeSE丙组CTF月赛Writeup
toc: true
date: 2022-09-25 10:33:16
index_img: /image/thumbnails/ethereum-logo.png
categories:
	- [CTF]
tags: [NeSE, Writeup]
---

NeSE战队丙组月赛Writeup（包括解题和出题）

<!--more-->

🕊🕊🕊，之后的月赛writeup会更新在[乙组CTF月赛writeup]()

# 202209-蓝丙出题

> 看到前几个月的丙组月赛里似乎没有出现过区块链相关的题目，正好最近有了解一些智能合约安全，故参考chainFlag出了(改了)两道入门级合约安全赛题

## 1 EasyCheckin

- 考察对智能合约、钱包账户、转账、以太坊事件等基础概念的理解
- 难度：签到级

### 题目搭建

- git clone [repo]()或者下载[EasyCheckin.tar.gz]()
- 配置`config.py`，设置infura token，port和flag：

```python
# Your need a infura ropsten key to go on
INFURA_ROPSTEN_KEY = "https://ropsten.infura.io/v3/YOUR_KEY"
EasyCheckin_PORT = 31040
EasyCheckin_Flag = "flag{w0w_Y0u_hav3_ch3ck3d_1n}"
```

- build & run 启动题目docker

```Dockerfile
FROM ubuntu:18.04

LABEL maintainer="zhangzhiyu1999@iie.ac.cn"

COPY ./*.py /ctf/
COPY ./*.txt /ctf/
COPY ./*.sol /ctf/

# WORKDIR /ctf

RUN sed -i "s/archive.ubuntu.com/mirrors.aliyun.com/g" /etc/apt/sources.list \ 
&& apt update \
&& apt install -y python3 python3-pip \
&& mkdir -p ~/.pip \
&& echo "" > ~/.pip/pip.conf \
&& echo "[global]" >> ~/.pip/pip.conf \
&& echo "index-url = https://pypi.tuna.tsinghua.edu.cn/simple" >> ~/.pip/pip.conf \
&& ln -s /usr/bin/python3 /usr/bin/python \
&& ln -s /usr/bin/pip3 /usr/bin/pip \
&& python -m pip install --upgrade pip \
&& pip install -r /ctf/requirements.txt

CMD  cd /ctf && python EasyCheckin_server.py

EXPOSE 31040

# sudo docker build -t smartcontract:challenge1 -f ./Dockerfile .
# sudo docker run -p 31040:31040 --name EasyCheckin -d smartcontract:challenge1
```

### 解题过程

1. nc服务器，选择 2 生成deployer账户，记住账户地址和私钥
2. 通过metamask已有账户向deployer转账0.001 ether用于部署题目，然后选择 3 部署题目合约，记住合约地址
3. 选择 1 复制源码，进入remix web IDE编译合约并在deploy页面通过`At Address`导入远程链上题目合约进行交互（或者通过etherscan打开题目合约并且上传源码，接入metamask之后，即可在etherscan进行交互）
4. 调用setCheckinStr函数写入`Welcome to EasyCheckin`，然后调用isCheckin触发pass事件

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2022/202210051143335.png"/>

5. 最后提交触发pass事件的交易哈希，获得**flag{w0w_Y0u_hav3_ch3ck3d_1n}**

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2022/202210051144058.png"/>

## 2 RichOwners

- 考察solidity语言基础、常见智能合约漏洞利用（storage覆盖，整数溢出等）
- 难度：入门级

### 题目搭建

- git clone [repo]()或者下载[RichOwners.tar.gz]()
- 配置`config.py`，设置infura token，port和flag：

```python
# Your need a infura ropsten key to go on
INFURA_ROPSTEN_KEY = "https://ropsten.infura.io/v3/1b670860e6e645fe9f85efd8f75a0e5a"
RichOwners_PORT = 31041
RichOwners_Flag = "flag{St0rage_0verwrit3_and_1nt3ger_und3rfl0w}"
```

- build & run 启动题目，同上`EasyCheckin`

```bash
sudo docker build -t smartcontract:challenge2 -f ./Dockerfile .
sudo docker run -p 31041:31041 --name RichOwners -d smartcontract:challenge2
```

### 解题过程

#### 漏洞原理

**1 未初始化结构体对storage的覆盖**

Solidity语言的变量存储位置分为三种：storage，memory和calldata。storage存储在链上，类似与计算机的硬盘，对合约具备全局可见性；memory和calldata则是存储在临时的内存中，不上链。

solidity语言(低于0.4.25)的变量存储有一个特性，即数组、映射、**结构体**类型的局部变量默认是引用合约的storage，而全局变量默认按照声明顺序存储在storage中。因此，如果这些局部变量未被初始化，则它们将直接指向storage首部，修改未初始化的这些变量即可实现对全局变量覆盖写入。 

| Storage slot index                         | Var                                         |
| ------------------------------------------ | ------------------------------------------- |
| 0（ ⬅ 未初始化的hacker结构体默认指向这里） | owner_1  （hacker.hackeraddress1 覆盖写入） |
| 1                                          | owner_2  （hacker.hackeraddress2 覆盖写入） |
| 2                                          | owner_3                                     |
| 3                                          | owner_4                                     |
| ...                                        | ...                                         |

**2 整数下溢**

很简单，如`uint(1-2)`下溢

**3 以太坊钱包地址生成(碰撞尾部)**

通过这个网站(https://vanity-eth.tk/)可以实现哈希碰撞的钱包地址

#### 漏洞利用

1. nc服务器，选择 2 生成deployer账户，记住账户地址和私钥
2. 通过metamask已有账户向deployer转账0.001 ether用于部署题目，然后选择 3 部署题目合约，记住合约地址
3. 通过https://vanity-eth.tk/生成碰撞尾部地址的钱包账户，记住地址和私钥，导入metamask，然后在[ropsten水龙头](https://faucet.egorfine.com/)向该地址领取10个ropsten test ether
4. 选择 1 复制源码，进入remix web IDE编译合约并在deploy页面通过`At Address`导入远程链上题目合约

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2022/202210051924026.png"/>

5. 调用nothing函数，并且附带1 Finney(也即0.001 ether)的value，利用未初始化的结构体对storage首部的覆盖，使得owner_1被修改为了msg.sender，也即我们自己的账户

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2022/202209170021209.png" style="zoom: 67%;" />

6. 同上理，调用nothing并附带1 ether，覆盖owner_2为msg.sender

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2022/202209170025135.png" style="zoom:62%;" />

7. 直接花 1 ether 买下owner_3所属权

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2022/202209170041506.png" style="zoom:70%;" />

8. 由于msg.sender的地址已经通过网站设计好碰撞了尾部，因此直接附带1 ether调用dead构造整数下溢，然后调用imRich函数买下owner_4

<img src="C:\Users\Zhiyu\AppData\Roaming\Typora\typora-user-images\image-20220917005259333.png" alt="image-20220917005259333" style="zoom:62%;" />

9. 最后调用payforflag函数生成GetFlag事件，并且可以看到合约将3.001 ether归还答题钱包，合 约余额清零

10. 复制payforflag的transaction hash，提交至nc服务器拿到**flag{St0rage_0verwrit3_and_1nt3ger_und3rfl0w}**