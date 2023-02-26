package com.kaiserkalep.bean;

import com.kaiserkalep.utils.CommonUtils;

import java.io.Serializable;

public class StatusBean implements Serializable {

    private String maintainStatue;//0：未维护 1：维护中
    private String content;//维护内容
    private String beginTime;//开始时间
    private String endTime;//结束时间

    public String getMaintainStatue() {
        return maintainStatue;
    }

    public void setMaintainStatue(String maintainStatue) {
        this.maintainStatue = maintainStatue;
    }

    public String getContent() {
        if (CommonUtils.StringNotNull(content)) {
            return content;
        }
        return "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBeginTime() {
        if (CommonUtils.StringNotNull(beginTime)) {
            return beginTime;
        }
        return "";
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        if (CommonUtils.StringNotNull(endTime)) {
            return endTime;
        }
        return "";
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
