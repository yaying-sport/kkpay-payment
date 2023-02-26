package com.kaiserkalep.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.kaiserkalep.interfaces.SucceedCallBackListener;

import java.lang.reflect.Method;

import androidx.annotation.NonNull;

import static android.view.View.NO_ID;

/**
 * 虚拟导航键
 *
 * @Auther: Administrator
 * @Date: 2019/5/4 0004 17:52
 * @Description:
 */
public class NavigationBarUtil {


    public static NavigationBarUtil initActivity(Activity activity) {
        return new NavigationBarUtil(activity.findViewById(android.R.id.content));
    }

    private static final int SOFT_KEY_BOARD_MIN_HEIGHT = 100;
    private View mObserved;//被监听的视图
    public static int usableHeightView;//视图变化前的可用高度
    private ViewGroup.LayoutParams layoutParams;
    private int heightDiff = 0;//键盘高度
    private KeyboardVisibilityListener mVisibilityListener;
    private HeightViewChangeListener mHeightViewChangeListener;
    private boolean keyboardVisible = false;
    public static boolean isNeedListener = true;//是否需要监听

    private NavigationBarUtil(View content) {
        mObserved = content;
        //给View添加全局的布局监听器监听视图的变化
        mObserved.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        layoutParams = mObserved.getLayoutParams();
    }


    ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            if (isNeedListener) {
                resetViewHeight();
            }
        }
    };

    /**
     * 重置视图的高度，使不被底部虚拟键遮挡
     */
    private void resetViewHeight() {
        int usableHeightViewNow = CalculateAvailableHeight();
        if (mObserved != null && mObserved.getRootView() != null) {
            int heightDiff = mObserved.getRootView().getHeight() - usableHeightViewNow;
            if (this.heightDiff != heightDiff) {
                usableHeightView = 0;
                this.heightDiff = heightDiff;
            }
            if (heightDiff > SOFT_KEY_BOARD_MIN_HEIGHT) { // if more than 100 pixels, its probably a keyboard...
                if (!keyboardVisible) {
                    keyboardVisible = true;
                    if (mVisibilityListener != null) {
                        mVisibilityListener.onKeyboardVisibility(true);
                    }
                }
            } else {
                if (keyboardVisible) {
                    keyboardVisible = false;
                    if (mVisibilityListener != null) {
                        mVisibilityListener.onKeyboardVisibility(false);
                    }
                }
            }
        }
        //比较布局变化前后的View的可用高度
        if (usableHeightViewNow != usableHeightView) {
            LogUtils.d("usableHeightView__" + usableHeightView + "____usableHeightViewNow____" + usableHeightViewNow);
            //如果两次高度不一致
            //将当前的View的可用高度设置成View的实际高度
            layoutParams.height = usableHeightViewNow;
            if (mObserved != null) {
                mObserved.requestLayout();//请求重新布局
            }
            usableHeightView = usableHeightViewNow;

            if (mHeightViewChangeListener != null) {
                mHeightViewChangeListener.onHeightViewChange(usableHeightViewNow);
            }
        }
    }

    /**
     * 计算试图高度
     *
     * @return
     */
    private int CalculateAvailableHeight() {
        Rect r = new Rect();
        if (mObserved != null) {
            mObserved.getWindowVisibleDisplayFrame(r);
        }
//        return (r.bottom - r.top);//如果不是沉浸状态栏，需要减去顶部高度
        return (r.bottom);//如果是沉浸状态栏
    }

    /**
     * 判断底部是否有虚拟键
     *
     * @param context
     * @return
     */
    public static boolean hasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }


    private static final String NAVIGATION = "navigationBarBackground";

    // 该方法需要在View完全被绘制出来之后调用，否则判断不了
    //在比如 onWindowFocusChanged（）方法中可以得到正确的结果
    public static boolean isNavigationBarExist(@NonNull Activity activity) {
        ViewGroup vp = (ViewGroup) activity.getWindow().getDecorView();
        if (vp != null) {
            for (int i = 0; i < vp.getChildCount(); i++) {
                vp.getChildAt(i).getContext().getPackageName();
                if (vp.getChildAt(i).getId() != NO_ID && NAVIGATION.equals(activity.getResources()
                        .getResourceEntryName(vp.getChildAt(i).getId()))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void isNavigationBarExist(Activity activity, SucceedCallBackListener<Integer[]> navShowing) {
        if (activity == null) {
            return;
        }
        final int height = getNavigationHeight(activity);
        activity.getWindow().getDecorView().setOnApplyWindowInsetsListener((v, windowInsets) -> {
            boolean isShowing = false;
            int b = 0;
            if (windowInsets != null) {
                b = windowInsets.getSystemWindowInsetBottom();
                isShowing = (b == height);
            }
            if (navShowing != null) {
                navShowing.succeedCallBack(new Integer[]{isShowing ? 1 : 0, b});
            }
//            if (onNavigationStateListener != null && b <= height) {
//                onNavigationStateListener.onNavigationState(isShowing, b);
//            }
            return windowInsets;
        });
    }

    public static int getNavigationHeight(Context activity) {
        if (activity == null) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        int height = 0;
        if (resourceId > 0) {
            //获取NavigationBar的高度
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }


    /**
     * 清除监听
     */
    public void removeListener() {
        if (mObserved != null) {
            ViewTreeObserver viewTreeObserver = mObserved.getViewTreeObserver();
            if (viewTreeObserver != null) {
                viewTreeObserver.removeOnGlobalLayoutListener(layoutListener);
            }
            mObserved = null;
        }
    }

    public void setVisibilityListener(KeyboardVisibilityListener listener) {
        mVisibilityListener = listener;
    }

    public interface KeyboardVisibilityListener {
        void onKeyboardVisibility(boolean isVisible);
    }

    public interface HeightViewChangeListener {
        void onHeightViewChange(int usableHeightViewNow);
    }

    public void setHeightViewChangeListener(HeightViewChangeListener mHeightViewChangeListener) {
        this.mHeightViewChangeListener = mHeightViewChangeListener;
    }
}
