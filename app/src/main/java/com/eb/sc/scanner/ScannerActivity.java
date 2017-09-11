package com.eb.sc.scanner;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.activity.CaptureActivity;
import com.eb.sc.bean.DataInfo;
import com.eb.sc.bean.TicketInfo;
import com.eb.sc.business.BusinessManager;
import com.eb.sc.offline.OfflLineDataDb;
import com.eb.sc.priter.PrinterActivity;
import com.eb.sc.priter.PrinterHelper;
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
import com.eb.sc.utils.SupportMultipleScreensUtil;
import com.eb.sc.utils.Utils;
import com.eb.sc.widget.ScanDialog;
import com.eb.sc.widget.ShowMsgDialog;
import com.smartdevice.aidl.ICallBack;

import org.aisen.android.component.eventbus.NotificationCenter;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ScannerActivity extends BaseActivity {
    private int cannum = 1;
    private boolean runFlag = true;
    public String text = "";
    RemoteControlReceiver screenStatusReceiver = null;
    MediaPlayer player;
    Vibrator vibrator;
    private String firstCodeStr = "";
    private boolean beginToReceiverData = true;
    private boolean isconnect = true;
    private LinearLayout top_left,close_bg;
    private ShowMsgDialog smdiilag = null;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    @Bind(R.id.top_title)
    TextView top_title;
    ICallBack.Stub mCallback = new ICallBack.Stub() {
        @Override
        public void onReturnValue(byte[] buffer, int size)
                throws RemoteException {
            if (beginToReceiverData) {
                beginToReceiverData = false;
                return;
            }
            String codeStr = new String(buffer, 0, size);
            if (firstCodeStr.equals(codeStr)) {
                vibrator.vibrate(100);
            }
            player.start();
            vibrator.vibrate(100);
            firstCodeStr = codeStr;
            //发送到外部接收
            Intent intentBroadcast = new Intent();
            intentBroadcast.setAction("com.zkc.scancode");
            intentBroadcast.putExtra("code", codeStr);
            sendBroadcast(intentBroadcast);
            text += codeStr;
            int startIndex = text.indexOf("{");
            int endIndex = text.indexOf("}");
            String keyStr = "";
            if (startIndex > -1 && endIndex > -1 && endIndex - startIndex < 5) {
                keyStr = text.substring(startIndex + 1, endIndex);
                text = text.substring(0, text.indexOf("{"));
            }
            if (!TextUtils.isEmpty(text)) {
                mHandler.sendEmptyMessage(1);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        View rootView = findViewById(android.R.id.content);
        SupportMultipleScreensUtil.init(getApplication());
        SupportMultipleScreensUtil.scale(rootView);
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(PutEvent.class, putSubscriber);
        ButterKnife.bind(this);
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
        top_title.setText("扫描");
        beginToReceiverData = true;
        player = MediaPlayer.create(getApplicationContext(), R.raw.scan);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        screenStatusReceiver = new RemoteControlReceiver();
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
        screenStatusIF.addAction(Intent.ACTION_SHUTDOWN);
        screenStatusIF.addAction("com.zkc.keycode");
        registerReceiver(screenStatusReceiver, screenStatusIF);
        //查询服务是否绑定成功，bindSuccessFlag为服务是否绑定成功的标记，在BaseActivity声明
        ExecutorFactory.executeThread(new Runnable() {
            @Override
            public void run() {
                while (runFlag) {
                    Log.i("client", "runFlag=" + runFlag);
                    if (bindSuccessFlag) {
                        // 注册回调接口
                        try {
//							if(DEVICE_MODEL==3505){
//								mIzkcService.setModuleFlag(8);
//							}
                            mIzkcService.registerCallBack("Scanner", mCallback);
                            // 关闭线程
                            runFlag = false;
                            mHandler.sendEmptyMessage(0);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            runFlag = false;
                            e.printStackTrace();
                        }
                    }
                }

            }
        });
        ExecutorFactory.executeThread(new Runnable() {
            @Override
            public void run() {
                while (runFlag) {
                    if (bindSuccessFlag) {
                        //检测打印是否正常
                        try {
                            String printerSoftVersion = mIzkcService.getFirmwareVersion1();
                            if (TextUtils.isEmpty(printerSoftVersion)) {
                                printerSoftVersion = mIzkcService.getFirmwareVersion2();
                            }
                            if (TextUtils.isEmpty(printerSoftVersion)) {
                                mIzkcService.setModuleFlag(module_flag);
                                mHandler.obtainMessage(1).sendToTarget();
                            } else {
                                mHandler.obtainMessage(0, printerSoftVersion).sendToTarget();
                                runFlag = false;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    initScanSet();
                    Log.i("client", "initScanSet1=");
                    break;
                case 1:
//                    playBeepSoundAndVibrate();
                    Log.i("clientd", "text=" + text);

                    if (TextUtils.isEmpty(text)) {
                        break;
                    }
                    if (text.contains("system")) {
                        break;
                    }


                    if (!NetWorkUtils.isNetworkConnected(ScannerActivity.this) || !isconnect) {//无网络
                        if (BusinessManager.isHaveScan(text, cannum)) {//票已检
                            showDialogMsg("已使用!");
                        } else {
                            showresult(text);
                        }
                    } else {//有网络
                        if (text.length() == 6) {
                            PushManager.getInstance(ScannerActivity.this).sendMessage(Utils.getMjScan(ScannerActivity.this, text));
                        } else {
                            String updata = Utils.getscan(ScannerActivity.this, text);
                            PushManager.getInstance(ScannerActivity.this).sendMessage(updata);
                        }
                    }
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            return false;
        }
    });


    @Override
    public void onResume() {
        initScanSet();
        super.onResume();
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


    @OnClick({R.id.top_left,R.id.close_bg})
    void onclick(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                ScannerActivity.this.finish();
                break;
            case R.id.close_bg:
                ExitDialog();
                break;
            default:
                break;
        }
    }

    //扫码
    void initScanSet() {
        if (mIzkcService != null) {
            try {
                mIzkcService.openScan(true);
                mIzkcService.dataAppendEnter(true);
                mIzkcService.openBackLight(0);
                mIzkcService.setModuleFlag(4);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 135:
//			mHandler.sendEmptyMessage(2);
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    //该BroadcastReceiver的意图在于接收扫描按键（受系统控制的产品不起作用），屏幕打开, 屏幕关闭的广播；
    //屏幕打开需要打开扫描模块，唤醒扫描功能；
    //屏幕关闭须要关闭扫描模块，开启省电模式；
    int count = 1;

    public class RemoteControlReceiver extends BroadcastReceiver {
        private static final String TAG = "RemoteControlReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            beginToReceiverData = false;
            Log.i(TAG, "System message " + action);
            if (action.equals("com.zkc.keycode")) {
                if (count++ > 0) {
                    count = 0;
                    int keyValue = intent.getIntExtra("keyvalue", 0);
                    Log.i(TAG, "KEY VALUE:" + keyValue);
                    if (keyValue == 136 || keyValue == 135 || keyValue == 131) {
                        Log.i(TAG, "Scan key down.........");
                        try {
                            if (mIzkcService != null) {
                                mIzkcService.scan();
                                mHandler.sendEmptyMessage(2);
                            } else {
                                mIzkcService.scanGT();
                                mHandler.sendEmptyMessage(2);
                            }

                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            } else if (action.equals("android.intent.action.SCREEN_ON")) {
                Log.i(TAG, "Power off,Close scan modules power.........");
                if (mIzkcService != null) {
                    beginToReceiverData = true;
                    initScanSet();
                }
            } else if (action.equals("android.intent.action.SCREEN_OFF")) {
                Log.i(TAG, "ACTION_SCREEN_OFF,Close scan modules power.........");
                try {
                    if (mIzkcService != null)
                        mIzkcService.openScan(false);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (action.equals("android.intent.action.ACTION_SHUTDOWN")) {
                Log.i(TAG, "ACTION_SCREEN_ON,Open scan modules power.........");
                try {
                    if (mIzkcService != null)
                        mIzkcService.openScan(false);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPause() {
        beginToReceiverData = true;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(screenStatusReceiver);
        super.onDestroy();
        NotificationCenter.defaultCenter().unsubscribe(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(PutEvent.class, putSubscriber);
    }

    //-------------------------
    //在线成功
    PutSubscriber putSubscriber = new PutSubscriber() {
        @Override
        public void onEvent(PutEvent putEvent) {
            String srt = putEvent.getStrs();
            String sgs = putEvent.getStrs().substring(0, 2);
            String renshu = putEvent.getStrs().substring(srt.length() - 2, srt.length());
            if ("06".equals(sgs)) {
                showDialogd("团队票", text, Utils.getXiangmu(ScannerActivity.this), String.valueOf(Integer.parseInt(renshu)));
            } else if ("02".equals(sgs)) {
                showDialogd("儿童票", text, Utils.getXiangmu(ScannerActivity.this), String.valueOf(Integer.parseInt(renshu)));
            } else if ("01".equals(sgs)) {
                showDialogd("成人票", text, Utils.getXiangmu(ScannerActivity.this), String.valueOf(Integer.parseInt(renshu)));
            } else if ("05".equals(sgs)) {
                showDialogd("老年票", text, Utils.getXiangmu(ScannerActivity.this), String.valueOf(Integer.parseInt(renshu)));
            } else if ("03".equals(sgs)) {
                showDialogd("优惠票", text, Utils.getXiangmu(ScannerActivity.this), String.valueOf(Integer.parseInt(renshu)));
            } else if ("07".equals(sgs)) {
                showDialogMsg("已使用");
            } else {
                showDialogMsg("无效票");
            }
        }
    };

    //无效票
    private void showDialogMsg(String names) {
        new ShowMsgDialog(this, R.style.dialog, names, new ShowMsgDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    text = "";
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();
    }

    private void toprinter(String renshu){
            BaseConfig bg = BaseConfig.getInstance(ScannerActivity.this);
            String state= bg.getStringValue(Constants.SHIFOU_PRINT, "0");
            if("0".equals(state)){
                return;
            }else {
                try {
                    mIzkcService.setModuleFlag(0);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                TicketInfo tInfo = new TicketInfo();
                tInfo.setOrderId(bg.getStringValue(Constants.ORDER_ID, ""));
                tInfo.setPrice(Utils.getPrice(ScannerActivity.this));
                tInfo.setpNum(renshu);
                tInfo.setItem(Utils.getXiangmu(ScannerActivity.this));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                tInfo.setpTime(str);
                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
                Date curDate1 = new Date(System.currentTimeMillis());//获取当前时间
                String str1 = formatter1.format(curDate1);
                tInfo.setOrderTime(str1 + "至" + str1);
                PrinterHelper.getInstance(ScannerActivity.this).printPurchaseBillModelTwo(mIzkcService, tInfo);
                try {
                    mIzkcService.setModuleFlag(4);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
    }


    //分析二维码-无线
    private void showresult(String strs) {
        int a = AnalysisHelp.useScan(ScannerActivity.this, strs);
        Log.i("tttt", "aaaaaa=" + a);
        if (a == 1) {//1------可用
            showDialog(Utils.getXiangmu(ScannerActivity.this), strs, AnalysisHelp.renshu(strs) + "");
        } else if (a == 2) {
            showDialogMsg("票已过期!");
        } else if (a == 3) {
            showDialogMsg("票型不符合!");
        } else if (a == 4) {
            showDialog(Utils.getXiangmu(ScannerActivity.this), strs, "");//梅江
        } else {
            showDialogMsg("无效票!");
        }
    }


    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber() {
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(ScannerActivity.this);
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
            BaseConfig bg = new BaseConfig(ScannerActivity.this);
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

    //无线
    private void showDialog(String num, final String code, final String reshu) {
        new ScanDialog(this, R.style.dialog, num, "", reshu, new ScanDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    DataInfo dataInfo = new DataInfo();
                    if (BusinessManager.isHaveuse(text, cannum) == 0) {
                        dataInfo.setCanuse(1);
                        Log.i("mmmm", "setCanuse=");
                        if (code.length()==6){
                            dataInfo.setId(code);
                            dataInfo.setNet(false);
                            dataInfo.setName(Utils.getXiangmu(ScannerActivity.this));
                            dataInfo.setType(2);
                            dataInfo.setInsertTime(System.currentTimeMillis() + "");
                            dataInfo.setUp(false);
                        }else {
                            dataInfo.setId(code);
                            dataInfo.setNet(false);
                            dataInfo.setpNum(reshu);
                            dataInfo.setName(Utils.getXiangmu(ScannerActivity.this));
                            dataInfo.setType(2);
                            dataInfo.setInsertTime(System.currentTimeMillis() + "");
                            dataInfo.setUp(false);
                        }
                        OfflLineDataDb.insert(dataInfo);
                    } else if (BusinessManager.isHaveuse(text, cannum) > 0) {
                        int isuse = BusinessManager.isHaveuse(text, cannum);
                        DataInfo a = OfflLineDataDb.getDB().selectById(null, DataInfo.class, text);
                        a.setCanuse(isuse + 1);
                        OfflLineDataDb.updata(a);
                    }
                    text = "";
                    dialog.dismiss();
                    toprinter(reshu);
                }
            }
        }).setTitle("提示").show();
    }

    //有线
    private void showDialogd(String num, final String code, String name, final String renshu) {
        new ScanDialog(this, R.style.dialog, num, name, renshu, new ScanDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    DataInfo dataInfo = new DataInfo();
                    if (BusinessManager.isHaveuse(text, cannum) == 0) {
                        dataInfo.setCanuse(1);
                        if (code.length()==6){
                            dataInfo.setId(code);
                            dataInfo.setNet(false);
                            dataInfo.setName(Utils.getXiangmu(ScannerActivity.this));
                            dataInfo.setType(2);
                            dataInfo.setInsertTime(System.currentTimeMillis() + "");
                            dataInfo.setUp(false);
                        } else {
                            dataInfo.setId(code);
                            dataInfo.setNet(true);
                            dataInfo.setpNum(renshu);
                            dataInfo.setName(Utils.getXiangmu(ScannerActivity.this));
                            dataInfo.setType(2);
                            dataInfo.setInsertTime(System.currentTimeMillis() + "");
                            dataInfo.setUp(true);
                        }
                        OfflLineDataDb.insert(dataInfo);
                    } else {
                        int isuse = BusinessManager.isHaveuse(text, cannum);
                        DataInfo a = OfflLineDataDb.getDB().selectById(null, DataInfo.class, text);
                        a.setCanuse(isuse + 1);
                        OfflLineDataDb.updata(a);
                    }
                    text = "";
                    dialog.dismiss();
                    toprinter(renshu);
                }
            }
        }).setTitle("提示").show();
    }
}
