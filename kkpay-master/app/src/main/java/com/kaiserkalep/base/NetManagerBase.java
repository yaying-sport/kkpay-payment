package com.kaiserkalep.base;

/**
 * @Auther: Administrator
 * @Date: 2019/3/19 0019 19:59
 * @Description:
 */
public class NetManagerBase {

    protected CallbackBase callBack;

    /**
     * 新接口list每页条数
     */
    protected String listRows = "10";

    public NetManagerBase(CallbackBase callBack) {
        this.callBack = callBack;
    }
}
