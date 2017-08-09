package com.eb.sc;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.activity.CaptureActivity;
import com.eb.sc.activity.DetailActivity;
import com.eb.sc.base.BaseActivity;
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
import com.eb.sc.tcprequest.TcpResponse;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.HexStr;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.utils.Utils;
import com.eb.sc.widget.CommomDialog;
import com.eb.sc.widget.ShowMsgDialog;
import com.zkteco.android.IDReader.IDPhotoHelper;
import com.zkteco.android.IDReader.WLTService;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.module.fingerprint.FingerprintFactory;
import com.zkteco.android.biometric.module.fingerprint.FingerprintSensor;
import com.zkteco.android.biometric.module.fingerprint.exception.FingerprintSensorException;
import com.zkteco.android.biometric.module.idcard.IDCardReader;
import com.zkteco.android.biometric.module.idcard.IDCardReaderFactory;
import com.zkteco.android.biometric.module.idcard.exception.IDCardReaderException;
import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo;

import org.aisen.android.component.eventbus.NotificationCenter;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    private IDCardReader idCardReader = null;
    private FingerprintSensor fingerprintSensor = null;
    private byte[] feature = null;

    final static int idPort = 13;
    final static String idPower = "5V";

    final static int fpPort = 0;
    final static String fpPower = "rfid power,scan power";

    final static int baudrate = 115200;

    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;

    private boolean mbStop = false;
    private MediaPlayer mMediaPlayer = null;
    private SoundPool soundPool = null;
    private HashMap<Integer, Integer> soundPoolMap = null;

    private WorkThread workThread = null;
    private WorkThreadVerFP workThreadVerFP = null;
    private boolean mbVerifying = false;
    private String mLastName = "", idcard_id = "";
    BarAsyncTask task;
    private boolean isconnect = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(PutEvent.class, putSubscriber);
        LogHelper.setLevel(Log.VERBOSE);
        BaseConfig bg = new BaseConfig(this);
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
        top_title.setText("身份证");
        task = new BarAsyncTask();
        task.execute();
    }


    private void startIDCardReader() {
        // Start fingerprint sensor
        Map idrparams = new HashMap();
        idrparams.put(ParameterHelper.PARAM_SERIAL_BAUDRATE, baudrate);
        idrparams.put(ParameterHelper.PARAM_POWRER_STR, idPower);
        idrparams.put(ParameterHelper.PARAM_WAIT_SETON, 1000);
        //idrparams.put(ParameterHelper.PARAM_GPIO_STR, -1);
        idrparams.put(ParameterHelper.PARAM_GPIO_STR, 9);
        idCardReader = IDCardReaderFactory.createIDCardReader(this, TransportType.SERIALPORT, idrparams);
    }

    private void startFPSensor() {
        // Start fingerprint sensor
        Map fpparams = new HashMap();
        fpparams.put(ParameterHelper.PARAM_SERIAL_BAUDRATE, baudrate);
        fpparams.put(ParameterHelper.PARAM_POWRER_STR, fpPower);
        //fpparams.put(ParameterHelper.PARAM_WAIT_SETON, 3000);
        //fpparams.put(ParameterHelper.PARAM_GPIO_STR, 9);
        fpparams.put(ParameterHelper.PARAM_WAIT_SETON, 1000);
        fpparams.put(ParameterHelper.PARAM_GPIO_STR, -1);
        fingerprintSensor = FingerprintFactory.createFingerprintSensor(this, TransportType.SERIALPORT, fpparams);
    }

    private boolean openDevices() {
        boolean bRet = false;
        try {
            idCardReader.open(idPort);
        } catch (IDCardReaderException e) {
            e.printStackTrace();
            bRet = false;
            return bRet;
        }
        try {
            fingerprintSensor.open(fpPort);
        } catch (FingerprintSensorException e) {
            e.printStackTrace();
            bRet = false;
            try {
                idCardReader.close(idPort);
            } catch (IDCardReaderException e1) {
                e1.printStackTrace();
            }
            return bRet;
        }
        bRet = true;
        mbStop = false;
        workThread = new WorkThread();
        workThread.start();// 线程启动
        return bRet;
    }


    /**
     * initialize sounds
     */
    @SuppressLint("UseSparseArrays")
    public void initSounds() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.effect);
        soundPool = new SoundPool(9, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(1, soundPool.load(this, R.raw.thank_you, 1));
        soundPoolMap.put(2, soundPool.load(this, R.raw.please_try_again, 1));
        soundPoolMap.put(3, soundPool.load(this, R.raw.ao_ou, 1));
        soundPoolMap.put(4, soundPool.load(this, R.raw.invalid_id, 1));
        soundPoolMap.put(5, soundPool.load(this, R.raw.re_enter_id, 1));
        soundPoolMap.put(6, soundPool.load(this, R.raw.do_repeart_finger, 1));
        soundPoolMap.put(7, soundPool.load(this, R.raw.alarm, 1));
        soundPoolMap.put(8, soundPool.load(this, R.raw.effect, 1));
        soundPoolMap.put(9, soundPool.load(this, R.raw.beep, 1));
        soundPoolMap.put(10, soundPool.load(this, R.raw.readsucc, 1));
        soundPoolMap.put(11, soundPool.load(this, R.raw.readfail, 1));
        soundPoolMap.put(12, soundPool.load(this, R.raw.fpsucc, 1));
        soundPoolMap.put(13, soundPool.load(this, R.raw.fpfail, 1));
        soundPoolMap.put(14, soundPool.load(this, R.raw.fptimeout, 1));
    }

    /**
     * play sounds
     */
    public void playSound(int sound, int loop) {
        AudioManager mgr = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 1f);
    }

    public void playSoundMedia(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }


    private class WorkThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!mbStop) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (!mbVerifying) {
                            if (!ReadCardInfo()) {
                                //textView.setText("请放卡...");
                            } else {
//                                textView.setText("读卡成功，请放入下一张卡");

                            }
                        }
                    }
                });

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private class WorkThreadVerFP extends Thread {
        @Override
        public void run() {
            super.run();
            mbVerifying = true;
            final int ret = verifyFinger();
            runOnUiThread(new Runnable() {
                public void run() {
                    long time = System.currentTimeMillis();
                    final Calendar mCalendar = Calendar.getInstance();
                    mCalendar.setTimeInMillis(time);
                    String strResult = "";
                    if (1 == ret) {
                        strResult = "指纹核验成功...";
                        playSound(12, 1);
                    } else {
                        strResult = "指纹核验失败...";
                        playSound(13, 1);
                    }
//                    infoResult.append(mCalendar.get(Calendar.YEAR) + "-" + mCalendar.get(Calendar.MONTH) + "-" + mCalendar.get(Calendar.DAY_OF_MONTH) + " " +
//                            mCalendar.get(Calendar.HOUR) + ":" + mCalendar.get(Calendar.MINUTE) + ":" + mCalendar.get(Calendar.SECOND)
//                            + " " + mLastName + strResult + "\r\n");
//                    mbVerifying = false;
//                    textView.setText("请放卡...");
                }
            });

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            workThreadVerFP = null;
        }
    }

    private int verifyFinger() {
        int ret = 0;
        if (null != feature) {
            try {
                ret = fingerprintSensor.verifyByFeature(fpPort, feature);
            } catch (FingerprintSensorException e) {
                e.printStackTrace();
                ret = 0;
            }
        }
        return ret;
    }


    //身份证以刷
    public void OnBtnVerify(View view) {
        if (null == feature) {
//            textView.setText("您的身份证未登记指纹！");
            playSound(9, 1);
            return;
        }
        if (null != workThreadVerFP || mbVerifying) {
            //textView.setText("操作太频繁！");
            //playSound(9, 1);
            return;
        }
        workThreadVerFP = new WorkThreadVerFP();
        workThreadVerFP.start();// 线程启动
    }

    private boolean ReadCardInfo() {
        try {
            if (!idCardReader.findCard(idPort) || !idCardReader.selectCard(idPort)) {
                return false;
            }
        } catch (IDCardReaderException e) {
            e.printStackTrace();
            return false;
        }
        try {
            feature = null;
//            textView.setText("正在读卡...");//111111111111111--------------------------------------------------------
            final IDCardInfo idCardInfo = new IDCardInfo();
            boolean bReadCard = false;
            long nTickSet = System.currentTimeMillis();
            while (System.currentTimeMillis() - nTickSet < 2000) {
                bReadCard = idCardReader.readCard(idPort, 1, idCardInfo);
                if (bReadCard) break;
            }
            if (bReadCard) {
                playSound(10, 1);
                long time = System.currentTimeMillis();
                final Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTimeInMillis(time);
                mLastName = idCardInfo.getName();
//                infoResult.append(mCalendar.get(Calendar.YEAR) + "-" + mCalendar.get(Calendar.MONTH) + "-" + mCalendar.get(Calendar.DAY_OF_MONTH) + " " +
//                        mCalendar.get(Calendar.HOUR) + ":" + mCalendar.get(Calendar.MINUTE) + ":" + mCalendar.get(Calendar.SECOND)
//                        + " " + mLastName + "刷卡成功！\r\n");
                idcard_id = idCardInfo.getId();
                if (BusinessManager.isHave(idCardInfo.getId())) {//票已检
                    showDialogMsg("票已使用!");
                } else {
                    if (NetWorkUtils.isNetworkConnected(this) && isconnect) {
//                        byte[] updatas = HexStr.hex2byte(HexStr.str2HexStr(Utils.getIdcard(this,idCardInfo.getId())));
                        String updata = Utils.getIdcard(this, idCardInfo.getId());
                        PushManager.getInstance(this).sendMessage(updata);
                    } else {
                        showDialog(mLastName, idCardInfo.getId(), "");
                    }
                }
                feature = idCardInfo.getFpdata();
//                if (idCardInfo.getPhoto() != null) {
//                    byte[] buf = new byte[WLTService.imgLength];
//                    if (1 == WLTService.wlt2Bmp(idCardInfo.getPhoto(), buf)) {
//                        Bitmap bitmap = IDPhotoHelper.Bgr2Bitmap(buf);
//                        if (null != bitmap) {
//
//
//                        }
//                    }
//                }
                return true;
            } else {
                //playSound(9, 0);
            }
        } catch (IDCardReaderException e) {
            e.printStackTrace();
        }

        playSound(11, 1);
//        textView.setText("读卡失败...");//------------------------------------------------------------------------------------------------
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter().unsubscribe(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(PutEvent.class, putSubscriber);
        mbStop = true;
        // Destroy fingerprint sensor when it's not used
        IDCardReaderFactory.destroy(idCardReader);
        FingerprintFactory.destroy(fingerprintSensor);
    }

    @OnClick({R.id.top_left})
    void onclick(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                MainActivity.this.finish();
                break;
        }
    }

    //有效票-无线
    private void showDialog(String names, final String num, String code) {
        new CommomDialog(this, R.style.dialog, names, num, code, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    DataInfo data = new DataInfo();
                    data.setId(num);
                    data.setUp(true);
                    data.setNet(false);
                    data.setType(1);
                    data.setName(Utils.getXiangmu(MainActivity.this));
                    data.setInsertTime(System.currentTimeMillis() + "");
                    OfflLineDataDb.insert(data);
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();
    }

    //有效票-有线
    private void showDialogd(String names, final String num, String code) {
        new CommomDialog(this, R.style.dialog, names, num, code, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    DataInfo data = new DataInfo();
                    data.setId(num);
                    data.setUp(false);
                    data.setNet(true);
                    data.setType(1);
                    data.setName(Utils.getXiangmu(MainActivity.this));
                    data.setInsertTime(System.currentTimeMillis() + "");
                    OfflLineDataDb.insert(data);
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
                }
            }
        }).setTitle("提示").show();
    }


    class BarAsyncTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            startIDCardReader();
            startFPSensor();
            initSounds();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (!openDevices()) {
                Toast.makeText(MainActivity.this, "打开设备失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //在线成功
    PutSubscriber putSubscriber = new PutSubscriber() {
        @Override
        public void onEvent(PutEvent putEvent) {
            String sgs = putEvent.getStrs().substring(0, 2);
            if ("01".equals(sgs)) {
                showDialogMsg("无效票");
            } else if ("02".equals(sgs)) {
                showDialogMsg("已使用");
            } else {
                showDialogd(Utils.pullScan(putEvent.getStrs()), idcard_id, Utils.getXiangmu(MainActivity.this));
            }
        }
    };

    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber() {
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(MainActivity.this);
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

