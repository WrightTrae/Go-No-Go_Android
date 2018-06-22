package com.wright.android.t_minus.Objects;

import java.io.Serializable;
import java.util.ArrayList;

// Trae Wright
// JAV2 - Term Number
// Java File Name
public class PadLocation implements Serializable{
    private final int id;
    private final String name;
    private final String countryCode;
    private ArrayList<LaunchPad> launchPads;

    public PadLocation(int id, String name, String countryCode) {
        this.id = id;
        this.name = name;
        this.countryCode = countryCode;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<LaunchPad> getLaunchPads() {
        return launchPads;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setLaunchPads(ArrayList<LaunchPad> launchPads) {
        this.launchPads = launchPads;
    }
}
