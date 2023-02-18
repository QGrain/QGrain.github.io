---
title: 内核Fuzz技巧与备忘
toc: true
date: 2023-02-01 16:26:29
tags: [Kernel, Fuzz]
categories: [Fuzz]
index_img: /image/thumbnails/cube.jpg
hide: true
---

留个备忘录方便查看做kernel fuzzing中遇到的问题和解决技巧

<!--more-->

## 一些脚本

- 查看crashs目录结果

```shell
#!/bin/bash

set -e

print_help() {
	echo -e "Usage: ./get_result.sh /path/to/crashs_dir"
}

if [[ ! -n "$1" ]]
then
	print_help
else
    ls $1 | while read crash
    do
        echo -e "\n======== $crash ========"
        desc=`cat $1/$crash/description`
        echo -e "Description: $desc"
        repro=`ls $1/$crash | grep repro | wc -l`
        echo -e "Repro: $repro"
    done
    echo -e "\nDone!"
fi
```

