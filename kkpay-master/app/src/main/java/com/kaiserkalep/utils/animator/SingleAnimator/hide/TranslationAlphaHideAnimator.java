package com.kaiserkalep.utils.animator.SingleAnimator.hide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.kaiserkalep.utils.animator.Action;
import com.kaiserkalep.utils.animator.SingleAnimator.ViewAnimatorImpl;
import com.kaiserkalep.utils.animator.SkinAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by erfli on 2/25/17.
 */

public class TranslationAlphaHideAnimator extends ViewAnimatorImpl {

    private ObjectAnimator animator;

    private TranslationAlphaHideAnimator() {
    }


    public static TranslationAlphaHideAnimator getInstance() {
        return new TranslationAlphaHideAnimator();
    }

    @Override
    public SkinAnimator apply(@NonNull final View view, @Nullable final Action action, @Nullable final Action end) {
        int offset = -view.getWidth() / 2;
        animator = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("alpha", 1, 0),
                PropertyValuesHolder.ofFloat("translationX", 0, - offset, -offset, offset, offset, 0),
                PropertyValuesHolder.ofFloat("rotation", 0, -30, -30, 30, 30, 0),
                PropertyValuesHolder.ofFloat("translationY", 0, 3 * view.getHeight())
        );
        animator.setDuration(5 * PRE_DURATION);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
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
