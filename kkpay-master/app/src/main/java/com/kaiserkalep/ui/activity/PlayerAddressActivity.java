package com.kaiserkalep.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.Gson;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.PlayerAddAdapter;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.PlayerAddBean;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.widgets.CleanEditTextView;
import com.kaiserkalep.widgets.LoadingLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.THREE_STRING;
import static com.kaiserkalep.constants.Config.TWO_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 玩家通讯录
 */
public class PlayerAddressActivity extends ZZActivity implements TextWatcher, SucceedCallBackListener<String>, View.OnClickListener {

    @BindView(R.id.et_sh_account)
    CleanEditTextView etShAccount;
    @BindView(R.id.tv_text_tip)
    TextView tvTextTip;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.loading)
    LoadingLayout mLoadingLayout;

    private String title = "";
    private List<String> list = new ArrayList<>();
    private String searchStr = "";
    private PlayerAddAdapter adapter;
    private PlayerAddBean playeraddbean;
    private String logo = "";
    private String id = "";//商户id
    private String address = "";//商户地址
    private String startTitle = "";
    private String noNetwork;
    private String noContent;

    @Override
    public int getViewId() {
        return R.layout.activity_playeraddress;
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
        Bundle bundle = getBundleParams();
        if (bundle != null) {
            id = bundle.getString(StringConstants.ID);
            address = bundle.getString(StringConstants.ADDRESS);
            startTitle = bundle.getString(StringConstants.TITLE);
        }
        title = String.format(MyApp.getLanguageString(this, R.string.player_view_title), startTitle);
        commTitle.init(title, "", "", R.drawable.icon_addwj_right, this::addWj);
        tvTextTip.setText(String.format(MyApp.getLanguageString(this, R.string.player_text_tip), startTitle));
        etShAccount.setHint(String.format(MyApp.getLanguageString(this, R.string.depositsh_search_wj_name_tip), startTitle));
        etShAccount.addTextChangedListener(this);
        SimpleItemAnimator itemAnimator = (SimpleItemAnimator) recyclerView.getItemAnimator();
        if (null != itemAnimator) {
            itemAnimator.setSupportsChangeAnimations(false);//单条刷新无动画
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlayerAddAdapter(getContext(), logo, list, this);
        recyclerView.setAdapter(adapter);
        mLoadingLayout.setRetryListener(this);
        mLoadingLayout.setErrorImage(R.drawable.no_data);
        noNetwork = MyApp.getLanguageString(getContext(), R.string.no_network);
        mLoadingLayout.setErrorText(noNetwork);
        mLoadingLayout.setEmptyImage(R.drawable.no_data);
        noContent = MyApp.getLanguageString(getContext(), R.string.no_content);
        mLoadingLayout.setEmptyText(noContent);
        getAgentUserName();
    }

    /**
     * @param view
     */
    private void addWj(View view) {
        Intent intent = new Intent(this, AddShAcountDetailActivity.class);
        intent.putExtra(StringConstants.ID, id);
        intent.putExtra(StringConstants.ADDRESS, address);
        intent.putExtra(StringConstants.TYPE, ONE_STRING);
        startActivity(intent);
        finish();
    }

    @Override
    public void succeedCallBack(@Nullable String o) {
        if (CommonUtils.StringNotNull(o)) {
            setResult(RESULT_OK, new Intent().putExtra(StringConstants.DATA, o));
            finish();
        }
    }

    /**
     * 获取自定商家玩家账号
     */
    private void getAgentUserName() {
        new UserImpl(new ZZNetCallBack<PlayerAddBean>(this, PlayerAddBean.class) {
            @Override
            public void onSuccess(PlayerAddBean response) {
                if (null != response) {
                    if (list.size() > ZERO) {
                        list.clear();
                    }
                    playeraddbean = response;
                    logo = response.getLogo();
                    List<String> agentUsername = response.getAgentUsername();
                    if (null != agentUsername && agentUsername.size() > ZERO) {
                        showError(ZERO_STRING, "");
                        list.addAll(agentUsername);
                    } else {
                        showError(ONE_STRING, noContent);
                    }
                    adapter.changeDataUi(PlayerAddressActivity.this.logo, list);
                } else {
                    showError(TWO_STRING, MyApp.getLanguageString(getContext(), R.string.Share_Data_Error));
                }
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                showError(TWO_STRING, CommonUtils.StringNotNull(msg) ? msg : noNetwork);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }
        }.setNeedDialog(true).setNeedToast(true)).getAgentUserName(id);
    }


    private void showError(String state, String str) {
        switch (state) {
            case ZERO_STRING:
                if (null != mLoadingLayout) {
                    mLoadingLayout.showContent();
                }
                break;
            case ONE_STRING:
                if (null != mLoadingLayout) {
                    mLoadingLayout.showEmpty();
                    mLoadingLayout.setEmptyText(str);
                }
                break;
            case TWO_STRING:
                if (null != mLoadingLayout) {
                    mLoadingLayout.showError();
                    mLoadingLayout.setErrorText(str);
                }
                break;
            case THREE_STRING:
                if (null != mLoadingLayout) {
                    mLoadingLayout.showLoading();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry_button) {
            getAgentUserName();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Editable text = etShAccount.getText();
        if (null != text) {
            searchStr = text.toString().trim();
        } else {
            searchStr = "";
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        setSearchStr(searchStr);
    }

    /**
     * @param searchStr 搜索字段
     */
    public void setSearchStr(String searchStr) {
        if (CommonUtils.StringNotNull(searchStr)) {
            if (null != adapter && list.size() > ZERO) {
                List<String> listSearch = new ArrayList<>();
                for (String str : list) {
                    if (CommonUtils.StringNotNull(str) && str.toLowerCase().contains(searchStr.toLowerCase())) {
                        listSearch.add(str);
                    }
                }
                adapter.changeDataUi(PlayerAddressActivity.this.logo, listSearch);
            }
        } else {
            if (null != adapter) {
                adapter.changeDataUi(PlayerAddressActivity.this.logo, list);
            }
        }
    }
}
