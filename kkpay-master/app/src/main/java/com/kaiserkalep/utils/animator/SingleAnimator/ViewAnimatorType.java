package com.kaiserkalep.utils.animator.SingleAnimator;

import android.view.View;

import com.kaiserkalep.utils.animator.Action;
import com.kaiserkalep.utils.animator.SingleAnimator.update.AlphaAnimator;


/**
 * Created by erfli on 2/28/17.
 */

public enum ViewAnimatorType {
    //Visible
    //Update

    AlphaUpdateAnimator() {
        @Override
        public void apply(View view, Action action, Action end) {
            AlphaAnimator.getInstance().apply(view, action, end).start();
        }
    },

    //Hide
    AlphaHideAnimator() {
        @Override
        public void apply(View view, Action action, Action end) {
            com.kaiserkalep.utils.animator.SingleAnimator.hide.AlphaHideAnimator.getInstance().apply(view, action, end).start();
        }
    },
    RotationHideAnimator() {
        @Override
        public void apply(View view, Action action, Action end) {
            com.kaiserkalep.utils.animator.SingleAnimator.hide.RotationHideAnimator.getInstance().apply(view, action, end).start();
        }
    },
    ScaleHideAnimator() {
        @Override
        public void apply(View view, Action action, Action end) {
            com.kaiserkalep.utils.animator.SingleAnimator.hide.ScaleHideAnimator.getInstance().apply(view, action, end).start();
        }
    },
    TranslationAlphaHideAnimator() {
        @Override
        public void apply(View view, Action action, Action end) {
            com.kaiserkalep.utils.animator.SingleAnimator.hide.TranslationAlphaHideAnimator.getInstance().apply(view, action, end).start();
        }
    },
    TranslationAlphaHideAnimator2() {
        @Override
        public void apply(View view, Action action, Action end) {
            com.kaiserkalep.utils.animator.SingleAnimator.hide.TranslationAlphaHideAnimator2.getInstance().apply(view, action, end).start();
        }
    },
    TranslationHideAnimator() {
        @Override
        public void apply(View view, Action action, Action end) {
            com.kaiserkalep.utils.animator.SingleAnimator.hide.TranslationHideAnimator.getInstance().apply(view, action, end).start();
        }
    },
    TranslationHideAnimator2() {
        @Override
        public void apply(View view, Action action, Action end) {
            com.kaiserkalep.utils.animator.SingleAnimator.hide.TranslationHideAnimator2.getInstance().apply(view, action, end).start();
        }
    },
    TranslationRotationHideAnimator2() {
        @Override
        public void apply(View view, Action action, Action end) {
            com.kaiserkalep.utils.animator.SingleAnimator.hide.TranslationRotationHideAnimator2.getInstance().apply(view, action, end).start();
        }
    },

    None() {
        @Override
        public void apply(View view, Action action, Action end) {

        }
    };


    public abstract void apply(View view, Action action, Action end);
}
