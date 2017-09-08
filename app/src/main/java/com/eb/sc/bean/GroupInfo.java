package com.eb.sc.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lyj on 2017/9/8.
 */

public class GroupInfo implements Serializable{
    private String OrderSn;

    private String UserName;

    private String Phone;

    private List<ChileInfo>  Tickets;

    public String getOrderSn() {
        return OrderSn;
    }

    public void setOrderSn(String orderSn) {
        OrderSn = orderSn;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public List<ChileInfo> getTickets() {
        return Tickets;
    }

    public void setTickets(List<ChileInfo> tickets) {
        Tickets = tickets;
    }
}
