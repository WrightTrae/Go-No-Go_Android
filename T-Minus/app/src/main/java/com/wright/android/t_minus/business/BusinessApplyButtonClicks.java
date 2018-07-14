package com.wright.android.t_minus.business;

public interface BusinessApplyButtonClicks {
    void userInfoNextClick(String name, String number, String email);
    void businessInfoSubmitClick(String name, String number, String address);
    void conformationDoneClick();
}