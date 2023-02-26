package com.zhongjh.albumcamerarecorder.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 *
 * @author zhongjh
 * @date 2018/12/27
 */
public class ChildClickableFrameLayout extends RelativeLayout {

    /**
     * 子控件是否可以接受点击事件
     */
    private boolean childClickable = true;

    public ChildClickableFrameLayout(Context context) {
        super(context);
    }

    public ChildClickableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildClickableFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //返回true则拦截子控件所有点击事件，如果childClickable为true，则需返回false
        return !childClickable;
    }

    /**
     * 是否允许子控件可以点击
     * @param clickable 是否可以点击
     */
    public void setChildClickable(boolean clickable) {
        childClickable = clickable;
    }

}
