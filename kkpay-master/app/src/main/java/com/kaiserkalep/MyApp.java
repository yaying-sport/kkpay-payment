package com.kaiserkalep;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.multidex.MultiDex;

import com.billy.android.swipe.SmartSwipeBack;
import com.billy.android.swipe.SmartSwipeWrapper;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;
import com.billy.android.swipe.listener.SimpleSwipeListener;
import com.kaiserkalep.bean.MyOrderBean;
import com.kaiserkalep.bean.MySellBean;
import com.kaiserkalep.service.KKpayBackgroundService;
import com.kaiserkalep.ui.activity.ScanDetailActivity;
import com.kaiserkalep.widgets.MyClassicsHeader;
import com.opensource.svgaplayer.SVGACache;
import com.opensource.svgaplayer.SVGAParser;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.kaiserkalep.base.BaseApp;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.bean.ClientInfo;
import com.kaiserkalep.eventbus.AppFrontBackEvent;
import com.kaiserkalep.ui.activity.LoginActivity;
import com.kaiserkalep.ui.activity.MainActivity;
import com.kaiserkalep.ui.activity.StartActivity;
import com.kaiserkalep.utils.AppFrontBackHelper;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.utils.skinlibrary.loader.SkinManager;
import com.kaiserkalep.utils.toast.ToastUtils;
import com.kaiserkalep.widgets.DefaultToastStyle;
import com.kaiserkalep.widgets.MyClassicsFooter;
import com.kaiserkalep.widgets.MyRefreshHeader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.billy.android.swipe.SwipeConsumer.DIRECTION_LEFT;


/**
 * @Auther: Administrator
 * @Date: 2019/3/14 16:10
 * @Description:
 */
public class MyApp extends BaseApp implements AppFrontBackHelper.OnAppStatusListener {
    public static boolean isDebug = BuildConfig.DEBUG;
    @SuppressLint("StaticFieldLeak")
    private static MyApp instance = null;
    //进行中的挂单 map
    public static Map<String, MySellBean.RowsDTO> pendingOrderMap = new HashMap<>();
    //进行中的订单 map
    public static Map<String, MyOrderBean.RowsDTO> orderMap = new HashMap<>();

    public static MyApp getApplication() {
        return instance;
    }

    //0 锁屏  1 开屏  2 开屏且解锁
    public int isScreenOn = 0;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            webviewSetPath(getApplicationContext());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        appFrontBack();
    }

    private void init() {
        instance = this;
        setLog();
        BaseNetUtil.init();
        ClientInfo.init(getApplicationContext());
        ToastUtils.init(this);
        ToastUtils.initStyle(new DefaultToastStyle(getApplicationContext()));
        closeAndroidPDialog();
        initBugly();
        initSwipe();
        initSkin();
        initUmeng();
        initSVGA();
    }

    private void initSVGA() {
        SVGACache.INSTANCE.onCreate(this, SVGACache.Type.FILE);
        SVGAParser.Companion.shareParser().init(this);
    }

    private void initUmeng() {
        String key = getResources().getString(isDebug ? R.string.UMengKey_test : R.string.UMengKey_release);
        LogUtils.d("友盟初始__" + key);
        UMConfigure.init(this, key, "", UMConfigure.DEVICE_TYPE_PHONE, "");
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL);//选用LEGACY_AUTO页面采集模式
    }

    private void setLog() {
        LogUtils.setDebugMode(isDebug);
        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
        UMConfigure.setLogEnabled(isDebug);
    }

    private void initSkin() {
        SkinManager.getInstance().init(this);
    }


    /**
     * 主线程
     *
     * @return
     */
    public boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            if (CommonUtils.ListNotNull(runningAppProcesses)) {
                for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcesses) {
                    if (appProcess.pid == pid) {
                        return getApplicationInfo().packageName.equals(appProcess.processName);
                    }
                }
            }
        }
        return false;
    }


    /**
     * 初始化侧滑
     */
    private void initSwipe() {
        //主Activity不需要侧滑返回功能，其它Activity都采用仿微信侧滑返回效果
        SmartSwipeBack.activityBack(this, activity -> new ActivitySlidingBackConsumer(activity)
                .setRelativeMoveFactor(0.5f)
                .setScrimColor(Color.TRANSPARENT)
                .setShadowColor(0x80000000)
                .setShadowSize(UIUtils.dip2px(getApplicationContext(), 10))
                .setEdgeSize(UIUtils.getScreenWidth(getApplicationContext()) / 10)
                .enableDirection(DIRECTION_LEFT/*| DIRECTION_TOP*/)
                .addListener(new SimpleSwipeListener() {
                    @Override
                    public void onSwipeOpened(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction1) {
                        if (activity != null) {
                            activity.finish();
                            activity.overridePendingTransition(R.anim.anim_none, R.anim.anim_none);
                        }
                    }
                }), activity ->
                //根据传入的activity，返回true代表需要侧滑返回；false表示不需要侧滑返回
                !(activity instanceof MainActivity
                        || activity instanceof StartActivity
                        || activity instanceof LoginActivity
                        || activity instanceof ScanDetailActivity));
    }

    private void appFrontBack() {
        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(this, this);
    }


    private void initBugly() {
        Context context = getApplicationContext();

        //在开发测试阶段，可以在初始化Bugly之前通过以下接口把调试设备设置成“开发设备”。
        CrashReport.setIsDevelopmentDevice(context, isDebug);

        /*
        *
        * 第三个参数为SDK调试模式开关，调试模式的行为特性如下：

           输出详细的Bugly SDK的Log；
           每一条Crash都会被立即上报；
           自定义日志将会在Logcat中输出。
          建议在测试阶段建议设置成true，发布时设置为false。
        * */
        String BUGLY_ID = getMyString(isDebug ? R.string.bugly_key_test : R.string.bugly_key_release);
        CrashReport.initCrashReport(this, BUGLY_ID, isDebug);

    }


    @Override
    public void onFront() {
        //应用切到前台处理
        EventBusUtil.post(new AppFrontBackEvent(true));
    }

    @Override
    public void onBack() {
        //应用切到后台处理
        EventBusUtil.post(new AppFrontBackEvent(false));
    }


    /**
     * Android P 非公共API弹框关闭
     */
    private void closeAndroidPDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                Class aClass = Class.forName("android.content.pm.PackageParser$Package");
                Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
                declaredConstructor.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Class cls = Class.forName("android.app.ActivityThread");
                Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
                declaredMethod.setAccessible(true);
                Object activityThread = declaredMethod.invoke(null);
                Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
                mHiddenApiWarningShown.setAccessible(true);
                mHiddenApiWarningShown.setBoolean(activityThread, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = 28)
    public void webviewSetPath(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                String processName = getProcessName(context);
                if (!getApplicationInfo().packageName.equals(processName)) {//判断不等于默认进程名称
                    WebView.setDataDirectorySuffix(processName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
                if (processInfo.pid == android.os.Process.myPid()) {
                    return processInfo.processName;
                }
            }
        }
        return null;
    }

    //static 代码段可以防止内存泄露
    static {
        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer((context, layout) -> {
            //全局设置（优先级最低）
            layout.setPrimaryColorsId(R.color.activity_bg, R.color.smart_refresh_layout)//全局设置主题颜色
                    .setEnablePureScrollMode(true)//设置纯滚动模式,代码控制下拉及上拉加载
                    .setHeaderHeight(100)
                    .setFooterHeight(70);//底部刷新高度
        });
        //设置全局的Header构建器
//        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> new MyRefreshHeader(context, null, 0));
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(((context, layout) -> new MaterialHeader(context, null, 0).setColorSchemeResources(R.color.colorPrimary)));
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new MyClassicsFooter(context)
                .setNoText()
                .setProgressResource(R.drawable.icon_refresh_foot)
                .setTextSizeTitle(12)
                .setAccentColor(getMyColor(R.color.classics_footer))
                .setFinishDuration(0)
                .setDrawableArrowSize(0)
                .setDrawableProgressSize(28)
                .setDrawableMarginRight(-14)
                .setSpinnerStyle(SpinnerStyle.Translate));
    }

    //
    public void checkOrder() {
        if (MyApp.getApplication().pendingOrderMap.size() == 0 && MyApp.getApplication().orderMap.size() == 0) {
            KKpayBackgroundService.stopService(getContext());
        }
    }

}
