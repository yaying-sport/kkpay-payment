package com.kaiserkalep.ui.activity;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.WalletAdapter;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.CommDialogData;
import com.kaiserkalep.bean.AuthenDialogData;
import com.kaiserkalep.bean.WalletManageBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.NumberConstants;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.WalletAddInterface;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.MyRecycleView;
import com.kaiserkalep.widgets.MySmartRefreshLayout;
import com.kaiserkalep.widgets.shadowLayout.TouchShadowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.THREE_STRING;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 收付款管理
 *
 * @Auther: Jack
 * @Date: 2021/1/1 16:42
 * @Description:
 */
public class WalletManageActivity extends ZZActivity implements WalletAddInterface {

    //    @BindView(R.id.tv_addpay_tip)
//    TextView tvAddpayTip;
    @BindView(R.id.recyclerView)
    MyRecycleView recyclerView;
    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;
    @BindView(R.id.sl_click_add)
    TouchShadowLayout slClickAdd;

    private String title = "";
    private List<WalletManageBean> listBank = new ArrayList<>();//银行卡列表
    private WalletAdapter walletAdapter;
    //    private List<WalletManageBean> response;
    private boolean haveWeChat = false;
    private boolean haveAlipay = false;
    private boolean haveBank = false;

    @Override
    public int getViewId() {
        return R.layout.activity_wallet_manage;
    }

    @Override
    public void onResume() {
        super.onResume();
        onPageStart(title);
        checkVerify();
    }

    @Override
    public void onPause() {
        super.onPause();
        onPageEnd(title);
    }

    @Override
    protected void onStart() {
        super.onStart();
        geBankListInfo(true);
    }

    @Override
    public void afterViews() {
        title = MyApp.getLanguageString(getContext(), R.string.wallet_title);
        commTitle.init(title);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnablePureScrollMode(false);
        refreshLayout.setEnableOverScrollDrag(true);//是否启用越界拖动（仿苹果效果）1.0.4
        walletAdapter = new WalletAdapter(getContext(), listBank, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(walletAdapter);
        refreshLayout.setOnRefreshListener(refreshLayout -> geBankListInfo(false));
    }

    private void geBankListInfo(boolean showDialog) {
        new UserImpl(new ZZNetCallBack<List<WalletManageBean>>(this, WalletManageBean.class) {
            @Override
            public void onSuccess(List<WalletManageBean> response) {
                if (null != getActivity() && null != response) {
                    haveWeChat = false;
                    haveAlipay = false;
                    haveBank = false;
//                    WalletManageActivity.this.response = response;
//                    listBank = WalletManageBean.setWalletList(WalletManageActivity.this, response);
                    listBank = response;
                    boolean show = listBank.size() < NumberConstants.PAYMENT_MAX_NUM;
                    if (null != slClickAdd) {
                        slClickAdd.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
//                    if (null != tvAddpayTip) {
//                        tvAddpayTip.setVisibility(show ? View.GONE : View.VISIBLE);
//                    }
                    if (null != walletAdapter) {
                        walletAdapter.changeDataUi(listBank);
                    }
                    for (int i = 0; i < listBank.size(); i++) {
                        WalletManageBean walletManageBean = listBank.get(i);
                        int type = walletManageBean.getType();
                        setHaveWallet(type, true);
                    }
                    String bankListString = new Gson().toJson(listBank);
                    SPUtil.setStringValue(SPUtil.PAYMENT_MODE_LIST, bankListString);
//                    checkVerify();
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (null != refreshLayout) {
                    refreshLayout.finishRefresh();
                }
            }
        }.setNeedDialog(showDialog)).getPayModeList();
    }

    /**
     * 校验实名
     */
    private void checkVerify() {
        if (!Config.THREE_STRING.equals(SPUtil.getStringValue(SPUtil.VERIFY_IDENTITYSTATUS))) {
            MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.VerifyDialog,
                    new AuthenDialogData(v -> {
                        startClass(MyApp.getLanguageString(getContext(), R.string.VerifyFaceActivity));
                        WalletManageActivity.this.finish();
                    }, v -> {
                        WalletManageActivity.this.finish();
                    }));
        }
    }


    private void setHaveWallet(int type, boolean have) {
        switch (type) {
            case ZERO:
                haveWeChat = have;
                break;
            case ONE:
                haveAlipay = have;
                break;
            case TWO:
                haveBank = have;
                break;
        }
    }

    @Override
    public void delete(int position, WalletManageBean item) {
        String name = "";
        ////0:微信;1:支付宝;2,银行卡
        if(item.getType() == 2){
            name = UIUtils.getString(R.string.Bank_name);
        } else {
            name =  item.getBankName();
        }
        String content = MyApp.getLanguageString(getContext(), R.string.wallet_Unbind) + name;
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.CommonDialog,
                new CommDialogData(MyApp.getLanguageString(getContext(), R.string.Share_tip), content, MyApp.getLanguageString(getContext(), R.string.Share_cancel),
                        MyApp.getLanguageString(getContext(), R.string.Share_certain), null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new FundImpl(new ZZNetCallBack<Object>(WalletManageActivity.this, Object.class) {
                            @Override
                            public void onSuccess(Object response) {
                                setHaveWallet(item.getType(), false);
                                listBank.remove(position);
                                if (null != walletAdapter) {
                                    walletAdapter.changeDataUi(listBank);
                                }
                                geBankListInfo(true);
                            }
                        }.setNeedDialog(true)).deleteWallet(item.getId());
                    }
                }));
    }

    @OnClick({R.id.sl_click_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sl_click_add:
                HashMap<String, String> params = new HashMap<>();
//                params.put(StringConstants.WALLET_WECHAT, haveWeChat ? ONE_STRING : ZERO_STRING);
//                params.put(StringConstants.WALLET_ALIPAY, haveAlipay ? ONE_STRING : ZERO_STRING);
//                List<WalletManageBean> bankList = BaseNetUtil.parseFromJson(SPUtil.getStringValue(SPUtil.PAYMENT_MODE_LIST), WalletManageBean.class);
//                //如果支付方式数量 大于等于6 则不能再添加银行
//                if (bankList != null && bankList.size() >= 6) {
//                    params.put(StringConstants.WALLET_BANK, ONE_STRING);
//                } else {
//                    params.put(StringConstants.WALLET_BANK, ZERO_STRING);
//                }
                startClass(MyApp.getLanguageString(getContext(), R.string.AddWalletManageActivity), params);
                break;
        }
    }


}
