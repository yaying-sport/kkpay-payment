package com.kaiserkalep.ui.fragmnet;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;

import com.kaiserkalep.adapter.PagerBaseAdapter;
import com.kaiserkalep.adapter.ZZNavigatorAdapter;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.bean.OrderStatusNoticeBean;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.eventbus.MainDoubleClickIndexEvent;
import com.kaiserkalep.ui.activity.MainActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.StatusBarUtil;
import com.kaiserkalep.widgets.CommonNavigator;
import com.kaiserkalep.widgets.ViewPagerFixed;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.kaiserkalep.constants.Config.ZERO;

/**
 * @Auther: Jack
 * @Date: 2020/8/11 17:26
 * @Description:
 */
public class SaleFragment extends PageBaseFragment {
    @BindView(R.id.view_title_p)
    View viewTitleP;
    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.view_page)
    ViewPagerFixed viewpager;
    private CommonNavigator commonNavigator;

    private List<String> classify = new ArrayList<>();
    private List<PageBaseFragment> mDataList = new ArrayList<>();
    private String title;
    private int position = ZERO;

    /**
     * 设置友盟开始页面统计
     */
    public void setUmengStartPageStatistics() {
        onPageStart(title);
    }

    /**
     * 设置友盟停止页面统计
     */
    public void setUmengEndPageStatistics() {
        onPageEnd(title);
    }

    @Override
    public int getViewId() {
        return R.layout.fragment_sale;
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        Context context = getContext();
        if (context != null) {
            title = MyApp.getLanguageString(context, R.string.main_tab_guadan);
            commonNavigator = new CommonNavigator(context);
            commonNavigator.setNeedSetTitleWidth(false);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewTitleP.getLayoutParams();
            layoutParams.height = StatusBarUtil.getStatusBarHeight(context);
            viewTitleP.setLayoutParams(layoutParams);
        }
        setListTitle();
    }

    private void setListTitle() {
        if (classify.size() > ZERO) {
            classify.clear();
        }
        classify.add(MyApp.getLanguageString(getContext(), R.string.sale_jinxingzhong));
        classify.add(MyApp.getLanguageString(getContext(), R.string.sale_yiwancheng));
        classify.add(MyApp.getLanguageString(getContext(), R.string.sale_yiquxiao));
        classify.add(MyApp.getLanguageString(getContext(), R.string.Share_All));
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        initData();
    }

    @Override
    protected void onUserDoubleVisible() {
        super.onUserDoubleVisible();
        if (null != mDataList) {
            if (mDataList.size() > position && null !=  mDataList.get(position)) {
                mDataList.get(position).onUserViewVisible();
            }
        }
    }

    @Override
    public void setIndex(int index) {
        super.setIndex(index);
        if (null != viewpager && null != mDataList && index >= ZERO && index < mDataList.size()) {
            PageBaseFragment pageBaseFragment = mDataList.get(index);
            if (pageBaseFragment != null) {
                pageBaseFragment.scrollTop();
                viewpager.setCurrentItem(index, false);
            }
        }
    }


    /**
     * 取消订单时
     * 通知我的挂单/我的订单
     * 下次显示时自动刷新
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(OrderStatusNoticeBean event) {
        if (null != mDataList) {
            for (int i = ZERO; i < mDataList.size(); i++) {
                PageBaseFragment pageBaseFragment = mDataList.get(i);
                if (pageBaseFragment != null) {
                    pageBaseFragment.setNextShowRefresh(true);
                }
            }
        }
    }

    /**
     * 初始化magicIndicator 和viewpager
     */
    private void initData() {
        Context context = getContext();
        if (!isAdded() || null == context) {
            return;
        }
        for (int i = 0; i < classify.size(); i++) {
            SaleListFragment saleListFragment = new SaleListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(StringConstants.SALE_INDEX_ID, i);
            saleListFragment.setArguments(bundle);
            mDataList.add(saleListFragment);
        }

        PagerBaseAdapter pagerBaseAdapter = new PagerBaseAdapter(getChildFragmentManager(), mDataList);
        viewpager.setAdapter(pagerBaseAdapter);
        viewpager.setOffscreenPageLimit(mDataList.size());

        ZZNavigatorAdapter navigatorAdapter = new ZZNavigatorAdapter(context, classify, viewpager);
        commonNavigator.setAdapter(navigatorAdapter);
        commonNavigator.setAdjustMode(true);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);

        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                SaleFragment.this.position = position;
                ((SaleListFragment) mDataList.get(position)).onUserViewVisible();
            }
        });
        viewpager.post(() -> {
            viewpager.setCurrentItem(position, false);
            ((SaleListFragment) mDataList.get(position)).onUserViewVisible();
        });
    }


    /**
     * 点击tab刷新
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MainDoubleClickIndexEvent event) {
        if (event != null && (event.currentIndex == MainActivity.TAB_SALE)) {
            if (CommonUtils.ListNotNull(mDataList) && viewpager != null) {
                PageBaseFragment pageBaseFragment = mDataList.get(viewpager.getCurrentItem());//获取当前可见view
                if (pageBaseFragment != null) {
                    pageBaseFragment.scrollTop();
                }
            }
        }
    }
}
