package com.kaiserkalep.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号,第一位必须为1.输入方式为数字
 *
 * @Auther: Jack
 * @Date: 2020/12/31 16:33
 * @Description:
 */
public class PhoneFilterNumber implements InputFilter {

    private Pattern p = Pattern.compile("[1][0-9]*");
    private StringBuilder sb = new StringBuilder();

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        sb.delete(0, sb.length());
        String newString = source.toString();
        String oldString = dest.toString();
        if (!TextUtils.isEmpty(oldString) && oldString.length() >= 2 &&//已有输入,长度等于或超过2位
                TextUtils.isEmpty(newString) && dstart == 0 && dend == 1 &&//新输入为空时是删除操作,删除位置为第1位
                !"1".equals(oldString.substring(1, 2))) {//已输入第二位不为1
            return oldString.substring(0, 1);//返回第一位删除的值
        }
        String ss = "";
        if (!TextUtils.isEmpty(oldString)) {
            sb.append(oldString);
            sb.insert(dstart, newString);
            ss = sb.toString();
        } else {
            ss = newString;
        }
        Matcher m = p.matcher(ss);
        if (!m.matches()) return "";
        return null;
    }
}
