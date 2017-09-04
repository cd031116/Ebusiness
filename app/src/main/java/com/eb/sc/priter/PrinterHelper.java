package com.eb.sc.priter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.os.SystemClock;

import com.eb.sc.R;
import com.eb.sc.bean.TicketInfo;
import com.smartdevice.aidl.IZKCService;

public class PrinterHelper {

    /* 等待打印缓冲刷新的时间 */
    private static final int mIzkcService_BUFFER_FLUSH_WAITTIME = 150;
    /* 分割线 */
    private static final String mIzkcService_CUT_OFF_RULE = "--------------------------------\n";

    // 品名占位长度
    private static final int GOODS_NAME_LENGTH = 6;
    // 单价占位长度
    private static final int GOODS_UNIT_PRICE_LENGTH = 6;
    // 价格占位长度
    private static final int GOODS_PRICE_LENGTH = 6;
    // 数量占位长度
    private static final int GOODS_AMOUNT = 6;

    private Context mContext;

    private static PrinterHelper _instance;

    private PrinterHelper(Context mContext) {
        this.mContext = mContext;
    }

    synchronized public static PrinterHelper getInstance(Context mContext) {
        if (null == _instance)
            _instance = new PrinterHelper(mContext);
        return _instance;
    }



    synchronized public void printPurchaseBillModelTwo(
            IZKCService mIzkcService, TicketInfo ticketInfo) {

        try {
            if (mIzkcService != null && mIzkcService.checkPrinterAvailable()) {
                mIzkcService.printGBKText("\n\n");
                if (ticketInfo.getStart_bitmap() != null) {
                    mIzkcService.printBitmap(ticketInfo.getStart_bitmap());
                }
                SystemClock.sleep(50);
//				mIzkcService.printGBKText("\n");
                mIzkcService.printGBKText("中惠旅"+ "\n\n");
                mIzkcService.printGBKText(mIzkcService_CUT_OFF_RULE + "\n\n");

                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.SERIAL_NUMBER_TAG + "\t" + ticketInfo.getOrderId() + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_NAME_TAG + "\t" + ticketInfo.getOrderName() + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_UNIT_PRICE_TAG + "\t" + ticketInfo.getPrice() + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_AMOUNT_TAG + "\t" + ticketInfo.getpNum() + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_CONTAINS_TYPE + "\t" + ticketInfo.getItem() + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.USE_TIME + "\t" + ticketInfo.getOrderTime() + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.PRINTER_TIME + "\t" + ticketInfo.getpTime() + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_CONTAINS_TYPE + "\t\n");
                mIzkcService.printGBKText("\n");
                mIzkcService.printGBKText(mIzkcService_CUT_OFF_RULE);
                // if(mIzkcService.getBufferState(100)){
                if (ticketInfo.getEnd_bitmap()!= null) {
                    SystemClock.sleep(200);
//					mIzkcService.printBitmap(bill.end_bitmap);
                    mIzkcService.printBitmap(ticketInfo.getEnd_bitmap());

                }
                // }
                mIzkcService.printGBKText("\n\n\n");
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



}
