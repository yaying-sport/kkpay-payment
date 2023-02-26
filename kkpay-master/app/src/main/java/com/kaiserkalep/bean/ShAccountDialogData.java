package com.kaiserkalep.bean;

import android.view.View;

/**
 * 立即实名
 *
 * @Auther: Jack
 * @Date: 2019/10/14 13:15
 * @Description:
 */
public class ShAccountDialogData {

    public String textOne;
    public String textTwo;
    public View.OnClickListener nowBtn;
    public View.OnClickListener close;

    public ShAccountDialogData(String textOne, String textTwo, View.OnClickListener nowbtn, View.OnClickListener close) {
        this.textOne = textOne;
        this.textTwo = textTwo;
        this.nowBtn = nowbtn;
        this.close = close;
    }
}
