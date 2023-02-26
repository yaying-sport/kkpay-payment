package com.kaiserkalep.bean;

import android.content.Context;

import com.kaiserkalep.base.CallbackBase;

/**
 * 加载中弹框
 *
 * @Auther: Jack
 * @Date: 2019/10/15 16:01
 * @Description:
 */
public class LoadingDialogData {

    public String msg;
    public boolean isDelayedLoading;
    public boolean cancel;
    public Context context;
    public CallbackBase callbackBase;

    public LoadingDialogData(String msg, boolean isDelayedLoading, boolean cancel, Context context,
                             CallbackBase callbackBase) {
        this.msg = msg;
        this.isDelayedLoading = isDelayedLoading;
        this.cancel = cancel;
        this.context = context;
        this.callbackBase = callbackBase;
    }
}
