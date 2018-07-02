package com.wright.android.t_minus.objects;

import java.io.Serializable;

public class ManifestDetails implements Serializable {
    private String status;
    private String probability;
    private String windowStart;
    private String windowEnd;
    private String missionProvider;
    private String url;
    private String type;
    private String description;

    public ManifestDetails(String status, String probability, String windowStart, String windowEnd, String missionProvider, String url, String type, String description) {
        this.status = status;
        this.probability = probability;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.missionProvider = missionProvider;
        this.url = url;
        this.type = type;
        this.description = description;
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

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
