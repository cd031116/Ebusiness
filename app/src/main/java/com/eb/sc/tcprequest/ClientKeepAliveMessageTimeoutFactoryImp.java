package com.eb.sc.tcprequest;

import android.util.Log;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;

/**
 * Created by lyj on 2017/7/28.
 */

public class ClientKeepAliveMessageTimeoutFactoryImp implements KeepAliveRequestTimeoutHandler {
    @Override
    public void keepAliveRequestTimedOut(KeepAliveFilter keepAliveFilter, IoSession ioSession) throws Exception {
        Log.e("ClientKeepAliveMessageT", "心跳超时");
    }
}