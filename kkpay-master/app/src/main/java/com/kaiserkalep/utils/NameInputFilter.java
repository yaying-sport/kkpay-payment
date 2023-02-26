package com.kaiserkalep.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 仅支持中英文字母
 *
 * @Auther: Jack
 * @Date: 2020/12/21 20:58
 * @Description:
 */
public class NameInputFilter implements InputFilter {

    Pattern p = Pattern.compile("[a-zA-Z|\u4e00-\u9fa5]+");


    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher m = p.matcher(source.toString());
        if (!m.matches()) return "";
        return null;
    }
}
