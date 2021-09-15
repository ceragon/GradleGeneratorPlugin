package io.github.ceragon.protobuf.bean;

import com.google.protobuf.Descriptors.FieldDescriptor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProtoFieldPojo {
    FieldDescriptor orig;
    public String getName() {
        return orig.getName();
    }
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
}
