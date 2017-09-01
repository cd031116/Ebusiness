package com.eb.sc.bean;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/31.
 */

public class TicketInfo {

    public  String address;
    public String ticketCode;
    public  String ticketNum;
    public   String orderNum;
    public   String orderName;
    public   String orderPrice;
    public  String orderPeople;
    public   String orderTime;
    public String ticketOrderNum;
    public String printTime;
    public Bitmap start_bitmap;
    public Bitmap end_bitmap;

    public ArrayList<String> orderType = new ArrayList<>();

}
