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
            IZKCService mIzkcService, TicketInfo ticketInfo, int imageType) {

        try {
            if (mIzkcService != null && mIzkcService.checkPrinterAvailable()) {
                mIzkcService.printGBKText("\n\n");
                if (ticketInfo.start_bitmap != null) {
//					mIzkcService.printBitmapAlgin(bill.start_bitmap, 376, 120, 1);
                    switch (imageType) {
                        case 0:
                            mIzkcService.printBitmap(ticketInfo.start_bitmap);
                            break;
                        case 1:
                            mIzkcService.printImageGray(ticketInfo.start_bitmap);
                            break;
                        case 2:
                            mIzkcService.printRasterImage(ticketInfo.start_bitmap);
                            break;
                    }
                }
                SystemClock.sleep(50);
//				mIzkcService.printGBKText("\n");
                mIzkcService.printGBKText(ticketInfo.orderNum + "\n\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_PRICE_TAG
                        + ticketInfo.ticketCode + "\t\t\n" +
                        PrintTicketTag.PurchaseTag.TOTAL_PRICE_TAG + ticketInfo.ticketNum
                        + "\n");

                mIzkcService.printGBKText(mIzkcService_CUT_OFF_RULE + "\n\n");

                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.SERIAL_NUMBER_TAG + "\t" + ticketInfo.orderNum + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_NAME_TAG + "\t" + ticketInfo.orderName + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_UNIT_PRICE_TAG + "\t" + ticketInfo.orderPrice + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_AMOUNT_TAG + "\t" + ticketInfo.orderPeople + "\n");
                mIzkcService.printGBKText(PrintTicketTag.PurchaseTag.GOODS_CONTAINS_TYPE + "\t\n");
                for (int i = 0; i < ticketInfo.orderType.size(); i++) {
                    String space0 = "";

                    String name = ticketInfo.orderType.get(i);

                    int name_length = name.length();

                    int space_length0 = 5 - name_length;

                    String name1 = "";
                    String name2 = "";


                    if (name_length > 5) {
                        name1 = name.substring(0, 5);
                        name2 = name.substring(5, name_length);

                        mIzkcService
                                .printGBKText(name1 + "  ");

//                            mIzkcService.printGBKText(name2);
                    } else {
                        for (int j = 0; j < space_length0; j++) {
                            space0 += "  ";
                        }
                        mIzkcService.printGBKText(name + space0);
                    }
                    if ((i + 1) % 3 == 0) {
                        mIzkcService.printGBKText("\n");
                    }
                }
                mIzkcService.printGBKText("\n");

                mIzkcService.printGBKText(mIzkcService_CUT_OFF_RULE);

                // if(mIzkcService.getBufferState(100)){
                if (ticketInfo.end_bitmap != null) {
                    SystemClock.sleep(200);
//					mIzkcService.printBitmap(bill.end_bitmap);
                    switch (imageType) {
                        case 0:
                            mIzkcService.printBitmap(ticketInfo.end_bitmap);
                            break;
                        case 1:
                            mIzkcService.printImageGray(ticketInfo.end_bitmap);
                            break;
                        case 2:
                            mIzkcService.printRasterImage(ticketInfo.end_bitmap);
                            break;
                    }
                }
                // }
                mIzkcService.printGBKText("\n\n\n");
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public TicketInfo getTicket(IZKCService mIzkcService,
                                boolean display_start_pic, boolean display_end_pic) {
        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.ticketCode = "14444444444444444444";
        ticketInfo.ticketNum = "0111111111100";
        ticketInfo.orderNum = "21012201021210";
        ticketInfo.orderName = "驴妈妈门票98元";
        ticketInfo.orderPrice = "￥98.00";
        ticketInfo.orderPeople = "2ren";
        ticketInfo.ticketOrderNum = "dnasaskhdjk";
        ticketInfo.orderTime = "2017-09-09";
        ticketInfo.orderType.add("大门票");
        ticketInfo.orderType.add("大门票");
        ticketInfo.orderType.add("旋转木");
        ticketInfo.orderType.add("9d电影");
        ticketInfo.orderType.add("大门票");
        ticketInfo.orderType.add("旋转木");
        ticketInfo.orderType.add("大门票");
        generalticketBitmap(mIzkcService, ticketInfo, display_start_pic, display_end_pic);
        return ticketInfo;
    }

    private void generalticketBitmap(IZKCService mIzkcService, TicketInfo ticketInfo,
                                     boolean display_start_pic, boolean display_end_pic) {

        if (display_start_pic) {
            Bitmap mBitmap = null;
//			try {
//				mBitmap = mIzkcService.createBarCode("4333333367", 1, 384, 120, false);
            mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.logo);
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
            ticketInfo.start_bitmap = mBitmap;
        }
        if (display_end_pic) {
            Bitmap btMap;
            try {
                btMap = mIzkcService.createQRCode("扫描关注本店，有惊喜喔", 240, 240);
                ticketInfo.end_bitmap = btMap;
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
