package io.github.ceragon.protobuf.bean;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import lombok.Builder;
import lombok.Singular;

import java.util.List;

/**
 * 单个proto文件的描述信息
 */
@Builder
public class ProtoFileDesc {
    private FileDescriptorProto orig;
    @Singular(value = "message")
    private List<ProtoMessageDesc> messageList;

    /**
     * 获取 proto 文件原始描述信息，全部接口详见：https://developers.google.com/protocol-buffers/docs/reference/cpp/google.protobuf.descriptor?hl=en#FileDescriptor
     * @return 原始描述信息
     */
    public FileDescriptorProto getOrig() {
        return orig;
    }

    /**
     * 获取文件中定义的所有 message 的描述信息
     * @return 描述信息列表
     */
    public List<ProtoMessageDesc> getMessageList() {
        return messageList;
    }

    /**
     * proto文件的全名，包括.proto
     * @return 返回全名
     */
    public String getName() {
        return orig.getName();
    }

    /**
     * 获取proto中定义的 java_outer_classname 属性的值
     * @return 返回生成的java文件的名称
     */
    public String getJavaOuterClassname() {
        return orig.getOptions().getJavaOuterClassname();
    }

    /**
     * 获取proto中定义的 java_package 属性的值
     * @return 返回定义的java包名
     */
    public String getJavaPackage() {
        return orig.getOptions().getJavaPackage();
    }

    /**
     * 获取proto中定义的 java_multiple_files 属性的值
     * @return 是否将proto文件中的所有message散列成多个类，默认是生成一个类
     */
    public boolean getJavaMultipleFiles() {
        return orig.getOptions().getJavaMultipleFiles();
    }


}