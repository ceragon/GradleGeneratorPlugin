package com.ceragon.protobuf.extension;

import lombok.Data;

@Data
public class TotalMsgBuildConfig {
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
