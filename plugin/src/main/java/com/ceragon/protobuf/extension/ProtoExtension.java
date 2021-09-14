package com.ceragon.protobuf.extension;

import com.ceragon.PluginContext;
import com.github.os72.protocjar.ProtocVersion;
import groovy.lang.Closure;
import lombok.Data;
import lombok.Getter;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.tasks.Internal;
import org.gradle.util.internal.ConfigureUtil;

import javax.inject.Inject;
import java.util.List;

@Data
public class ProtoExtension {
    @Internal("protoc 的版本号")
    private String protocVersion = ProtocVersion.PROTOC_VERSION.mVersion;

    @Internal("proto 文件的目录")
    private List<String> protoFilePaths;

    @Internal("输出配置")
    private final NamedDomainObjectContainer<OutputTarget> outputTargets;
    @Internal("模板文件的目录")
    private String templatePath;
    @Internal("使用全部消息来生成代码")
    private final NamedDomainObjectContainer<TotalMsgBuildConfig> totalMsgBuilds;
    @Internal("遍历每个消息来生成代码")
    private final NamedDomainObjectContainer<EveryMsgBuildConfig> everyMsgBuilds;
    @Internal("遍历每个proto文件来生成代码")
    private final NamedDomainObjectContainer<EveryProtoBuildConfig> everyProtoBuilds;

    @Inject
    public ProtoExtension(Project project) {
        protoFilePaths = List.of(
                PluginContext.pathFormat().format("${project.base.dir}${s}protofiles")
        );
        outputTargets = project.container(OutputTarget.class);
        totalMsgBuilds = project.container(TotalMsgBuildConfig.class);
        everyMsgBuilds = project.container(EveryMsgBuildConfig.class);
        everyProtoBuilds = project.container(EveryProtoBuildConfig.class);
        templatePath = PluginContext.pathFormat().format("${project.base.dir}${s}template");
    }

    //region 闭包方法区
    public String baseDir(String path) {
        return PluginContext.pathFormat().format("${project.base.dir}${s}" + path);
    }

    public void outputTargets(Closure<?> block) {
        ConfigureUtil.configure(block, this.outputTargets);
    }

    public void outputTargets(Action<NamedDomainObjectContainer<OutputTarget>> block) {
        block.execute(this.outputTargets);
    }

    public void totalMsgBuilds(Closure<?> block) {
        ConfigureUtil.configure(block, this.totalMsgBuilds);
    }

    public void totalMsgBuilds(Action<NamedDomainObjectContainer<TotalMsgBuildConfig>> block) {
        block.execute(this.totalMsgBuilds);
    }

    public void everyMsgBuilds(Closure<?> block) {
        ConfigureUtil.configure(block, this.everyMsgBuilds);
    }

    public void everyMsgBuilds(Action<NamedDomainObjectContainer<EveryMsgBuildConfig>> block) {
        block.execute(this.everyMsgBuilds);
    }

    public void everyProtoBuilds(Closure<?> block) {
        ConfigureUtil.configure(block, this.everyProtoBuilds);
    }

    public void everyProtoBuilds(Action<NamedDomainObjectContainer<EveryProtoBuildConfig>> block) {
        block.execute(this.everyProtoBuilds);
    }
    //endregion
}
