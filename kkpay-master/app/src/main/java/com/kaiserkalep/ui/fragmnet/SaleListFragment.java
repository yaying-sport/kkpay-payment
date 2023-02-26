package com.kaiserkalep.ui.fragmnet;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.SaleListAdapter;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.MySellBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.MySmartRefreshLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.THREE;
import static com.kaiserkalep.constants.Config.THREE_STRING;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.TWO_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.StringConstants.CREATETIME;
import static com.kaiserkalep.constants.StringConstants.UPDATETIME;

/**
 * @Auther: Jack
 * @Date: 2020/8/15 15:47
 * @Description:
 */
public class SaleListFragment extends PageBaseFragment implements View.OnClickListener, SucceedCallBackListener<MySellBean.RowsDTO> {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.loading)
    LoadingLayout mLoadingLayout;
    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;

    //外层通知,下次显示是否需要刷新
    private boolean isRefresh = false;

    private List<MySellBean.RowsDTO> list = new ArrayList<>();
    private SaleListAdapter adapter;
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
        adapter = new SaleListAdapter(context, list, this);
        recyclerView.setAdapter(adapter);
        mLoadingLayout.setRetryListener(this);
        mLoadingLayout.setErrorImage(R.drawable.no_data);
        mLoadingLayout.setErrorText(MyApp.getLanguageString(getContext(), R.string.no_content));
        refreshLayout.setOnRefreshListener(refreshLayout -> refresh(true));
        refreshLayout.setOnLoadMoreListener(refreshLayout -> refresh(false));
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

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
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
        new FundImpl(new ZZNetCallBack<MySellBean>(this, MySellBean.class) {
            @Override
            public void onSuccess(MySellBean response) {
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
        }.setNeedDialog(false).setNeedToast(true)).mySell(UPDATETIME, StringConstants.DESC, Config.NORMAL_PAGE_SIZE, String.valueOf(pageIndex), getState());
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
    private void setData(boolean isRefresh, MySellBean response) {
        if (isRefresh && list.size() > ZERO) {
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

    private String getState() {
        switch (saleIndexId) {
            case ZERO:
                return ONE_STRING;
            case ONE:
                return TWO_STRING;
            case TWO:
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
