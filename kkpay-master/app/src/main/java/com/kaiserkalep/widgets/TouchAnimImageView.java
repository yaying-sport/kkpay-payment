package com.kaiserkalep.widgets;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AnticipateInterpolator;
import android.widget.ImageView;

import androidx.annotation.Nullable;

/**
 *  触摸缩放动画
 *
 * @Auther: Jack
 * @Date: 2021/1/7 18:37
 * @Description:
 */
@SuppressLint("AppCompatCustomView")
public class TouchAnimImageView extends ImageView {
    private AnimatorSet one;
    public TouchAnimImageView(Context context) {
        super(context);
    }

    public TouchAnimImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchAnimImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
