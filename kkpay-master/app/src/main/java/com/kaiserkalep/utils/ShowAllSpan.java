package com.kaiserkalep.utils;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.kaiserkalep.utils.skinlibrary.utils.SkinResourcesUtils;

/**
 * @Auther: Administrator
 * @Date: 2019/3/15 0015 19:06
 * @Description:
 */
public class ShowAllSpan extends ClickableSpan {

    private OnAllSpanClickListener clickListener;
    private boolean isPressed = false;
    private Context context;

    public ShowAllSpan(Context context, OnAllSpanClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View widget) {
        if (clickListener != null) {
            clickListener.onClick(widget);
        }
    }

    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }

    public interface OnAllSpanClickListener {
        void onClick(View widget);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
//        if (isPressed) {
//            ds.bgColor = 0xffdddddd;
//        } else {
        ds.bgColor = SkinResourcesUtils.getColor(android.R.color.transparent);
//        }
//        ds.setColor(SkinResourcesUtils.getColor(R.color.colorPrimary));
//        ds.setTextSize(UIUtils.dip2px(context, 15));
        ds.setUnderlineText(false);
    }
}