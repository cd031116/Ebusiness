package com.eb.sc.bean;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/31.
 */

public class TicketInfo implements Serializable{

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

    public ArrayList<String> orderType;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public String getTicketNum() {
        return ticketNum;
    }

    public void setTicketNum(String ticketNum) {
        this.ticketNum = ticketNum;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderPeople() {
        return orderPeople;
    }

    public void setOrderPeople(String orderPeople) {
        this.orderPeople = orderPeople;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getTicketOrderNum() {
        return ticketOrderNum;
    }

    public void setTicketOrderNum(String ticketOrderNum) {
        this.ticketOrderNum = ticketOrderNum;
    }

    public String getPrintTime() {
        return printTime;
    }

    public void setPrintTime(String printTime) {
        this.printTime = printTime;
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

    public ArrayList<String> getOrderType() {
        return orderType;
    }

    public void setOrderType(ArrayList<String> orderType) {
        this.orderType = orderType;
    }
}
