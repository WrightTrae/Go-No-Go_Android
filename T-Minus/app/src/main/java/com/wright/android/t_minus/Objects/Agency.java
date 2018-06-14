package com.wright.android.t_minus.Objects;

// Trae Wright
// JAV2 - Term Number
// Java File Name
public class Agency {
    private int id;
    private String name;
    private String countryCode;

    public Agency(int id, String name, String countryCode) {
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

    public String getCountryCode() {
        return countryCode;
    }
}
