package com.kaiserkalep.ui.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.PagerBaseAdapter;
import com.kaiserkalep.adapter.ZZNavigatorAdapter;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.ui.fragmnet.MyOrderListFragment;
import com.kaiserkalep.ui.fragmnet.SaleListFragment;
import com.kaiserkalep.widgets.CommonNavigator;
import com.kaiserkalep.widgets.ViewPagerFixed;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.kaiserkalep.constants.Config.ZERO;

/**
 * 我的订单
 */
public class MyOrderActivity extends ZZActivity {

    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.view_page)
    ViewPagerFixed viewpager;

    private String title = "";
    private CommonNavigator commonNavigator;
    private List<String> classify = new ArrayList<>();
    private List<PageBaseFragment> mDataList = new ArrayList<>();

    @Override
    public int getViewId() {
        return R.layout.activity_myorder;
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
        title = MyApp.getLanguageString(getContext(), R.string.my_order_title);
        commTitle.init(title);
        commonNavigator = new CommonNavigator(this);
        commonNavigator.setNeedSetTitleWidth(false);
        setListTitle();
        initData();
    }

    private void setListTitle() {
        if (classify.size() > ZERO) {
            classify.clear();
        }
        classify.add(MyApp.getLanguageString(getContext(), R.string.sale_jinxingzhong));
        classify.add(MyApp.getLanguageString(getContext(), R.string.sale_yiwancheng));
        classify.add(MyApp.getLanguageString(getContext(), R.string.sale_yiquxiao));
        classify.add(MyApp.getLanguageString(getContext(), R.string.sale_zantinging));
        classify.add(MyApp.getLanguageString(getContext(), R.string.Share_All));
    }

    /**
     * 初始化magicIndicator 和viewpager
     */
    private void initData() {
        Context context = getContext();
        if (null == context) {
            return;
        }
        for (int i = 0; i < classify.size(); i++) {
            MyOrderListFragment orderListFragment = new MyOrderListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(StringConstants.SALE_INDEX_ID, i);
            orderListFragment.setArguments(bundle);
            mDataList.add(orderListFragment);
        }

        PagerBaseAdapter pagerBaseAdapter = new PagerBaseAdapter(getSupportFragmentManager(), mDataList);
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
                ((MyOrderListFragment) mDataList.get(position)).onUserViewVisible();
            }
        });
        viewpager.post(() -> {
            viewpager.setCurrentItem(ZERO, false);
            ((MyOrderListFragment) mDataList.get(ZERO)).onUserViewVisible();
        });
    }

}
