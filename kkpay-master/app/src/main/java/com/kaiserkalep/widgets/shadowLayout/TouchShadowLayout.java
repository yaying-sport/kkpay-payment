package com.kaiserkalep.widgets.shadowLayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AnticipateInterpolator;

/**
 *  触摸动画(带阴影)
 *
 * @Auther: Jack
 * @Date: 2021/1/6 20:21
 * @Description:
 */
public class TouchShadowLayout extends ShadowLayout {
    private AnimatorSet one;

    public TouchShadowLayout(Context context) {
        super(context);
    }

    public TouchShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //组合动画
        one = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0.97f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat( this, "scaleY", 0.97f, 1f);
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
