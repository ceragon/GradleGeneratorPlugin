package io.github.ceragon.protobuf.protoc;


import io.github.ceragon.PluginContext;
import io.github.ceragon.protobuf.constant.ContextKey;
import io.github.ceragon.protobuf.extension.OutputBigDescriptor;
import io.github.ceragon.protobuf.extension.OutputTarget;
import io.github.ceragon.protobuf.protoc.util.CommandUtil;
import io.github.ceragon.protobuf.protoc.util.FileUtil;
import io.github.ceragon.protobuf.protoc.util.IncludeUtil;
import io.github.ceragon.protobuf.protoc.util.OutputTargetUtil;
import io.github.ceragon.util.BuildContext;
import io.github.ceragon.util.FileFilter;
import io.github.ceragon.util.PluginTaskException;
import io.github.ceragon.util.StringUtils;
import com.github.os72.protocjar.ProtocVersion;
import lombok.Builder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.gradle.api.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public class ProtocBuild {
    private final static String extension = ".proto";
    private static final String DEFAULT_INPUT_DIR = "/src/main/resources/".replace('/', File.separatorChar);

    String protocVersion;
    String protocCommand;
    List<String> inputDirectories;
    File[] includeDirectories;
    boolean includeStdTypes;
    boolean includeImports;

    public void process(Collection<OutputTarget> outputTargets, List<OutputBigDescriptor> outputBigDescriptors) throws PluginTaskException {
        BuildContext context = PluginContext.buildContext();
        final Project project = PluginContext.project();
        // 默认值
        if (StringUtils.isEmpty(protocVersion)) {
            protocVersion = ProtocVersion.PROTOC_VERSION.mVersion;
        }
        if (inputDirectories == null) {
            inputDirectories = new ArrayList<>();
        }
        if (inputDirectories.isEmpty()) {
            inputDirectories.add(project.getBuildDir().getAbsolutePath() + DEFAULT_INPUT_DIR);
        }

        context.setValue(ContextKey.PROTOC_VERSION, protocVersion);
        context.setValue(ContextKey.PROTO_EXTENSION, extension);
        context.setValue(ContextKey.INCLUDE_IMPORTS, includeImports);

        // 初始化outputTarget
        outputTargets.forEach(OutputTargetUtil::initTarget);

        // 封装protoc的程序完整路径
        String protocCommand = CommandUtil.buildProtocExe(this.protocCommand, protocVersion);

        List<File> includeDirectoryList = IncludeUtil.buildIncludePath(includeStdTypes, includeDirectories);

        for (OutputTarget target : outputTargets) {
            OutputTargetUtil.preprocessTarget(target);
        }

        for (OutputTarget target : outputTargets) {
            OutputTargetUtil.processTarget(protocCommand, inputDirectories, includeDirectoryList, target);
        }

        for (OutputBigDescriptor descriptor : outputBigDescriptors) {
            OutputTargetUtil.preprocessDescriptor(descriptor);
        }

        FileFilter fileFilter = new FileFilter(extension);
        for (OutputBigDescriptor descriptor : outputBigDescriptors) {
            List<File> inputFileList = inputDirectories.stream()
                    .map(File::new)
                    .flatMap(dirFile -> FileUtils.listFiles(dirFile, fileFilter, TrueFileFilter.INSTANCE).stream())
                    .filter(file -> FileUtil.nameFilter(descriptor.getProtoNameMatch(), file.getName()))
                    .collect(Collectors.toList());
            OutputTargetUtil.processDescriptor(protocCommand, inputFileList, includeDirectoryList, descriptor);
        }
    }


}
