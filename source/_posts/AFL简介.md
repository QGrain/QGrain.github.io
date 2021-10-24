---
title: AFL简介
toc: true
date: 2020-08-25 22:14:25
tags: [AFL, Fuzzing]
index_img: /img/afl-window.png
---

**模糊测试（Fuzzing）**技术作为**漏洞挖掘**最有效的手段之一，近年来一直是众多安全研究人员发现漏洞的首选技术。  **AFL**、LibFuzzer、honggfuzz等操作简单友好的工具相继出现，也极大地降低了模糊测试的门槛。 

<!--more-->

AFL即American Fuzzy Lop，是由安全研究员Micha · Zalewski（[@lcamtuf](https://twitter.com/lcamtuf)）开发的一款基于覆盖引导（Coverage-guided）的模糊测试工具，采用一种新型的**编译时插桩**和**遗传算法**来自动生成测试样本，使用这些样本可触发目标二进制程序中新的内部状态，从而可提高模糊测试的代码覆盖率。

## 1 AFL工作流

<img src="https://gitee.com/QGrain/picgo-bed/raw/master/img/afl-workflow.jpg"/>

**AFL工作的基本流程如图所示：**

-  从源码编译程序时进行插桩，以记录代码覆盖率（Code Coverage） 
-  选择一些输入文件，作为初始测试集加入输入队列（queue） 
-  将队列中的文件按一定的策略进行“突变” （Mutation）
-  如果经过变异文件更新了覆盖范围，则将其保留添加到队列中 
-  上述过程会一直循环进行，期间触发了crash的文件会被记录下来 

`afl-fuzz` 会记录触发crash的`cmdline`以便研究者手动复现和`gdb`调试分析。

## 2 代码插桩

 在AFL编译文件时候`afl-gcc`会在规定位置插入桩代码，可以理解为一个个的**探针**(但是没有暂停功能)，在后续fuzz的过程中会**根据这些桩代码进行路径探索，测试等**。对于插桩的理解也可以这样理解，如下图： 

## 3 构建语料库

使用AFL进行模糊测试的对象一定是具备接收输入的程序，也正是这样的程序有更大可能出现漏洞。

 AFL需要一些初始输入数据（也叫种子文件）作为Fuzzing的起点，AFL可以通过启发式算法自动确定文件格式结构。lcamtuf就在博客中给出了一个有趣的[例子](https://lcamtuf.blogspot.com/2014/11/pulling-jpegs-out-of-thin-air.html)——对djpeg进行Fuzzing时，仅用一个字符串”hello”作为输入，最后凭空生成大量jpeg图像！

尽管AFL的输入可以是毫无意义的文件，但是一个**高质量的语料库**，能够让Fuzzing更加高效和精准。

### 3.1 对输入种子的要求

- **有效的输入：**无效的输入会产生bug和crash，但是有效的输入能够更快找到更多的执行路径
- **尽量小的体积：** 较小的文件会不仅可以减少测试和处理的时间，也能节约更多的内存，AFL给出的建议是最好小于1 KB，但其实可以根据自己测试的程序权衡，这在AFL文档的`perf_tips.txt`中有具体说明。 

### 3.2 如何寻找输入种子

- 项目自身提供的测试用例
- 目标程序bug提交页面
- 使用格式转换器，将现有格式文件转换为不常见的文件格式
- AFL源码仓库的testcases目录下提供了测试用例
- 其他大型语料库：
  - [afl generated image test sets](http://lcamtuf.coredump.cx/afl/demo/) 
  - [fuzzer-test-suite](https://github.com/google/fuzzer-test-suite)
  - [libav samples](https://samples.libav.org/)
  - [ffmpeg samples](http://samples.ffmpeg.org/)
  - [fuzzdata](https://github.com/MozillaSecurity/fuzzdata)
  - [moonshine](https://gitlab.anu.edu.au/lunar/moonshine)

### 3.3 如何精简找到的种子

 AFL提供了两个工具来帮助我们完成**语料库蒸馏**工作——`afl-cmin`和`afl-tmin`。 

- **移除执行相同代码的输入文件——afl-cmin:**

  -  `afl-cmin`的核心思想是：**尝试找到与语料库全集具有相同覆盖范围的最小子集**。举个例子：假设有多个文件，都覆盖了相同的代码，那么就丢掉多余的文件。其使用方法如下： 

    ```bash
    $ afl-cmin -i input_dir -o output_dir -- /path/to/tested/program [params] @@
    ```

- **减小单个输入文件的大小——afl-tmin:**

  - 在缩减了语料库规模之后，还需要对单个语料文件进行精简。`afl-tmin`有两种工作模式，`instrumented mode`和`crash mode`。默认的工作方式是`instrumented mode`，如下所示（如果指定了参数`-x`，即`crash mode`，会把导致程序非正常退出的文件直接剔除。 ）： 

    ```bash
    $ afl-tmin [-x] -i input_file -o output_file -- /path/to/tested/program [params] @@
    ```

  -   `afl-tmin`接受单个文件输入，所以可以用一条简单的shell脚本批量处理：

    ```shell
    for i in *
    do
        afl-tmin -i $i -o tmin-$i -- ~/path/to/tested/program [params] @@
    done
    ```

- **使用完afl-tmin后再次使用afl-cmin，可能可以再过滤掉一些用例**

## 4 Mutation突变策略

AFL的突变策略十分丰富，它能够尽可能地保证输入种子充分地变异，以追求更大的执行路径覆盖率，从而测试出更多的crash。AFL的突变策略依次包括：bitflip，arithmetic，interest，dictionary，havoc和splice。

### 4.1 bitflip

#### 4.1.1 基础bitflip

- **基本原理**： 按位翻转，1变为0，0变为1。AFL会采用不同的翻转长度和步长来进行位翻转，顺序如下：
  - bitflip 1/1，2/1，4/1，8/8，16/8，32/8
  - bitflip m/n即每次翻转m个bit，按照n个bit的步长从文件头部开始翻转
  - AFL还有一些对文件格式启发式的判断，如自动检测token和生成effector map
- **举例**：对某jpeg格式的文件从其头部`FF D8...`开始进行bitflip
  - 第一次bitflip 1/1：`7F D8`，第二次bitflip 1/1：`BF D8`，第三次bitflip 1/1：`DF D8`，第四次bitflip 1/1：`EF D8`

#### 4.1.2 自动检测token

- **基本原理**： 如果连续多个bytes的最低位被翻转后，程序的执行路径都未变化，而且与原始执行路径不一致  ，那么就把这一段连续的bytes判断是一条token。
- **举例**：PNG文件中用`...IHDR... `作为起始块的标识，当翻转到最高位字节的时候，由于IHDR被破坏，程序执行路径发生改变，随后在翻转接下来的三个字节的时候IHDR同样被破坏，程序会采取相同的执行路径。由此AFL就判断得到了一个可能的**token：IHDR**，并将其记录为后续的变异提供备选。

#### 4.1.3 生成effector map

- **基本原理**：在执行bitflip 8/8，即对每个字节进行翻转时，如果执行路径发生了改变，则将该byte在effector map中标记为1，反之标记为0。其逻辑为如果翻转一个byte都无法带来程序执行路径的改变，则该byte很有可能是属于`data`而非`metadata(如size，flag等)`，对fuzzing的意义不大，在之后的变异里会参考effector map跳过那些"无效"的bytes。
- **说明**：在以下三种情况下，AFL不会判定有效字符：
  - AFL工作模式为`dumb mode`或者`Slave mode`即（静默模式和从模式）
  - 如果文件大小小于128bytes，则默认所有字节均为"有效"字节
  - 如果文件被标记为"有效"的字节超过了90%，则默认所有字节均为"有效"字节

### 4.2 arithmetic

bitflip策略全部突变完毕后，进入到arithmetic突变阶段。arithmetic阶段会根据目标大小的不同，分为了一下几个子阶段：

- **基本原理**：arith 8/8，arith 16/8，arith 32/8。其含义为每次对8，16，32bits进行加减运算，按照每8个bits的步长从文件头开始，即对文件的每个byte，word，dword进行整数加减变异。
- **说明**：
  - 加减变异运算的上限在config.h中的宏ARITH_MAX定义，默认为35，即进行 ±1，±2，...，±35的运算变异
  - AFL会考虑整数的大端序和小端序形式，并以这两种方式分别进行变异
  - AFL会跳过effector map中标记为"无效"的bytes，以及之前bitflip阶段已经生成过的变异(比如加减某个数之后产生的效果和之前bitflip的某次变异一样)

### 4.3 interest



### 4.4 dictionary



### 4.5 havoc



### 4.6 splice

## 5 Fuzzing后分析

