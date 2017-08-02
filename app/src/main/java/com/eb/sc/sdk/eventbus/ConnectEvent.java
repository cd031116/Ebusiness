package com.eb.sc.sdk.eventbus;

/**
 * Created by lyj on 2017/8/2. //长连接是否在
 */

public class ConnectEvent {
    private boolean connect;

    public boolean isConnect() {
        return connect;
    }

    public  ConnectEvent(boolean connect) {
        this.connect = connect;
    }
}
