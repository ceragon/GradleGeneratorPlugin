package com.ceragon.protobuf.extension;

import lombok.Data;

@Data
public class TotalMsgBuildConfig {
    String vmFile;
    String targetFile;
    boolean overwrite = true;
}
