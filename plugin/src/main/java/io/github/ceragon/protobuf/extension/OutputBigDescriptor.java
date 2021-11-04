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
public class OutputBigDescriptor {
    final String name;

    @Internal("导出目录")
    String outputFile;

    /**
     * When using --descriptor_set_out, also include
     * all dependencies of the input files in the
     * set, so that the set is self-contained.
     */
    @Internal("是否包含依赖")
    boolean includeImports = true;
    @Internal("是否导入注释信息")
    boolean includeSourceInfo;
    @Internal("导出的可选项，支持protoc的所有配置")
    String outputOptions;
    @Internal("协议文件名称匹配,默认匹配所有")
    List<String> protoNameMatch = new ArrayList<>();
}
