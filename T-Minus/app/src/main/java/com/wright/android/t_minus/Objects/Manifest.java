package com.wright.android.t_minus.Objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;

// Trae Wright
// JAV2 - Term Number
// Java File Name
public class Manifest implements Serializable {
    private String title;
    private String time;
    private String imageUrl;
    private PadLocation padLocation;
    private String status;
    private String probability;
    private String windowStart;
    private String windowEnd;
    private String missionProvider;
    private String url;

    public Manifest(String title, String time, String imageUrl, PadLocation padLocation, String status, String probability, String windowStart, String windowEnd, String missionProvider, String url) {
        this.title = title;
        this.time = time;
        this.imageUrl = imageUrl;
        this.padLocation = padLocation;
        this.status = status;
        this.probability = probability;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.missionProvider = missionProvider;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    public String getProbability() {
        return probability;
    }

    public String getWindowStart() {
        return windowStart;
    }

    public String getWindowEnd() {
        return windowEnd;
    }

    public String getMissionProvider() {
        return missionProvider;
    }

    public PadLocation getPadLocation() {
        return padLocation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }
}
