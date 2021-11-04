package io.github.ceragon.protobuf.bean;

import com.google.protobuf.DescriptorProtos.SourceCodeInfo.Location;

public interface IProtoDesc {
    Location getLocation();

    /**
     * 获取proto文件中消息的注释 leadingComments，详见 https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/DescriptorProtos.SourceCodeInfo.Location.html#getLeadingComments--
     *
     * @return 首部注释信息
     */
    default String getLeadingComments() {
        return StringUtils.trimAndLine(getLocation().getLeadingComments());
    }

    /**
     * 获取proto文件中消息的注释 trailingComments, 详见 https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/DescriptorProtos.SourceCodeInfo.Location.html#getTrailingComments--
     *
     * @return 尾部的注释信息
     */
    default String getTrailingComments() {
        return StringUtils.trimAndLine(getLocation().getTrailingComments());
    }

    /**
     * 综合消息的所有注释，且会删掉首尾的换行符
     *
     * @return 消息的所有注释
     */
    default String getComments() {
        if (getLocation() == null) {
            return "";
        }
        String leadingComments = getLocation().getLeadingComments();
        String trailingComments = getLocation().getTrailingComments();
        if (StringUtils.isEmpty(leadingComments) && StringUtils.isEmpty(trailingComments)) {
            return "";
        }
        if (StringUtils.isEmpty(leadingComments)) {
            return getTrailingComments();
        }
        if (StringUtils.isEmpty(trailingComments)) {
            return getLeadingComments();
        }
        return getLeadingComments().replaceAll("[\r\n]", " | ") +
                " | " +
                getTrailingComments().replaceAll("[\r\n]", " | ");
    }
}
