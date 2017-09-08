package com.eb.sc.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/8.
 */

public class TotalInfo implements Serializable{
    private String  cash_price;
    private String cash_num;
    private String weichat_price;
    private String weichat_num;
    private String ali_price;
    private String ali_num;
    private  String print_time;
    private Bitmap  start_bit;

    public Bitmap getStart_bit() {
        return start_bit;
    }

    public void setStart_bit(Bitmap start_bit) {
        this.start_bit = start_bit;
    }

    public String getPrint_time() {
        return print_time;
    }

    public void setPrint_time(String print_time) {
        this.print_time = print_time;
    }

    public String getCash_price() {
        return cash_price;
    }

    public void setCash_price(String cash_price) {
        this.cash_price = cash_price;
    }

    public String getCash_num() {
        return cash_num;
    }

    public void setCash_num(String cash_num) {
        this.cash_num = cash_num;
    }

    public String getWeichat_price() {
        return weichat_price;
    }

    public void setWeichat_price(String weichat_price) {
        this.weichat_price = weichat_price;
    }

    public String getWeichat_num() {
        return weichat_num;
    }

    public void setWeichat_num(String weichat_num) {
        this.weichat_num = weichat_num;
    }

    public String getAli_price() {
        return ali_price;
    }

    public void setAli_price(String ali_price) {
        this.ali_price = ali_price;
    }

    public String getAli_num() {
        return ali_num;
    }

    public void setAli_num(String ali_num) {
        this.ali_num = ali_num;
    }
}
