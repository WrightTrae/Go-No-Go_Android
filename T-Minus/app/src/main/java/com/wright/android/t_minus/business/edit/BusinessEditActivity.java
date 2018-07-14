package com.wright.android.t_minus.business.edit;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.objects.Business;

import java.util.ArrayList;

public class BusinessEditActivity extends AppCompatActivity implements BusinessListFragment.OnListFragmentInteractionListener {

    private static ArrayList<Business> businesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        getBusinessesData();
    }

    private void getBusinessesData(){
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.child("businesses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                businesses = new ArrayList<>();
                for(DataSnapshot singleSnap: dataSnapshot.getChildren()){
                    boolean verified = (boolean)singleSnap.child("isVerified").getValue();
                    if (verified){
                        DataSnapshot detailsSnap = singleSnap.child("details");
                        businesses.add(new Business(
                                (boolean)singleSnap.child("isVerified").getValue(),
                                (String)detailsSnap.child("name").getValue(),
                                (String)detailsSnap.child("number").getValue(),
                                (double)detailsSnap.child("latitude").getValue(),
                                (double)detailsSnap.child("longitude").getValue(),
                                (String) detailsSnap.child("address").getValue(),
                                (String)detailsSnap.child("description").getValue()));
                    }
                }
                getSupportFragmentManager().beginTransaction().add(R.id.blankFrame, BusinessListFragment.newInstance(businesses)).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    public void onListFragmentInteraction(Business item) {

    }
}
