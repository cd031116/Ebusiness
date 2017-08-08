package com.eb.sc.sdk.eventbus;

/**
 * Created by lyj on 2017/8/2. //长连接是否在
 */

public class TongbuEvent {
    private String responseStr;
    private String isResponse;
    private String code;
    public String getIsResponse() {
        return isResponse;
    }

    public String getCode() {
        return code;
    }

    public String getResponseStr() {
        return responseStr;
    }

    public TongbuEvent(String responseStr, String isResponse,String code) {
        this.responseStr = responseStr;
        this.isResponse = isResponse;
        this.code=code;
    }
}
