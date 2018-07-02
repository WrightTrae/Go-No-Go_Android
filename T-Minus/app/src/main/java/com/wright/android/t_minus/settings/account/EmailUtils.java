package com.wright.android.t_minus.settings.account;

public class EmailUtils {
    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean isPasswordInvalid(String password) {
        //TODO: Replace this with your own logic
        return password.length() <= 5;
    }
}
