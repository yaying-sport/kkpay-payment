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
import com.kaiserkalep.bean.MyOrderBean;
import com.kaiserkalep.bean.MySellBean;
import com.kaiserkalep.bean.WalletManageBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.util.List;

import static com.kaiserkalep.constants.Config.FOUR_STRING;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.OWN_ONE_STRING;
import static com.kaiserkalep.constants.Config.THREE;
import static com.kaiserkalep.constants.Config.THREE_STRING;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.ZERO;

/**
 * 优惠活动列表
 *
 * @Auther: Jack
 * @Date: 2020/8/15 16:03
 * @Description:
 */
public class MyOrderListAdapter extends SimpleAdapter3<MyOrderBean.RowsDTO> {

    private SucceedCallBackListener<MyOrderBean.RowsDTO> listener;
    private String userId;

    public MyOrderListAdapter(Context context, List<MyOrderBean.RowsDTO> mData, SucceedCallBackListener<MyOrderBean.RowsDTO> listener) {
        super(context, mData, R.layout.items_myorder_list);
        this.listener = listener;
        userId = SPUtil.getUserId();
    }

    @Override
    public void changeDataUi(List<MyOrderBean.RowsDTO> content) {
        super.changeDataUi(content);
        userId = SPUtil.getUserId();
    }

    @Override
    public void convert(ViewHolder holder, MyOrderBean.RowsDTO item, int position) {
        if (null != item) {
            holder.setVisible(R.id.sl_content, true);
            holder.setText(R.id.tv_item_id, item.getUsername());
            String updateTime = item.getUpdateTime();
            if (CommonUtils.StringNotNull(updateTime)) {
                holder.setText(R.id.tv_item_date_time, updateTime);
            } else {
                holder.setText(R.id.tv_item_date_time, item.getCreateTime());
            }
            holder.setText(R.id.tv_item_money, Config.subOneAndDot(item.getAmount()));
            TextView tvItemOrderno = holder.getView(R.id.tv_item_orderno);
            boolean equals = userId.equals(item.getMemberId());
            if (equals) {
//                GlideUtil.loadLocalImage(R.drawable.icon_order_buy, holder.getView(R.id.iv_sell_buy));
                holder.setTextColorRes(R.id.tv_item_money, R.color.colorPrimary);
                tvItemOrderno.setText("");
                tvItemOrderno.setVisibility(View.GONE);
            } else {
//                GlideUtil.loadLocalImage(R.drawable.icon_order_sell, holder.getView(R.id.iv_sell_buy));
                holder.setTextColorRes(R.id.tv_item_money, R.color.wallet_record_out_text_color);
                tvItemOrderno.setText(MyApp.getLanguageString(mContext, R.string.mysell_orderno) + ":" + item.getSellOrderNo());
                tvItemOrderno.setVisibility(View.VISIBLE);
            }
            String receiveType = item.getPayType();
            LinearLayout llSaleItemStateIcon = holder.getView(R.id.ll_item_state_icon);
            if (llSaleItemStateIcon.getChildCount() > ZERO) {
                llSaleItemStateIcon.removeAllViews();
            }
            if (CommonUtils.StringNotNull(receiveType)) {
                String[] split = receiveType.split(StringConstants.comma);
                int wheight = UIUtils.dip2px(17);
                int Margin = UIUtils.dip2px(4);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(wheight, wheight);
                layoutParams.rightMargin = Margin;
                for (int i = 0; i < split.length; i++) {
                    int payStrIcon = WalletManageBean.getPayStrIcon(split[i]);
                    if (Config.EGATIVE_ONE != payStrIcon) {
                        ImageView imageView = new ImageView(mContext);
                        imageView.setImageResource(payStrIcon);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        imageView.setLayoutParams(layoutParams);
                        llSaleItemStateIcon.addView(imageView);
                    }
                }
            }
            TextView tvSaleItemState = holder.getView(R.id.tv_item_state);
            ImageView ivItemLogo = holder.getView(R.id.iv_item_logo);
            String status = item.getStatus();
            switch (status) {
                case Config.ZERO_STRING:
                case Config.ONE_STRING:
                case Config.TWO_STRING://进行中
                    GlideUtil.loadLocalImage(R.drawable.iccon_sale_item_jingxing, ivItemLogo);
                    tvSaleItemState.setBackgroundResource(R.drawable.shape_sale_item_state_jx);
                    tvSaleItemState.setText(MyApp.getLanguageString(mContext, R.string.sale_jinxingzhong));
                    break;
                case Config.THREE_STRING:
                    GlideUtil.loadLocalImage(R.drawable.icon_sale_item_pause, ivItemLogo);
                    tvSaleItemState.setBackgroundResource(R.drawable.shape_sale_item_state_zt);
                    tvSaleItemState.setText(MyApp.getLanguageString(mContext, R.string.sale_zantinging));
                    break;
                case Config.FOUR_STRING:
                    GlideUtil.loadLocalImage(R.drawable.icon_sale_item_finish, ivItemLogo);
                    tvSaleItemState.setBackgroundResource(R.drawable.shape_sale_item_state_wc);
                    tvSaleItemState.setText(MyApp.getLanguageString(mContext, R.string.sale_yiwancheng));
                    break;
                case Config.OWN_ONE_STRING:
                    GlideUtil.loadLocalImage(R.drawable.icon_sale_item_cancle, ivItemLogo);
                    tvSaleItemState.setBackgroundResource(R.drawable.shape_sale_item_state_cancel);
                    tvSaleItemState.setText(MyApp.getLanguageString(mContext, R.string.sale_yiquxiao));
                    break;
                default:
                    GlideUtil.loadLocalImage(R.drawable.icon_transparent_bg, ivItemLogo);
                    tvSaleItemState.setBackgroundResource(R.drawable.shape_sale_item_state_nocolor);
                    tvSaleItemState.setText("");
                    break;
            }
            holder.setOnClickListener(R.id.ll_parent_view, v -> {
                if (null != listener) {
                    listener.succeedCallBack(item);
                }
            });
        } else {
            holder.setVisible(R.id.sl_content, false);
            holder.setOnClickListener(R.id.ll_parent_view, null);
        }
    }
}
