package io.github.ceragon.table.extension;

import io.github.ceragon.PluginContext;
import org.gradle.api.tasks.Internal;

import java.util.Collections;
import java.util.List;

public class TableExtension {
    @Internal("表格 文件的目录")
    public List<String> tableFilePaths;

    public TableExtension() {
        this.tableFilePaths = Collections.singletonList(
                PluginContext.pathFormat().format("${project.base.dir}${s}tableFiles")
        );
    }
}
