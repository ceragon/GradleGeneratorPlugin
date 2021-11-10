# GradleGeneratorPlugin

gradle generator plugin 是一个使用java语言编写的gradle插件。 你可以用它完成protobuf的代码生成，相较于protobuf官方的protoc工具，具有跨平台运行、额外代码生成等特性。

## 功能

1. 通过定义的proto文件，生成不同语言的protobuf官方代码
2. 通过编写模板文件，实现生成proto的衍生代码，例如某个消息的处理类

## 快速上手

用于生成官方的 protobuf 代码

1.

点击下载基础文件目录 [proto-simple.zip](https://github.com/ceragon/GradleGeneratorPlugin/releases/download/v1.1/proto-simple.zip)

2. 解压到某个目录
3. 在目录的 protofiles 目录中添加自定义的 proto 文件
4. 执行 proto-build.bat 或者 proto-build.sh
5. 最后在build 目录的 generated-sources 目录中可以看到生成的 protobuf 的 java 文件

## 使用方法

工具提供两种使用方式，一种是独立方式，另外一种是作为gradle工程的插件使用方式

### 独立方式

1. 下载基础文件目录 [proto-full.zip](https://github.com/ceragon/GradleGeneratorPlugin/releases/download/v1.1/proto-full.zip)
2. 解压到某个目录
3. 在目录的 protofiles 目录中添加自定义的 proto 文件
4. 执行 proto-build.bat 或者 proto-build.sh
5. 最后在build 目录的 generated-sources 目录中可以看到生成的 protobuf 的 java 文件

### 作为 gradle 工程的插件

1. 在build.gradle 中添加插件配置

> 更多配置方式可查看此工程的 [gradle官方插件仓库](https://plugins.gradle.org/plugin/io.github.ceragon.proto)

```groovy
plugins {
    id "io.github.ceragon.proto" version "1.3"
}
```

## 配置说明

上述两种方式，无论哪种都需要在build.gradle中配置相关参数。

### 目录结构

以下是独立方式下的完整的目录结构，插件方式也类似:

```
.
├── gradle
│   └── wrapper
│       └── gradle-wrapper.jar
│       └── gralde-wrapper.properties
├── protofiles
│   ...
│   └── *.proto
├── template   
│   ...
│   └── *.vm
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
└── settings.gradle
```

### 生成官方的protobuf代码

本插件的核心功能是生成官方的protobuf代码，遵从开箱即用的设计理念。通过简单的配置，就可以直接生成代码，无需额外步骤。

#### build.gradle 的配置

```groovy

plugins {
    id 'io.github.ceragon.proto' version '1.3'
}
proto {
    protocVersion = "3.11.4"
    outputTargets {
        javaCode {
            type = "java"
        }
        cppCode {
            type = "cpp"
        }
    }
}
```

#### 配置说明

- protocVersion : 声明protoc的版本号，完整版本号可以查看 [protoc版本号](https://repo.maven.apache.org/maven2/com/google/protobuf/protoc/)
- outputTargets : 用于配置生成代码的类型，等效于protoc 命令行中的 --java_out --cpp_out 等参数
- javaCode 和 cppCode : 这两个值用于表示名称，可以任意起，在 outputTargets 闭包中不重复即可
- type : 声明要生成的protobuf官方代码的类别，type的值和 protoc 命令行中的 --java_out --cpp_out 保持一致

> 如果想生成 C# 的代码，C# 对应的 protoc 生成命令是 --csharp_out，则type应该填写 csharp

#### 运行生成

##### 命令行执行

```shell
// windows 下执行 .\gradlew.bat proto
sh gradlew proto
```

##### idea中执行

可以在idea的gradle插件中直接运行，位于 Tasks -> other -> proto

![idea-run-proto](res/idea-run-proto.png)