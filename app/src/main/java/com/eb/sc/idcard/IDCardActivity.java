package com.eb.sc.idcard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.R;
import com.eb.sc.bean.DataInfo;
import com.eb.sc.offline.OfflLineDataDb;
import com.eb.sc.scanner.BaseActivity;
import com.eb.sc.scanner.ExecutorFactory;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.EventSubscriber;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.sdk.eventbus.PutEvent;
import com.eb.sc.sdk.eventbus.PutSubscriber;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.utils.PlayVedio;
import com.eb.sc.utils.SupportMultipleScreensUtil;
import com.eb.sc.utils.Utils;
import com.eb.sc.widget.CommomDialog;
import com.eb.sc.widget.ShowMsgDialog;

import org.aisen.android.component.eventbus.NotificationCenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author zkc-soft
 *         Created by Administrator on 2017/4/12 15:16
 * @ClassName: IDCardActivity.java
 * @Description: 身份证感应信息页面
 */

public class IDCardActivity extends BaseActivity {
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    @Bind(R.id.top_title)
    TextView top_title;
    boolean linkSuccess = false;
    private boolean runFlag = true;
    private boolean isgetIdcard=false;
    private String result="";
    private boolean isconnect = true;
    private String idcard_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(PutEvent.class, putSubscriber);
        View rootView = findViewById(android.R.id.content);
        SupportMultipleScreensUtil.init(getApplication());
        SupportMultipleScreensUtil.scale(rootView);
        ButterKnife.bind(this);
        ExecutorFactory.executeThread(new Runnable() {
            @Override
            public void run() {
                while (runFlag) {
                    if (bindSuccessFlag) {
                        if(mIzkcService!=null){
                            mHandler.obtainMessage(0).sendToTarget();
                            runFlag = false;
                            linkSuccess=true;
                            Log.i("result","linkSuccess=");
                        }else {
                            linkSuccess=false;
                        }
                    }
                }
            }
        });
        initView();
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        mIzkcService.setModuleFlag(5);
                        mIzkcService.turnOn();
                        Log.i("result","setModuleFlag=");
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    break;
                case 8:
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        thandler=new MyHandler();
        handler.postDelayed(runnable,400);
        if(mIzkcService!=null){
            try {
                Log.i("result","mIzkcService!=null");
                mIzkcService.turnOn();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }

    private void initView() {

        top_title.setText("身份证感应");
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
    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(!isgetIdcard){
                new BarAsyncTask().execute();
                handler.postDelayed(this, 100);
            }
        }
    };

    class BarAsyncTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {

            try {
                result = mIzkcService.getIdentifyInfo();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String resultd) {
            if(TextUtils.isEmpty(result)||"未找到卡".equals(result)||"选卡失败，请重新放卡".equals(result)||result.length()<13){
                Log.i("result","未找到卡=");
                return;
            }else {
                Log.i("result","result="+result);
                isgetIdcard=true;
                idcard_id=result.substring(result.indexOf("号码：4")+1, result.lastIndexOf("签发机关"));
                idcard_id=idcard_id.substring(2,idcard_id.length()).trim();
                Log.i("result","result="+result);
                Log.i("result","idcard_id="+idcard_id);
                if (NetWorkUtils.isNetworkConnected(IDCardActivity.this) && isconnect) {
                    String updata = Utils.getIdcard(IDCardActivity.this,idcard_id);
                    PushManager.getInstance(IDCardActivity.this).sendMessage(updata);
                } else {
                    Toast.makeText(IDCardActivity.this,"与服务器断开连接",Toast.LENGTH_SHORT).show();
                    isgetIdcard=false;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        NotificationCenter.defaultCenter().unsubscribe(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(PutEvent.class, putSubscriber);
        super.onDestroy();
        if (mIzkcService != null) {
            try {
                mIzkcService.turnOff();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        handler.removeCallbacks(runnable);
    }


    @Override
    protected void onStop() {
        handler.removeCallbacks(runnable);
        super.onStop();
    }


    //在线成功
    PutSubscriber putSubscriber = new PutSubscriber() {
        @Override
        public void onEvent(PutEvent putEvent) {
            String srt = putEvent.getStrs();
            String sgs = putEvent.getStrs().substring(0, 2);
            int renshu = Integer.parseInt(putEvent.getStrs().substring(2, 6),16);
            Log.d("dddd", "putEvent sgs: " + sgs + ",renshu:" + renshu+",id_n:"+idcard_id+",xiangmu:"+Utils.getXiangmu(IDCardActivity.this));
            if ("01".equals(sgs)) {
                showDialogd("成人票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(renshu));
                PlayVedio.getInstance().play(IDCardActivity.this, 5);
            } else if ("02".equals(sgs)) {
                PlayVedio.getInstance().play(IDCardActivity.this, 2);
                showDialogd("儿童票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(renshu));
            } else if ("03".equals(sgs)) {
                PlayVedio.getInstance().play(IDCardActivity.this, 7);
                showDialogd("优惠票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(renshu));
            } else if ("04".equals(sgs)) {
                PlayVedio.getInstance().play(IDCardActivity.this, 7);
                showDialogd("招待票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(renshu));
            } else if ("05".equals(sgs)) {
                PlayVedio.getInstance().play(IDCardActivity.this, 3);
                showDialogd("老年票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(renshu));
            } else if ("06".equals(sgs)) {
                PlayVedio.getInstance().play(IDCardActivity.this, 8);
                showDialogd("团队票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(renshu));
            } else if ("07".equals(sgs)) {
                PlayVedio.getInstance().play(IDCardActivity.this, 6);
                showDialogMsg("已使用");
            } else if ("08".equals(sgs)) {
                PlayVedio.getInstance().play(IDCardActivity.this, 1);
                showDialogMsg("无效票");
            } else if ("09".equals(sgs)) {
                PlayVedio.getInstance().play(IDCardActivity.this, 9);
                showDialogMsg("已过期");
            } else if ("0A".equals(sgs)) {
                showDialogMsg("网络超时");
            } else if ("0B".equals(sgs)) {
                showDialogMsg("票型不符");
            } else if ("0C".equals(sgs)) {
                showDialogMsg("团队满人");
            } else if ("0D".equals(sgs)) {
                showDialogMsg("游玩尚未开始（网购票当天购买要第二天用）");
            }else if ("0E".equals(sgs)) {
                showDialogd("年卡", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(renshu));
            } else if ("10".equals(sgs)) {
                showDialogMsg("通道不符");
            }
//            String srt = putEvent.getStrs();
//            String sgs = putEvent.getStrs().substring(0, 2);
//            String renshu = putEvent.getStrs().substring(srt.length() - 2, srt.length());
//            if ("06".equals(sgs)) {
//                PlayVedio.getInstance().play(IDCardActivity.this,8);
//                showDialogd("团队票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(Integer.parseInt(renshu)));
//            } else if ("02".equals(sgs)) {
//                PlayVedio.getInstance().play(IDCardActivity.this,2);
//                showDialogd("儿童票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(Integer.parseInt(renshu)));
//            } else if ("01".equals(sgs)) {
//                PlayVedio.getInstance().play(IDCardActivity.this,5);
//                showDialogd("成人票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(Integer.parseInt(renshu)));
//            } else if ("05".equals(sgs)) {
//                PlayVedio.getInstance().play(IDCardActivity.this,3);
//                showDialogd("老年票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(Integer.parseInt(renshu)));
//            } else if ("03".equals(sgs)) {
//                PlayVedio.getInstance().play(IDCardActivity.this,7);
//                showDialogd("优惠票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(Integer.parseInt(renshu)));
//            } else if ("07".equals(sgs)) {
//                PlayVedio.getInstance().play(IDCardActivity.this,6);
//                showDialogMsg("已使用");
//            } else if("09".equals(sgs)){
//                PlayVedio.getInstance().play(IDCardActivity.this,9);
//                showDialogMsg("已过期");
//            }else {
//                PlayVedio.getInstance().play(IDCardActivity.this,1);
//                showDialogMsg("无效票");
//            }
        }
    };

    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber() {
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(IDCardActivity.this);
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

    //有效票-有线
    private void showDialogd(String names, final String num, String code, final String renshu) {
        new CommomDialog(this, R.style.dialog, names, num, code, renshu, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    DataInfo data = new DataInfo();
                    data.setId(num);
                    data.setUp(true);
                    data.setNet(true);
                    data.setType(1);
                    data.setpNum(renshu);
                    data.setName(Utils.getXiangmu(IDCardActivity.this));
                    data.setInsertTime(System.currentTimeMillis() + "");
                    OfflLineDataDb.insert(data);
                    dialog.dismiss();
                    idcard_id="";
                    isgetIdcard=false;
                    handler.postDelayed(runnable, 2000);
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
                    idcard_id="";
                    isgetIdcard=false;
                    handler.postDelayed(runnable, 2000);
                }
            }
        }).setTitle("提示").show();
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
    MyHandler thandler;//定义handler
    Message msg;
    @OnClick({R.id.top_left,R.id.close_bg,R.id.clis})
    void onclick(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                IDCardActivity.this.finish();
                break;
            case R.id.close_bg:
                ExitDialog();
                break;
            case R.id.clis:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result = null;
                        try {
                            result = mIzkcService.getIdentifyInfo();
                            if(TextUtils.isEmpty(result))return;
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        Bundle bundle=new Bundle();
                        bundle.putString("num",result);//bundle中也可以放序列化或包裹化的类对象数据
                        msg=thandler.obtainMessage();//每发送一次都要重新获取
                        msg.setData(bundle);
                        thandler.sendMessage(msg);//用handler向主线程发送信息
                    }
                }).start();
                break;
            default:
                break;
        }
    }
    //自定义handler类
    class MyHandler extends Handler{
        @Override
        //接收别的线程的信息并处理
        public void handleMessage(Message msg) {
            Bundle bundle=msg.getData();
            Toast.makeText(IDCardActivity.this,""+bundle.get("num").toString(),Toast.LENGTH_SHORT).show();
        }
    }


}
