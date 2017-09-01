package com.eb.sc.scanner;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.eb.sc.R;
import com.eb.sc.bean.TicketInfo;


/**
 * Created by Administrator on 2017/9/1.
 */

public class PrinterActivity extends BaseActivity {

    private Button btnPrintModel, btnUnicode;
    private Bitmap mBitmap = null;
    private boolean runFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        //打印模板
        printPurcase(true, true);
    }

    Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String status;
                    String aidlServiceVersion;
                    try {
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
                    Toast.makeText(PrinterActivity.this, "正在连接打印机，请稍后...", Toast.LENGTH_SHORT).show();
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


    int imageType=0;
    private void printPurcase(boolean hasStartPic, boolean hasEndPic) {
        TicketInfo ticketInfo = PrinterHelper.getInstance(this).getTicket(mIzkcService, hasStartPic, hasEndPic);
        PrinterHelper.getInstance(this).printPurchaseBillModelTwo(mIzkcService,ticketInfo, imageType);
    }
}
