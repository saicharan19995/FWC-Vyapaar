package com.mienut.tst;

public class Popular {
    private String prod_name;
    private  String prod_price;
    private String type;
    private String usrid;
    private double longitude, latitude, miles;
    private long quantity;
    private String milk_type="mlk";
    private String brand="mlk";
    private String mobility;
    private String pid;
    private String imageuri;
    private String discription;
    private String unit;
    private String vendor;
    private float stars;
    private int people;


    public Popular() {
    }

    public Popular(String prod_name, String prod_price, String type, String usrid, double latitude, double longitude, long quantity, String milk_type, String brand, String mobility, String pid, String imageuri, String discription, String unit, double miles, String vendor, float stars, int people ) {
        this.prod_name = prod_name;
        this.prod_price = prod_price;
        this.type = type;
        this.usrid = usrid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.quantity = quantity;
        this.milk_type = milk_type;
        this.brand = brand;
        this.mobility = mobility;
        this.pid = pid;
        this.imageuri = imageuri;
        this.discription = discription;
        this.unit = unit;
        this.miles = miles;
        this.vendor = vendor;
        this.stars = stars;
        this.people = people;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getProd_price() {
        return prod_price;
    }

    public void setProd_price(String prod_price) {
        this.prod_price = prod_price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsrid() {
        return usrid;
    }

    public void setUsrid(String usrid) {
        this.usrid = usrid;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getMilk_type() {
        return milk_type;
    }

    public void setMilk_type(String milk_type) {
        this.milk_type = milk_type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getMobility() {
        return mobility;
    }

    public void setMobility(String mobility) {
        this.mobility = mobility;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getMiles() {
        return miles;
    }

    public void setMiles(double miles) {
        this.miles = miles;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }
}
