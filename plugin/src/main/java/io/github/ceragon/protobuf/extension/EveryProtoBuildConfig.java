package io.github.ceragon.protobuf.extension;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class EveryProtoBuildConfig {
    final String name;
    String vmFile;
    String targetFile;
    List<String> protoNameMatch = new ArrayList<>();
    /** proto 文件内 message 名称的匹配规则，支持正则表达式；只有包含至少一个匹配 message 的 proto 文件才会生成代码 */
    List<String> msgNameMatch;
    boolean overwrite = true;
}
