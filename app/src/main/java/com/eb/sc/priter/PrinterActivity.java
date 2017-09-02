package com.eb.sc.priter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.MainActivity;
import com.eb.sc.R;
import com.eb.sc.activity.CaptureActivity;
import com.eb.sc.bean.TickBean;
import com.eb.sc.bean.TicketInfo;
import com.eb.sc.scanner.BaseActivity;
import com.eb.sc.scanner.ExecutorFactory;
import com.eb.sc.sdk.eventbus.ConnectEvent;
import com.eb.sc.sdk.eventbus.ConnentSubscriber;
import com.eb.sc.sdk.eventbus.GetOrderEvent;
import com.eb.sc.sdk.eventbus.GetOrderSubscriber;
import com.eb.sc.sdk.eventbus.NetEvent;
import com.eb.sc.sdk.eventbus.PutEvent;
import com.eb.sc.sdk.recycle.CommonAdapter;
import com.eb.sc.sdk.recycle.ViewHolder;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.SupportMultipleScreensUtil;
import com.eb.sc.utils.Utils;
import com.eb.sc.widget.ProgressDialog;
import com.zkteco.android.biometric.module.fingerprint.FingerprintFactory;
import com.zkteco.android.biometric.module.idcard.IDCardReaderFactory;

import org.aisen.android.component.eventbus.NotificationCenter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/9/1.
 */

public class PrinterActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView recycleview;
    private Bitmap mBitmap = null;
    private boolean runFlag = true;
    private CommonAdapter<TickBean> mAdapter;
    private List<TickBean> mList = new ArrayList<>();
    private TextView top_title;
    private LinearLayout top_left;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationCenter.defaultCenter().subscriber(GetOrderEvent.class, getOrderscriber);
        setContentView(R.layout.activity_printer);
        View rootView = findViewById(android.R.id.content);
        SupportMultipleScreensUtil.init(getApplication());
        SupportMultipleScreensUtil.scale(rootView);
        recycleview = (RecyclerView) findViewById(R.id.recycleview);
        top_title = (TextView) findViewById(R.id.top_title);
        top_left = (LinearLayout) findViewById(R.id.top_left);
        top_left.setOnClickListener(this);
        top_title.setText("售票");
        ExecutorFactory.executeThread(new Runnable() {
            @Override
            public void run() {
                while (runFlag) {
                    if (bindSuccessFlag) {
                        //检测打印是否正常
                        try {
                            String printerSoftVersion = mIzkcService.getFirmwareVersion1();
                            if (TextUtils.isEmpty(printerSoftVersion)) {
                                printerSoftVersion = mIzkcService.getFirmwareVersion2();
                            }
                            if (TextUtils.isEmpty(printerSoftVersion)) {
                                mIzkcService.setModuleFlag(module_flag);
                                mHandler.obtainMessage(1).sendToTarget();
                            } else {
                                mHandler.obtainMessage(0, printerSoftVersion).sendToTarget();
                                runFlag = false;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

        initData();
    }

    private void initData() {
        BaseConfig bg = BaseConfig.getInstance(this);
        final String s = bg.getStringValue(Constants.address, "-1");
        TickBean ifo = new TickBean();
        ifo.setNmae(Utils.getXiangmu(this));
        ifo.setpNum(1);
        ifo.setId_tick(s);
        ifo.setPrice("129");
        mList.add(ifo);
        mAdapter = new CommonAdapter<TickBean>(PrinterActivity.this, R.layout.tick_item, mList) {
            @Override
            protected void convert(ViewHolder holder, final TickBean tickBean, int position) {
                holder.setText(R.id.name, "项目点:" + tickBean.getNmae());
                holder.setText(R.id.price, "￥" + tickBean.getPrice());
                holder.setText(R.id.num, tickBean.getpNum() + "");
                holder.setOnClickListener(R.id.jian, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int num = tickBean.getpNum();
                        if (num < 2) {
                            Toast.makeText(PrinterActivity.this, "售票数量不能小于1", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            num = num - 1;
                            tickBean.setpNum(num);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

                holder.setOnClickListener(R.id.jia, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int num = tickBean.getpNum();
                        if (num >= 100) {
                            return;
                        } else {
                            num = num + 1;
                            tickBean.setpNum(num);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
                holder.setOnClickListener(R.id.go_buy, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String datas = Utils.getItemId(PrinterActivity.this) + "&" + (Double.parseDouble(tickBean.getPrice()) * tickBean.getpNum() + "") + "&" + "3" + "&" + (tickBean.getpNum() + "");
                        Log.i("bbbb", "datas=" + datas);
                        String updata = Utils.getBuy(PrinterActivity.this, datas);
                        Log.i("bbbb", "updata=" + updata);
                        boolean abg = PushManager.getInstance(PrinterActivity.this).sendMessage(updata);
                        if (abg) {
                            showAlert("正在提交..", true);
                        } else {
                            dismissAlert();
                            Toast.makeText(PrinterActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        };
        recycleview.setLayoutManager(new LinearLayoutManager(PrinterActivity.this));
        recycleview.setAdapter(mAdapter);
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String status;
                    String aidlServiceVersion;
                    try {
                        mIzkcService.setModuleFlag(0);
//					mIzkcService.sendRAWData("printer", new byte[] {0x1b, 0x40});
                        status = mIzkcService.getPrinterStatus();
//					tv_printStatus.setText(status);
                        aidlServiceVersion = mIzkcService.getServiceVersion();
//					tv_printer_soft_version.setText(msg.obj + "AIDL Service Version:" + aidlServiceVersion);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case 1:
//                    Toast.makeText(PrinterActivity.this, "正在连接打印机，请稍后...", Toast.LENGTH_SHORT).show();
                    break;
                case 8:
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onStop() {
        super.onStop();
    }

    int imageType = 0;

    private void printPurcase(boolean hasStartPic, boolean hasEndPic) {
        TicketInfo ticketInfo = PrinterHelper.getInstance(this).getTicket(mIzkcService, hasStartPic, hasEndPic);
        PrinterHelper.getInstance(this).printPurchaseBillModelTwo(mIzkcService, ticketInfo, imageType);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                PrinterActivity.this.finish();
                break;
        }
    }

    //收到反回的数据
    GetOrderSubscriber getOrderscriber = new GetOrderSubscriber() {
        @Override
        public void onEvent(GetOrderEvent event) {
            dismissAlert();
            String order_id = event.getOrder_id();
            BaseConfig bg = BaseConfig.getInstance(PrinterActivity.this);
            bg.setStringValue(Constants.ORDER_ID, order_id);
            Intent intent = new Intent(PrinterActivity.this, CaptureActivity.class);
            intent.putExtra("select", "1");
            startActivityForResult(intent, 0);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter().unsubscribe(GetOrderEvent.class, getOrderscriber);
    }


    /**
     * 显示加载图标
     *
     * @param txt
     */
    public void showAlert(String txt, final boolean isCancel) {
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        if (txt != null) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this, isCancel);
            }
            progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (isCancel) {
                            progressDialog.dismiss();
                        }
                    }
                    return false;
                }
            });
            progressDialog.show();
            progressDialog.showText(txt);
        }
    }

    /**
     * 关闭加载图标
     */
    public void dismissAlert() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                Bundle b = data.getExtras(); //data为B中回传的Intent
                String str = b.getString("scansts");//str即为回传的值
                if (!TextUtils.isEmpty(str)) {
                    BaseConfig bg = BaseConfig.getInstance(PrinterActivity.this);
                      String order=bg.getStringValue(Constants.ORDER_ID, "");
                    String updata = Utils.sentBuy(PrinterActivity.this, str+"&"+order);
                    Log.i("bbbb", "updata=" + updata);
                    boolean abg = PushManager.getInstance(PrinterActivity.this).sendMessage(updata);
                    if (abg) {
                        showAlert("正在支付..", true);
                    } else {
                        dismissAlert();
                        Toast.makeText(PrinterActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }
}
