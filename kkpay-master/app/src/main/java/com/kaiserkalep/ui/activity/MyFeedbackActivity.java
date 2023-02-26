package com.kaiserkalep.ui.activity;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.MyFeedbackListAdapter;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.MyFeedbackBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.net.impl.SysImpl;
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
 * 我的反馈
 */
public class MyFeedbackActivity extends ZZActivity implements SucceedCallBackListener<MyFeedbackBean.RowsDTO>, View.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.loading)
    LoadingLayout mLoadingLayout;
    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;

    private String title = "";

    private List<MyFeedbackBean.RowsDTO> list = new ArrayList<>();
    private MyFeedbackListAdapter adapter;
    private int pageIndex = 1;//页数


    @Override
    public int getViewId() {
        return R.layout.activity_myfeedback;
    }

    @Override
    public void afterViews() {
        title = MyApp.getLanguageString(getContext(), R.string.Share_MyFeedback);
        commTitle.init(title);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);//单条刷新无动画
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyFeedbackListAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
        mLoadingLayout.setRetryListener(this);
        mLoadingLayout.setErrorImage(R.drawable.no_data);
        mLoadingLayout.setErrorText(MyApp.getLanguageString(getContext(), R.string.no_content));
        refreshLayout.setOnRefreshListener(refreshLayout -> refresh(true));
        refreshLayout.setOnLoadMoreListener(refreshLayout -> refresh(false));

        refresh(true);
    }


    @Override
    public void succeedCallBack(@Nullable MyFeedbackBean.RowsDTO o) {
        HashMap<String, String> params = new HashMap<>();
        params.put(StringConstants.ID,o.getId());
        MyFeedbackActivity.this.startClass(R.string.FeedBackDetailActivity, params);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取网络数据
     */
    public void refresh(boolean isStart) {
        if (isStart) {
            pageIndex = 1;
        }
        new SysImpl(new ZZNetCallBack<MyFeedbackBean>(this, MyFeedbackBean.class) {
            @Override
            public void onSuccess(MyFeedbackBean response) {
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
        }.setNeedDialog(false).setNeedToast(true)).getMyFeedBackList(String.valueOf(pageIndex), Config.NORMAL_PAGE_SIZE);
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
    private void setData(boolean isRefresh, MyFeedbackBean response) {
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry_button) {
            if (null != refreshLayout) {
                refreshLayout.setBackgroundColor(MyApp.getMyColor(R.color.activity_bg));
            }
            refresh(true);
        }
    }
}
