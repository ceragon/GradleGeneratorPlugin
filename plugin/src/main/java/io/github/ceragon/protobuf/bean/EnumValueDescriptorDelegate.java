package io.github.ceragon.protobuf.bean;

import com.google.protobuf.DescriptorProtos.SourceCodeInfo.Location;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import lombok.Builder;

/**
 * 枚举值的描述信息代理类
 */
@Builder
public class EnumValueDescriptorDelegate implements IProtoDesc {
    private EnumValueDescriptor orig;
    private Location location;

    /**
     * 枚举值的原始描述信息, 全部接口详见 https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/Descriptors.EnumValueDescriptor.html
     *
     * @return 原始描述信息
     */
    public EnumValueDescriptor getOrig() {
        return orig;
    }
    /**
     * 枚举的原始注释信息，详见：https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/DescriptorProtos.SourceCodeInfo.Location.html
     *
     * @return 注释原始信息
     */
    @Override
    public Location getLocation() {
        return location;
    }

    /**
     * 获取枚举值的名字
     * @return 名字
     */
    public String getName() {
        return orig.getName();
    }

    /**
     * 获取枚举值在proto文件中定义的序号
     * @return 序号
     */
    public int getNumber() {
        return orig.getNumber();
    }
}
