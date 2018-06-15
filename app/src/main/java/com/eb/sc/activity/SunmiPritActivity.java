package com.eb.sc.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.R;
import com.eb.sc.bean.SaleBean;
import com.eb.sc.bean.TickBean;
import com.eb.sc.bean.TicketInfo;
import com.eb.sc.offline.SaleDataDb;
import com.eb.sc.priter.PrinterActivity;
import com.eb.sc.priter.PrinterHelper;
import com.eb.sc.scanner.BaseActivity;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.EventSubscriber;
import com.eb.sc.sdk.eventbus.FinishEvent;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.sdk.eventbus.PayResultEvent;
import com.eb.sc.sdk.eventbus.PayResultSubscriber;
import com.eb.sc.sdk.recycle.CommonAdapter;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.utils.SupportMultipleScreensUtil;
import com.eb.sc.utils.Utils;
import com.eb.sc.widget.ProgressDialog;

import org.aisen.android.component.eventbus.NotificationCenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SunmiPritActivity extends BaseActivity {
    private Bitmap mBitmap = null;
    private boolean runFlag = true;
    private CommonAdapter<TickBean> mAdapter;
    private List<TickBean> mList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private TickBean mInfo;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    @Bind(R.id.state)
    TextView state;
    @Bind(R.id.printer_tick)
    TextView printer_tick;
    @Bind(R.id.check_buy)
    TextView check_buy;


    private int select = 0;
    private String s_neirong, order = "";
    private boolean isconnect = true;
    private boolean isbuy = false;
    private boolean ispringter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationCenter.defaultCenter().subscriber(PayResultEvent.class, payscriber);
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        mInfo = (TickBean) getIntent().getSerializableExtra("tick");
        order = getIntent().getExtras().getString("order");
        select = getIntent().getExtras().getInt("select");
        BaseConfig bg=BaseConfig.getInstance(this);
        bg.setStringValue(Constants.USER_ORDER,order);
        setContentView(R.layout.activity_printer);
        ButterKnife.bind(this);
        printer_tick.setEnabled(false);
        View rootView = findViewById(android.R.id.content);
        SupportMultipleScreensUtil.init(getApplication());
        SupportMultipleScreensUtil.scale(rootView);
        top_title.setText("正在支付");
        showAlert("..正在支付..", true);
        handler.postDelayed(runnable, 2000);
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

    @OnClick({R.id.top_left, R.id.printer_tick, R.id.check_tick,R.id.check_buy})
    void onclick(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                if (!ispringter) {
                    exitDialog();
                } else {
                    SunmiPritActivity.this.finish();
                }
                break;
            case R.id.printer_tick:
                if (isbuy) {
                    printPurcase(s_neirong);
                }
                break;
            case R.id.check_tick:
                startActivity(new Intent(SunmiPritActivity.this, SelectActivity.class));
                NotificationCenter.defaultCenter().publish(new FinishEvent());
                this.finish();
                break;
            case  R.id.check_buy:
                String updatd = Utils.lunxun(SunmiPritActivity.this, order);
                boolean postg=PushManager.getInstance(SunmiPritActivity.this).sendMessage(updatd);
                if(postg){
                    showAlert("..正在查询..", true);
                }else {
                    Toast.makeText(SunmiPritActivity.this,"发送失败",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    //打印相关
    private void printPurcase(String sttrs) {
        String arrs[]=sttrs.split("#");
        String aa = "";
        BaseConfig bg=BaseConfig.getInstance(SunmiPritActivity.this);
        boolean abg = PushManager.getInstance(SunmiPritActivity.this).sendMessage(Utils.sentprint(SunmiPritActivity.this, bg.getStringValue(Constants.ORDER_ID, "")));
        TicketInfo tInfo = new TicketInfo();
        String order_id = bg.getStringValue(Constants.ORDER_ID, "");
        String arrss[]=order_id.split("&");
        Log.i("hhhh","arrssarrss="+arrss[0]);
        tInfo.setOrderId(arrss[0]);
        tInfo.setPrice((Double.parseDouble(mInfo.getPrice()) * mInfo.getpNum() + ""));
        tInfo.setpNum(mInfo.getpNum() + "");
        tInfo.setOrderName(mInfo.getNmae());
        String items=arrs[1].substring(0,arrs[1].length()-1);
        tInfo.setItem(items);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        tInfo.setpTime(str);

        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate1 = new Date(System.currentTimeMillis());//获取当前时间
        String str1 = formatter1.format(curDate1);
        tInfo.setOrderTime(str1 + "至" + str1);

        Bitmap mBitmap = null;
        mBitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.prnter);
        tInfo.setStart_bitmap(mBitmap);

        PrinterHelper.getInstance(this).sunmiprit(tInfo,arrs[0]);
        ispringter = true;
        printer_tick.setEnabled(false);
        bg.setStringValue(Constants.USER_ORDER,"");
    }



    //收到反回的数据
    PayResultSubscriber payscriber = new PayResultSubscriber() {
        @Override
        public void onEvent(PayResultEvent event) {
            check_buy.setVisibility(View.GONE);
            if (event.getStrs().contains("cancel")) {
                dismissAlert();
                Toast.makeText(SunmiPritActivity.this, "用户取消支付", Toast.LENGTH_SHORT).show();
                handler.removeCallbacks(runnable);
                SunmiPritActivity.this.finish();
            } else if (!event.getStrs().contains("nopay")) {
                printer_tick.setEnabled(true);
                handler.removeCallbacks(runnable);
                isbuy = true;
                String arrs[]=event.getStrs().split("#");
                s_neirong = event.getStrs();
                top_title.setText("支付成功");
                state.setText("已成功收款");
                state.setTextColor(Color.parseColor("#51AD5F"));
                dismissAlert();
                BaseConfig bg = BaseConfig.getInstance(SunmiPritActivity.this);
                bg.setIntValue(Constants.IS_PAY, 1);
                SaleBean sbean = new SaleBean();
                sbean.setOrderId(bg.getStringValue(Constants.ORDER_ID, ""));
                sbean.setpNum(mInfo.getpNum());
                sbean.setPrice((Double.parseDouble(mInfo.getPrice()) * mInfo.getpNum() + ""));
                sbean.setPrint_time(System.currentTimeMillis() + "");
                String items=arrs[1].substring(0,arrs[1].length()-1);
                sbean.setItem(items);
                sbean.setState(select);
                if (!TextUtils.isEmpty(bg.getStringValue(Constants.ORDER_ID, "")) && !SaleDataDb.isHave(bg.getStringValue(Constants.ORDER_ID, ""))) {
                    SaleDataDb.insert(sbean);
                }
            } else {
                top_title.setText("支付失败");
                state.setTextColor(Color.parseColor("#E22018"));
                state.setText("支付失败!");
                isbuy = false;
                dismissAlert();
                ispringter = true;
            }
        }
    };

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            String updatd = Utils.lunxun(SunmiPritActivity.this, order);
            PushManager.getInstance(SunmiPritActivity.this).sendMessage(updatd);
            handler.postDelayed(this, 2000);
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!ispringter&&isbuy) {
                exitDialog();
            } else {
                SunmiPritActivity.this.finish();
            }
        }
        return false;
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
    public void onDestroy(){
        super.onDestroy();
        NotificationCenter.defaultCenter().unsubscribe(PayResultEvent.class, payscriber);
        NotificationCenter.defaultCenter().unsubscribe(PayResultEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(PayResultEvent.class, netEventSubscriber);
    }

    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber() {
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(SunmiPritActivity.this);
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

    /**
     * 显示加载图标
     *
     * @param txt
     */
    public void showAlert(String txt, final boolean isCancel) {
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        if (txt != null) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this, isCancel);
            }
            progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (isCancel) {
                            progressDialog.dismiss();
                        }
                    }
                    return false;
                }
            });
            progressDialog.show();
            progressDialog.showText(txt);
        }
    }

    /**
     * 关闭加载图标
     */
    public void dismissAlert() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    public void exitDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您还没有打印票,确定退出?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SunmiPritActivity.this.finish();
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
