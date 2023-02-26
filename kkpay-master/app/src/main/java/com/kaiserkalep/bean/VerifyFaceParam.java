package com.kaiserkalep.bean;

import java.io.Serializable;

public class VerifyFaceParam implements Serializable {

    String bizSeqNo;//"22052510001184433421255228129439",
    String transactionTime;// "20220525212552"
    String orderNo;// "714106900345520128",
    String faceId;// "tx0c8890f8b4afe72ef65aa2a4b1b0c2",
    boolean success;// false,


    public String getBizSeqNo() {
        return bizSeqNo;
    }

    public void setBizSeqNo(String bizSeqNo) {
        this.bizSeqNo = bizSeqNo;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }
}
