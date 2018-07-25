package com.wright.android.t_minus.objects;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

// Trae Wright
// JAV2 - Term Number
// Java File Name
public class Manifest implements Serializable {
    private final int launchId;
    private final String title;
    private final String time;
    private final String imageUrl;
    private final String agencyName;
    private String agencyURL;
    private final PadLocation padLocation;
    private ManifestDetails manifestDetails;

    public Manifest(int launchId, String title, String time, String imageUrl, String agencyName, String agencyURL, PadLocation padLocation) {
        this.launchId = launchId;
        this.title = title;
        this.time = time;
        this.imageUrl = imageUrl;
        this.padLocation = padLocation;
        this.agencyURL = agencyURL;
        this.agencyName = agencyName;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public String getAgencyURL() {
//        StringBuilder stringBuilder = new StringBuilder(agencyURL);
//        if(agencyURL == null || agencyURL.trim().isEmpty() || stringBuilder.charAt(0) != 'h'){
//            return "InvalidUrl";
//        }
//        int backSlashIndex = stringBuilder.indexOf("/",8);
//        stringBuilder.delete(backSlashIndex, stringBuilder.length()-1);
        return agencyURL;
    }

    public void setAgencyURL(String agencyURL) {
        this.agencyURL = agencyURL;
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

    public Date getTimeDate(){
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm a",Locale.getDefault());
        df.setTimeZone(TimeZone.getDefault());
        try {
            return df.parse(time);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
