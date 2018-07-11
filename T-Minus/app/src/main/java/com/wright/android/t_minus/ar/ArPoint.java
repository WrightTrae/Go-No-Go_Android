package com.wright.android.t_minus.ar;

import android.location.Location;


public class ArPoint {
    Location location;
    String name;

    public ArPoint(String name, double lat, double lon, double altitude) {
        this.name = name;
        location = new Location("ArPoint");
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setAltitude(altitude);
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
}
