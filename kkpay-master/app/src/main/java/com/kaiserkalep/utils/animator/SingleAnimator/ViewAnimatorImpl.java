package com.kaiserkalep.utils.animator.SingleAnimator;

import android.view.View;

import com.kaiserkalep.utils.animator.Action;
import com.kaiserkalep.utils.animator.SkinAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by erfli on 2/25/17.
 */

public abstract class ViewAnimatorImpl implements SkinAnimator {
    @Override
    public abstract SkinAnimator apply(@NonNull View view, @Nullable Action action, @Nullable final Action end);

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
    public abstract void start();

    protected void resetView(View view) {
        view.setAlpha(1);
        view.setScaleY(1);
        view.setScaleX(1);
        view.setRotation(0);
        view.setTranslationX(0);
        view.setTranslationY(0);
        view.setPivotX(view.getWidth() / 2);
        view.setPivotY(view.getHeight() / 2);
    }
}
