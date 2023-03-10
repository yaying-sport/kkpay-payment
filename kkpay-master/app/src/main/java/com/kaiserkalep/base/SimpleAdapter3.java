package com.kaiserkalep.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kaiserkalep.interfaces.OnItemClickListener;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 简单适配器
 *
 * @Auther: Administrator
 * @Date: 2019/4/19 0019 13:11
 * @Description:
 */
public abstract class SimpleAdapter3<T> extends RecyclerView.Adapter<ViewHolder> {

    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mData;
    protected final int mItemLayoutId;
    protected OnItemClickListener listener;


    public SimpleAdapter3(Context context, List<T> mData, int itemLayoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mData = mData;
        this.mItemLayoutId = itemLayoutId;
    }

    public SimpleAdapter3(Context context, List<T> mData, int itemLayoutId, OnItemClickListener listener) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mData = mData;
        this.mItemLayoutId = itemLayoutId;
        this.listener = listener;
    }

    public void changeDataUi(List<T> content) {
        this.mData = content;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ViewHolder.createViewHolder(parent, mItemLayoutId);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        convert(holder, mData.get(position), position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public abstract void convert(ViewHolder holder, T item, int position);
}
