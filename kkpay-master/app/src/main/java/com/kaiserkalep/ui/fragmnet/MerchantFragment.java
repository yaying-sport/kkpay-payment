package com.kaiserkalep.ui.fragmnet;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.MerchantAdapter;
import com.kaiserkalep.adapter.SaleListAdapter;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.MerchantBean;
import com.kaiserkalep.bean.MySellBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.ui.activity.MerchantDirectoryActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.MySmartRefreshLayout;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.THREE_STRING;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.TWO_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;
import static com.kaiserkalep.constants.StringConstants.CREATETIME;

/**
 * @Auther: Jack
 * @Date: 2020/8/15 15:47
 * @Description:
 */
public class MerchantFragment extends PageBaseFragment implements View.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.loading)
    LoadingLayout mLoadingLayout;

    private SucceedCallBackListener<MerchantBean.RowsDTO> listener;
    private List<MerchantBean.RowsDTO> list = new ArrayList<>();
    private MerchantAdapter adapter;
    private int saleIndexId;
    private String noContent;
    private String noNetwork;

    @Override
    public int getViewId() {
        return R.layout.merchant_list_layout;
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
        adapter = new MerchantAdapter(getContext(), saleIndexId, list, listener);
        recyclerView.setAdapter(adapter);
        mLoadingLayout.setRetryListener(this);
        mLoadingLayout.setErrorImage(R.drawable.no_data);
        noNetwork = MyApp.getLanguageString(getContext(), R.string.no_network);
        mLoadingLayout.setErrorText(noNetwork);
        mLoadingLayout.setEmptyImage(R.drawable.no_data);
        noContent = MyApp.getLanguageString(getContext(), R.string.no_content);
        mLoadingLayout.setEmptyText(noContent);
    }

    public MerchantFragment setListener(SucceedCallBackListener<MerchantBean.RowsDTO> listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 网络错误重试
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry_button) {
            refresh();
        }
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        refresh();
    }

    private void refresh() {
        FragmentActivity activity = getActivity();
        if (activity instanceof MerchantDirectoryActivity) {
            String searchStr = ((MerchantDirectoryActivity) activity).getSearchStr();
            if (CommonUtils.StringNotNull(searchStr)) {
                getAgentMemBook(true, searchStr);
            } else {
                getAgentMemBook(false, "");
            }
        }
    }

    @Override
    protected void onUserDoubleVisible() {
        super.onUserDoubleVisible();
    }

    /**
     * @param searchStr 搜索字段
     */
    public void setSearchStr(String searchStr) {
        if (CommonUtils.StringNotNull(searchStr)) {
            if (null != adapter && list.size() > ZERO) {
                String searchStrLower = searchStr.toLowerCase();
                List<MerchantBean.RowsDTO> listSearch = new ArrayList<>();
                for (int i = ZERO; i < list.size(); i++) {
                    MerchantBean.RowsDTO merchantBean = list.get(i);
                    String agentName = merchantBean.getAgentName().toLowerCase();
                    String walletAddress = merchantBean.getWalletAddress().toLowerCase();
                    String agentUsername = merchantBean.getAgentUsername().toLowerCase();
                    if (CommonUtils.StringNotNull(agentName) && agentName.contains(searchStrLower)) {
                        listSearch.add(merchantBean);
                        continue;
                    }
                    if (CommonUtils.StringNotNull(walletAddress) && walletAddress.contains(searchStrLower)) {
                        listSearch.add(merchantBean);
                        continue;
                    }
                    if (CommonUtils.StringNotNull(agentUsername) && agentUsername.contains(searchStrLower)) {
                        if (saleIndexId == ZERO) {
                            listSearch.add(merchantBean);
                        }
                    }
                }
                adapter.changeDataUi(saleIndexId, listSearch);
            }
        } else {
            if (null != adapter) {
                adapter.changeDataUi(saleIndexId, list);
            }
        }
    }

    /**
     * 获取商户通讯录
     */
    private void getAgentMemBook(boolean search, String searchStr) {
        new UserImpl(new ZZNetCallBack<MerchantBean>(this, MerchantBean.class) {
            @Override
            public void onSuccess(MerchantBean response) {
                if (null != response) {
                    if (list.size() > ZERO) {
                        list.clear();
                    }
                    List<MerchantBean.RowsDTO> rows = response.getRows();
                    if (rows.size() > ZERO) {
                        showOrHideMloadView(ZERO_STRING, "");
                        list.addAll(rows);
                    } else {
                        showOrHideMloadView(ONE_STRING, noContent);
                    }
                    if (search) {
                        setSearchStr(searchStr);
                    } else {
                        adapter.changeDataUi(saleIndexId, list);
                    }
                } else {
                    showOrHideMloadView(TWO_STRING, MyApp.getLanguageString(getContext(), R.string.Share_Data_Error));
                }
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                showOrHideMloadView(TWO_STRING, CommonUtils.StringNotNull(msg) ? msg : noNetwork);
            }

        }.setNeedDialog(true).setNeedToast(true)).getAgentbook(ZERO == saleIndexId ? StringConstants.LAST : StringConstants.ALL);
    }

    private void showOrHideMloadView(String state, String str) {
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
}
