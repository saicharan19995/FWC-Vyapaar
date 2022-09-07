package com.mienut.tst;

public class address_retriver {

    private String City, locality, flat, pincode, state, landmark, name, phone, alphone, upiid, upiname;
    private double longitutde, latitute;

    public address_retriver() {
    }

    public address_retriver(String City, String locality, String flat, String pincode, String state, String landmark, String name, String phone, String alphone, double longitutde, double latitute, String upiid, String upiname) {
        this.City = City;
        this.locality = locality;
        this.flat = flat;
        this.pincode = pincode;
        this.state = state;
        this.landmark = landmark;
        this.name = name;
        this.phone = phone;
        this.alphone = alphone;
        this.longitutde = longitutde;
        this.latitute = latitute;
        this.upiid = upiid;
        this.upiname = upiname;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAlphone() {
        return alphone;
    }

    public void setAlphone(String alphone) {
        this.alphone = alphone;
    }

    public double getLongitutde() {
        return longitutde;
    }

    public void setLongitutde(double longitutde) {
        this.longitutde = longitutde;
    }

    public double getLatitute() {
        return latitute;
    }

    public void setLatitute(double latitute) {
        this.latitute = latitute;
    }

    public String getUpiid() {
        return upiid;
    }

    public void setUpiid(String upiid) {
        this.upiid = upiid;
    }

    public String getUpiname() {
        return upiname;
    }

    public void setUpiname(String upiname) {
        this.upiname = upiname;
    }
}
