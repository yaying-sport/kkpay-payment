package com.kaiserkalep.utils.animator.activityAnimator;

import android.view.View;

import com.kaiserkalep.utils.animator.Action;
import com.kaiserkalep.utils.animator.AnimatorType;


/**
 * Created by erfli on 2/26/17.
 */

public class SkinActivityAnimator {
    private static AnimatorType ACTIVITYANIMATORTYPE = AnimatorType.ALPHA;

    public static void configActivityAnimatorType(AnimatorType animatorType) {
        ACTIVITYANIMATORTYPE = animatorType;
    }

    public static void updateSkin(View view, Action action, Action end) {
        ACTIVITYANIMATORTYPE.apply(view, action, end);
    }
}
