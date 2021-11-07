package io.github.ceragon.protobuf.bean;

import com.google.protobuf.DescriptorProtos.SourceCodeInfo;
import com.google.protobuf.DescriptorProtos.SourceCodeInfo.Location;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.UnknownFieldSet.Field;
import lombok.Builder;
import lombok.Singular;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * proto的单个消息的描述信息
 */
@Builder
public class EnumDescriptorDelegate implements IProtoDesc {
    private EnumDescriptor orig;
    private SourceCodeInfo sourceCodeInfo;
    private Location location;
    private List<EnumValueDescriptorDelegate> valueList;

    /**
     * 消息的原始描述信息, 全部接口详见 https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/Descriptors.Descriptor.html
     *
     * @return 原始描述信息
     */
    public EnumDescriptor getOrig() {
        return orig;
    }

    /**
     * 消息的原始注释信息，详见：https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/DescriptorProtos.SourceCodeInfo.Location.html
     *
     * @return 注释原始信息
     */
    public Location getLocation() {
        return location;
    }

    public List<EnumValueDescriptorDelegate> getValueList() {
        if (valueList == null) {
            valueList = orig.getValues().stream().map(descriptor -> {
                Location location = DescriptorUtils.getLocationOfEnumValue(sourceCodeInfo, this.location.getPathList(), descriptor.getIndex());
                return EnumValueDescriptorDelegate.builder().orig(descriptor).location(location).build();
            }).collect(Collectors.toList());
        }
        return valueList;
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

}
