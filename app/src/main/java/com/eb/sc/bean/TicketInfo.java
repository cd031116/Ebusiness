package com.eb.sc.bean;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/31.
 */

public class TicketInfo implements Serializable{

    private String orderId;//单号

    private  String orderName;//名称

    private String price;//价格

    private String pNum;//人数

    private  String orderTime;// 日期

    private String pTime;//打印时间

    private String  item;//项目

    private Bitmap start_bitmap;

    private Bitmap end_bitmap;//二维码内容

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getpNum() {
        return pNum;
    }

    public void setpNum(String pNum) {
        this.pNum = pNum;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Bitmap getStart_bitmap() {
        return start_bitmap;
    }

    public void setStart_bitmap(Bitmap start_bitmap) {
        this.start_bitmap = start_bitmap;
    }

    public Bitmap getEnd_bitmap() {
        return end_bitmap;
    }

    public void setEnd_bitmap(Bitmap end_bitmap) {
        this.end_bitmap = end_bitmap;
    }
}
