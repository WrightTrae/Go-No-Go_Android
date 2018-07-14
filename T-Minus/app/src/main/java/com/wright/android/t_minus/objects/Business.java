package com.wright.android.t_minus.objects;

import java.io.Serializable;

public class Business implements Serializable{
    private boolean isVerified;
    private String name;
    private String number;
    private double latitude;
    private double longitude;
    private String description;
    private String address;


    public Business(boolean isVerified, String name, String number, double latitude, double longitude, String address, String description) {
        this.isVerified = isVerified;
        this.name = name;
        this.number = number;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }
}
