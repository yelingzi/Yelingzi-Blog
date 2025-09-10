package com.yeling.utils;

public class EmailUtils {
    /**
     * 检验邮箱格式的函数
     *
     * @param email 待检验的邮箱字符串
     * @return 0表示邮箱格式正确，-1表示邮箱为空，-2表示邮箱格式不符合规范
     */
    public static int validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            // 如果邮箱为空，返回 -1
            return -1;
        }

        // 定义邮箱格式的正则表达式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        // 创建Pattern对象，用于匹配正则表达式
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(emailRegex);
        // 创建Matcher对象，用于对输入的邮箱字符串进行匹配操作
        java.util.regex.Matcher matcher = pattern.matcher(email);

        if (matcher.matches()) {
            // 如果匹配成功，说明邮箱格式正确，返回 0
            return 0;
        } else {
            // 如果匹配失败，说明邮箱格式不符合规范，返回 -2
            return -2;
        }
    }
}
