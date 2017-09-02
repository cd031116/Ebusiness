package com.eb.sc.bean;

import java.io.Serializable;

/**
 * Created by lyj on 2017/8/7.
 */

public class ItemInfo implements Serializable{
    private String Id;
    private String Name;
    private String Code;
    private String Num;
    private String Price;
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name){
        Name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getNum() {
        return Num;
    }

    public void setNum(String num) {
        Num = num;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
