package com.kaiserkalep.bean;

import com.kaiserkalep.utils.CommonUtils;

import java.io.Serializable;
import java.util.List;

public class BuyListBean implements Serializable {
    private int total;
    private List<BuyListBean.RowsDTO> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<BuyListBean.RowsDTO> getRows() {
        return rows;
    }

    public void setRows(List<BuyListBean.RowsDTO> rows) {
        this.rows = rows;
    }

    public static class RowsDTO {
        private String avatar;
        private String memberId;
        private String amount;
        private String amountDeal;
        private String amountCancel;
        private String receiveType;
        private String canSplit;//0,不支持   1,支持
        private String orderNo;
        private String createTime;
        private String status;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
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
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }


}
