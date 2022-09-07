package com.mienut.tst;

import java.sql.Timestamp;
import java.util.Date;

public class OrderByTIme {
    private String orderid;
    private Date time;

    public OrderByTIme() {
    }

    public OrderByTIme(String orderid, Date time) {
        this.orderid = orderid;
        this.time = time;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
