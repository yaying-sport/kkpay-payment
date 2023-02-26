package com.kaiserkalep.base;

import androidx.fragment.app.FragmentActivity;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.bean.LoadingDialogData;
import com.kaiserkalep.utils.MyDialogManager;

/**
 * @Auther: Administrator
 * @Date: 2019/3/14 0014 20:45
 * @Description:
 */
public class PresenterBase {
    private static final String TAG = "PresenterBase";

    public IBaseView getiBaseView() {
        return iBaseView;
    }

    IBaseView iBaseView;

    public PresenterBase(IBaseView IBaseView) {
        this.iBaseView = IBaseView;
    }

    public void showDialog(String msg, boolean canCancle, boolean isDelayedLoading, CallbackBase callbackBase) {
        if (iBaseView != null && iBaseView.getContext() != null) {
            if (iBaseView.getContext() instanceof FragmentActivity) {
                if (((FragmentActivity) iBaseView.getContext()).isFinishing()) {
                    return;
                }
            }
            MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value,
                    MyDialogManager.LoadingDialog, new LoadingDialogData(msg, isDelayedLoading, canCancle, iBaseView.getContext(), callbackBase));
        }

    }

    public void closeDialog() {
        if (MyDialogManager.getManager().loadingDialog != null && MyDialogManager.getManager().loadingDialog.isShowing()) {
            MyDialogManager.getManager().loadingDialog.dismiss();
        }
    }

    public void showDialog() {
        showDialog("", false, false, null);
    }


    public void toast(String msg) {
        toast(msg, -1);
    }

    public void toast(String msg, int imgId) {
        MyApp.toast(msg);
    }

    public String getString(int id) {
        return iBaseView.getContext().getString(id);
    }
}
