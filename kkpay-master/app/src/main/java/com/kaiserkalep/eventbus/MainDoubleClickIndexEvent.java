package com.kaiserkalep.eventbus;

/**
 *  首页双击
 *
 * @Auther: Administrator
 * @Date: 2019/4/11 0011 13:01
 * @Description:
 */
public class MainDoubleClickIndexEvent {

    public int currentIndex;

    public MainDoubleClickIndexEvent() {
    }

    public MainDoubleClickIndexEvent(int currentIndex) {
        this.currentIndex = currentIndex;
    }

}
