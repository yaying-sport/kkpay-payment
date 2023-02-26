package com.kaiserkalep.utils.animator.SingleAnimator.hide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Build;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.kaiserkalep.utils.animator.Action;
import com.kaiserkalep.utils.animator.SingleAnimator.ViewAnimatorImpl;
import com.kaiserkalep.utils.animator.SkinAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Created by erfli on 2/25/17.
 */

public class TranslationAlphaHideAnimator2 extends ViewAnimatorImpl {

    private ObjectAnimator animator;

    private TranslationAlphaHideAnimator2() {
    }


    public static TranslationAlphaHideAnimator2 getInstance() {
        return new TranslationAlphaHideAnimator2();
    }

    @Override
    public SkinAnimator apply(@NonNull final View view, @Nullable final Action action, @Nullable final Action end) {
        animator = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("alpha", 1, 0),
                PropertyValuesHolder.ofFloat("translationY", -view.getHeight()),
                PropertyValuesHolder.ofFloat("translationX", view.getWidth()),
                PropertyValuesHolder.ofFloat("rotation", 270),
                PropertyValuesHolder.ofFloat("scaleX", 0),
                PropertyValuesHolder.ofFloat("scaleY", 0)
        );
        animator.setDuration(5 * PRE_DURATION);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                resetView(view);
                if (action != null) {
                    action.action(null);
                }
            }
        });
        return this;
    }

    @Override
    public void start() {
        animator.start();
    }
}
