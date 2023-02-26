package com.kaiserkalep.base;

import android.content.Context;

import com.kaiserkalep.interfaces.ActivityIntentInterface;

/**
 * 底部dialog
 *
 * @Auther: Administrator
 * @Date: 2019/3/18 0018 11:50
 * @Description:
 */
public abstract class DialogBase extends DialogCommBase {

    public ActivityIntentInterface getCtrl() {
        return ctrl;
    }

    ActivityIntentInterface ctrl;

    public DialogBase(ActivityIntentInterface context) {
        this(context, defaultStyle);
        this.ctrl = context;
    }

    public DialogBase(ActivityIntentInterface context, int theme) {
        super(context.getContext(), theme);
        this.ctrl = context;
    }

    public DialogBase(ActivityIntentInterface context, int theme, int gravity) {
        super(context.getContext(), theme, gravity);
        this.ctrl = context;
    }

    protected DialogBase(ActivityIntentInterface context, boolean cancelable, OnCancelListener cancelListener) {
        super(context.getContext(), cancelable, cancelListener);
        this.ctrl = context;
    }
}
