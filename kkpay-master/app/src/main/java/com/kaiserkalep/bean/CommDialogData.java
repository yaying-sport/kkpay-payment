package com.kaiserkalep.bean;

import android.view.View;

/**
 * 通用实体弹框
 *
 * @Auther: Jack
 * @Date: 2019/10/14 13:15
 * @Description:
 */
public class CommDialogData {

    public CharSequence title;
    public CharSequence msg;
    public String leftStr;
    public String rightStr;
    public View.OnClickListener left;
    public View.OnClickListener right;
    public boolean mCancelable = false;
    public boolean mCanceledOnTouchOutside = false;

    public CommDialogData(CharSequence title, CharSequence msg, String leftStr
            , String rightStr, View.OnClickListener left, View.OnClickListener right) {
        this.title = title;
        this.msg = msg;
        this.leftStr = leftStr;
        this.rightStr = rightStr;
        this.left = left;
        this.right = right;
    }

    public CommDialogData(CharSequence title, CharSequence msg, String leftStr
            , String rightStr, View.OnClickListener left, View.OnClickListener right,
                          boolean mCancelable, boolean mCanceledOnTouchOutside) {
        this.title = title;
        this.msg = msg;
        this.leftStr = leftStr;
        this.rightStr = rightStr;
        this.left = left;
        this.right = right;
        this.mCancelable = mCancelable;
        this.mCanceledOnTouchOutside = mCanceledOnTouchOutside;
    }
}
