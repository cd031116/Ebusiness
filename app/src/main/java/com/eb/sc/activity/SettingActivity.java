package com.eb.sc.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.EventSubscriber;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.utils.Utils;

import org.aisen.android.component.eventbus.NotificationCenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.ip_tcp)
    EditText ip_tcp;
    @Bind(R.id.ip_port)
    EditText ip_port;
    @Bind(R.id.state)
    EditText state;
    @Bind(R.id.amend)
    RelativeLayout amend;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    @Bind(R.id.code)
    EditText code;
    //----------------------------
    /** popup窗口里的ListView */
    private ListView mTypeLv;
    /** popup窗口 */
    private PopupWindow typeSelectPopup;
    /** 模拟的假数据 */
    private List<String> testData;
    /** 数据适配器 */
    private ArrayAdapter<String> testDataAdapter;
    private boolean isconnect = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        super.initView();
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        top_title.setText("设置");
        if(NetWorkUtils.isNetworkConnected(this)){
            BaseConfig bg=new BaseConfig(this);
            bg.setStringValue(Constants.havenet,"0");
            changeview(true);
        }else {
            changeview(false);
        }
    }

    @Override
    public void initData() {
        super.initData();
        code.setText(Utils.getImui(this) + "");
        BaseConfig bg=new BaseConfig(this);
        ip_tcp.setText( bg.getStringValue(Constants.tcp_ip,""));
        ip_port.setText(bg.getStringValue(Constants.ip_port,""));
    }

    @OnClick({R.id.top_left, R.id.top_right_text, R.id.amend,R.id.state})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                SettingActivity.this.finish();
                break;
            case R.id.top_right_text:
                SettingActivity.this.finish();
                break;
            case R.id.amend:
                startActivity(new Intent(SettingActivity.this, AmendActivity.class));
                break;
            case R.id.state:
                // 使用isShowing()检查popup窗口是否在显示状态
                initSelectPopup();
                if (typeSelectPopup != null && !typeSelectPopup.isShowing()) {
                    typeSelectPopup.showAsDropDown(state, 0, 10);
                }
                break;
        }
    }

    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber() {
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(SettingActivity.this);
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
        BaseConfig bg=new BaseConfig(this);
        String http_url=ip_tcp.getText().toString();
        String http_code=ip_port.getText().toString();
        if(!TextUtils.isEmpty(http_url)){
            bg.setStringValue(Constants.tcp_ip,http_url);
        }
        if(!TextUtils.isEmpty(http_code)){
            bg.setStringValue(Constants.ip_port,http_code);
        }

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
    /**
     * 模拟假数据
     */
    private void TestData() {
        testData = new ArrayList<>();
        testData.add("大门票");  //1      T170401
        testData.add("游船");  //2         T170402
        testData.add("深林漫步"); //3       T170403
        testData.add("激战鲨鱼岛");//4   T170404
        testData.add("旋转木马");//5       T170405
        testData.add("9D电影");//6       T170406
        testData.add("飞碟");//7          T170407
        testData.add("漂流"); //8          T170408
        testData.add("丛林穿越");//9         T170409
        testData.add("飞索");//10         T170411
    }

    /**
     * 初始化popup窗口
     */
    private void initSelectPopup() {
        mTypeLv = new ListView(this);
        TestData();
        // 设置适配器
        testDataAdapter = new ArrayAdapter<String>(this, R.layout.popup_text_item, testData);
        mTypeLv.setAdapter(testDataAdapter);
        // 设置ListView点击事件监听
        mTypeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 在这里获取item数据
                BaseConfig bg=new BaseConfig(SettingActivity.this);
                String value = testData.get(position);
                // 把选择的数据展示对应的TextView上
                state.setText(value);
                if(value.contains("大门票")){
                    bg.setStringValue(Constants.address,"1");
                }
                if(value.contains("游船")){
                    bg.setStringValue(Constants.address,"2");
                }
                if(value.contains("深林漫步")){
                    bg.setStringValue(Constants.address,"3");
                }
                if(value.contains("激战鲨鱼岛")){
                    bg.setStringValue(Constants.address,"4");
                }if(value.contains("旋转木马")){
                    bg.setStringValue(Constants.address,"5");
                }if(value.contains("9D电影")){
                    bg.setStringValue(Constants.address,"6");
                }
                if(value.contains("飞碟")){
                    bg.setStringValue(Constants.address,"7");
                }
                if(value.contains("漂流")){
                    bg.setStringValue(Constants.address,"8");
                }

                if(value.contains("丛林穿越")){
                    bg.setStringValue(Constants.address,"9");
                }
                if(value.contains("飞索")){
                    bg.setStringValue(Constants.address,"10");
                }
                // 选择完后关闭popup窗口
                typeSelectPopup.dismiss();
            }
        });

        typeSelectPopup = new PopupWindow(mTypeLv, state.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        // 取得popup窗口的背景图片
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_corner);
        typeSelectPopup.setBackgroundDrawable(drawable);
        typeSelectPopup.setFocusable(true);
        typeSelectPopup.setOutsideTouchable(true);
        typeSelectPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 关闭popup窗口
                typeSelectPopup.dismiss();
            }
        });
    }




}
