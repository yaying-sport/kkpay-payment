package com.kaiserkalep.ui.fragmnet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.MyOrderListAdapter;
import com.kaiserkalep.adapter.SaleListAdapter;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.MyOrderBean;
import com.kaiserkalep.bean.MySellBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.ui.activity.MainActivity;
import com.kaiserkalep.ui.activity.OrderDetailsActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.MySmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

import static com.kaiserkalep.constants.Config.FOUR_STRING;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.OWN_ONE_STRING;
import static com.kaiserkalep.constants.Config.THREE;
import static com.kaiserkalep.constants.Config.THREE_STRING;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.TWO_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;
import static com.kaiserkalep.constants.StringConstants.CREATETIME;
import static com.kaiserkalep.constants.StringConstants.UPDATETIME;

/**
 * @Description: 状态 1进行中，4已完成，-1已取消，3暂停中，不传取全部
 */
public class MyOrderListFragment extends PageBaseFragment implements View.OnClickListener, SucceedCallBackListener<MyOrderBean.RowsDTO> {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.loading)
    LoadingLayout mLoadingLayout;
    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;

    //外层通知,下次显示是否需要刷新
    protected boolean isRefresh = false;

    private List<MyOrderBean.RowsDTO> list = new ArrayList<>();
    private MyOrderListAdapter adapter;
    private int saleIndexId;
    private int pageIndex = 1;//页数

    @Override
    public int getViewId() {
        return R.layout.comm_list_layout;
    }

    @Override
    protected void afterViews() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            saleIndexId = bundle.getInt(StringConstants.SALE_INDEX_ID);
        }
        Context context = getContext();
        assert context != null;
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);//单条刷新无动画
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MyOrderListAdapter(context, list, this);
        recyclerView.setAdapter(adapter);
        mLoadingLayout.setRetryListener(this);
        mLoadingLayout.setErrorImage(R.drawable.no_data);
        mLoadingLayout.setErrorText(MyApp.getLanguageString(getContext(), R.string.no_content));
        refreshLayout.setOnRefreshListener(refreshLayout -> refresh(true));
        refreshLayout.setOnLoadMoreListener(refreshLayout -> refresh(false));
    }

    @Override
    public void succeedCallBack(@Nullable MyOrderBean.RowsDTO o) {
        Context context = getContext();
        if (null != o && null != context) {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra(StringConstants.ORDERNO, o.getOrderNo());
            context.startActivity(intent);
        }
    }

    /**
     * 网络错误重试
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry_button) {
            if (null != refreshLayout) {
                refreshLayout.setBackgroundColor(MyApp.getMyColor(R.color.activity_bg));
            }
            refresh(true);
        }
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        if (null != mLoadingLayout) {
            mLoadingLayout.showLoading();
        }
        refresh(true);
    }

    @Override
    protected void onUserDoubleVisible() {
        super.onUserDoubleVisible();
        if (isRefresh) {
            isRefresh = false;
            toTopRefresh();
        }
    }

    /**
     * 获取网络数据
     */
    public void refresh(boolean isStart) {
        if (isStart) {
            pageIndex = 1;
        }
        new FundImpl(new ZZNetCallBack<MyOrderBean>(this, MyOrderBean.class) {
            @Override
            public void onSuccess(MyOrderBean response) {
                setData(isStart, response);
                pageIndex++;
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                if (mLoadingLayout != null) {
                    refreshLayout.setDataContent(false);
                    mLoadingLayout.showError();
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                loadDone(isStart);
            }
        }.setNeedDialog(false).setNeedToast(true)).myBuyOrder(UPDATETIME, StringConstants.DESC, Config.NORMAL_PAGE_SIZE, String.valueOf(pageIndex), getState());
    }

    /**
     * 请求完成
     */
    private void loadDone(boolean isRefresh) {
        if (refreshLayout != null) {
            openRefresh(refreshLayout, mLoadingLayout);
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
    }

    /**
     * 设置数据
     *
     * @param isRefresh 是否刷新
     * @param response  请求的数据
     */
    private void setData(boolean isRefresh, MyOrderBean response) {
        if (isRefresh && list.size() > ZERO) {
            list.clear();
        }
        //如果是进行中或者暂停中的订单
        if ((saleIndexId == 0 || saleIndexId == 3) && response != null) {
            if (CommonUtils.ListNotNull(response.getRows())) {
                for (MyOrderBean.RowsDTO r : response.getRows()) {
                    MyApp.getApplication().orderMap.put(r.getOrderNo(), r);
                }
            } else {
                for (String orderId : MyApp.orderMap.keySet()) {
                    MyOrderBean.RowsDTO r = MyApp.orderMap.get(orderId);
                    //0买家下单，1卖家确认，2买家转账，3，暂停，4卖家打币，-1取消(当前状态，页面展示下一个状态)
                    if (saleIndexId == 0 &&
                            (ZERO_STRING.equals(r.getStatus())
                                    || ONE_STRING.equals(r.getStatus())
                                    || TWO_STRING.equals(r.getStatus())
                            )) {
                        //如果是进行中 则删除orderMap中进行中的数据
                        MyApp.orderMap.remove(orderId);
                    } else if (saleIndexId == 3 && THREE_STRING.equals(r.getStatus())) {
                        //如果是暂停中 则删除orderMap中暂停中的数据
                        MyApp.orderMap.remove(orderId);
                    }
                }
                MyApp.getApplication().checkOrder();
            }
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
        handlerEmpty();
    }

    /**
     * 处理空太页
     */
    private void handlerEmpty() {
        if (CommonUtils.ListNotNull(list)) {
            refreshLayout.setDataContent(true);
            mLoadingLayout.showContent();
        } else {
            refreshLayout.setDataContent(false);
            mLoadingLayout.showEmpty();
        }
    }

    /**
     * 状态 1进行中，4已完成，-1已取消，3暂停中，不传取全部
     *
     * @return
     */
    private String getState() {
        switch (saleIndexId) {
            case ZERO:
                return ONE_STRING;
            case ONE:
                return FOUR_STRING;
            case TWO:
                return OWN_ONE_STRING;
            case THREE:
                return THREE_STRING;
        }
        return "";
    }

    @Override
    public void setNextShowRefresh(boolean refresh) {
        if (!isNeedInit) {//没有初始化,不设置下次显示刷新
            this.isRefresh = refresh;
        }
    }

    @Override
    public void scrollTop() {
        if (!isNeedInit && !isRefresh) {//没有初始化不调用
            toTopRefresh();
        }
    }

    private void toTopRefresh() {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
        if (refreshLayout != null) {
            refreshLayout.autoRefresh();
        }
    }
}
