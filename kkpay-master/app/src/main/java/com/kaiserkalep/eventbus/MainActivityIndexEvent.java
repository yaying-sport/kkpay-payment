package com.kaiserkalep.eventbus;

public class MainActivityIndexEvent {

    public int index;
    public int indexSecond;

    public MainActivityIndexEvent(int index, int indexSecond) {
        this.index = index;
        this.indexSecond = indexSecond;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndexSecond() {
        return indexSecond;
    }

    public void setIndexSecond(int indexSecond) {
        this.indexSecond = indexSecond;
    }
}
