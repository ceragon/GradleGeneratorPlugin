package io.github.ceragon.protobuf.bean;

import com.google.protobuf.DescriptorProtos.SourceCodeInfo.Location;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.UnknownFieldSet.Field;
import io.github.ceragon.util.StringUtils;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * proto的单个消息的描述信息
 */
@Builder
public class ProtoMessageDesc {
    private Descriptor orig;
    private Location location;
    @Singular("field")
    private List<ProtoMessageFieldDesc> fieldList;

    /**
     * 消息的原始描述信息, 全部接口详见 https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/Descriptors.Descriptor.html
     *
     * @return 原始描述信息
     */
    public Descriptor getOrig() {
        return orig;
    }

    /**
     * 消息的原始注释信息，详见：https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/DescriptorProtos.SourceCodeInfo.Location.html
     * @return
     */
    public Location getLocation() {
        return location;
    }

    /**
     * 获取消息的全部字段描述信息
     *
     * @return 全部的字段描述信息
     */
    public List<ProtoMessageFieldDesc> getFieldList() {
        return fieldList;
    }

    private Field getOptionField(int number) {
        UnknownFieldSet unknownFieldSet = orig.getOptions().getUnknownFields();
        if (!unknownFieldSet.hasField(number)) {
            return null;
        }
        return orig.getOptions().getUnknownFields().getField(number);
    }

    /**
     * 获取消息中定义的 option 字段，返回值为整型
     *
     * @param number option 字段的唯一Id，详见 https://developers.google.com/protocol-buffers/docs/overview?hl=en#options
     * @return 将 option 字段的值以整型返回，默认值为 0
     */
    public long getOptionInt(int number) {
        Field field = getOptionField(number);
        if (field == null) {
            return 0;
        }
        if (field.getVarintList().isEmpty()) {
            return 0;
        }
        return field.getVarintList().get(0);
    }

    /**
     * 获取 message 所在 proto 文件中定义的 java 包名
     *
     * @return java 包名
     */
    public String getJavaPackage() {
        return orig.getFile().getOptions().getJavaPackage();
    }

    /**
     * 获取消息的名称
     *
     * @return 消息名称
     */
    public String getName() {
        return orig.getName();
    }

    /**
     * 获取此 message 中依赖的包名。(该方法目前处于测试状态)
     * 如：字段是枚举类型，字段是另外一个 message 类型, 字段是 proto的 ByteString 类型
     *
     * @return 依赖的所有包名
     */
    public List<String> getDependencyPackage() {
        return fieldList.stream()
                .filter(field -> {
                    switch (field.getOrig().getJavaType()) {
                        case BYTE_STRING:
                        case ENUM:
                        case MESSAGE:
                            return true;
                        default:
                            return false;
                    }
                }).map(field -> {
                    switch (field.getOrig().getJavaType()) {
                        case ENUM:
                            return field.getOrig().getEnumType().getFullName();
                        case MESSAGE:
                            return field.getOrig().getMessageType().getFullName();
                        case BYTE_STRING:
                            return "com.google.protobuf.ByteString";
                        default:
                            return "";
                    }
                }).collect(Collectors.toList());
    }

    /**
     * 获取proto文件中消息的注释 leadingComments，详见 https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/DescriptorProtos.SourceCodeInfo.Location.html#getLeadingComments--
     * @return
     */
    public String getLeadingComments() {
        return StringUtils.trimAndLine(location.getLeadingComments());
    }

    /**
     * 获取proto文件中消息的注释 trailingComments, 详见 https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/DescriptorProtos.SourceCodeInfo.Location.html#getTrailingComments--
     * @return
     */
    public String getTrailingComments() {
        return StringUtils.trimAndLine(location.getTrailingComments());
    }

    /**
     * 综合消息的所有注释，且会删掉首尾的换行符
     * @return 消息的所有注释
     */
    public String getComments() {
        if (location == null) {
            return "";
        }
        if (StringUtils.isEmpty(location.getLeadingComments())) {
            return getTrailingComments();
        }
        if (StringUtils.isEmpty(location.getTrailingComments())) {
            return getLeadingComments();
        }
        return getLeadingComments() + "\r\n" + getTrailingComments();
    }
}
