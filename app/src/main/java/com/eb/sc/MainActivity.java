package com.eb.sc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.eb.sc.base.BaseActivity;
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

import org.aisen.android.support.inject.ViewInject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {
    private IDCardReader idCardReader = null;
    private FingerprintSensor fingerprintSensor = null;
    private byte[] feature = null;

    final static int idPort = 13;
    final static String idPower = "5V";

    final static int fpPort = 0;
    final static String fpPower = "rfid power,scan power";

    final static int baudrate = 115200;
    @ViewInject(id=R.id.main_textview)
     TextView textView;
    @ViewInject(id=R.id.user_name)
    TextView infoName;
    @ViewInject(id=R.id.user_sex)
     TextView infoSex;
    @ViewInject(id=R.id.user_nation)
     TextView infoNation;
    @ViewInject(id=R.id.user_birth)
     TextView infoBirth;
    @ViewInject(id=R.id.user_address)
     TextView infoAddress;
    @ViewInject(id=R.id.user_id)
     TextView infoIdcard;
    @ViewInject(id=R.id.user_certifying)
     TextView infoCertifying;
    @ViewInject(id=R.id.user_data)
     TextView infoData;
    @ViewInject(id=R.id.user_number)
     TextView infoNumber;
    @ViewInject(id=R.id.editText)
     EditText infoResult;
    @ViewInject(id=R.id.idPhoto)
     ImageView image;
    @ViewInject(id=R.id.checkFinger)
     CheckBox checkFinger = null;
    @ViewInject(id=R.id.btnVerify)
     Button btnVerify = null;

    private boolean mbStop = false;

    private MediaPlayer mMediaPlayer = null;
    private SoundPool soundPool = null;
    private HashMap<Integer, Integer> soundPoolMap = null;

    private WorkThread workThread = null;
    private WorkThreadVerFP workThreadVerFP = null;
    private boolean mbVerifying = false;
    private String mLastName = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        initSounds();
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LogHelper.setLevel(Log.VERBOSE);
        initUI();
        startIDCardReader();
        startFPSensor();
        if (!openDevices()) {
            textView.setText("Open device failed!");
        }
    }

    @Override
    public void initData() {
        super.initData();
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

    private void initUI() {
        infoResult.setFocusable(false);
        infoResult.setEnabled(false);
        infoResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        infoResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        image = (ImageView) findViewById(R.id.idPhoto);
        btnVerify.setVisibility(View.INVISIBLE);
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
                                textView.setText("读卡成功，请放入下一张卡");
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
                    infoResult.append(mCalendar.get(Calendar.YEAR) + "-" + mCalendar.get(Calendar.MONTH) + "-" + mCalendar.get(Calendar.DAY_OF_MONTH) + " " +
                            mCalendar.get(Calendar.HOUR) + ":" + mCalendar.get(Calendar.MINUTE) + ":" + mCalendar.get(Calendar.SECOND)
                            + " " + mLastName + strResult + "\r\n");
                    mbVerifying = false;
                    textView.setText("请放卡...");
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


    public void OnBtnClear(View view) {
        infoResult.setText("");
    }

    public void OnBtnVerify(View view) {
        if (null == feature) {
            textView.setText("您的身份证未登记指纹！");
            playSound(9, 1);
            return;
        }
        if (null != workThreadVerFP || mbVerifying) {
            //textView.setText("操作太频繁！");
            //playSound(9, 1);
            return;
        }
        textView.setText("请按手指...");
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
            resetContent();
            textView.setText("正在读卡...");
            checkFinger.setChecked(false);
            btnVerify.setVisibility(View.INVISIBLE);
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
                infoResult.append(mCalendar.get(Calendar.YEAR) + "-" + mCalendar.get(Calendar.MONTH) + "-" + mCalendar.get(Calendar.DAY_OF_MONTH) + " " +
                        mCalendar.get(Calendar.HOUR) + ":" + mCalendar.get(Calendar.MINUTE) + ":" + mCalendar.get(Calendar.SECOND)
                        + " " + mLastName + "刷卡成功！\r\n");
                infoName.setText(mLastName);
                infoSex.setText(idCardInfo.getSex());
                infoNation.setText(idCardInfo.getNation());
                infoBirth.setText(idCardInfo.getBirth());
                infoAddress.setText(idCardInfo.getAddress());
                infoIdcard.setText(idCardInfo.getId());
                infoCertifying.setText(idCardInfo.getDepart());
                infoData.setText(idCardInfo.getValidityTime());
                feature = idCardInfo.getFpdata();
                if (feature != null) {
                    checkFinger.setChecked(true);
                    btnVerify.setVisibility(View.VISIBLE);
                }

                if (idCardInfo.getPhoto() != null) {
                    byte[] buf = new byte[WLTService.imgLength];
                    if (1 == WLTService.wlt2Bmp(idCardInfo.getPhoto(), buf)) {
                        Bitmap bitmap = IDPhotoHelper.Bgr2Bitmap(buf);
                        if (null != bitmap) {
                            image.setImageBitmap(bitmap);
                        }
                    }
                }
                return true;
            } else {
                //playSound(9, 0);
            }
        } catch (IDCardReaderException e) {
            e.printStackTrace();
        }

        playSound(11, 1);
        textView.setText("读卡失败...");
        return false;
    }


    public void resetContent() {
        infoName.setText("");
        infoSex.setText("");
        infoNation.setText("");
        infoBirth.setText("");
        infoAddress.setText("");
        infoIdcard.setText("");
        infoCertifying.setText("");
        infoData.setText("");
        image.setImageBitmap(null);
    }


    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (((keyCode == KeyEvent.KEYCODE_BACK) || keyCode == KeyEvent.KEYCODE_HOME)
                && event.getRepeatCount() == 0) {
            dialog_Exit(MainActivity.this);
        }
        return false;
    }

    ;

    public void dialog_Exit(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确定要退出程序?");
        builder.setTitle("注意");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
                // android.os.Process.killProcess(android.os.Process.myPid());
                onDestroy();
            }
        });


        builder.setNegativeButton("否",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mbStop = true;
        // Destroy fingerprint sensor when it's not used
        IDCardReaderFactory.destroy(idCardReader);
        FingerprintFactory.destroy(fingerprintSensor);
        System.exit(0);
    }
}

