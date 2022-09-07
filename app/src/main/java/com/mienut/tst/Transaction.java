package com.mienut.tst;

import java.util.Date;

public class Transaction {
private String product_id;
private Date timestamp;
private String name;
private String user;
private String phone;
private long amount;
private String quantity;
private String uid;
private String status;
private String type;
private String userid;
private String puid;
private String imageuri;
private String orderid;
private String payment;
private String otp;
private String rating;
private String ordertype;
    public Transaction() {
    }

    public Transaction(String product_id, Date timestamp, String name, String user, String phone, long amount, String quantity,String uid ,String status, String type, String userid, String puid, String imageuri, String orderid, String payment, String otp, String rating, String ordertype) {
        this.product_id = product_id;
        this.timestamp = timestamp;
        this.name = name;
        this.phone = phone;
        this.user = user;
        this.amount = amount;
        this.quantity = quantity;
        this.uid = uid;
        this.status = status;
        this.type = type;
        this.userid = userid;
        this.puid = puid;
        this.imageuri = imageuri;
        this.orderid = orderid;
        this.payment = payment;
        this.otp = otp;
        this.rating = rating;
        this.ordertype = ordertype;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }
}
