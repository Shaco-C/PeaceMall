package com.peacemall.user.utils;

import java.util.regex.Pattern;

/**
 * 密码校验工具类
 * 规则：
 * 1. 长度8-16个字符
 * 2. 只能包含字母、数字和部分安全特殊字符（!@#$%^&*()_+-=）
 * 3. 禁止包含空格、单引号、分号、问号等危险字符
 */
public class PasswordValidator {

    // 私有构造方法防止实例化
    private PasswordValidator() {}

    // 预编译正则表达式，提升性能
    // 字符串开始
    // 至少包含一个字母（大小写）
    // 至少包含一个数字
    // 允许的字符集
    // 长度8-16
    // 字符串结束
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^" +
            "(?=.*[A-Za-z])" +
            "(?=.*\\d)" +
            "[A-Za-z\\d!@#$%^&*()_+\\-=]" +
            "{8,16}" +
            "$"
    );

    // 定义明确禁止的危险字符（可根据需求扩展）
    private static final String DANGEROUS_CHARS = " '\";?<>/\\`~|";

    /**
     * 校验密码是否符合安全规则
     * @param password 待校验的密码
     * @return 符合规则返回true，否则false
     */
    public static boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        // 检查是否包含危险字符
        for (char c : password.toCharArray()) {
            if (DANGEROUS_CHARS.indexOf(c) != -1) {
                return false;
            }
        }

        // 正则表达式验证格式
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}