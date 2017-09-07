package com.eb.sc.sdk.eventbus;

/**
 * Created by Administrator on 2017/9/7.
 */

public class LoginEvent {
    private String user_id;

    public String getUser_id() {
        return user_id;
    }

    public  LoginEvent(String user_id) {
        this.user_id = user_id;
    }
}
