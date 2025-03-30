package com.bank.duty.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类
 */
public class StringUtil extends StringUtils {

    /** 空字符串 */
    private static final String NULLSTR = "";

    /** 下划线 */
    private static final char SEPARATOR = '_';

    /**
     * 判断一个对象是否为空
     *
     * @param object 对象
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * 判断一个对象是否非空
     *
     * @param object 对象
     * @return true：非空 false：空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    /**
     * 判断一个字符串是否为空串
     *
     * @param str 字符串
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(String str) {
        return isNull(str) || NULLSTR.equals(str.trim());
    }

    /**
     * 判断一个字符串是否为非空串
     *
     * @param str 字符串
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断一个字符串是否包含指定的子字符串列表中的任何一个，忽略大小写
     *
     * @param str       要检查的字符串
     * @param subStrs   子字符串列表
     * @return 如果字符串包含子字符串列表中的任何一个，则返回true
     */
    public static boolean inStringIgnoreCase(String str, String... subStrs) {
        if (str == null || subStrs == null) {
            return false;
        }

        for (String subStr : subStrs) {
            if (subStr != null && str.toLowerCase().contains(subStr.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 驼峰式命名法 例如：user_name->userName
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线命名法 例如：userName->user_name
     */
    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase = true;
        // 当前字符是否大写
        boolean curreCharIsUpperCase = true;
        // 下一字符是否大写
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1)) {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
                sb.append(SEPARATOR);
            } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 格式化文本, {} 表示占位符
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params 参数值
     * @return 格式化后的文本
     */
    public static String format(String template, Object... params) {
        if (isEmpty(template) || params == null || params.length == 0) {
            return template;
        }

        final int length = template.length();
        StringBuilder result = new StringBuilder(length + 50);

        int paramIndex = 0;
        int textIndex = 0;
        int index;
        while ((index = template.indexOf("{}", textIndex)) != -1) {
            if (paramIndex < params.length) {
                result.append(template, textIndex, index);
                result.append(params[paramIndex++]);
                textIndex = index + 2;
            } else {
                // 参数不够，直接返回
                result.append(template, textIndex, length);
                break;
            }
        }

        // 加入剩余部分
        if (textIndex < length) {
            result.append(template, textIndex, length);
        }

        return result.toString();
    }
}