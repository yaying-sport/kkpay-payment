package com.kaiserkalep.bean;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.io.Serializable;
import java.util.List;

public class WalletRecordBean implements Serializable {

    private int total;
    private List<WalletRecordBean.RowsDTO> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<WalletRecordBean.RowsDTO> getRows() {
        return rows;
    }

    public void setRows(List<WalletRecordBean.RowsDTO> rows) {
        this.rows = rows;
    }

    public static class RowsDTO {

        private String title;
        private String desc;
        private String pageDomain;
        private String beginTime;
        private String endTime;
        private String searchValue;
        private String createBy;
        private String createTime;
        private String updateBy;
        private String updateTime;
        private String remark;

        private String id;
        private String memberId;
        private String phone;
        private String billNo;
        private String orderNo;
        private String agentId;
        private String amountOriginal;
        private String amountOrder;
        private String fee;
        private String amount;
        private String billType;
        private int status;
        private String createTimestamp;
        private String amountType;//交易类型，0为负，1为正
        private String tradeType;//1不可售，2可售
        private String agentName;//商户名称

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }


        public String getDesc() {
            if (null == desc && TextUtils.isEmpty(desc)) {
                return "";
            }
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPageDomain() {
            return pageDomain;
        }

        public void setPageDomain(String pageDomain) {
            this.pageDomain = pageDomain;
        }

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getSearchValue() {
            return searchValue;
        }

        public void setSearchValue(String searchValue) {
            this.searchValue = searchValue;
        }

        public String getCreateBy() {
            return createBy;
        }

        public void setCreateBy(String createBy) {
            this.createBy = createBy;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateBy() {
            return updateBy;
        }

        public void setUpdateBy(String updateBy) {
            this.updateBy = updateBy;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getBillNo() {
            return billNo;
        }

        public void setBillNo(String billNo) {
            this.billNo = billNo;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public String getAmountOriginal() {
            return amountOriginal;
        }

        public void setAmountOriginal(String amountOriginal) {
            this.amountOriginal = amountOriginal;
        }

        public String getAmountOrder() {
            return amountOrder;
        }

        public void setAmountOrder(String amountOrder) {
            this.amountOrder = amountOrder;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getBillType() {
            return billType;
        }

        public void setBillType(String billType) {
            this.billType = billType;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getCreateTimestamp() {
            return createTimestamp;
        }

        public void setCreateTimestamp(String createTimestamp) {
            this.createTimestamp = createTimestamp;
        }

        public String getAmountType() {
            return amountType;
        }

        public void setAmountType(String amountType) {
            this.amountType = amountType;
        }

        public String getTradeType() {
            return tradeType;
        }

        public void setTradeType(String tradeType) {
            this.tradeType = tradeType;
        }

        public String getAgentName() {
            return agentName;
        }

        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }
    }
}
