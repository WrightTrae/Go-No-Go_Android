package com.wright.android.t_minus.Objects;

import android.location.Location;

import java.io.Serializable;

// Trae Wright
// JAV2 - Term Number
// Java File Name
public class LaunchPad implements Serializable{
    private String name;
    private double latitude;
    private double longitude;
    private int locationId;

    public LaunchPad(String name, double latitude, double longitude, int locationId) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationId = locationId;
    }

    public int getLocationId() {
        return locationId;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
