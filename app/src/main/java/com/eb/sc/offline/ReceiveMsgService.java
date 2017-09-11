package com.eb.sc.offline;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.eb.sc.bean.DataInfo;
import com.eb.sc.business.BusinessManager;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/9/9.
 */

public class ReceiveMsgService extends Service {// 实时监听网络状态改变
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Timer timer = new Timer();
                timer.schedule(new QunXTask(getApplicationContext()), 1000);
            }
        }
    };
    BarAsyncTask task;
    private Binder binder = new MyBinder();
    private boolean isContected = true;
    private List<DataInfo> mdata = new ArrayList<>();
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {// 注册广播
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); // 添加接收网络连接状态改变的Action
        registerReceiver(mReceiver, mFilter);
    }

    class QunXTask extends TimerTask {
        private Context context;

        public QunXTask(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            if (isNetworkConnected(context) || isWifiConnected(context)) {

                isContected = true;
                BaseConfig bg=BaseConfig.getInstance(context);
                String b = bg.getStringValue(Constants.havelink, "-1");
                if("1".equals(b)){
                    task = new BarAsyncTask();
                    task.execute();
                }

            } else {
                isContected = false;
            }
        }



        /*
         * 判断是3G否有网络连接
         */
        private boolean isNetworkConnected(Context context) {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable();
                }
            }
            return false;
        }

        /*
         * 判断是否有wifi连接
         */
        private boolean isWifiConnected(Context context) {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (mWiFiNetworkInfo != null) {
                    return mWiFiNetworkInfo.isAvailable();
                }
            }
            return false;
        }
    }

    public class MyBinder extends Binder {
        public ReceiveMsgService getService() {
            return ReceiveMsgService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver); // 删除广播
    }
    private void getdata() {
        if (mdata != null) {
            mdata.clear();
        }
        List<DataInfo> mlist = BusinessManager.querAll();
        for (int i = 0; i < mlist.size(); i++) {
//            if (!mlist.get(i).isUp()) {
            mdata.add(mlist.get(i));
        }
        if(mdata.size()<=0){
            return;
        }
    }

    private void sycnData() {
        getdata();
        String sendMsg = "";
        DataInfo dataInfo = null;
        synchronized (this) {
            for (int i = 0; i < mdata.size(); i++) {
                dataInfo = mdata.get(i);
                if (!dataInfo.isUp()) {
                    //二维码，身份证信息
                    String id = dataInfo.getId();
                    if (dataInfo.getType() == 1)
                        sendMsg = Utils.getIdcard_t(this, id);
                    else if (dataInfo.getType() == 2) {
//                        if (dataInfo.getId().length() == 6) {
//                            sendMsg = Utils.getscan_t_mj(this, id);
//                        } else {
//                        }
                        sendMsg = Utils.getscan_t(this, id);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    PushManager.getInstance(this).sendMessage(sendMsg);
                }
            }
        }
    }

    class BarAsyncTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            sycnData();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

}
