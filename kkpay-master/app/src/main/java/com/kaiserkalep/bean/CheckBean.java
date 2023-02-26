package com.kaiserkalep.bean;

import android.text.TextUtils;

import java.io.Serializable;

public class CheckBean implements Serializable {

    private String code;

    public String getCode() {
        if (TextUtils.isEmpty(code)) {
            return "";
        }
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
