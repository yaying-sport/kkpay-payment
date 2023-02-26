package com.kaiserkalep.ui.activity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.Gson;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.AuthenDialogData;
import com.kaiserkalep.bean.BuyListBean;
import com.kaiserkalep.bean.CoinNoticeBean;
import com.kaiserkalep.bean.MySellBean;
import com.kaiserkalep.bean.OrderFromBean;
import com.kaiserkalep.bean.PayTypeBean;
import com.kaiserkalep.bean.WalletManageBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.present.BuyCoinListRefreshManager;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.MyRecycleView;
import com.kaiserkalep.widgets.MySmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 买币中心
 */
public class BuyCoinCenterActivity extends ZZActivity implements View.OnClickListener, SucceedCallBackListener<BuyListBean.RowsDTO> {

    @BindView(R.id.recyclerView)
    MyRecycleView recyclerView;
    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;
    @BindView(R.id.loading)
    LoadingLayout mLoading;

    @BindView(R.id.iv_top_right)
    ImageView ivTopRight;
    @BindView(R.id.iv_top_left)
    ImageView ivTopLeft;

    private String title = "";
    private boolean startView = true;
    private List<BuyListBean.RowsDTO> list = new ArrayList<>();
    private BuyListAdapter adapter;
    private boolean isLoading = false;
    private String orderByColumn = StringConstants.CREATETIME;
    private String isAsc = StringConstants.DESC;

    @Override
    public int getViewId() {
        return R.layout.activity_buycoincenter;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (startView) {
            startView = false;
            startCallbackNet(true, true, true);
        } else {
            startCallbackNet(true, true, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onPageStart(title);
    }

    @Override
    public void onPause() {
        super.onPause();
        onPageEnd(title);
    }

    @Override
    protected void onStop() {
        super.onStop();
        startCallbackNet(false, false, false);
    }

    @Override
    public void afterViews() {
        title = MyApp.getLanguageString(getContext(), R.string.buy_center_title);
        commTitle.init(title);
        mLoading.setEmptyImage(R.drawable.no_data);
        mLoading.setEmptyText(MyApp.getLanguageString(getContext(), R.string.no_content));
        ivTopRight.setTag(true);
        ivTopLeft.setTag(true);
        initRecycleView();
        refreshLayout.setOnRefreshListener(refreshLayout -> refresh(true, false, false));
        refreshLayout.setOnLoadMoreListener(refreshLayout -> refresh(false, true, false));
    }

    private void initRecycleView() {
        SimpleItemAnimator simpleitemanimator = ((SimpleItemAnimator) recyclerView.getItemAnimator());
        if (null != simpleitemanimator) {
            simpleitemanimator.setSupportsChangeAnimations(false);//单条刷新无动画
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BuyListAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void succeedCallBack(@Nullable BuyListBean.RowsDTO o) {
        if (null != o) {
            String userId = SPUtil.getUserId();
            if (userId.equals(o.getMemberId())) {
                toast(MyApp.getLanguageString(this, R.string.Not_buy_me_order_tip));
            } else {
                if (checkBindWallet()) {
//                    boolean bindPayWechat = SPUtil.getBindPayWechat();
//                    boolean bindPayAlipay = SPUtil.getBindPayAlipay();
//                    boolean bindPayBank = SPUtil.getBindPayBank();
                    boolean[] paymentEnable = SPUtil.getPaymentEnable();
                    boolean bindPayWechat = paymentEnable[0];
                    boolean bindPayAlipay = paymentEnable[1];
                    boolean bindPayBank = paymentEnable[2];
                    String receiveType = o.getReceiveType();
                    if ((bindPayWechat && receiveType.contains(StringConstants.PAYTYPE_WECHAT)) ||
                            (bindPayAlipay && receiveType.contains(StringConstants.PAYTYPE_ALI)) ||
                            (bindPayBank && receiveType.contains(StringConstants.PAYTYPE_BANK))) {
                        getOrderNo(o.getOrderNo());
                    } else {
                        toast(MyApp.getLanguageString(this, R.string.buy_coin_correspind_paytype_tip));
                    }
                }
            }
        }
    }

    /**
     * 开始定时轮询
     *
     * @param refreshNow 是否立即回调一次
     * @param isStart    开始或停止
     * @param initialize 首页启动
     */
    public void startCallbackNet(boolean refreshNow, boolean isStart, boolean initialize) {
        BuyCoinListRefreshManager manager = BuyCoinListRefreshManager.getManager();
        if (isStart) {
            manager.startRefresh(refreshNow, o -> {
                if (!isLoading) {//不是手动加载时才允许轮询
                    if (ZERO_STRING.equals(o)) {//立即刷新
                        refresh(initialize, false, true);
                    } else {//正常轮询
                        refresh(false, false, false);
                    }
                }
            });
        } else {
            manager.stopRefresh();
        }
    }

    /**
     * 获取下注订单号/金额
     *
     * @param sellOrderNo 卖单号
     */
    private void getOrderNo(String sellOrderNo) {
        new FundImpl(new ZZNetCallBack<OrderFromBean>(this, OrderFromBean.class) {
            @Override
            public void onSuccess(OrderFromBean response) {
                if (null != response) {
                    String orderNo = response.getOrderNo();
                    if (CommonUtils.StringNotNull(sellOrderNo, orderNo)) {
                        goToBuyCoin(orderNo, sellOrderNo);
                    }
                }
            }

        }.setNeedDialog(true).setNeedToast(true)).getOrderFrom();
    }

    private void goToBuyCoin(String orderNo, String sellOrderNo) {
        String buyDialogTipTitle = MyApp.getLanguageString(BuyCoinCenterActivity.this, R.string.buy_dialog_tip_title);
        String content;
        String buyOrderNotice = SPUtil.getBuyOrderNotice();
        if (CommonUtils.StringNotNull(buyOrderNotice)) {
            content = buyOrderNotice;
        } else {
            String buyDialogTipOne = MyApp.getLanguageString(BuyCoinCenterActivity.this, R.string.buy_dialog_tip_one);
            String buyDialogTipTwo = MyApp.getLanguageString(BuyCoinCenterActivity.this, R.string.buy_dialog_tip_two);
            String buyDialogTipThr = MyApp.getLanguageString(BuyCoinCenterActivity.this, R.string.buy_dialog_tip_thr);
            String buyDialogTipFour = MyApp.getLanguageString(BuyCoinCenterActivity.this, R.string.buy_dialog_tip_four);
            content = buyDialogTipOne + "\n\n" + buyDialogTipTwo + "\n\n" + buyDialogTipThr + "\n\n" + buyDialogTipFour;
        }
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value,
                MyDialogManager.CoinNoticeDialog,
                new CoinNoticeBean(buyDialogTipTitle, content, dialog -> {
                    MyDialogManager.getManager().disMissCoinNoticeDialog();
                    HashMap<String, String> params = new HashMap<>();
                    params.put(StringConstants.ORDERNO, orderNo);
                    params.put(StringConstants.SELLORDERNO, sellOrderNo);
                    BuyCoinCenterActivity.this.startClass(R.string.BuyCoinActivity, params);
                }));
    }

    /**
     * 效验收款方式
     */
    private boolean checkBindWallet() {
//        boolean bindPayWechat = SPUtil.getBindPayWechat();
//        boolean bindPayAlipay = SPUtil.getBindPayAlipay();
//        boolean bindPayBank = SPUtil.getBindPayBank();
        boolean[] paymentEnable = SPUtil.getPaymentEnable();
        boolean bindPayWechat = paymentEnable[0];
        boolean bindPayAlipay = paymentEnable[1];
        boolean bindPayBank = paymentEnable[2];

        if (!bindPayWechat && !bindPayAlipay && !bindPayBank) {
            MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.AddPayTypeDialog,
                    new AuthenDialogData(v -> {
                        BuyCoinCenterActivity.this.startClass(MyApp.getLanguageString(getContext(), R.string.WalletManageActivity));
                        BuyCoinCenterActivity.this.finish();
                    }, v -> {
                        toast(MyApp.getLanguageString(getContext(), R.string.add_pay_dialog_title_tip));
                    }));
            return false;
        } else {
            return true;
        }
    }


    /**
     * @param isFrist 是否重置
     * @param isMroe  true上拉(加载更多), false下拉（上一页）
     */
    private void refresh(boolean isFrist, boolean isMroe, boolean needDialog) {
        isLoading = true;
        String pageSize = getPageSize(isFrist, isMroe);
        new FundImpl(new ZZNetCallBack<BuyListBean>(BuyCoinCenterActivity.this, BuyListBean.class) {
            @Override
            public void onSuccess(BuyListBean response) {
                if (response != null && CommonUtils.ListNotNull(response.getRows())) {
                    setData(isFrist, response);
                } else {
                    if (mLoading != null) {
                        refreshLayout.setDataContent(false);
                        mLoading.showEmpty();
                    }
                }
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                if (mLoading != null) {
                    refreshLayout.setDataContent(false);
                    mLoading.setEmptyText(msg);
                    mLoading.showEmpty();
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                loadDone(isFrist);
            }
        }.setNeedDialog(needDialog).setNeedToast(true)).OrderBuyList(orderByColumn, isAsc, pageSize, ONE_STRING);
    }

    /**
     * @param start 是否重置
     * @param mroe  true上拉(加载更多), false下拉（上一页）
     * @return 返回接口请求item条数
     */
    private String getPageSize(boolean start, boolean mroe) {
        String pageSize;
        if (start) {
            pageSize = Config.NORMAL_BUYCOIN_PAGE_SIZE;
        } else {
            if (mroe) {
                if (list.size() <= ZERO) {
                    pageSize = Config.NORMAL_BUYCOIN_PAGE_SIZE;
                } else {
                    int size = list.size() + Config.TWENTY;
                    if (size > Config.TWOHUNDRED) {
                        pageSize = Config.MAX_PAGE_SIZE;
                    } else {
                        pageSize = String.valueOf(size);
                    }
                }
            } else {
                int size = list.size();
                if (size <= ZERO) {
                    pageSize = Config.NORMAL_BUYCOIN_PAGE_SIZE;
                } else {
                    if (size > Config.TWOHUNDRED) {
                        pageSize = Config.MAX_PAGE_SIZE;
                    } else {
                        pageSize = String.valueOf(size);
                    }
                }
            }
        }
        return pageSize;
    }

    /**
     * 请求完成
     */
    private void loadDone(boolean isRefresh) {
        if (refreshLayout != null) {
            openRefresh(refreshLayout, mLoading);
            if (isRefresh) {
                refreshLayout.finishRefresh();
                if (refreshLayout.isFooterNoMoreData()) {
                    refreshLayout.finishLoadMoreWithNoMoreData();
                } else {
                    refreshLayout.resetNoMoreData();
                }
            } else {
                refreshLayout.finishLoadMore(300, true, refreshLayout.isFooterNoMoreData());
            }
        }
        isLoading = false;
    }

    /**
     * 设置数据
     *
     * @param frist
     * @param response 请求的数据
     */
    private void setData(boolean frist, BuyListBean response) {
        if (list.size() > ZERO) {
            list.clear();
        }
        if (response != null && CommonUtils.ListNotNull(response.getRows())) {
            list.addAll(response.getRows());
            refreshLayout.setFooterNoMoreData(list.size() >= response.getTotal());
        } else {
            refreshLayout.setFooterNoMoreData(true);
        }
        if (adapter != null) {
            adapter.changeDataUi(list);

        }
        if (null != recyclerView && frist) {
            recyclerView.scrollToPosition(ZERO);
        }
        handlerEmpty();
    }

    /**
     * 处理空太页
     */
    private void handlerEmpty() {
        if (CommonUtils.ListNotNull(list)) {
            refreshLayout.setDataContent(true);
            mLoading.showContent();
        } else {
            refreshLayout.setDataContent(false);
            mLoading.showEmpty();
        }
    }

    @OnClick({R.id.ll_top_right_parent, R.id.ll_top_left_parent})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_top_right_parent:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    toast(MyApp.getLanguageString(this, R.string.Share_Operate_Frequently));
                } else {
                    startCallbackNet(false, false, false);
                    isLoading = false;
                    orderByColumn = StringConstants.AMOUNT;
                    if ((Boolean) ivTopRight.getTag()) {
                        ivTopRight.setTag(false);
                        ivTopRight.setBackgroundResource(R.drawable.icon_up_tip);
                        isAsc = StringConstants.ASC;
                    } else {
                        ivTopRight.setTag(true);
                        ivTopRight.setBackgroundResource(R.drawable.icon_down_tip);
                        isAsc = StringConstants.DESC;
                    }
                    ivTopLeft.setTag(true);
                    ivTopLeft.setBackgroundResource(R.drawable.icon_down_tip);
                    startCallbackNet(true, true, false);
                }
                break;
            case R.id.ll_top_left_parent:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    toast(MyApp.getLanguageString(this, R.string.Share_Operate_Frequently));
                } else {
                    startCallbackNet(false, false, false);
                    isLoading = false;
                    orderByColumn = StringConstants.CANSPLIT;
                    if ((Boolean) ivTopLeft.getTag()) {
                        ivTopLeft.setTag(false);
                        ivTopLeft.setBackgroundResource(R.drawable.icon_up_tip);
                        isAsc = StringConstants.ASC;
                    } else {
                        ivTopLeft.setTag(true);
                        ivTopLeft.setBackgroundResource(R.drawable.icon_down_tip);
                        isAsc = StringConstants.DESC;
                    }
                    ivTopRight.setTag(true);
                    ivTopRight.setBackgroundResource(R.drawable.icon_down_tip);
                    startCallbackNet(true, true, false);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startCallbackNet(false, false, false);
    }
}
