package com.kaiserkalep.utils.animator.activityAnimator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.kaiserkalep.utils.animator.Action;
import com.kaiserkalep.utils.animator.SkinAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by erfli on 2/25/17.
 */

public class SkinAlphaAnimator implements SkinAnimator {
    protected ObjectAnimator preAnimator;
    protected ObjectAnimator afterAnimator;
    protected View targetView;

    private SkinAlphaAnimator() {
    }

    public static SkinAlphaAnimator getInstance() {
        SkinAlphaAnimator skinAlphaAnimator = new SkinAlphaAnimator();
        return skinAlphaAnimator;
    }

    @Override
    public SkinAnimator apply(@NonNull View view, @Nullable final Action action, @Nullable final Action end) {
        this.targetView = view;
        preAnimator = ObjectAnimator.ofFloat(targetView, "alpha", 1, 0)
                .setDuration(PRE_DURATION * 3);
        preAnimator.setInterpolator(new LinearInterpolator());
        afterAnimator = ObjectAnimator.ofFloat(targetView, "alpha", 0, 1)
                .setDuration(AFTER_DURATION * 6);
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
