package com.eb.sc.bean;/**
 * Created by lyj on 2017/7/28.
 */

import org.aisen.android.component.orm.annotation.PrimaryKey;

import java.io.Serializable;

/**
 * created by lyj at 2017-7-28
 */

public class DataInfo implements Serializable {
    @PrimaryKey(column = "id")
    private String id; //身份证或者二维码

    private String name;//地址名称--票类型

    private boolean isUp;//是否上传

    private boolean isNet;//是否无线

    private int type;//类型,身份证（1）或二维码（2）

    private String code;//服务器给设备的编号

    private String validTime;//有效时间

    private  String insertTime;//存入时间

    private String pName;//

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUp() {
        return isUp;
    }

    public void setUp(boolean up) {
        isUp = up;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public boolean isNet() {
        return isNet;
    }

    public void setNet(boolean net) {
        isNet = net;
    }
}
