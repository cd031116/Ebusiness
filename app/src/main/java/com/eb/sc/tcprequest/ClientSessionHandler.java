package com.eb.sc.tcprequest;

import android.content.Context;
import android.util.Log;

import com.eb.sc.bean.Params;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.PutEvent;
import com.eb.sc.utils.AESCipher;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.HexStr;
import com.eb.sc.utils.Utils;

import org.aisen.android.component.eventbus.NotificationCenter;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.nio.ByteBuffer;

/**
 * Created by lyj on 2017/7/28.
 */

public class ClientSessionHandler extends IoHandlerAdapter {
   private Context mcontext;
    public ClientSessionHandler( Context context){
        this.mcontext=context;
    }
    private TcpResponse tcpResponse;

    public void setTcpResponse(TcpResponse tcpResponse){
        this.tcpResponse=tcpResponse;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        Log.e("ClientSessionHandler", "服务器与客户端创建连接...");

    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        Log.e("ClientSessionHandler", "服务器与客户端连接打开...");
        NotificationCenter.defaultCenter().publish(new ConnectEvent(true));
        BaseConfig bg=new BaseConfig(mcontext);
        bg.setStringValue(Constants.havelink, "1");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        Log.e("ClientSessionHandler", "服务器与客户端断开连接...");
        NotificationCenter.defaultCenter().publish(new ConnectEvent(false));
        BaseConfig bg=new BaseConfig(mcontext);
        bg.setStringValue(Constants.havelink, "-1");
        tcpResponse.breakConnect();
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
        Log.e("ClientSessionHandler", "服务器发送异常...");
        tcpResponse.breakConnect();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);
//        if(message.toString()!=null){
//            BaseConfig bg=new BaseConfig(mcontext);
//            bg.setStringValue(Constants.shebeihao, "1");
//        }
        //是
        if(Utils.pullItem(message.toString())){
            BaseConfig bg=new BaseConfig(mcontext);
            bg.setStringValue(Constants.px_list,HexStr.hexStr2Str((message.toString()).substring(12,message.toString().length())));
        }
        //是身份证
        if(Utils.pullIdCard(message.toString())){
            NotificationCenter.defaultCenter().publish(new PutEvent(1,message.toString()));

        }
        //是二维码
        if(Utils.pullScan(message.toString())){
            NotificationCenter.defaultCenter().publish(new PutEvent(2,message.toString()));
        }
        //是二维码
        if(Utils.pullSync(message.toString())){

            NotificationCenter.defaultCenter().publish(new PutEvent(2,message.toString()));
        }

//        tcpResponse.receivedMessage(message.toString().trim());
        Log.e("ClientSessionHandler", "客户端接受消息成功..."+HexStr.hexStr2Str((message.toString()).substring(12,message.toString().length())));
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        tcpResponse.receivedMessage(message.toString().trim());
        Log.e("ClientSessionHandler", "客户端发送消息成功..."+message.toString());

    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
        Log.e("ClientSessionHandler", "客户端进入空闲状态...");
    }
}
