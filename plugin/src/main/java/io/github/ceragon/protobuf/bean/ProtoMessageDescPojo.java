package io.github.ceragon.protobuf.bean;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.UnknownFieldSet.Field;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
public class ProtoMessageDescPojo {
    Descriptor orig;
    @Singular("field")
    List<ProtoFieldPojo> fieldList;

    public long getOptionInt(int number) {
        UnknownFieldSet unknownFieldSet = orig.getOptions().getUnknownFields();
        if (!unknownFieldSet.hasField(number)) {
            return 0;
        }
        Field field = orig.getOptions().getUnknownFields().getField(number);
        if (field.getVarintList().isEmpty()) {
            return 0;
        }
        return field.getVarintList().get(0);
    }

    public String getJavaPackage() {
        return orig.getFile().getOptions().getJavaPackage();
    }

    public String getName() {
        return orig.getName();
    }

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
}
