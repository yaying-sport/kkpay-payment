package com.kaiserkalep.ui.activity;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.SaleListAdapter;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.MySellBean;
import com.kaiserkalep.bean.WalletRecordBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.MySmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 钱包记录
 */
public class WalletRecordActivity extends ZZActivity implements View.OnClickListener, SucceedCallBackListener<WalletRecordBean.RowsDTO> {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.loading)
    LoadingLayout mLoadingLayout;
    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;

    private String title = "";
    private WalletRecordAdapter adapter;
    private List<WalletRecordBean.RowsDTO> list = new ArrayList<>();
    private int pageNum = ZERO;

    @Override
    public int getViewId() {
        return R.layout.activity_wallet_record;
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
    public void afterViews() {
        title = MyApp.getLanguageString(getContext(), R.string.wallet_record_title);
        commTitle.init(title);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);//单条刷新无动画
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WalletRecordAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
        mLoadingLayout.setRetryListener(this);
        mLoadingLayout.setErrorImage(R.drawable.no_data);
        mLoadingLayout.setErrorText(MyApp.getLanguageString(getContext(), R.string.no_content));
        refreshLayout.setOnRefreshListener(refreshLayout -> refresh(true));
        refreshLayout.setOnLoadMoreListener(refreshLayout -> refresh(false));
        refresh(true);
    }

    @Override
    public void succeedCallBack(@Nullable WalletRecordBean.RowsDTO o) {
        if (null != o) {
            HashMap<String, String> params = new HashMap<>();
            params.put(StringConstants.TITLE, o.getTitle());
            params.put(StringConstants.MONEY, o.getAmountOrder());
            params.put(StringConstants.AMOUNT, o.getAmount());
            params.put(StringConstants.BILLNO, o.getBillNo());
            params.put(StringConstants.BILLTYPE, o.getBillType());
            params.put(StringConstants.AMOUNTTYPE, o.getAmountType());
            params.put(StringConstants.CREATETIME, o.getCreateTime());
            params.put(StringConstants.TRADETYPE, o.getTradeType());
            params.put(StringConstants.AGENTUSERNAME, o.getAgentName());
            startClass(MyApp.getLanguageString(getContext(), R.string.WalletRecordDetailsActivity), params);
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
            refresh(true);
        }
    }

    /**
     * 初始化
     */
    public void refresh(boolean isStart) {
        if (isStart) {
            pageNum = 1;
        }
        new FundImpl(new ZZNetCallBack<WalletRecordBean>(this, WalletRecordBean.class) {
            @Override
            public void onSuccess(WalletRecordBean response) {
                setData(isStart, response);
                pageNum++;
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
        }.setNeedDialog(false).setNeedToast(true)).myWalletBill(StringConstants.CREATETIME, StringConstants.DESC, Config.NORMAL_PAGE_SIZE, String.valueOf(pageNum));
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
    private void setData(boolean isRefresh, WalletRecordBean response) {
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


}
