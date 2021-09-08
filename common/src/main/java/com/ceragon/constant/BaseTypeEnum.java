package com.ceragon.constant;

import org.apache.commons.lang.StringUtils;

public enum BaseTypeEnum {
    // 数字类型
    number {
        @Override
        public boolean checkMatch(Object value) {
            return StringUtils.isNumeric(value.toString());
        }
    },
    // 字符串类型
    string {
        @Override
        public boolean checkMatch(Object value) {
            return value instanceof String;
        }
    },
    ;

    public abstract boolean checkMatch(Object value);
}
