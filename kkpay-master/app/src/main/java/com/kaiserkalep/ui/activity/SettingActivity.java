package com.kaiserkalep.ui.activity;

import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.L;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.ClientInfo;
import com.kaiserkalep.bean.UserData;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.eventbus.TokenTimeOutEvent;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.MyDataManager;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置页面
 *
 * @Auther: Jack
 * @Date: 2020/12/18 20:50
 * @Description:
 */
public class SettingActivity extends ZZActivity {


    @BindView(R.id.sl_login)
    ShadowLayout slLogin;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_cache_size)
    TextView tvCacheSize;

    private String title = "";
    private String internalCache;

    @Override
    public int getViewId() {
        return R.layout.activity_setting;
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
        title = MyApp.getLanguageString(getContext(), R.string.setting_title);
        commTitle.init(title);
        internalCache = MyDataManager.getInternalCache(getContext());
        tvCacheSize.setText(internalCache);
        tvVersion.setText(MyApp.getLanguageString(getContext(), R.string.app_version) + "：\tv" + ClientInfo.VER);
    }

    @OnClick({R.id.ll_click_about, R.id.ll_click_notify, R.id.ll_click_clear_cache, R.id.sl_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_click_about:
                startClass(MyApp.getLanguageString(getContext(), R.string.AboutActivity));
                break;
            case R.id.ll_click_notify:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                Config.toSettingNotify(SettingActivity.this);
                break;
            case R.id.ll_click_clear_cache:
                showDialog();
                MyDataManager.deleteAllCache(getContext());
                SPUtil.clearCache();
                internalCache = MyDataManager.DEFAULT_FILE_SIZE;
                tvCacheSize.postDelayed(() -> {
                    tvCacheSize.setText(MyDataManager.DEFAULT_FILE_SIZE);
                    closeDialog();
                    toast(MyApp.getLanguageString(getContext(), R.string.settingactivity_ClearedSuccess));
                }, 2000);
                break;
            case R.id.sl_login:
                loginOut();
                break;
        }
    }

    /**
     * 退出登录
     */
    private void loginOut() {
        new UserImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                closeDialog();
                EventBusUtil.post(new TokenTimeOutEvent(true, false, ""));
                SettingActivity.this.finish();
            }
        }.setNeedDialog(true)).loginOut();
    }
}
