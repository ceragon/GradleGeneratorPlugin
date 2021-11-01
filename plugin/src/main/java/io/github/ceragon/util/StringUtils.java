package io.github.ceragon.util;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import java.util.Map;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    private final static ParameterizedMessageFactory factory = ParameterizedMessageFactory.INSTANCE;

    public static String format(String message, Object... params) {
        return factory.newMessage(message, params).getFormattedMessage();
    }

    /**
     * 例如 message = "hello ${name}" , value = ["name":"kevin"] , 结果是 return = "hello kevin"
     *
     * @param message
     * @param key
     * @param value
     * @return
     */
    public static String formatKV(String message, String key, Object value) {
        return formatKV(message, Maps.of(key, value));
    }

    public static String formatKV(String message, Map<String, Object> params) {
        return StrSubstitutor.replace(message, params);
    }

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
