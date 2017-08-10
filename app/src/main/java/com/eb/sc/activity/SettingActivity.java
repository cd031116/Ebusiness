package com.eb.sc.activity;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.base.MyApplication;
import com.eb.sc.bean.ItemInfo;
import com.eb.sc.bean.Params;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.EventSubscriber;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.sdk.eventbus.RefreshEvent;
import com.eb.sc.sdk.eventbus.RefreshSubscriber;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.utils.Utils;
import com.eb.sc.widget.RestartDialog;
import com.eb.sc.widget.ShengjiDialog;

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
    @Bind(R.id.tongbu)
    RelativeLayout tongbu;
    @Bind(R.id.submit)
    TextView submit;

    //----------------------------
    /**
     * popup窗口里的ListView
     */
    private ListView mTypeLv;
    /**
     * popup窗口
     */
    private PopupWindow typeSelectPopup;
    /**
     * 模拟的假数据
     */
    private List<String> testData;
    /**
     * 数据适配器
     */
    private ArrayAdapter<String> testDataAdapter;
    private boolean isconnect = true;
    private List<ItemInfo> mList = new ArrayList<>();
    private  String address_ip="";
    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        super.initView();
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(RefreshEvent.class, refreshEvent);
        top_title.setText("设置");
        BaseConfig bg = new BaseConfig(this);
        String b = bg.getStringValue(Constants.havelink, "-1");
        if ("1".equals(b)) {
            isconnect = true;
        } else {
            isconnect = false;
        }
        if (NetWorkUtils.isNetworkConnected(this) && isconnect) {
            changeview(true);
        } else {
            changeview(false);
        }
        address_ip=bg.getStringValue(Constants.tcp_ip,"");
    }

    @Override
    public void initData() {
        super.initData();

        TestData();
        code.setText(Utils.getImui(this) + "");
        BaseConfig bg = new BaseConfig(this);
        ip_tcp.setText(bg.getStringValue(Constants.tcp_ip, ""));
        ip_port.setText(bg.getStringValue(Constants.ip_port, ""));

        String s = bg.getStringValue(Constants.address, "-1");
        if (!TextUtils.isEmpty(s)) {
            for (int i = 0; i < mList.size(); i++) {
                if (s.equals(mList.get(i).getCode())) {
                    state.setText(mList.get(i).getName());
                }
            }
        }

    }

    @OnClick({R.id.top_left, R.id.top_right_text, R.id.amend, R.id.state,R.id.tongbu,R.id.submit})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                Intent intent1= new Intent(SettingActivity.this,CheckActivity.class);
                SettingActivity.this.setResult(1,intent1);
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
            case R.id.tongbu:
                BaseConfig bgs = new BaseConfig(this);
                String http_urls = ip_tcp.getText().toString();
                if(!address_ip.equals(http_urls)){
                    Toast.makeText(SettingActivity.this,"您修改了ip地址,请先保存再同步",Toast.LENGTH_SHORT).show();
                   return;
                }
                boolean a=  PushManager.getInstance(SettingActivity.this).sendMessage(Params.SHEBEI);
                if(a){
                    Toast.makeText(SettingActivity.this,"同步成功",Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(SettingActivity.this,"同步失败",Toast.LENGTH_SHORT).show();
                break;
            case R.id.submit:
                BaseConfig bg = new BaseConfig(this);
                String http_url = ip_tcp.getText().toString();
                String http_code = ip_port.getText().toString();
                if (!TextUtils.isEmpty(http_url)) {
                    bg.setStringValue(Constants.tcp_ip, http_url);
                }
                if (!TextUtils.isEmpty(http_code)) {
                    bg.setStringValue(Constants.ip_port, http_code);
                }
                Intent intent= new Intent(SettingActivity.this,CheckActivity.class);
                if (!http_url.equals(address_ip)) {
                    if(TextUtils.isEmpty(address_ip)){
                        intent.putExtra("result",http_url);
                    }else {
                        showDialogMsg();
                        break;
                    }
                }else{
                    intent.putExtra("result","");
                }
                Toast.makeText(SettingActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                SettingActivity.this.setResult(1,intent );
                SettingActivity.this.finish();
                break;
        }
    }

    RefreshSubscriber refreshEvent = new RefreshSubscriber() {
        @Override
        public void onEvent(RefreshEvent refreshEvent) {
            TestData();
        }
    };

    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber(){
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
        NotificationCenter.defaultCenter().unsubscribe(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(RefreshEvent.class, refreshEvent);
        address_ip="";
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
        if (mList != null) {
            mList.clear();
        }
        testData = new ArrayList<>();
        BaseConfig bg = new BaseConfig(SettingActivity.this);
        String list_item = bg.getStringValue(Constants.px_list, "");
        if(TextUtils.isEmpty(list_item)){
           return;
        }
            mList = JSON.parseArray(list_item, ItemInfo.class);
            for (int i = 0; i < mList.size(); i++) {
                testData.add(mList.get(i).getName());
            }
    }

    /**
     * 初始化popup窗口
     */
    private void initSelectPopup() {
        mTypeLv = new ListView(this);
        // 设置适配器
        testDataAdapter = new ArrayAdapter<String>(this, R.layout.popup_text_item, testData);
        mTypeLv.setAdapter(testDataAdapter);
        // 设置ListView点击事件监听
        mTypeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 在这里获取item数据
                BaseConfig bg = new BaseConfig(SettingActivity.this);
                String value = testData.get(position);
                // 把选择的数据展示对应的TextView上
                state.setText(value);
                for (int i = 0; i < mList.size(); i++) {
                    if (value.equals(mList.get(i).getName())) {
                        bg.setStringValue(Constants.address, mList.get(i).getCode());
                        Log.i("tttt","top_title="+mList.get(i).getCode());

                    }
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

    @Override
    public void onBackPressed() {
        Intent intent= new Intent(SettingActivity.this,CheckActivity.class);
        SettingActivity.this.setResult(1,intent );
        SettingActivity.this.finish();
    }



    private void showDialogMsg() {
        new RestartDialog(this, R.style.dialog, "您修改了IP地址,需重启应用!", new RestartDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm, String text) {
                if (confirm) {
                    PushManager.getInstance(SettingActivity.this).close();
                    BaseConfig bg = new BaseConfig(SettingActivity.this);
                    bg.setStringValue(Constants.tcp_ip, ip_tcp.getText().toString());
                    bg.setStringValue(Constants.address,"");
                    bg.setStringValue(Constants.px_list,"");
                    dialog.dismiss();

                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    MyApplication.instance.getActivityManager().popAllActivityExceptOne(); // 自定义方法，关闭当前打开的所有avtivity

                } else {
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();

    }




}
