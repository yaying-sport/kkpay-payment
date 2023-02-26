package com.kaiserkalep.adapter;

import android.content.Context;
import android.view.View;

import com.kaiserkalep.R;
import com.kaiserkalep.base.SimpleAdapter3;
import com.kaiserkalep.base.ViewHolder;
import com.kaiserkalep.bean.SysNoticeListData;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.interfaces.OnItemClickListener;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.DateUtils;

import java.util.List;

/**
 * 消息通知,公告
 *
 * @Auther: Jack
 * @Date: 2020/8/18 10:33
 * @Description:
 */
public class MessageNotifyAdapter extends SimpleAdapter3<SysNoticeListData.RowsBean> {


    private boolean isEdit;
    private int type;

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public MessageNotifyAdapter(Context context, List<SysNoticeListData.RowsBean> mData, int type, OnItemClickListener listener) {
        super(context, mData, R.layout.item_message_notify, listener);
        this.type = type;
    }

    @Override
    public void convert(ViewHolder holder, SysNoticeListData.RowsBean item, int position) {
        holder.setVisible(R.id.iv_top_line, Config.ZERO == position);
        holder.setImageResource(R.id.iv_type, CommonUtils.getMsgTypeStatus(item.getNoticeType()));
        holder.setText(R.id.tv_title, item.getNoticeTitle());
        holder.setText(R.id.tv_content, item.getNoticeContent());
        holder.setVisible(R.id.view_unread, item.getStatus() == 1 ? View.INVISIBLE : View.VISIBLE);
        holder.setText(R.id.tv_time, item.getCreateTime());
//        holder.setVisible(R.id.view_line, position != mData.size() - 1);
        if (isEdit) {
            holder.setImageResource(R.id.iv_check_status, item.isSelect() ? R.drawable.icon_edit_msg_check : R.drawable.icon_edit_msg_uncheck);
            holder.setVisible(R.id.iv_check_status, true);
        } else {
            holder.setImageResource(R.id.iv_check_status, R.drawable.icon_edit_msg_uncheck);
            holder.setVisible(R.id.iv_check_status, false);
        }
        holder.getConvertView().setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(v, position);
            }
        });
    }


}
