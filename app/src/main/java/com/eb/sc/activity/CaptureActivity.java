package com.eb.sc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.bean.DataInfo;
import com.eb.sc.bean.TicketInfo;
import com.eb.sc.business.BusinessManager;
import com.eb.sc.offline.OfflLineDataDb;
import com.eb.sc.priter.PrinterActivity;
import com.eb.sc.priter.PrinterHelper;
import com.eb.sc.scan.CaptureActivityHandler;
import com.eb.sc.scan.InactivityTimer;
import com.eb.sc.scan.camera.CameraManager;
import com.eb.sc.scanner.BaseActivity;
import com.eb.sc.scanner.ExecutorFactory;
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
import com.eb.sc.utils.Utils;
import com.eb.sc.widget.ScanDialog;
import com.eb.sc.widget.ShowMsgDialog;

import org.aisen.android.component.eventbus.NotificationCenter;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/*
* create 2017-7-29    lyj
* 二维码扫描
* */


public class CaptureActivity extends BaseActivity implements Callback {
    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    private boolean vibrate;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;
    private RelativeLayout mContainer = null;
    private RelativeLayout mCropLayout = null;
    private boolean runFlag = true;
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    private boolean isconnect = true;
    private String scansts = "";
    private int cannum = 1;
    private String select = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        ButterKnife.bind(this);
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

        initView();
    }

   private void initView() {
        Bundle bd=getIntent().getExtras();
        if(bd!=null){
            select=bd.getString("select");
        }

        // 初始化 CameraManager
        BaseConfig bg = BaseConfig.getInstance(this);
        try {
            cannum = Integer.parseInt(bg.getStringValue(Constants.X_NUM, "1"));
        } catch (Exception e) {

        }
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(PutEvent.class, putSubscriber);
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
        mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);

        ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
        ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(1200);
        mQrLineView.startAnimation(animation);
       initData();
   }

    private void initData() {
        if("1".equals(select)){
            top_title.setText("扫描收款码");
        }else {
            top_title.setText("扫描");
        }

    }

    Handler mHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                case 0:
                    try {
                        mIzkcService.setModuleFlag(0);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case 1:
//                    Toast.makeText(PrinterActivity.this, "正在连接打印机，请稍后...", Toast.LENGTH_SHORT).show();
                    break;
                case 8:
                    break;
                default:
                    break;
            }
            return false;
        }
    });



    private void toprinter() {
        BaseConfig bg = BaseConfig.getInstance(CaptureActivity.this);
        TicketInfo tInfo = new TicketInfo();
        tInfo.setOrderId(bg.getStringValue(Constants.ORDER_ID, ""));
        tInfo.setPrice("20");
        tInfo.setpNum("2");
        PrinterHelper.getInstance(CaptureActivity.this).printPurchaseBillModelTwo(mIzkcService, tInfo);
    }


    boolean flag = true;

    protected void light() {
        if (flag == true) {
            flag = false;
            // 开闪光灯
            CameraManager.get().openLight();
        } else {
            flag = true;
            // 关闪光灯
            CameraManager.get().offLight();
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
        NotificationCenter.defaultCenter().unsubscribe(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(PutEvent.class, putSubscriber);
    }

    public void handleDecode(String result) {
        this.scansts = result;
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        if("1".equals(select)){
            Intent intent = new Intent(CaptureActivity.this,PrinterActivity.class);
            intent.putExtra("scansts", scansts);
            setResult(RESULT_OK, intent);
            CaptureActivity.this.finish();
        }else
        if (!NetWorkUtils.isNetworkConnected(CaptureActivity.this) || !isconnect) {//无网络
            if (BusinessManager.isHaveScan(result, cannum)) {//票已检
                showDialogMsg("已使用!");
            } else {
                showresult(result);
            }
        } else {//有网络
            if (result.length() == 6) {
                PushManager.getInstance(this).sendMessage(Utils.getMjScan(this, result));
            } else {
                String updata = Utils.getscan(this, result);
                PushManager.getInstance(this).sendMessage(updata);
            }
        }
        // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
//		handler.sendEmptyMessage(R.id.restart_preview);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);

            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;

            int x = mCropLayout.getLeft() * width / mContainer.getWidth();
            int y = mCropLayout.getTop() * height / mContainer.getHeight();

            int cropWidth = mCropLayout.getWidth() * width
                    / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height
                    / mContainer.getHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);

        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(CaptureActivity.this);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public Handler getHandler() {
        return handler;
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.scan);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    //无线
    private void showDialog(String num, final String code) {
        new ScanDialog(this, R.style.dialog, num, "", "", new ScanDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    DataInfo dataInfo = new DataInfo();
                    if (BusinessManager.isHaveuse(scansts, cannum) == 0) {
                        dataInfo.setCanuse(1);
                        Log.i("mmmm", "setCanuse=");
                        if (!code.contains("&")) {
                            dataInfo.setId(code);
                            dataInfo.setNet(false);
                            dataInfo.setName(Utils.getXiangmu(CaptureActivity.this));
                            dataInfo.setType(2);
                            dataInfo.setInsertTime(System.currentTimeMillis() + "");
                            dataInfo.setUp(false);
                        } else {
                            String arr[] = AnalysisHelp.arrayScan(code);
                            dataInfo.setId(code);
                            dataInfo.setNet(false);
                            dataInfo.setName(Utils.getXiangmu(CaptureActivity.this));
                            dataInfo.setType(2);
                            dataInfo.setValidTime(arr[1]);
                            dataInfo.setInsertTime(System.currentTimeMillis() + "");
                            dataInfo.setUp(false);
                        }
                        OfflLineDataDb.insert(dataInfo);
                    } else if (BusinessManager.isHaveuse(scansts, cannum) > 0){
                        int isuse = BusinessManager.isHaveuse(scansts, cannum);
                        DataInfo a = OfflLineDataDb.getDB().selectById(null, DataInfo.class, scansts);
                        a.setCanuse(isuse + 1);
                        OfflLineDataDb.updata(a);
                        Log.i("mmmm", "BusinessManager=" + String.valueOf(isuse + 1));
                    }

                    handler.sendEmptyMessage(R.id.restart_preview);
                    dialog.dismiss();
                    toprinter();
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
                    if (BusinessManager.isHaveuse(scansts, cannum) == 0) {
                        dataInfo.setCanuse(1);
                        if (!code.contains("&")) {
                            dataInfo.setId(code);
                            dataInfo.setNet(true);
                            dataInfo.setpNum(renshu);
                            dataInfo.setName(Utils.getXiangmu(CaptureActivity.this));
                            dataInfo.setType(2);
                            dataInfo.setInsertTime(System.currentTimeMillis() + "");
                            dataInfo.setUp(true);
                        } else {
                            String arr[] = AnalysisHelp.arrayScan(code);
                            dataInfo.setId(code);
                            dataInfo.setpNum(renshu);
                            dataInfo.setNet(true);
                            dataInfo.setType(2);
                            dataInfo.setValidTime(arr[1]);
                            dataInfo.setName(Utils.getXiangmu(CaptureActivity.this));
                            dataInfo.setInsertTime(System.currentTimeMillis() + "");
                            dataInfo.setUp(true);
                        }
                        OfflLineDataDb.insert(dataInfo);
                    } else {
                        int isuse = BusinessManager.isHaveuse(scansts, cannum);
                        DataInfo a = OfflLineDataDb.getDB().selectById(null, DataInfo.class, scansts);
                        a.setCanuse(isuse + 1);
                        OfflLineDataDb.updata(a);
                    }
                    handler.sendEmptyMessage(R.id.restart_preview);
                    dialog.dismiss();
                    toprinter();
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
            String renshu = putEvent.getStrs().substring(srt.length() - 2, srt.length());
            if ("06".equals(sgs)) {
                showDialogd("团队票", scansts, Utils.getXiangmu(CaptureActivity.this), String.valueOf(Integer.parseInt(renshu)));
            } else if ("02".equals(sgs)) {
                showDialogd("儿童票", scansts, Utils.getXiangmu(CaptureActivity.this), String.valueOf(Integer.parseInt(renshu)));
            } else if ("01".equals(sgs)) {
                showDialogd("成人票", scansts, Utils.getXiangmu(CaptureActivity.this), String.valueOf(Integer.parseInt(renshu)));
            } else if ("05".equals(sgs)) {
                showDialogd("老年票", scansts, Utils.getXiangmu(CaptureActivity.this), String.valueOf(Integer.parseInt(renshu)));
            } else if ("03".equals(sgs)) {
                showDialogd("优惠票", scansts, Utils.getXiangmu(CaptureActivity.this), String.valueOf(Integer.parseInt(renshu)));
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
                    handler.sendEmptyMessage(R.id.restart_preview);
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();
    }

    @OnClick({R.id.top_left})
    void onclick(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                CaptureActivity.this.finish();
                break;
        }
    }

    //分析二维码-无线
    private void showresult(String strs) {
        Log.i("tttt", "strs=" + strs);
        int a = AnalysisHelp.useScan(CaptureActivity.this, strs);
        if (a == 1) {//1------可用
            showDialog(Utils.getXiangmu(CaptureActivity.this), strs);
        } else if (a == 2) {
            showDialogMsg("票已过期!");
        } else if (a == 3) {
            showDialogMsg("票型不符合!");
        } else if (a == 4) {
            showDialog(Utils.getXiangmu(CaptureActivity.this), strs);//梅江
        } else {
            showDialogMsg("无效票!");
        }
    }

    //分析二维码
    private void showresultd(String strs) {
        int a = AnalysisHelp.StringScan(CaptureActivity.this, strs);
        if (a == 1) {//1------可用
            showDialogd("", strs, Utils.getXiangmu(CaptureActivity.this), "");
        } else if (a == 2) {
            showDialogMsg("票已过期!");
        } else if (a == 3) {
            showDialogMsg("票型不符合!");
        } else {
            showDialogMsg("未检测到票!");
        }
    }

    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber() {
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(CaptureActivity.this);
            String a = bg.getStringValue(Constants.havenet, "-1");
            if (event.isConnect()) {
                isconnect = true;
                if ("1".equals(a)) {

                } else {

                }
            } else {
                isconnect = false;
            }

        }
    };
    //网络
    EventSubscriber netEventSubscriber = new EventSubscriber() {
        @Override
        public void onEvent(NetEvent event) {
            BaseConfig bg = new BaseConfig(CaptureActivity.this);
            if (event.isConnect()) {
                if (isconnect) {

                } else {

                }
            } else {

            }
        }
    };


}