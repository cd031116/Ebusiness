package com.eb.sc.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/2.
 */

public class TickBean implements Serializable{
    private String nmae;
    private String id_tick;
    private String price;
    private int pNum;

    public String getNmae() {
        return nmae;
    }

    public void setNmae(String nmae) {
        this.nmae = nmae;
    }

    public String getId_tick() {
        return id_tick;
    }

    public void setId_tick(String id_tick) {
        this.id_tick = id_tick;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getpNum() {
        return pNum;
    }

    public void setpNum(int pNum) {
        this.pNum = pNum;
    }
}
