package com.eb.sc.tcprequest;

import android.util.Log;

import com.eb.sc.bean.Params;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

/**
 * Created by Administrator on 2017/7/28.
 */

public class ClientKeepAliveMessageFactoryImp implements KeepAliveMessageFactory {
    @Override
    public boolean isRequest(IoSession ioSession, Object o) {
        if (o instanceof String && o.equals(Params.SEND)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isResponse(IoSession ioSession, Object o) {
        if (o instanceof String && o.equals(Params.RECEIVE)) {
            return true;
        }
        return false;
    }

    @Override
    public Object getRequest(IoSession ioSession) {
        Log.e("ClientKeepAliveMessageF", "发送心跳...");
        return Params.SEND;
    }

    @Override
    public Object getResponse(IoSession ioSession, Object o) {
        Log.e("dawns", "getResponse: "+o.toString());
        return null;
    }
}
