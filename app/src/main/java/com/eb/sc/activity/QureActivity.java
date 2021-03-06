package com.eb.sc.activity;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.eb.sc.MainActivity;
import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.bean.GroupInfo;
import com.eb.sc.bean.ItemInfo;
import com.eb.sc.business.QueryAdapter;
import com.eb.sc.scanner.ScannerActivity;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.EventSubscriber;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.sdk.eventbus.PutEvent;
import com.eb.sc.sdk.eventbus.QueryEvent;
import com.eb.sc.sdk.eventbus.QuerySubscriber;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.utils.Utils;

import org.aisen.android.component.eventbus.NotificationCenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
/*
*
* @author lyj
* @describe 核销查询
* @data 2017/11/10
* */

public class QureActivity extends BaseActivity {
    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.expand)
    ExpandableListView expand;
    @Bind(R.id.empty)
    RelativeLayout empty;

    private List<GroupInfo> mList =new ArrayList<>();
    private boolean isconnect = true;
    private QueryAdapter mAdapter;
    @Override
    protected int getLayoutId(){
        return R.layout.activity_qure;
    }

    @Override
    public void initView() {
        super.initView();
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(QueryEvent.class, querySubscriber);
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
        top_title.setText("查询");
    }

    @Override
    public void initData() {
        super.initData();
    }

    @OnClick({R.id.top_left,R.id.close_bg,R.id.cheeck})
    void onclick(View v) {
        BaseConfig bg = BaseConfig.getInstance(this);
        switch (v.getId()) {
            case R.id.top_left:
                QureActivity.this.finish();
                break;
            case R.id.close_bg:
                ExitDialog();
                break;
            case R.id.cheeck:
                String keys=password.getText().toString();
                if(TextUtils.isEmpty(keys)){
                    Toast.makeText(QureActivity.this,"请输入关键字",Toast.LENGTH_SHORT).show();
                    break;
                }
                String updatd = Utils.Toquery(QureActivity.this,keys);
                Log.i("tttt","updatd="+updatd);
                boolean gg=  PushManager.getInstance(QureActivity.this).sendMessage(updatd);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter().unsubscribe(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(QueryEvent.class, querySubscriber);
    }
    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber(){
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(QureActivity.this);
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
    //获得数据
    QuerySubscriber querySubscriber = new QuerySubscriber() {
        @Override
        public void onEvent(QueryEvent event) {
            TestData(event.getDatas());
        }
    };

    /**
     * 数据
     */
    private void TestData(String data) {
        if (mList != null) {
            mList.clear();
        }
        if(data==null||TextUtils.isEmpty(data)){
            expand.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }else if(!data.startsWith("[{")&&!data.endsWith("}]")){
            expand.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }else {
            mList = JSON.parseArray(data, GroupInfo.class);
            if (!"null".equals(data) || mList.size() > 0) {
                expand.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
                mAdapter = new QueryAdapter(QureActivity.this, mList);
                expand.setAdapter(mAdapter);
            } else {
                expand.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }
        }
    }

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
