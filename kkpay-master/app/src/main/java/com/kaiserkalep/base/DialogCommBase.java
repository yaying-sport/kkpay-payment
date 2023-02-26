package com.kaiserkalep.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.kaiserkalep.R;
import com.kaiserkalep.interfaces.ActivityIntentInterface;
import com.kaiserkalep.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;

/**
 * 底部dialog
 *
 * @Auther: Administrator
 * @Date: 2019/3/18 0018 11:50
 * @Description:
 */
public abstract class DialogCommBase extends Dialog implements DialogInterface.OnDismissListener {

    protected static int defaultStyle = R.style.BottomDialog;

    public int level;
    public int gravity = Gravity.CENTER;

    private Context mContext;

    public Context getMyContext() {
        return mContext;
    }

    public DialogCommBase(Context context) {
        this(context, defaultStyle);
        mContext = context;
    }

    public DialogCommBase(Context context, int theme) {
        super(context, theme);
        mContext = context;
        initView();
        init();
    }

    public DialogCommBase(Context context, int theme, int gravity) {
        super(context, theme);
        mContext = context;
        this.gravity = gravity;
        initView();
        init();
    }

    protected DialogCommBase(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    protected int getParamsX() {
        return 0;
    }

    protected int getParamsY() {
        return 0;
    }

    protected void init() {
        Window window = getWindow();
        if (window == null) {
            return;
        }
//        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //设置无边距
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = getWindowManagerWidth();
        params.height = getWindowManagerHeight();
        //设置dialog的位置在底部
        params.gravity = gravity;
        params.x = getParamsX();
        params.y = getParamsY();
        window.setAttributes(params);

    }

    public View setView(@LayoutRes int ResId) {
        View view = getLayoutInflater().inflate(ResId, (ViewGroup) getWindow().getDecorView());
        ButterKnife.bind(this, view);
        //解决有时候布局加载高度不正常情况
        return view;
    }

    public abstract void initView();

    public String getString(int id) {
        return getContext().getString(id);
    }

    protected int getWindowManagerWidth() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    protected int getWindowManagerHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public void show() {
        try {
            if (getMyContext() != null && getMyContext() instanceof Activity
                    && !((Activity) getMyContext()).isDestroyed() && !((Activity) getMyContext()).isFinishing()) {
                //弹框显示时防止并发跳转
                if (!(this instanceof LoadingDialog)) {
                    ActivityPresent.lastClickTime = Calendar.getInstance().getTimeInMillis();
                }
                super.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            if (getMyContext() != null && getMyContext() instanceof Activity
                    && !((Activity) getMyContext()).isDestroyed() && !((Activity) getMyContext()).isFinishing()) {
                super.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DialogCommBase setLevel(int level) {
        this.level = level;
        return this;
    }

    ArrayList<OnDismissListener> listeners = new ArrayList<>();


    public void addOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(this);
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    public void cleanListener() {
        if (listeners != null) {
            listeners.clear();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (CommonUtils.ListNotNull(listeners)) {
            for (OnDismissListener listener : listeners) {
                if (listener != null) {
                    listener.onDismiss(dialog);
                }
            }
        }
    }

    protected ActivityIntentInterface getInterface() {
        Context myContext = getMyContext();
        if (myContext != null && myContext instanceof ActivityIntentInterface) {
            return (ActivityIntentInterface) myContext;
        }
        return null;
    }

    public void toast(String msg) {
        ActivityIntentInterface anInterface = getInterface();
        if (anInterface != null) {
            anInterface.toast(msg, -1);
        }
    }

    public Activity getActivity() {
        return CommonUtils.getActivityFromContext(mContext);
    }
}