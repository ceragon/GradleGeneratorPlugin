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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class DescriptorLoader {
    private final static Set<String> SPECIAL_DECENDENCYS = new HashSet<String>() {
        {
            add("google/protobuf/descriptor.proto");
        }
    };
    private static Map<String, FileDescriptorProto> allFdpMap;
    private static final ConcurrentMap<String, FileDescriptor> descriptorMap = new ConcurrentHashMap<>();

    public static List<FileDescriptorDelegate> loadDesc(String descPathStr) {
        try (FileInputStream fin = new FileInputStream(descPathStr)) {
            FileDescriptorSet descriptorSet = FileDescriptorSet.parseFrom(fin);
            allFdpMap = descriptorSet.getFileList().stream()
                    .collect(HashMap::new, (fdpMap, fdp) -> fdpMap.put(fdp.getName(), fdp), Map::putAll);
            return descriptorSet.getFileList().stream()
                    .map(DescriptorLoader::toFileDesc).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            descriptorMap.clear();
        }
    }


    private static FileDescriptorDelegate toFileDesc(FileDescriptorProto fdp) {
        SourceCodeInfo sourceCodeInfo = fdp.getSourceCodeInfo();
        FileDescriptor fd = descriptorMap.computeIfAbsent(fdp.getName(), DescriptorLoader::loadFileDescriptor);
        return FileDescriptorDelegate.builder().orig(fdp).fdOrig(fd).sourceCodeInfo(sourceCodeInfo).build();
    }

    private static FileDescriptor loadFileDescriptor(String name) {
        FileDescriptorProto fdp = allFdpMap.get(name);
        if (fdp == null) {
            throw new RuntimeException(String.format("the FileDescriptorProto=%s is not exist!", name));
        }
        List<FileDescriptor> descriptors = new ArrayList<>();
        for (String dependency : fdp.getDependencyList()) {
            if (SPECIAL_DECENDENCYS.contains(dependency)) {
                continue;
            }
            descriptors.add(descriptorMap.computeIfAbsent(dependency, DescriptorLoader::loadFileDescriptor));
        }
        try {
            return FileDescriptor.buildFrom(fdp, descriptors.toArray(new FileDescriptor[0]), true);
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
