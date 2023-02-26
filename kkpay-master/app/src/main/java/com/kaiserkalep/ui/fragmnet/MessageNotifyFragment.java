package com.kaiserkalep.ui.fragmnet;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.MessageNotifyAdapter;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.CommDialogData;
import com.kaiserkalep.bean.SysNoticeListData;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.OnItemClickListener;
import com.kaiserkalep.net.impl.SysImpl;
import com.kaiserkalep.ui.activity.MessageNotifyActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.MyRecycleView;
import com.kaiserkalep.widgets.MySmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ZERO;

/**
 * 消息通知,公告
 *
 * @Auther: Jack
 * @Date: 2020/8/18 10:25
 * @Description:
 */
public class MessageNotifyFragment extends PageBaseFragment implements View.OnClickListener, OnItemClickListener {

    @BindView(R.id.recyclerView)
    MyRecycleView recyclerView;
    @BindView(R.id.loading)
    LoadingLayout mLoadingLayout;
    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_read)
    TextView tvRead;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.rl_edit_content)
    RelativeLayout rlEditContent;

    public List<SysNoticeListData.RowsBean> list = new ArrayList<>();
    private int pageIndex = Config.ONE;//页数
    private MessageNotifyAdapter adapter;
    private int noticeType;//1,通知   2，公告
    private HashSet<String> delIds = new HashSet<>();//删除id
    private HashSet<String> readIds = new HashSet<>();//未读消息id
    private MessageNotifyActivity activity;


    @Override
    public int getViewId() {
        return R.layout.fragment_message_notify;
    }

    @Override
    protected void afterViews() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            noticeType = bundle.getInt(StringConstants.TYPE);
        }
        Context context = getContext();
        assert context != null;
        activity = (MessageNotifyActivity) context;
        recyclerView.closeDefaultAnimator();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MessageNotifyAdapter(context, list, noticeType, this);
        recyclerView.setAdapter(adapter);
        mLoadingLayout.setRetryListener(this);
        refreshLayout.setOnRefreshListener(refreshLayout -> refresh(true));
        refreshLayout.setOnLoadMoreListener(refreshLayout -> refresh(false));

    }


    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        autoRefresh();
    }

    @Override
    protected void onUserDoubleVisible() {
        super.onUserDoubleVisible();
    }

    /**
     * 获取网络数据
     */
    public void refresh(boolean isRefresh) {
        if (isRefresh) {
            pageIndex = Config.ONE;
        }
        new SysImpl(new ZZNetCallBack<SysNoticeListData>(this, SysNoticeListData.class) {
            @Override
            public void onSuccess(SysNoticeListData response) {
                setData(isRefresh, response);
                pageIndex++;
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                if (!CommonUtils.ListNotNull(list)) {
                    if (mLoadingLayout != null) {
                        refreshLayout.setDataContent(false);
                        mLoadingLayout.showError();
                    }
                } else {
                    super.onError(msg, code);
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                loadDone(isRefresh);
            }
        }.setNeedDialog(false)).sysNoticeList(String.valueOf(noticeType), StringConstants.COMM_PAGE_SIZE_2, String.valueOf(pageIndex));
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
    private void setData(boolean isRefresh, SysNoticeListData response) {
        if (isRefresh) {
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
        boolean listNotNull = CommonUtils.ListNotNull(list);
        refreshLayout.setDataContent(listNotNull);
        if (listNotNull) {
            mLoadingLayout.showContent();
        } else {
            mLoadingLayout.showEmpty();
        }
        setFunVisibility(listNotNull);//加载前结束设置编辑按钮显示
    }


    /**
     * 网络错误重试
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (mLoadingLayout != null) {
            mLoadingLayout.showLoading();
        }
        autoRefresh();
    }

    /**
     * 自动下拉刷新
     */
    public void autoRefresh() {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(ZERO);
        }
//        if (refreshLayout != null) {
//            refreshLayout.autoRefresh();
//        }
        refresh(true);
    }


    /**
     * 设置全选
     *
     * @param selectAll
     */
    public void selectAll(boolean selectAll) {
        if (CommonUtils.ListNotNull(list)) {
            if (!selectAll) {
                delIds.clear();
                readIds.clear();
            }
            for (SysNoticeListData.RowsBean rowsBean : list) {
                rowsBean.setSelect(selectAll);
                if (selectAll) {
                    delIds.add(rowsBean.getId());
                    if (rowsBean.getStatus() == ZERO) {
                        readIds.add(rowsBean.getId());
                    }
                }
            }
            adapter.notifyDataSetChanged();
            setSelectNum();
        }
    }

    /**
     * 设置编辑按钮是否显示
     *
     * @param visibility
     */
    private void setFunVisibility(boolean visibility) {
        if (activity != null && activity.tvFunction != null) {
            activity.tvFunction.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 进入编辑
     */
    public void startEdit() {
        adapter.setEdit(true);
        adapter.notifyDataSetChanged();
        refreshLayout.setEnablePureScrollMode(true);
        rlEditContent.setVisibility(View.VISIBLE);
    }

    /**
     * 退出编辑
     */
    public void cancelEdit() {
        adapter.setEdit(false);
        adapter.notifyDataSetChanged();
        refreshLayout.setEnablePureScrollMode(false);
        rlEditContent.setVisibility(View.GONE);
    }

    @OnClick({R.id.tv_read, R.id.tv_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_read://标记已读
                if (!tvRead.isSelected()) {
                    return;
                }
                readMsg(true);
                break;
            case R.id.tv_delete://删除消息
                if (!tvDelete.isSelected()) {
                    return;
                }
                MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.CommonDialog,
                        new CommDialogData(MyApp.getLanguageString(getContext(), R.string.messagenotifyfragment_ConfirmDelete), MyApp.getLanguageString(getContext(), R.string.messagenotifyfragment_ConfirmDelete_title),
                                MyApp.getLanguageString(getContext(), R.string.Share_cancel), MyApp.getLanguageString(getContext(), R.string.Share_certain), null, v -> deleteMsg()));
                break;
        }
    }

    /**
     * 标记已读
     */
    private void readMsg(boolean needDialog) {
        String s = CommonUtils.hashSetToString(readIds);
        if (!CommonUtils.StringNotNull(s)) {
            return;
        }
        new SysImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                cancelEditRefresh();
            }

            @Override
            public void onError(String msg, String code) {
                if (StringConstants.REQUEST_OTHER_ERROR.equals(code)) {//网络异常
                    super.onError(msg, code);
                } else {
                    toast(MyApp.getLanguageString(getContext(), R.string.messagenotifyfragment_FailedToMarkRead));
                }
            }
        }.setNeedDialog(needDialog)).readMsg(s);
    }

    /**
     * 删除
     */
    private void deleteMsg() {
        String s = CommonUtils.hashSetToString(delIds);
        if (!CommonUtils.StringNotNull(s)) {
            return;
        }
        new SysImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                cancelEditRefresh();
            }

            @Override
            public void onError(String msg, String code) {
                if (StringConstants.REQUEST_OTHER_ERROR.equals(code)) {//网络异常
                    super.onError(msg, code);
                } else {
                    toast(MyApp.getLanguageString(getContext(), R.string.messagenotifyfragment_FailedToDelete));
                }
            }
        }).sysNoticeDelete(s);
    }

    /**
     * 退出编辑并刷新列表及未读
     */
    private void cancelEditRefresh() {
        //先做本地列表删除
        if (CommonUtils.ListNotNull(list)) {
            Iterator<SysNoticeListData.RowsBean> iterator = list.iterator();
            while (iterator.hasNext()) {
                SysNoticeListData.RowsBean next = iterator.next();
                if (next != null && next.isSelect()) {
                    iterator.remove();
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
        if (delIds != null) {
            delIds.clear();
        }
        if (readIds != null) {
            readIds.clear();
        }
        setSelectNum();
        if (activity != null) {
            activity.cancelEdit();
            activity.getMsgNum();
        }
        autoRefresh();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (CommonUtils.ListNotNull(list)) {
            SysNoticeListData.RowsBean rowsBean = list.get(position);
            if (rowsBean != null) {
                String id = rowsBean.getId();
                if (adapter.isEdit()) {
                    rowsBean.setSelect(!rowsBean.isSelect());
                    adapter.notifyItemChanged(position);
                    if (rowsBean.isSelect()) {
                        delIds.add(id);
                        if (rowsBean.getStatus() == ZERO) {
                            readIds.add(id);
                        }
                        if (delIds.size() == list.size()) {//全部选中后，全选变更为取消全选
                            setSelectAll(true);
                        }
                    } else {
                        delIds.remove(id);
                        readIds.remove(id);
//                        if (delIds.size() == 0) {//全部取消选中后，取消全选变更为全选
                        setSelectAll(false);//只要有取消操作,取消全选变更为全选
//                        }
                    }
                    setSelectNum();
                } else {
                    //跳转时做消息已读
                    if (readIds != null) {
                        readIds.add(rowsBean.getId());
                    }
                    readMsg(false);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(StringConstants.DATA, rowsBean);
                    startClassForResult(MyApp.getMyString(R.string.MessageNotifyDetailActivity),
                            null, MessageNotifyActivity.CHECK_DETAIL_CODE, true, bundle);
                }
            }
        }
    }

    /**
     * 选择条目控制全选
     *
     * @param selectAll
     */
    private void setSelectAll(boolean selectAll) {
        if (activity != null) {
            activity.selectAll(selectAll, false);
        }
    }

    /**
     * 设置删除、标记已读按钮
     */
    private void setSelectNum() {
        String ShareDelete = MyApp.getLanguageString(getContext(), R.string.Share_delete);
        if (delIds.size() > ZERO) {
            tvDelete.setText(ShareDelete + "(" + delIds.size() + ")");
            tvDelete.setSelected(true);
        } else {
            tvDelete.setText(ShareDelete);
            tvDelete.setSelected(false);
        }
        tvRead.setSelected(readIds.size() > ZERO);
    }
}
