package io.github.ceragon.protobuf.extension;

import com.github.os72.protocjar.ProtocVersion;
import groovy.lang.Closure;
import io.github.ceragon.PluginContext;
import lombok.Data;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.file.DeleteSpec;
import org.gradle.api.tasks.Internal;
import org.gradle.util.internal.ConfigureUtil;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

@Data
public class ProtoExtension {
    @Internal("需要删除的文件列表")
    private Object[] deleteFiles;
    private Action<? super DeleteSpec> deleteAction;
    @Internal("protoc 的版本号")
    private String protocVersion = ProtocVersion.PROTOC_VERSION.mVersion;
    @Internal("自定义的protoc路径")
    private String protocCommand;
    @Internal("proto 文件的目录")
    private List<String> protoFilePaths;

    @Internal("输出配置")
    private final NamedDomainObjectContainer<OutputTarget> outputTargets;
    @Internal("匹配多个proto文件，生成一个大的描述信息")
    private final NamedDomainObjectContainer<OutputBigDescriptor> outputBigDescriptors;
    @Internal("模板文件的目录")
    private String templatePath;
    @Internal("使用全部消息来生成代码")
    private final NamedDomainObjectContainer<TotalMsgBuildConfig> totalMsgBuilds;
    @Internal("遍历每个消息来生成代码")
    private final NamedDomainObjectContainer<EveryMsgBuildConfig> everyMsgBuilds;
    @Internal("遍历每个枚举来生成代码")
    private final NamedDomainObjectContainer<EveryEnumBuildConfig> everyEnumBuilds;
    @Internal("遍历每个proto文件来生成代码")
    private final NamedDomainObjectContainer<EveryProtoBuildConfig> everyProtoBuilds;

    @Inject
    public ProtoExtension(Project project) {
        protoFilePaths = Collections.singletonList(
                PluginContext.pathFormat().format("${project.base.dir}${s}protofiles")
        );
        outputTargets = project.container(OutputTarget.class);
        outputBigDescriptors = project.container(OutputBigDescriptor.class);
        totalMsgBuilds = project.container(TotalMsgBuildConfig.class);
        everyMsgBuilds = project.container(EveryMsgBuildConfig.class);
        everyEnumBuilds = project.container(EveryEnumBuildConfig.class);
        everyProtoBuilds = project.container(EveryProtoBuildConfig.class);
        templatePath = PluginContext.pathFormat().format("${project.base.dir}${s}template");
    }

    //region 闭包方法区
    public String baseDir(String path) {
        return PluginContext.pathFormat().format("${project.base.dir}${s}" + path);
    }

    public void deleteFile(Object... targetFile) {
        this.deleteFiles = targetFile;
    }

    public void deleteFile(Action<? super DeleteSpec> deleteAction) {
        this.deleteAction = deleteAction;
    }

    public void outputTargets(Closure<?> block) {
        ConfigureUtil.configure(block, this.outputTargets);
    }

    public void outputTargets(Action<NamedDomainObjectContainer<OutputTarget>> block) {
        block.execute(this.outputTargets);
    }

    public void outputBigDescriptors(Closure<?> block) {
        ConfigureUtil.configure(block, this.outputBigDescriptors);
    }

    public void outputBigDescriptors(Action<NamedDomainObjectContainer<OutputBigDescriptor>> block) {
        block.execute(this.outputBigDescriptors);
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

    public void everyEnumBuilds(Closure<?> block) {
        ConfigureUtil.configure(block, this.everyEnumBuilds);
    }

    public void everyEnumBuilds(Action<NamedDomainObjectContainer<EveryEnumBuildConfig>> block) {
        block.execute(this.everyEnumBuilds);
    }

    public void everyProtoBuilds(Closure<?> block) {
        ConfigureUtil.configure(block, this.everyProtoBuilds);
    }

    public void everyProtoBuilds(Action<NamedDomainObjectContainer<EveryProtoBuildConfig>> block) {
        block.execute(this.everyProtoBuilds);
    }
    //endregion
}
