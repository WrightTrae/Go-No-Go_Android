package com.wright.android.t_minus.Settings.Account;

import android.app.Fragment;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

public interface LoginListener {
    void OperationSwitch(android.support.v4.app.Fragment fromFragment);
    void SignUpButton(@NonNull FirebaseUser user, @NonNull String _name);
    void LogInButton(@NonNull FirebaseUser user);
}
