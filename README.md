# 5 种语言排序速度比较（python，php，nodejs，java，rust）

## 在命令行里输入（需安装相应的解释器/编译器）

python sort.py

php sort.php

node sort.mjs

node sort.mjs 100000

node sort.mjs 1000000

java Sort.java

java Sort.java 100000

java Sort.java 1000000

rustc sort.rs

（先编译）

sort.exe

（后直接运行 exe 文件）

sort.exe 100000

sort.exe 1000000

node setup.mjs

（生成数据文件）

## 结论

1. 选择排序和插入排序非常慢
1. 快速排序比归并排序稍快
1. `python` 和 `php` 的速度较慢（相比其它 3 个语言），程序里只排序 1 万个数（不然时间太长），输入命令行看结果
1. 主要比较 `nodejs`，`java`，`rust` 的速度，其中 java 最快，nodejs 第二，rust 第三，输入命令行看结果
1. 在用**快速排序**排序 **`100 万`个随机数**时，java 用时 **`155` 毫秒**左右，nodejs 用时 **`240` 毫秒**左右，rust 用时 **`310` 毫秒**左右
1. 低层语言如 rust，在不会用的情况下并不比高层语言如 nodejs 更快

## 其他

`js` 如 v8 的速度很快，`python` 为什么这么慢，我可能并没有用正确的方式写 python，毕竟不熟悉。我也不熟悉 `java`，但写出来的 java 速度最快。`rust` 是系统语言，我本以为 rust 是最快的，但是写出来比 js 还慢。rust 光表示数组就有`[T;N]`、`Vec<T>`、`&[T]`三种数据类型，这时候会感觉 js 让生活更轻松。

> _P.S._ 运行时间是在各语言自己的时间模块下计算的，不是严格意义上统一的测试标准
>
> v8 对 js 是先编译再执行，所以快
