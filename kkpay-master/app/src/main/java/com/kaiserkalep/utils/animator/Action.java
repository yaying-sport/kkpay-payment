package com.kaiserkalep.utils.animator;

import com.kaiserkalep.interfaces.SucceedCallBackListener;

/**
 * Created by erfli on 2/25/17.
 */

public interface Action {
    void action(SucceedCallBackListener<Boolean> listener);
}
