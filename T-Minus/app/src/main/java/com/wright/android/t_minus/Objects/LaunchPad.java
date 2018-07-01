package com.wright.android.t_minus.Objects;

import java.io.Serializable;

// Trae Wright
// JAV2 - Term Number
// Java File Name
public class LaunchPad implements Serializable{
    private final int id;
    private String name;
    private final double latitude;
    private final double longitude;
    private final int locationId;

    public LaunchPad(int id, String name, double latitude, double longitude, int locationId) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationId = locationId;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
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
