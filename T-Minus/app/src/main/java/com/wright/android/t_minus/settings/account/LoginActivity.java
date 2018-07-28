package com.wright.android.t_minus.settings.account;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import java.util.HashMap;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wright.android.t_minus.R;

public class LoginActivity extends AppCompatActivity implements LoginListener{

    private LoginFragment loginFragment;
    private SignupFragment signupFragment;
//TODO: Add toast messages for signing in/out successfully
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        signupFragment = SignupFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.login_frame, signupFragment).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void OperationSwitch(android.support.v4.app.Fragment fromFragment) {
        if (fromFragment instanceof SignupFragment){
            if(loginFragment == null){
                loginFragment = LoginFragment.newInstance();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.login_frame, loginFragment).commit();
        }else if(fromFragment instanceof LoginFragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.login_frame, signupFragment).commit();
        }
    }

    @Override
    public void SignUpButton(@NonNull FirebaseUser user, @NonNull String _name) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(String.valueOf(user.getUid()));
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", _name);
        userMap.put("email", user.getEmail());
        userMap.put("admin", false);
        userMap.put("admin_in_progress", false);
        userRef.setValue(userMap);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void LogInButton(@NonNull FirebaseUser user) {
        setResult(RESULT_OK);
        finish();
    }
}

