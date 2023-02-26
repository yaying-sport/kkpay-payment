package com.kaiserkalep.interfaces;

import androidx.annotation.Nullable;

public interface SelectBankInterface<T> {

    void succeedCallBack(@Nullable T o , int position);
}
