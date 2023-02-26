package com.kaiserkalep.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.PagerBaseAdapter;
import com.kaiserkalep.adapter.ZZNavigatorAdapter;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.bean.MerchantBean;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.ui.fragmnet.MerchantFragment;

import com.kaiserkalep.widgets.CleanEditTextView;
import com.kaiserkalep.widgets.CommonNavigator;
import com.kaiserkalep.widgets.ViewPagerFixed;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.kaiserkalep.constants.Config.ZERO;

/**
 * 商户通讯录
 */
public class MerchantDirectoryActivity extends ZZActivity implements SucceedCallBackListener<MerchantBean.RowsDTO>, TextWatcher {

    @BindView(R.id.et_sh_account)
    CleanEditTextView etShAccount;
    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.view_page)
    ViewPagerFixed viewpager;

    private String title = "";
    private int position = ZERO;
    private List<String> classify = new ArrayList<>();
    private List<PageBaseFragment> mDataList = new ArrayList<>();
    private CommonNavigator commonNavigator;
    private String searchStr = "";

    @Override
    public int getViewId() {
        return R.layout.activity_merchantdirectory;
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
        title = MyApp.getLanguageString(getContext(), R.string.managesh_address_book_title);
        commTitle.init(title, "", "", R.drawable.icon_addsh_right, this::addSh);
        commonNavigator = new CommonNavigator(this);
        commonNavigator.setNeedSetTitleWidth(false);
        setListTitle();
        initData();
    }

    /**
     * 跳转添加商户
     *
     * @param view
     */
    private void addSh(View view) {
        checkLogin(R.string.AddManageShActivity);
        finish();
    }

    private void setListTitle() {
        etShAccount.addTextChangedListener(this);
        if (classify.size() > ZERO) {
            classify.clear();
        }
        classify.add(MyApp.getLanguageString(getContext(), R.string.Share_Recent));
        classify.add(MyApp.getLanguageString(getContext(), R.string.Share_All));
    }

    @Override
    public void succeedCallBack(@Nullable MerchantBean.RowsDTO o) {
        Log.e("answer", "succeedCallBack:" + o.toString());
        if (null != o) {
            setResult(RESULT_OK, new Intent().putExtra(StringConstants.DATA, new Gson().toJson(o)));
            finish();
        }
    }

    /**
     * 初始化magicIndicator 和viewpager
     */
    private void initData() {
        for (int i = ZERO; i < classify.size(); i++) {
            MerchantFragment merchantfragment = new MerchantFragment().setListener(this);
            Bundle bundle = new Bundle();
            bundle.putInt(StringConstants.SALE_INDEX_ID, i);
            merchantfragment.setArguments(bundle);
            mDataList.add(merchantfragment);
        }

        PagerBaseAdapter pagerBaseAdapter = new PagerBaseAdapter(getSupportFragmentManager(), mDataList);
        viewpager.setAdapter(pagerBaseAdapter);
        viewpager.setOffscreenPageLimit(mDataList.size());

        ZZNavigatorAdapter navigatorAdapter = new ZZNavigatorAdapter(this, classify, viewpager);
        commonNavigator.setAdapter(navigatorAdapter);
        commonNavigator.setAdjustMode(true);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);

        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                MerchantDirectoryActivity.this.position = position;
                ((MerchantFragment) mDataList.get(position)).onUserViewVisible();
            }
        });
        viewpager.post(() -> {
            viewpager.setCurrentItem(position, false);
            ((MerchantFragment) mDataList.get(position)).onUserViewVisible();
        });
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
        for (int i = ZERO; i < mDataList.size(); i++) {
            ((MerchantFragment) mDataList.get(i)).setSearchStr(searchStr);
        }
    }

    public String getSearchStr() {
        return searchStr;
    }
}
