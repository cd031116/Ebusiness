package com.eb.sc.sdk.eventbus;

/**
 * Created by Administrator on 2017/9/2.
 */

public class GetOrderEvent {
    private String order_id;

    public String getOrder_id() {
        return order_id;
    }

    public  GetOrderEvent(String order_id) {
        this.order_id = order_id;
    }
}
