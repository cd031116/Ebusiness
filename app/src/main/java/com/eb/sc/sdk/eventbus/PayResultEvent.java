package com.eb.sc.sdk.eventbus;

/**
 * Created by Administrator on 2017/9/4.
 */

public class PayResultEvent {
    private String  strs;

    public String getStrs() {
        return strs;
    }

    public  PayResultEvent(String strs) {
        this.strs = strs;
    }
}
