package com.kaiserkalep.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.FeedDetailAdapter;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.FeedDetailBean;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.net.impl.SysImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.widgets.AndroidBug5497Workaround;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.MyLinearLayoutManager;
import com.kaiserkalep.widgets.MySmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ZERO;

/**
 * 反馈详情
 */
public class FeedBackDetailActivity extends ZZActivity implements View.OnClickListener, TextWatcher {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.loading)
    LoadingLayout mLoadingLayout;
    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;
    @BindView(R.id.et_send)
    EditText etSend;
    @BindView(R.id.iv_send)
    ImageView ivSend;


    private String title = "";
    private List<FeedDetailBean> list = new ArrayList<>();
    private String feedBackId = "";
    private FeedDetailAdapter feedDetailAdapter;
    private MyLinearLayoutManager myLinearLayoutManager;
    private String content = "";

    @Override
    public int getViewId() {
        return R.layout.activity_feedback_detail;
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
        AndroidBug5497Workaround.assistActivity(this).setNeedStatusBar(false);
        title = MyApp.getLanguageString(getContext(), R.string.Share_Feedback_Detail);
        commTitle.init(title);
        try {
            HashMap<String, String> urlParam = getUrlParam();
            if (null != urlParam && urlParam.size() > ZERO) {
                feedBackId = urlParam.get(StringConstants.ID);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        feedDetailAdapter = new FeedDetailAdapter(this, list);
        recyclerView.setAdapter(feedDetailAdapter);
        myLinearLayoutManager = new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(myLinearLayoutManager);

        etSend.addTextChangedListener(this);

        mLoadingLayout.setRetryListener(this);
        mLoadingLayout.setErrorImage(R.drawable.no_data);
        mLoadingLayout.setErrorText(MyApp.getLanguageString(getContext(), R.string.no_content));
        refreshLayout.setOnRefreshListener(refreshLayout -> refresh(false, false));
        refresh(true, false);
    }

    /**
     * 获取网络数据
     */
    public void refresh(boolean isStart, boolean needDialog) {
        if (isStart && mLoadingLayout != null) {
            mLoadingLayout.showLoading();
        }
        new SysImpl(new ZZNetCallBack<List<FeedDetailBean>>(this, FeedDetailBean.class) {
            @Override
            public void onSuccess(List<FeedDetailBean> response) {
                if (null != response && response.size() > ZERO) {
                    if (mLoadingLayout != null) {
                        mLoadingLayout.showContent();
                    }
                    if (null != list && null != feedDetailAdapter && null != recyclerView) {
                        if (list.size() > ZERO) {
                            list.clear();
                        }
                        list.addAll(response);
                        feedDetailAdapter.notifyDataSetChanged();
                        MyApp.getMainHandler().postDelayed(() -> {
                            if (null != recyclerView && null != feedDetailAdapter) {
                                recyclerView.scrollToPosition(feedDetailAdapter.getItemCount() - ONE);
                            }
                        }, 500);
                    }
                } else {
                    if (mLoadingLayout != null) {
                        mLoadingLayout.showError();
                    }
                }
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                if (mLoadingLayout != null) {
                    mLoadingLayout.showError();
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }
            }
        }.setNeedDialog(needDialog).setNeedToast(true)).getFeedBackDetail(feedBackId);
    }

    @OnClick({R.id.iv_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_send:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    toast(MyApp.getLanguageString(this, R.string.Frequent_operations));
                } else {
                    if (CommonUtils.StringNotNull(content)) {
                        feedReply();
                    } else {
                        toast(MyApp.getLanguageString(this, R.string.feeback_detail_et_hint));
                    }
                }
                break;
            case R.id.retry_button:
                if (null != refreshLayout) {
                    refreshLayout.setBackgroundColor(MyApp.getMyColor(R.color.activity_bg));
                }
                refresh(true, false);
                break;
        }
    }

    /**
     * 回复
     */
    public void feedReply() {
        new SysImpl(new ZZNetCallBack<FeedDetailBean>(this, FeedDetailBean.class) {
            @Override
            public void onSuccess(FeedDetailBean response) {
                if (null != etSend) {
                    etSend.setText("");
                }
                if (null != content) {
                    content = "";
                }
                refresh(false, true);
            }

        }.setNeedDialog(true).setNeedToast(true)).getFeedBackReply(feedBackId, content);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Editable text = etSend.getText();
        if (text.length() > ZERO) {
            ivSend.setImageResource(R.drawable.chat_send_selected);
        } else {
            ivSend.setImageResource(R.drawable.chat_send_normal);
        }
        if (null != text) {
            content = text.toString().trim();
        } else {
            content = "";
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
