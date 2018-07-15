package com.wright.android.t_minus.business.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wright.android.t_minus.R;
import com.wright.android.t_minus.objects.Business;

public class BusinessViewActivity extends AppCompatActivity {

    public static final String ARG_BUSINESS = "ARG_BUSINESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        if(getIntent().hasExtra(ARG_BUSINESS)){
            getSupportFragmentManager().beginTransaction().add(R.id.blankFrame, BusinessViewFragment.newInstance(
                    (Business)getIntent().getSerializableExtra(ARG_BUSINESS))).commit();
        }else{
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
