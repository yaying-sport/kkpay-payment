package com.kaiserkalep.bean;

import android.view.View;

import com.kaiserkalep.interfaces.SucceedCallBackListener;

/**
 * 立即实名
 *
 * @Auther: Jack
 * @Date: 2019/10/14 13:15
 * @Description:
 */
public class PayPassWordDialogData {

    public SucceedCallBackListener<String> nowBtn;
    public View.OnClickListener close;

    public PayPassWordDialogData(SucceedCallBackListener<String> nowbtn, View.OnClickListener close) {
        this.nowBtn = nowbtn;
        this.close = close;
    }
}
