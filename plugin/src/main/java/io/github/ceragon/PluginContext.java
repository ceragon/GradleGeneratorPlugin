package io.github.ceragon;

import io.github.ceragon.util.BuildContext;
import io.github.ceragon.util.PathFormat;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

public class PluginContext {
    private volatile static Project project;
    private volatile static Logger log;
    private volatile static PathFormat pathFormat;
    private final static ThreadLocal<StringBuilder> builderLocal = ThreadLocal.withInitial(StringBuilder::new);
    private final static ThreadLocal<BuildContext> buildContextLocal = ThreadLocal.withInitial(BuildContext::new);

    public static void init(Project project) {
        PluginContext.project = project;
        PluginContext.log = project.getLogger();
        PluginContext.pathFormat = new PathFormat(project);
    }

    public static Project project() {
        return project;
    }

    public static Logger log() {
        return log;
    }

    public static PathFormat pathFormat() {
        return pathFormat;
    }

    public static StringBuilder stringBuilder() {
        StringBuilder builder = builderLocal.get();
        builder.setLength(0);
        return builder;
    }

    public static BuildContext buildContext() {
        return buildContextLocal.get();
    }
}
