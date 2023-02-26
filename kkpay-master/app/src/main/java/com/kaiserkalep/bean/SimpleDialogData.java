package com.kaiserkalep.bean;


import com.kaiserkalep.interfaces.SucceedCallBackListener;

/**
 *  简单选择弹框
 *
 * @Auther: Jack
 * @Date: 2019/10/15 09:28
 * @Description:
 */
public class SimpleDialogData {


    public String one;
    public int oneTextColor;
    public String two;
    public int twoTextColor;
    public SucceedCallBackListener listener;

    public SimpleDialogData(String one, int oneTextColor, String two,
                            int twoTextColor, SucceedCallBackListener listener) {
        this.one = one;
        this.oneTextColor = oneTextColor;
        this.two = two;
        this.twoTextColor = twoTextColor;
        this.listener = listener;
    }
}
