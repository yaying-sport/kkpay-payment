package com.kaiserkalep.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager.widget.ViewPager;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.base.IntentUtils;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.CommDialogData;
import com.kaiserkalep.bean.MyOrderBean;
import com.kaiserkalep.bean.OrderNoticeDialogData;
import com.kaiserkalep.bean.OrderStatusNoticeBean;
import com.kaiserkalep.bean.UnReadOrderBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.eventbus.MainActivityIndexEvent;
import com.kaiserkalep.interfaces.MainViewCallBackListener;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.present.OrderNotifyManager;
import com.kaiserkalep.service.CpuWakeLock;
import com.kaiserkalep.service.KKpayBackgroundService;
import com.kaiserkalep.service.PollingReceiver;
import com.kaiserkalep.service.PollingUtils;
import com.kaiserkalep.service.ScreenObserver;
import com.kaiserkalep.ui.fragmnet.HomeFragmentPro;
import com.kaiserkalep.ui.fragmnet.OrderFragment;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.NetWorkCaCheUtils;
import com.kaiserkalep.utils.PushMessageUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.notification.NotificationParams;
import com.kaiserkalep.widgets.notification.NotificationUtils;
import com.king.zxing.Intents;
import com.opensource.svgaplayer.SVGAManager;

import com.kaiserkalep.R;
import com.kaiserkalep.adapter.PagerBaseAdapter;
import com.kaiserkalep.base.DialogCommBase;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.interfaces.UnReadMsgListener;
import com.kaiserkalep.ui.fragmnet.MineFragment;
import com.kaiserkalep.ui.fragmnet.SaleFragment;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.LogUtils;

import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.widgets.MainHomeBottomTab;
import com.kaiserkalep.widgets.NoAnimationViewPager;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.kaiserkalep.constants.Config.FOUR_STRING;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.OWN_ONE_STRING;
import static com.kaiserkalep.constants.Config.THREE_STRING;
import static com.kaiserkalep.constants.Config.TWO_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;


/**
 * ????????????:  ??????
 *
 * @auther: Jack
 * @date: 2019/12/16 10:50
 */
public class MainActivity extends ZZActivity implements UnReadMsgListener, MainViewCallBackListener {

    @BindView(R.id.cvp_view)
    NoAnimationViewPager mViewPager;
    @BindView(R.id.magic_indicator)
    MainHomeBottomTab magicIndicator;

    public static int TAB_HOME = 0;
    public static int TAB_SALE = 1;
    //    public static int TAB_SERVICE = 2;
    public static int TAB_ORDER = 2;
    public static int TAB_MINE = 3;
    public static final int REQUEST_CODE_SCAN = 0X01;
    public List<PageBaseFragment> mDataList = new ArrayList<>();
    public int position = 0;
    private HomeFragmentPro homeFragment;
    private boolean isOrderReading = false;

    private NotificationUtils notificationUtils;
    public static MainActivity instance = null;

    @Override
    public int getViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //??????
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON //?????????????????????
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON//????????????
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED//?????????????????????
        );
        if (Build.VERSION.SDK_INT > 27) {
            setShowWhenLocked(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }
        instance = this;
    }

    @Override
    public void afterViews() {
        homeFragment = new HomeFragmentPro();
        homeFragment.onUserViewVisible();
        mDataList.add(homeFragment);
        mDataList.add(new SaleFragment());
//        mDataList.add(new ServiceFragment());
        mDataList.add(new OrderFragment());
        mDataList.add(new MineFragment());
        mViewPager.setAdapter(new PagerBaseAdapter(getSupportFragmentManager(), mDataList));
        mViewPager.setOffscreenPageLimit(mDataList.size());
        initMainTab();
        getNotifySetting();
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (null != mDataList && mDataList.size() > position) {
                    MainActivity.this.position = position;
                    mDataList.get(position).onUserViewVisible();
                    setStatusBarColor();
                }
            }
        });
        screenObserver();
    }

    @Override
    public void onResume() {
        super.onResume();
        setStatusBarColor();
    }


    public void setStatusBarColor() {
        if (position == 0 || position == 3) {
            setStatusBarTextColor(1);
        } else {
            setStatusBarTextColor(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startCallbackNet(false, true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (null != mDataList) {
            if (mDataList.size() > position && null != mDataList.get(position)) {
                mDataList.get(position).onUserViewVisible();
            }
        }
    }

    /**
     * ????????????
     */
//    public void stopStartCallBackNet() {
//        startCallbackNet(false, false);
//    }

    /**
     * ??????????????????
     *
     * @param refreshNow ????????????????????????
     * @param isStart    ???????????????
     */
    public void startCallbackNet(boolean refreshNow, boolean isStart) {
        OrderNotifyManager manager = OrderNotifyManager.getManager();
        if (isStart) {
            manager.startRefresh(refreshNow, o -> {
                //???????????? ??????handle
//                if (!isOrderReading && MyApp.getApplication().isScreenOn != 0) {
                    unReadOrderNotice();
//                }
            });
        } else {
            manager.stopRefresh();
        }
    }

    /**
     * ????????????????????????
     */
    public void unReadOrderNotice() {
        isOrderReading = true;
        new FundImpl(new ZZNetCallBack<UnReadOrderBean>(this, UnReadOrderBean.class) {// ?????? ??????0???????????????1???????????????2???????????????3????????????4???????????????-1??????
            @Override
            public void onSuccess(UnReadOrderBean unRead) {
                if (null != unRead && ZERO_STRING.equals(unRead.getIsRead())) {//??????????????????
                    showOrderNotice(unRead.getId(), unRead.getOrderNo(), unRead.getStatus(), unRead.getCreateTime());
                }
                // ?????? ??????0???????????????1???????????????2???????????????3????????????4???????????????-1??????
                if (unRead != null) {
                    if (ZERO_STRING.equals(unRead.getStatus())
                            || ONE_STRING.equals(unRead.getStatus())
                            || TWO_STRING.equals(unRead.getStatus())
                            || THREE_STRING.equals(unRead.getStatus())) {
                        MyOrderBean.RowsDTO r = new MyOrderBean.RowsDTO();
                        r.setOrderNo(unRead.getOrderNo());
                        r.setStatus(unRead.getStatus());
                        MyApp.getApplication().orderMap.put(unRead.getOrderNo(), r);
                    } else if (FOUR_STRING.equals(unRead.getStatus())
                            || OWN_ONE_STRING.equals(unRead.getStatus())) {
                        //3????????????4 ?????????-1??????
                        MyApp.getApplication().orderMap.remove(unRead.getOrderNo());
                        MyApp.getApplication().checkOrder();
                    }
                    //?????????????????? ??? ?????? ???????????????????????????????????????
                    // 0???????????????1???????????????2???????????????3????????????4???????????????-1??????
                    if (FOUR_STRING.equals(unRead.getStatus())
                            || OWN_ONE_STRING.equals(unRead.getStatus())) {
                        if (homeFragment != null) {
                            homeFragment.getMySell(false);
                        }
                    }
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                isOrderReading = false;
            }
        }.setNeedDialog(false).setTimeOut(10000).setNeedToast(false)).getOrderListUnReadNotice();
    }

    /**
     * ????????????????????????
     *
     * @param id
     * @param orderId
     * @param status  ????????????
     */
    private void showOrderNotice(String id, String orderId, String status, String time) {
        HashMap<String, String> orderNoticeId = NetWorkCaCheUtils.getOrderNoticeId();
        if (null == orderNoticeId.get(id)) {//???????????????????????????????????????????????????????????????
            readNotice(orderId);//????????????????????????
            orderNoticeId.put(id, id);
            NetWorkCaCheUtils.setOrderNoticeId(orderNoticeId);//??????????????????
            String title = "";
            String content = "";
            String notifyTitle = MyApp.getLanguageString(MainActivity.this, R.string.notify_notify_title);
            String notifyText = "";
            boolean isCancel = false;
            switch (status) {
                case ZERO_STRING://????????????
                    EventBusUtil.post(new OrderStatusNoticeBean());
                    title = MyApp.getLanguageString(MainActivity.this, R.string.notify_order_title);
                    content = MyApp.getLanguageString(MainActivity.this, R.string.notify_order_queren);
                    notifyText = String.format(MyApp.getLanguageString(MainActivity.this, R.string.notify_notify_queren), orderId);
                    isCancel = false;
                    break;
                case ONE_STRING://????????????
                    title = MyApp.getLanguageString(MainActivity.this, R.string.notify_order_title);
                    content = MyApp.getLanguageString(MainActivity.this, R.string.notify_order_fukuanzhuanzhang);
                    notifyText = String.format(MyApp.getLanguageString(MainActivity.this, R.string.notify_notify_xianxiafukuan), orderId);
                    isCancel = false;
                    break;
                case TWO_STRING://????????????
                    title = MyApp.getLanguageString(MainActivity.this, R.string.notify_order_title);
                    content = MyApp.getLanguageString(MainActivity.this, R.string.notify_order_dabi);
                    notifyText = String.format(MyApp.getLanguageString(MainActivity.this, R.string.notify_notify_dabi), orderId);
                    isCancel = false;
                    break;
                case THREE_STRING://??????
                    EventBusUtil.post(new OrderStatusNoticeBean());
                    title = MyApp.getLanguageString(MainActivity.this, R.string.notify_order_title);
                    content = MyApp.getLanguageString(MainActivity.this, R.string.notify_order_zanting);
                    notifyText = String.format(MyApp.getLanguageString(MainActivity.this, R.string.notify_notify_zanting), orderId);
                    isCancel = false;
                    break;
                case FOUR_STRING://????????????
                    EventBusUtil.post(new OrderStatusNoticeBean());
                    title = MyApp.getLanguageString(MainActivity.this, R.string.notify_order_title);
                    content = MyApp.getLanguageString(MainActivity.this, R.string.notify_order_yidabi);
                    notifyText = String.format(MyApp.getLanguageString(MainActivity.this, R.string.notify_notify_yidabi), orderId);
                    isCancel = false;
                    break;
                case OWN_ONE_STRING://-1??????
                    EventBusUtil.post(new OrderStatusNoticeBean());
                    title = MyApp.getLanguageString(MainActivity.this, R.string.notify_order_cancel);
                    content = String.format(MyApp.getLanguageString(MainActivity.this, R.string.notify_order_xxIscancel), orderId);
                    notifyText = String.format(MyApp.getLanguageString(MainActivity.this, R.string.notify_notify_yicancel), orderId);
                    isCancel = true;
                    break;
            }

            setNotification(id, orderId, title, content, notifyTitle, notifyText, isCancel, time);
        }
    }

    public void setNotification(String id, String orderId, String title, String content, String notifyitle, String notifycontent, boolean isCancel, String time) {

        int integer;
        try {
            integer = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            integer = Config.NotificationID;
        }
        //??????intent-????????????Notification?????????
        Intent resultIntent = new Intent(this, OrderDetailsActivity.class);
        resultIntent.putExtra(StringConstants.ORDERNO, orderId);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, integer, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //?????? Notification ??? flags = FLAG_NO_CLEAR
        //FLAG_NO_CLEAR ????????????????????????????????????????????????????????????,????????????????????????,???????????? cancel() ????????????
        //flags ???????????? |= ??????????????????
        notificationUtils = new NotificationUtils(this);
        NotificationParams notificationParams = new NotificationParams();
        notificationParams
                .setOngoing(true)  //???????????????????????????????????????????????????
                .setContentIntent(resultPendingIntent)
                .setTicker(Config.SCHEME) //????????????????????????
                .setOnlyAlertOnce(true)//?????????????????????
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)  //??????sound
                .setPriority(Notification.PRIORITY_MAX)   //???????????????
                .setVibrate(new long[]{96, 120, 180})//??????????????????
                .setFlags(Notification.FLAG_AUTO_CANCEL);

        //????????????????????????????????????
        notificationUtils.setNotificationParams(notificationParams).sendNotification(integer, notifyitle, notifycontent, R.drawable.icon_notify);
        CommonUtils.showPhoneWakeUp(this);
        //??????????????????(????????????????????????????????????????????? ????????????????????????)
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
        rt.play();
        //????????????
        if (MyDialogManager.getManager().isCanShowOrderNoticeDialog()) {
            MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.OrderNoticeDialog,
                    new OrderNoticeDialogData(integer, title, content, isCancel,
                            o -> {
                                if (null != notificationUtils && null != o && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//??????????????????????????????
                                    notificationUtils.clearNotificationChannel(o);
                                }
                                Intent intent = new Intent(MainActivity.this, OrderDetailsActivity.class);
                                intent.putExtra(StringConstants.ORDERNO, orderId);
                                MainActivity.this.startActivity(intent);
                            }, null));
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param orderno
     */
    private void readNotice(String orderno) {
        if (CommonUtils.StringNotNull(orderno)) {
            new FundImpl(new ZZNetCallBack<Object>(MainActivity.this, Object.class) {
                @Override
                public void onSuccess(Object response) {

                }
            }.setNeedDialog(false).setNeedToast(false)).setReadNotice(orderno);
        }
    }

    /**
     * ????????????tab
     */
    private void initMainTab() {
        magicIndicator.bindData(mViewPager, this);
    }


    @Override
    public void onThemeUpdate() {
        super.onThemeUpdate();
        if (CommonUtils.ListNotNull(mDataList)) {
            for (PageBaseFragment pageBaseFragment : mDataList) {
                if (pageBaseFragment != null) {
                    pageBaseFragment.onThemeUpdate();
                }
            }
        }
    }

    /**
     * ????????????
     * index(??????-????????????)
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MainActivityIndexEvent event) {
        if (event != null && mViewPager != null) {
            try {
                int index = event.getIndex();
                if (index >= ZERO) {
                    if (CommonUtils.ListNotNull(mDataList) && index < mDataList.size()) {
                        mViewPager.setCurrentItem(index, false);
                        int indexSecond = event.getIndexSecond();
                        if (indexSecond >= ZERO) {
                            PageBaseFragment pageBaseFragment = mDataList.get(index);
                            if (pageBaseFragment != null) {
                                pageBaseFragment.setIndex(indexSecond);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ????????????????????????
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exitBy2Click(getApplicationContext())) {// ????????????????????????
                finish();
            }
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.d("MainActivity_onNewIntent");
        setIntent(intent);//????????????intent
    }


    /**
     * ???????????????
     */
    private void handleDialog() {
        runOnUiThread(() -> {
            try {
                DialogCommBase currentDialog = MyDialogManager.getManager().currentDialog;
                if (currentDialog != null
                        && currentDialog.isShowing()
                        && currentDialog.level != MyDialogManager.LEVEL_TYPE.ONE.value) {
                    currentDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onUnReadMsg(int count) {
        if (magicIndicator != null) {
            magicIndicator.showNewMessage(count);
        }
    }

    @Override
    protected void onDestroy() {
        startCallbackNet(false, false);
        SVGAManager.getManager().release();
        KKpayBackgroundService.stopService(getContext());
        super.onDestroy();
    }

    @Override
    public void succeedCallBack(@Nullable Object o) {

    }

    @SuppressLint("CheckResult")
    @Override
    public void cameraCallBack() {
        Context context = getContext();
        if (null == context || JudgeDoubleUtils.isDoubleClick()) {
            return;
        }
        String RationaleCaneraMsg = MyApp.getLanguageString(context, R.string.Rationale_Canera_Msg);
        new RxPermissions(this)
                .requestEachCombined(Manifest.permission.CAMERA)//??????
                .subscribe(permission -> {
                    if (permission.granted) {
                        // All permissions are granted !
                        goToCamera();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // At least one denied permission without ask never again
                        toast(RationaleCaneraMsg);
                    } else {
                        // At least one denied permission with ask never again
                        // Need to go to the settings
                        showRejectCameraDialog(RationaleCaneraMsg);
                    }
                });
    }

    private void goToCamera() {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(MainActivity.this, R.anim.in, R.anim.out);
        Intent intent = new Intent(MainActivity.this, CameraCaptureActivity.class);
        MainActivity.this.startActivityForResult(intent, REQUEST_CODE_SCAN, optionsCompat.toBundle());
    }

    private void showRejectCameraDialog(String msg) {
        Context context = getContext();
        if (null != context) {
            String tipAudioCaneraTitle = MyApp.getLanguageString(context, R.string.Rationale_Canera_title);
            String goToSettingTip = MyApp.getLanguageString(context, R.string.goTo_Setting_tip);
            String cancel = MyApp.getLanguageString(context, R.string.Share_cancel);
            new AlertDialog.Builder(context)
                    .setTitle(tipAudioCaneraTitle)
                    .setMessage(msg)
                    .setPositiveButton(goToSettingTip, (dialog, which) -> {
                        dialog.dismiss();
                        Uri packageURI = Uri.parse("package:" + context.getPackageName());
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }).setNegativeButton(cancel, (dialog, i) -> {
                dialog.dismiss();
            }).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN://??????????????????
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    if (!IntentUtils.StartScanResultIntent(this, result)) {
                        toast(MyApp.getLanguageString(getContext(), R.string.QrPhoto_Error_Tip));
                    }
                    break;
            }

        }
    }

    private void getNotifySetting() {
        boolean booleanValue = SPUtil.getBooleanValue(SPUtil.IS_FIRSTSTARTAPP_NOTIFY);
        NotificationManagerCompat notification = NotificationManagerCompat.from(this);
        boolean isEnabled = notification.areNotificationsEnabled();
        if (!booleanValue || !isEnabled) {//???????????????,???????????????????????????????????????
            SPUtil.setBooleanValue(SPUtil.IS_FIRSTSTARTAPP_NOTIFY, true);
            String OpenNoticeTitle = MyApp.getLanguageString(this, R.string.Open_Notice_Title);
            String ShareCancel = MyApp.getLanguageString(this, R.string.Share_cancel);
            String ShareCertain = MyApp.getLanguageString(this, R.string.Share_certain);
            String OpenFirstNoticeContentOne = MyApp.getLanguageString(this, R.string.Open_First_Notice_Content_one);
            String OpenFirstNoticeContentTwo = MyApp.getLanguageString(this, R.string.Open_First_Notice_Content_two);
            String OpenFirstNoticeContentThr = MyApp.getLanguageString(this, R.string.Open_First_Notice_Content_thr);
            MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.CommonDialog,
                    new CommDialogData(OpenNoticeTitle,
                            OpenFirstNoticeContentOne + "\n" + OpenFirstNoticeContentTwo + "\n" + OpenFirstNoticeContentThr,
                            ShareCancel, ShareCertain, null, v -> {
                        Config.toSettingNotify(MainActivity.this);
                    }));

            if (!booleanValue) {
                if (null != homeFragment) {
                    homeFragment.setHelpTutorial();
                }
            }
        }
    }

    //???????????? ??????????????? ??????
    ScreenObserver screenObserver;

    private void screenObserver() {
        //??????????????????
        screenObserver = new ScreenObserver(this);
        screenObserver.startObserver(new ScreenObserver.ScreenStateListener() {
            @Override
            public void onScreenOff() {
                MyApp.getApplication().isScreenOn = 0;
                if (MyApp.getApplication().pendingOrderMap.size() > 0
                        || MyApp.getApplication().orderMap.size() > 0) {
                    KKpayBackgroundService.startService(getContext());
                }
                //????????? ????????????????????????
//                PollingUtils.startPollingService(MainActivity.this, 3, PollingReceiver.class, PollingUtils.ACTION);
                CpuWakeLock.getInstance().lock(MainActivity.this, 1000 * 60 * 60 * 4);
            }

            @Override
            public void onScreenOn() {
                MyApp.getApplication().isScreenOn = 1;
                //????????? ????????????????????????
//                PollingUtils.stopPollingService(MainActivity.this, PollingReceiver.class, PollingUtils.ACTION);
                CpuWakeLock.getInstance().unlock();
            }

            @Override
            public void onUserPresent() {
                MyApp.getApplication().isScreenOn = 2;
                //???????????? app?????????????????? ????????????
                if (CommonUtils.isAppShow(getContext())) {
                    KKpayBackgroundService.stopService(getContext());
                }
            }
        });
    }
}
