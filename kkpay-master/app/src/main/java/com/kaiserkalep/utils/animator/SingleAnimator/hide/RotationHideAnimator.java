package com.kaiserkalep.utils.animator.SingleAnimator.hide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.kaiserkalep.utils.animator.Action;
import com.kaiserkalep.utils.animator.SingleAnimator.ViewAnimatorImpl;
import com.kaiserkalep.utils.animator.SkinAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by erfli on 2/25/17.
 */

public class RotationHideAnimator extends ViewAnimatorImpl {

    private ObjectAnimator hiteAniamator;
    private ObjectAnimator rotationAnimator;

    private RotationHideAnimator() {
    }


    public static RotationHideAnimator getInstance() {
        return new RotationHideAnimator();
    }

    @Override
    public SkinAnimator apply(@NonNull final View view, @Nullable final Action action, @Nullable final Action end) {
        view.setPivotY(0);
        view.setPivotX(view.getHeight() / 2);
        view.animate().translationX(view.getHeight() / 2).setDuration(100).start();

        int middle = 45;
        int range = 15;
        rotationAnimator = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("rotation",
                        0, middle - range, middle + range, middle - range, middle + range, middle - range, middle + range)
        ).setDuration(PRE_DURATION * 8);
        rotationAnimator.setStartDelay(100);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                hiteAniamator.start();
            }
        });

        hiteAniamator = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("TranslationY", 0, view.getHeight()),
                PropertyValuesHolder.ofFloat("alpha", 1, 0));
        hiteAniamator.setInterpolator(new AccelerateInterpolator());
        hiteAniamator.addListener(new AnimatorListenerAdapter() {
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
        rotationAnimator.start();
    }
}
