package com.kaiserkalep.bean;

import com.kaiserkalep.interfaces.SucceedCallBackListener;

/**
 * 滑块验证
 *
 * @Auther: Jack
 * @Date: 2020/12/3 18:59
 * @Description:
 */
public class SliderValidationDialogData {

    public CaptchaImageData data;
    public MineCallBack callBack;

    public SliderValidationDialogData() {
    }


    public SliderValidationDialogData(CaptchaImageData data, MineCallBack callBack) {
        this.data = data;
        this.callBack = callBack;
    }

    public interface MineCallBack {

        /**
         * 验证通过
         */
        void verifyFinish(String validCode);

        void refreshCaptcha(boolean needLoading,SucceedCallBackListener<CaptchaImageData> listener);
    }
}
