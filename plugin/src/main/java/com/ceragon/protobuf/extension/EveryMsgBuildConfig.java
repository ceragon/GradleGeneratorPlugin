package com.ceragon.protobuf.extension;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class EveryMsgBuildConfig {
    final String name;
    String vmFile;
    String targetFile;
    List<String> msgNameMatch;
    List<String> protoNameMatch = new ArrayList<>();
    boolean overwrite = true;
}
