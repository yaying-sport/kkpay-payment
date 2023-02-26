package com.kaiserkalep.bean;

import com.kaiserkalep.utils.CommonUtils;

import java.io.Serializable;
import java.util.List;

public class MyOrderBean implements Serializable {
    private int total;
    private String tranceId;
    private List<RowsDTO> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTranceId() {
        return tranceId;
    }

    public void setTranceId(String tranceId) {
        this.tranceId = tranceId;
    }

    public List<RowsDTO> getRows() {
        return rows;
    }

    public void setRows(List<RowsDTO> rows) {
        this.rows = rows;
    }

    public static class RowsDTO {
        private String amount;
        private String memberId;
        private String username;
        private String payType;
        private String orderNo;
        private String sellOrderNo;
        private String createTime;//订单创建时间
        private String updateTime;//更新时间（为空时，显示createTime）
        private String status;//0买家下单，1卖家确认，2买家转账，3，暂停，4卖家打币，-1取消(当前状态，页面展示下一个状态)

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getSellOrderNo() {
            return sellOrderNo;
        }

        public void setSellOrderNo(String sellOrderNo) {
            this.sellOrderNo = sellOrderNo;
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
}
