---
title: Kali Linux安装Docker
toc: true
date: 2019-01-12 23:10:44
tags: [Kali, Docker]
---

由于计算机网络课设需求，需要部署多个NS-3的Docker，于是我便尝试在我的Kali上安装Docker。本文将主要介绍其安装过程。

<!--more-->

## 1 卸载旧版本

```bash
sudo apt remove docker docker.io docker-engine
```

## 2 使用APT安装

### 2.1 安装依赖

由于APT源使用HTTPS以确保软件下载过程中不被篡改。因此，我们首先需要添加使用HTTPS传输的软件包以及CA证书。

```bash
sudo apt-get update

sudo apt-get install apt-transport ca-certificates curl gnupg2 lsb-release software-properties-common
```

### 2.2 添加软件源

- 为了确认所下载软件包的合法性，需要添加软件源的GPG密钥

```bash
curl -fsSL https://mirrors.ustc.edu.cn/docker-ce/linux/debian/gpg | sudo apt-key add -

# 官方源
# $ curl -fsSL https://download.docker.com/linux/debian/gpg | sudo apt-key add -
```

- 向`sources.list`添加Docker CE的软件源

```bash
# 官方源
sudo echo "deb [arch=amd64] https://download.docker.com/linux/debian stretch stable" | sudo tee -a /etc/apt/sources.list
```

### 2.3 安装Docker CE

更新apt软件包缓存，并安装`docker-ce`

```bash
sudo apt-get update

sudo apt-get install docker-ce
```

## 3 使用脚本自动安装

 在测试或开发环境中 Docker 官方为了简化安装流程，提供了一套便捷的安装脚本，Debian 系统上可以使用这套脚本安装，另外可以通过 `--mirror` 选项使用国内源进行安装： 

```bash
curl -fsSL get.docker.com -o get-docker.sh

sudo sh get-docker.sh --mirror Aliyun
# sudo sh get-docker.sh --mirror AzureChinaCloud
```

## 4 建立docker用户组

 默认情况下，`docker` 命令会使用 [Unix socket](https://en.wikipedia.org/wiki/Unix_domain_socket) 与 Docker 引擎通讯。而只有 `root` 用户和 `docker` 组的用户才可以访问 Docker 引擎的 Unix socket。出于安全考虑，一般 Linux 系统上不会直接使用 `root` 用户。因此，更好地做法是将需要使用 `docker` 的用户加入 `docker` 用户组。 

- 建立`docker`组：

```bash
sudo groupadd docker
```

- 将当前用户加入`docker`组：

```bash
sudo usermod -aG docker $USER
```

退出当前终端并重新登录