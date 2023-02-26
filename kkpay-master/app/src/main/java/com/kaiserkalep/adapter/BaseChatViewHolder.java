package com.kaiserkalep.adapter;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.kaiserkalep.bean.FeedDetailBean;

/**
 * base Adapter
 */
public class BaseChatViewHolder extends RecyclerView.ViewHolder {

    private Context bContext;

    public BaseChatViewHolder(Context context, View view) {
        super(view);
        this.bContext = context;

    }

    public void setMessage(FeedDetailBean data) {

    }

}
