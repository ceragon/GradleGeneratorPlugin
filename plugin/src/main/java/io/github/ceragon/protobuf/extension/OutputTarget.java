package io.github.ceragon.protobuf.extension;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.gradle.api.tasks.Internal;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@RequiredArgsConstructor
public class OutputTarget {
    final String name;
    @Internal("生成语言类型")
    String type = "java";

    @Internal("是否预先清理之前生成的文件夹")
    boolean cleanOutputFolder = true;

    @Internal("是否覆盖之前生成文件")
    boolean overwriteOutputFile = false;

    @Internal("导出目录")
    String outputDirectory;

    @Internal("导出的可选项，支持protoc的所有配置")
    String outputOptions;

    @Internal("自定义 protoc 插件的可执行文件路径，等价于 --plugin=protoc-gen-<type>=<path>；为空时按 protoc 的默认规则查找")
    String pluginPath = null;

    @Internal("proto 文件名的匹配规则，正则按全匹配生效；只匹配文件名，不含目录；为空时匹配全部")
    List<String> protoNameMatch = new ArrayList<>();

}
