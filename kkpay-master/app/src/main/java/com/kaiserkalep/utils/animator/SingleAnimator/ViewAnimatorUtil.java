package com.kaiserkalep.utils.animator.SingleAnimator;

import android.view.View;

import com.kaiserkalep.utils.animator.Action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by erfli on 2/27/17.
 */

public class ViewAnimatorUtil {
    private ViewAnimatorUtil() {
    }

    public static void executeAnimator(@NonNull View view, @NonNull
            ViewAnimatorType animatorType, @Nullable Action action, @Nullable Action end) {
        animatorType.apply(view, action, end);
    }
}
