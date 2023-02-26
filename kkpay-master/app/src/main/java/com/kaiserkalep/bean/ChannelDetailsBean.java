package com.kaiserkalep.bean;

import java.io.Serializable;

public class ChannelDetailsBean implements Serializable {

    private String id;
    private String name;
    private String icon;
    /**
     * 是否选中
     */
    private boolean isSelect;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public ChannelDetailsBean setSelect(boolean select) {
        isSelect = select;
        return this;
    }
}
