package com.eb.sc.tcprequest;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.eb.sc.MainActivity;
import com.eb.sc.activity.SettingActivity;
import com.eb.sc.bean.Params;
import com.eb.sc.utils.AESCipher;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.HexStr;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.utils.Utils;

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

import static com.squareup.okhttp.internal.Internal.logger;

/*
*
* @author lyj
* @describe tcp/ip协议管理类
* @data 2017/11/10
* */


public class PushManager {
    private static volatile PushManager manager;
    private static NioSocketConnector connector;
    private static ConnectFuture connectFuture;
    private static IoSession ioSession;
    private static Context mcontext;
    private  ClientSessionHandler clientSessionHandler = null;
    public  ClientSessionHandler getClientSessionHandler(String strs) {
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
        KeepAliveMessageFactory heartBeatFactory = new ClientKeepAliveMessageFactoryImp(context);
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


    public   void add(){
        BaseConfig bg=new BaseConfig(mcontext);
        String dd=bg.getStringValue(Constants.tcp_ip,"");
        if(TextUtils.isEmpty(dd)){
            manager=null;
            return ;
        }
        if(!NetWorkUtils.isNetworkConnected(mcontext)){
            manager=null;
            return ;
        }
        BarAsyncTask task=new BarAsyncTask();
        task.execute();
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
            connectFuture = connector.connect(new InetSocketAddress(bg.getStringValue(Constants.tcp_ip,""),Integer.parseInt(bg.getStringValue(Constants.ip_port,""))));
            Log.e("dawns", "connector: ");
            //等待是否连接成功，相当于是转异步执行为同步执行。
              connectFuture.awaitUninterruptibly();
            Log.e("dawns", "connectFuture: ");
            bg.setStringValue(Constants.havelink, "-1");
            //连接成功后获取会话对象。如果没有上面的等待，由于connect()方法是异步的，session 可能会无法获取。
            ioSession = connectFuture.getSession();
//            String encrypt = AESCipher.encrypt(Params.KEY,);
            bg.setStringValue(Constants.havelink, "1");
            Log.e("dawns", "ioSession: ");
            sendMessage(Params.SHENGJI);
            String by=  bg.getStringValue(Constants.px_list,"");
            if(TextUtils.isEmpty(by)){
                sendMessage(Params.SHEBEI);
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
           new RelayTask().execute();
        }
        return false;
    }

    class BarAsyncTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            connect();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    class RelayTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            connect();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    /**
     * 关闭
     */
    public   void close() {
        Log.e("dawn", "close: " );
        if (ioSession != null && ioSession.isConnected()){
            ioSession.close(true);
        }
        if (connectFuture != null && connectFuture.isConnected()){
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
    public   boolean sendMessage(String message) {
        Log.e("dawn", "sendMessage: "+message );
        if (ioSession == null || !ioSession.isConnected()) {
            return false;
        }
        Log.e("dawn", "writeFuture: " );
        WriteFuture writeFuture = ioSession.write(message);
        if (writeFuture == null) {
              Log.e("dawn", "writeFuture:" );
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