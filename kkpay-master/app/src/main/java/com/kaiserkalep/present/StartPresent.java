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
 * ?????????
 *
 * @Auther: Jack
 * @Date: 2019/10/21 09:12
 * @Description:
 */
public class StartPresent extends ActivityPresent {


    private final int NET_DELAYED_TIME = 5000;//????????????????????????(??????)
    private ValueAnimator valueAnimator;
    private int animatorValue = -1;//?????????????????????
    private List<AdGuideData.ListBean> data;//???????????????????????????????????????????????????
    private boolean netSucceed;//??????????????????????????????
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
     * ?????????????????????
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
     * ??????????????????
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
     * ????????????
     *
     * @param context
     */
    private void initData(Activity context) {
        SPUtil.setSystemStartTime();//??????????????????
        MyDialogManager.getManager().initAllDialog();
        MyDataManager.cleanVersion(context.getApplicationContext());
//        NetWorkService.startMyService(context.getApplicationContext());
    }

    /**
     * ??????????????????
     */
    private void getVersionUpdate() {
        VersionUpdateUtils.initVersionUpdateUtils(getCtrl(), null);
    }

    /**
     * ??????????????????
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
     * ???????????????
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
     * ??????????????????
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
     * ??????????????????
     *
     * @param data
     */
    public void startMainActivitye(AdGuideData.ListBean data) {
        if (data == null) {
            return;
        }
        String url = data.getUrl();
        if (!CommonUtils.StringNotNull(url)) {//????????????
            return;
        }
        if (isFastClick()) {
            return;
        }
        //???????????????0????????????1??????????????????2???????????????
        int linkType = data.getLinkType();
        if (linkType == 0) {
            return;
        }
        FragmentActivity context = getContext();
        ((ZZActivity) context).setShow(false);//????????????????????????
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
     * ????????????????????????/????????????
     *
     * @param over_status
     * @param response
     */
    private void setAdGuide(int over_status, List<AdGuideData.ListBean> response) {
        netSucceed = true;
        if (!IStart.isShow()) {//????????????
            startToView(System.currentTimeMillis());
            return;
        }
        if (!CommonUtils.ListNotNull(response)) {//?????????
            startToView(System.currentTimeMillis());
            return;
        }

        List<String> loadImageUrls = new ArrayList<>();//?????????????????????url??????
        List<String> deleteImageUrls = new ArrayList<>();//?????????????????????url??????
        LinkedHashMap<Integer, Integer> totalTimeMap = new LinkedHashMap<>();//??????????????????????????????
        int totalTime = 0;//?????????
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
                    } else {//???????????????,????????????
                        loadImageUrls.add(imageUrl);
                    }
                }
            }
        }

        if (CommonUtils.ListNotNull(data)) {//??????????????????????????????
            //????????????
            IStart.setAdGuideVisible(over_status, totalTimeMap, totalTime, data);
        }
        //??????????????????
        if (CommonUtils.ListNotNull(deleteImageUrls)) {
            try {
                MyDataManager.deleteOverdueAd(getContext().getApplicationContext(), deleteImageUrls);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //??????????????????
        if (CommonUtils.ListNotNull(loadImageUrls)) {
            MyDataManager.downLoadImage(getContext().getApplicationContext(), loadImageUrls, o -> getContext().finish());
        }
        //?????????????????????:????????????
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
     * ????????????
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


    private long lastClickTime = 0L; // ????????????????????????

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
        if (netSucceed) {//???????????????????????????
            return;
        }
        //??????????????????
        startToView(System.currentTimeMillis());
    };

    /**
     * ?????????????????????
     */
    public void cancelAnimator() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    /**
     * ??????????????????
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
     * ?????????????????????
     */
    public void setTvLotteryIngAnimator(int over_status, LinkedHashMap<
            Integer, Integer> totalTimeMap, int totalTime) {
        if (CommonUtils.MapNotNull(totalTimeMap)) {
            cancelAnimator();
            valueAnimator = ValueAnimator.ofInt(0, totalTime).setDuration(totalTime * 1000);
            valueAnimator.setInterpolator(new LinearInterpolator());//??????
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
