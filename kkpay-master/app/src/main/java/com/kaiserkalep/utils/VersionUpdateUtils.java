package com.kaiserkalep.utils;

import android.text.TextUtils;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.UpdateDate;
import com.kaiserkalep.interfaces.ActivityIntentInterface;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.net.impl.SysImpl;

/**
 * 版本更新工具类
 * Created by lenovo on 2019/5/30.
 */

public class VersionUpdateUtils {
    private final String VERSION;
    private VersionCallback versionCallback;

    private int requestNum = 0;
    private ActivityIntentInterface ctrl;
    private boolean needDialog;
    private boolean redDot;


    public static VersionUpdateUtils initVersionUpdateUtils(ActivityIntentInterface ctrl, VersionCallback versionCallback) {
        return initVersionUpdateUtils(ctrl, versionCallback, false, false);
    }

    public static VersionUpdateUtils initVersionUpdateUtils(ActivityIntentInterface ctrl, VersionCallback versionCallback,
                                                            boolean needDialog, boolean redDot) {
        return new VersionUpdateUtils(ctrl, versionCallback, needDialog, redDot);
    }

    private VersionUpdateUtils(ActivityIntentInterface ctrl, VersionCallback versionCallback, boolean needDialog, boolean redDot) {
        this.versionCallback = versionCallback;
        this.needDialog = needDialog;
        this.ctrl = ctrl;
        this.redDot = redDot;
        VERSION = AppUtils.getVersionName(MyApp.getContext());
        getVersion();
    }


    private void getVersion() {
        new SysImpl(new ZZNetCallBack<UpdateDate>(ctrl, UpdateDate.class) {
            @Override
            public void onSuccess(UpdateDate response) {
                if (response != null) {
                    setUpdateDialog(response);
                } else {
                    if (versionCallback != null) {
                        versionCallback.toast();
                    }
                }
            }

            @Override
            public void onError(String msg, String code) {
                if (needDialog) {
                    super.onError(msg, code);
                    return;
                }
                setReLoadVersion();
            }
        }.setNeedDialog(needDialog).setNeedToast(needDialog)).getUpdateVersion();
    }

    /**
     * 设置更新弹框或更新提示
     *
     * @param response
     */
    private void setUpdateDialog(UpdateDate response) {
        if (response != null) {
            String downBrowserUrl = response.getDownBrowserUrl();
            if (CommonUtils.StringNotNull(downBrowserUrl)) {
                SPUtil.setDownBrowserUrl(downBrowserUrl);
            }
            if (CommonUtils.StringNotNull(response.getVersion()) && CommonUtils.StringNotNull(VERSION)) {
                String serverVersionD = response.getVersion();
                String LastVersionD = response.getLast_version();
                // String localVersionD = "1.0.2";
                String state = response.getState();//1 推荐更新 2 强制更新
                String serverVersion = serverVersionD.replace(".", ""); //服务器版本
                String localVersion = VERSION.replace(".", ""); // 当前app 版本
                String LastVersion = LastVersionD.replace(".", ""); //最低版本

                if (TextUtils.isEmpty(serverVersion))
                    return;
                requestNum = 0;
                //强更
                if (state.equals("2") && (Integer.valueOf(serverVersion) > Integer.valueOf(localVersion))) {
                    if (redDot && versionCallback != null) {
                        versionCallback.redDot(serverVersionD);
                        return;
                    }
                    showEditionDialog(response);
                } else {
                    //非强更
                    if (state.equals("1") && (Integer.valueOf(serverVersion) > Integer.valueOf(localVersion))) {
                        //最低版本设置强更
                        if (CommonUtils.StringNotNull(LastVersion) && Integer.valueOf(LastVersion) >= Integer.valueOf(localVersion)) {
                            response.setState("2");
                        }
                        if (redDot && versionCallback != null) {
                            versionCallback.redDot(serverVersionD);
                            return;
                        }
                        showEditionDialog(response);
                    } else {
                        if (versionCallback != null) {
                            versionCallback.toast();
                        }
                    }
                }
            }
        } else {
            if (versionCallback != null) {
                versionCallback.toast();
            }
        }
    }

    /**
     * 网络从无到有更新接口重试
     */
    private SucceedCallBackListener listener = new SucceedCallBackListener<Boolean>() {
        @Override
        public void succeedCallBack(Boolean o) {
            listener = null;
            requestNum = 1;
            LogUtils.d("网络联通更新接口重试");
            getVersion(); //5 秒后再次请求
        }
    };


    /**
     * 请求失败设置重试及网络联通回调重试
     */
    private void setReLoadVersion() {
        if (requestNum >= 2) {
            if (!NetWorkUtils.isNetworkConnected()) {//无网络才设置网络联通回调
                NetWorkUtils.setListener(listener);
            }
            return;
        }
        MyApp.postDelayed(() -> {
            requestNum++;
            LogUtils.d("更新接口重试:" + requestNum);
            getVersion(); //5 秒后再次请求
        }, 10000);
    }


    /**
     * 更新弹窗
     */
    public void showEditionDialog(UpdateDate updateDater) {
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.ONE.value, MyDialogManager.UpdateDialog, updateDater);
    }

    public interface VersionCallback {
        void toast();

        void redDot(String version);
    }
}
