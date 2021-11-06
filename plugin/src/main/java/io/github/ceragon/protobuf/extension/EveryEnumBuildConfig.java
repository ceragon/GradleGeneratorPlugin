package io.github.ceragon.protobuf.extension;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class EveryEnumBuildConfig {
    final String name;
    String vmFile;
    String targetFile;
    List<String> enumNameMatch;
    List<String> protoNameMatch = new ArrayList<>();
    boolean overwrite = true;
}
