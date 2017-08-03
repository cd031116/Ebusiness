package com.eb.sc.tcprequest;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * Created by lyj on 2017/7/28.
 */

public class PushService extends IntentService {
    public PushService() {
        super("PushService");
    }

    private static NioSocketConnector connector;
    private static ConnectFuture connectFuture;
    private static IoSession ioSession;

    @Override
    protected void onHandleIntent(Intent intent){
        Log.e("dawns", "onHandleIntent: " );
        PushManager.getInstance().connect(getApplicationContext());

    }

}
