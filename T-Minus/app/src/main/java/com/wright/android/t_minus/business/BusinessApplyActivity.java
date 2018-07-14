package com.wright.android.t_minus.business;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.settings.PreferencesActivity;

import java.util.HashMap;

public class BusinessApplyActivity extends AppCompatActivity implements BusinessApplyButtonClicks {

    private BusinessApplyFragmentBusinessInfo businessInfoFragment;
    private BusinessApplyFragmentConformation conformationFragment;
    private final static String BUSINESS_FRAGMENT_TAG = "BUSINESS_FRAGMENT_TAG";
    private final static String CONFIRM_FRAGMENT_TAG = "CONFIRM_FRAGMENT_TAG";

    private String user_name;
    private String user_phone;
    private String user_email;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        getSupportFragmentManager().beginTransaction().add(R.id.blankFrame,
                BusinessApplyFragmentUserInfo.newInstance()).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        boolean pop = getSupportFragmentManager().popBackStackImmediate();
        if(!pop){
            onBackPressed();
        }
        return true;
    }

    @Override
    public void userInfoNextClick(String name, String number, String email) {
        if(businessInfoFragment == null) {
            businessInfoFragment = BusinessApplyFragmentBusinessInfo.newInstance();
        }
        user_name = name;
        user_phone = number;
        user_email = email;

        getSupportFragmentManager().beginTransaction().add(R.id.blankFrame, businessInfoFragment).
                addToBackStack(BUSINESS_FRAGMENT_TAG).commit();
    }

    @Override
    public void businessInfoSubmitClick(String name, String number, String address) {
        if(conformationFragment == null) {
            conformationFragment = BusinessApplyFragmentConformation.newInstance();
        }
        saveBusiness(name, number, address);
        getSupportFragmentManager().beginTransaction().add(R.id.blankFrame, conformationFragment).
                addToBackStack(CONFIRM_FRAGMENT_TAG).commit();
    }

    @Override
    public void conformationDoneClick() {
        setResult(RESULT_OK);
        finish();
    }

    private void updateUser(){
        if(user == null){
            return;
        }
        DatabaseReference userRef = databaseReference.child("users").child(user.getUid());
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("admin_in_progress", true);
        userRef.updateChildren(userMap);
    }

    private void saveBusiness(String name, String number, String address){
        DatabaseReference businessRef = databaseReference.child("businesses").push();

        HashMap<String, Object> businessMap = new HashMap<>();
        businessMap.put("isVerfied", false);
        businessMap.put("specials", null);

        HashMap<String, Object> businessDetailMap = new HashMap<>();
        businessDetailMap.put("name", name);
        businessDetailMap.put("address", address);
        businessDetailMap.put("number", number);
        businessDetailMap.put("description", null);
        businessDetailMap.put("hours_of_operation", null);
        businessMap.put("details", businessDetailMap);

        HashMap<String, Object> adminMap = new HashMap<>();
        HashMap<String, Object> adminDetailMap = new HashMap<>();
        adminDetailMap.put("name", user_name);
        adminDetailMap.put("email", user_email);
        adminDetailMap.put("number", user_phone);
        adminMap.put(user.getUid(), adminDetailMap);
        businessMap.put("admins", adminMap);

        businessRef.setValue(businessMap).addOnFailureListener((Exception e)->
            new AlertDialog.Builder(this).setTitle("Unexpected Error")
                    .setMessage("Sorry, something went wrong on our in. Please try again later")
                    .setNeutralButton("Okay", null))
                .addOnCompleteListener(((@NonNull Task<Void> task)->updateUser()));
    }
}
