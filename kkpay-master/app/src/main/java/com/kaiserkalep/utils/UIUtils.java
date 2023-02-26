package com.kaiserkalep.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.base.BaseApp;
import com.kaiserkalep.bean.ClientInfo;
import com.kaiserkalep.constants.NumberConstants;
import com.kaiserkalep.utils.skinlibrary.utils.SkinResourcesUtils;
import com.kaiserkalep.widgets.immersionbar.ImmersionBar;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;


public class UIUtils {


    public static double scale = 1920f / 1080f;

    /*
     * dp转px
     */
    public static int dip2px(float dipValue) {
        assert MyApp.getApplication().getApplicationContext() != null;
        float scale = MyApp.getApplication().getApplicationContext().getResources().getDisplayMetrics().density;
        ClientInfo.scale = scale;
        return dip2px(scale, dipValue);
    }


    /*
     * dp转px
     */
    public static int dip2px(Context context, float dipValue) {
        assert context != null;
        float scale = context.getResources().getDisplayMetrics().density;
        ClientInfo.scale = scale;
        return dip2px(scale, dipValue);
    }

    private static float getDensity(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }

    public static int dip2px(float scale, float dipValue) {
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转像素
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue * scale);
    }

    /**
     * 动态计算Listview高度
     *
     * @param lsitview
     * @param maxHight  最多多高
     * @param lineHight 分割线高度
     */
    public static void setListViewHeightBasedOnChildren(ListView lsitview, int maxHight, int lineHight) {
        // 获取listview的adapter
        ListAdapter listAdapter = lsitview.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i++) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, lsitview);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = lsitview.getLayoutParams();
        // 设置高度
        params.height = totalHeight + lsitview.getPaddingTop() + lsitview.getPaddingBottom() + lineHight;
        if (maxHight > 0 && params.height >= maxHight) {
            params.height = maxHight;
        }
//        // 设置margin
//        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        lsitview.setLayoutParams(params);
    }

    public static ColorStateList createColorStateList(int normal, int selected) {
        int[] colors = new int[]{selected, normal};
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};
        return new ColorStateList(states, colors);
    }

    /**
     * @param color
     * @return
     */
    public static int myParseColor(String color) {
        int c = 0xffffffff;
        if (CommonUtils.StringNotNull(color) && color.startsWith("#")) {
            try {
                int length = color.length();
                if (length == 7 || length == 9) {
                    return Color.parseColor(color);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return c;
    }

    /**
     * 设置Height

     * @param v 需要设置margin的view
     */
    public static void setHeight( View v, int height) {
        if (v != null) {
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            if (null != layoutParams){
                layoutParams.height = height;
            }
        }
    }

    /**
     * 设置Margins
     *
     * @param c context
     * @param v 需要设置margin的view
     * @param l 左
     * @param t 上
     * @param r 右
     * @param b 下
     */
    public static void setMargins(Context c, View v, float l, float t, float r, float b) {
        if (c != null) {
            setMargins2(v, dip2px(c, l), dip2px(c, t), dip2px(c, r), dip2px(c, b));
        }
    }

    /**
     * 设置Margins
     *
     * @param v 需要设置margin的view
     * @param l 左
     * @param t 上
     * @param r 右
     * @param b 下
     */
    public static void setMargins2(View v, int l, int t, int r, int b) {
        if (v != null) {
            if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                p.setMargins(l, t, r, b);
                v.requestLayout();
            }
        }
    }

    /**
     * 设置Margins
     *
     * @param v 需要设置margin的view
     * @param l 左
     * @param t 上
     * @param r 右
     * @param b 下
     * @param gravity  对齐方式
     */
    public static void setMargins3(View v, int l, int t, int r, int b,int gravity) {
        if (v != null) {
            if (v.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) v.getLayoutParams();
                param.gravity = gravity;
                param.setMargins(l, t, r, b);
                v.requestLayout();
            }
        }
    }

    /**
     * 设置Padding
     *
     * @param c context
     * @param v 需要设置Padding的view
     * @param l 左
     * @param t 上
     * @param r 右
     * @param b 下
     */
    public static void setPadding(Context c, View v, float l, float t, float r, float b) {
        if (v != null && c != null) {
            setPadding2(v, dip2px(c, l), dip2px(c, t), dip2px(c, r), dip2px(c, b));
        }
    }

    /**
     * 设置Padding
     *
     * @param v 需要设置Padding的view
     * @param l 左
     * @param t 上
     * @param r 右
     * @param b 下
     */
    public static void setPadding2(View v, int l, int t, int r, int b) {
        if (v != null) {
            v.setPadding(l, t, r, b);
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        //应用区域
        Rect outRect1 = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        return dm.heightPixels - outRect1.height();  //状态栏高度=屏幕高度-应用区域高度
    }


    /**
     * @param context
     * @param tv*          @param has
     * @param selectResId* @param nomalResId
     */
    public static void setLeftDrawable(Context context, TextView tv, @DrawableRes int selectResId) {
//        Drawable drawable = context.getResources().getDrawable(selectResId);
        Drawable drawable = SkinResourcesUtils.getDrawable(selectResId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv.setCompoundDrawables(drawable, null, null, null);
        }

    }

    /**
     * @param context
     * @param tv
     * @param selectResId
     */
    public static void setRightDrawable(Context context, TextView tv, int selectResId) {
//        Drawable drawable = context.getResources().getDrawable(selectResId);
        Drawable drawable = SkinResourcesUtils.getDrawable(selectResId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv.setCompoundDrawables(null, null, drawable, null);
        }
    }

    /**
     * @param context
     * @param tv
     * @param selectResId
     */
    public static void setTopDrawable(Context context, TextView tv, int selectResId) {
//        Drawable drawable = context.getResources().getDrawable(selectResId);
        Drawable drawable = SkinResourcesUtils.getDrawable(selectResId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv.setCompoundDrawables(null, drawable, null, null);
        }
    }


    /**
     * @param context
     * @param tv
     * @param selectResId
     */
    public static void setBottomDrawable(Context context, TextView tv, int selectResId) {
//        Drawable drawable = context.getResources().getDrawable(selectResId);
        Drawable drawable = SkinResourcesUtils.getDrawable(selectResId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv.setCompoundDrawables(null, null, null, drawable);
        }
    }


    /**
     * 隐藏状态栏
     *
     * @param context
     * @param on
     */
    @TargetApi(19)
    public static void setTranslucentStatus(Activity context, boolean on) {
        Window win = context.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    public static void hideBottomUIMenu(Activity context) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = context.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = context.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    public static int getViewWidth(View view) {
        if (view == null) {
            return -1;
        }
        measureView(view);
        return view.getMeasuredWidth();
    }

    public static int getViewHeight(View view) {
        if (view == null) {
            return -1;
        }
        measureView(view);
        return view.getMeasuredHeight();
    }

    private static void measureView(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
    }


    /**
     * 获得屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        return getScreenWidth(MyApp.getContext());
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        if (context != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            try {
                wm.getDefaultDisplay().getMetrics(outMetrics);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return outMetrics.widthPixels;
        } else
            return 0;

    }

    /**
     * 获得屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        return getScreenHeight(MyApp.getContext());
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
//        if (NavigationBarUtil.usableHeightView > 0) {
//            return NavigationBarUtil.usableHeightView;
//        }
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得屏幕高度（view可用用高度+ 刘海高度）
     *
     * @param context
     * @return
     */
    public static int getScreenHeight2(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得屏幕高度(整块屏幕真实宽高)
     *
     * @param context
     * @return
     */
    public static int getScreenHeight3(Activity context) {
        int height = 0;
        if (null != context) {
            boolean showNav = ImmersionBar.hasNavigationBar(context);
            WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            Point realSize = new Point();
            ((Display) wm.getDefaultDisplay()).getRealSize(realSize);
            if (showNav) {
                int navigationBarHeight = ImmersionBar.getNavigationBarHeight(context);
                height = realSize.y - navigationBarHeight;
            } else {
                height = realSize.y;
            }
        } else {
            WindowManager wm = (WindowManager) MyApp.getContext()
                    .getSystemService(Context.WINDOW_SERVICE);
            Point realSize = new Point();
            ((Display) wm.getDefaultDisplay()).getRealSize(realSize);
            height = realSize.y;
        }
        return height;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen.xml");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 计算指定画笔下指定字符串需要的宽度
     */
    public static int getTheTextNeedWidth(Paint thePaint, String text) {
        float[] widths = new float[text.length()];
        thePaint.getTextWidths(text, widths);
        int length = widths.length, nowLength = 0;
        for (int i = 0; i < length; i++) {
            nowLength += widths[i];
        }
        return nowLength;
    }


    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     */
    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity, color);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @return 状态栏矩形条
     */
    @SuppressLint("ResourceType")
    private static View createStatusView(Activity activity, int color) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);

        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        statusView.setId(NumberConstants.ACTIVITY_STATUS_VIEW_ID);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }


    /**
     * 设置页面的透明度
     *
     * @param bgAlpha 1表示不透明
     */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 获取底部导航栏高度
     *
     * @param context context
     * @return 底部导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        // 底部导航栏高度
        return context.getResources().getDimensionPixelSize(resourceId);
    }


    /**
     * context 获取Activity
     *
     * @param context
     * @return
     */
    @Nullable
    public static Activity findActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) context;
            return findActivity(wrapper.getBaseContext());
        } else {
            return null;
        }
    }

    /**
     * 彩图转换成灰色图片
     *
     * @param img
     * @return
     */
    public static Bitmap convertGreyImg(Bitmap img) {
        if (img == null) {
            return null;
        }
        int width, height;
        height = img.getHeight();
        width = img.getWidth();

        Bitmap bmpGrayScale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayScale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(img, 0, 0, paint);
        return bmpGrayScale;
    }

    /**
     * 是否全面屏
     * 宽高比大于2即认为是全面屏手机
     *
     * @param context
     * @return
     */
    public static boolean isFullScreen(Context context) {

        NavigationBarUtil.isNavigationBarExist((Activity) context, o -> {
            if (o != null && o.length > 1) {
                Integer a = o[0];
                Integer b = o[1];
            }

        });
        return getScreenHeight(context) / (getScreenWidth(context) * 1f) >= 2;
    }

    /**
     * 得到上下文
     *
     * @return
     */
    public static Context getContext() {
        return BaseApp.getContext();
    }

    /**
     * 得到resources对象
     *
     * @return
     */
    public static Resources getResource() {
        return getContext().getResources();
    }

    /**
     * 得到string.xml中的字符串
     *
     * @param resId
     * @return
     */
    public static String getString(int resId) {
        return getResource().getString(resId);
    }

    /**
     * 得到string.xml中的字符串，带点位符
     *
     * @return
     */
    public static String getString(int id, Object... formatArgs) {
        return getResource().getString(id, formatArgs);
    }

    /**
     * 得到应用程序的包名
     *
     * @return
     */
    public static String getPackageName() {
        return getContext().getPackageName();
    }

    /**
     * 得到主线程Handler
     *
     * @return
     */
    public static Handler getMainThreadHandler() {
        return MyApp.getMainHandler();
    }

    /**
     * 得到主线程id
     *
     * @return
     */
    public static long getMainThreadId() {
        return MyApp.getMainThreadId();
    }
}
