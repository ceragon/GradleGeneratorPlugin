package com.ceragon.protobuf.extension;

import lombok.Data;
import lombok.ToString;

import java.io.File;

@Data
@ToString
public class OutputTarget {
    String type = "java";
    String addSources = "main";
    boolean cleanOutputFolder = true;
    File outputDirectory;
    String outputDirectorySuffix;
    String outputOptions;
}
