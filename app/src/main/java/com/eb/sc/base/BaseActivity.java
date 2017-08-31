package com.eb.sc.base;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.R;
import com.eb.sc.utils.SupportMultipleScreensUtil;
import com.smartdevice.aidl.IZKCService;

import butterknife.ButterKnife;


/**
 * Created by lyj on 2017/2/6 0006.
 */

public class BaseActivity extends AppCompatActivity implements BaseViewInterface {
    protected View contentView;
    private TextView msg;
    InputMethodManager manager;

    public static String MODULE_FLAG = "module_flag";
    public static int module_flag = 0;
    public static int DEVICE_MODEL = 0;
    ScreenOnOffReceiver mReceiver = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.instance.getActivityManager().pushActivity(this);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (getLayoutId()!= 0){
            // setContentView(getLayoutId());
            contentView = View.inflate(this, getLayoutId(), null);
            setContentView(contentView);
            View rootView=findViewById(android.R.id.content);
            SupportMultipleScreensUtil.init(getApplication());
            SupportMultipleScreensUtil.scale(rootView);
        }
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        module_flag = getIntent().getIntExtra(MODULE_FLAG, 8);
        bindService();
        mReceiver = new ScreenOnOffReceiver();
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, screenStatusIF);

        initView();
        initData();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        MyApplication.instance.getActivityManager().popActivity(this);
//        if (EventBus.getDefault().isRegistered(this)){
//            EventBus.getDefault().unregister(this);
//        }
    }

    protected int getLayoutId(){
        return 0;
    }



    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }
    @Override
    public void finish() {
        super.finish();
//		this.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
//		this.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
//		this.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    protected void showPopwindow(PopupWindow popWin) {
        // -----------------------
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        popWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        // -----------------------
    }


    public static boolean isConnect(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.v("mcn", e.toString());
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        // TODO Auto-generated method stub
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    protected boolean onHome() {
        return false;
    }

    protected boolean onBack() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO Auto-generated method stub

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    public String getClassName(){
        return this.getClass().getSimpleName();
    }


    @Override
    protected void onStop() {
        if(module_flag==3){
            try {
                mIzkcService.openBackLight(0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
//		if(module_flag!=8){
//			unbindService();
//		}

        super.onStop();
    }

    private boolean isForeground (Context context)
    {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(getPackageName()))
        {
            return true ;
        }

        return false ;
    }

    public boolean bindSuccessFlag = false;
    public static IZKCService mIzkcService;
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("client", "onServiceDisconnected");
            mIzkcService = null;
            bindSuccessFlag = false;
            Toast.makeText(BaseActivity.this, getString(R.string.service_bind_fail), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("client", "onServiceConnected");
            mIzkcService = IZKCService.Stub.asInterface(service);
            if(mIzkcService!=null){
                try {
                    Toast.makeText(BaseActivity.this, getString(R.string.service_bind_success), Toast.LENGTH_SHORT).show();
                    DEVICE_MODEL = mIzkcService.getDeviceModel();
                    mIzkcService.setModuleFlag(module_flag);
                    if(module_flag==3){
                        mIzkcService.openBackLight(1);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                bindSuccessFlag = true;
            }
        }
    };

    public void bindService() {
        //com.zkc.aidl.all为远程服务的名称，不可更改
        //com.smartdevice.aidl为远程服务声明所在的包名，不可更改，
        // 对应的项目所导入的AIDL文件也应该在该包名下
        Intent intent = new Intent("com.zkc.aidl.all");
        intent.setPackage("com.smartdevice.aidl");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        unbindService(mServiceConn);
    }



    public class ScreenOnOffReceiver extends BroadcastReceiver {

        private static final String TAG = "ScreenOnOffReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.SCREEN_ON")) {
//				SCREEN_ON = true;
                try {
                    //打开电源
                    mIzkcService.setModuleFlag(8);
//					SystemClock.sleep(1000);
                    mIzkcService.setModuleFlag(module_flag);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else if (action.equals("android.intent.action.SCREEN_OFF")) {
//				SCREEN_ON = false;
//				try {
//					//关闭电源
//					mIzkcService.setModuleFlag(9);
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
            }
        }
    }

}
