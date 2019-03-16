package com.liurui.project.liuruiapplication;

import java.io.Serializable;

/**
 * Created by jszx on 2018/12/11.
 */

public class Friend implements Serializable{
    private String name;
    private String number;
    private double latitude;
    private double longitude;
    private double altitude;
    private double accuracy;
    private String nearest_address;
    private double time1;
    private double time2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public String getNearest_address() {
        return nearest_address;
    }

    public void setNearest_address(String nearest_address) {
        this.nearest_address = nearest_address;
    }

    public double getSeconds_since_last_update() {
        return time1;
    }

    public void setSeconds_since_last_update(double seconds_since_last_update) {
        this.time1 = seconds_since_last_update;
    }

    public double getSeconds_since_next_update() {
        return time2;
    }

    public void setSeconds_since_next_update(double seconds_since_next_update) {
        this.time2 = seconds_since_next_update;
    }

}
