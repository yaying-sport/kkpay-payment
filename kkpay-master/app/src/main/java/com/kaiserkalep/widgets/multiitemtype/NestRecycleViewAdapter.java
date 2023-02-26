package com.kaiserkalep.widgets.multiitemtype;

import android.util.SparseArray;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 多类型item 子View为recycleView保存 LayoutManager及Adapter
 *
 * @Auther: Jack
 * @Date: 2019/12/24 14:04
 * @Description:
 */
public class NestRecycleViewAdapter {

    protected SparseArray<RecyclerView> recyclerViews;
    protected SparseArray<RecyclerView.Adapter> adapters;
    protected SparseArray<RecyclerView.LayoutManager> layoutManagers;

    public NestRecycleViewAdapter() {
        recyclerViews = new SparseArray<>();
        adapters = new SparseArray<>();
        layoutManagers = new SparseArray<>();
    }
}
