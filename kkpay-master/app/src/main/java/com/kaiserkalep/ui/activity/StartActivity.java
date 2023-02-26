package com.kaiserkalep.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.StartPagerAdapter;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.bean.AdGuideData;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.eventbus.TokenTimeOutEvent;
import com.kaiserkalep.present.StartPresent;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.NetWorkCaCheUtils;
import com.kaiserkalep.view.IStartLaunch;

import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 启动页
 *
 * @auther: Administrator
 * @date: 2019/3/14 0014 下午 10:53
 */
public class StartActivity extends ZZActivity implements IStartLaunch,
        ViewPager.OnPageChangeListener {

    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.rl_ad_content)
    RelativeLayout rlAdContent;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    StartPresent present = new StartPresent(this);
    private String pageName = "";
    private String url = "";
    private String type = "";
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        needAutoSize = false;
        super.onCreate(savedInstanceState);
        isOnKeyBack = true;//拦截返回键
        isDecorViewBg = false;
        isStatusBar = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (present != null) {
            present.cancelAnimator();
        }
    }

    @Override
    public int getViewId() {
        return R.layout.activity_start;
    }

    @Override
    public void afterViews() {
        //解决 部分手机 第一次安装启动 再置为后台 点击图标 应用重启的问题
        if (!isTaskRoot()) {
            Intent i = getIntent();
            if (i != null) {
                String action = i.getAction();
                if (i.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }
        pageName = MyApp.getLanguageString(getContext(), R.string.startactivity_StartPage);
        try {
            url = getIntent().getStringExtra(StringConstants.DATA);
            type = getIntent().getStringExtra(StringConstants.TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        //检查模拟器
//        if (!MyApp.isDebug && EasyProtectorLib.checkIsRunningInEmulator(this, null)) {
//            new CommonDialog(this)
//                    .show(new CommDialogData("请使用真实手机运行",
//                            "", null, "好", v -> finish(), v -> finish()
//                            , false, false));
//            return;
//        }
//        NetWorkCaCheUtils.encryption("b.json","json/base.json");
//        NetWorkCaCheUtils.encryption("u.json","json/url.json");
        startTime = System.currentTimeMillis();
        present.init(StartActivity.this, url, type, startTime);
//        DefaultDomainUtils.init(this,null);
        // 获取唤醒参数
//        OpenInstall.getWakeUp(getIntent(), wakeUpAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.d("onNewIntent_StartActivity");
        // 此处要调用，否则App在后台运行时，会无法截获
//        OpenInstall.getWakeUp(intent, wakeUpAdapter);

        // 此处要调用，否则app在后台运行时，会无法截获
//        ShareInstall.getInstance().getWakeUpParams(intent, wakeUpListener);
    }

//    AppWakeUpAdapter wakeUpAdapter = new AppWakeUpAdapter() {
//        @Override
//        public void onWakeUp(AppData appData) {
//            // 打印数据便于调试
//            LogUtils.d("OpenInstall", "getWakeUp : wakeupData = " + appData.toString());
//            // 获取渠道数据
//            String channelCode = appData.getChannel();
//            // 获取绑定数据
//            String bindData = appData.getData();
//        }
//    };

    /**
     * 设置倒计时
     *
     * @param s
     */
    @Override
    public void setTimeText(int over_status, String s) {
        if (tvTime != null) {
            tvTime.setTag(over_status);
            tvTime.setText(s);
        }
    }

    /**
     * 已有数据直接设置图片
     *
     * @param totalTimeMap 切换锚点
     * @param totalTime    总倒计时
     */
    @Override
    public void setAdGuideVisible(int over_status, LinkedHashMap<Integer, Integer> totalTimeMap, int totalTime,
                                  List<AdGuideData.ListBean> data) {
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        if (time >= 3400) {
            setAdGuide(over_status, totalTimeMap, totalTime, data);
        } else {
            MyApp.getMainHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setAdGuide(over_status, totalTimeMap, totalTime, data);
                }
            }, (3600 - time));
        }
    }

    private void setAdGuide(int over_status, LinkedHashMap<Integer, Integer> totalTimeMap, int totalTime,
                            List<AdGuideData.ListBean> data) {
        pagePv(false);
        pageName = MyApp.getLanguageString(getContext(), R.string.startactivity_StartAdPage);
        pagePv(true);
        present.setTvLotteryIngAnimator(over_status, totalTimeMap, totalTime);
        viewPager.setAdapter(new StartPagerAdapter(getApplicationContext(), data, o -> {
            present.startMainActivity(o);
        }));
        viewPager.addOnPageChangeListener(this);

//        rlStart.startAnimation(present.getAnimation(1f, 0));
//        rlStart.setVisibility(View.GONE);

        rlAdContent.startAnimation(present.getAnimation(0, 1f));
        rlAdContent.setVisibility(View.VISIBLE);

    }

    /**
     * 页面访问路径
     *
     * @param isStart
     */
    private void pagePv(boolean isStart) {
        if (isStart) {
            onPageStart(pageName);
        } else {
            onPageEnd(pageName);
        }
    }

    /**
     * 获取广告页码
     *
     * @return
     */
    @Override
    public int getCurrentItem() {
        return viewPager.getCurrentItem();
    }

    /**
     * 设置广告页码
     *
     * @param currentItem
     */
    @Override
    public void setCurrentItem(int currentItem) {
        viewPager.setCurrentItem(currentItem);
    }


    @OnClick({R.id.view_click_time})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.view_click_time) {//跳过
            if (tvTime != null && tvTime.getTag() != null) {
                Integer tag = (Integer) tvTime.getTag();
                if (tag != null && tag == 0) {
                    return;
                }
            }
            present.startToView(System.currentTimeMillis());
        }
    }


    /**
     * 登录失效,
     *
     * @param event
     */
    @Override
    public void tokenTimeOut(TokenTimeOutEvent event) {
        present.removeTimeOut();
        super.tokenTimeOut(event);
    }


    @Override
    public void onResume() {
        super.onResume();
//        setWhiteText();
//        setViewPerch();
//        setDecorViewBg(false);
        pagePv(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        pagePv(false);
    }

//    /**
//     * 设置状态栏高度占位
//     */
//    private void setViewPerch() {
//        ViewGroup contentView = findViewById(android.R.id.content);
//        if (contentView != null) {
//            contentView.setPadding(0, 0, 0, 0);
//        }
//        viewPerch.getLayoutParams().height = (int) (StatusBarUtil.getStatusBarHeight(getApplicationContext()) / 2f);
//    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (present != null) {
            present.onPageSelected();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
