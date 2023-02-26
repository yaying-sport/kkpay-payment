package com.kaiserkalep.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.SimpleAdapter3;
import com.kaiserkalep.base.ViewHolder;
import com.kaiserkalep.bean.MySellBean;
import com.kaiserkalep.bean.WalletManageBean;
import com.kaiserkalep.bean.WalletRecordBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.ui.activity.SellCoinCenterActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.util.HashMap;
import java.util.List;

import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.ZERO;

/**
 * 我的挂单列表
 *
 * @Auther: Jack
 * @Date: 2020/8/15 16:03
 * @Description:
 */
public class SaleListAdapter extends SimpleAdapter3<MySellBean.RowsDTO> {

    private SucceedCallBackListener<MySellBean.RowsDTO> listener;
    private String nickName;

    public SaleListAdapter(Context context, List<MySellBean.RowsDTO> mData, SucceedCallBackListener<MySellBean.RowsDTO> listener) {
        super(context, mData, R.layout.items_sale_list);
        this.listener = listener;
        nickName = SPUtil.getNickName();
    }

    @Override
    public void changeDataUi(List<MySellBean.RowsDTO> content) {
        super.changeDataUi(content);
        nickName = SPUtil.getNickName();
    }

    @Override
    public void convert(ViewHolder holder, MySellBean.RowsDTO item, int position) {
        if (null != item) {
            holder.setVisible(R.id.sl_content, true);
            holder.setText(R.id.tv_item_id, nickName);
            holder.setText(R.id.tv_item_orderno, MyApp.getLanguageString(mContext, R.string.mysell_orderno) + ":" + item.getOrderNo());
            String updateTime = item.getUpdateTime();
            if (CommonUtils.StringNotNull(updateTime)) {
                holder.setText(R.id.tv_item_date_time, updateTime);
            } else {
                holder.setText(R.id.tv_item_date_time, item.getCreateTime());
            }
            holder.setText(R.id.tv_item_money, Config.subOneAndDot(item.getAmount()));

            String receiveType = item.getReceiveType();
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
            String status = item.getStatus();
            TextView tvSaleItemState = holder.getView(R.id.tv_item_state);
            ImageView ivItemLogo = holder.getView(R.id.iv_item_logo);
            switch (status) {
                case Config.ZERO_STRING://未销售
                case Config.ONE_STRING://销售部分
                    GlideUtil.loadLocalImage(R.drawable.iccon_sale_item_jingxing, ivItemLogo);
                    tvSaleItemState.setBackgroundResource(R.drawable.shape_sale_item_state_jx);
                    tvSaleItemState.setText(MyApp.getLanguageString(mContext, R.string.sale_jinxingzhong));
                    break;
                case Config.TWO_STRING://完成
                    GlideUtil.loadLocalImage(R.drawable.icon_sale_item_finish, ivItemLogo);
                    tvSaleItemState.setBackgroundResource(R.drawable.shape_sale_item_state_wc);
                    tvSaleItemState.setText(MyApp.getLanguageString(mContext, R.string.sale_yiwancheng));
                    break;
                case Config.OWN_ONE_STRING://取消
                case Config.OWN_TWO_STRING://部分取消
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
