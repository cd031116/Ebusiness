package com.eb.sc.sdk.eventbus;

/**
 * Created by lyj on 2017/8/2.//检测网络
 */

public class NetEvent{
    private boolean httpnet;

    public boolean isConnect() {
        return httpnet;
    }

    public  NetEvent(boolean connect) {
        this.httpnet = connect;
    }

}
