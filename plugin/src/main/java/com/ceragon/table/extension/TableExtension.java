package com.ceragon.table.extension;

import com.ceragon.PluginContext;
import org.gradle.api.tasks.Internal;

import java.util.List;

public class TableExtension {
    @Internal("表格 文件的目录")
    public List<String> tableFilePaths;

    public TableExtension() {
        this.tableFilePaths = List.of(
                PluginContext.pathFormat().format("${project.base.dir}${s}tableFiles")
        );
    }
}
