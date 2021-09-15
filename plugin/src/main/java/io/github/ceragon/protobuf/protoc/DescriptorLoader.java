package io.github.ceragon.protobuf.protoc;

import io.github.ceragon.protobuf.bean.ProtoFieldPojo;
import io.github.ceragon.protobuf.bean.ProtoFileDescPojo;
import io.github.ceragon.protobuf.bean.ProtoMessageDescPojo;
import io.github.ceragon.util.FileFilter;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DescriptorLoader {
    public static List<ProtoFileDescPojo> loadDesc(String descPathStr) {
        File descPath = new File(descPathStr);
        FileFilter fileFilter = new FileFilter(".desc");
        List<ProtoFileDescPojo> fileDescPojos = new ArrayList<>();
        if (!FileUtils.listFiles(descPath, fileFilter, TrueFileFilter.INSTANCE).stream()
                .allMatch(file -> buildFileDesc(fileDescPojos, file))) {
            return Collections.emptyList();
        }
        return fileDescPojos;
    }

    private static boolean buildFileDesc(List<ProtoFileDescPojo> protoFileDescPojoList, File descFile) {
        try (FileInputStream fin = new FileInputStream(descFile)) {
            FileDescriptorSet descriptorSet = FileDescriptorSet.parseFrom(fin);
            for (FileDescriptorProto fdp : descriptorSet.getFileList()) {
                ProtoFileDescPojo.ProtoFileDescPojoBuilder fileDescPojoBuilder = ProtoFileDescPojo.builder()
                        .orig(fdp);
                if (fdp.getMessageTypeCount() < 1) {
                    protoFileDescPojoList.add(fileDescPojoBuilder.build());
                    continue;
                }
                FileDescriptor fd = FileDescriptor.buildFrom(fdp, new FileDescriptor[]{});
                for (Descriptor descriptor : fd.getMessageTypes()) {
                    ProtoMessageDescPojo.ProtoMessageDescPojoBuilder messageBuilder = ProtoMessageDescPojo.builder()
                            .orig(descriptor);
                    for (FieldDescriptor fieldDescriptor : descriptor.getFields()) {
                        messageBuilder.field(ProtoFieldPojo.builder().orig(fieldDescriptor).build());
                    }
                    fileDescPojoBuilder.message(messageBuilder.build());
                }
                protoFileDescPojoList.add(fileDescPojoBuilder.build());
            }
            return true;
        } catch (IOException | DescriptorValidationException e) {
            throw new RuntimeException(e);
        }
    }
}
