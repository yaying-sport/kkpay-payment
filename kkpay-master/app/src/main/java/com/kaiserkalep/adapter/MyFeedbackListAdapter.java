package com.kaiserkalep.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.SimpleAdapter3;
import com.kaiserkalep.base.ViewHolder;
import com.kaiserkalep.bean.MyFeedbackBean;
import com.kaiserkalep.bean.MySellBean;
import com.kaiserkalep.bean.WalletManageBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.util.List;

import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 优惠活动列表
 *
 * @Auther: Jack
 * @Date: 2020/8/15 16:03
 * @Description:
 */
public class MyFeedbackListAdapter extends SimpleAdapter3<MyFeedbackBean.RowsDTO> {

    private SucceedCallBackListener<MyFeedbackBean.RowsDTO> listener;

    public MyFeedbackListAdapter(Context context, List<MyFeedbackBean.RowsDTO> mData, SucceedCallBackListener<MyFeedbackBean.RowsDTO> listener) {
        super(context, mData, R.layout.items_myfeedback_list);
        this.listener = listener;
    }

    @Override
    public void convert(ViewHolder holder, MyFeedbackBean.RowsDTO item, int position) {
        if (null != item) {
            holder.setVisible(R.id.sl_content, true);
            holder.setText(R.id.tv_item_title, item.getTitleValue());
            holder.setText(R.id.tv_item_time, item.getCreateTime());
            holder.setText(R.id.tv_item_content, item.getContent());
            holder.setVisible(R.id.iv_red, ZERO_STRING.equals(item.getReadStatus()) ? View.VISIBLE : View.INVISIBLE);
            holder.setOnClickListener(R.id.ll_parent_view, v -> {
                if (null != listener && null != item) {
                    item.setReadStatus(ONE_STRING);
                    listener.succeedCallBack(item);
                }
            });

        } else {
            holder.setVisible(R.id.sl_content, false);
            holder.setOnClickListener(R.id.ll_parent_view, null);
        }
    }
}
