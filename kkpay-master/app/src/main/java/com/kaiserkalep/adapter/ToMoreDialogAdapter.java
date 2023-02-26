package com.kaiserkalep.adapter;

import android.content.Context;
import android.view.View;

import com.kaiserkalep.R;
import com.kaiserkalep.base.SimpleAdapter3;
import com.kaiserkalep.base.ViewHolder;
import com.kaiserkalep.bean.NoticeDialogData;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.utils.CommonUtils;

import java.util.List;


/**
 * 重要公告弹框
 *
 * @Auther: Jack
 * @Date: 2020/12/22 18:33
 * @Description:
 */
public class ToMoreDialogAdapter extends SimpleAdapter3<String> {

    public ToMoreDialogAdapter(Context context, List<String> mData) {
        super(context, mData, R.layout.items_tomore_dialog);
    }

    @Override
    public void convert(ViewHolder holder, String item, int position) {
        holder.setText(R.id.tv_title, item);
        holder.setOnClickListener(R.id.ll_content_view, v -> {
            if (CommonUtils.StringNotNull(item)) {
                CommonUtils.startBrowser(mContext, item);
            }
        });
    }
}
