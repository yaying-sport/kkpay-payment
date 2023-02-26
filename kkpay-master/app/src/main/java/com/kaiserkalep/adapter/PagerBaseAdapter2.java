package com.kaiserkalep.adapter;


import com.kaiserkalep.base.ActivityBase;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.utils.CommonUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * viewPage配合使用
 *
 * @auther: Administrator
 * @date: 2019/3/29 0029 17:51
 */
public class PagerBaseAdapter2 extends FragmentStateAdapter {

    private List<PageBaseFragment> fragments;

    public PagerBaseAdapter2(ActivityBase activityBase, List<PageBaseFragment> fragmentList) {
        super(activityBase);
        fragments = fragmentList;
    }

//    @Override
//    public int getCount() {
//        return fragments == null ? 0 : fragments.size();
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        return CommonUtils.ListNotNull(fragments) ? fragments.get(position) : null;
//    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CommonUtils.ListNotNull(fragments) ? fragments.get(position) : null;
    }

    @Override
    public int getItemCount() {
        return fragments == null ? 0 : fragments.size();
    }
}
