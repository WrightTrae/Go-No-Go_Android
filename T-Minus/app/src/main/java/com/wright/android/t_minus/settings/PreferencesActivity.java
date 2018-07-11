package com.wright.android.t_minus.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wright.android.t_minus.R;


public class PreferencesActivity extends AppCompatActivity {
    private PreferencesFragment p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        p =  PreferencesFragment.newInstance();
        p.setActivity(this);
        getFragmentManager().beginTransaction().replace(R.id.prefFrameLayout, p).commit();

    }

    @Override
    public void onBackPressed() {
        Intent resultInt = new Intent();
        p.writeCustomPrefs();
        setResult(RESULT_OK, resultInt);
        finish();
        super.onBackPressed();
    }
}
