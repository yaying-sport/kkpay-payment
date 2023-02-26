package com.kaiserkalep.utils.animator.activityAnimator;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.kaiserkalep.utils.animator.Action;
import com.kaiserkalep.utils.animator.SkinAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Created by erfli on 2/25/17.
 */

public class SkinRotateAnimator3 implements SkinAnimator {
    protected ObjectAnimator preAnimator;
    protected View targetView;

    private SkinRotateAnimator3() {
    }

    public static SkinRotateAnimator3 getInstance() {
        SkinRotateAnimator3 skinAlphaAnimator = new SkinRotateAnimator3();
        return skinAlphaAnimator;
    }

    @Override
    public SkinAnimator apply(@NonNull View view, @Nullable final Action action, @Nullable final Action end) {
        this.targetView = view;
        preAnimator = ObjectAnimator.ofPropertyValuesHolder(targetView,
                PropertyValuesHolder.ofFloat("scaleX",
                        1, 0.5f, 0.2f, 0.05f, 0.8f, 1),
                PropertyValuesHolder.ofFloat("scaleY",
                        1, 0.5f, 0.2f, 0.05f, 0.8f, 1),
                PropertyValuesHolder.ofFloat("rotationY", 0, 720))
                .setDuration(PRE_DURATION * 3);
//        preAnimator.setInterpolator(new LinearInterpolator());

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (action != null) {
                    action.action(null);
                }
            }
        }, PRE_DURATION);

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (end != null) {
                    end.action(null);
                }
            }
        }, PRE_DURATION * 2);

        preAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
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
