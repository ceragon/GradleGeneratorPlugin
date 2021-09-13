package com.ceragon.protobuf.extension;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class EveryProtoBuildConfig {
    String vmFile;
    String targetFile;
    List<String> protoNameMatch = new ArrayList<>();
    boolean overwrite = true;
}
