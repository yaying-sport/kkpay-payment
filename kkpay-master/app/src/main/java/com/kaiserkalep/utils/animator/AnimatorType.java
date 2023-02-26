package com.kaiserkalep.utils.animator;

import android.view.View;

import com.kaiserkalep.utils.animator.activityAnimator.ScaleAnimator;
import com.kaiserkalep.utils.animator.activityAnimator.ScaleAnimator2;
import com.kaiserkalep.utils.animator.activityAnimator.SkinAlphaAnimator;
import com.kaiserkalep.utils.animator.activityAnimator.SkinRotateAnimator;
import com.kaiserkalep.utils.animator.activityAnimator.SkinRotateAnimator2;
import com.kaiserkalep.utils.animator.activityAnimator.SkinRotateAnimator3;
import com.kaiserkalep.utils.animator.activityAnimator.TranslationAnimator;
import com.kaiserkalep.utils.animator.activityAnimator.TranslationAnimator2;


/**
 * Created by erfli on 2/26/17.
 */

public enum AnimatorType {
    ALPHA {//1

        @Override
        public void apply(View view, Action action, Action end) {
            SkinAlphaAnimator.getInstance().apply(view, action, end).start();
        }
    },
    ROTATE {//2

        @Override
        public void apply(View view, Action action, Action end) {
            SkinRotateAnimator.getInstance().apply(view, action, end).start();
        }
    },
    ROTATE2 {//3

        @Override
        public void apply(View view, Action action, Action end) {
            SkinRotateAnimator2.getInstance().apply(view, action, end).start();
        }
    },
    ROTATE3 {//4

        @Override
        public void apply(View view, Action action, Action end) {
            SkinRotateAnimator3.getInstance().apply(view, action, end).start();
        }
    },
//    ROTATE4 {//5
//
//        @Override
//        public void apply(View view, Action action, Action end) {
//            SkinRotateAnimator4.getInstance().apply(view, action, end).start();
//        }
//    },
//    ROTATE5 {//6
//
//        @Override
//        public void apply(View view, Action action, Action end) {
//            SkinRotateHintAnimator.getInstance().apply(view, action, end).start();
//        }
//    },
    TRANSLATION {//5

        @Override
        public void apply(View view, Action action, Action end) {
            TranslationAnimator.getInstance().apply(view, action, end).start();
        }
    },
    TRANSLATION2 {//6

        @Override
        public void apply(View view, Action action, Action end) {
            TranslationAnimator2.getInstance().apply(view, action, end).start();
        }
    },
    Scale {//7

        @Override
        public void apply(View view, Action action, Action end) {
            ScaleAnimator.getInstance().apply(view, action, end).start();
        }
    },
    Scale2 {//8

        @Override
        public void apply(View view, Action action, Action end) {
            ScaleAnimator2.getInstance().apply(view, action, end).start();
        }
    },
    None {
        @Override
        public void apply(View view, Action action, Action end) {

        }
    };

    public abstract void apply(View view, Action action, Action end);
}
