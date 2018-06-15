package com.eb.sc.priter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.eb.sc.R;
import com.eb.sc.bean.TicketInfo;
import com.eb.sc.bean.TotalInfo;
import com.eb.sc.utils.AidlUtil;
import com.eb.sc.utils.Utils;
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
                mIzkcService.printGBKText(mIzkcService_CUT_OFF_RULE + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.SERIAL_NUMBER_TAG + "\t" + ticketInfo.getOrderId() + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_NAME_TAG + "\t" + ticketInfo.getOrderName() + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_UNIT_PRICE_TAG + "\t" + ticketInfo.getPrice() + "\n");
                if(!TextUtils.isEmpty(ticketInfo.getpNum())){
                    mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_AMOUNT_TAG + "\t" + ticketInfo.getpNum() + "\n");
                }
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_CONTAINS_TYPE + "\t" + ticketInfo.getItem() + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.USE_TIME + "\t" + ticketInfo.getOrderTime() + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.PRINTER_TIME + "\t" + ticketInfo.getpTime() + "\n");
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

    synchronized public void printhexiao(
            IZKCService mIzkcService, TicketInfo ticketInfo) {

        try {
            if (mIzkcService != null && mIzkcService.checkPrinterAvailable()) {
                mIzkcService.printGBKText("\n\n");
                if (ticketInfo.getStart_bitmap() != null) {
                    mIzkcService.printBitmap(ticketInfo.getStart_bitmap());
                }
                SystemClock.sleep(50);
//				mIzkcService.printGBKText("\n");
                mIzkcService.printGBKText("中惠旅(核销)"+ "\n\n");
                mIzkcService.printGBKText(mIzkcService_CUT_OFF_RULE + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.SERIAL_NUMBER_TAG + "\t" + ticketInfo.getOrderId() + "\n");

                if(!TextUtils.isEmpty(ticketInfo.getOrderName())){
                    mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_NAME_TAG + "\t" + ticketInfo.getOrderName() + "\n");
                }
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_UNIT_PRICE_TAG + "\t" + ticketInfo.getPrice() + "\n");
                if(!TextUtils.isEmpty(ticketInfo.getpNum())){
                    mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_AMOUNT_TAG + "\t" + ticketInfo.getpNum() + "\n");
                }
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_CONTAINS_TYPE + "\t" + ticketInfo.getItem() + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.USE_TIME + "\t" + ticketInfo.getOrderTime() + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.PRINTER_TIME + "\t" + ticketInfo.getpTime() + "\n");
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



    synchronized public void printTotal(
            IZKCService mIzkcService, TotalInfo ticketInfo) {

        try {
            if (mIzkcService != null && mIzkcService.checkPrinterAvailable()) {
                mIzkcService.printGBKText("\n\n");
                if (ticketInfo.getStart_bit() != null) {
                    mIzkcService.printBitmap(ticketInfo.getStart_bit());
                }
                SystemClock.sleep(50);
//				mIzkcService.printGBKText("\n");
                mIzkcService.printGBKText("中惠旅(售票统计)"+ "\n\n");
                mIzkcService.printGBKText(mIzkcService_CUT_OFF_RULE + "\n");
                mIzkcService.printGBKText("售票点"+ "\t" + Utils.getXiangmu(mContext) + "\n");
                mIzkcService.printGBKText("现金收款"+ "\t" + ticketInfo.getCash_price() + "\n");
                mIzkcService.printGBKText("现金人数" + "\t" + ticketInfo.getCash_num() + "\n");
                mIzkcService.printGBKText("微信收款" + "\t" + ticketInfo.getWeichat_price() + "\n");
                mIzkcService.printGBKText("售票人数"+ "\t" + ticketInfo.getWeichat_num() + "\n");
                mIzkcService.printGBKText("支付宝收款"+ "\t" + ticketInfo.getAli_price() + "\n");
                mIzkcService.printGBKText("售票人数"+ "\t" + ticketInfo.getAli_num() + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.PRINTER_TIME + "\t" + ticketInfo.getPrint_time() + "\n");
                // if(mIzkcService.getBufferState(100)){
                // }
                mIzkcService.printGBKText("\n\n\n");
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    //商米打印
    synchronized  public void sunmiprit(TicketInfo ticketInfo,String sttrs){
        if (ticketInfo.getStart_bitmap() != null) {
            AidlUtil.getInstance().printBitmap(ticketInfo.getStart_bitmap());
        }
        SystemClock.sleep(200);
        AidlUtil.getInstance().printText("中惠旅",13,false,false);
        AidlUtil.getInstance().print1Line();
        AidlUtil.getInstance().printText("--------------------------------",13,false,false);
        AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.SERIAL_NUMBER_TAG + ticketInfo.getOrderId(),13,false,false);

            AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.GOODS_NAME_TAG  + ticketInfo.getOrderName(),13,false,false);

        AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.GOODS_UNIT_PRICE_TAG  + ticketInfo.getPrice(),13,false,false);
        if(!TextUtils.isEmpty(ticketInfo.getpNum())){
            AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.GOODS_AMOUNT_TAG  + ticketInfo.getpNum(),13,false,false);
        }
        AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.GOODS_CONTAINS_TYPE + ticketInfo.getItem(),13,false,false);
        AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.USE_TIME + "\t" + ticketInfo.getOrderTime(),13,false,false);
        AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.PRINTER_TIME + "\t" + ticketInfo.getpTime(),13,false,false);
        AidlUtil.getInstance().print1Line();
        AidlUtil.getInstance().printText("--------------------------------",13,false,false);
        AidlUtil.getInstance().print1Line();
        // if(mIzkcService.getBufferState(100)){
        if (!TextUtils.isEmpty(sttrs)) {
            SystemClock.sleep(200);
            AidlUtil.getInstance().printQr(sttrs, 5, 3);
            Log.i("cccc","sss="+5);
        }
        AidlUtil.getInstance().print3Line();
    }

    synchronized public void printSunmihexiao(TicketInfo ticketInfo) {

        try {
            if (ticketInfo.getStart_bitmap() != null) {
                AidlUtil.getInstance().printBitmap(ticketInfo.getStart_bitmap());
               }
                SystemClock.sleep(50);
//				mIzkcService.printGBKText("\n");
                AidlUtil.getInstance().printText("中惠旅(核销)",13,false,false);
                AidlUtil.getInstance().print1Line();
                AidlUtil.getInstance().printText("--------------------------------",13,false,false);
                AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.SERIAL_NUMBER_TAG + ticketInfo.getOrderId(),13,false,false);
                if(!TextUtils.isEmpty(ticketInfo.getOrderName())){
                    AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.GOODS_NAME_TAG  + ticketInfo.getOrderName() ,13,false,false);
                }else {
                    AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.GOODS_NAME_TAG  + ticketInfo.getItem(),13,false,false);
                }
                AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.GOODS_UNIT_PRICE_TAG  + ticketInfo.getPrice(),13,false,false);
                if(!TextUtils.isEmpty(ticketInfo.getpNum())){
                    AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.GOODS_AMOUNT_TAG  + ticketInfo.getpNum(),13,false,false);
                }
                AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.GOODS_CONTAINS_TYPE + ticketInfo.getItem(),13,false,false);
                AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.USE_TIME + "\t" + ticketInfo.getOrderTime(),13,false,false);
                AidlUtil.getInstance().printText(PrintTicketTag.PurchaseTag.PRINTER_TIME + "\t" + ticketInfo.getpTime(),13,false,false);
                AidlUtil.getInstance().print1Line();
                AidlUtil.getInstance().printText("--------------------------------",13,false,false);
                AidlUtil.getInstance().print3Line();
                // if(mIzkcService.getBufferState(100)){
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    synchronized public void printSunmiTotal(TotalInfo ticketInfo) {
        try {
                if (ticketInfo.getStart_bit() != null) {
                    AidlUtil.getInstance().printBitmap(ticketInfo.getStart_bit());
                }
                SystemClock.sleep(50);
            AidlUtil.getInstance().printText("中惠旅(售票统计)",13,false,false);
            AidlUtil.getInstance().print1Line();
//				mIzkcService.printGBKText("\n");
            AidlUtil.getInstance().printText("--------------------------------",13,false,false);
            AidlUtil.getInstance().printText("售票点" + Utils.getXiangmu(mContext) ,13,false,false);
            AidlUtil.getInstance().printText("现金收款"+ticketInfo.getCash_price() ,13,false,false);
            AidlUtil.getInstance().printText("现金人数" + ticketInfo.getCash_num(),13,false,false);
            AidlUtil.getInstance().printText("微信收款" + ticketInfo.getWeichat_price(),13,false,false);
            AidlUtil.getInstance().printText("售票人数" + ticketInfo.getWeichat_num(),13,false,false);
            AidlUtil.getInstance().printText("支付宝收款"+ ticketInfo.getAli_price(),13,false,false);
            AidlUtil.getInstance().printText("售票人数"+ ticketInfo.getAli_num(),13,false,false);
            AidlUtil.getInstance().printText("--------------------------------",13,false,false);
            AidlUtil.getInstance().print3Line();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



}
