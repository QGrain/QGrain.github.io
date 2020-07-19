---
title: Linux磁盘分区扩容(非LVM)
toc: true
date: 2019-12-12 22:41:08
tags: [Linux]
thumbnail: /image/thumbnails/logo-linux.jpg
index_img: /img/logo-linux.jpg
---

## 1 适用场景

在为主系统以外的系统(无论是虚拟机还是多系统)分配磁盘空间时我们常常**比较保守**，这样导致的结果是根分区的空间不足。<!-- more -->

如图：

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191102191126.png)

但是我们的磁盘上还有空闲的空间未使用。因此我们可以将未使用部分扩展到相邻的分区中。

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191102192359.png)

## 2 实现

### 2.1 进入rescue mode

- 如果**扩容的对象是根分区(系统分区)**，则首先需要进入rescue mode。如果扩容对象为非系统分区，如/home等，则不需要进入rescue mode。

  一个简单判定是否需要进入rescue mode 或者使用安装镜像U盘启动的方法是，`umount`此次扩容的对象，操作系统是否会受到影响。

  - 键入命令`init 1`（单用户模式，仅root）进入到rescue mode

  ![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191102205559.png)

  - 或者在grub界面选择advanced启动项里的recovery mode

  ![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191102205706.png)

  最终还是进入到此界面

  ![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191102210135.png)

### 2.2 删除待扩容的分区

- `fdisk /dev/sda` 打开**fdisk**工具，键入`p`打印当前/dev/sda磁盘的信息：

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191102210505.png)

- 依次键入`d`，`2`，删除/dev/sda2分区

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191102210624.png)



### 2.3 新建分区

- 依次键入`n`，`p`，`2`，`no`，来重新创建/dev/sda2。由于之前删除该分区后使得它与此磁盘上未使用的空间合并，因此创建新分区时能够通过指定`start`和`end`来扩大新分区的容量。

  注意在被询问是否移除新分区/dev/sda2带有的**signature**时，选择no，否则该分区原有的数据会丢失。

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191102210932.png)

- 如果此过程操作无误可以键入`w`保存并退出。如果有问题可以键入`q`不保存并退出。



### 2.4 更新分区表

- 此时`df -h`查看的结果是待扩容分区容量仍然未发生变化，因为还需要**调整分区表**。

- `e2fsck -f /dev/sda2`检查分区信息

- 调整分区大小。有两种方法：

  - 一种是卸载待扩容分区，然后运行`resize2fs -f /dev/sda2`

  - 另一种是不用卸载，在线扩容。不卸载 / 分区直接运行`resize2fs -f /dev/sda2`会得到如下报错：

  ![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191102212415.png)

  因此需要重新挂载`/`分区为**可读写模式**`mount -o remount, rw /`，然后就可以调整分区大小了。且未使用的空间被格式化为和根分区一致的ext4。

  ![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191102212921.png)

- reboot之后查看结果。发现之前空闲的6.4G空间已经并入待扩容的根分区。**扩容成功**

![](https://raw.githubusercontent.com/QGrain/picBed/master/img/20191102213630.png)

## 3 一点补充

后期分别在不同的系统上做过尝试，发现有的Linux发行版需要重新挂载/分区来更新分区表大小，比如Kali。而有的发行版比如CentOS，Ubuntu则不需要，进入操作系统后可以直接resize2fs