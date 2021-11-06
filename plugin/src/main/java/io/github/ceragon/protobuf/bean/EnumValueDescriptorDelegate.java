package io.github.ceragon.protobuf.bean;

import com.google.protobuf.DescriptorProtos.SourceCodeInfo.Location;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import lombok.Builder;

@Builder
public class EnumValueDescriptorDelegate implements IProtoDesc {
    private EnumValueDescriptor orig;
    private Location location;

    @Override
    public Location getLocation() {
        return location;
    }

    public String getName() {
        return orig.getName();
    }

    public int getNumber() {
        return orig.getNumber();
    }
}
