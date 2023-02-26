package com.kaiserkalep.bean;

import java.io.Serializable;

public class AgentFormBean implements Serializable {

    private String orderNo;
    private String amount;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
