package io.github.ceragon.protobuf.extension;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TotalMsgBuildConfig {
    final String name;
    /**
     * 模板文件名称
     */
    String vmFile;
    /**
     * 生成的文件名称
     */
    String targetFile;
    /**
     * 是否强制覆盖
     */
    boolean overwrite = true;
}
