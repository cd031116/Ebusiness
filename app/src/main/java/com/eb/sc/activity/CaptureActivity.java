package com.eb.sc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
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
import android.widget.Toast;

import com.eb.sc.MainActivity;
import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.bean.DataInfo;
import com.eb.sc.business.BusinessManager;
import com.eb.sc.offline.OfflLineDataDb;
import com.eb.sc.scan.CaptureActivityHandler;
import com.eb.sc.scan.InactivityTimer;
import com.eb.sc.scan.camera.CameraManager;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.EventSubscriber;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.tcprequest.PushService;
import com.eb.sc.tcprequest.TcpResponse;
import com.eb.sc.utils.AnalysisHelp;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.HexStr;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.utils.Utils;
import com.eb.sc.widget.CommomDialog;
import com.eb.sc.widget.ScanDialog;
import com.eb.sc.widget.ShowMsgDialog;

import org.aisen.android.component.eventbus.NotificationCenter;

import java.io.IOException;

import butterknife.Bind;
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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_qr_scan;
    }

    @Override
    public void initView() {
        super.initView();
        // 初始化 CameraManager
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
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
    }

    @Override
    public void initData() {
        super.initData();
        top_title.setText("扫描");
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
    }
private  String scanstrs="";
    public void handleDecode(String result) {
        this.scanstrs=result;
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();


        if (BusinessManager.isHave(result)) {//票已检
            showDialogMsg("无效票!");
        } else {
            if (!NetWorkUtils.isNetworkConnected(CaptureActivity.this)||!isconnect) {//无网络
                showresult(result);
            } else {//有网络
                Log.i("tttt","sssssssssss="+Utils.getscan(this,result));
               String updata =HexStr.str2HexStr(Utils.getscan(this,result));
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

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
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


    private void showDialog(String num, final  String code) {
        new ScanDialog(this, R.style.dialog, num, code, new ScanDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    String arr[]=AnalysisHelp.arrayScan(code);
                    DataInfo dataInfo=new DataInfo();
                    dataInfo.setId(code);
                    dataInfo.setNet(false);
                    dataInfo.setType(2);
                    dataInfo.setValidTime(arr[1]);
                    dataInfo.setInsertTime(System.currentTimeMillis()+"");
                    dataInfo.setUp(false);
                    OfflLineDataDb.insert(dataInfo);
                    handler.sendEmptyMessage(R.id.restart_preview);
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();
    }
    //有线
    private void showDialogd(String num, final  String code) {
        new ScanDialog(this, R.style.dialog, num, code, new ScanDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    String arr[]=AnalysisHelp.arrayScan(code);
                    DataInfo dataInfo=new DataInfo();
                    dataInfo.setId(code);
                    dataInfo.setNet(true);
                    dataInfo.setType(2);
                    dataInfo.setValidTime(arr[1]);
                    dataInfo.setInsertTime(System.currentTimeMillis()+"");
                    dataInfo.setUp(true);
                    OfflLineDataDb.insert(dataInfo);
                    handler.sendEmptyMessage(R.id.restart_preview);
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();
    }


    //无效票
    private void showDialogMsg(String names) {
        new ShowMsgDialog(this, R.style.dialog, names, new ShowMsgDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    dialog.dismiss();
                    handler.sendEmptyMessage(R.id.restart_preview);
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
        int a = AnalysisHelp.StringScan(CaptureActivity.this,strs);
        if (a == 1) {//1------可用
            showDialog(null, strs);
        } else if (a == 2) {
            showDialogMsg("票已过期!");
        } else if (a == 3) {
            showDialogMsg("票型不符合!");
        } else {
            showDialogMsg("未检测到票!");
        }
    }
    //分析二维码-无线
    private void showresultd(String strs) {
        int a = AnalysisHelp.StringScan(CaptureActivity.this,strs);
        if (a == 1) {//1------可用
            showDialogd(null, strs);
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