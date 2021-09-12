package com.ceragon.util;


import org.gradle.api.Project;

import java.io.File;
import java.util.Map;

public class PathFormat {
    public final static String PROJECT_BUILD_DIR = "project.build.dir";
    public final static String PROJECT_BASE_DIR = "project.base.dir";
    public final static String SEPARATOR = "s";
    private final Map<String, Object> basicVar;

    public PathFormat(Project project) {

        String projectSrcDir = project.getBuildDir().getPath();
        String projectBaseDir = project.getProjectDir().getPath();
        this.basicVar = Map.of(
                PROJECT_BUILD_DIR, projectSrcDir,
                PROJECT_BASE_DIR, projectBaseDir,
                SEPARATOR, File.separator
        );
    }


    public String format(String sourcePath) {
        return format(sourcePath, null);
    }

    public String format(String sourcePath, String key, Object value) {
        return format(sourcePath, Map.of(key, value, key.toLowerCase(), value, key.toUpperCase(), value));
    }

    public String format(String sourcePath, Map<String, Object> params) {
        sourcePath = StringUtils.formatKV(sourcePath, basicVar);
        if (params == null) {
            return sourcePath;
        }
        return StringUtils.formatKV(sourcePath, params);
    }
}
