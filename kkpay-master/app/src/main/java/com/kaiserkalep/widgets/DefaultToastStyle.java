package com.kaiserkalep.widgets;

import android.content.Context;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.utils.toast.style.ToastBlackStyle;


/**
 * @Auther: Jack
 * @Date: 2020/1/6 23:07
 * @Description:
 */
public class DefaultToastStyle extends ToastBlackStyle {

    public DefaultToastStyle(Context context) {
        super(context);
    }

    @Override
    public int getCornerRadius() {
        return dp2px(5);
    }

    @Override
    public int getBackgroundColor() {
        return MyApp.getMyColor(R.color.toast_bg);
    }

    @Override
    public int getTextColor() {
        return MyApp.getMyColor(R.color.toast_text);
    }

    @Override
    public float getTextSize() {
        return sp2px(12);
    }

    @Override
    public int getpaddingLeft() {
        return dp2px(10);
    }

    @Override
    public int getPaddingTop() {
        return dp2px(8);
    }

    @Override
    public int getZ() {
        return 15;
    }
}
