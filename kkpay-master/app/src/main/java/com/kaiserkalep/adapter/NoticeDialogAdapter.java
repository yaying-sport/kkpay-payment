package com.kaiserkalep.adapter;

import android.content.Context;

import com.kaiserkalep.R;
import com.kaiserkalep.base.SimpleAdapter3;
import com.kaiserkalep.base.ViewHolder;
import com.kaiserkalep.bean.NoticeDialogData;
import com.kaiserkalep.utils.CommonUtils;

import java.util.List;


/**
 * 重要公告弹框
 *
 * @Auther: Jack
 * @Date: 2020/12/22 18:33
 * @Description:
 */
public class NoticeDialogAdapter extends SimpleAdapter3<NoticeDialogData.DialogList> {


    public NoticeDialogAdapter(Context context, List<NoticeDialogData.DialogList> mData) {
        super(context, mData, R.layout.items_notice_dialog);
    }

    @Override
    public void convert(ViewHolder holder, NoticeDialogData.DialogList item, int position) {
        holder.setText(R.id.tv_title, CommonUtils.StringNotNull(item.getNoticeTitle()) ? item.getNoticeTitle() : "");
        holder.setText(R.id.tv_content, CommonUtils.StringNotNull(item.getNoticeContent()) ? item.getNoticeContent() : "");
    }
}
