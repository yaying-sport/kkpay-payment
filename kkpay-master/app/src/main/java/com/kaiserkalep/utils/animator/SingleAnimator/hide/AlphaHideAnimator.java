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

public class AlphaHideAnimator extends ViewAnimatorImpl {

    private ObjectAnimator animator;

    private AlphaHideAnimator() {
    }


    public static AlphaHideAnimator getInstance() {
        return new AlphaHideAnimator();
    }

    @Override
    public SkinAnimator apply(@NonNull final View view, @Nullable final Action action, @Nullable final Action end) {
        animator = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("alpha", 1, 0)
        );
        animator.setDuration(3 * PRE_DURATION);
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
