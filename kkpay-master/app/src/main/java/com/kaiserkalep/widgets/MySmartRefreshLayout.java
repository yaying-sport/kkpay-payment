package com.kaiserkalep.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.kaiserkalep.constants.NumberConstants;

import static com.scwang.smartrefresh.layout.util.DensityUtil.dp2px;

/**
 * 重写刷新layout定制功能
 *
 * @Auther: Administrator
 * @Date: 2019/4/11 0011 13:27
 * @Description:
 */
public class MySmartRefreshLayout extends SmartRefreshLayout {

    public MySmartRefreshLayout(Context context) {
        super(context);
    }

    public MySmartRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySmartRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    RecyclerView recyclerView;
    ListView listView;
    int lastPosition = NumberConstants.PRELOAD_LIST_NUMBER;
    int status = -1;//-1,初始  0,未加载   1,加载中  2,全部加载


    /**
     * 递归初始滑动View
     *
     * @param viewGroup
     */
    private void initScroll(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View view = viewGroup.getChildAt(i);
                if (view != null) {
                    if (view instanceof RecyclerView) {
                        recyclerView = (RecyclerView) view;
                        break;
                    } else if (view instanceof ListView) {
                        listView = (ListView) view;
                        break;
                    } else if (view instanceof LoadingLayout) {
                        initScroll((LoadingLayout) view);
                    }
                }
            }
        }
    }

    /**
     * 初始及设置列表预加载
     */
    private void init() {
        if (status != -1 || lastPosition == 0) {
            return;
        }
        status = 0;
        initScroll(this);
        if (recyclerView != null && recyclerView.getAdapter() != null && recyclerView.getLayoutManager() != null &&
                recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState != 0) {
                        if (layoutManager != null && recyclerView.getAdapter() != null) {
                            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                            if (recyclerView.getAdapter().getItemCount() - lastVisibleItemPosition <= lastPosition && status == 0
                                    && mState != RefreshState.Refreshing) {
                                status = 1;
//                            if (mLoadMoreListener != null) {
//                                mLoadMoreListener.onLoadMore(MySmartRefreshLayout.this);
//                            }
                                setStateDirectLoading(true);
//                            setStateLoading(true);
                            }
                        }
                    }
                }
            });
        } else if (listView != null && listView.getAdapter() != null) {
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState != 0) {
                        int lastVisibleItemPosition = listView.getLastVisiblePosition();
                        if (listView.getAdapter() != null &&
                                listView.getAdapter().getCount() - lastVisibleItemPosition <= lastPosition && status == 0
                                && mState != RefreshState.Refreshing) {
                            status = 1;
//                        if (mLoadMoreListener != null) {
//                            mLoadMoreListener.onLoadMore(MySmartRefreshLayout.this);
//                        }
                            setStateDirectLoading(true);
//                        setStateLoading(true);
                        }
                    }
                }
            });
        }

    }


    /**
     * Display refresh animation and trigger refresh event.
     * 显示刷新动画并且触发刷新事件
     *
     * @return true or false, Status non-compliance will fail.
     * 是否成功（状态不符合会失败）
     * <p>
     * 自动下拉距离为head,不回弹
     */
    @Override
    public boolean autoRefresh() {
        return autoRefresh(mHandler == null ? 400 : 0, mReboundDuration, 1, false);
    }

    /**
     * finish load more.
     * 完成加载/修改延时保证有300毫秒
     *
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout finishLoadMore() {
        status = isFooterNoMoreData() ? 2 : 0;
        return finishLoadMore(300);//保证加载动画有300毫秒的时间
    }

    /**
     * finish load more with no more data.
     * 完成加载并标记没有更多数据
     *
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout finishLoadMoreWithNoMoreData() {
        status = 2;
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return finishLoadMore(Math.min(Math.max(0, 300 - (int) passTime), 300), true, true);
    }

    /**
     * finish refresh.
     * 完成刷新
     *
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout finishRefresh() {
        init();
        return finishRefresh(300, false);//保证刷新动画有300毫秒的时间
    }

    /**
     * finish refresh.
     * 完成刷新
     *
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout finishRefresh(final boolean success) {
        init();
        return finishRefresh(300, success);//保证刷新动画有300毫秒的时间
    }

    /**
     * 是否已加载全部
     *
     * @return
     */
    public boolean isFooterNoMoreData() {
        return mFooterNoMoreData;
    }

    private boolean mFooterNoMoreData = false;//将不会再次触发加载更多事件

    /**
     * 设置已全部加载完成
     */
    public void setFooterNoMoreData(boolean mFooterNoMoreData) {
        this.mFooterNoMoreData = mFooterNoMoreData;
    }

    /**
     * 移动视图到指定位置
     * moveSpinner 的取名来自 谷歌官方的 {link androidx.core.widget.SwipeRefreshLayout}
     *
     * @param spinner 位置 (px)
     * @return RefreshKernel
     */
    public void moveSpinner(int spinner) {
        mKernel.moveSpinner(dp2px(spinner), true);
    }


    /**
     * 是否开启纯滚动模式
     *
     * @return
     */
    public boolean isEnablePureScrollMode() {
        return mEnablePureScrollMode;
    }

    /**
     * 是否刷新中/加载下一页中
     *
     * @return
     */
    public boolean isRefreshing() {
        return mState == RefreshState.Refreshing || mState == RefreshState.Loading;
    }

    public void setDataContent(boolean hasData) {
        RefreshFooter refreshFooter = getRefreshFooter();
        if (refreshFooter != null && refreshFooter instanceof MyClassicsFooter) {
            MyClassicsFooter refreshFooter1 = (MyClassicsFooter) refreshFooter;
            refreshFooter1.setHasData(hasData);
        }
    }

    public void setFootText(){
        RefreshFooter refreshFooter = getRefreshFooter();
        if (refreshFooter != null && refreshFooter instanceof MyClassicsFooter) {
            MyClassicsFooter refreshFooter1 = (MyClassicsFooter) refreshFooter;
            refreshFooter1.setFootText();
        }
    }
}
