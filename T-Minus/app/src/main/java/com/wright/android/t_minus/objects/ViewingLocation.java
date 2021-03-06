package com.wright.android.t_minus.objects;

public class ViewingLocation {
    private final double latitude;
    private final double longitude;
    private final String name;
    private String id;

    public ViewingLocation(String id, String name, double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
