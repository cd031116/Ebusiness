package com.hoare.hand;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.bean.DataInfo;
import com.eb.sc.business.BusinessManager;
import com.eb.sc.offline.OfflLineDataDb;
import com.eb.sc.scanner.ScannerActivity;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.EventSubscriber;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.sdk.eventbus.PutEvent;
import com.eb.sc.sdk.eventbus.PutSubscriber;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.utils.AnalysisHelp;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.utils.PlayVedio;
import com.eb.sc.utils.Utils;
import com.eb.sc.widget.ScanDialog;
import com.eb.sc.widget.ShowMsgDialog;
import com.hoare.hand.scan.ScanUtil;
import com.hoare.hand.scan.Util;

import org.aisen.android.component.eventbus.NotificationCenter;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * aunthor lyj
 * create 2018/6/16/016 9:35  中惠旅手持机(汉德)
 **/
public class HandScanActivity extends BaseActivity {
    private String TAG="HandScanActivity";
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    @Bind(R.id.top_title)
    TextView top_title;
    private int cannum = 1;
    private String text_code = "";
    private boolean isconnect = true;

    private ScanUtil scanUtil ;
    //广播接收扫描数据
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] data = intent.getByteArrayExtra("data");
            if (data != null) {
                Log.e(TAG, new String(data));
                text_code="";
                text_code = new String(data);
                Log.i("tttt","barcode="+text_code);

                if (!NetWorkUtils.isNetworkConnected(HandScanActivity.this) || !isconnect) {//无网络
                    if (BusinessManager.isHaveScan(text_code, cannum)) {//票已检
                        showDialogMsg("已使用!");
                    } else {
                        showresult(text_code);
                    }
                } else {//有网络
                    if (text_code.length() == 6) {
                        PushManager.getInstance(HandScanActivity.this).sendMessage(Utils.getMjScan(HandScanActivity.this, text_code));
                    } else {
                        String updata = Utils.getscan(HandScanActivity.this, text_code);
                        PushManager.getInstance(HandScanActivity.this).sendMessage(updata);
                    }
                }

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.rfid.SCAN");
        registerReceiver(receiver, filter);
        Util.initSoundPool(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hand_scan;
    }

    @Override
    public void initView() {
        super.initView();
        top_title.setText("二维码扫描");
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(PutEvent.class, putSubscriber);
        BaseConfig bg = BaseConfig.getInstance(this);
        try {
            cannum = Integer.parseInt(bg.getStringValue(Constants.X_NUM, "1"));
        } catch (Exception e) {

        }
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

    @OnClick({R.id.top_left, R.id.close_bg, R.id.clisk})
    void onclick(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                HandScanActivity.this.finish();
                break;
            case R.id.close_bg:
                ExitDialog();
                break;
            case R.id.clisk:
                if (scanUtil != null) {
                    scanUtil.setScanMode(0);//mode :0 , 广播模式， 1， 编辑输入模式
                    scanUtil.scan();
                }
                break;
        }
    }

    @Override
    public void initData() {
        super.initData();

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

    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber() {
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(HandScanActivity.this);
            String a = bg.getStringValue(Constants.havenet, "-1");
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
            BaseConfig bg = new BaseConfig(HandScanActivity.this);
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

    //---------------------------------------检票相关
    //无效票
    private void showDialogMsg(String names) {
        new ShowMsgDialog(this, R.style.dialog, names, new ShowMsgDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    text_code = "";
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();
    }

    //分析二维码-无线
    private void showresult(String strs) {
        int a = AnalysisHelp.useScan(HandScanActivity.this, strs);
        Log.i("tttt", "aaaaaa=" + a);
        if (a == 1) {//1------可用
            PlayVedio.getInstance().play(HandScanActivity.this,4);
            showDialog(Utils.getXiangmu(HandScanActivity.this), strs, AnalysisHelp.renshu(strs) + "");
        } else if (a == 2) {
            PlayVedio.getInstance().play(HandScanActivity.this,9);
            showDialogMsg("票已过期!");
        } else if (a == 3) {
            PlayVedio.getInstance().play(HandScanActivity.this,1);
            showDialogMsg("票型不符合!");
        } else if (a == 4) {
            PlayVedio.getInstance().play(HandScanActivity.this,4);
            showDialog(Utils.getXiangmu(HandScanActivity.this), strs, "");//梅江
        } else {
            PlayVedio.getInstance().play(HandScanActivity.this,1);
            showDialogMsg("无效票!");
        }
    }


    //无线
    private void showDialog(String num, final String code, final String reshu) {
        new ScanDialog(this, R.style.dialog, num, "", reshu, new ScanDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    DataInfo dataInfo = new DataInfo();
                    if (BusinessManager.isHaveuse(text_code, cannum) == 0) {
                        dataInfo.setCanuse(1);
                        Log.i("mmmm", "setCanuse=");
                        if (code.length() == 6) {
                            dataInfo.setId(code);
                            dataInfo.setNet(false);
                            dataInfo.setName(Utils.getXiangmu(HandScanActivity.this));
                            dataInfo.setType(2);
                            dataInfo.setInsertTime(System.currentTimeMillis() + "");
                            dataInfo.setUp(false);
                        } else {
                            dataInfo.setId(code);
                            dataInfo.setNet(false);
                            dataInfo.setpNum(reshu);
                            dataInfo.setName(Utils.getXiangmu(HandScanActivity.this));
                            dataInfo.setType(2);
                            dataInfo.setInsertTime(System.currentTimeMillis() + "");
                            dataInfo.setUp(false);
                        }
                        OfflLineDataDb.insert(dataInfo);
                    } else if (BusinessManager.isHaveuse(text_code, cannum) > 0) {
                        int isuse = BusinessManager.isHaveuse(text_code, cannum);
                        DataInfo a =  OfflLineDataDb.getDB().selectById(null, DataInfo.class, text_code);
                        a.setCanuse(isuse + 1);
                        OfflLineDataDb.updata(a);
                    }
                    text_code = "";
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();
    }


    //在线成功
    PutSubscriber putSubscriber = new PutSubscriber() {
        @Override
        public void onEvent(PutEvent putEvent) {
            String srt = putEvent.getStrs();
            String sgs = putEvent.getStrs().substring(0, 2);
            int renshu = Integer.parseInt(putEvent.getStrs().substring(2, 6), 16);
            Log.d("dddd", "putEvent sgs: " + sgs + ",renshu:" + renshu + ",id_n:" + text_code + ",xiangmu:" + Utils.getXiangmu(HandScanActivity.this));
            if ("01".equals(sgs)) {
                showDialogd("成人票", text_code, Utils.getXiangmu(HandScanActivity.this), String.valueOf(renshu));
                PlayVedio.getInstance().play(HandScanActivity.this, 5);
            } else if ("02".equals(sgs)) {
                PlayVedio.getInstance().play(HandScanActivity.this, 2);
                showDialogd("儿童票", text_code, Utils.getXiangmu(HandScanActivity.this), String.valueOf(renshu));
            } else if ("03".equals(sgs)) {
                PlayVedio.getInstance().play(HandScanActivity.this, 7);
                showDialogd("优惠票", text_code, Utils.getXiangmu(HandScanActivity.this), String.valueOf(renshu));
            } else if ("04".equals(sgs)) {
                PlayVedio.getInstance().play(HandScanActivity.this, 7);
                showDialogd("招待票", text_code, Utils.getXiangmu(HandScanActivity.this), String.valueOf(renshu));
            } else if ("05".equals(sgs)) {
                PlayVedio.getInstance().play(HandScanActivity.this, 3);
                showDialogd("老年票", text_code, Utils.getXiangmu(HandScanActivity.this), String.valueOf(renshu));
            } else if ("06".equals(sgs)) {
                PlayVedio.getInstance().play(HandScanActivity.this, 8);
                showDialogd("团队票", text_code, Utils.getXiangmu(HandScanActivity.this), String.valueOf(renshu));
            } else if ("07".equals(sgs)) {
                PlayVedio.getInstance().play(HandScanActivity.this, 6);
                showDialogMsg("已使用");
            } else if ("08".equals(sgs)) {
                PlayVedio.getInstance().play(HandScanActivity.this, 1);
                showDialogMsg("无效票");
            } else if ("09".equals(sgs)) {
                PlayVedio.getInstance().play(HandScanActivity.this, 9);
                showDialogMsg("已过期");
            } else if ("0A".equals(sgs)) {
                showDialogMsg("网络超时");
            } else if ("0B".equals(sgs)) {
                showDialogMsg("票型不符");
            } else if ("0C".equals(sgs)) {
                showDialogMsg("团队满人");
            } else if ("0D".equals(sgs)) {
                showDialogMsg("游玩尚未开始（网购票当天购买要第二天用）");
            } else if ("0E".equals(sgs)) {
                showDialogd("年卡", text_code, Utils.getXiangmu(HandScanActivity.this), String.valueOf(renshu));
            } else if ("10".equals(sgs)) {
                showDialogMsg("通道不符");
            }

        }
    };

    //有线
    private void showDialogd(String num, final String code, String name, final String renshu) {
        new ScanDialog(this, R.style.dialog, num, name, renshu, new ScanDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    DataInfo dataInfo = new DataInfo();
                    if (BusinessManager.isHaveuse(text_code, cannum) == 0) {
                        dataInfo.setCanuse(1);
                        if (code.length() == 6) {
                            dataInfo.setId(code);
                            dataInfo.setNet(false);
                            dataInfo.setName(Utils.getXiangmu(HandScanActivity.this));
                            dataInfo.setType(2);
                            dataInfo.setInsertTime(System.currentTimeMillis() + "");
                            dataInfo.setUp(false);
                        } else {
                            dataInfo.setId(code);
                            dataInfo.setNet(true);
                            dataInfo.setpNum(renshu);
                            dataInfo.setName(Utils.getXiangmu(HandScanActivity.this));
                            dataInfo.setType(2);
                            dataInfo.setInsertTime(System.currentTimeMillis() + "");
                            dataInfo.setUp(true);
                        }
                        OfflLineDataDb.insert(dataInfo);
                    } else {
                        int isuse = BusinessManager.isHaveuse(text_code, cannum);
                        DataInfo a = OfflLineDataDb.getDB().selectById(null, DataInfo.class, text_code);
                        a.setCanuse(isuse + 1);
                        OfflLineDataDb.updata(a);
                    }
                    text_code = "";
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();
    }
    //--------------------------------------------------
    @Override
    public void onResume() {
        super.onResume();
        if (scanUtil == null) {
            scanUtil = new ScanUtil(this);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (scanUtil != null) {
            scanUtil.close();
            scanUtil = null ;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        NotificationCenter.defaultCenter().unsubscribe(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(PutEvent.class, putSubscriber);
    }
}
