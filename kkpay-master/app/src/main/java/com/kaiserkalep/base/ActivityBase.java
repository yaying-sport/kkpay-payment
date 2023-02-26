package com.kaiserkalep.base;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.umeng.analytics.MobclickAgent;
import com.kaiserkalep.R;
import com.kaiserkalep.bean.ClientInfo;
import com.kaiserkalep.ui.activity.MainActivity;
import com.kaiserkalep.ui.activity.StartActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.MobclickAgentUtils;
import com.kaiserkalep.utils.MyActivityManager;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.NavigationBarUtil;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.skinlibrary.base.SkinBaseActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;

/**
 * @Auther: Administrator
 * @Date: 2019/3/14 0014 20:42
 * @Description:
 */
public abstract class ActivityBase extends SkinBaseActivity implements NavigationBarUtil.KeyboardVisibilityListener {

    final String TAG = "ActivityBase";
    protected Bundle bundle = new Bundle();
    protected boolean isShow = false;
    //    protected NavigationBarUtil navigationBarUtil;
    //    protected KeyboardStatusDetector keyboardStatus;
    protected boolean keyboardVisible;

    protected boolean isOnKeyBack;//是否拦截返回键

    /**
     * 是否为竖屏
     */
    protected boolean isVertical = true;

    private boolean mStateSaved;

    /**
     * 是否点击空白关闭键盘
     */
    public boolean needTouchKeyboard = false;

    public boolean needAutoSize = true;
    protected ViewGroup content;

    /**
     * 友盟页面统计临时停止
     */
    public boolean stopUmPage;

    public ActivityBase() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        MyActivityManager.getActivityManager().pushActivity(this);
        checkParams();
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            handlerDownPermission();
            if (savedInstanceState.containsKey("bundle") &&
                    savedInstanceState.getBundle("bundle") != null) {
                this.bundle = savedInstanceState.getBundle("bundle");
            }
        }
//        try {
//            navigationBarUtil = NavigationBarUtil.initActivity(this);
//            navigationBarUtil.setVisibilityListener(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        this.setContentView(this.getViewId());
        mStateSaved = false;
        this.superViews();
        //该方法是【友盟+】Push后台进行日活统计及多维度推送的必调用方法，请务必调用！
//        PushAgent.getInstance(this).onAppStart();
        ButterKnife.bind(this);
        MyGsonSyntaxErrorListener.start();
        content = (ViewGroup) findViewById(android.R.id.content).getRootView();
        if (this.isRenderingView()) {
            this.afterViews();
        }
        //  PushAgent.getInstance(this).onAppStart(); //友盟推送
//        SkinManager.getInstance().register(this);

    }

    void checkParams() {
//        if (!CommonUtils.StringNotNull(PrivateStringData.k)) {
//            PrivateStringData.getK(this);
//        }
        if (!CommonUtils.MapNotNull(ClientInfo.headMap)) {
            ClientInfo.init(this);
        }
    }


    /**
     * 手动关闭存储卡权限,页面重启时,//如果有离线缓存列表//,main页面操作(避免重复调用重启)重启应用
     */
    private void handlerDownPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            if (this instanceof MainActivity) {//首页时重启
                Intent intent = new Intent(this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                LogUtils.d("储存卡权限被关闭首页重启__" + this.getClass().getSimpleName());
            } else {
                LogUtils.d("储存卡权限被关闭非首页关闭__" + this.getClass().getSimpleName());
                finish();
            }
        }
    }

    public abstract int getViewId();

    public abstract void afterViews();

    public void superViews() {
    }

    public void replace(int id, Fragment fragment) {
        replace(id, fragment, false);
    }

    public void replace(int id, Fragment fragment, boolean isAnim) {
        if (!this.isFinishing()) {
            FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
            if (isAnim) {
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_right_in,
                        R.anim.slide_left_out,
                        R.anim.slide_left_in,
                        R.anim.slide_right_out);
            }
            fragmentTransaction.replace(id, fragment).commitAllowingStateLoss();
        }
    }

    public void addFragment(int id, Fragment fragment) {
        if (!this.isFinishing()) {
            this.getSupportFragmentManager().beginTransaction().add(id, fragment).commitAllowingStateLoss();
        }
    }

    public void hideFragment(Fragment fragment) {
        hideFragment(fragment, false);
    }

    public void hideFragment(Fragment fragment, boolean isAnim) {
        if (!this.isFinishing()) {
            FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
            if (isAnim) {
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_right_in,
                        R.anim.slide_left_out,
                        R.anim.slide_left_in,
                        R.anim.slide_right_out);
            }
            fragmentTransaction.hide(fragment).commitAllowingStateLoss();
        }
    }

    public void showFragment(Fragment fragment) {
        showFragment(fragment, false);
    }

    public void showFragment(Fragment fragment, boolean isAnim) {
        if (!this.isFinishing()) {
            FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
            if (isAnim) {
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_right_in,
                        R.anim.slide_left_out,
                        R.anim.slide_left_in,
                        R.anim.slide_right_out);
            }
            fragmentTransaction.show(fragment).commitAllowingStateLoss();
        }
    }

    /**
     * 判断是否再某个app
     */
   /* public boolean isAppForeground(String packageName) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();

//        Log.i("qian.api", "currentPackageName:" + currentPackageName + " packageName:" + packageName);

        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
            return true;
        }
        return false;
    }*/


//    /**
//     * 判断是否再某个app
//     */
//    public boolean isAppForeground(String packageName) {
//        String[] activePackages;
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
//            activePackages = getActivePackages();
//        } else {
//            activePackages = getActivePackagesCompat();
//        }
//        if (activePackages != null) {
//            for (String activePackage : activePackages) {
//                if (activePackage.equals(packageName)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

//    String[] getActivePackagesCompat() {
//        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        if (am != null) {
//            final List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//            if (CommonUtils.ListNotNull(taskInfo)) {
//                final ComponentName componentName = taskInfo.get(0).topActivity;
//                final String[] activePackages = new String[1];
//                activePackages[0] = componentName.getPackageName();
//                return activePackages;
//            }
//        }
//        return null;
//    }

    String[] getActivePackages() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            final Set<String> activePackages = new HashSet<>();
            final List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
            if (CommonUtils.ListNotNull(processInfos)) {
                for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                    if (processInfo != null) {
                        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            activePackages.addAll(Arrays.asList(processInfo.pkgList));
                        }
                    }
                }
                return activePackages.toArray(new String[activePackages.size()]);
            }
        }
        return null;
    }

    public void removeFragment(Fragment fragment) {
        if (!this.isFinishing()) {
            if (fragment != null) {
                this.getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLayout();
        isShow = true;
        mStateSaved = false;
        MobclickAgent.onResume(this);
        MyDialogManager.getManager().checkDialog();
    }

    public void requestLayout() {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            view.requestLayout();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isShow = false;
        MobclickAgent.onPause(this); // 不能遗漏
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStateSaved = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mStateSaved = false;
    }


    /**
     * 开始页面统计
     */
    public void onPageStart(String name) {
        if (stopUmPage) {
            return;
        }
        MobclickAgentUtils.onPageStart(name);
    }

    /**
     * 结束页面统计
     */
    public void onPageEnd(String name) {
        if (stopUmPage) {
            return;
        }
        MobclickAgentUtils.onPageEnd(name);
    }

    @Override
    protected void onDestroy() {
        MyDialogManager.getManager().checkDestroy(this);
        super.onDestroy();
//        SkinManager.getInstance().unregister(this);
        MyActivityManager.getActivityManager().popActivity(this);
    }


    @Override
    public void finish() {
        MyActivityManager.getActivityManager().popActivity(this);
        super.finish();
    }

    /**
     * activity管理器迭代关闭页面时,由迭代器处理activity出栈
     */
    public void superFinish() {
        super.finish();
    }

    public Bundle getSaveInstance() {
        return this.bundle;
    }

    public Object getSaveObject(String key, Object def) {
        if (this.bundle == null) {
            return def;
        } else {
            return !this.bundle.containsKey(key) ? def : this.bundle.get(key);
        }
    }

    public String getSaveString(String key, String def) {
        return (String) this.getSaveObject(key, def);
    }

    public String getSaveString(String key) {
        return (String) this.getSaveObject(key, (Object) null);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("bundle", this.bundle);
        mStateSaved = true;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            if (needTouchKeyboard) {
                return super.dispatchTouchEvent(ev);
            }
            if (ev.getAction() == 0) {
                View view = this.getCurrentFocus();
                if (this.isHideInput(view, ev)) {
                    this.HideSoftInput(view.getWindowToken());

                }
            }
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && v instanceof EditText) {
            int[] l = new int[]{0, 0};
            v.getLocationInWindow(l);
            int left = l[0];
            int top = l[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return ev.getX() <= (float) left || ev.getX() >= (float) right || ev.getY() <= (float) top || ev.getY() >= (float) bottom;
        } else {
            return false;
        }
    }

    private void HideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token, 2);
        }

    }

    protected boolean isRenderingView() {
        return true;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getToken() {
        return SPUtil.getToken();
    }

    public boolean isLogin() {
        return CommonUtils.StringNotNull(getToken());
    }

    public boolean isShow() {
        return isShow;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStringEvent(String event) {
    }

//    @Override
//    public Resources getResources() {
//        if (needAutoSize) {
//            //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
//            AutoSizeCompat.autoConvertDensity((super.getResources()), 375, true);//如果有自定义需求就用这个方法
//        }
//        return super.getResources();
//    }

//    @Override
//    public Resources getResources() {
//        //APP字体不需要跟随系统改变字体大小而改变
//        Resources resources = super.getResources();
//        Configuration newConfig = resources.getConfiguration();
//        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
//        if (resources != null && newConfig.fontScale != 1) {
//            newConfig.fontScale = 1;
//            Context configurationContext = createConfigurationContext(newConfig);
//            resources = configurationContext.getResources();
//            displayMetrics.scaledDensity = displayMetrics.density * newConfig.fontScale;
//        }
//        return resources;
//    }

    /**
     * 软键盘监听
     *
     * @param keyboardVisible 键盘是否开启
     */
    @Override
    public void onKeyboardVisibility(boolean keyboardVisible) {
        this.keyboardVisible = keyboardVisible;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isOnKeyBack) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MENU) {//MENU键
                //监控/拦截菜单键
                return true;
            }
        }
        if (!mStateSaved) {
            return super.onKeyDown(keyCode, event);
        } else {
            // State already saved, so ignore the event
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (!mStateSaved) {
            super.onBackPressed();
        }
    }
}

