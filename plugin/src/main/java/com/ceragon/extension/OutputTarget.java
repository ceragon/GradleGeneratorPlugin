package com.ceragon.extension;

import lombok.Data;
import lombok.ToString;
import org.gradle.api.file.Directory;

@Data
@ToString
public class OutputTarget {
    String type = "java";
    String addSources = "main";
    boolean cleanOutputFolder = true;
    Directory outputDirectory;
    String outputDirectorySuffix;
    String outputOptions;
}
