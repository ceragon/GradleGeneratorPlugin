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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DescriptorLoader {
    private final static Set<String> SPECIAL_DECENDENCYS = new HashSet<String>(){
        {
            add("google/protobuf/descriptor.proto");
        }
    };
    private static ConcurrentMap<String, FileDescriptor> descriptorMap = new ConcurrentHashMap<>();
    public static List<FileDescriptorDelegate> loadDesc(String descPathStr) {
        try (FileInputStream fin = new FileInputStream(descPathStr)) {
            FileDescriptorSet descriptorSet = FileDescriptorSet.parseFrom(fin);
            return descriptorSet.getFileList().stream().parallel()
                    .map(DescriptorLoader::toFileDesc).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            descriptorMap.clear();
        }
    }


    private static FileDescriptorDelegate toFileDesc(FileDescriptorProto fdp) {
        SourceCodeInfo sourceCodeInfo = fdp.getSourceCodeInfo();
        try {
            FileDescriptor fd;
            if (fdp.getDependencyList().isEmpty()) {
                fd = FileDescriptor.buildFrom(fdp, new FileDescriptor[]{}, true);
            } else {
                List<FileDescriptor> descriptors = new ArrayList<>();
                for (String dependency : fdp.getDependencyList()) {
                    if (SPECIAL_DECENDENCYS.contains(dependency)) {
                        continue;
                    }
                    FileDescriptor descriptor;
                    do{
                        descriptor = descriptorMap.get(dependency);
                        if (descriptor == null) {
                            TimeUnit.SECONDS.sleep(1);
                        }
                    }while(descriptor == null);
                    descriptors.add(descriptor);
                }
                fd = FileDescriptor.buildFrom(fdp, descriptors.toArray(new FileDescriptor[0]), true);;
            }
            descriptorMap.put(fdp.getName(), fd);
            return FileDescriptorDelegate.builder().orig(fdp).fdOrig(fd).sourceCodeInfo(sourceCodeInfo).build();
        } catch (DescriptorValidationException | InterruptedException e) {
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
