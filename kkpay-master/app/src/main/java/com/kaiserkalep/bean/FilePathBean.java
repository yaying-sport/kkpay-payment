package com.kaiserkalep.bean;

import android.text.TextUtils;

import java.io.Serializable;

public class FilePathBean implements Serializable {

    private String path;

    public String getPath() {
        if (TextUtils.isEmpty(path)){
            return "";
        }
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
