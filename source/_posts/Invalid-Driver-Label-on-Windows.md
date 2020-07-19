---
title: Invalid Driver Label in Windows
toc: true
date: 2019-05-16 22:38:40
thumbnail: /image/thumbnails/hello-world.jpg
tag: [Windows, Virtualization]
index_img: /image/thumbnails/hello-world.jpg
---
## 1 Cause

After we change the label of a disk, the Install control programs in the original driver may be inexecutable, with an error of **"Invalid Driver X:\ "**

## 2 Solutions

### 2.1 Recommended

Using a DOS-command: **subst** may solve this problem properly.

<!-- more -->

**subst**: Assign a driver label to a path(a directory) temporarily. Then the directory can be accessed as the Driver.

- `subst [Driver Label] [path to dir]`, Create a virtual driver on the /path/to/dir.
- `subst [Driver Label] /D`, Delete the Created virtual driver.
- `subst`, list the current Virtual Driver

- with the help of **subst**, we could also hide the Real Driver like "D: " by Creating a homonymous Virtual Driver"D: "

### 2.2 Not Recommended

Modify the Driver Label to the required Label according to the error.

Be Cautious**!** Remember to **change back** to the original Driver Label, or you will find the other programs in this Disk are inaccessible.
