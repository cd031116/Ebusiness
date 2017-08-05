package com.eb.sc.tcprequest;

/**
 * Created by Administrator on 2017/8/5.
 */

public interface TcpResponse {
    void receivedMessage(String trim);
    void breakConnect();
}
