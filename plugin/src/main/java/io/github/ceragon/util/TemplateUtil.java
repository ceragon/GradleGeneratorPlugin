package io.github.ceragon.util;

/**
 * 模板引擎的工具类
 */
public class TemplateUtil {
    private final static TemplateUtil instance = new TemplateUtil();

    private TemplateUtil() {
    }

    public static TemplateUtil getInstance() {
        return instance;
    }

    /**
     * 首字母大写
     * @param value 字符串
     * @return 首字母大写后的字符串
     */
    public String upperFirstLetter(String value) {
        char[] cs = value.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    /**
     * 判断一个字符串是否以 endWith 字符串结尾，比较过程忽略大小写
     * @param str 目标字符串
     * @param endWith 结尾的字符串
     * @return true：表示匹配成功
     */
    public boolean endsWith(String str, String endWith) {
        return str.toLowerCase().endsWith(endWith.toLowerCase());
    }
}
