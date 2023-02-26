package com.kaiserkalep.eventbus;

/**
 * 前后台切换
 *
 * @Auther: Jack
 * @Date: 2019/11/21 18:37
 * @Description:
 */
public class AppFrontBackEvent {

    public boolean isFront;//前后台切换 true,后台切前台  false,前台切后台

    public AppFrontBackEvent(boolean isFront) {
        this.isFront = isFront;
    }
}
