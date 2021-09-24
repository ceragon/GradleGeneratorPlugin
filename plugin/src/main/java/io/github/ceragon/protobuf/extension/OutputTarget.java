package io.github.ceragon.protobuf.extension;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.gradle.api.tasks.Internal;

@Data
@ToString
@RequiredArgsConstructor
public class OutputTarget {
    final String name;
    @Internal("生成语言类型")
    String type = "java";

    @Internal("是否预先清理之前生成的文件")
    boolean cleanOutputFolder = true;
    @Internal("导出目录")
    String outputDirectory;
;
    @Internal("导出的可选项，支持protoc的所有配置")
    String outputOptions;

}
