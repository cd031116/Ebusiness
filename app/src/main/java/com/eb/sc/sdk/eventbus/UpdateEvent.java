package com.eb.sc.sdk.eventbus;

/**
 * Created by Administrator on 2017/8/10.
 */

public class UpdateEvent {
    private  String code;

    public String getCode() {
        return code;
    }
    public UpdateEvent(String code){
        this.code=code;
    }
}
