package com.kaiserkalep.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kaiserkalep.R;
import com.kaiserkalep.bean.FeedDetailBean;

import java.util.List;

import static com.kaiserkalep.constants.Config.ZERO;


public class FeedDetailAdapter extends RecyclerView.Adapter<BaseChatViewHolder> {

    private static final int MESSAGE_TYPE_TXET = 0;//正常聊天消息

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<FeedDetailBean> mData;

    public FeedDetailAdapter(Context context, List<FeedDetailBean> mdata) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mData = mdata;
    }

    @Override
    public BaseChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FeedTextPresenter(mContext, mLayoutInflater.inflate(R.layout.items_feed_msg_text, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseChatViewHolder holder, int position) {
        FeedDetailBean chatRoomData = mData.get(position);
        holder.setMessage(chatRoomData);
    }

    @Override
    public int getItemViewType(int position) {
        return MESSAGE_TYPE_TXET;
    }

    @Override
    public int getItemCount() {
        return null == mData ? ZERO : mData.size();
    }


}
