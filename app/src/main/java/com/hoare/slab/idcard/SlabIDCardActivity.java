package com.hoare.slab.idcard;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.bean.DataInfo;
import com.eb.sc.offline.OfflLineDataDb;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.EventSubscriber;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.sdk.eventbus.PutEvent;
import com.eb.sc.sdk.eventbus.PutSubscriber;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.NetWorkUtils;
import com.eb.sc.utils.PlayVedio;
import com.eb.sc.utils.Utils;
import com.eb.sc.widget.CommomDialog;
import com.eb.sc.widget.ShowMsgDialog;
import com.hoare.hand.idcard.GetCardActivity;
import com.hoare.hand.idcard.IDCardModel;

import org.aisen.android.component.eventbus.NotificationCenter;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import cn.pda.serialport.Tools;
/**
*aunthor lyj
* create 2018/6/19/019 17:45  平板身份证
**/
public class SlabIDCardActivity extends BaseActivity {

    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.right_bg)
    ImageView mRight_bg;
    @Bind(R.id.top_title)
    TextView top_title;
    private IDCardManager manager;
    private ReadThread thread;
    private boolean runFlag = true;
    private boolean startFlag = false;
    private boolean isconnect = true;
    private String idcard_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (manager == null) {
            manager = new IDCardManager(SlabIDCardActivity.this);
        }
        startFlag = true;
        NotificationCenter.defaultCenter().subscriber(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().subscriber(PutEvent.class, putSubscriber);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_get_card;
    }

    @Override
    public void initView() {
        super.initView();
        top_title.setText("身份证感应");
        String path = Environment.getExternalStorageDirectory() + "/IDCard";
        File file_paper = new File(path);
        if (!file_paper.exists()) {
            file_paper.mkdirs();
        }
        thread = new ReadThread();
        thread.start();
    }

    @Override
    public void initData() {
        super.initData();
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


    @OnClick({R.id.top_left,R.id.close_bg})
    void onclick(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                SlabIDCardActivity.this.finish();
                break;
            case R.id.close_bg:
                ExitDialog();
                break;
        }
    }


    private class ReadThread extends Thread {
        @Override
        public void run() {
            while (runFlag) {
                if (startFlag && manager != null) {
                    if (manager.findCard(200)) {
                        handler.sendEmptyMessage(1);
                        IDCardModel model = null;

                        long time = System.currentTimeMillis();
//                        if (checkBox_fp.isChecked()) {
//                            //获取身份证信息、图像、指纹
//                            model = manager.getDataFP(2000);
//                            Log.e("get data time:", System.currentTimeMillis() - time +"ms");
//                        }

                        if (model != null) {
                            sendMessage(model.getName(), model.getSex(), model.getNation(),
                                    model.getYear(), model.getMonth(), model.getDay(),
                                    model.getAddress(), model.getIDCardNumber(), model.getOffice(),
                                    model.getBeginTime(), model.getEndTime(), model.getOtherData(),
                                    model.getPhotoBitmap(), Tools.Bytes2HexString(model.getFP1(), 512), Tools.Bytes2HexString(model.getFP2(), 512));
                        } else {
                            //获取身份证信息、图像
                            model = manager.getData(2000);
                            if (model != null) {
                                sendMessage(model.getName(), model.getSex(), model.getNation(),
                                        model.getYear(), model.getMonth(), model.getDay(),
                                        model.getAddress(), model.getIDCardNumber(), model.getOffice(),
                                        model.getBeginTime(), model.getEndTime(), model.getOtherData(),
                                        model.getPhotoBitmap(), "未读出指纹数据", "未读出指纹数据");
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            super.run();
        }

    }

    //结果
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    Bundle bundle = msg.getData();
//			    	String id = bundle.getString("id");//身份证号码
                    idcard_id = bundle.getString("id");
                    if (NetWorkUtils.isNetworkConnected(SlabIDCardActivity.this) && isconnect) {
                        String updata = Utils.getIdcard(SlabIDCardActivity.this, idcard_id);
                        PushManager.getInstance(SlabIDCardActivity.this).sendMessage(updata);
                    } else {
                        Toast.makeText(SlabIDCardActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT).show();
                    }

                    //获取身份证信息：姓名、性别、出生年、月、日、住址、身份证号、签发机关、有效期开始、结束、（额外信息新地址（一般情况为空））
                    //获取图片位图，并显示：
//                    imageView.setImageBitmap(photoBitmap);
                    break;
                case 1:
//                    clear();
//                    showToast("发现身份证!\n正在获取身份证数据...");
                    break;
                case 2:
                    break;
                case 3:

                    break;
                default:
                    break;
            }
        }

        ;
    };

    private void sendMessage(String name, String sex, String nation,
                             String year, String month, String day, String address, String id,
                             String office, String start, String stop, String newaddress
            , Bitmap bitmap, String fp1, String fp2) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("sex", sex);
        bundle.putString("nation", nation);
        bundle.putString("year", year);
        bundle.putString("month", month);
        bundle.putString("day", day);
        bundle.putString("address", address);
        bundle.putString("id", id);
        bundle.putString("office", office);
        bundle.putString("begin", start);
        bundle.putString("end", stop);
        bundle.putString("newaddress", newaddress);
        bundle.putString("fp1", fp1);
        bundle.putString("fp2", fp2);
        message.setData(bundle);
        handler.sendMessage(message);
    }
//----------------------------------------------------------验票


    //在线成功
    PutSubscriber putSubscriber = new PutSubscriber() {
        @Override
        public void onEvent(PutEvent putEvent) {
            String srt = putEvent.getStrs();
            String sgs = putEvent.getStrs().substring(0, 2);
            int renshu = Integer.parseInt(putEvent.getStrs().substring(2, 6), 16);
            Log.d("dddd", "putEvent sgs: " + sgs + ",renshu:" + renshu + ",id_n:" + idcard_id + ",xiangmu:" + Utils.getXiangmu(SlabIDCardActivity.this));
            if ("01".equals(sgs)) {
                showDialogd("成人票", idcard_id, Utils.getXiangmu(SlabIDCardActivity.this), String.valueOf(renshu));
                PlayVedio.getInstance().play(SlabIDCardActivity.this, 5);
            } else if ("02".equals(sgs)) {
                PlayVedio.getInstance().play(SlabIDCardActivity.this, 2);
                showDialogd("儿童票", idcard_id, Utils.getXiangmu(SlabIDCardActivity.this), String.valueOf(renshu));
            } else if ("03".equals(sgs)) {
                PlayVedio.getInstance().play(SlabIDCardActivity.this, 7);
                showDialogd("优惠票", idcard_id, Utils.getXiangmu(SlabIDCardActivity.this), String.valueOf(renshu));
            } else if ("04".equals(sgs)) {
                PlayVedio.getInstance().play(SlabIDCardActivity.this, 7);
                showDialogd("招待票", idcard_id, Utils.getXiangmu(SlabIDCardActivity.this), String.valueOf(renshu));
            } else if ("05".equals(sgs)) {
                PlayVedio.getInstance().play(SlabIDCardActivity.this, 3);
                showDialogd("老年票", idcard_id, Utils.getXiangmu(SlabIDCardActivity.this), String.valueOf(renshu));
            } else if ("06".equals(sgs)) {
                PlayVedio.getInstance().play(SlabIDCardActivity.this, 8);
                showDialogd("团队票", idcard_id, Utils.getXiangmu(SlabIDCardActivity.this), String.valueOf(renshu));
            } else if ("07".equals(sgs)) {
                PlayVedio.getInstance().play(SlabIDCardActivity.this, 6);
                showDialogMsg("已使用");
            } else if ("08".equals(sgs)) {
                PlayVedio.getInstance().play(SlabIDCardActivity.this, 1);
                showDialogMsg("无效票");
            } else if ("09".equals(sgs)) {
                PlayVedio.getInstance().play(SlabIDCardActivity.this, 9);
                showDialogMsg("已过期");
            } else if ("0A".equals(sgs)) {
                showDialogMsg("网络超时");
            } else if ("0B".equals(sgs)) {
                showDialogMsg("票型不符");
            } else if ("0C".equals(sgs)) {
                showDialogMsg("团队满人");
            } else if ("0D".equals(sgs)) {
                showDialogMsg("游玩尚未开始（网购票当天购买要第二天用）");
            } else if ("0E".equals(sgs)) {
                showDialogd("年卡", idcard_id, Utils.getXiangmu(SlabIDCardActivity.this), String.valueOf(renshu));
            } else if ("10".equals(sgs)) {
                showDialogMsg("通道不符");
            }
//            String srt = putEvent.getStrs();
//            String sgs = putEvent.getStrs().substring(0, 2);
//            String renshu = putEvent.getStrs().substring(srt.length() - 2, srt.length());
//            if ("06".equals(sgs)) {
//                PlayVedio.getInstance().play(IDCardActivity.this,8);
//                showDialogd("团队票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(Integer.parseInt(renshu)));
//            } else if ("02".equals(sgs)) {
//                PlayVedio.getInstance().play(IDCardActivity.this,2);
//                showDialogd("儿童票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(Integer.parseInt(renshu)));
//            } else if ("01".equals(sgs)) {
//                PlayVedio.getInstance().play(IDCardActivity.this,5);
//                showDialogd("成人票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(Integer.parseInt(renshu)));
//            } else if ("05".equals(sgs)) {
//                PlayVedio.getInstance().play(IDCardActivity.this,3);
//                showDialogd("老年票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(Integer.parseInt(renshu)));
//            } else if ("03".equals(sgs)) {
//                PlayVedio.getInstance().play(IDCardActivity.this,7);
//                showDialogd("优惠票", idcard_id, Utils.getXiangmu(IDCardActivity.this), String.valueOf(Integer.parseInt(renshu)));
//            } else if ("07".equals(sgs)) {
//                PlayVedio.getInstance().play(IDCardActivity.this,6);
//                showDialogMsg("已使用");
//            } else if("09".equals(sgs)){
//                PlayVedio.getInstance().play(IDCardActivity.this,9);
//                showDialogMsg("已过期");
//            }else {
//                PlayVedio.getInstance().play(IDCardActivity.this,1);
//                showDialogMsg("无效票");
//            }
        }
    };

    //长连接
    ConnentSubscriber connectEventSubscriber = new ConnentSubscriber() {
        @Override
        public void onEvent(ConnectEvent event) {
            BaseConfig bg = new BaseConfig(SlabIDCardActivity.this);
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

    //有效票-有线
    private void showDialogd(String names, final String num, String code, final String renshu) {
        new CommomDialog(this, R.style.dialog, names, num, code, renshu, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    DataInfo data = new DataInfo();
                    data.setId(num);
                    data.setUp(true);
                    data.setNet(true);
                    data.setType(1);
                    data.setpNum(renshu);
                    data.setName(Utils.getXiangmu(SlabIDCardActivity.this));
                    data.setInsertTime(System.currentTimeMillis() + "");
                    OfflLineDataDb.insert(data);
                    dialog.dismiss();
                    idcard_id = "";
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
                    idcard_id = "";
                }
            }
        }).setTitle("提示").show();
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


    //-------------------------------------------
    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter().unsubscribe(ConnectEvent.class, connectEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(NetEvent.class, netEventSubscriber);
        NotificationCenter.defaultCenter().unsubscribe(PutEvent.class, putSubscriber);
        startFlag = false;
        if (manager != null) {
            manager.close();
        }
        runFlag = false;
        try {
//			clearWallpaper();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.toString();
        }
    }

    //app暂停
    private boolean pasue = false;

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (!pasue && manager != null) {
            pasue = true;
            manager.close();
            manager = null;
            startFlag = false;
        }
    }

    //app暂停后重启
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (pasue) {
            pasue = false;
            if (manager == null) {
                manager = new IDCardManager(SlabIDCardActivity.this);
            }
            startFlag = true;
        }
    }
}
