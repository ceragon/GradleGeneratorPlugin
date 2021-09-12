package com.ceragon.protobuf.extension;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EveryMsgBuildConfig {
    String vmFile;
    String targetFile;
    List<String> msgNameMatch;
    List<String> protoNameMatch = new ArrayList<>();
    boolean overwrite = true;
}
