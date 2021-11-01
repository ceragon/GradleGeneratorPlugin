package io.github.ceragon.protobuf.bean;

class StringUtils extends org.apache.commons.lang3.StringUtils {
    /**
     * 将字符串的空格 和 结尾的换行去除
     * @param value 字符串
     * @return 处理后的结果
     */
    public static String trimAndLine(String value) {
        if (isEmpty(value)) {
            return "";
        }
        value = value.trim();
        if (value.endsWith("\r")) {
            return trimAndLine(value.substring(0, value.length() - 1));
        }
        if (value.endsWith("\n")) {
            return trimAndLine(value.substring(0, value.length() - 1));
        }
        return value;
    }
}
