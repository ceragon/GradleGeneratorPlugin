package com.ceragon.table;

import com.ceragon.table.extension.TableExtension;
import com.ceragon.util.PluginTaskException;
import org.gradle.api.Task;

public class TableGeneratorTask {
    private final TableExtension extension;

    public TableGeneratorTask(TableExtension extension) {
        this.extension = extension;
    }

    public void execute(Task task) {
        PluginTaskException.taskNameLocal.set(task.getName());

    }
}
