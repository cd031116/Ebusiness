package com.eb.sc.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.bean.DataInfo;
import com.eb.sc.bean.SaleBean;
import com.eb.sc.offline.OfflLineDataDb;
import com.eb.sc.offline.SaleDataDb;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.EventSubscriber;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;

import org.aisen.android.component.eventbus.NotificationCenter;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class SaleTotalActivity extends BaseActivity {
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    @Bind(R.id.x_price)
    TextView x_price;
    @Bind(R.id.p_num)
    TextView p_num;
    @Bind(R.id.weichat)
    TextView weichat;
    @Bind(R.id.w_num)
    TextView weichat_num;
    @Bind(R.id.ali_pay)
    TextView ali_pay;
    @Bind(R.id.a_num)
    TextView a_num;
    private boolean isconnect = true;
    private double cash = 0.0;
    private double w_cash = 0.0;//微信
    private double a_cash = 0.0;//阿里
    private int num = 0;
    private int w_num = 0;
    private int A_num = 0;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_sale_total;
    }

    @Override
    public void initView() {
        super.initView();
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        top_title.setText("售票");
    }

    @Override
    public void initData() {
        super.initData();
    }

    private void ShowView() {
          cash = 0.0;
          w_cash = 0.0;
          a_cash = 0.0;
          num = 0;
          w_num = 0;
          A_num = 0;
        List<SaleBean> mCash = SaleDataDb.querCashAll();
        Log.i("vvvv","num="+mCash.size());
        for (SaleBean xj_info : mCash) {
            cash = Double.parseDouble(xj_info.getPrice()) + cash;
            num = xj_info.getpNum() + num;
        }

        List<SaleBean> wCash = SaleDataDb.querWAll();
        for (SaleBean w_info : wCash) {
            w_cash = Double.parseDouble(w_info.getPrice()) + w_cash;
            w_num = w_info.getpNum() + w_num;
        }

        List<SaleBean> aCash = SaleDataDb.querAAll();
        for (SaleBean a_info : aCash) {
            a_cash = Double.parseDouble(a_info.getPrice()) + a_cash;
            A_num = a_info.getpNum() + A_num;
        }
        x_price.setText("￥" + cash + "");
        p_num.setText(num + "");
        weichat.setText("￥" + w_cash + "");
        weichat_num.setText(w_num + "");
        ali_pay.setText("￥" + a_cash + "");
        a_num.setText(A_num + "");

    }

    @OnClick({R.id.top_left,R.id.close_bg})
    void onBuy(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                SaleTotalActivity.this.finish();
                break;
            case R.id.close_bg:
                ExitDialog();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowView();
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
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter().unsubscribe(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(NetEvent.class, netEventSubscriber);
    }

    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber() {
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(SaleTotalActivity.this);
            String a = bg.getStringValue(Constants.havenet, "-1");
            Log.e("ClientSessionHandler", "11111111111");
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
}
