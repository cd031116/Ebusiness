package com.eb.sc.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.eb.sc.BuildConfig;
import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.bean.DataInfo;
import com.eb.sc.bean.ItemInfo;
import com.eb.sc.bean.Params;
import com.eb.sc.bean.SaleBean;
import com.eb.sc.business.BusinessManager;
import com.eb.sc.offline.OfflLineDataDb;
import com.eb.sc.offline.SaleDataDb;
import com.eb.sc.priter.PrinterActivity;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.EventSubscriber;
import com.eb.sc.sdk.eventbus.LoginEvent;
import com.eb.sc.sdk.eventbus.LoginSubscriber;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.sdk.eventbus.RefreshEvent;
import com.eb.sc.sdk.eventbus.RefreshSubscriber;
import com.eb.sc.sdk.eventbus.UpdateEvent;
import com.eb.sc.sdk.eventbus.UpdateEventSubscriber;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.tcprequest.PushService;
import com.eb.sc.tcprequest.TcpResponse;
import com.eb.sc.utils.AnalysisHelp;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.ChangeData;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.DoubleClickExitHelper;
import com.eb.sc.utils.HexStr;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.utils.Utils;
import com.eb.sc.widget.InputDialog;
import com.eb.sc.widget.LogDialog;
import com.eb.sc.widget.ShengjiDialog;
import com.eb.sc.widget.ShowMsgDialog;

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
import java.util.ArrayList;
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
    private boolean isconnect = true;
    private DoubleClickExitHelper mDoubleClickExit;
    private List<ItemInfo> mList = new ArrayList<>();

    @Override
    protected int getLayoutId(){
        return R.layout.activity_check;
    }

    @Override
    public void initView() {
        super.initView();
        mDoubleClickExit = new DoubleClickExitHelper(this);
        BaseConfig bg = new BaseConfig(this);
        bg.setStringValue(Constants.admin_word, "123456");
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(RefreshEvent.class, refreshEvent);
        NotificationCenter.defaultCenter().subscriber(UpdateEvent.class, updateEvent);
        NotificationCenter.defaultCenter().subscriber(LoginEvent.class, loginSubscriber);
        top_left.setVisibility(View.GONE);

        String b = bg.getStringValue(Constants.havelink, "-1");
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
        TestData();
        cleardata();
        Log.e("ClientSessionHandler", "shebei..." + Utils.getShebeipul(CheckActivity.this, Utils.getImui(CheckActivity.this)));
    }

    private void cleardata() {
        List<DataInfo> mList = OfflLineDataDb.queryAll();
        long times = ChangeData.getNowtime();
        for (int i = 0; i < mList.size(); i++) {
            if (Long.parseLong(mList.get(i).getInsertTime()) < times) {
                OfflLineDataDb.delete(mList.get(i));
            }
        }
    }

    @OnClick({R.id.scan, R.id.detail, R.id.top_right_text, R.id.setting, R.id.sync,R.id.sale,R.id.select,R.id.close_bg})
    void onBuy(View v) {
      final   BaseConfig bg = new BaseConfig(CheckActivity.this);
        switch (v.getId()) {
            case R.id.scan:
                String address = bg.getStringValue(Constants.address, "");
//                String she=  bg.getStringValue(Constants.shebeihao,"");
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(CheckActivity.this, "您还没设置检票项目,请前往设置中心设置!", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(CheckActivity.this, SelectActivity.class));
                break;
            case R.id.detail:
                startActivity(new Intent(CheckActivity.this, DetailActivity.class));
                break;
            case R.id.sync:
                //同步
                startActivity(new Intent(CheckActivity.this, TongbBuActivity.class));
                break;
            case R.id.setting:
                new InputDialog(this, R.style.dialog, "请输入管理员密码？", new InputDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm, String text) {
                        if (confirm) {
                            String psd = bg.getStringValue(Constants.admin_word, "-1");
                            if (psd.equals(text)) {
                                startActivityForResult(new Intent(CheckActivity.this, SettingActivity.class), 1);
                            } else {
                                Toast.makeText(CheckActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                        }
                    }
                }).setTitle("提示").show();
                break;
            case R.id.sale://售票
                String addressd = bg.getStringValue(Constants.address, "");
//                String she=  bg.getStringValue(Constants.shebeihao,"");
                if (TextUtils.isEmpty(addressd)) {
                    Toast.makeText(CheckActivity.this, "您还没设置售票项目,请前往设置中心设置!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String user_id = bg.getStringValue(Constants.USER_ID, "");
                if(TextUtils.isEmpty(user_id)){
                    new LogDialog(this, R.style.dialog, "请输入管理员密码？", new LogDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm, String account,String psd) {
                            if (confirm) {
                                String updatd = Utils.ToLogin(CheckActivity.this, account+"&"+psd);
                                boolean gg=  PushManager.getInstance(CheckActivity.this).sendMessage(updatd);
                                if(gg){
                                    showAlert("正在登录", false);
                                }else {
                                    Toast.makeText(CheckActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            } else {
                                dialog.dismiss();
                            }
                        }
                    }).setTitle("提示").show();
                }else {
                    startActivity(new Intent(CheckActivity.this, SaleTickActivity.class));
                }
                break;
            case R.id.select://查询
                startActivity(new Intent(CheckActivity.this, QureActivity.class));
                break;
            case R.id.close_bg:
                ExitDialog();
                break;
        }
    }

    //长连接
    LoginSubscriber loginSubscriber = new LoginSubscriber() {
        @Override
        public void onEvent(LoginEvent event) {
            dismissAlert();

            if(event.getUser_id().contains("loginfail")){
                Toast.makeText(CheckActivity.this,"登录失败!",Toast.LENGTH_SHORT).show();
            }else {
                BaseConfig bg = new BaseConfig(CheckActivity.this);
                bg.setStringValue(Constants.USER_ID,event.getUser_id());
                startActivity(new Intent(CheckActivity.this, SaleTickActivity.class));
            }
        }
    };

    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber() {
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(CheckActivity.this);
            String a = bg.getStringValue(Constants.havenet, "-1");
            Log.e("ClientSessionHandler", "11111111111");
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

    UpdateEventSubscriber updateEvent = new UpdateEventSubscriber() {
        @Override
        public void onEvent(UpdateEvent event) {
            int distance = String.valueOf(BuildConfig.VERSION_CODE).compareTo(event.getCode());
            if (distance < 0) {
                showDialogMsg();
            }
        }
    };

    private void showDialogMsg() {
        new ShengjiDialog(this, R.style.dialog, "检测到新版本,是否升级?", new ShengjiDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm, String text) {
                if (confirm) {
                    dialog.dismiss();
                    Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.pgyer.com/5IzM"));
                    it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                    CheckActivity.this.startActivity(it);

                } else {
                    dialog.dismiss();
                }
            }
        }).setTitle("新版本").show();

    }


    RefreshSubscriber refreshEvent = new RefreshSubscriber() {
        @Override
        public void onEvent(RefreshEvent refreshEvent) {
            PushManager.getInstance(CheckActivity.this).sendMessage(Utils.getShebeipul(CheckActivity.this, Utils.getImui(CheckActivity.this)));
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter().unsubscribe(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(RefreshEvent.class, refreshEvent);
        NotificationCenter.defaultCenter().unsubscribe(UpdateEvent.class, updateEvent);
        NotificationCenter.defaultCenter().unsubscribe(LoginEvent.class, loginSubscriber);
        stopService(new Intent(CheckActivity.this, PushService.class));
    }

    private void changeview(boolean conect) {
        if (conect) {
            mRight_bg.setImageResource(R.drawable.lianjie);
            top_right_text.setText("在线");
            top_right_text.setTextColor(Color.parseColor("#0973FD"));
        } else {
            mRight_bg.setImageResource(R.drawable.lixian);
            top_right_text.setText("离线");
            top_right_text.setTextColor(Color.parseColor("#EF4B55"));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return mDoubleClickExit.onKeyDown(keyCode, event);
        }
        Log.e("dawns", "onKeyDown: ");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        BaseConfig bg = new BaseConfig(CheckActivity.this);
        String result = "";
        try {
            result = data.getStringExtra("result");
        } catch (Exception e) {

        }
        if (!TextUtils.isEmpty(result)) {
            bg.setStringValue(Constants.tcp_ip, result);
            PushManager.getInstance(CheckActivity.this).add();
            Log.e("ClientSessionHandler", "result..." + result);
        }
        TestData();
        PushManager.getInstance(CheckActivity.this).sendMessage(Utils.getShebeipul(CheckActivity.this, Utils.getImui(CheckActivity.this)));
    }

    /**
     *
     */
    private void TestData() {
        if (mList != null) {
            mList.clear();
        }
        BaseConfig bg = new BaseConfig(CheckActivity.this);
        String list_item = bg.getStringValue(Constants.px_list, "");
        if (TextUtils.isEmpty(list_item)) {
            return;
        }
        if(!list_item.startsWith("[{")&&!list_item.endsWith("}]")){
            return;
        }
        mList = JSON.parseArray(list_item, ItemInfo.class);
        String s = bg.getStringValue(Constants.address, "-1");
        if (!TextUtils.isEmpty(s)) {
            for (int i = 0; i < mList.size(); i++) {
                if (s.equals(mList.get(i).getCode())) {
                    top_title.setText(mList.get(i).getName() + "核销点");
                    Log.i("tttt", "top_title=" + mList.get(i).getCode());
                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        TestData();
    }
}
