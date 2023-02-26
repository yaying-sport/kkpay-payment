package com.kaiserkalep.bean;

import android.view.View;

/**
 * 立即实名
 *
 * @Auther: Jack
 * @Date: 2019/10/14 13:15
 * @Description:
 */
public class AuthenDialogData {

    public View.OnClickListener nowBtn;
    public View.OnClickListener close;

    public AuthenDialogData(View.OnClickListener nowbtn, View.OnClickListener close) {
        this.nowBtn = nowbtn;
        this.close = close;
    }
}
