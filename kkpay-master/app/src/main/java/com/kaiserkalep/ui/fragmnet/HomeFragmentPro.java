package com.kaiserkalep.ui.fragmnet;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.MySellAdapter;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.IntentUtils;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.AboutBean;
import com.kaiserkalep.bean.AmountInfo;
import com.kaiserkalep.bean.MySellBean;
import com.kaiserkalep.bean.NoticeDialogData;
import com.kaiserkalep.bean.UnreadNum;
import com.kaiserkalep.bean.UserInfoBean;
import com.kaiserkalep.bean.WalletManageBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.eventbus.MainActivityIndexEvent;
import com.kaiserkalep.eventbus.MainDoubleClickIndexEvent;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.net.impl.SysImpl;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.net.impl.WebsiteImpl;
import com.kaiserkalep.net.url.HomeUrl;
import com.kaiserkalep.ui.activity.MainActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.utils.skinlibrary.utils.SkinResourcesUtils;
import com.kaiserkalep.widgets.BoldTextView;
import com.kaiserkalep.widgets.MySmartRefreshLayout;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import q.rorbin.badgeview.QBadgeView;

import static com.kaiserkalep.constants.Config.EGATIVE_ONE;
import static com.kaiserkalep.constants.Config.FOUR_STRING;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.THREE_STRING;
import static com.kaiserkalep.constants.Config.TWO_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.StringConstants.CREATETIME;

/**
 * @Auther: Jack
 * @Date: 2020/8/11 17:26
 * @Description:
 */
public class HomeFragmentPro extends PageBaseFragment implements SucceedCallBackListener<MySellBean.RowsDTO> {

    @BindView(R.id.sl_user_content)
    ShadowLayout slUserContent;
    @BindView(R.id.tv_statusbar)
    ImageView tvStatusbar;

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;
    @BindView(R.id.ll_home_top_msg_view)
    LinearLayout llHomeTopMsgView;
    @BindView(R.id.rl_help_parent)
    RelativeLayout rlHelpParent;
    @BindView(R.id.iv_user_photo)
    ImageView ivUserPhoto;
    @BindView(R.id.tv_userid)
    BoldTextView tvUserId;
    @BindView(R.id.tv_userid_verfiy)
    BoldTextView tvUserIdVerfiy;
    @BindView(R.id.iv_level)
    ImageView ivLevel;
    @BindView(R.id.tv_money)
    BoldTextView tvMoney;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.iv_copy_icon)
    ImageView ivCopyIcon;
    @BindView(R.id.tv_sale)
    TextView tvSale;
    @BindView(R.id.tv_not_sale)
    TextView tvNotSale;
    @BindView(R.id.tv_sell)
    TextView tvSell;
    @BindView(R.id.tv_trade)
    TextView tvTrade;
    @BindView(R.id.rec_shadow_nodata)
    ShadowLayout recShadow;
    @BindView(R.id.rec_guad)
    RecyclerView recGuad;
    @BindView(R.id.ll_rec_lookall)
    LinearLayout llRecLookall;
    private boolean isCanUserInfo = false;
    private boolean isCanAmountInfo = false;
    private boolean isCanMySell = false;
    private boolean isCanMsgNum = false;
    private boolean isCanBankList = false;
    private QBadgeView newMessage;
    private List<MySellBean.RowsDTO> mySellList = new ArrayList<>();
    private MySellAdapter mySellAdapter;
    private String identityStatus;//0未申请，1实名认证申请，2实名认证驳回，3实名认证通过
    private boolean isShowHelpView = false;

    @Override
    public int getViewId() {
        return R.layout.fragment_homepro;
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        int statusBarHeight = UIUtils.getStatusBarHeight(MyApp.getContext());
        UIUtils.setMargins2(slUserContent, 0, UIUtils.dip2px(50) + statusBarHeight, 0, 0);
        UIUtils.setHeight(tvStatusbar, statusBarHeight);
        refreshLayout.setEnablePureScrollMode(false);
        refreshLayout.setEnableOverScrollDrag(false);//是否启用越界拖动（仿苹果效果）1.0.4
        refreshLayout.setOnRefreshListener(refreshLayout -> refresh(false, true));
        rlHelpParent.setVisibility(isShowHelpView ? View.VISIBLE : View.GONE);

        initRecyclerView();
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        refresh(true, false);
        getNoticeAndNotifyData();
    }

    @Override
    public void onUserDoubleVisible() {
        super.onUserDoubleVisible();
        refresh(false, false);
    }

    private void initRecyclerView() {
        if (null == mySellAdapter) {
            recGuad.setLayoutManager(new LinearLayoutManager(getContext()));
            mySellAdapter = new MySellAdapter(getContext(), mySellList, this);
            recGuad.setAdapter(mySellAdapter);
        }
    }

    @Override
    public void succeedCallBack(@Nullable MySellBean.RowsDTO o) {
        if (null != o) {
            HashMap<String, String> params = new HashMap<>();
            params.put(StringConstants.TITLE, MyApp.getLanguageString(getContext(), R.string.sell_finish_details_money_one_tip));
            params.put(StringConstants.TITLE_SECOND, MySellBean.getDealOrCancelTitle(getContext(), o.getStatus()));
            params.put(StringConstants.ORDERNO, o.getOrderNo());
            startClass(R.string.SellCoinDetailsActivity, params);
        }
    }

    public void setHelpTutorial() {
        isShowHelpView = true;
    }

    public void setHideHelpTutorial() {
        if (null != rlHelpParent) {
            rlHelpParent.setVisibility(View.GONE);
            isShowHelpView = false;
        }
    }

    /**
     * @param needDialog 是否需要加载框
     * @param isManual   是否手动刷新
     */
    public void refresh(boolean needDialog, boolean isManual) {
        if (!isCanUserInfo || isManual) {
            getUserInfo(needDialog);
        }
        if (!isCanAmountInfo || isManual) {
            getAmountInfo(needDialog);
        }
        if (!isCanMySell || isManual) {
            getMySell(needDialog);
        } else {
            if (null != refreshLayout)
                refreshLayout.finishRefresh();
        }
        if (!isCanMsgNum || isManual) {
            getMsgNum();
        }
        if (!isCanBankList || isManual) {
            geBankListInfo();
        }
    }

    /**
     * 获取个人信息
     *
     * @param showDialog
     */
    private void getUserInfo(boolean showDialog) {
        isCanUserInfo = true;
        new UserImpl(new ZZNetCallBack<UserInfoBean>(this, UserInfoBean.class) {
            @Override
            public void onSuccess(UserInfoBean response) {
                if (null != response) {
                    identityStatus = response.getIdentityStatus();
                    tvUserId.setText(response.getUsername());
                    tvUserIdVerfiy.setVisibility(Config.THREE_STRING.equals(identityStatus) ? View.GONE : View.VISIBLE);
                    ivLevel.setBackgroundResource(Config.THREE_STRING.equals(identityStatus) ? R.drawable.icon_user_logo : R.drawable.icon_user_logo_noverfiy);
                    tvMoney.setText(Config.subOneAndDot(response.getAmount()));
                    tvAddress.setText(response.getWalletAddress());
                    GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(response.getAvatar()), ivUserPhoto, R.drawable.icon_default_photo);
                    SPUtil.setUserInfo(response);
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                isCanUserInfo = false;
            }
        }.setNeedDialog(showDialog)).getUserInfo();
    }

    /**
     * 获取余额信息
     *
     * @param showDialog
     */
    private void getAmountInfo(boolean showDialog) {
        isCanAmountInfo = true;
        new UserImpl(new ZZNetCallBack<AmountInfo>(this, AmountInfo.class) {
            @Override
            public void onSuccess(AmountInfo response) {
                if (null != response) {
                    tvSale.setText(Config.subOneAndDot(response.getCanSellAmount()));
                    tvNotSale.setText(Config.subOneAndDot(response.getBuyAmount()));
                    tvSell.setText(Config.subOneAndDot(response.getSellAmount()));
                    tvTrade.setText(Config.subOneAndDot(response.getSellingAmount()));
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                isCanAmountInfo = false;
            }
        }.setNeedDialog(showDialog)).getAmountInfo();
    }

    /**
     * 我的挂单
     */
    public void getMySell(boolean showDialog) {
        isCanMySell = true;
        new FundImpl(new ZZNetCallBack<MySellBean>(this, MySellBean.class) {
            @Override
            public void onSuccess(MySellBean response) {
                if (mySellList.size() > ZERO) {
                    mySellList.clear();
                }
                if (null != response && null != response.getRows() && response.getRows().size() >= Config.ONE) {
                    recShadow.setVisibility(View.GONE);
                    recGuad.setVisibility(View.VISIBLE);
                    List<MySellBean.RowsDTO> rows = response.getRows();
                    if (rows.size() > Config.FIVE) {
                        mySellList.addAll(rows.subList(ZERO, Config.FIVE));
                        llRecLookall.setVisibility(View.VISIBLE);
                    } else {
                        mySellList = rows;
                        llRecLookall.setVisibility(View.INVISIBLE);
                    }
                    for (MySellBean.RowsDTO r : response.getRows()) {
                        if (Config.ZERO_STRING.equals(r.getStatus())
                                || Config.ONE_STRING.equals(r.getStatus())
                        ) {
                            MyApp.pendingOrderMap.put(r.getOrderNo(), r);
                        } else {
                            MyApp.pendingOrderMap.remove(r.getOrderNo());
                            MyApp.getApplication().checkOrder();
                        }
                    }
                } else {
                    recShadow.setVisibility(View.VISIBLE);
                    recGuad.setVisibility(View.GONE);
                    llRecLookall.setVisibility(View.INVISIBLE);
                    MyApp.pendingOrderMap.clear();
                    MyApp.getApplication().checkOrder();
                }
                if (mySellAdapter != null) {
                    mySellAdapter.changeDataUi(mySellList);
                }
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                if (mySellList.size() > ZERO) {
                    mySellList.clear();
                }
                if (null != recShadow) {
                    recShadow.setVisibility(View.VISIBLE);
                }
                if (null != recGuad) {
                    recGuad.setVisibility(View.GONE);
                }
                if (null != llRecLookall) {
                    llRecLookall.setVisibility(View.INVISIBLE);
                }
                if (mySellAdapter != null) {
                    mySellAdapter.changeDataUi(mySellList);
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                isCanMySell = false;
                if (null != refreshLayout) {
                    refreshLayout.finishRefresh();
                }
            }
        }.setNeedDialog(showDialog)).mySell(CREATETIME, StringConstants.DESC, Config.TEN_STRING, ONE_STRING, ONE_STRING);
    }

    /**
     * 获取未读消息
     */
    public void getMsgNum() {
        isCanMsgNum = true;
        new SysImpl(new ZZNetCallBack<UnreadNum>(this, UnreadNum.class) {
            @Override
            public void onSuccess(UnreadNum response) {
                if (response != null) {
                    int unread = response.getUnread();
                    setUnReadMsg(llHomeTopMsgView, unread);
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                isCanMsgNum = false;
            }
        }.setNeedDialog(false)).sysNoticeUnreadNum();
    }


    /**
     * 设置显示未读消息
     *
     * @param unread
     */
    private void setUnReadMsg(View view, int unread) {
        if (newMessage == null) {
            newMessage = new QBadgeView(getContext());
            newMessage.setBadgeBackgroundColor(SkinResourcesUtils.getColor(R.color.unread_message_dot));
            newMessage.setBadgePadding(3, true);
            newMessage.setBadgeTextSize(4, true);
            newMessage.setBadgeGravity(Gravity.END | Gravity.TOP);
            if (view != null) {
                newMessage.bindTarget(view);
            }
        }
        if (unread > ZERO) {
            newMessage.setBadgeNumber(EGATIVE_ONE);
        } else {
            newMessage.hide(true);
        }
    }

    /**
     * 获取公告信息,有更新弹框需要展示时
     */
    private void getNoticeAndNotifyData() {
        if (CommonUtils.needShowNotice()) {
            getNoticeData();
        }
    }

    /**
     * 获取公告
     */
    private void getNoticeData() {
        new SysImpl(new ZZNetCallBack<List<NoticeDialogData.DialogList>>(this, NoticeDialogData.DialogList.class) {
            @Override
            public void onSuccess(List<NoticeDialogData.DialogList> response) {
                if (null != response) {
                    setNoticeDialog(response);
                }
            }
        }.setNeedDialog(false)).getAlertNoticeList();
    }

    /**
     * 校验显示时机
     *
     * @param response
     */
    private void setNoticeDialog(List<NoticeDialogData.DialogList> response) {
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value,
                MyDialogManager.NoticeDialog, new NoticeDialogData(response));
    }

    private void geBankListInfo() {
        new UserImpl(new ZZNetCallBack<List<WalletManageBean>>(this, WalletManageBean.class) {
            @Override
            public void onSuccess(List<WalletManageBean> response) {
                if (response != null) {
                    isCanBankList = true;
                    String bankListString = new Gson().toJson(response);
                    SPUtil.setStringValue(SPUtil.PAYMENT_MODE_LIST, bankListString);
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }
        }.setNeedDialog(false)).getPayModeList();
    }


    /**
     * 设置友盟开始页面统计
     */
    public void setUmengStartPageStatistics() {
        onPageStart(MyApp.getLanguageString(getContext(), R.string.main_tab_home));
    }

    /**
     * 设置友盟停止页面统计
     */
    public void setUmengEndPageStatistics() {
        onPageEnd(MyApp.getLanguageString(getContext(), R.string.main_tab_home));
    }

    /**
     * 点击tab刷新
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MainDoubleClickIndexEvent event) {
        if (event != null && (event.currentIndex == MainActivity.TAB_HOME)) {
            if (refreshLayout != null) {
                refreshLayout.autoRefresh();
            }
        }
    }

    @OnClick({R.id.ll_home_top_msg_view, R.id.ll_home_top_service_view, R.id.ll_home_top_help_view, R.id.tv_help_tutoria, R.id.rl_help_parent, R.id.ll_user_name, R.id.iv_copy_icon, R.id.btn_buy, R.id.btn_sell, R.id.ll_sh_deposit,
            R.id.ll_transaction_record_jl, R.id.ll_transaction_record_manage, R.id.ll_sh_manage, R.id.ll_rec_lookall})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_home_top_msg_view:
                checkLogin(R.string.MessageNotifyActivity);
                break;
            case R.id.ll_home_top_service_view:
                checkLogin(R.string.ServiceActivity);
                break;
            case R.id.ll_home_top_help_view:
            case R.id.tv_help_tutoria:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                setHideHelpTutorial();
                String helpUrl = SPUtil.getHelpUrl();
                if (CommonUtils.StringNotNull(helpUrl)) {
                    goToHelpWebview(helpUrl);
                } else {
                    getAbout();
                }
                break;
            case R.id.rl_help_parent:
                setHideHelpTutorial();
                break;
            case R.id.ll_user_name:
                checkLogin(R.string.VerifyFaceActivity);
                break;
            case R.id.iv_copy_icon:
                if (null != tvAddress) {
                    String string = tvAddress.getText().toString();
                    CommonUtils.copyText(getContext(), string);
                }
                break;
            case R.id.btn_buy:
                checkLogin(R.string.BuyCoinCenterActivity);
                break;
            case R.id.btn_sell:
                checkLogin(R.string.SellCoinCenterActivity);
                break;
            case R.id.ll_sh_deposit:
                checkLogin(R.string.DepositShactivity);
                break;
            case R.id.ll_transaction_record_jl:
                checkLogin(R.string.WalletRecordActivity);
                break;
            case R.id.ll_transaction_record_manage:
                checkLogin(R.string.WalletManageActivity);
                break;
            case R.id.ll_sh_manage:
                checkLogin(R.string.ManageShactivity);
                break;
            case R.id.ll_rec_lookall:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                EventBusUtil.post(new MainActivityIndexEvent(ONE, ZERO));
                break;
        }
    }

    /**
     * 获取用户须知 (没有帮助Url时，重新请求获取)
     */
    private void getAbout() {
        new WebsiteImpl(new ZZNetCallBack<List<AboutBean>>(this, AboutBean.class) {
            @Override
            public void onSuccess(List<AboutBean> response) {
                if (null != response) {
                    String helpUrl = "";
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
                                helpUrl = content;
                            }
                        }
                    }
                    goToHelpWebview(helpUrl);
                }
            }
        }.setNeedDialog(true).setNeedToast(true)).getAbout();
    }

    /**
     * 跳转帮助页面
     *
     * @param url
     */
    private void goToHelpWebview(String url) {
        if (CommonUtils.StringNotNull(url)) {
            if (url.contains("?")) {
                url = url + "&nav=1";
            } else {
                url = url + "?nav=1";
            }
            startClass(MyApp.getMyString(R.string.WebViewActivity), IntentUtils.getHashObj(new String[]{
                    StringConstants.TITLE, MyApp.getLanguageString(getContext(), R.string.help_center_title),
                    StringConstants.URL, url}));
        } else {
            toast(MyApp.getLanguageString(getContext(), R.string.Share_Error));
        }
    }
}
