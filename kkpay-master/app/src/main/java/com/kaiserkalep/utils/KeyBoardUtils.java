package com.kaiserkalep.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.Selection;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MuMu on 2016/11/11/0011.
 */

public class KeyBoardUtils {

    /**
     * 切换软键盘的状态
     * 如当前为收起变为弹出,若当前为弹出变为收起
     */
    public static void toggleInput(Context context) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 强制隐藏输入法键盘
     */
    public static void hideInput(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 隐藏键盘
     */
    public static void hideInput(Activity activity, EditText view) {
        //关闭软键盘
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * 显示键盘
     */
    public static void showInput(Activity activity, EditText view) {
        //打开软键盘
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


    private void showEmotionLayout(Activity mActivity, EditText editText, View mEmotionLayout) {
        int softInputHeight = getSupportSoftInputHeight(mActivity);
        if (softInputHeight == 0) {
            softInputHeight = SPUtil.getSoftInputHeight();
            if (softInputHeight == 0) {
                softInputHeight = 400;
            }
        }
        hideSoftInput(mActivity, editText);
        mEmotionLayout.getLayoutParams().height = softInputHeight;
        mEmotionLayout.setVisibility(View.VISIBLE);
    }

    private void hideSoftInput(Activity mActivity, EditText editText) {
        ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    public static int getSupportSoftInputHeight(Activity mActivity) {
        Rect r = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight(mActivity);
        }
        if (softInputHeight < 0) {
            Log.w("EmotionInputDetector", "Warning: value of softInputHeight is below zero!");
        }
        if (softInputHeight > 0) {
//            SimpleDialog.edit().putInt(SHARE_PREFERENCE_TAG, softInputHeight).apply();
            SPUtil.setSoftInputHeight(softInputHeight);
        }
        return softInputHeight;
    }

    /**
     * 隐藏或显示软键盘
     * 如果现在是显示调用后则隐藏 反之则显示
     *
     * @param activity
     */
    public static void showORhideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 强制显示软键盘
     *
     * @param activity
     */
    public static void showSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(activity.getWindow().getDecorView(), InputMethodManager.SHOW_FORCED);
    }

    /**
     * sgf
     * 强制隐藏软键盘
     *
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);//强制隐藏键盘
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用系统方法 强制隐藏软键盘
     *
     * @param activity
     */
    public static void hideSystemSoftKeyboard(Activity activity) {
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 判断软键盘是否显示方法
     *
     * @param activity
     * @return
     */

    public static boolean isSoftShowing(Activity activity) {
        //获取当屏幕内容的高度
        int screenHeight = activity.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        //DecorView即为activity的顶级view
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        //选取screenHeight*2/3进行判断
        return screenHeight * 2 / 3 > rect.bottom + getSoftButtonsBarHeight(activity);
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }


    /**
     * sgf
     * 进入页面打开软键盘
     * 首先要对指定的输入框请求焦点。然后调用输入管理器弹出软键盘。
     * 警告：对于刚跳到一个新的界面就要弹出软键盘的情况上述代码可能由于界面为加载完全而无法弹出软键盘。
     * 此时应该适当的延迟弹出软键盘如300毫秒（保证界面的数据加载完成）。实例代码如下：
     */
    public static void setEditTextState(EditText editText) {
        setEditTextState(editText, 300);
    }

    /**
     * sgf
     * 进入页面打开软键盘
     * 首先要对指定的输入框请求焦点。然后调用输入管理器弹出软键盘。
     * 警告：对于刚跳到一个新的界面就要弹出软键盘的情况上述代码可能由于界面为加载完全而无法弹出软键盘。
     * 此时应该适当的延迟弹出软键盘如200毫秒（保证界面的数据加载完成）。实例代码如下：
     */
    public static void setEditTextState(EditText editText, int delay) {
        if (editText == null) {
            return;
        }
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(editText, 0);
                           }

                       },
                delay);
    }

    /**
     * ***  定制软键盘弹出后，滚动到底部  ***
     */
    public static void setEditTextNowState(EditText editText) {
        if (editText == null) {
            return;
        }
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    /**
     * 把光标移到文字的最右边
     *
     * @param editText 输入框
     */
    public static void setCursorRight(EditText editText) {
        Editable etext = editText.getText();
        Selection.setSelection(etext, etext.length());
    }

}
