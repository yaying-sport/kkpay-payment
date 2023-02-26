package com.kaiserkalep.bean;

import android.view.View;

import com.kaiserkalep.R;
import com.kaiserkalep.interfaces.SucceedCallBackListener;

/**
 * 订单提醒数据
 *
 * @Auther: Jack
 * @Date: 2019/10/14 13:15
 * @Description:
 */
public class OrderNoticeDialogData {

    private int id;
    private String title;
    private String content;
    private int logoPlaceholder;
    private SucceedCallBackListener<Integer> nowBtn;
    private View.OnClickListener close;


    public OrderNoticeDialogData(int id, String title, String content, boolean isCancel, SucceedCallBackListener<Integer> nowbtn, View.OnClickListener close) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.logoPlaceholder = isCancel ? R.drawable.icon_cancel_topview : R.drawable.icon_notice_topview;
        this.nowBtn = nowbtn;
        this.close = close;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLogoPlaceholder() {
        return logoPlaceholder;
    }

    public void setLogoPlaceholder(int logoPlaceholder) {
        this.logoPlaceholder = logoPlaceholder;
    }

    public SucceedCallBackListener<Integer> getNowBtn() {
        return nowBtn;
    }

    public void setNowBtn(SucceedCallBackListener<Integer> nowBtn) {
        this.nowBtn = nowBtn;
    }

    public View.OnClickListener getClose() {
        return close;
    }

    public void setClose(View.OnClickListener close) {
        this.close = close;
    }
}
