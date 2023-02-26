package com.kaiserkalep.bean;

import android.content.Context;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.utils.CommonUtils;
import com.tencent.msdk.dns.core.IStatisticsMerge;

import java.io.Serializable;
import java.util.List;

public class MySellBean implements Serializable {
    private int total;
    private List<RowsDTO> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }


    public List<RowsDTO> getRows() {
        return rows;
    }

    public void setRows(List<RowsDTO> rows) {
        this.rows = rows;
    }

    public static class RowsDTO {
        private String avatar;
        private String amount;
        private String amountDeal;
        private String amountCancel;
        private String amountFreeze;
        private String receiveType;
        private String canSplit;
        private String orderNo;
        private String createTime;//订单创建时间

        private String updateTime;//更新时间（为空时，显示createTime）
        private String status;

        public Object getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAmount() {
            if (CommonUtils.StringNotNull(amount)) {
                return amount;
            }
            return "0";
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getAmountDeal() {
            if (CommonUtils.StringNotNull(amountDeal)) {
                return amountDeal;
            }
            return "0";
        }

        public void setAmountDeal(String amountDeal) {
            this.amountDeal = amountDeal;
        }

        public String getAmountCancel() {
            if (CommonUtils.StringNotNull(amountCancel)) {
                return amountCancel;
            }
            return "0";
        }

        public void setAmountCancel(String amountCancel) {
            this.amountCancel = amountCancel;
        }

        public String getAmountFreeze() {
            if (CommonUtils.StringNotNull(amountFreeze)) {
                return amountFreeze;
            }
            return "0";
        }

        public void setAmountFreeze(String amountFreeze) {
            this.amountFreeze = amountFreeze;
        }

        public String getReceiveType() {
            return receiveType;
        }

        public void setReceiveType(String receiveType) {
            this.receiveType = receiveType;
        }

        public String getCanSplit() {
            return canSplit;
        }

        public void setCanSplit(String canSplit) {
            this.canSplit = canSplit;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getCreateTime() {
            if (CommonUtils.StringNotNull(createTime)) {
                return createTime;
            }
            return "";
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            if (CommonUtils.StringNotNull(updateTime)) {
                return updateTime;
            }
            return "";
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static String getOrderStatusTitle(Context context, String type) {
        if (CommonUtils.StringNotNull(type)) {
            switch (type) {
                case Config.ZERO_STRING://未销售
                case Config.ONE_STRING://销售部分
                    return MyApp.getLanguageString(context, R.string.sale_jinxingzhong);
                case Config.TWO_STRING://完成
                    return MyApp.getLanguageString(context, R.string.sale_yiwancheng);
                case Config.OWN_ONE_STRING://取消
                case Config.OWN_TWO_STRING://部分取消
                    return MyApp.getLanguageString(context, R.string.sale_yiquxiao);
                default:
                    return "";
            }
        } else {
            return "";
        }
    }

    public static String getDealOrCancelTitle(Context context, String type) {
        if (CommonUtils.StringNotNull(type)) {
            switch (type) {
                case Config.ZERO_STRING://未销售
                case Config.ONE_STRING://销售部分
                    return MyApp.getLanguageString(context, R.string.sell_details_moneytwo_sale_tip);
                case Config.TWO_STRING://完成
                    return MyApp.getLanguageString(context, R.string.sell_details_moneytwo_deal_tip);
                case Config.OWN_ONE_STRING://取消
                case Config.OWN_TWO_STRING://部分取消
                    return MyApp.getLanguageString(context, R.string.sell_details_moneytwo_cancel_tip);
                default:
                    return "";
            }
        } else {
            return "";
        }
    }
}
