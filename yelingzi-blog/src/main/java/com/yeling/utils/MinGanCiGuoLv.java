package com.yeling.utils;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.core.SensitiveWord;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.github.houbb.sensitive.word.support.allow.WordAllows;
import com.github.houbb.sensitive.word.support.deny.WordDenys;

public class MinGanCiGuoLv {

    SensitiveWordBs sensitiveWordBs =
            SensitiveWordBs.newInstance()
                    .wordAllow(WordAllows.empty())
                    .wordDeny(WordDenys.empty())
                    .init();

    // 新增单个



    public void addWord(String text) {
        sensitiveWordBs.addWord(text);
    }

    public static String sensitiveWordsReplace(String text) {
        return SensitiveWordHelper.replace(text);
    }

    public static Boolean existSensitiveWord(String text) {
        return SensitiveWordHelper.contains(text);
    }

}
