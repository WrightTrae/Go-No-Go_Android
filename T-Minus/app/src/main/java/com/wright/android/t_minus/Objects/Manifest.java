package com.wright.android.t_minus.Objects;

import java.io.Serializable;

// Trae Wright
// JAV2 - Term Number
// Java File Name
public class Manifest implements Serializable {
    private final int launchId;
    private final String title;
    private final String time;
    private final String imageUrl;
    private final PadLocation padLocation;
    private ManifestDetails manifestDetails;

    public Manifest(int launchId, String title, String time, String imageUrl, PadLocation padLocation) {
        this.launchId = launchId;
        this.title = title;
        this.time = time;
        this.imageUrl = imageUrl;
        this.padLocation = padLocation;
    }

    public void setManifestDetails(ManifestDetails manifestDetails) {
        this.manifestDetails = manifestDetails;
    }

    public ManifestDetails getManifestDetails() {
        return manifestDetails;
    }

    public int getLaunchId() {
        return launchId;
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
