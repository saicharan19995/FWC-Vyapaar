package com.mienut.tst;

import com.squareup.picasso.RequestCreator;

public class Cat {

    private String Title;
    private String ImageUrl;
    private int Thumbnail ;

    public Cat(){
    }

    public Cat(String title, int thumbnail) {
        Title = title;
        Thumbnail = thumbnail;
    }

    public Cat(String title, String imageUrl) {
        Title = title;
        ImageUrl = imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public String getTitle() {
        return Title;
    }


    public int getThumbnail() {
        return Thumbnail;
    }


    public void setTitle(String title) {
        Title = title;
    }


    public void setThumbnail(int thumbnail) {
        Thumbnail = thumbnail;
    }
}
