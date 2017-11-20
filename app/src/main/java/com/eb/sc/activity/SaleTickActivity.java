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
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.sdk.eventbus.RefreshEvent;
import com.eb.sc.sdk.eventbus.UpdateEvent;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.tcprequest.PushService;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.utils.Utils;

import org.aisen.android.component.eventbus.NotificationCenter;

import butterknife.Bind;
import butterknife.OnClick;
/*
*
* @author lyj
* @describe 售票界面入口
* @data 2017/11/10
* */

public class SaleTickActivity extends BaseActivity {
    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.price)
    TextView price;
    @Bind(R.id.p_num)
    TextView p_num;

    private int buy_num = 1;
    private boolean isconnect = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sale_tick;
    }

    @Override
    public void initView() {
        super.initView();
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        top_title.setText("售票");
        BaseConfig bg = BaseConfig.getInstance(this);
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
        BaseConfig bg = BaseConfig.getInstance(this);
        final String s = bg.getStringValue(Constants.address, "-1");
        name.setText(Utils.getXiangmu(this));
        price.setText("￥" + Utils.getPrice(this));
        p_num.setText(buy_num + "");
    }

    @OnClick({R.id.top_left, R.id.submit, R.id.cut_t, R.id.add_t, R.id.total, R.id.close_bg})
    void onBuy(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                SaleTickActivity.this.finish();
                break;
            case R.id.submit:
                BaseConfig bg = BaseConfig.getInstance(this);
                final String s = bg.getStringValue(Constants.address, "-1");
                TickBean ifo = new TickBean();
                ifo.setNmae(Utils.getXiangmu(this));
                ifo.setpNum(buy_num);
                ifo.setId_tick(s);
                ifo.setPrice(Utils.getPrice(this));
                Intent intent = new Intent(SaleTickActivity.this, ToPayActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("tick", ifo);
                intent.putExtras(mBundle);
                startActivity(intent);
                break;
            case R.id.cut_t:
                if (buy_num <= 1) {
                    Toast.makeText(SaleTickActivity.this, "售票数量不能小于1", Toast.LENGTH_SHORT).show();
                    break;
                }
                buy_num = buy_num - 1;
                p_num.setText(buy_num + "");
                price.setText("￥" + (Double.parseDouble(Utils.getPrice(SaleTickActivity.this)) * buy_num + ""));
                break;
            case R.id.add_t:
                buy_num = buy_num + 1;
                p_num.setText(buy_num + "");
                price.setText("￥" + (Double.parseDouble(Utils.getPrice(SaleTickActivity.this)) * buy_num + ""));
                break;
            case R.id.total:
                startActivity(new Intent(SaleTickActivity.this, SaleTotalActivity.class));
                break;
            case R.id.close_bg:
                ExitDialog();
                break;
        }
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
            BaseConfig bg = new BaseConfig(SaleTickActivity.this);
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
