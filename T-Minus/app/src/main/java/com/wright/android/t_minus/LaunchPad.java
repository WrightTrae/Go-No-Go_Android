package com.wright.android.t_minus;

import android.location.Location;

import com.wright.android.t_minus.Objects.Agency;

// Trae Wright
// JAV2 - Term Number
// Java File Name
public class LaunchPad {
    private String name;
    private Location longLat;
    private Agency[] agencies;

    public LaunchPad(String name, Location longLat, Agency[] agencies) {
        this.name = name;
        this.longLat = longLat;
        this.agencies = agencies;
    }

    public String getName() {
        return name;
    }

    public Location getLongLat() {
        return longLat;
    }

    public Agency[] getAgencies() {
        return agencies;
    }
}
