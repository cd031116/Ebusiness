package com.eb.sc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.MainActivity;
import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.bean.DataInfo;
import com.eb.sc.idcard.IDCardActivity;
import com.eb.sc.offline.OfflLineDataDb;
import com.eb.sc.scanner.ScannerActivity;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.EventSubscriber;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.sdk.eventbus.PutEvent;
import com.eb.sc.sdk.eventbus.PutSubscriber;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.HexStr;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.utils.Utils;
import com.eb.sc.utils.isIdNum;
import com.eb.sc.widget.CommomDialog;
import com.eb.sc.widget.ShowMsgDialog;

import org.aisen.android.component.eventbus.NotificationCenter;

import butterknife.Bind;
import butterknife.OnClick;

public class SelectActivity extends BaseActivity {
    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.idcard)
    RelativeLayout idcard;
    @Bind(R.id.scan)
    RelativeLayout scan;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    @Bind(R.id.cheeck)
    TextView cheeck;
    @Bind(R.id.password)
    EditText id_num;
    @Bind(R.id.t_text)
    TextView t_text;
    String id_n = "";
    private boolean xiumian = true;
    private boolean isconnect = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select;
    }

    @Override
    public void initView() {
        super.initView();
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(PutEvent.class, putSubscriber);
        top_title.setText("扫描检票");
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
        settitle();
    }

    private void settitle() {
        BaseConfig bg = BaseConfig.getInstance(this);
        int select = bg.getIntValue(Constants.JI_XING, -1);
        if (select == 1) {
            t_text.setText("按键扫码");
        } else if (select == 2) {
            t_text.setText("身份证感应");
        } else {
            t_text.setText("身份证感应");
        }
    }


    @OnClick({R.id.idcard, R.id.scan, R.id.top_left, R.id.cheeck, R.id.close_bg})
    void onclick(View v) {
        BaseConfig bg = BaseConfig.getInstance(this);
        switch (v.getId()) {
            case R.id.idcard:
                int select = bg.getIntValue(Constants.JI_XING, -1);
                if (select == 1) {
                    startActivity(new Intent(SelectActivity.this, ScannerActivity.class));
                } else if (select == 2){
                    startActivity(new Intent(SelectActivity.this, IDCardActivity.class));
                }else {
                    startActivity(new Intent(SelectActivity.this, MainActivity.class));
                }
                break;
            case R.id.scan:
                    Intent intent = new Intent(SelectActivity.this, CaptureActivity.class);
                    intent.putExtra("select", "2");
                    startActivity(intent);
                break;
            case R.id.top_left:
                SelectActivity.this.finish();
                break;
            case R.id.cheeck:
                id_n = id_num.getText().toString();
                if (TextUtils.isEmpty(id_n)) {
                    Toast.makeText(SelectActivity.this, "请输入身份证号码!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isIdNum.isIdNum(id_n)) {
                    Toast.makeText(SelectActivity.this, "您输入的身份证号码不正确!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (NetWorkUtils.isNetworkConnected(this) && isconnect) {
                    String updata = Utils.getIdcard(this, id_n);
                    PushManager.getInstance(this).sendMessage(updata);
                } else {
                   Toast.makeText(SelectActivity.this,"已与服务器断开连接!",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.close_bg:
                ExitDialog();
                break;

        }
    }

    //在线成功
    PutSubscriber putSubscriber = new PutSubscriber() {
        @Override
        public void onEvent(PutEvent putEvent) {
            if (!xiumian) {
                String srt = putEvent.getStrs();
                String sgs = putEvent.getStrs().substring(0, 2);
                String renshu = putEvent.getStrs().substring(srt.length() - 2, srt.length());
                if ("06".equals(sgs)) {
                    showDialogd("团队票", id_n, Utils.getXiangmu(SelectActivity.this), String.valueOf(Integer.parseInt(renshu)));
                } else if ("02".equals(sgs)) {
                    showDialogd("儿童票", id_n, Utils.getXiangmu(SelectActivity.this), String.valueOf(Integer.parseInt(renshu)));
                } else if ("01".equals(sgs)) {
                    showDialogd("成人票", id_n, Utils.getXiangmu(SelectActivity.this), String.valueOf(Integer.parseInt(renshu)));
                } else if ("05".equals(sgs)) {
                    showDialogd("老年票", id_n, Utils.getXiangmu(SelectActivity.this), String.valueOf(Integer.parseInt(renshu)));
                } else if ("03".equals(sgs)) {
                    showDialogd("优惠票", id_n, Utils.getXiangmu(SelectActivity.this), String.valueOf(Integer.parseInt(renshu)));
                } else if ("07".equals(sgs)) {
                    showDialogMsg("已使用");
                } else {
                    showDialogMsg("无效票");
                }
            }
        }
    };

    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber() {
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(SelectActivity.this);
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
        NotificationCenter.defaultCenter().unsubscribe(PutEvent.class, putSubscriber);
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

    //有效票-有线   姓名    身份证    项目
    private void showDialogd(String names, final String num, String code, String renshu) {
        new CommomDialog(this, R.style.dialog, names, num, code, renshu, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    DataInfo data = new DataInfo();
                    data.setId(num);
                    data.setUp(true);
                    data.setNet(true);
                    data.setType(1);
                    data.setName(Utils.getXiangmu(SelectActivity.this));
                    data.setInsertTime(System.currentTimeMillis() + "");
                    OfflLineDataDb.insert(data);
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();
    }

    //有效票
    private void showDialog(String names, final String nums, String code) {
        new CommomDialog(this, R.style.dialog, names, nums, code, "", new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    if (!NetWorkUtils.isNetworkConnected(SelectActivity.this)) {//无网络
                        DataInfo data = new DataInfo();
                        data.setId(nums);
                        data.setUp(false);
                        data.setNet(false);
                        data.setName(Utils.getXiangmu(SelectActivity.this));
                        data.setType(1);
                        data.setInsertTime(System.currentTimeMillis() + "");
                        OfflLineDataDb.insert(data);
                    } else {//有网络
                        byte[] updata = HexStr.hex2byte(HexStr.str2HexStr(nums));
                    }
                    dialog.dismiss();
                }

            }
        }).setTitle("提示").show();
    }

    //无效票
    private void showDialogMsg(String names) {
        new ShowMsgDialog(this, R.style.dialog, names, new ShowMsgDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();
    }

    @Override
    public void onResume() {
        super.onResume();
        xiumian = false;
        settitle();
    }

    @Override
    public void onPause() {
        super.onPause();
        xiumian = true;
    }
}
