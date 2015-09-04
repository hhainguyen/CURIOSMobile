package com.mapbox.mapboxsdk.android.testapp;

/**
 * Created by hainguyen on 21/06/2014.
 */
public class Place {
    private String ID;
    private String title;
    private String desc;
    private String type;
    private double lat;
    private double lng;
    private int suggested;


    public Place(String ID, String title, String desc, String type, double lat, double lng, int suggested) {
        this.ID = ID;
        this.title = title;
        this.lat = lat;
        this.lng = lng;
        this.desc = desc;
        this.type = type;
        this.suggested =suggested;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int isSuggested() {
        return suggested;
    }

    public void setSuggested(int suggested) {
        this.suggested = suggested;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
