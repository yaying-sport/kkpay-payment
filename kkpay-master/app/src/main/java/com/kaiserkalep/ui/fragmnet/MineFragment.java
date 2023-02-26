package com.kaiserkalep.ui.fragmnet;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.ClientInfo;
import com.kaiserkalep.bean.FeedBackUnReadbean;
import com.kaiserkalep.eventbus.MainDoubleClickIndexEvent;
import com.kaiserkalep.net.impl.SysImpl;
import com.kaiserkalep.ui.activity.MainActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.utils.VersionUpdateUtils;

import com.kaiserkalep.widgets.MySmartRefreshLayout;
import com.kaiserkalep.widgets.glide.GlideUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ELEVEN;
import static com.kaiserkalep.constants.Config.SEVEN;
import static com.kaiserkalep.constants.Config.THREE;
import static com.kaiserkalep.constants.Config.ZERO;

/**
 * 我的
 *
 * @Auther: Jack
 * @Date: 2020/8/11 17:26
 * @Description:
 */
public class MineFragment extends PageBaseFragment {

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;
    @BindView(R.id.iv_bar)
    ImageView ivBar;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.iv_mineuser_photo)
    ImageView ivMineuserPhoto;
    @BindView(R.id.tv_mine_phone)
    TextView tvMinePhone;
    @BindView(R.id.tv_mine_address)
    TextView tvMineAddress;

    @BindView(R.id.v_feedback_icon)
    View vFeedbackIcon;
    @BindView(R.id.versionUpdateIcon)
    View versionUpdateIcon;
    @BindView(R.id.tv_current_version)
    TextView tvCurrentVersion;

    private boolean isCanFeedbackUnread = false;

    @Override
    public int getViewId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        int statusBarHeight = UIUtils.getStatusBarHeight(MyApp.getContext());
        UIUtils.setHeight(ivBar, statusBarHeight);
        refreshLayout.setEnablePureScrollMode(false);
        refreshLayout.setEnableOverScrollDrag(true);//是否启用越界拖动（仿苹果效果）1.0.4
        refreshLayout.setOnRefreshListener(refreshLayout -> refresh(false));
        tvCurrentVersion.setText(MyApp.getLanguageString(getContext(), R.string.app_Currentversion) + "v" + ClientInfo.VER);
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        refresh(true);
        getVersionList(false);
    }

    @Override
    protected void onUserDoubleVisible() {
        super.onUserDoubleVisible();
        refresh(false);
    }

    public void refresh(boolean needDialog) {
        setUserData();
        if (!isCanFeedbackUnread) {
            sysFeedbackUnread();//获取意见反馈未读数量
        }

    }

    /**
     * 设置头像/昵称/手机号
     */
    private void setUserData() {
        if (null != ivMineuserPhoto) {
            String userAvatar = SPUtil.getUserAvatar();
            GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(userAvatar), ivMineuserPhoto, R.drawable.icon_default_photo);
        }
        if (null != tvMineAddress) {
            String userWalletAddress = SPUtil.getUserWalletAddress();
            tvMineAddress.setText(userWalletAddress);
        }
        if (null != tvNickname) {
            tvNickname.setText(SPUtil.getNickName());
        }
        if (null != tvMinePhone) {
            String userPhone = SPUtil.getUserPhone();
            if (CommonUtils.StringNotNull(userPhone)) {
                if (userPhone.length() == ELEVEN) {
                    String account = userPhone.substring(ZERO, THREE) + "****" + userPhone.substring(SEVEN, userPhone.length());
                    tvMinePhone.setText(account);
                }
            }
        }
        if (null != refreshLayout) {
            refreshLayout.finishRefresh();
        }
    }


    /**
     * 请求意反馈未读消息
     */
    private void sysFeedbackUnread() {
        if (!isLogin()) {
            return;
        }
        isCanFeedbackUnread = true;
        new SysImpl(new ZZNetCallBack<FeedBackUnReadbean>(this, FeedBackUnReadbean.class) {
            @Override
            public void onSuccess(FeedBackUnReadbean response) {
                if (response != null && null != vFeedbackIcon) {
                    try {
                        int integer = Integer.parseInt(response.getTotal());
                        vFeedbackIcon.setVisibility(integer > ZERO ? View.VISIBLE : View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        vFeedbackIcon.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                isCanFeedbackUnread = false;
            }
        }.setNeedDialog(false).setNeedToast(false)).sysFeedbackUnreadNum();
    }

    /**
     * 获取更新
     */
    private void getVersionList(boolean isToast) {
        VersionUpdateUtils.initVersionUpdateUtils(this, new VersionUpdateUtils.VersionCallback() {
            @Override
            public void toast() {
                if (isToast) {
                    MineFragment.this.toast(MyApp.getLanguageString(getContext(), R.string.settingactivity_LatestVersion));
                }
            }

            @Override
            public void redDot(String version) {
                if (!version.equals(SPUtil.getStringValue(SPUtil.VERSION_STATUS))) {
                    versionUpdateIcon.setVisibility(View.VISIBLE);
                }
                if (CommonUtils.StringNotNull(version)) {
                    SPUtil.setStringValue(SPUtil.VERSION_STATUS, version);
                }
            }
        }, isToast, !isToast);
    }

    /**
     * 点击tab刷新
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MainDoubleClickIndexEvent event) {
        if (event != null && (event.currentIndex == MainActivity.TAB_MINE)) {
            if (refreshLayout != null) {
                refreshLayout.autoRefresh();
                refreshLayout.finishRefresh();
            }
        }
    }

    /**
     * 设置友盟开始页面统计
     */
    public void setUmengStartPageStatistics() {
        onPageStart(MyApp.getLanguageString(getContext(), R.string.main_tab_mine));
    }

    /**
     * 设置友盟停止页面统计
     */
    public void setUmengEndPageStatistics() {
        onPageEnd(MyApp.getLanguageString(getContext(), R.string.main_tab_mine));
    }

    @OnClick({R.id.ll_edit_nickname_view, R.id.sl_usermine_photo, R.id.tv_mine_phone, R.id.iv_mine_setting, R.id.iv_mine_copy_icon, R.id.ll_click_check_update, R.id.ll_click_update_loginpass,
            R.id.ll_click_update_paypass, R.id.ll_click_feedback, R.id.ll_click_Verified})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_edit_nickname_view:
            case R.id.sl_usermine_photo:
            case R.id.tv_mine_phone:
                checkLogin(R.string.EditorUserDataActivity);
                break;
            case R.id.iv_mine_copy_icon:
                if (null != tvMineAddress) {
                    String string = tvMineAddress.getText().toString();
                    CommonUtils.copyText(getContext(), string);
                }
                break;
            case R.id.iv_mine_setting:
                checkLogin(R.string.SettingActivity);
                break;
            case R.id.ll_click_check_update://版本更新检测
                if (CommonUtils.isFastClick()) {
                    return;
                }
                versionUpdateIcon.setVisibility(View.INVISIBLE);
                getVersionList(true);
                break;
            case R.id.ll_click_update_loginpass:
                checkLogin(R.string.ModifyLoginPassActivity);
                break;
            case R.id.ll_click_update_paypass:
                checkLogin(R.string.ModifyPayPassActivity);
                break;
            case R.id.ll_click_feedback:
                checkLogin(R.string.FeedbackActivity);
                break;
            case R.id.ll_click_Verified:
                checkLogin(R.string.VerifyFaceActivity);
                break;
        }
    }
}
