# GradleGeneratorPlugin

gradle generator plugin 是一个使用java语言编写的gradle插件。 你可以用它完成protobuf的代码生成，相较于protobuf官方的protoc工具，具有跨平台运行、额外代码生成等特性。

## 功能

1. 通过定义的proto文件，生成不同语言的protobuf官方代码
2. 通过编写模板文件，实现生成proto的衍生代码，例如某个消息的处理类

## 快速上手

用于生成官方的 protobuf 代码

1. 点击下载基础文件目录 [proto-simple.zip](https://github.com/ceragon/GradleGeneratorPlugin/releases/download/v1.1/proto-simple.zip)
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

### 生成配置里的占位符与过滤

`targetFile` 支持以下占位符，大小写三种写法都能用（例如 `${MsgName}`、`${msgname}`、`${MSGNAME}`）：

| 占位符 | 用于 | 取值 |
| --- | --- | --- |
| `${MsgName}` | `everyMsgBuilds` | message 名 |
| `${EnumName}` | `everyEnumBuilds` | 枚举名 |
| `${ProtoName}` | `everyProtoBuilds` | proto 文件名去掉目录和 `.proto` 后缀，`net/ProtoLogin.proto` → `ProtoLogin` |

`everyMsgBuilds`、`everyProtoBuilds`、`everyEnumBuilds` 都可以用正则做过滤，正则按**全匹配**生效：

- `protoNameMatch`：按 proto 文件名过滤
- `msgNameMatch`：`everyMsgBuilds` 里按 message 名过滤；`everyProtoBuilds` 里表示「该 proto 至少包含一个匹配的 message 才生成代码」，只看顶层 message
- `enumNameMatch`：按枚举名过滤

`everyProtoBuilds` 会为每个命中的 proto 渲染一次模板，但**不会自动区分文件名**：`targetFile` 里不带 `${ProtoName}` 时多次渲染都写同一个路径（`overwrite` 为 true 只剩最后一个，为 false 只剩第一个）。想让一个 proto 产出一个文件，必须显式带上占位符：

```groovy
proto {
    everyProtoBuilds {
        perProto {
            vmFile = "ProtoMeta.vm"
            targetFile = baseDir('build/proto-meta/${ProtoName}.txt')
            msgNameMatch = [".*Request"]
        }
    }
}
```

注意 `targetFile` 里的占位符必须写在**单引号**字符串里，双引号会被 Groovy 在配置阶段当作变量插值。不同目录下的同名 proto 会得到相同的 `${ProtoName}`，仍然会互相覆盖。

### 输出目标（outputTargets）的插件路径、文件过滤与 include 目录

`outputTargets` 的每个目标支持 `pluginPath` 与 `protoNameMatch`，`proto` 扩展本身新增 `includeDirectories`：

- `pluginPath`：给当前 `type` 指定自定义的 protoc 插件可执行文件，等价于 protoc 的 `--plugin=protoc-gen-<type>=<path>`（例如 `type = 'go'` 时指向 `protoc-gen-go`）；不配置时按 protoc 的默认规则在 `PATH` 里查找
- `protoNameMatch`：按 proto **文件名**过滤，正则按全匹配生效，匹配的是带 `.proto` 后缀的文件名（不含目录）；不配置或为空时匹配全部
- `includeDirectories`（写在 `proto` 上）：在 `protofiles` 之外追加 protoc 的 `-I` 目录，用来 import 公共 proto；相对路径按项目根目录解析

`outputOptions` 现在对所有语言都生效：非 `js` 类型会拼成 `--<type>_opt=<outputOptions>`（此前只有 `go`、`csharp` 会拼，其它类型配置了也被静默丢弃），`js` 仍按 protoc 的约定写进 `--js_out=<outputOptions>:<outputDirectory>`。

```groovy
proto {
    includeDirectories = [baseDir('protofiles/common')]

    outputTargets {
        goServer {
            type = 'go'
            pluginPath = 'C:/tools/protoc-gen-go.exe'
            outputDirectory = baseDir('build/gen/go')
            outputOptions = 'paths=source_relative'
            protoNameMatch = ['.*Protocol.*']
        }
    }
}
```

