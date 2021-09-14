package com.ceragon.protobuf.extension;

import com.ceragon.PluginContext;
import com.github.os72.protocjar.ProtocVersion;
import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.util.internal.ConfigureUtil;

import java.util.ArrayList;
import java.util.List;

public class ProtoExtension {
    @Internal("protoc 的版本号")
    public String protocVersion = ProtocVersion.PROTOC_VERSION.mVersion;

    @Internal("proto 文件的目录")
    public List<String> protoFilePaths;

    @Internal("输出配置")
    public List<OutputTarget> outputTargets = new ArrayList<>();
    @Internal("模板文件的目录")
    public String templatePath;
    @Nested
    public List<TotalMsgBuildConfig> totalMsgBuilds = new ArrayList<>();
    @Nested
    public List<EveryMsgBuildConfig> everyMsgBuilds = new ArrayList<>();
    @Nested
    public List<EveryProtoBuildConfig> everyProtoBuilds = new ArrayList<>();

    public ProtoExtension() {
        protoFilePaths = List.of(
                PluginContext.pathFormat().format("${project.base.dir}${s}protofiles")
        );
        templatePath = PluginContext.pathFormat().format("${project.base.dir}${s}template");
    }

    public OutputTarget outputTarget(Action<OutputTarget> action) {
        OutputTarget outputTarget = new OutputTarget();
        action.execute(outputTarget);
        return outputTarget;
    }

    //region 闭包方法区
    public String baseDir(String path) {
        return PluginContext.pathFormat().format("${project.base.dir}${s}" + path);
    }

    public OutputTarget outputTarget(Closure closure) {
        return ConfigureUtil.configure(closure, new OutputTarget());
    }

    public TotalMsgBuildConfig totalMsgBuild(Action<TotalMsgBuildConfig> action) {
        TotalMsgBuildConfig config = new TotalMsgBuildConfig();
        action.execute(config);
        return config;
    }

    public TotalMsgBuildConfig totalMsgBuild(Closure closure) {
        return ConfigureUtil.configure(closure, new TotalMsgBuildConfig());
    }

    public EveryMsgBuildConfig everyMsgBuild(Action<EveryMsgBuildConfig> action) {
        EveryMsgBuildConfig config = new EveryMsgBuildConfig();
        action.execute(config);
        return config;
    }

    public EveryMsgBuildConfig everyMsgBuild(Closure closure) {
        return ConfigureUtil.configure(closure, new EveryMsgBuildConfig());
    }

    public EveryProtoBuildConfig everyProtoBuild(Action<EveryProtoBuildConfig> action) {
        EveryProtoBuildConfig config = new EveryProtoBuildConfig();
        action.execute(config);
        return config;
    }

    public EveryProtoBuildConfig everyProtoBuild(Closure closure) {
        return ConfigureUtil.configure(closure, new EveryProtoBuildConfig());
    }
    //endregion
}
