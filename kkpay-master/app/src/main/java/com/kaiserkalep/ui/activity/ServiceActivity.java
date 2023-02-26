package com.kaiserkalep.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.ServiceUrlData;
import com.kaiserkalep.net.impl.WebsiteImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.NetWorkCaCheUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.CommTitle;
import com.kaiserkalep.widgets.MySmartRefreshLayout;
import com.kaiserkalep.widgets.glide.GlideUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class ServiceActivity extends ZZActivity {

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refresh;
    @BindView(R.id.iv_bar)
    ImageView ivBar;
    @BindView(R.id.comm_title)
    CommTitle commTitle;
    @BindView(R.id.iv_service_portrait)
    ImageView ivServicePortrait;
    @BindView(R.id.tv_welcome)
    TextView tvWelcome;
    @BindView(R.id.iv_main_service)
    ImageView ivMainService;
    @BindView(R.id.iv_second_service)
    ImageView ivSecondService;
    private String main;
    private String secondary;
    private ServiceUrlData serviceUrl;
    private String title;

    @Override
    public void onResume() {
        super.onResume();
        onPageStart(title);
        setStatusBarTextColor(1);
    }

    @Override
    public void onPause() {
        super.onPause();
        onPageEnd(title);
    }

    @Override
    public int getViewId() {
        return R.layout.activity_service;
    }

    @Override
    public void afterViews() {
        int statusBarHeight = UIUtils.getStatusBarHeight(MyApp.getContext());
        UIUtils.setHeight(ivBar, statusBarHeight);
        title = MyApp.getLanguageString(getContext(), R.string.service_ttitle);
        commTitle.init(title, "", true, "", 0, null);
        commTitle.setTvTitleColor(MyApp.getMyColor(R.color.view_title_title_service));
        commTitle.setMyBackgroundColor(MyApp.getMyColor(R.color.service_top_bg));
        commTitle.setViewLine(false);
        serviceUrl = NetWorkCaCheUtils.getServiceUrl();
        if (serviceUrl != null) {
            setData(serviceUrl);
        }
        refresh.setOnRefreshListener(refreshLayout -> refresh(serviceUrl == null));
        refresh(true);
    }

    public void refresh(boolean needDialog) {
        GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(SPUtil.getUserAvatar()), ivServicePortrait, R.drawable.icon_default_photo);
        tvWelcome.setText(SPUtil.getNickName());
        getData(needDialog);
    }

    private void getData(boolean needDialog) {
        new WebsiteImpl(new ZZNetCallBack<ServiceUrlData>(this, ServiceUrlData.class) {
            @Override
            public void onSuccess(ServiceUrlData response) {
                setData(response);
                NetWorkCaCheUtils.setServiceUrl(response);
            }

            @Override
            public void onError(String msg, String code) {
                if (needDialog) {
                    setData(null);
                } else {
                    super.onError(msg, code);
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (refresh != null) {
                    refresh.finishRefresh();
                }
            }
        }.setNeedDialog(needDialog)).getServiceUrl();
    }

    private void setData(ServiceUrlData response) {
        if (response == null) {
            response = new ServiceUrlData();
        }
        main = response.getMain();
        secondary = response.getSecondary();
//
        if (ivMainService != null) {
            ivMainService.setVisibility(CommonUtils.StringNotNull(main) ? View.VISIBLE : View.GONE);
        }
        if (ivSecondService != null) {
            ivSecondService.setVisibility(CommonUtils.StringNotNull(secondary) ? View.VISIBLE : View.GONE);
        }
    }


    private void showService(View view, boolean isShow, long delay) {
        MyApp.postDelayed(() -> {
            view.setVisibility(View.VISIBLE);//先设置显示
            AnimatorSet animatorSet = new AnimatorSet();//组合动画
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", isShow ? 0 : 1f, isShow ? 1.2f : 1.2f, isShow ? 1 : 0);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", isShow ? 0 : 1f, isShow ? 1.2f : 1.2f, isShow ? 1 : 0);
            animatorSet.setDuration(800);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
            animatorSet.start();
        }, delay);
    }


    @OnClick({R.id.iv_main_service, R.id.iv_second_service})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_main_service:
                if (!CommonUtils.StringNotNull(main)) {
                    toast("");
                    return;
                }
                MyApp.postDelayed(() -> gotoWebView(main, MyApp.getLanguageString(getContext(), R.string.main_tab_service), false), MyApp.getMyInteger(R.integer.chick_touch_anim));
                break;
            case R.id.iv_second_service:
                if (!CommonUtils.StringNotNull(secondary)) {
                    toast("");
                    return;
                }
                MyApp.postDelayed(() -> gotoWebView(secondary, MyApp.getLanguageString(getContext(), R.string.main_tab_service), false),
                        MyApp.getMyInteger(R.integer.chick_touch_anim));
                break;
        }
    }
}
