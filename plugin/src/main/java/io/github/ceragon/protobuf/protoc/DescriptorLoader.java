package io.github.ceragon.protobuf.protoc;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.DescriptorProtos.SourceCodeInfo;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FileDescriptor;
import io.github.ceragon.PluginContext;
import io.github.ceragon.protobuf.bean.FileDescriptorDelegate;
import io.github.ceragon.protobuf.extension.OutputBigDescriptor;
import io.github.ceragon.protobuf.protoc.util.CommandUtil;
import io.github.ceragon.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DescriptorLoader {
    public static List<FileDescriptorDelegate> loadDesc(String descPathStr) {
        try (FileInputStream fin = new FileInputStream(descPathStr)) {
            FileDescriptorSet descriptorSet = FileDescriptorSet.parseFrom(fin);
            return descriptorSet.getFileList().stream().map(DescriptorLoader::toFileDesc).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static FileDescriptorDelegate toFileDesc(FileDescriptorProto fdp) {
        SourceCodeInfo sourceCodeInfo = fdp.getSourceCodeInfo();
        try {
            FileDescriptor fd = FileDescriptor.buildFrom(fdp, new FileDescriptor[]{}, true);
            return FileDescriptorDelegate.builder().orig(fdp).fdOrig(fd).sourceCodeInfo(sourceCodeInfo).build();
        } catch (DescriptorValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public static OutputBigDescriptor createOutputDescriptor(String protocCommand, String protocVersion) {
        OutputBigDescriptor descriptor = new OutputBigDescriptor("temp-descriptor");
        descriptor.setIncludeImports(false);
        descriptor.setIncludeSourceInfo(true);
        if (!StringUtils.isEmpty(protocCommand)) {
            String theVersion = CommandUtil.getVersion(protocCommand);
            if (theVersion == null || theVersion.equals("2.4.1")) {
                descriptor.setIncludeSourceInfo(false);
            }
        }
        if ("2.4.1".equals(protocVersion)) {
            descriptor.setIncludeSourceInfo(false);
        }
        descriptor.setOutputFile(PluginContext.pathFormat().format("${project.build.dir}/temp-descriptor/temp.desc"));
        return descriptor;
    }
}
