package com.ceragon.protobuf.extension;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@ToString
@RequiredArgsConstructor
public class OutputTarget {
    final String name;
    String type = "java";
    String addSources = "main";
    boolean cleanOutputFolder = true;
    String outputDirectory;
    String outputDirectorySuffix;
    String outputOptions;

}
