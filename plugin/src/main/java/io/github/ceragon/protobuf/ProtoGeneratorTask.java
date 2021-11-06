package io.github.ceragon.protobuf;

import io.github.ceragon.protobuf.bean.FileDescriptorDelegate;
import io.github.ceragon.protobuf.extension.OutputBigDescriptor;
import io.github.ceragon.protobuf.extension.ProtoExtension;
import io.github.ceragon.protobuf.protoc.DescriptorLoader;
import io.github.ceragon.protobuf.protoc.MsgCodeBuild;
import io.github.ceragon.protobuf.protoc.ProtocBuild;
import io.github.ceragon.util.PluginTaskException;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskExecutionException;

import java.util.ArrayList;
import java.util.List;

public class ProtoGeneratorTask {
    private final ProtoExtension extension;

    public ProtoGeneratorTask(ProtoExtension extension) {
        this.extension = extension;
    }

    public void execute(Task task) {
        try {
            PluginTaskException.taskNameLocal.set(task.getName());
            prepareExecute(task);

            ProtocBuild protocBuild = ProtocBuild.builder()
                    .protocVersion(extension.getProtocVersion())
                    .protocCommand(extension.getProtocCommand())
                    .inputDirectories(extension.getProtoFilePaths())
                    .includeStdTypes(true)
                    .includeImports(false)
                    .build();

            // 生成目标proto格式，以及描述信息
            List<OutputBigDescriptor> outputBigDescriptors = new ArrayList<>(extension.getOutputBigDescriptors());
            OutputBigDescriptor descriptor = DescriptorLoader.createOutputDescriptor(extension.getProtocCommand(), extension.getProtocVersion());
            outputBigDescriptors.add(descriptor);

            protocBuild.process(extension.getOutputTargets(), outputBigDescriptors);

            // 加载描述信息
            List<FileDescriptorDelegate> protoFileDescList = DescriptorLoader.loadDesc(descriptor.getOutputFile());

            MsgCodeBuild msgCodeBuild = new MsgCodeBuild(protoFileDescList);

            String resourceRoot = extension.getTemplatePath();

            if (!msgCodeBuild.buildTotalMsgCode(resourceRoot, extension.getTotalMsgBuilds())) {
                throw new PluginTaskException("build totalMsg code error");
            }
            if (!msgCodeBuild.buildEveryMsgCode(resourceRoot, extension.getEveryMsgBuilds())) {
                throw new PluginTaskException("build everyMsg code error");
            }
            if (!msgCodeBuild.buildEveryProtoCode(resourceRoot, extension.getEveryProtoBuilds())) {
                throw new PluginTaskException("build everyProto code error");
            }
            if (!msgCodeBuild.buildEveryEnumCode(resourceRoot, extension.getEveryEnumBuilds())) {
                throw new PluginTaskException("build everyEnum code error");
            }
        } catch (Throwable e) {
            throw new TaskExecutionException(task, e);
        }
    }

    private void prepareExecute(Task task) {
        if (extension.getDeleteFiles() != null) {
            task.getProject().delete(extension.getDeleteFiles());
        }
        if (extension.getDeleteAction() != null) {
            task.getProject().delete(extension.getDeleteAction());
        }
        if ("Mac OS X".equals(System.getProperty("os.name"))
                && "aarch64".equals(System.getProperty("os.arch"))) {
            // 苹果m1电脑，改为x86_64 架构
            System.setProperty("os.arch", "x86_64");
        }
    }
}
