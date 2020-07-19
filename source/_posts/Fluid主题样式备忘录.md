---
title: Fluid主题样式备忘录
toc: true
date: 2020-07-15 17:55:55
tags: [Blog, Second]
index_img: /img/fluid-logo.png
categories:
	- [Configuration]
---

[Hexo Fluid](https://hexo.fluid-dev.com/docs/)是一款兼具美观和实用的博客主题。本文用于记录其常用样式的用法，包括脚注，

<!--more-->

## 1 脚注

主题内置了脚注语法支持，可以在文章末尾自动生成带有锚点的脚注，该功能在**主题配置**中默认开启：

```yaml
post:
  footnote:
    enable: true
    header: ''
```

参考配置指南[^1]，可知脚注语法如下：

```markdown
这是一句话[^1]
[^1]: 这是对应的脚注
```

更优雅的使用方式，是将脚注写在文末，比如：

```markdown
正文
## 参考
[^1]: 参考资料1
[^2]: 参考资料2
```

## 2 Tag插件

### 2.1 便签

在 markdown 中加入如下的代码来使用便签：

```markdown
{% note success %}
文字 或者 `markdown` 均可
{% endnote %}
```

或者使用 HTML 形式：

```html
<p class="note note-primary">标签</p>
```

<p class="note note-primary">可选便签：success, danger, warning, primary, secondary, info, list</p>
{% note success %}
success
{% endnote %}

{% note danger %}
danger
{% endnote %}

{% note warning %}
warning
{% endnote %}

{% note primary %}
primary
{% endnote %}

{% note secondary %}
secondary
{% endnote %}

{% note info %}
info
{% endnote %}

{% note light %}
light
{% endnote %}

### 2.2 行内标签

在 markdown 中加入如下的代码来使用 Label：

```markdown
{% label primary @text %}
```

或者使用 HTML 形式：

```html
<span class="label label-primary">Label</span>
```

可选 Label：

{% label primary @primary %} {% label default @default %} {% label info @info %} {% label success  @success %} {% label warning @warning %} {% label danger @danger %}

### 2.3 勾选框

在 markdown 中加入如下的代码来使用 Checkbox：

```markdown
{% cb text, checked?, incline? %}
```

text：显示的文字
checked：默认是否已勾选，默认 false
incline: 是否内联（可以理解为后面的文字是否换行），默认 false

### 2.4 按钮

你可以在 markdown 中加入如下的代码来使用 Button：

```markdown
{% btn url, text, title %}
```

或者使用 HTML 形式：

```html
<a class="btn" href="url" title="title">text</a>
```

url：跳转链接
text：显示的文字
title：鼠标悬停时显示的文字（可选）

{% btn https://hexo.fluid-dev.com/docs/guide/#tag-插件, tag-插件用法, zzy yyds （￣︶￣）↗　 %}

### 2.5 组图

如果想把多张图片按一定布局组合显示，你可以在 markdown 中按如下格式：

total：图片总数量，对应中间包含的图片 url 数量
n1-n2-...：每行的图片数量，可以省略，默认单行最多 3 张图，求和必须相等于 total，否则按默认样式

## 参考

[^1]: [Fluid配置指南](https://hexo.fluid-dev.com/docs/guide/#脚注)