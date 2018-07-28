package com.wright.android.t_minus.objects;

import java.io.Serializable;
import java.util.HashMap;

public class Business implements Serializable{
    private final String id;
    private boolean isVerified;
    private String name;
    private String number;
    private double latitude;
    private double longitude;
    private String address;
    private String description;

    public Business(String id, boolean isVerified, String name, String number, double latitude, double longitude, String address, String description) {
        this.id = id;
        this.isVerified = isVerified;
        this.name = name;
        this.number = number;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.description = description;
    }

    public HashMap<String, Object> getDetailsMap(){
        HashMap<String, Object> businessDetailMap = new HashMap<>();
        businessDetailMap.put("name", name);
        businessDetailMap.put("longitude", longitude);
        businessDetailMap.put("latitude", latitude);
        businessDetailMap.put("address", address);
        businessDetailMap.put("number", number);
        businessDetailMap.put("description", description);
        businessDetailMap.put("hours_of_operation", null);
        return businessDetailMap;
    }

    public String getId() {
        return id;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(String address) {
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
