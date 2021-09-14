package com.ceragon.protobuf.extension;

import groovy.lang.Closure;
import lombok.Data;
import lombok.ToString;
import org.gradle.api.Action;
import org.gradle.util.internal.ConfigureUtil;

import java.io.File;

@Data
@ToString
public class OutputTarget {
    String type = "java";
    String addSources = "main";
    boolean cleanOutputFolder = true;
    String outputDirectory;
    String outputDirectorySuffix;
    String outputOptions;

}
