package com.eb.sc.tcprequest;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.eb.sc.bean.Params;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.GetOrderEvent;
import com.eb.sc.sdk.eventbus.PayResultEvent;
import com.eb.sc.sdk.eventbus.PutEvent;
import com.eb.sc.sdk.eventbus.RefreshEvent;
import com.eb.sc.sdk.eventbus.TongbuEvent;
import com.eb.sc.sdk.eventbus.UpdateEvent;
import com.eb.sc.utils.AESCipher;
import com.eb.sc.utils.AnalysisHelp;
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

    public ClientSessionHandler(Context context) {
        this.mcontext = context;
    }

    private TcpResponse tcpResponse;

    public void setTcpResponse(TcpResponse tcpResponse) {
        this.tcpResponse = tcpResponse;
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
        BaseConfig bg = new BaseConfig(mcontext);
        bg.setStringValue(Constants.havelink, "1");
        NotificationCenter.defaultCenter().publish(new ConnectEvent(true));
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        Log.e("ClientSessionHandler", "服务器与客户端断开连接...");
        BaseConfig bg = new BaseConfig(mcontext);
        bg.setStringValue(Constants.havelink, "-1");
        NotificationCenter.defaultCenter().publish(new ConnectEvent(false));
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

        if (Utils.pullShebei(message.toString())) {
            BaseConfig bg = new BaseConfig(mcontext);
            String sb = HexStr.hexStr2Str((message.toString()).substring(8, message.toString().length()));
            String shebei = Integer.toHexString(Integer.parseInt(sb));
            if (shebei.length() <= 1) {
                shebei = "000" + shebei;
            } else if (shebei.length() <= 2) {
                shebei = "00" + shebei;
            } else if (shebei.length() <= 3) {
                shebei = "0" + shebei;
            }
            bg.setStringValue(Constants.shebeihao, shebei);
        }
        //获取订单
        if (Utils.getOrderid(message.toString())) {
            BaseConfig bg = new BaseConfig(mcontext);
            String orderId=HexStr.hexStr2Str((message.toString()).substring(8, message.toString().length()));
            NotificationCenter.defaultCenter().publish(new GetOrderEvent(orderId));
        }
        //获取支付结果
        if (Utils.getPay(message.toString())) {
            BaseConfig bg = new BaseConfig(mcontext);
            String sfts=HexStr.hexStr2Str((message.toString()).substring(8, message.toString().length()));
            NotificationCenter.defaultCenter().publish(new PayResultEvent(sfts));
        }

        //是项目item
        if (Utils.pullItem(message.toString())){
            BaseConfig bg = new BaseConfig(mcontext);
            bg.setStringValue(Constants.px_list, HexStr.hexStr2Str((message.toString()).substring(8, message.toString().length())));
            NotificationCenter.defaultCenter().publish(new RefreshEvent());
        }

        //升级
        if (Utils.pullShengji(message.toString())) {
//            NotificationCenter.defaultCenter().publish(new TongbuEvent(2, message.toString()));
            String jieguo= HexStr.hexStr2Str((message.toString()).substring(20, message.toString().length()));
            NotificationCenter.defaultCenter().publish(new UpdateEvent(jieguo.substring(jieguo.length()-1,jieguo.length())));
        }
//        是同步  身份证1
        if (Utils.pullSync(message.toString())) {
//            NotificationCenter.defaultCenter().publish(new TongbuEvent(2, message.toString()));
            String jieguo= HexStr.hexStr2Str((message.toString()).substring(20, message.toString().length()));
            NotificationCenter.defaultCenter().publish(new TongbuEvent(AnalysisHelp.getjieguo(jieguo),AnalysisHelp.getresylt(jieguo),"1"));
        }
        //  二维码2
        if (Utils.pullscan(message.toString())) {
            String jieguo= HexStr.hexStr2Str((message.toString()).substring(20, message.toString().length()));
            NotificationCenter.defaultCenter().publish(new TongbuEvent(AnalysisHelp.getScanjieguo(jieguo),AnalysisHelp.getScanresylt(jieguo),"1"));
        }
        //  二维码 票务通
        if (Utils.pullscanMj(message.toString())) {
            String jieguo= HexStr.hexStr2Str((message.toString()).substring(20, message.toString().length()));
            NotificationCenter.defaultCenter().publish(new TongbuEvent(AnalysisHelp.getScanjieguo(jieguo),AnalysisHelp.getScanresylt(jieguo),"1"));
        }

        if (message.toString().length() <7) {
            NotificationCenter.defaultCenter().publish(new PutEvent(2, message.toString()));
        }
//        tcpResponse.receivedMessage(message.toString().trim());
        Log.e("ClientSessionHandler", "客户端接受消息成功..." + HexStr.hexStr2Str((message.toString()).substring(8, message.toString().length())));
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        tcpResponse.receivedMessage(message.toString().trim());
        Log.e("ClientSessionHandler", "客户端发送消息成功..." + message.toString());

    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
        Log.e("ClientSessionHandler", "客户端进入空闲状态...");
    }
}
