package com.eb.sc.tcprequest;

import android.content.Context;
import android.util.Log;

import com.eb.sc.bean.Params;
import com.eb.sc.utils.AESCipher;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.HexStr;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Created by lyj on 2017/7/28.
 */

public class PushManager {
    private static volatile PushManager manager;
    private static NioSocketConnector connector;
    private static ConnectFuture connectFuture;
    private static IoSession ioSession;
    private static Context mcontext;
    private static ClientSessionHandler clientSessionHandler = null;
    public static ClientSessionHandler getClientSessionHandler(String strs) {
        sendMessage(strs);
        return clientSessionHandler;
    }

    private PushManager(Context context){
        Log.e("dawns", "PushManager: ");
        connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(Params.CONNECT_TIMEOUT);
        //----------------------
        //为接收器设置管理服务
        clientSessionHandler=new ClientSessionHandler(context);
        connector.setHandler(clientSessionHandler);
        //设置过滤器（使用Mina提供的文本换行符编解码器）
//        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue())));
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ByteArrayCodecFactory()));
        //读写通道5秒内无操作进入空闲状态
        connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, Params.REQUEST_TIMEOUT);
        //设置读取数据的缓冲区大小
        connector.getSessionConfig().setReadBufferSize(2048);
        //设置心跳
        KeepAliveMessageFactory heartBeatFactory = new ClientKeepAliveMessageFactoryImp();
        KeepAliveRequestTimeoutHandler heartBeatHandler = new ClientKeepAliveMessageTimeoutFactoryImp();
        KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory, IdleStatus.BOTH_IDLE,heartBeatHandler);
        //是否回发
        heartBeat.setForwardEvent(true);
        //心跳发送频率
        heartBeat.setRequestInterval(Params.REQUEST_INTERVAL);
        connector.getSessionConfig().setKeepAlive(true);
        connector.getFilterChain().addLast("keepalive", heartBeat);
    }

    public static PushManager getInstance(Context context){
         mcontext=context;
        if (manager == null) {
            synchronized (PushManager.class) {
                manager = new PushManager(mcontext);
            }
        }
        return manager;
    }
    /**
     * 连接
     * @return
     */
    public boolean connect() {
        BaseConfig bg=new BaseConfig(mcontext);
        if (connector != null && connector.isActive() &&
                connectFuture != null && connectFuture.isConnected() &&
                ioSession != null && ioSession.isConnected()) {
            return true;
        }
        try {
            Log.e("dawns", "connect: ");
            connectFuture = connector.connect(new InetSocketAddress(bg.getStringValue(Constants.tcp_ip,"192.168.18.10"),Integer.parseInt(bg.getStringValue(Constants.ip_port,"2020"))));
            //等待是否连接成功，相当于是转异步执行为同步执行。
            connectFuture.awaitUninterruptibly();
            //连接成功后获取会话对象。如果没有上面的等待，由于connect()方法是异步的，session 可能会无法获取。
            ioSession = connectFuture.getSession();
//            String encrypt = AESCipher.encrypt(Params.KEY,);
            sendMessage(Params.SEND);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关闭
     */
    public void close() {
        if (ioSession != null && ioSession.isConnected()) {
            ioSession.close(false);
        }
        if (connectFuture != null && connectFuture.isConnected()) {
            connectFuture.cancel();
        }
        if (connector != null && !connector.isDisposed()) {
            connector.dispose();
        }
    }

    /**
     * 发送
     * @param message
     * @return
     */
    public static  boolean sendMessage(String message) {
        Log.e("dawn", "sendMessage: "+message );
        if (ioSession == null || !ioSession.isConnected()) {
            return false;
        }
        WriteFuture writeFuture = ioSession.write(message);
        if (writeFuture == null) {
            return false;
        }
        writeFuture.awaitUninterruptibly();
        if (writeFuture.isWritten()) {
            return true;
        } else {
            return false;
        }
    }


}