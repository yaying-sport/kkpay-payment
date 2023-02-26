package com.kaiserkalep.bean;

import com.kaiserkalep.constants.StringConstants;

/**
 * @Auther: Administrator
 * @Date: 2019/3/14 0014 19:49
 * @Description:
 */
public class ResultData<T> {

    private T data;
    private String msg;
    private String code;//状态码 200：成功；306 ：效验手机号；500：失败；401:跳转到登录；403：地区限制 ;506存款下单银行卡姓名不一致


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isVerifyNewPass() {
        return StringConstants.REQUEST_SUCCEED_TO_VERIFYNEW.equals(code);
    }

    public boolean isOk() {
        return StringConstants.REQUEST_SUCCEED.equals(code);
    }

    public boolean isLoginOut() {
        return StringConstants.REQUEST_LOGIN_OUT.equals(code);
    }
}
