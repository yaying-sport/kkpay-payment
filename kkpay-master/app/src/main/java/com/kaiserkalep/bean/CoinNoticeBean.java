package com.kaiserkalep.bean;

import android.content.DialogInterface;

public class CoinNoticeBean {

    private String title;
    private String message;


    private DialogInterface.OnDismissListener onDismissListener;

    public CoinNoticeBean() {
    }

    public CoinNoticeBean(String title, String message, DialogInterface.OnDismissListener onDismissListener) {
        this.title = title;
        this.message = message;
        this.onDismissListener = onDismissListener;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DialogInterface.OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

}
