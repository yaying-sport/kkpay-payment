package com.kaiserkalep.widgets;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AnticipateInterpolator;
import android.widget.RelativeLayout;

/**
 * 触摸缩放动画
 *
 * @Auther: Jack
 * @Date: 2021/1/14 15:14
 * @Description:
 */
public class TouchAnimRelativeLayout extends RelativeLayout {
    private AnimatorSet one;

    public TouchAnimRelativeLayout(Context context) {
        super(context);
    }

    public TouchAnimRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchAnimRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //组合动画
        one = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0.97f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0.97f, 1f);
        one.setDuration(400);
        one.setInterpolator(new AnticipateInterpolator());
        one.play(scaleX).with(scaleY);//两个动画同时开始
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (one != null) {
                one.start();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}

