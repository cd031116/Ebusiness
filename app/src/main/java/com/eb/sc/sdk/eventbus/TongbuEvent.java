package com.eb.sc.sdk.eventbus;

/**
 * Created by lyj on 2017/8/2. //长连接是否在
 */

public class TongbuEvent {
    private String responseStr;
    private boolean isResponse;

    public boolean isResponse() {
        return isResponse;
    }

    public String getResponse() {
        return responseStr;
    }

    public TongbuEvent(String responseStr,boolean isResponse) {
        this.responseStr = responseStr;
        this.isResponse = isResponse;
    }
}
