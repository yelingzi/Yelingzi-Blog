package com.yeling.utils;

import java.security.SecureRandom;
import java.util.Random;

public class VerCodeGenerateUtil {
    /**
     * 验证码包含的字段
     */
    private static final String SYMBOLS = "0123456789ABCDEFGHIGKLMNOPQRSTUVWXYZ";
    private static final Random RAND = new SecureRandom();

    /**
     * 生成 6 位数的随机数字
     * @return {@link String 验证码}
     */
    public static String generateVerCode() {
        char[] code = new char[6];
        for (int i = 0; i < code.length; i++) {
            code[i] = SYMBOLS.charAt(RAND.nextInt(SYMBOLS.length()));
            //  RAND.nextInt(SYMBOLS.length()) 生成一个随机的索引值;
            //  RAND.nextInt(n) 生成一个0到n-1之间的随机整数;
        }
        return new String(code);
    }
}
