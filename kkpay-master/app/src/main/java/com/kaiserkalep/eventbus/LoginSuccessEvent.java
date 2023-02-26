package com.kaiserkalep.eventbus;

import com.kaiserkalep.bean.UserData;

/**
 * @Auther: Jack
 * @Date: 2020/8/13 15:54
 * @Description:
 */
public class LoginSuccessEvent {

    public UserData userData;

    private LoginSuccessEvent() {
    }

    public LoginSuccessEvent(UserData userData) {
        this.userData = userData;
    }
}
