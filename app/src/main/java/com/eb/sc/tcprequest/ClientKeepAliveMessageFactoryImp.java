package com.eb.sc.tcprequest;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.eb.sc.bean.Params;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.Utils;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

/**
 * Created by lyj on 2017/7/28.
 */

public class ClientKeepAliveMessageFactoryImp implements KeepAliveMessageFactory {
    private Context context;
    public ClientKeepAliveMessageFactoryImp(Context context){
        this.context=context;
    }
    @Override
    public boolean isRequest(IoSession ioSession, Object o) {
        if (o instanceof String && o.equals(Utils.xintiao(context))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isResponse(IoSession ioSession, Object o) {
        if (o instanceof String && o.equals(Utils.xintiao(context))) {
            return true;
        }
        return false;
    }

    @Override
    public Object getRequest(IoSession ioSession) {
        Log.e("ClientKeepAliveMessageF", "发送心跳...");
        BaseConfig bg = new BaseConfig(context);
        String she=  bg.getStringValue(Constants.shebeihao,"");
        if(!TextUtils.isEmpty(she)){
            return Utils.xintiao(context);
        }
       return null;
    }

    @Override
    public Object getResponse(IoSession ioSession, Object o) {
        Log.e("dawns", "getResponse: "+o.toString());
        return null;
    }
}
