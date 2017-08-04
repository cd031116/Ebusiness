package com.eb.sc.activity;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.bean.DataInfo;
import com.eb.sc.business.BusinessManager;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.EventSubscriber;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.sdk.recycle.CommonAdapter;
import com.eb.sc.sdk.recycle.ViewHolder;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.ChangeData;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.NetWorkUtils;

import org.aisen.android.component.eventbus.NotificationCenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class TongbBuActivity extends BaseActivity {
    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    @Bind(R.id.sycn)
    TextView sycn;
    @Bind(R.id.mlist)
    RecyclerView mlist;
    private boolean isconnect = true;
    private CommonAdapter<DataInfo> mAdapter;
    private List<DataInfo> mdata = new ArrayList<>();
    LinearLayoutManager layoutManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tongb_bu;
    }

    @Override
    public void initView() {
        super.initView();
        top_title.setText("离线核销明细");
        BaseConfig bg = new BaseConfig(this);
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        String b = bg.getStringValue(Constants.havelink, "1");
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
        mAdapter = new CommonAdapter<DataInfo>(TongbBuActivity.this, R.layout.tongbu_item, mdata) {
            @Override
            protected void convert(ViewHolder holder, DataInfo info, int position) {
                if(position==0){
                    holder.setBackgroundColor(R.id.top, Color.parseColor("#ffffff"));
                }else if (position % 2 == 1) {
                    holder.setBackgroundColor(R.id.top, Color.parseColor("#EAEAEA"));
                } else {
                    holder.setBackgroundColor(R.id.top, Color.parseColor("#ffffff"));
                }
                holder.setText(R.id.address, "");
                holder.setText(R.id.time, ChangeData.cuotoString(info.getInsertTime()));
                if(!info.isUp()){
                    holder.setText(R.id.state, "未同步");
                    holder.setTextColor(R.id.state,Color.parseColor("#FD1E1D"));
                }else{
                    holder.setText(R.id.state, "未同步");
                    holder.setTextColor(R.id.state,Color.parseColor("#5CE064"));
                }
            }
        };
        layoutManager = new LinearLayoutManager(TongbBuActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mlist.setLayoutManager(layoutManager);
        mlist.setAdapter(mAdapter);
        getdata();
    }


    private void getdata(){
        if(mdata!=null){
            mdata.clear();
        }
        List<DataInfo> mlist= BusinessManager.querAll();
        for (int i=0;i<mlist.size();i++){
            if(!mlist.get(i).isUp()){
                mdata.add(mlist.get(i));
            }
        }
        mAdapter.notifyDataSetChanged();
    }




    @OnClick({R.id.top_left, R.id.sycn})
    void onBuy(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                TongbBuActivity.this.finish();
                break;
            case R.id.sycn:
                if (NetWorkUtils.isNetworkConnected(this) && isconnect) {

                } else {
                    Toast.makeText(TongbBuActivity.this,"与服务器断开连接或网络不可用!",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber(){
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(TongbBuActivity.this);
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
    EventSubscriber netEventSubscriber = new EventSubscriber(){
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter().unsubscribe(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(NetEvent.class, netEventSubscriber);
    }

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