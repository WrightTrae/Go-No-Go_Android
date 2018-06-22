package com.wright.android.t_minus.Objects;

import java.io.Serializable;

// Trae Wright
// JAV2 - Term Number
// Java File Name
public class Manifest implements Serializable {
    private final String title;
    private final String time;
    private final String imageUrl;
    private final PadLocation padLocation;
    private final String status;
    private final String probability;
    private final String windowStart;
    private final String windowEnd;
    private final String missionProvider;
    private final String url;

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
