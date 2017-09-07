package com.eb.sc.bean;

import org.aisen.android.component.orm.annotation.PrimaryKey;

import java.io.Serializable;

/**
 * Created by lyj on 2017/9/6.
 */

public class SaleBean implements Serializable {
    @PrimaryKey(column = "id")
    private String orderId; //订单号

    private String name;//名称

    private int pNum;//人数

    private String item; //包含项目

    private String print_time;

    private String  price;

    private int  state;//0-现金   3--微信  2- 支付宝

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getpNum() {
        return pNum;
    }

    public void setpNum(int pNum) {
        this.pNum = pNum;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getPrint_time() {
        return print_time;
    }

    public void setPrint_time(String print_time) {
        this.print_time = print_time;
    }
}
