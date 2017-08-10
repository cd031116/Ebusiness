package com.eb.sc.activity;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.bean.DataInfo;
import com.eb.sc.business.BusinessManager;
import com.eb.sc.offline.OfflLineDataDb;
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
import com.eb.sc.utils.Utils;

import org.aisen.android.component.eventbus.NotificationCenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class DetailActivity extends BaseActivity {
    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    @Bind(R.id.ticket_list)
    RecyclerView mRecy;
    @Bind(R.id.total_num)
    TextView total_num;
    @Bind(R.id.ticket_num)
    TextView ticket_num;
    @Bind(R.id.ticket_door)
    TextView ticket_door;
    @Bind(R.id.ticket_door_layout)
    RelativeLayout ticket_door_layout;

    private Map<String, List<DataInfo>> mMap;
    private CommonAdapter<DataInfo> mAdapter;
    private List<DataInfo> mdata = new ArrayList<>();
    LinearLayoutManager layoutManager;
    private boolean isconnect = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    public void initView() {
        super.initView();
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        top_title.setText("当前核销明细");
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

    @Override
    public void initData() {
        super.initData();
        getdata();
        mAdapter = new CommonAdapter<DataInfo>(DetailActivity.this, R.layout.detail_item, mdata) {
            @Override
            protected void convert(ViewHolder holder, DataInfo info, int position) {
                if (position == 0) {
                    holder.setBackgroundColor(R.id.top, Color.parseColor("#ffffff"));
                } else if (position % 2 == 1) {
                    holder.setBackgroundColor(R.id.top, Color.parseColor("#EAEAEA"));
                } else {
                    holder.setBackgroundColor(R.id.top, Color.parseColor("#ffffff"));
                }
                holder.setText(R.id.address, info.getName());
                holder.setText(R.id.time, ChangeData.cuotoString(info.getInsertTime()));
                if (!info.isNet()) {
                    holder.setText(R.id.state, "[离线]");
                } else {
                    holder.setText(R.id.state, "");
                }
            }
        };
        layoutManager = new LinearLayoutManager(DetailActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecy.setLayoutManager(layoutManager);
        mRecy.setAdapter(mAdapter);

    }

    private void getdata() {
        if (mdata != null) {
            mdata.clear();
        }
        mdata = OfflLineDataDb.queryAll();
        ticket_num.setText(mdata.size() + "");
        ticket_door.setText(Utils.getXiangmu(DetailActivity.this));
        total_num.setText(mdata.size() + "");
        try {
            mMap = Utils.groupDataInfo(mdata);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        init();
    }

    @OnClick({R.id.top_left})
    void onBuy(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                DetailActivity.this.finish();
                break;
        }
    }

    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber() {
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(DetailActivity.this);
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

    private void init() {
        for (int i = 0; i < 2; i++) {
            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            RelativeLayout lLayout = new RelativeLayout(this);
            lLayout.setId(i + 10);
//            lLayout.setOrientation(LinearLayout.HORIZONTAL);
            RelativeLayout.LayoutParams lLayoutlayoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lLayout.setLayoutParams(lLayoutlayoutParams);



            TextView tv = new TextView(this);
            tv.setId(i);
            tv.setText("这是第" + i + "个文本框");
            tv.setTextSize(20);
            tv.setTextColor(Color.parseColor("#7c7c7c"));

            TextView tv_num = new TextView(this);
            tv_num.setId(i + 20);
            tv_num.setText(i + "台");
            tv_num.setTextSize(20);
            tv_num.setTextColor(Color.parseColor("#7c7c7c"));


//            RelativeLayout.LayoutParams s = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//            s.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, -1);
////添加位置信息 -1表示相对于父控件的位置 ，如果要相对某个平级控件则参数是该控件的ID
////            s.setMargins(10, 10, 10, 10);//设置左，上，右，下，的距离
//            //上面的定义好了之后可以用了：

            RelativeLayout.LayoutParams layoutParams_txt1 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv_num.setLayoutParams(layoutParams_txt1);
            tv.setLayoutParams(layoutParams_txt1);

            // 添加到每行的linearlayout中
            lLayout.addView(tv);
            lLayout.addView(tv_num);

            // 每个linearlayout都在前一个的下面，第一个在顶,不处理
            if (i > 0) {
                relativeParams.addRule(RelativeLayout.BELOW, i + 10 - 1);
            }
            // 把每个linearlayout加到relativelayout中
            ticket_door_layout.addView(lLayout, relativeParams);
        }


    }


}
