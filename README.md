## Java4Compiler
实现C语言的简易编译器，并将C语言转换成字节码运行在Java虚拟机中

No.1 实现一个简单数学表达式的词法分析器

No.2 实现简单数学表达式的语法分析（递归调用）

No.3 改进的语法分析器（递归调用->循环方式）

No.4 实现代码生成，并修改了词法分析和代码生成对于输入空格产生的错误

No.5 修改了词法分析、语法分析和代码生成不能同时运行的错误，以及寄存器释放的指针错误，并优化了结果输出的格式

No.6 设计了词法分析的内存结构，将输入流读入缓冲区，再从缓冲区读入字符，并实现了该输入系统（控制台获取输入流）

No.7 学习了有限状态机基本原理，实现了简单的包含六个状态的浮点——整型状态机
- 下次任务：
- [ ] 完善输入系统
- [x] 完善浮点——整型状态机
- [x] 修改日志中的 bug
- [ ] 优化输入系统中刷新流的逻辑，
- 遗留问题：
- [ ] lookahead() 函数和 pushback() 函数
- [ ] 缓冲区有效字符串的指针封装

No.7-fix.1 修改了浮点——整型状态机中输入多行行号错误的问题，解决了对于文件末尾的 0 错误处理的问题，
增加了输入类似于"1a122 1aaaa 12"的错误判断逻辑
- 下次任务：
- [x] 学习将正则表达式转化成有限状态机

No.8 学习了正则表达式运行原理，实现了正则表达式的宏展开和宏替换
- 下次任务：
- [x] 解决正则表达式中的间套问题

No.8-fix.1 解决了正则表达式的间套问题

No.8-fix.2 解决了正则表达式引号中间的表达式无需进行宏替换的问题，增加了从文件获取输入流的操作

No.9 实现了对解析后的正则表达式进行词法分析，包括对转义字符和双引号里内容的分析

No.10 实现了将简单正则表达式 (如 a, [0-9], [^a-z], .) 转化为简单 NFA

No.11 实现了从简单到复杂构建正则表达式，包括实现正则表达式的三种闭包操作和连接操作

No.11-fix.1 实现了正则表达式的 OR 连接操作，修改了无法解析圆括号和输入正则表达式宏空指针错误

No.11-fix.2 修改了输入两个以上 OR 操作导致的错误

No.12 实现了 NFA 识别输入的字符串，包括实现 ε 闭包操作和 move 操作。并且修改了 "\." 反义操作符被识别为任意匹配字符的错误。

No.13 实现了 NFA 转化为 DFA

## Log 
[2022.1.21] 
- Input.java 中 ii_text 和 ii_pText 中输出的字符串末尾空格需处理
- 浮点——整型状态机输入多行的行号错误，且换行符后面的 0 无需处理

[2022.1.22]
- null

[2022.1.24]
- 解析多行正则表达式时，第二行及以后解析出现错误

[2022.1.25]
- 解析类似于 {D}.{D} (一个表达式中包含多个宏定义)的正则表达式抛出宏不存在的异常

[2022.1.30]
- null

[2022.2.3]
- 解析单个字符的闭包而非字符集时会发生死循环导致解析的正则表达式超过 256 个
- 当输入正则表达式的宏而非输入字符集时会报空指针错误

[2022.2.10]
- null

[2022.2.13]
- null