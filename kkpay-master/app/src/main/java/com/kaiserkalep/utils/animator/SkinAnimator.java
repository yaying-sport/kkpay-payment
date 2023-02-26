package com.kaiserkalep.utils.animator;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by erfli on 2/25/17.
 */

public interface SkinAnimator {
    int PRE_DURATION = 200;
    int AFTER_DURATION = 200;
    int AFTER_DURATION_DELAY = 150;

    SkinAnimator apply(@NonNull View view, @Nullable Action action, @Nullable final Action end);

    SkinAnimator setPreDuration();

    SkinAnimator setAfterDuration();

    SkinAnimator setDuration();

    void start();

}
