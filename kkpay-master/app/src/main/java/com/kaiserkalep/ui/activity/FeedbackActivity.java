package com.kaiserkalep.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.ChannelDetailsBean;
import com.kaiserkalep.bean.FeedBackUnReadbean;
import com.kaiserkalep.bean.FeedbackTitleBean;
import com.kaiserkalep.bean.SelectBankDialogData;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.net.impl.SysImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.widgets.LoadingLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ZERO;

public class FeedbackActivity extends ZZActivity implements TextWatcher, View.OnClickListener {


    @BindView(R.id.loading)
    LoadingLayout mLoading;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.et_content)
    EditText etContent;

    private String title = "";
    /**
     * 选择反馈标题l
     */
    private List<FeedbackTitleBean> list = new ArrayList<>();
    private String content = "";
    private FeedbackTitleBean feedbackTitleBean;

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
    public int getViewId() {
        return R.layout.feedbackactivity;
    }

    @Override
    public void afterViews() {
        title = MyApp.getLanguageString(getContext(), R.string.Share_Feedback);
        String MyFeedbackTitle = MyApp.getLanguageString(getContext(), R.string.Share_MyFeedback);
        commTitle.init(title, "", MyFeedbackTitle, 0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackActivity.this.startClass(R.string.MyFeedbackActivity);
            }
        });
        mLoading.setRetryListener(this);
        mLoading.setErrorImage(R.drawable.no_data);
        mLoading.setErrorText(MyApp.getLanguageString(getContext(), R.string.no_content));

        etContent.addTextChangedListener(this);

        getFeedbackTitle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sysFeedbackUnread();
    }

    /**
     * 请求意反馈未读消息
     */
    private void sysFeedbackUnread() {
        if (!isLogin()) {
            return;
        }
        new SysImpl(new ZZNetCallBack<FeedBackUnReadbean>(this, FeedBackUnReadbean.class) {
            @Override
            public void onSuccess(FeedBackUnReadbean response) {
                if (response != null && null != commTitle) {
                    try {
                        int integer = Integer.parseInt(response.getTotal());
                        commTitle.setUnReadMsgConunt(integer);
                    } catch (Exception e) {
                        e.printStackTrace();
                        commTitle.setUnReadMsgConunt(Config.EGATIVE_ONE);
                    }
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }
        }.setNeedDialog(false).setNeedToast(false)).sysFeedbackUnreadNum();
    }

    private void getFeedbackTitle() {
        if (null != mLoading) {
            mLoading.showLoading();
        }
        new SysImpl(new ZZNetCallBack<List<FeedbackTitleBean>>(this, FeedbackTitleBean.class) {
            @Override
            public void onSuccess(List<FeedbackTitleBean> response) {
                if (null != response || response.size() > ZERO) {
                    if (null != mLoading) {
                        mLoading.showContent();
                    }
                    if (null != list) {
                        list.clear();
                    }
                    list.addAll(response);
                } else {
                    if (null != mLoading) {
                        mLoading.showError();
                    }
                }
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                if (null != mLoading) {
                    mLoading.showError();
                }
            }
        }.setNeedDialog(false).setNeedToast(true)).getFeedTitle();
    }

    private void showTitleDialog() {
        String title = MyApp.getLanguageString(FeedbackActivity.this, R.string.feedback_choose_id);
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.SelectBankDialog,
                new SelectBankDialogData<FeedbackTitleBean>(title, R.drawable.icon_transparent_bg, list, (o, position) -> {
                    if (Config.EGATIVE_ONE != position) {
                        for (int j = 0; j < list.size(); j++) {
                            if (position == j) {
                                feedbackTitleBean = list.get(j);
                                tvId.setText(feedbackTitleBean.getDictLabel());
                                tvId.setTextColor(FeedbackActivity.this.getResources().getColor(R.color.black));
                                feedbackTitleBean.setSelect(true);
                            } else {
                                list.get(j).setSelect(false);
                            }
                        }
                    }
                }));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Editable text = etContent.getText();
        if (null != text) {
            content = text.toString().trim();
        } else {
            content = "";
        }


    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void resetData() {
        feedbackTitleBean = null;
        etContent.setText("");
        tvId.setText(MyApp.getLanguageString(this, R.string.feedback_choose_types));
        tvId.setTextColor(FeedbackActivity.this.getResources().getColor(R.color.feedback_iamge_text_color));
        content = "";
        if (null != list) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setSelect(false);
            }
        }
    }

    @OnClick({R.id.ll_id_view, R.id.tv_reset, R.id.tv_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_id_view:
                if (JudgeDoubleUtils.isDoubleClick()){
                    return;
                }
                if (null != list && list.size() > ZERO) {
                    showTitleDialog();
                }
                break;
            case R.id.tv_reset:
                resetData();
                break;
            case R.id.tv_ok:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                if (null != feedbackTitleBean) {
                    if (CommonUtils.StringNotNull(content)) {
                        getFeedbackTitle(feedbackTitleBean.getDictValue(), content);
                    } else {
                        toast(MyApp.getLanguageString(this, R.string.feedback_input_content));
                    }
                } else {
                    toast(MyApp.getLanguageString(this, R.string.feedback_choose_types));
                }
                break;
            case R.id.retry_button:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                getFeedbackTitle();
                break;
        }
    }

    private void getFeedbackTitle(String titleType, String content) {
        new SysImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                FeedbackActivity.this.toast(MyApp.getLanguageString(FeedbackActivity.this, R.string.feedback_success));
                FeedbackActivity.this.startClass(R.string.MyFeedbackActivity);
                FeedbackActivity.this.finish();
            }
        }.setNeedDialog(true).setNeedToast(true)).addFeedBack(titleType, content);
    }

}
