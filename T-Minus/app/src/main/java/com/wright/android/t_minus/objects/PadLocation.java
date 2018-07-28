package com.wright.android.t_minus.objects;

import java.io.Serializable;
import java.util.ArrayList;

// Trae Wright
// JAV2 - Term Number
// Java File Name
public class PadLocation implements Serializable{
    private final int id;
    private String name;
    private final ArrayList<LaunchPad> launchPads;

    public PadLocation(int id, String name, ArrayList<LaunchPad> launchPads) {
        this.id = id;
        this.name = name;
        this.launchPads = launchPads;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<LaunchPad> getLaunchPads() {
        return launchPads;
    }

    public void addLaunchPads(LaunchPad launchPad) {
        this.launchPads.add(launchPad);
    }
}
