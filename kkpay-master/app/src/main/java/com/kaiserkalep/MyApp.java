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
    //?????????????????? map
    public static Map<String, MySellBean.RowsDTO> pendingOrderMap = new HashMap<>();
    //?????????????????? map
    public static Map<String, MyOrderBean.RowsDTO> orderMap = new HashMap<>();

    public static MyApp getApplication() {
        return instance;
    }

    //0 ??????  1 ??????  2 ???????????????
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
        LogUtils.d("????????????__" + key);
        UMConfigure.init(this, key, "", UMConfigure.DEVICE_TYPE_PHONE, "");
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL);//??????LEGACY_AUTO??????????????????
    }

    private void setLog() {
        LogUtils.setDebugMode(isDebug);
        /**
         * ??????????????????Log??????
         * ??????: boolean ?????????false???????????????LOG?????????true
         */
        UMConfigure.setLogEnabled(isDebug);
    }

    private void initSkin() {
        SkinManager.getInstance().init(this);
    }


    /**
     * ?????????
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
     * ???????????????
     */
    private void initSwipe() {
        //???Activity????????????????????????????????????Activity????????????????????????????????????
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
                //???????????????activity?????????true???????????????????????????false???????????????????????????
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

        //??????????????????????????????????????????Bugly?????????????????????????????????????????????????????????????????????
        CrashReport.setIsDevelopmentDevice(context, isDebug);

        /*
        *
        * ??????????????????SDK?????????????????????????????????????????????????????????

           ???????????????Bugly SDK???Log???
           ?????????Crash????????????????????????
           ????????????????????????Logcat????????????
          ????????????????????????????????????true?????????????????????false???
        * */
        String BUGLY_ID = getMyString(isDebug ? R.string.bugly_key_test : R.string.bugly_key_release);
        CrashReport.initCrashReport(this, BUGLY_ID, isDebug);

    }


    @Override
    public void onFront() {
        //????????????????????????
        EventBusUtil.post(new AppFrontBackEvent(true));
    }

    @Override
    public void onBack() {
        //????????????????????????
        EventBusUtil.post(new AppFrontBackEvent(false));
    }


    /**
     * Android P ?????????API????????????
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
                if (!getApplicationInfo().packageName.equals(processName)) {//?????????????????????????????????
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

    //static ?????????????????????????????????
    static {
        //????????????????????????????????????????????????????????????????????????
        SmartRefreshLayout.setDefaultRefreshInitializer((context, layout) -> {
            //?????????????????????????????????
            layout.setPrimaryColorsId(R.color.activity_bg, R.color.smart_refresh_layout)//????????????????????????
                    .setEnablePureScrollMode(true)//?????????????????????,?????????????????????????????????
                    .setHeaderHeight(100)
                    .setFooterHeight(70);//??????????????????
        });
        //???????????????Header?????????
//        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> new MyRefreshHeader(context, null, 0));
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(((context, layout) -> new MaterialHeader(context, null, 0).setColorSchemeResources(R.color.colorPrimary)));
        //???????????????Footer?????????
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
