package com.ceragon.protobuf.bean;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ProtoFileDescPojo {
    FileDescriptorProto orig;
    @Singular(value = "message")
    List<ProtoMessageDescPojo> messageList;

    public String getName() {
        return orig.getName();
    }

    public String getJavaOuterClassname() {
        return orig.getOptions().getJavaOuterClassname();
    }

    public String getJavaPackage() {
        return orig.getOptions().getJavaPackage();
    }

    public boolean getJavaMultipleFiles() {
        return orig.getOptions().getJavaMultipleFiles();
    }


}
