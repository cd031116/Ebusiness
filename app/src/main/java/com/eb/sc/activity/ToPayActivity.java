package com.eb.sc.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.bean.TickBean;
import com.eb.sc.priter.PrinterActivity;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.EventSubscriber;
import com.eb.sc.sdk.eventbus.FinishEvent;
import com.eb.sc.sdk.eventbus.FinishEventSubsrciber;
import com.eb.sc.sdk.eventbus.GetOrderEvent;
import com.eb.sc.sdk.eventbus.GetOrderSubscriber;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.utils.Utils;

import org.aisen.android.component.eventbus.NotificationCenter;

import butterknife.Bind;
import butterknife.OnClick;

public class ToPayActivity extends BaseActivity {
    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    private   TickBean mInfo;
    private int select=0;
    private boolean isconnect = true;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_to_pay;
    }

    @Override
    public void initView(){
        super.initView();
        NotificationCenter.defaultCenter().subscriber(FinishEvent.class,finishscriber);
        NotificationCenter.defaultCenter().subscriber(GetOrderEvent.class, getOrderscriber);
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        top_title.setText("选择支付方式");
         mInfo = (TickBean)getIntent().getSerializableExtra("tick");
        BaseConfig bg=BaseConfig.getInstance(this);
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
    public void initData(){
        super.initData();
    }



    @OnClick({R.id.top_left,R.id.cash,R.id.weichat,R.id.ali_pay,R.id.close_bg})
    void onBuy(View v) {
        BaseConfig bg=BaseConfig.getInstance(ToPayActivity.this);
        switch (v.getId()) {
            case R.id.top_left:
                ToPayActivity.this.finish();
                break;
            case R.id.cash:
                select=0;
                String datad = Utils.getItemId(ToPayActivity.this) + "&" + (Double.parseDouble(mInfo.getPrice()) * mInfo.getpNum() + "") + "&" + select+ "&" + (mInfo.getpNum() + "&"+bg.getStringValue(Constants.USER_ID,""));
                String updatd = Utils.getBuy(ToPayActivity.this, datad);
                boolean abgs = PushManager.getInstance(ToPayActivity.this).sendMessage(updatd);
                if (abgs) {
                    showAlert("正在获取订单..", true);
                } else {
                    dismissAlert();
                    Toast.makeText(ToPayActivity.this, "提交失败,请检查您的网络!", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.weichat:
                select=3;
                String datas = Utils.getItemId(ToPayActivity.this) + "&" + (Double.parseDouble(mInfo.getPrice()) * mInfo.getpNum() + "") + "&" + select+ "&" + mInfo.getpNum() + "&"+bg.getStringValue(Constants.USER_ID,"");
                Log.i("vvvv","datas="+datas);
                String updata = Utils.getBuy(ToPayActivity.this, datas);
                boolean abg = PushManager.getInstance(ToPayActivity.this).sendMessage(updata);
                if (abg) {
                    showAlert("正在获取订单..", true);
                } else {
                    dismissAlert();
                    Toast.makeText(ToPayActivity.this, "提交失败,请检查您的网络!", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.ali_pay:
                select=2;
                String data2 = Utils.getItemId(ToPayActivity.this) + "&" + (Double.parseDouble(mInfo.getPrice()) * mInfo.getpNum() + "") + "&" + select + "&" + mInfo.getpNum() + "&"+bg.getStringValue(Constants.USER_ID,"");
                String updata2 = Utils.getBuy(ToPayActivity.this, data2);
                boolean abgd = PushManager.getInstance(ToPayActivity.this).sendMessage(updata2);
                if (abgd) {
                    showAlert("正在获取订单..", true);
                } else {
                    dismissAlert();
                    Toast.makeText(ToPayActivity.this, "提交失败,请检查您的网络!", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.close_bg:
                ExitDialog();
                break;
        }
    }

    //收到反回的数据
    GetOrderSubscriber getOrderscriber = new GetOrderSubscriber(){
        @Override
        public void onEvent(GetOrderEvent event){
            dismissAlert();
            String order_id = event.getOrder_id();
            BaseConfig bg = BaseConfig.getInstance(ToPayActivity.this);
            bg.setStringValue(Constants.ORDER_ID, order_id);
            bg.setIntValue(Constants.IS_PAY, 0);
            if(select==0){
                String order = bg.getStringValue(Constants.ORDER_ID, "");
                String updata = Utils.sentBuy(ToPayActivity.this, order + "&" + (Double.parseDouble(mInfo.getPrice()) * mInfo.getpNum() + "") + "&" + select + "&" + Utils.getItemId(ToPayActivity.this) + "&" + "0"+"&"+mInfo.getpNum()+"&"+bg.getStringValue(Constants.USER_ID,""));
                boolean abg = PushManager.getInstance(ToPayActivity.this).sendMessage(updata);
                if (abg) {
                    Intent intent=new Intent(ToPayActivity.this,PrinterActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("tick", mInfo);
                    mBundle.putString("order",order+"&"+Utils.getItemId(ToPayActivity.this)+"&"+mInfo.getpNum()+"");
                    intent.putExtras(mBundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(ToPayActivity.this, "网络慢!", Toast.LENGTH_SHORT).show();
                }
            }else {
                Intent intent = new Intent(ToPayActivity.this, CaptureActivity.class);
                intent.putExtra("select", "1");
                startActivityForResult(intent, 0);
            }
        }
    };
    @Override
    public void onDestroy(){
        super.onDestroy();
        NotificationCenter.defaultCenter().unsubscribe(GetOrderEvent.class, getOrderscriber);
        NotificationCenter.defaultCenter().unsubscribe(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(NetEvent.class, netEventSubscriber);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                Bundle b = data.getExtras(); //data为B中回传的Intent
                String str = b.getString("scansts");//str即为回传的值
                if (!TextUtils.isEmpty(str)) {
                    BaseConfig bg = BaseConfig.getInstance(ToPayActivity.this);
                    String order = bg.getStringValue(Constants.ORDER_ID, "");// (Double.parseDouble(mInfo.getPrice()) * mInfo.getpNum() + "")
                    String updata = Utils.sentBuy(ToPayActivity.this, order + "&" +(Double.parseDouble(mInfo.getPrice()) * mInfo.getpNum() + "")+ "&" + select + "&" + Utils.getItemId(this) + "&" + str+"&"+mInfo.getpNum()+"&"+bg.getStringValue(Constants.USER_ID,""));
                    Log.i("vvvv","updata="+updata);
                    boolean abg = PushManager.getInstance(ToPayActivity.this).sendMessage(updata);
                    if (abg) {
                        Intent intent=new Intent(ToPayActivity.this,PrinterActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("tick", mInfo);
                        mBundle.putString("order",order+"&"+Utils.getItemId(this)+"&"+mInfo.getpNum()+"");
                        mBundle.putInt("select",select);
                        intent.putExtras(mBundle);
                        startActivity(intent);
                    } else {

                        Toast.makeText(ToPayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }


    //长连接
    FinishEventSubsrciber finishscriber = new FinishEventSubsrciber(){
        @Override
        public void onEvent(FinishEvent event) {
           ToPayActivity.this.finish();
        }
    };

    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber(){
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(ToPayActivity.this);
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
            mRight_bg.setImageResource(R.drawable.lianjie);
            top_right_text.setText("在线");
            top_right_text.setTextColor(Color.parseColor("#0973FD"));
        } else {
            mRight_bg.setImageResource(R.drawable.lixian);
            top_right_text.setText("离线");
            top_right_text.setTextColor(Color.parseColor("#EF4B55"));
        }
    }

}
