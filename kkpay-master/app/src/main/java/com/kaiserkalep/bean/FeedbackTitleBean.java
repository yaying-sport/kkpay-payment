package com.kaiserkalep.bean;

import java.io.Serializable;

public class FeedbackTitleBean implements Serializable {

    private String dictValue;
    private String dictLabel;
    private String dictSort;
    /**
     * 是否选中
     */
    private boolean isSelect;

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    public String getDictLabel() {
        return dictLabel;
    }

    public void setDictLabel(String dictLabel) {
        this.dictLabel = dictLabel;
    }

    public String getDictSort() {
        return dictSort;
    }

    public void setDictSort(String dictSort) {
        this.dictSort = dictSort;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public FeedbackTitleBean setSelect(boolean select) {
        isSelect = select;
        return this;
    }
}
