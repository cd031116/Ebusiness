package com.eb.sc.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.sdk.eventbus.RefreshEvent;
import com.eb.sc.sdk.eventbus.RefreshSubscriber;
import com.eb.sc.sdk.eventbus.UpdateEvent;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.tcprequest.PushService;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.Utils;

import org.aisen.android.component.eventbus.NotificationCenter;

import butterknife.Bind;
import butterknife.OnClick;
/*
*
* @author lyj
* @describe 设备型号设置
* @data 2017/11/10
* */

public class ChoiceActivity extends BaseActivity {
    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.image_one)
    ImageView image_one;
    @Bind(R.id.image_two)
    ImageView image_two;
    @Bind(R.id.image_three)
    ImageView image_three;

    @Bind(R.id.image_one_t)
    ImageView image_one_t;


    private int select = 1;
    @Override
    protected int getLayoutId(){
        return R.layout.activity_choice;
    }

    @Override
    public void initView() {
        super.initView();
        NotificationCenter.defaultCenter().subscriber(RefreshEvent.class, refreshEvent);
        top_title.setText("机型选择");
        top_left.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        super.initData();
    }

    @OnClick({R.id.submit,R.id.one,R.id.two,R.id.three,R.id.one_t})
    void onclick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                BaseConfig bg=BaseConfig.getInstance(this);
                bg.setIntValue(Constants.JI_XING,select);
                startActivity(new Intent(ChoiceActivity.this, CheckActivity.class));
                this.finish();
                break;
            case R.id.one:
                if (select == 1){
                    return;
                }
                select = 1;
                setview(select);
                break;

            case R.id.one_t:
                if (select == 2){
                    return;
                }
                select = 2;
                setview(select);
                break;

            case R.id.two:
                if (select == 3) {
                    return;
                }
                select = 3;
                setview(select);
                break;
            case R.id.three:
                if (select == 4) {
                    return;
                }
                select = 4;
                setview(select);
                break;
        }
    }
    private void setview(int index) {
        image_one.setImageResource(R.drawable.order_pay_gray_gou);
        image_one_t.setImageResource(R.drawable.order_pay_gray_gou);
        image_two.setImageResource(R.drawable.order_pay_gray_gou);
        image_three.setImageResource(R.drawable.order_pay_gray_gou);
        if (index == 1) {
            image_one.setImageResource(R.drawable.order_pay_red_gou);
        } else if(index == 2){
            image_one_t.setImageResource(R.drawable.order_pay_red_gou);
        }else if(index==3){
            image_two.setImageResource(R.drawable.order_pay_red_gou);
        }else {
            image_three.setImageResource(R.drawable.order_pay_red_gou);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter().unsubscribe(RefreshEvent.class, refreshEvent);
    }

    RefreshSubscriber refreshEvent = new RefreshSubscriber() {
        @Override
        public void onEvent(RefreshEvent refreshEvent) {
            PushManager.getInstance(ChoiceActivity.this).sendMessage(Utils.getShebeipul(ChoiceActivity.this, Utils.getImui(ChoiceActivity.this)));
        }
    };
}
