---
title: AFL简介与使用
toc: true
date: 2020-08-25 22:14:25
tags: [AFL, Fuzzing]
index_img: /img/afl-window.png
---

## AFL简介

AFL即American Fuzzy Lop，是一种安全导向的**模糊测试工具**，采用一种新型的**编译时插桩**和**遗传算法**来自动生成测试样本，使用这些样本可触发目标二进制程序中新的内部状态，从而可提高模糊测试的代码覆盖率。