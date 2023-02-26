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
public class AddWjAccountDialogData {

    public SucceedCallBackListener<Boolean> nowBtn;
    public View.OnClickListener close;
    public String id = "";


    public AddWjAccountDialogData(String id,  SucceedCallBackListener<Boolean> nowbtn, View.OnClickListener close) {
        this.id = id;
        this.nowBtn = nowbtn;
        this.close = close;
    }
}
