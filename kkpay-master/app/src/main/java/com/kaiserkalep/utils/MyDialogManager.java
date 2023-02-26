package com.kaiserkalep.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;

import com.kaiserkalep.base.ActivityBase;
import com.kaiserkalep.base.DialogCommBase;
import com.kaiserkalep.base.LoadingDialog;

import com.kaiserkalep.bean.AddWjAccountDialogData;
import com.kaiserkalep.bean.CoinNoticeBean;
import com.kaiserkalep.bean.CommDialogData;
import com.kaiserkalep.bean.NoticeDialogData;
import com.kaiserkalep.bean.OrderNoticeDialogData;
import com.kaiserkalep.bean.PayPassWordDialogData;
import com.kaiserkalep.bean.SelectBankDialogData;
import com.kaiserkalep.bean.ShAccountDialogData;
import com.kaiserkalep.bean.StatusBean;
import com.kaiserkalep.bean.LoadingDialogData;
import com.kaiserkalep.bean.MsgNoticeData;
import com.kaiserkalep.bean.SimpleDialogData;
import com.kaiserkalep.bean.SliderValidationDialogData;
import com.kaiserkalep.bean.ToMoreDialogData;
import com.kaiserkalep.bean.UpdateDate;
import com.kaiserkalep.bean.AuthenDialogData;
import com.kaiserkalep.interfaces.ActivityIntentInterface;
import com.kaiserkalep.mydialog.AddPayTypeDialog;
import com.kaiserkalep.mydialog.AddShAccountDialog;
import com.kaiserkalep.mydialog.AddWjAccountDialog;
import com.kaiserkalep.mydialog.CoinCancelDialog;
import com.kaiserkalep.mydialog.CoinConfirmPauseDialog;
import com.kaiserkalep.mydialog.CoinNoticeDialog;
import com.kaiserkalep.mydialog.CommonDialog;
import com.kaiserkalep.mydialog.InputPassWordDialog;
import com.kaiserkalep.mydialog.MsgNoticeDialog;
import com.kaiserkalep.mydialog.NoticeDialog;
import com.kaiserkalep.mydialog.OrderNoticeDialog;
import com.kaiserkalep.mydialog.RegistrNoticeDialog;
import com.kaiserkalep.mydialog.SelectBankDialog;
import com.kaiserkalep.mydialog.SimpleDialog;
import com.kaiserkalep.mydialog.SliderValidationDialog;
import com.kaiserkalep.mydialog.ToMoreUrlDialog;
import com.kaiserkalep.mydialog.VerifyDialog;
import com.kaiserkalep.ui.activity.BuyCoinCenterActivity;
import com.kaiserkalep.ui.activity.DepositShActivity;
import com.kaiserkalep.ui.activity.FeedbackActivity;
import com.kaiserkalep.ui.activity.FindPasswordActivity;
import com.kaiserkalep.ui.activity.LoginActivity;
import com.kaiserkalep.ui.activity.MainActivity;
import com.kaiserkalep.ui.activity.ManageShActivity;
import com.kaiserkalep.ui.activity.ManageShSettingActivity;
import com.kaiserkalep.ui.activity.OrderDetailsActivity;
import com.kaiserkalep.ui.activity.RegisterActivity;
import com.kaiserkalep.ui.activity.SellCoinCenterActivity;
import com.kaiserkalep.ui.activity.SellCoinDetailsActivity;
import com.kaiserkalep.ui.activity.StartActivity;
import com.kaiserkalep.ui.activity.AddWalletManageActivity;
import com.kaiserkalep.ui.activity.WalletManageActivity;
import com.kaiserkalep.utils.update.utils.AppUpdateUtils;
import com.kaiserkalep.mydialog.StatusDialog;
import com.kaiserkalep.widgets.UpdateDialog;

import java.util.ArrayList;

/**
 * 全局弹框管理类
 *
 * @Auther: Jack
 * @Date: 2019/10/9 19:34
 * @Description:
 */
public class MyDialogManager implements DialogInterface.OnDismissListener {


    /**
     * 1.版本更新弹框(全局) {@link UpdateDialog}
     */
    public static final String UpdateDialog = UpdateDialog.class.getSimpleName();
    /**
     * 平台维护状态 {@link StatusDialog}
     */
    public static final String GameUrlStatusDialog = StatusDialog.class.getSimpleName();
    /**
     * 2.通用弹框(任意页面) {@link CommonDialog}
     */
    public static final String CommonDialog = CommonDialog.class.getSimpleName();
    /**
     * 接口请求加载中弹框(任意页面) {@link LoadingDialog}
     */
    public static final String LoadingDialog = LoadingDialog.class.getSimpleName();
    /**
     * 公告弹框(重要公告)(首页) {@link NoticeDialog}
     */
    public static final String NoticeDialog = NoticeDialog.class.getSimpleName();
    /**
     * 商户更多网址 {@link ToMoreUrlDialog}
     */
    public static final String ToMoreUrlDialog = ToMoreUrlDialog.class.getSimpleName();
    /**
     * 立即实名认证 {@link VerifyDialog}
     */
    public static final String VerifyDialog = VerifyDialog.class.getSimpleName();
    /**
     * 添加收付款信息 {@link AddPayTypeDialog}
     */
    public static final String AddPayTypeDialog = AddPayTypeDialog.class.getSimpleName();
    /**
     * 订单提醒 {@link OrderNoticeDialog}
     */
    public static final String OrderNoticeDialog = OrderNoticeDialog.class.getSimpleName();
    /**
     * 暂停打比/确认打币
     */
    public static final String CoinConfirmPauseDialog = CoinConfirmPauseDialog.class.getSimpleName();
    /**
     * 取消订单/取消（订单详情）
     */
    public static final String CoinCancelDialog = CoinCancelDialog.class.getSimpleName();
    /**
     * 添加商户/玩家账号
     */
    public static final String AddShAccountDialog = AddShAccountDialog.class.getSimpleName();
    /**
     * 请输入支付密码 {@link InputPassWordDialog}
     */
    public static final String InputPassWordDialog = InputPassWordDialog.class.getSimpleName();
    /**
     * 添加玩家账号 {@link AddWjAccountDialog}
     */
    public static final String AddWjAccountDialog = AddWjAccountDialog.class.getSimpleName();
    /**
     * (公告栏)公告弹框(首页) {@link MsgNoticeDialog}
     */
    public static final String MsgNoticeDialog = MsgNoticeDialog.class.getSimpleName();
    /**
     * 滑块验证 {@link SliderValidationDialog}
     */
    public static final String SliderValidationDialog = SliderValidationDialog.class.getSimpleName();

    /**
     * 底部弹出简单弹框(任意页面) {@link SimpleDialog}
     */
    public static final String SimpleDialog = SimpleDialog.class.getSimpleName();

    /**
     * 注册须知(登录页面) {@link RegistrNoticeDialog}
     */
    public static final String RegistrNoticeDialog = RegistrNoticeDialog.class.getSimpleName();

    /**
     * 买卖须知(登录页面) {@link CoinNoticeDialog}
     */
    public static final String CoinNoticeDialog = CoinNoticeDialog.class.getSimpleName();

    /**
     * 选择银行(存款/绑定银行页面) {@link SelectBankDialog}
     */
    public static final String SelectBankDialog = SelectBankDialog.class.getSimpleName();


    public enum LEVEL_TYPE {

        占位(0),

        /**
         * 此优先级弹框已显示时,其他所有弹框不显示,未显示时,关闭其他所有已显示弹框,显示此优先级弹框
         * 此优先级需全局显示
         * 1.版本更新弹框(全局)
         * 2.热更新成功(全局,Android端)
         * 3.公告弹框(全局)
         */
        ONE(1),

        /**
         * 此优先优先级为页面专属优先级,除1级外,与3级优先级关系互斥展示关系
         * 1.红包雨页面(全局)
         * 2.新手引导页面(首页)
         */
        TWO(2),

        /**
         * 此优先级为普通弹框,包含全局弹框及固定页面弹框,除1级2外,互斥队列显示
         */
        THREE(3),

        /**
         * 待续
         */
        FOUR(4),
        FIVE(5),
        ;

        public Integer value;

        LEVEL_TYPE(Integer value) {
            this.value = value;
        }
    }

    /**
     * 当前展示的Dialog
     */
    public DialogCommBase currentDialog;
    /**
     * LoadingDialog会显示与其他Dialog之上,需单独保存处理
     */
    public LoadingDialog loadingDialog;
    /**
     * 储存所有的级别
     */
    private static ArrayList<Integer> levels = new ArrayList<>();
    /**
     * 储存所有的弹框或页面名称
     */
    private static ArrayList<String> dialogs = new ArrayList<>();
    /**
     * 储存所有的弹框所需数据
     */
    private static ArrayList<Object> dialogData = new ArrayList<>();
    /**
     * 延时check时间
     */
    public static long delayMillis = 0;
    /**
     * 发布延时时间
     */
    public static final long RELEASE_DELAY_MILLIS = 2000;

    /**
     * 初始清空
     */
    public void initAllDialog() {
        levels.clear();
        dialogs.clear();
        dialogData.clear();
        delayMillis = 0;
        if (currentDialog != null) {
            currentDialog.dismiss();
            currentDialog = null;
        }
    }

    /**
     * 所有弹框加入队列(包括activity类型),主线程添加
     *
     * @param level  页面等级
     * @param dialog 页面名称
     * @param obj    页面所需实体
     */
    public void putDialog(Integer level, String dialog, Object obj) {
        LogUtils.d("putDialog_" + dialog + "__" + obj.toString());
        if (loadingDialog != null && loadingDialog.isShowing()) {//loading中可以被其他弹框关闭
            if (!LoadingDialog.equals(dialog)) {
                loadingDialog.dismiss();
            } else {
                return;
            }
        }
        if (checkDialogOnlyOne(dialog)) {
            //已存在弹框没有在显示,延时显示
            if (currentDialog == null || !currentDialog.isShowing()) {
                loadDelayed(200);
            }
            return;
        }
        levels.add(level);
        dialogs.add(dialog);
        dialogData.add(obj);
        checkDialog();
    }


    /**
     * 校验弹框队列catch捕获
     */
    public void checkDialog() {
        try {
            if (delayMillis > 0) {
                loadDelayed(delayMillis);
            } else {
                checkCatchDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 实际弹框队列检验
     */
    private void checkCatchDialog() {
        if (CommonUtils.ListNotNull(levels)
                && CommonUtils.ListNotNull(dialogs)
                && CommonUtils.ListNotNull(dialogData)
                && levels.size() == dialogs.size()
                && dialogs.size() == dialogData.size()) {
            if (levels.contains(LEVEL_TYPE.ONE.value)) {//是否有1级弹框需要显示
                if (currentDialog != null && currentDialog.isShowing()) {//已有弹框展示
                    if (currentDialog.level == LEVEL_TYPE.ONE.value) {//已显示1级,不显示新弹框
                        Activity activity = currentDialog.getActivity();
                        if (activity != null && !activity.isDestroyed()
                                && !activity.isFinishing()) {
                            return;
                        }
                    }
                    currentDialog.setOnDismissListener(null);//先关闭当前其他优先级弹框
                    currentDialog.dismiss();
                }
                //展示弹框
                int i = levels.indexOf(LEVEL_TYPE.ONE.value);
                ActivityBase currentActivity1 = getCurrentActivity();
                if (currentActivity1 != null && !(currentActivity1 instanceof StartActivity)) {//启动页不弹框
                    showCurrentDialog(dialogs.remove(i), levels.remove(i), dialogData.remove(i), currentActivity1);
                } else {
                    loadDelayed();
                }
            } else {//无1级需要弹框
                if (currentDialog != null) {
                    Context myContext = currentDialog.getMyContext();
                    if (myContext instanceof Activity
                            && !((Activity) myContext).isDestroyed()
                            && !((Activity) myContext).isFinishing()) {
                        //已有弹框展示(无论是几级)
                        if (!LoadingDialog.equals(dialogs.get(0))) {//已有弹框展示(无论是几级)
                            if (currentDialog.level != LEVEL_TYPE.ONE.value) {
                                currentDialog = null;
                            }
                            loadDelayed();
                            return;
                        }//加载框可以直接显示
                    } else {
                        if (currentDialog.level != LEVEL_TYPE.ONE.value) {
                            currentDialog = null;
                        }
                        loadDelayed();
                        return;
                    }
                }

                /**
                 * 页面校验 有新手引导等activity类型弹框页面不显示非1级弹框
                 */
                ActivityBase currentActivity = getCurrentActivity();
                int index = 0;
                //展示弹框
                if (currentActivity != null && !(currentActivity instanceof StartActivity)) {//启动页不弹框
                    showCurrentDialog(dialogs.remove(index), levels.remove(index), dialogData.remove(index), currentActivity);
                } else {
                    loadDelayed();
                }
            }
        }
    }


    /**
     * 实际展示页面逻辑,根据弹框或页面名称展示页面或弹框
     *
     * @param dialogCommBase  页面名称
     * @param level           等级
     * @param obj             所需要的数据
     * @param currentActivity 当前页面
     */
    private void showCurrentDialog(String dialogCommBase, int level, Object obj, ActivityBase currentActivity) {
        if (CommonUtils.StringNotNull(dialogCommBase) && obj != null && level > 0) {
            //再次检验
            if (currentActivity == null
                    || currentActivity.isFinishing()
                    || currentActivity.isDestroyed()) {
                //未达到显示条件,设置到队列等待check
                resetData(dialogCommBase, level, obj);
                //延时check
                loadDelayed();
                return;
            }
            if (dialogCommBase.equals(UpdateDialog)) {//更新弹框(全局)
                if (currentActivity instanceof StartActivity) {//启动页不展示,启动广告延迟展示
                    //未达到显示条件,设置到队列等待check
                    resetData(dialogCommBase, level, obj);
                    //延时check
                    loadDelayed();
                    return;
                }
                AppUpdateUtils updateUtils = new AppUpdateUtils(currentActivity);
                try {
                    currentDialog = updateUtils.startUpdate((UpdateDate) obj);
                    if (currentDialog != null) {
                        currentDialog.addOnDismissListener(this);
                        currentDialog.setLevel(level);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (dialogCommBase.equals(GameUrlStatusDialog)) {//平台维护状态
                if (currentActivity instanceof ActivityIntentInterface) {
                    currentDialog = new StatusDialog(currentActivity);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                    ((StatusDialog) currentDialog).showDialog((StatusBean) obj);
                }
            } else if (dialogCommBase.equals(CommonDialog)) {//通用弹框
                if (currentActivity instanceof ActivityIntentInterface) {
                    currentDialog = new CommonDialog((ActivityIntentInterface) currentActivity)
                            .show((CommDialogData) obj);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                } else {
                    //未达到显示条件,设置到队列等待check
                    resetData(dialogCommBase, level, obj);
                    //延时check
                    loadDelayed();
                }
            } else if (dialogCommBase.equals(LoadingDialog)) {//加载中弹框
                if (obj instanceof LoadingDialogData) {
                    LoadingDialogData viewData = (LoadingDialogData) obj;
                    if (viewData.context != null && viewData.context.equals(currentActivity)) {
                        loadingDialog = new LoadingDialog(currentActivity);
                        loadingDialog.setCanceledOnTouchOutside(viewData.cancel);
                        loadingDialog.addOnDismissListener(this);
                        loadingDialog.setLevel(level);
                        loadingDialog.showDialog(viewData.msg, viewData.isDelayedLoading, viewData.callbackBase);
                    }
                }
            } else if (dialogCommBase.equals(VerifyDialog)) {//立即认证
                if (currentActivity instanceof WalletManageActivity) {
                    currentDialog = new VerifyDialog(currentActivity).show((AuthenDialogData) obj);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                }
            } else if (dialogCommBase.equals(AddPayTypeDialog)) {//立即添加收付款信息
                if (currentActivity instanceof SellCoinCenterActivity || currentActivity instanceof BuyCoinCenterActivity) {
                    currentDialog = new AddPayTypeDialog(currentActivity).show((AuthenDialogData) obj);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                }
            } else if (dialogCommBase.equals(OrderNoticeDialog)) {//订单提醒
                currentDialog = new OrderNoticeDialog((ActivityIntentInterface) currentActivity).show((OrderNoticeDialogData) obj);
                currentDialog.addOnDismissListener(this);
                currentDialog.setLevel(level);
            } else if (dialogCommBase.equals(CoinConfirmPauseDialog)) {//立即添加收付款信息
                if (currentActivity instanceof OrderDetailsActivity) {
                    currentDialog = new CoinConfirmPauseDialog(currentActivity).show((AuthenDialogData) obj);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                }
            } else if (dialogCommBase.equals(CoinCancelDialog)) {//取消订单/取消（订单详情）
                if (currentActivity instanceof OrderDetailsActivity) {
                    currentDialog = new CoinCancelDialog(currentActivity).show((AuthenDialogData) obj);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                }
            } else if (dialogCommBase.equals(AddShAccountDialog)) {//商户存款（添加商户/玩家账号）
                if (currentActivity instanceof DepositShActivity) {
                    currentDialog = new AddShAccountDialog(currentActivity).show((ShAccountDialogData) obj);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                }
            } else if (dialogCommBase.equals(InputPassWordDialog)) {//输入支付密码
                if (currentActivity instanceof AddWalletManageActivity ||
                        currentActivity instanceof SellCoinDetailsActivity ||
                        currentActivity instanceof DepositShActivity ||
                        currentActivity instanceof OrderDetailsActivity) {
                    currentDialog = new InputPassWordDialog(currentActivity).show((PayPassWordDialogData) obj);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                }
            } else if (dialogCommBase.equals(AddWjAccountDialog)) {//添加玩家账号
                if (currentActivity instanceof ManageShSettingActivity) {
                    currentDialog = new AddWjAccountDialog((ActivityIntentInterface) currentActivity).show((AddWjAccountDialogData) obj);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                }
            } else if (dialogCommBase.equals(MsgNoticeDialog)) {//(公告栏)公告弹框(首页:点击弹出)
                if (currentActivity instanceof MainActivity) {
                    currentDialog = new MsgNoticeDialog((ActivityIntentInterface) currentActivity)
                            .setData((MsgNoticeData) obj);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                }
            } else if (dialogCommBase.equals(RegistrNoticeDialog)) {//注册须知
                if (currentActivity instanceof RegisterActivity) {
                    currentDialog = new RegistrNoticeDialog((ActivityIntentInterface) currentActivity)
                            .setData((DialogInterface.OnDismissListener) obj);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                }
            } else if (dialogCommBase.equals(CoinNoticeDialog)) {//卖家须知/买家须知
                if (currentActivity instanceof SellCoinCenterActivity || currentActivity instanceof BuyCoinCenterActivity) {
                    currentDialog = new CoinNoticeDialog((ActivityIntentInterface) currentActivity)
                            .setData((CoinNoticeBean) obj);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                }
            } else if (dialogCommBase.equals(NoticeDialog)) {//公告弹框(重要公告)(首页)
                boolean needDelayed = false;
                if (currentActivity instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) currentActivity;
                    if (mainActivity.position == MainActivity.TAB_HOME) {
                        currentDialog = new NoticeDialog((ActivityIntentInterface) currentActivity)
                                .setData((NoticeDialogData) obj);
                        currentDialog.addOnDismissListener(this);
                        currentDialog.setLevel(level);
                    } else {
                        needDelayed = true;
                    }
                } else {
                    needDelayed = true;
                }
                if (needDelayed) {
                    //未达到显示条件,设置到队列等待check
                    resetData(dialogCommBase, level, obj);
                    //延时check
                    loadDelayed();
                }
            } else if (dialogCommBase.equals(ToMoreUrlDialog)) {//公告弹框(重要公告)(首页)
                if (currentActivity instanceof ManageShActivity) {
                    currentDialog = new ToMoreUrlDialog((ActivityIntentInterface) currentActivity)
                            .setData((ToMoreDialogData) obj);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                }
            } else if (dialogCommBase.equals(SliderValidationDialog)) {//滑块验证
                if (currentActivity instanceof LoginActivity || currentActivity instanceof RegisterActivity || currentActivity instanceof FindPasswordActivity) {
                    currentDialog = new SliderValidationDialog((ActivityIntentInterface) currentActivity)
                            .showDialog((SliderValidationDialogData) obj);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                }
            } else if (dialogCommBase.equals(SelectBankDialog)) {//选择银行(存款/绑定银行页面)  // 反馈类型
                if (currentActivity instanceof AddWalletManageActivity || currentActivity instanceof FeedbackActivity) {
                    currentDialog = new SelectBankDialog((ActivityIntentInterface) currentActivity);
                    currentDialog.addOnDismissListener(this);
                    currentDialog.setLevel(level);
                    ((SelectBankDialog) currentDialog).showDialog((SelectBankDialogData) obj);
                }
            } else if (dialogCommBase.equals(SimpleDialog)) {//底部弹框简单选择框
                currentDialog = new SimpleDialog(currentActivity);
                currentDialog.addOnDismissListener(this);
                currentDialog.setLevel(level);
                ((SimpleDialog) currentDialog).showDialog((SimpleDialogData) obj);
            }
        }
    }

    /**
     * 获取当前显示的页面并且未销毁未关闭
     *
     * @return 当前最上层页面
     */
    public ActivityBase getCurrentActivity() {
        ActivityBase currentActivity = MyActivityManager.getActivityManager().getCurrentActivity();
        if (currentActivity != null
                && !currentActivity.isDestroyed()
                && !currentActivity.isFinishing()) {
            return currentActivity;
        }
        return null;
    }

    /**
     * 获取页面即将显示时,页面关闭等原因,延时获取最上层页面,重新显示
     */
    private void loadDelayed() {
        new Handler().postDelayed(this::checkDialog, 1000);
    }

    /**
     * 获取页面即将显示时,页面关闭等原因,延时获取最上层页面,重新显示
     */
    public void loadDelayed(long l) {
        new Handler().postDelayed(this::checkDialog, l);
    }


    /**
     * 重新设置数据等待下次显示时机,设置位置为集合最后
     *
     * @param dialogCommBase
     * @param level
     * @param obj
     */
    private void resetData(String dialogCommBase, int level, Object obj) {
        dialogs.add(dialogCommBase);
        levels.add(level);
        dialogData.add(obj);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if ((dialog instanceof LoadingDialog)) {

        } else {
            if (currentDialog != null) {
                currentDialog = null;
            }
        }

        checkDialog();
    }


    private MyDialogManager() {
    }

    public static MyDialogManager getManager() {
        return MyDialogManager.MyDialogManagerHolder.INSTANCE;
    }

    private static class MyDialogManagerHolder {
        private static final MyDialogManager INSTANCE = new MyDialogManager();
    }

//    /**
//     * 登录成功后需要关闭的弹框
//     */
//    public void checkLoginSuccessDismiss() {
//        if (currentDialog != null && currentDialog.isShowing()) {
//            if (currentDialog instanceof RedNotLoginDialog) {//红包雨未登录
//                currentDialog.dismiss();
//            }
//        }
//    }
//
//    /**
//     * 任务完成弹框查看更多奖励,清除剩下未弹出的任务奖励弹框
//     */
//    public void removeTaskSuccess() {
//        if (CommonUtils.ListNotNull(dialogs)
//                && dialogs.contains(TaskSuccessDialog)) {
//            for (int i = 0; i < dialogs.size(); i++) {
//                String s = dialogs.get(i);
//                if (TaskSuccessDialog.equals(s)) {
//                    dialogs.remove(i);
//                    dialogData.remove(i);
//                    levels.remove(i);
//                    i--;
//                }
//            }
//        }
//    }
//
//    /**
//     * 红包雨结束,清除队列红包雨
//     */
//    public void cleanRedEnvelopeRain() {
//        if (CommonUtils.ListNotNull(dialogs)
//                && dialogs.contains(RedEnvelopeRainActivity)) {
//            int i = dialogs.indexOf(RedEnvelopeRainActivity);
//            if (i >= 0) {
//                dialogs.remove(i);
//                dialogData.remove(i);
//                levels.remove(i);
//            }
//        }
//    }
//
//    /**
//     * 开奖结束,清除队列开奖
//     */
//    public void cleanLotteryRemind() {
//        if (CommonUtils.ListNotNull(dialogs)
//                && dialogs.contains(LotteryRemindDialog)) {
//            int i = dialogs.indexOf(LotteryRemindDialog);
//            if (i >= 0) {
//                dialogs.remove(i);
//                dialogData.remove(i);
//                levels.remove(i);
//            }
//        }
//    }


    /**
     * 校验弹框,同类型是否可以存在多个
     *
     * @param dialog 需要添加的弹框
     * @return true: 不能添加  false:可以添加
     */
    private boolean checkDialogOnlyOne(String dialog) {
        boolean contains = dialogs.contains(dialog);
        LogUtils.d("putDialog_" + dialog + "__" + contains);
        return contains;
    }


    /**
     * 当前显示Dialog名称
     *
     * @return
     */
    private String getCurrentDialogName() {
        return currentDialog != null && currentDialog.isShowing() ? currentDialog.getClass().getSimpleName() : "";
    }

    /**
     * 获取当前显示的通用弹框
     * 热更新成功/app异常(配置文件加载异常等)重启提示(全局)
     *
     * @return
     */
    public CommonDialog getCommonDialog() {
        if (currentDialog != null && currentDialog.isShowing() && currentDialog instanceof CommonDialog) {
            return ((CommonDialog) currentDialog);
        }
        return null;
    }

    /**
     * 获取更新弹框
     *
     * @return
     */
    public UpdateDialog getUpdateDialog() {
        if (currentDialog != null && currentDialog.isShowing() && currentDialog instanceof UpdateDialog) {
            return ((UpdateDialog) currentDialog);
        }
        return null;
    }

    /**
     * 关闭原生滑动验证
     *
     * @return
     */
    public void disMissSliderValidationDialog() {
        if (currentDialog != null && currentDialog.isShowing() && currentDialog instanceof SliderValidationDialog) {
            currentDialog.dismiss();
        }
    }

    /**
     * 订单提醒 是否可以显示
     *
     * @return
     */
    public boolean isCanShowOrderNoticeDialog() {
        if (currentDialog != null) {
            ActivityBase currentActivity = MyDialogManager.getManager().getCurrentActivity();
            if (currentActivity instanceof OrderDetailsActivity) {//判断当前页面为OrderDetailsActivity时,关闭正在显示的弹窗
                MyDialogManager.getManager().checkDestroy(currentActivity);
            }
            if (currentDialog instanceof OrderNoticeDialog && currentDialog.isShowing()) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * 交易公告
     *
     * @return
     */
    public void disMissCoinNoticeDialog() {
        if (currentDialog != null && currentDialog.isShowing() && currentDialog instanceof CoinNoticeDialog) {
            currentDialog.dismiss();
        }
    }

    /**
     * 是否有Dialog 包含正在显示及等待显示的
     *
     * @param dialog
     * @return
     */
    public boolean hasDialog(Class dialog) {
        if (currentDialog != null) {
            return currentDialog.getClass().getSimpleName().equals(dialog.getSimpleName());
        } else {
            return CommonUtils.ListNotNull(dialogs) && dialogs.contains(dialog.getSimpleName());
        }
    }

    /**
     * 页面关闭Dialog显示检验并关闭
     */
    public void checkDestroy(Activity currentActivity) {
        if (currentDialog != null && currentDialog.isShowing()) {
            Context myContext = currentDialog.getMyContext();
            if (currentActivity != null && currentActivity.equals(myContext)) {
                if (currentDialog != null && currentDialog.isShowing()) {
                    currentDialog.dismiss();
                }
            }
        }
    }

}
