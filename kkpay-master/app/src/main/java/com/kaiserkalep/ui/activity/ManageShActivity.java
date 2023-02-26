package com.kaiserkalep.ui.activity;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.ManageShAdapter;
import com.kaiserkalep.adapter.WalletAdapter;
import com.kaiserkalep.base.IntentUtils;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.AgentBindInfoBean;
import com.kaiserkalep.bean.AgentBindListBean;
import com.kaiserkalep.bean.NoticeDialogData;
import com.kaiserkalep.bean.ToMoreDialogData;
import com.kaiserkalep.bean.WalletManageBean;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.ManageShInterface;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.widgets.MyRecycleView;
import com.kaiserkalep.widgets.MySmartRefreshLayout;
import com.kaiserkalep.widgets.shadowLayout.TouchShadowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.TEN;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 商户管理（商户列表）
 */
public class ManageShActivity extends ZZActivity implements ManageShInterface {

    @BindView(R.id.recyclerView)
    MyRecycleView recyclerView;
    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;
    @BindView(R.id.sl_click_add)
    TouchShadowLayout slClickAdd;

    private String title = "";
    private ManageShAdapter adapter;
    private List<AgentBindInfoBean> list = new ArrayList<>();

    @Override
    public int getViewId() {
        return R.layout.activity_managesh;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAgentBindList(true);
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
        title = MyApp.getLanguageString(getContext(), R.string.managesh_view_title);
        commTitle.init(title);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnablePureScrollMode(false);
        refreshLayout.setEnableOverScrollDrag(true);//是否启用越界拖动（仿苹果效果）1.0.4
        refreshLayout.setOnRefreshListener(refreshLayout -> getAgentBindList(false));
        adapter = new ManageShAdapter(getContext(), list, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void getAgentBindList(boolean needDialog) {
        new FundImpl(new ZZNetCallBack<AgentBindListBean>(this, AgentBindListBean.class) {
            @Override
            public void onSuccess(AgentBindListBean response) {
                if (null != response) {
                    reset();
                    List<AgentBindInfoBean> rows = response.getRows();
                    if (null != rows && rows.size() > ZERO) {
                        list.addAll(rows);
                    }
                    if (null != slClickAdd) {
                        slClickAdd.setVisibility(list.size() >= TEN ? View.GONE : View.VISIBLE);
                    }
                    if (null != adapter) {
                        adapter.changeDataUi(list);
                    }
                } else {
                    toast(MyApp.getLanguageString(ManageShActivity.this, R.string.Share_Data_Error));
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (null != refreshLayout) {
                    refreshLayout.finishRefresh();
                }

            }
        }.setNeedDialog(needDialog).setNeedToast(true)).agentMemberBindHasBind();
    }

    @OnClick({R.id.sl_click_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sl_click_add:
                checkLogin(R.string.AddManageShActivity);
                break;
        }
    }

    private void reset() {
        if (list.size() > ZERO) {
            list.clear();
        }
    }

    @Override
    public void toView(String url) {
        if (CommonUtils.StringNotNull(url)) {
            CommonUtils.startBrowser(ManageShActivity.this, url);
        } else {
            toast(MyApp.getLanguageString(ManageShActivity.this, R.string.Start_Browser_Url_Empty_Tip));
        }
    }

    @Override
    public void manage(String agentid) {
        checkLogin(R.string.ManageShSettingActivity, IntentUtils.getHashObj(new String[]{
                StringConstants.AGENTID, agentid}));
    }

    @Override
    public void more(String title, List<String> list) {
        if (null != list && list.size() > ZERO) {
            MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value,
                    MyDialogManager.ToMoreUrlDialog, new ToMoreDialogData(title, list));
        }
    }
}
