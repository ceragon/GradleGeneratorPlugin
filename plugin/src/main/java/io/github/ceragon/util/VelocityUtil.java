package io.github.ceragon.util;


public class VelocityUtil {
    private final static VelocityUtil instance = new VelocityUtil();

    private VelocityUtil() {
    }

    public static VelocityUtil getInstance() {
        return instance;
    }

    public String upperFirstLetter(String value) {
        char[] cs = value.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    public boolean endsWith(String str, String endWith) {
        return str.toLowerCase().endsWith(endWith.toLowerCase());
    }
}
