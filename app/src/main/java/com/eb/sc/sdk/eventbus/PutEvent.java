package com.eb.sc.sdk.eventbus;

/**
 * Created by lyj on 2017/8/7.
 */

public class PutEvent {
    private int code;  //1- 身份证  2- 二维码
    private String strs;


    public  PutEvent(int code,String strs){
        this.code = code;
        this.strs=strs;
    }

    public int getCode() {
        return code;
    }

    public String getStrs() {
        return strs;
    }
}
