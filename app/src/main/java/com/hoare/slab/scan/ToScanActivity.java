package com.hoare.slab.scan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.base.BaseSlabActivity;
import com.eb.sc.bean.DataInfo;
import com.eb.sc.business.BusinessManager;
import com.eb.sc.offline.OfflLineDataDb;
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
import com.hoare.hand.HandScanActivity;
import com.hoare.hand.scan.Util;

import org.aisen.android.component.eventbus.NotificationCenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
*aunthor lyj
* create 2018/6/19/019 17:45  平板扫码
**/
public class ToScanActivity extends BaseSlabActivity {
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    @Bind(R.id.top_title)
    TextView top_title;
    private boolean isconnect = true;
    private String text_code = "";

    private int cannum = 1;

    private ScanThread scanThread;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == ScanThread.SCAN) {
                 text_code = msg.getData().getString("data");
                Util.play(1, 0);
                if (!NetWorkUtils.isNetworkConnected(ToScanActivity.this) || !isconnect) {//无网络
                    if (BusinessManager.isHaveScan(text_code, cannum)) {//票已检
                        showDialogMsg("已使用!");
                    } else {
                        showresult(text_code);
                    }
                } else {//有网络
                    if (text_code.length() == 6) {
                        PushManager.getInstance(ToScanActivity.this).sendMessage(Utils.getMjScan(ToScanActivity.this, text_code));
                    } else {
                        String updata = Utils.getscan(ToScanActivity.this, text_code);
                        PushManager.getInstance(ToScanActivity.this).sendMessage(updata);
                    }
                }

            }
        };
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            scanThread = new ScanThread(mHandler);
        } catch (Exception e) {
            return;
            // e.printStackTrace();
        }
        scanThread.start();
        //init sound
        Util.initSoundPool(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_to_scan;
    }

    @Override
    public void initView() {
        super.initView();
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(PutEvent.class, putSubscriber);
        top_title.setText("二维码扫描");
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

    @Override
    public void initData() {
        super.initData();
    }

    @OnClick({R.id.top_left, R.id.close_bg, R.id.clisk})
    void onclick(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                ToScanActivity.this.finish();
                break;
            case R.id.close_bg:
                ExitDialog();
                break;
            case R.id.clisk:
                if (scanThread!=null){
                    scanThread.scan();
                }
                break;
        }
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
            BaseConfig bg = new BaseConfig(ToScanActivity.this);
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
            BaseConfig bg = new BaseConfig(ToScanActivity.this);
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
    //-----------------------------------------------------------------------------------------检票相关
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
        int a = AnalysisHelp.useScan(ToScanActivity.this, strs);
        Log.i("tttt", "aaaaaa=" + a);
        if (a == 1) {//1------可用
            PlayVedio.getInstance().play(ToScanActivity.this,4);
            showDialog(Utils.getXiangmu(ToScanActivity.this), strs, AnalysisHelp.renshu(strs) + "");
        } else if (a == 2) {
            PlayVedio.getInstance().play(ToScanActivity.this,9);
            showDialogMsg("票已过期!");
        } else if (a == 3) {
            PlayVedio.getInstance().play(ToScanActivity.this,1);
            showDialogMsg("票型不符合!");
        } else if (a == 4) {
            PlayVedio.getInstance().play(ToScanActivity.this,4);
            showDialog(Utils.getXiangmu(ToScanActivity.this), strs, "");//梅江
        } else {
            PlayVedio.getInstance().play(ToScanActivity.this,1);
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
                            dataInfo.setName(Utils.getXiangmu(ToScanActivity.this));
                            dataInfo.setType(2);
                            dataInfo.setInsertTime(System.currentTimeMillis() + "");
                            dataInfo.setUp(false);
                        } else {
                            dataInfo.setId(code);
                            dataInfo.setNet(false);
                            dataInfo.setpNum(reshu);
                            dataInfo.setName(Utils.getXiangmu(ToScanActivity.this));
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
            Log.d("dddd", "putEvent sgs: " + sgs + ",renshu:" + renshu + ",id_n:" + text_code + ",xiangmu:" + Utils.getXiangmu(ToScanActivity.this));
            if ("01".equals(sgs)) {
                showDialogd("成人票", text_code, Utils.getXiangmu(ToScanActivity.this), String.valueOf(renshu));
                PlayVedio.getInstance().play(ToScanActivity.this, 5);
            } else if ("02".equals(sgs)) {
                PlayVedio.getInstance().play(ToScanActivity.this, 2);
                showDialogd("儿童票", text_code, Utils.getXiangmu(ToScanActivity.this), String.valueOf(renshu));
            } else if ("03".equals(sgs)) {
                PlayVedio.getInstance().play(ToScanActivity.this, 7);
                showDialogd("优惠票", text_code, Utils.getXiangmu(ToScanActivity.this), String.valueOf(renshu));
            } else if ("04".equals(sgs)) {
                PlayVedio.getInstance().play(ToScanActivity.this, 7);
                showDialogd("招待票", text_code, Utils.getXiangmu(ToScanActivity.this), String.valueOf(renshu));
            } else if ("05".equals(sgs)) {
                PlayVedio.getInstance().play(ToScanActivity.this, 3);
                showDialogd("老年票", text_code, Utils.getXiangmu(ToScanActivity.this), String.valueOf(renshu));
            } else if ("06".equals(sgs)) {
                PlayVedio.getInstance().play(ToScanActivity.this, 8);
                showDialogd("团队票", text_code, Utils.getXiangmu(ToScanActivity.this), String.valueOf(renshu));
            } else if ("07".equals(sgs)) {
                PlayVedio.getInstance().play(ToScanActivity.this, 6);
                showDialogMsg("已使用");
            } else if ("08".equals(sgs)) {
                PlayVedio.getInstance().play(ToScanActivity.this, 1);
                showDialogMsg("无效票");
            } else if ("09".equals(sgs)) {
                PlayVedio.getInstance().play(ToScanActivity.this, 9);
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
                showDialogd("年卡", text_code, Utils.getXiangmu(ToScanActivity.this), String.valueOf(renshu));
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
                            dataInfo.setName(Utils.getXiangmu(ToScanActivity.this));
                            dataInfo.setType(2);
                            dataInfo.setInsertTime(System.currentTimeMillis() + "");
                            dataInfo.setUp(false);
                        } else {
                            dataInfo.setId(code);
                            dataInfo.setNet(true);
                            dataInfo.setpNum(renshu);
                            dataInfo.setName(Utils.getXiangmu(ToScanActivity.this));
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
    public void onDestroy() {
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread.close();
        }
        super.onDestroy();
        NotificationCenter.defaultCenter().unsubscribe(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(PutEvent.class, putSubscriber);
    }
    public void ExitDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("确定退出整个应用?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BaseConfig bg = new BaseConfig(ToScanActivity.this);
                        bg.setStringValue(Constants.USER_ID,"");
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // 显示
        normalDialog.show();
    }

}
