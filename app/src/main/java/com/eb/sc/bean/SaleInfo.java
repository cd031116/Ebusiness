package com.eb.sc.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/26/026.
 */

public class SaleInfo implements Serializable {

    private int Id ;
    private String Name ;
    private String Price ;
    private String PrintPrice ;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getPrintPrice() {
        return PrintPrice;
    }

    public void setPrintPrice(String printPrice) {
        PrintPrice = printPrice;
    }
}
