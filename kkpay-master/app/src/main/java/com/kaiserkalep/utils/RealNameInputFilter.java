package com.kaiserkalep.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 请输入真实姓名，仅支持中英文字母及符号“.”
 *
 * @Auther: Jack
 * @Date: 2020/12/21 20:58
 * @Description:
 */
public class RealNameInputFilter implements InputFilter {

    Pattern p = Pattern.compile("[a-zA-Z|\u4e00-\u9fa5]+");
    static String dot = "·";


    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher m = p.matcher(source.toString());
        if (!m.matches() && !source.toString().equals(dot)) return "";
        return null;
    }
}
