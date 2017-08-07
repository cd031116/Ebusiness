package com.eb.sc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.bean.DataInfo;
import com.eb.sc.bean.Params;
import com.eb.sc.business.BusinessManager;
import com.eb.sc.offline.OfflLineDataDb;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.EventSubscriber;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.tcprequest.PushService;
import com.eb.sc.tcprequest.TcpResponse;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.ChangeData;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.HexStr;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.widget.InputDialog;

import org.aisen.android.component.eventbus.NotificationCenter;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
/*
*
* @author lyj
* @describe  功能入口界面
* @data 2017/7/29
* */


public class CheckActivity extends BaseActivity {
    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    @Bind(R.id.scan)
    RelativeLayout scan;//检票
    @Bind(R.id.idcard)
    RelativeLayout idScan;//明细
    @Bind(R.id.sync)
    RelativeLayout sync;//设置
    @Bind(R.id.setting)
    ImageView setting;//设置
    private boolean isconnect = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_check;
    }

    @Override
    public void initView() {
        super.initView();
        BaseConfig bg = new BaseConfig(this);
        bg.setStringValue(Constants.admin_word, "123456");
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        top_left.setVisibility(View.GONE);
        top_title.setText("石燕湖大门核销点");
        String b = bg.getStringValue(Constants.havelink, "1");
        if ("1".equals(b)) {
            isconnect = true;
        } else {
            isconnect = false;
        }
        if (NetWorkUtils.isNetworkConnected(this) && isconnect) {
            bg.setStringValue(Constants.havenet, "1");
            changeview(true);
        } else {
            changeview(false);
        }
    }

    @Override
    public void initData() {
        super.initData();
        startService(new Intent(CheckActivity.this, PushService.class));
//        PushManager.getInstance(this).getClientSessionHandler(Params.SEND).setTcpResponse(new TcpResponse() {
//            @Override
//            public void receivedMessage(String trim) {
//                Log.e("ClientSessionHandler", "receivedMessage: "+trim);
//            }
//
//            @Override
//            public void breakConnect() {
//
//            }
//        });
        cleardata();
    }

    private void cleardata() {
        List<DataInfo> mList = BusinessManager.querAll();
        long times = ChangeData.getNowtime();
        for (int i = 0; i < mList.size(); i++) {
            if (Long.parseLong(mList.get(i).getInsertTime())<times){
                OfflLineDataDb.delete(mList.get(i));
            }
        }
    }

    @OnClick({R.id.scan, R.id.idcard, R.id.top_right_text, R.id.setting, R.id.sync})
    void onBuy(View v) {
        switch (v.getId()) {
            case R.id.scan:
                startActivity(new Intent(CheckActivity.this, SelectActivity.class));
                break;
            case R.id.idcard:
                startActivity(new Intent(CheckActivity.this, DetailActivity.class));
                break;
            case R.id.sync:
                //同步
                startActivity(new Intent(CheckActivity.this, TongbBuActivity.class));
                break;
            case R.id.setting:
                final BaseConfig bg = BaseConfig.getInstance(this);
                new InputDialog(this, R.style.dialog, "请输入管理员密码？", new InputDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm, String text) {
                        if (confirm) {
                            String psd = bg.getStringValue(Constants.admin_word, "-1");
                            if (psd.equals(text)) {
                                startActivity(new Intent(CheckActivity.this, SettingActivity.class));
                            } else {
                                Toast.makeText(CheckActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    }
                }).setTitle("提示").show();
                break;
        }
    }

    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber() {
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(CheckActivity.this);
            String a = bg.getStringValue(Constants.havenet, "-1");
            String piaox=bg.getStringValue(Constants.piaoxing,"-1");
            if(!piaox.equals("1")){
                PushManager.getInstance(CheckActivity.this).sendMessage(Params.SHEBEI);
            }

            if (event.isConnect()) {
                isconnect = true;
                if ("1".equals(a)) {
                    changeview(true);
                } else {
                    changeview(false);
                }
            } else {
                isconnect = false;
                changeview(false);
            }

        }
    };
    //网络
    EventSubscriber netEventSubscriber = new EventSubscriber() {
        @Override
        public void onEvent(NetEvent event) {
            if (event.isConnect()) {
                if (isconnect) {
                    changeview(true);
                } else {
                    changeview(false);
                }
            } else {
                changeview(false);
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter().unsubscribe(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(NetEvent.class, netEventSubscriber);
        stopService(new Intent(CheckActivity.this, PushService.class));
    }

    private void changeview(boolean conect) {
        if (conect) {
            mRight_bg.setImageResource(R.mipmap.lianjie);
            top_right_text.setText("链接");
            top_right_text.setTextColor(Color.parseColor("#0973FD"));
        } else {
            mRight_bg.setImageResource(R.mipmap.lixian);
            top_right_text.setText("离线");
            top_right_text.setTextColor(Color.parseColor("#EF4B55"));
        }
    }
}
