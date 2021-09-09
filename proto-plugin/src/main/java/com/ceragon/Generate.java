package com.ceragon;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

abstract public class Generate extends DefaultTask {
    @Input
    abstract public Property<String> getWord();

    @TaskAction
    public void perform() {
        System.out.println("test perform:" + getWord().get());
    }
}
