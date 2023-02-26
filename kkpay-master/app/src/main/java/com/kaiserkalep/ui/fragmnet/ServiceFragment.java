package com.kaiserkalep.ui.fragmnet;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.PageBaseFragment;
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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 客服
 *
 * @Auther: Jack
 * @Date: 2020/8/19 09:15
 * @Description:
 */
public class ServiceFragment extends PageBaseFragment {

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refresh;
    @BindView(R.id.comm_title)
    CommTitle commTitle;
    @BindView(R.id.tarl_content)
    RelativeLayout tarlContent;
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

    @Override
    public int getViewId() {
        return R.layout.fragment_service;
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        commTitle.init(MyApp.getLanguageString(getContext(), R.string.service_ttitle), false);
        commTitle.setTvTitleColor(MyApp.getMyColor(R.color.view_title_title_service));
        commTitle.setTvFunctionColor(MyApp.getMyColor(R.color.view_title_function_service));
        commTitle.setMyBackgroundColor(MyApp.getMyColor(R.color.service_top_bg));
        commTitle.setViewLine(false);
        TextView tvFunction = commTitle.getTvFunction();
        UIUtils.setMargins(getActivity(), tvFunction, 0, 0, 5, 0);
        UIUtils.setMargins2(commTitle, 0, UIUtils.getStatusBarHeight(MyApp.getContext()), 0, 0);
        tarlContent.setVisibility(View.VISIBLE);
        serviceUrl = NetWorkCaCheUtils.getServiceUrl();
        if (serviceUrl != null) {
            setData(serviceUrl);
        }
        refresh.setEnablePureScrollMode(false);
        refresh.setEnableOverScrollDrag(true);//是否启用越界拖动（仿苹果效果）1.0.4
        refresh.setOnRefreshListener(refreshLayout -> refresh(serviceUrl == null));

    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        refresh(serviceUrl == null);
    }

    @Override
    protected void onUserDoubleVisible() {
        super.onUserDoubleVisible();
    }

    public void refresh(boolean needDialog) {
        GlideUtil.loadCircleImage("", ivServicePortrait, R.drawable.icon_default_photo);
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

    /**
     * 设置友盟开始页面统计
     */
    public void setUmengStartPageStatistics() {
        onPageStart(MyApp.getLanguageString(getContext(), R.string.main_tab_service));
    }

    /**
     * 设置友盟停止页面统计
     */
    public void setUmengEndPageStatistics() {
        onPageEnd(MyApp.getLanguageString(getContext(), R.string.main_tab_service));
    }
}
