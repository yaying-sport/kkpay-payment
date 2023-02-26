package com.kaiserkalep.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 首位不能输0,输入方式为大小写字母加数字
 *
 * @Auther: Jack
 * @Date: 2020/12/31 16:33
 * @Description:
 */
public class NoZeroStartFilterText implements InputFilter {

    private Pattern p = Pattern.compile("[a-zA-Z1-9][a-zA-Z0-9]*");
    private StringBuilder sb = new StringBuilder();

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        sb.delete(0, sb.length());
        String newString = source.toString();
        String oldString = dest.toString();
        if (!TextUtils.isEmpty(oldString) && oldString.length() >= 2 &&//已有输入,长度等于或超过2位
                TextUtils.isEmpty(newString) && dstart == 0 && dend == 1 &&//新输入为空时是删除操作,删除位置为第1位
                "0".equals(oldString.substring(1, 2))) {//已输入第二位为0
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
