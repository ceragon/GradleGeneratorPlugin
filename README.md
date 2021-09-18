# GradleGeneratorPlugin

gradle generator plugin 是一个使用java语言编写的gradle插件。 你可以用它完成protobuf的代码生成，相较于protobuf官方的protoc工具，具有跨平台运行、额外代码生成等特性。

## 功能

1. 通过定义的proto文件，生成不同语言的protobuf官方代码
2. 通过编写模板文件，实现生成proto的衍生代码，例如某个消息的处理类

### 快速上手

1. 点击下载基础文件目录（待补充）
2. 解压到某个目录
3. 在文件的 protofiles 目录中添加自定义的proto文件
4. 执行proto-build.bat 或者 proto-build.sh
5. 最后在build 目录的 generated-sources 目录中可以看到生成的protobuf的java文件

## 用法

工具提供两种使用方式，一种是独立方式，另外一种是作为gradle工程的插件使用方式