package com.wright.android.t_minus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

// Trae Wright
// JAV2 - Term Number
// Java File Name
public class Manifest {
    private String title;
    private String time;
    private String location;
    private Bitmap image;

    public Manifest(String title, String time, String location, Bitmap image) {
        this.title = title;
        this.time = time;
        this.location = location;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
