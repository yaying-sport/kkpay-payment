package com.kaiserkalep.bean;

import java.io.Serializable;

public class AboutBean implements Serializable {

    private String id;//1:用户注册须知 2：买家用户须知 3.卖家用户须知
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
