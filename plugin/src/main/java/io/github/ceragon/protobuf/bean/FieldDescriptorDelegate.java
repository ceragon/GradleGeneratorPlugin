package io.github.ceragon.protobuf.bean;

import com.google.protobuf.DescriptorProtos.SourceCodeInfo;
import com.google.protobuf.DescriptorProtos.SourceCodeInfo.Location;
import com.google.protobuf.Descriptors.FieldDescriptor;
import lombok.Builder;

/**
 * 单个 message 中单个字段的描述信息代理类
 */
@Builder
public class FieldDescriptorDelegate implements IProtoDesc {
    private FieldDescriptor orig;
    private SourceCodeInfo sourceCodeInfo;
    private Location location;

    /**
     * 获取字段的原始描述信息，全部接口详见 https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/Descriptors.FieldDescriptor
     *
     * @return 原始描述信息
     */
    public FieldDescriptor getOrig() {
        return orig;
    }

    /**
     * 字段名称
     *
     * @return 名称
     */
    public String getName() {
        return orig.getName();
    }

    /**
     * 获取字段的java类型，如果是 enum 或者 message，则返回对应的名称
     *
     * @return java类型名称
     */
    public String getJavaTypeName() {
        switch (orig.getJavaType()) {
            case INT:
                return "int";
            case LONG:
                return "long";
            case FLOAT:
                return "float";
            case DOUBLE:
                return "double";
            case BOOLEAN:
                return "boolean";
            case STRING:
                return "String";
            case BYTE_STRING:
                return "ByteString";
            case ENUM:
                return orig.getEnumType().getFullName();
            case MESSAGE:
                return orig.getMessageType().getFullName();
            default:
                return "";
        }
    }
    /**
     * 消息字段的原始注释信息，详见：https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/DescriptorProtos.SourceCodeInfo.Location.html
     *
     * @return 注释原始信息
     */
    @Override
    public Location getLocation() {
        return location;
    }

    /**
     * 获取消息字段在proto文件中定义的序号
     * @return 序号
     */
    public int getNumber(){
        return orig.getNumber();
    }
}
