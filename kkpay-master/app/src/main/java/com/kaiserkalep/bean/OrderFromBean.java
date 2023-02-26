package com.kaiserkalep.bean;

import com.kaiserkalep.utils.CommonUtils;

import java.io.Serializable;

public class OrderFromBean implements Serializable {

    private String orderNo;
    private String amount;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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
}
