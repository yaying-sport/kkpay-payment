package com.kaiserkalep.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.utils.UIUtils;

/**
 * @Auther: Administrator
 * @Date: 2019/3/14 0014 20:46
 * @Description:
 */
public class LoadingDialog extends DialogCommBase {
    TextView msgText;
    private View iv_loading;

    public LoadingDialog(Context context) {
        super(context, R.style.loadingDialog);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void initView() {
        View view = setView(R.layout.dialog_loading);
        msgText = view.findViewById(R.id.msg);
        msgText.setVisibility(View.GONE);
        iv_loading = view.findViewById(R.id.iv_loading);
    }


    protected int getWindowManagerWidth() {
        return UIUtils.dip2px(80);
    }

    protected int getWindowManagerHeight() {
        return UIUtils.dip2px(80);
    }

    @Override
    protected void init() {
        super.init();
        Window window = getWindow();
        if (window == null) {
            return;
        }
        //设置该dialog与软键盘共存，不会在loading时隐藏软键盘
        window.setFlags(
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    public void showDialog(String msg, boolean isDelayedLoading, CallbackBase callbackBase) {
        if (this.msgText != null) {
            if (!TextUtils.isEmpty(msg)) {
                msgText.setVisibility(View.VISIBLE);
                msgText.setText(msg);
            } else {
                msgText.setVisibility(View.GONE);
            }
        }
        if (callbackBase != null) {
            // 监听 Cancel 事件
            setOnCancelListener(dialog -> {
                dialog.dismiss();
//                if (callbackBase != null) {//取消正在请求的网络接口
//                    callbackBase.cancel();
//                }
            });
        }

        if (iv_loading != null) {
            MyApp.removeCallbacks(runnable);
            MyApp.postDelayed(runnable, isDelayedLoading ? 300 : 0);
        }
        super.show();
    }

    private Runnable runnable = () -> iv_loading.setVisibility(View.VISIBLE);

    @Override
    public void dismiss() {
        MyApp.removeCallbacks(runnable);
        super.dismiss();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MyApp.removeCallbacks(runnable);
    }

}
