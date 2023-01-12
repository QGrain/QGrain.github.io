---
title: Linux个人工作环境配置
toc: true
date: 2021-04-10 11:21:00
tags: [Linux]
categories:
	- [Configuration]
index_img: /img/logo-linux.jpg
---

# Linux个人工作环境配置（持续更新）

在迁移到新的Linux系统时，可以用以下来配置个人工作环境，以快速恢复效率✨

<!--more-->

> 本文环境：Ubuntu 22.04 LTS

## 1 系统配置

### 配置代理

- 可直接在  Settings > Network > Network Proxy > Manual  图形界面中配置系统代理`http_proxy`和`https_proxy`，注意不只需写ip和port，不需要在ip前加protocol。然后重启`NetworkManager`生效。

```bash
sudo systemctl restart NetworkManager
```

- 或者在命令行中为终端shell配置代理：

```bash
# 仅对当前终端的shell生效
export http_proxy=http://PROXY_IP:PROXY:PORT
export https_proxy=http://PROXY_IP:PROXY:PORT

# 持久化（重启）生效
sudo touch /etc/profile.d/my_proxy.sh
echo "export http_proxy=http://PROXY_IP:PROXY:PORT" >> /etc/profile.d/my_proxy.sh
echo "export https_proxy=http://PROXY_IP:PROXY:PORT" >> /etc/profile.d/my_proxy.sh
sudo chmod +x /etc/profile.d/my_proxy.sh
source /etc/profile.d/my_proxy.sh
# 注: 如果是zsh，则需要注意是否在/etc/zsh/zprofile中添加了相关支持
```

- 验证代理配置成功

```bash
# 打印success即说明配置成功
timeout 5s curl -I google.com && echo success || echo fail
```

- 配置代理的一个大坑：保证http_proxy等代理对sudo生效（不论当前用户是否为root，都需要添加以下内容）

```bash
#/etc/sudoers
Defaults env_keep += "http_proxy https_proxy ftp_proxy"  
```

### 更换镜像源

- 清华源

```bash
deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ jammy main restricted universe multiverse
# deb-src https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ jammy main restricted universe multiverse
deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ jammy-updates main restricted universe multiverse
# deb-src https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ jammy-updates main restricted universe multiverse
deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ jammy-backports main restricted universe multiverse
# deb-src https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ jammy-backports main restricted universe multiverse
deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ jammy-security main restricted universe multiverse
# deb-src https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ jammy-security main restricted universe multiverse
```

- 自动化换源（暂时只支持Ubuntu）

```bash
git clone git@github.com:QGrain/My-Awesome-Configuration.git awesome-conf
pushd awesome-conf/mirror-source
chmod u+x change-mirror-source.sh
./change-mirror-source.sh
popd
```

### 其他的坑

- 再记录一个关于Ubuntu 22 in VMware的坑
  - 安装[open-vm-tools, open-vm-tools-desktop]和vmware-tools是互斥的两个

## 2 软件配置

### 常用软件

```bash
# 常用网络工具
sudo apt install -y net-tools openssh-server curl

# 常用运维工具
sudo apt install -y screen tmux

# 常用系统资源管理工具
sudo apt install -y htop screenfetch # neofetch
```

### Git

- 基本配置

```bash
# 安装git
sudo apt install git

# 配置账号
git config --global user.name "GIT_USERNAME"
git config --global user.email "GIT_EMAIL"

# 配置代理，仅针对https://github.com代理。移除此字段则是全局代理
git config --global http.https://github.com.proxy "socks5://PROXY_IP:PROXY_PORT"
git config --global https.https://github.com.proxy "socks5://PROXY_IP:PROXY_PORT"
```

- 配置`github ssh keys`

```bash
# 生成公私钥对
ssh-keygen -t rsa -C "YOUR_COMMENT"

# 将id_rsa.pub公钥上传至Github > Settings > SSH and GPG keys
# 测试配置是否成功
ssh -T git@github.com

# 注意！若提示需要输入git@github.com's password则说明github.com域名被污染，其解析的ip不正确，需要改/etc/hosts，添加以下一行即可（但是很奇怪的是无法再ping通了）
20.205.243.166 github.com
```

### Vim

- 安装vim

```bash
sudo apt install vim

# 使用个人常用的精简配置
wget -c https://raw.githubusercontent.com/QGrain/My-Awesome-Configuration/master/vim/vimrc -O ~/.vimrc
```

- 配置`Spacevim`？Todo

### Oh-My-Zsh

- 安装与配置

```bash
# 安装zsh
sudo apt install zsh

# 安装ohmyzsh
sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"

# 拉取个人zshrc配置
wget -c https://raw.githubusercontent.com/QGrain/My-Awesome-Configuration/master/zsh/zshrc -O ~/.zshrc

# Source
source ~/.zshrc
```

- 支持`/etc/profile.d/*.sh`脚本

```bash
# 在/etc/zsh/zprofile中追加以下内容
if [ -d /etc/profile.d ]; then
  for i in /etc/profile.d/*.sh; do
    if [ -r $i ]; then
      . $i
    fi
  done
  unset i
fi
```

### neovim

### fzf

```bash
git clone --depth 1 https://github.com/junegunn/fzf.git ~/.fzf
~/.fzf/install
```

### cmake

- 源码安装指定版本

### miniconda3

```bash
# 从官方站点下载速度较慢，可从国内镜像站下载miniconda3-py38 64-bit
wget -c https://mirrors.tuna.tsinghua.edu.cn/anaconda/miniconda/Miniconda3-py38_4.12.0-Linux-x86_64.sh

# 添加当前用户可执行权限
chmod u+x ./Miniconda3-py38_4.12.0-Linux-x86_64.sh

# 执行安装脚本，依照提示完成安装
./Miniconda3-py38_4.12.0-Linux-x86_64.sh

# 添加环境变量
sudo touch /etc/profile.d/my_software.sh
echo "export PATH=$PATH:/home/$USER/miniconda3/bin" >> /etc/profile.d/my_software.sh
source /etc/profile.d/my_software.sh
# 注: 如果是zsh，则需要注意是否在/etc/zsh/zprofile中添加了相关支持
```

## 3 开发配置

### C/C++

- gcc-9/g++-9：由于Ubuntu22自带的gcc版本是11较高，为了兼容部分源码安装gcc-9以及g++-9

```bash
sudo apt install gcc-9 g++-9
# 配置update-alternatives
sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-11 20 --slave /usr/bin/g++ g++ /usr/bin/g++-11 --slave /usr/bin/gcov gcov /usr/bin/gcov-11
sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-9 90 --slave /usr/bin/g++ g++ /usr/bin/g++-9 --slave /usr/bin/gcov gcov /usr/bin/gcov-9
```

- llvm-9 (with gold plugin)：插桩利器

```bash
# 一键安装（预计四十分钟？）
curl https://gitee.com/QGrain/aflgo-build/raw/master/build_llvm_11.sh | bash
```

### Go

### Java

### Rust

### Node