package com.ceragon.protobuf.extension;

import com.ceragon.PluginContext;
import com.github.os72.protocjar.ProtocVersion;
import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.tasks.Internal;
import org.gradle.util.internal.ConfigureUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProtoExtension {
    @Internal("protoc 的版本号")
    public String protocVersion = ProtocVersion.PROTOC_VERSION.mVersion;

    @Internal("proto 文件的目录")
    public List<File> protoFilePaths;

    @Internal("输出配置")
    public List<OutputTarget> outputTargets = new ArrayList<>();

    public List<TotalMsgBuildConfig> totalMsgBuild = new ArrayList<>();
    public List<EveryMsgBuildConfig> everyMsgBuild = new ArrayList<>();
    public List<EveryProtoBuildConfig> everyProtoBuild = new ArrayList<>();

    public ProtoExtension() {
        protoFilePaths = List.of(
                new File(PluginContext.pathFormat().format("${project.base.dir}${s}proto"))
        );
    }

    public String getProtocVersion() {
        return protocVersion;
    }

    public List<File> getProtoFilePaths() {
        return protoFilePaths;
    }

    public List<OutputTarget> getOutputTargets() {
        return outputTargets;
    }

    public OutputTarget outputTarget(Action<OutputTarget> action) {
        OutputTarget outputTarget = new OutputTarget();
        action.execute(outputTarget);
        return outputTarget;
    }

    public OutputTarget outputTarget(Closure closure) {
        return ConfigureUtil.configure(closure, new OutputTarget());
    }

    public void totalMsgBuild(Action<TotalMsgBuildConfig> action) {
        TotalMsgBuildConfig config = new TotalMsgBuildConfig();
        action.execute(config);
        totalMsgBuild.add(config);
    }

    public void totalMsgBuild(Closure closure) {
        totalMsgBuild.add(ConfigureUtil.configure(closure, new TotalMsgBuildConfig()));
    }

    public void everyMsgBuild(Action<EveryMsgBuildConfig> action) {
        EveryMsgBuildConfig config = new EveryMsgBuildConfig();
        action.execute(config);
        everyMsgBuild.add(config);
    }

    public void everyMsgBuild(Closure closure) {
        everyMsgBuild.add(ConfigureUtil.configure(closure, new EveryMsgBuildConfig()));
    }

    public void everyProtoBuild(Action<EveryProtoBuildConfig> action) {
        EveryProtoBuildConfig config = new EveryProtoBuildConfig();
        action.execute(config);
        everyProtoBuild.add(config);
    }

    public void everyProtoBuild(Closure closure) {
        everyProtoBuild.add(ConfigureUtil.configure(closure, new EveryProtoBuildConfig()));
    }
}
