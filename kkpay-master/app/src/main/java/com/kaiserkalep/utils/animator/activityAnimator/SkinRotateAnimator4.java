package com.kaiserkalep.utils.animator.activityAnimator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.kaiserkalep.utils.animator.Action;
import com.kaiserkalep.utils.animator.SkinAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by erfli on 2/25/17.
 */

public class SkinRotateAnimator4 implements SkinAnimator {
    protected ObjectAnimator preAnimator;
    protected ObjectAnimator afterAnimator;
    protected View targetView;

    private SkinRotateAnimator4() {
    }

    public static SkinRotateAnimator4 getInstance() {
        SkinRotateAnimator4 skinAlphaAnimator = new SkinRotateAnimator4();
        return skinAlphaAnimator;
    }

    @Override
    public SkinAnimator apply(@NonNull View view, @Nullable final Action action, @Nullable final Action end) {
        this.targetView = view;
        preAnimator = ObjectAnimator.ofPropertyValuesHolder(targetView,
                PropertyValuesHolder.ofFloat("translationX",
                        view.getLeft(), view.getLeft() - view.getWidth()),
                PropertyValuesHolder.ofFloat("translationY",
                        view.getTop(), view.getTop() - view.getHeight() / 2),
                PropertyValuesHolder.ofFloat("scaleX",
                        0f),
                PropertyValuesHolder.ofFloat("scaleY",
                        0f),
                PropertyValuesHolder.ofFloat("rotationY", 0, -90))
                .setDuration(PRE_DURATION * 3);
        preAnimator.setInterpolator(new LinearInterpolator());
        afterAnimator = ObjectAnimator.ofPropertyValuesHolder(targetView,
                PropertyValuesHolder.ofFloat("translationX",
                        view.getLeft() - view.getWidth(), view.getLeft()),
                PropertyValuesHolder.ofFloat("translationY",
                        view.getTop() - view.getHeight() / 2, view.getTop()),
                PropertyValuesHolder.ofFloat("scaleX",
                        1),
                PropertyValuesHolder.ofFloat("scaleY",
                        1),
                PropertyValuesHolder.ofFloat("rotationY", -90, 0))
                .setDuration(AFTER_DURATION * 3);
        afterAnimator.setInterpolator(new LinearInterpolator());
//        afterAnimator.setStartDelay(AFTER_DURATION_DELAY);
        preAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (action != null) {
                    action.action(o -> {
                        if (afterAnimator != null) {
                            afterAnimator.start();
                        }
                    });
                }
            }
        });

        afterAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (end != null) {
                    end.action(null);
                }
            }
        });
        return this;
    }

    @Override
    public SkinAnimator setPreDuration() {
        return this;
    }

    @Override
    public SkinAnimator setAfterDuration() {
        return this;
    }

    @Override
    public SkinAnimator setDuration() {
        return this;
    }

    @Override
    public void start() {
        preAnimator.start();
    }
}
