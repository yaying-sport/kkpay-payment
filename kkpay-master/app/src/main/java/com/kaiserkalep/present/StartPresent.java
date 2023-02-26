package com.kaiserkalep.present;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;

import androidx.fragment.app.FragmentActivity;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ActivityPresent;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.IntentUtils;
import com.kaiserkalep.base.ZZActivity;

import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.AboutBean;
import com.kaiserkalep.bean.AdGuideData;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.net.impl.WebsiteImpl;
import com.kaiserkalep.ui.activity.DepositShActivity;
import com.kaiserkalep.ui.activity.LoginActivity;
import com.kaiserkalep.ui.activity.MainActivity;
import com.kaiserkalep.ui.activity.ScanBindActivity;
import com.kaiserkalep.ui.activity.ScanCallbackJumpActivity;
import com.kaiserkalep.ui.activity.ScanDetailActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.DefaultDomainUtils;
import com.kaiserkalep.utils.ImageUtils;
import com.kaiserkalep.utils.MyDataManager;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.NetWorkCaCheUtils;
import com.kaiserkalep.utils.NetWorkUtils;
import com.kaiserkalep.utils.PushMessageUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.utils.VersionUpdateUtils;
import com.kaiserkalep.utils.wustrive.aesrsa.util.RSA;
import com.kaiserkalep.view.IStartLaunch;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.kaiserkalep.constants.Config.FOUR_STRING;
import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.THREE_STRING;
import static com.kaiserkalep.constants.Config.TWO_STRING;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 启动页
 *
 * @Auther: Jack
 * @Date: 2019/10/21 09:12
 * @Description:
 */
public class StartPresent extends ActivityPresent {


    private final int NET_DELAYED_TIME = 5000;//请求接口超时时间(毫秒)
    private ValueAnimator valueAnimator;
    private int animatorValue = -1;//倒计时多次回调
    private List<AdGuideData.ListBean> data;//已下载好图片并且需要展示的广告集合
    private boolean netSucceed;//广告接口请求是否成功
    private IStartLaunch IStart;
    private String scheme;
    private String type;
    private long startTime;
    private String shareJumpIoOver, ShareGoJoin;

    public StartPresent(IStartLaunch IStart) {
        super(IStart);
        this.IStart = IStart;
    }

    /**
     * 初始数据及开始
     *
     * @param context
     */
    public void init(Activity context, String scheme, String type, long time) {
        this.scheme = scheme;
        this.type = type;
        this.startTime = time;
        shareJumpIoOver = MyApp.getLanguageString(context, R.string.Share_JumpIoOver);
        ShareGoJoin = MyApp.getLanguageString(context, R.string.Share_GoJoin);
        new DefaultDomainUtils().init(context, defaultDomains -> {
            MyApp.post(() -> {
                initData(context);
                getVersionUpdate();
                getAbout();
                getAdData();
            });
        });
    }

    /**
     * 获取用户须知
     */
    private void getAbout() {
        new WebsiteImpl(new ZZNetCallBack<List<AboutBean>>(getCtrl(), AboutBean.class) {
            @Override
            public void onSuccess(List<AboutBean> response) {
                if (null != response) {
                    for (int i = 0; i < response.size(); i++) {
                        AboutBean aboutBean = response.get(i);
                        if (null != aboutBean) {
                            String id = aboutBean.getId();
                            String content = aboutBean.getContent();
                            if (ONE_STRING.equals(id)) {
                                SPUtil.setRegistNotice(content);
                            } else if (TWO_STRING.equals(id)) {
                                SPUtil.setBuyOrderNotic(content);
                            } else if (THREE_STRING.equals(id)) {
                                SPUtil.setSellOrderNotic(content);
                            } else if (FOUR_STRING.equals(id)) {
                                SPUtil.setHelpUrl(content);
                            }
                        }
                    }
                }
            }
        }.setNeedDialog(false).setNeedToast(false)).getAbout();
    }

    /**
     * 初始数据
     *
     * @param context
     */
    private void initData(Activity context) {
        SPUtil.setSystemStartTime();//储存启动时间
        MyDialogManager.getManager().initAllDialog();
        MyDataManager.cleanVersion(context.getApplicationContext());
//        NetWorkService.startMyService(context.getApplicationContext());
    }

    /**
     * 处理版本更新
     */
    private void getVersionUpdate() {
        VersionUpdateUtils.initVersionUpdateUtils(getCtrl(), null);
    }

    /**
     * 获取广告接口
     */
    private void getAdData() {
        MyApp.postDelayed(() -> {
            if (!NetWorkUtils.isNetworkConnected()) {
                AdGuideData adGuideData = NetWorkCaCheUtils.getAdGuideData();
                FragmentActivity context = getContext();
                if (context != null) {
                    ((ZZActivity) context).setShow(true);
                }
                toast("");
                setAdData(adGuideData);
                return;
            }
            MyApp.postDelayed(timeOut, NET_DELAYED_TIME);
            new WebsiteImpl(new ZZNetCallBack<AdGuideData>(getCtrl(), AdGuideData.class) {
                @Override
                public void onSuccess(AdGuideData response) {
                    setAdData(response);
                    NetWorkCaCheUtils.setAdGuideData(response);
                }
            }.setNeedDialog(false).setNeedToast(false)).getWebsiteStartAd();
        }, 300);
    }

    /**
     * 设置启动页
     *
     * @param response
     */
    private void setAdData(AdGuideData response) {
        if (response != null && CommonUtils.ListNotNull(response.getList())) {
            setAdGuide(response.getOverStatus(), response.getList());
        } else {
            startToView(System.currentTimeMillis());
        }
    }


    public void onPageSelected() {
        lastClickTime = 0;
    }

    /**
     * 点击广告跳转
     *
     * @param url
     */
    public void startMainActivity(AdGuideData.ListBean url) {
        try {
            startMainActivitye(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击广告跳转
     *
     * @param data
     */
    public void startMainActivitye(AdGuideData.ListBean data) {
        if (data == null) {
            return;
        }
        String url = data.getUrl();
        if (!CommonUtils.StringNotNull(url)) {//空无跳转
            return;
        }
        if (isFastClick()) {
            return;
        }
        //跳转类型，0无跳转，1内部浏览器，2外部浏览器
        int linkType = data.getLinkType();
        if (linkType == 0) {
            return;
        }
        FragmentActivity context = getContext();
        ((ZZActivity) context).setShow(false);//拦截广告自动跳转
        cancelAnimator();
        Intent mainIntent;
        if (isLogin()) {
            mainIntent = new Intent(context, MainActivity.class);
        } else {
            mainIntent = new Intent(context, LoginActivity.class);
        }
        if (linkType == 1) {
            Intent in = IntentUtils.getIntent(context.getApplicationContext(),
                    MyApp.getMyString(R.string.WebViewActivity), IntentUtils.getHashObj(new String[]{
                            StringConstants.TITLE, data.getTitle(),
                            StringConstants.URL, url}));
            if (in != null) {
                context.startActivities(new Intent[]{mainIntent, in});
            } else {
                context.startActivity(mainIntent);
            }
        } else {
            context.startActivity(mainIntent);
            IntentUtils.gotoDefaultWeb(getCtrl(), url);
        }
        context.finish();
    }

    /**
     * 根据接口数据设置/获取广告
     *
     * @param over_status
     * @param response
     */
    private void setAdGuide(int over_status, List<AdGuideData.ListBean> response) {
        netSucceed = true;
        if (!IStart.isShow()) {//超时跳转
            startToView(System.currentTimeMillis());
            return;
        }
        if (!CommonUtils.ListNotNull(response)) {//无数据
            startToView(System.currentTimeMillis());
            return;
        }

        List<String> loadImageUrls = new ArrayList<>();//需要下载的图片url集合
        List<String> deleteImageUrls = new ArrayList<>();//需要删除的图片url集合
        LinkedHashMap<Integer, Integer> totalTimeMap = new LinkedHashMap<>();//广告需要切换的时间点
        int totalTime = 0;//总时长
        for (int i = 0; i < response.size(); i++) {
            AdGuideData.ListBean imageData = response.get(i);
            if (imageData != null) {
                String imageUrl = imageData.getImage();
                if (CommonUtils.StringNotNull(imageUrl)) {
                    File file = ImageUtils.getAdGuideFile(getContext().getApplicationContext(), imageUrl);
                    if (file != null) {
                        totalTime += imageData.getShowTime();//3/4/5
                        totalTimeMap.put(response.size() > 1 ? totalTime : 0, response.size() > 1 ? i + 1 : i);
                        AdGuideData.ListBean clone = imageData.clone();
                        if (clone != null) {
                            clone.setFile(file);
                            if (data == null) {
                                data = new ArrayList<>();
                            }
                            data.add(clone);
                        }
                    } else {//图片不存在,需要下载
                        loadImageUrls.add(imageUrl);
                    }
                }
            }
        }

        if (CommonUtils.ListNotNull(data)) {//有可以显示的图片广告
            //展示广告
            IStart.setAdGuideVisible(over_status, totalTimeMap, totalTime, data);
        }
        //删除过期广告
        if (CommonUtils.ListNotNull(deleteImageUrls)) {
            try {
                MyDataManager.deleteOverdueAd(getContext().getApplicationContext(), deleteImageUrls);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //下载广告图片
        if (CommonUtils.ListNotNull(loadImageUrls)) {
            MyDataManager.downLoadImage(getContext().getApplicationContext(), loadImageUrls, o -> getContext().finish());
        }
        //没有广告在显示:跳转首页
        if (!CommonUtils.ListNotNull(data)) {
            startToView(System.currentTimeMillis());
        }
    }


    public void startToView(long endTime) {
        Log.e("answer", "startToView:" + endTime);
        long time = endTime - startTime;
        if (time >= 3400) {
            jumpView();
        } else {
            MyApp.getMainHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    jumpView();
                }
            }, (3600 - time));
        }
    }

    /**
     * 跳转首页
     */
    private void jumpView() {
        netSucceed = true;
        if (isFastClick() && null != IStart && null != IStart.getActivity()) {
            return;
        }
        cancelAnimator();
        FragmentActivity activity = IStart.getActivity();
        if (isLogin()) {
            if (!IntentUtils.StartScanResultIntent(activity, scheme)) {
                activity.startActivity(new Intent(activity, MainActivity.class));
            }
        } else {
            activity.startActivity(new Intent(activity, LoginActivity.class));
        }
        activity.finish();
    }


    private long lastClickTime = 0L; // 上一次点击的时间

    private boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ((time - lastClickTime) > 600) {
            lastClickTime = time;
            return false;
        }
        lastClickTime = time;
        return true;
    }

    private Runnable timeOut = () -> {
        if (netSucceed) {//设定时间内请求返回
            return;
        }
        //超时直接跳转
        startToView(System.currentTimeMillis());
    };

    /**
     * 取消倒计时动画
     */
    public void cancelAnimator() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    /**
     * 广告渐变展示
     *
     * @param fromAlpha
     * @param toAlpha
     * @return
     */
    public AlphaAnimation getAnimation(float fromAlpha, float toAlpha) {
        AlphaAnimation animation = new AlphaAnimation(fromAlpha, toAlpha);
        animation.setDuration(300);
        return animation;
    }

    /**
     * 设置等待中动画
     */
    public void setTvLotteryIngAnimator(int over_status, LinkedHashMap<
            Integer, Integer> totalTimeMap, int totalTime) {
        if (CommonUtils.MapNotNull(totalTimeMap)) {
            cancelAnimator();
            valueAnimator = ValueAnimator.ofInt(0, totalTime).setDuration(totalTime * 1000);
            valueAnimator.setInterpolator(new LinearInterpolator());//匀速
            valueAnimator.addUpdateListener(animation -> {
                int i = (int) animation.getAnimatedValue();
                if (i != animatorValue) {
                    animatorValue = i;
                    int k = totalTime - animatorValue;
                    if (totalTimeMap.containsKey(animatorValue)) {
                        Integer integer = totalTimeMap.get(animatorValue);
                        if (integer != null) {
                            if (integer <= totalTimeMap.size()) {
                                int currentItem = IStart.getCurrentItem();
                                if (currentItem != integer) {
                                    IStart.setCurrentItem(integer);
                                }
                            }
                        }
                    }
                    if (k > 0) {
                        String ss = over_status == 1 ? shareJumpIoOver + " " + k : k + "S";
                        IStart.setTimeText(over_status, ss);
                    }
                    if (animatorValue >= totalTime - (over_status == 1 ? 1 : 0)) {
                        if (over_status == 1) {
                            startToView(System.currentTimeMillis());
                        } else {
                            IStart.setTimeText(1, ShareGoJoin);
                        }
                    }
                }
            });
            valueAnimator.start();
        }
    }


    public void removeTimeOut() {
        isFastClick();
        MyApp.removeCallbacks(timeOut);
    }
}
