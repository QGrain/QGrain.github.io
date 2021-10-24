---
title: 搭建Jupyter-Notebook服务器
toc: true
date: 2020-06-02 16:39:01
tags: [Selfhosted, Server]
index_img: /img/jupyter-welcome.png
---

在自己的云服务器上搭建Jupyter-Notebook服务器有以下几个好处：

<!--more-->

- 充分利用云服务器的计算和存储资源
- 统一代码环境和数据集（在不同终端机器上构建的环境是可能不一致的）
- 能够随时随地访问（只要ECS还没过期&你的机器能上网）

## 1 环境搭建

### 1.1 安装Python

虽然阿里云的Ubuntu 18.04 云服务器是自带python2.7和python3.6的，但我还是选择**安装miniconda3[(官网)](https://docs.conda.io/en/latest/miniconda.html)**，理由如下：

- anaconda便于管理**多版本且隔离**的python环境（除此以外virtualenv和pyvenv也可，但管理方式不如conda）
- miniconda比anaconda**轻量**，它仅包含**conda和python**和少量依赖包与软件包

```bash
# 从官方站点下载速度较慢，可从国内镜像站下载miniconda3-py38 64-bit
wget -c https://mirrors.tuna.tsinghua.edu.cn/anaconda/miniconda/Miniconda3-py38_4.9.2-Linux-x86_64.sh

# 添加当前用户可执行权限
chmod u+x ./Miniconda3-py38_4.9.2-Linux-x86_64.sh

# 执行安装脚本，依照提示完成安装
./Miniconda3-py38_4.9.2-Linux-x86_64.sh

# 添加环境变量(非zsh用户)，若你正在使用zsh请将/etc/profile替换为~/.zshrc
sudo echo "export PATH=$PATH:/home/$USER/miniconda3/bin" >> /etc/profile & source /etc/profile
```

完成miniconda3的安装之后，需要使用`update-alternatives`配置**多版本Python**的优先级，将conda的python3.8设为默认python：

```bash
# 查看是否已经建立了python的alternative管理，若无则创建
sudo update-alternatives --config python

# 依次创建自带python2，python3以及miniconda3的python3的alternatives管理，最后的数字为优先级，越大越高
sudo update-alternatives --install /usr/bin/python python /usr/bin/python2.7 1
sudo update-alternatives --install /usr/bin/python python /usr/bin/python3.6 2
sudo update-alternatives --install /usr/bin/python python /home/$USER/miniconda3/bin/python3 3
```

### 1.2 安装Jupyter

在命令行中输入`python -V`和`pip -V` 确认能够看到正在使用miniconda3的python和pip

```bash
# pip换国内源以提速，在~/.pip/pip.conf中添加以下内容，文件不存在则创建
[global]
index-url = https://pypi.tuna.tsinghua.edu.cn/simple

# 安装jupyter
pip install jupyter
```

## 2 配置Jupyter-Notebook

### 2.1 配置登陆密码

为了保障远程访问云服务器Jupyter服务的安全性，我们需要为notebook设定密码。（该密码会在登陆Jupyter服务时要求用户输入，若与服务器上存储的hash值匹配则放行）

以下是借助`IPython.lib`中的`passwd`函数来生成密码的sha1 code：

```ipython
Python 3.8.5 (default, Sep  4 2020, 07:30:14)
[GCC 7.3.0] :: Anaconda, Inc. on linux
Type "help", "copyright", "credits" or "license" for more information.
>>> from IPython.lib import passwd
>>> passwd()
Enter password:
Verify password:
'sha1:..............YOUR-HASH-CODE....................'
>>>
```

### 2.2 生成配置文件

```bash
# 生成 /home/$USER/.jupyter/jupyter_notebook_config.py 配置文件
jupyter notebook --generate-config
```

由于该配置文档的所有内容均默认注释，因此可以把我们自己的配置集中追加到文尾：

```python
#--------------------------------------------------------------------------
# User configuration
#--------------------------------------------------------------------------
c.NotebookApp.ip = '*'                               # notebook监听的IP
c.NotebookApp.port = 8888  							 # notebook监听的端口
c.NotebookApp.notebook_dir = '/path/to/project-dirs' # notebook的工作目录
c.NotebookApp.open_browser = False                   # 运行notebook服务时不打开浏览器
c.NotebookApp.password = u'sha1:c63cf.....45b09bed'  # 密码的hash 值

```

 {% note warning %} 

**注意**：notebook_dir即为notebook的`/`，因此为了保证安全性，请将notebook的工作目录设为非重要目录，如你可以创建一个`~/jupyter-projects`

 {% endnote %} 

完成配置之后可以启动notebook并访问啦：

```bash
# 使用jupyter-notebook也是一样的
jupyter notebook

# 或者更优雅一点
nohup jupyter notebook >> ~/.notebook.log 2>&1 &
```

 {% note warning %} 

**注意**：要记得在云服务器控制台的安全组中配置规则放行notebook监听的`8888`端口哦

 {% endnote %} 

### 2.3 撰写systemd服务脚本

将以下内容写入`/usr/lib/systemd/system/notebook.service`，若目录不存在则手动创建

```shell
[Unit]
Description=My notebook service
After=network.target

[Service]
#Type=simple  # default simple
User=qgrain
Group=qgrain
ExecStart=/home/qgrain/miniconda3/bin/jupyter-notebook
Restart=on-failure
RestartSec=10s

[Install]
WantedBy=multi-user.target
```

然后配置`notebook.service`自启动

```bash
sudo systemctl enable notebook
sudo systemctl [start|stop|restart|status] notebook
```

## 3 主题美化⭐

### 3.1 安装jupyter-themes

参考[Git开源项目：jupyter-themes](https://github.com/dunovank/jupyter-themes)

```bash
# 安装jupyter-themes
pip install jupyter-themes

# 更新jupyter-themes至latest
pip install --update jupyter-themes
```

### 3.2 配置主题

`jupyter-themes`有以下几条关键命令，具体Usage请见其git仓库的README：

```bash
# 查看所有主题
jt -l

# 将主题配置为monokai
jt -t monokai

# 我的配置命令如下，这样的notebook界面极其舒适ヾ(≧▽≦ )o
jt -t monokai -f roboto -fs 12 -nfs 14 -tfs 13 -ofs 11 -dfs 10 -cellw 66% -T -N -kl
```

 {% note info %} 

其中**参数含义**依次为设置主题为`monokai`，设置代码字体为`roboto`，设置代码字号为`12`，设置notebook字号为`14`，设置文本/Markdown字号为`13`，设置输出字号为`11`，设置Pandas Dataframe字号为`10`，设置cell宽度为屏幕的`66%`，设置`Toolbar可见`，设置`Name&Logo可见`以及设置`Kernel Logo可见`

 {% endnote %} 

具体界面效果如下（当然，passwd我为了测试，输入的是123）：

<img src="https://gitee.com/QGrain/picgo-bed/raw/master/img/20210125012950.png"/>

## 4 参考

[[1] Ming's Blog | 搭建 ipython/jupyter notebook 服务器](https://bitmingw.com/2017/07/09/run-jupyter-notebook-server/)

[[2] Zhiyu's Blog | 编写systemd服务脚本 ](https://qgrain.github.io/2020/05/12/%E7%BC%96%E5%86%99systemd%E6%9C%8D%E5%8A%A1%E8%84%9A%E6%9C%AC/)

[[3] 知乎 |【内容引起舒适】让你的Jupyter Notebook不再辣眼睛](https://zhuanlan.zhihu.com/p/46242116)