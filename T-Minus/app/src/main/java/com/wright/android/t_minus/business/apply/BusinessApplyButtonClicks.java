package com.wright.android.t_minus.business.apply;

import com.google.android.gms.maps.model.LatLng;

public interface BusinessApplyButtonClicks {
    void userInfoNextClick(String name, String number, String email);
    void businessInfoSubmitClick(String name, String number, LatLng latLng, String address);
    void conformationDoneClick();
}