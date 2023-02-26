package com.kaiserkalep.interfaces;


import androidx.annotation.Nullable;

/**
 * 通用简单回调结果
 *
 * @author guangleilei
 * @version 1.0 2017-04-27
 */
public interface MainViewCallBackListener<T> {


    void succeedCallBack(@Nullable T o);

    void cameraCallBack();
}
