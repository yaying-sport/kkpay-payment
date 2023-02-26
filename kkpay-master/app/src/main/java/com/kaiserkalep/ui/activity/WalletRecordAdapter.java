package com.kaiserkalep.ui.activity;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.SimpleAdapter3;
import com.kaiserkalep.base.ViewHolder;

import com.kaiserkalep.bean.WalletManageBean;
import com.kaiserkalep.bean.WalletRecordBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.util.HashMap;
import java.util.List;

import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

public class WalletRecordAdapter extends SimpleAdapter3<WalletRecordBean.RowsDTO> {

    private SucceedCallBackListener<WalletRecordBean.RowsDTO> listener;

    public WalletRecordAdapter(Context context, List<WalletRecordBean.RowsDTO> mData, SucceedCallBackListener<WalletRecordBean.RowsDTO> listener) {
        super(context, mData, R.layout.items_wallet_record_list);
        this.listener = listener;
    }

    @Override
    public void convert(ViewHolder holder, WalletRecordBean.RowsDTO item, int position) {
        if (null != item) {
            holder.setVisible(R.id.sl_content, true);
            holder.setText(R.id.tv_item_date_time, item.getCreateTime());
            TextView tvItemTitle = holder.getView(R.id.tv_item_title);
            ImageView ivItemLogo = holder.getView(R.id.iv_item_logo);
            String billType = item.getBillType();
            String amountType = item.getAmountType();
            if (ZERO_STRING.equals(amountType)) {
                GlideUtil.loadLocalImage(R.drawable.icon_money_out, ivItemLogo);
                holder.setTextColorRes(R.id.tv_item_money, R.color.wallet_record_out_text_color);
                holder.setText(R.id.tv_item_money, "-" + Config.subOneAndDot(item.getAmountOrder()));
            } else if (ONE_STRING.equals(amountType)) {
                GlideUtil.loadLocalImage(R.drawable.icon_money_in, ivItemLogo);
                holder.setTextColorRes(R.id.tv_item_money, R.color.colorPrimary);
                holder.setText(R.id.tv_item_money, "+" + Config.subOneAndDot(item.getAmountOrder()));
            } else {
                switch (billType) {
                    case Config.ZERO_STRING://买币
                    case Config.THREE_STRING://从商户提现加币
                    case Config.FIVE_STRING://手动充币
                        GlideUtil.loadLocalImage(R.drawable.icon_default_photo, ivItemLogo);
                        holder.setTextColorRes(R.id.tv_item_money, R.color.colorPrimary);
                        holder.setText(R.id.tv_item_money, "+" + Config.subOneAndDot(item.getAmountOrder()));
                        break;
                    case Config.ONE_STRING://卖币
                    case Config.TWO_STRING://向商户充值扣币
                    case Config.SIX_STRING://手动扣币
                        GlideUtil.loadLocalImage(R.drawable.icon_money_out, ivItemLogo);
                        holder.setTextColorRes(R.id.tv_item_money, R.color.wallet_record_out_text_color);
                        holder.setText(R.id.tv_item_money, "-" + Config.subOneAndDot(item.getAmountOrder()));
                        break;
                    default:
                        GlideUtil.loadLocalImage(R.drawable.icon_transparent_bg, ivItemLogo);
                        holder.setText(R.id.tv_item_money, "");
                        break;
                }
            }
            String desc = item.getDesc();
            tvItemTitle.setText(desc);
            item.setTitle(desc);

            holder.setOnClickListener(R.id.ll_item_parent_view, v -> {
                if (null != listener) {
                    listener.succeedCallBack(item);
                }
            });
        } else {
            holder.setVisible(R.id.sl_content, false);
            holder.setOnClickListener(R.id.ll_item_parent_view, null);
        }
    }
}
