package com.kaiserkalep.bean;

import com.kaiserkalep.utils.CommonUtils;

import java.io.Serializable;

public class AmountInfo implements Serializable {

    private String canSellAmount;//可售
    private String sellAmount;//卖单余额
    private String sellingAmount;//交易中
    private String buyAmount;//不可售金额

    public String getCanSellAmount() {
        if (CommonUtils.StringNotNull(canSellAmount)) {
            return canSellAmount;
        }
        return "0";
    }

    public void setCanSellAmount(String canSellAmount) {
        this.canSellAmount = canSellAmount;
    }

    public String getSellAmount() {
        if (CommonUtils.StringNotNull(sellAmount)) {
            return sellAmount;
        }
        return "0";
    }

    public void setSellAmount(String sellAmount) {
        this.sellAmount = sellAmount;
    }

    public String getSellingAmount() {
        if (CommonUtils.StringNotNull(sellingAmount)) {
            return sellingAmount;
        }
        return "0";
    }

    public void setSellingAmount(String sellingAmount) {
        this.sellingAmount = sellingAmount;
    }

    public String getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(String buyAmount) {
        this.buyAmount = buyAmount;
    }

    @Override
    public String toString() {
        return "AmountInfo{" +
                "canSellAmount='" + canSellAmount + '\'' +
                ", sellAmount='" + sellAmount + '\'' +
                ", sellingAmount='" + sellingAmount + '\'' +
                ", buyAmount='" + buyAmount + '\'' +
                '}';
    }
}
