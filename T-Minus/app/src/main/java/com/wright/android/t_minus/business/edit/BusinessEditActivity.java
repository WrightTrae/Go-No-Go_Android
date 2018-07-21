package com.wright.android.t_minus.business.edit;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.objects.Business;

import java.util.ArrayList;

public class BusinessEditActivity extends AppCompatActivity implements BusinessListFragment.OnListFragmentInteractionListener {

    private ArrayList<Business> businesses;
    private Business selectedBusiness;
    private EditBusinessFragment editBusinessFragment;
    private BusinessListFragment businessListFragment;
    DatabaseReference mBuisnessesDatabaseRef;
    DatabaseReference mUserDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        mBuisnessesDatabaseRef = FirebaseDatabase.getInstance().getReference().child("businesses");
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid())
        .child("businesses");
        getYourBuisnessData();
    }

    private void getYourBuisnessData(){
        mUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> userBusinessIds = new ArrayList<>();
                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    userBusinessIds.add(snap.getKey());
                }
                getBusinessesData(userBusinessIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getBusinessesData(ArrayList<String> ids){
        mBuisnessesDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                businesses = new ArrayList<>();
                for(DataSnapshot singleSnap: dataSnapshot.getChildren()) {
                    if (ids.contains(singleSnap.getKey())) {
                        boolean verified = (boolean) singleSnap.child("isVerified").getValue();
                        if (verified) {
                            DataSnapshot detailsSnap = singleSnap.child("details");
                            businesses.add(new Business(
                                    singleSnap.getKey(),
                                    (boolean) singleSnap.child("isVerified").getValue(),
                                    (String) detailsSnap.child("name").getValue(),
                                    (String) detailsSnap.child("number").getValue(),
                                    (double) detailsSnap.child("latitude").getValue(),
                                    (double) detailsSnap.child("longitude").getValue(),
                                    (String) detailsSnap.child("address").getValue(),
                                    (String) detailsSnap.child("description").getValue()));
                        }
                    }
                }
                onBusinessDataFinish(businesses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onBusinessDataFinish(ArrayList<Business> businesses){
        if(!this.isDestroyed()) {
            businessListFragment = BusinessListFragment.newInstance(businesses);
            getSupportFragmentManager().beginTransaction().add(R.id.blankFrame, businessListFragment).commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(editBusinessFragment != null && selectedBusiness != null) {
            if (editBusinessFragment.checkFields()) {
                Business updatedBusiness = editBusinessFragment.getNewData();
                int index = businesses.indexOf(selectedBusiness);
                businesses.remove(index);
                businesses.add(index, updatedBusiness);
                businessListFragment.notifyListView(updatedBusiness, index);
                updateBusiness(updatedBusiness);
            } else {
                return false;
            }
        }
        boolean pop = getSupportFragmentManager().popBackStackImmediate();
        if(!pop){
            onBackPressed();
        }else{
            editBusinessFragment = null;
            selectedBusiness = null;
        }
        return true;
    }

    public void updateBusiness(Business updatedBusiness){
        DatabaseReference businessRef = mBuisnessesDatabaseRef.child(updatedBusiness.getId()).child("details");
        businessRef.updateChildren(updatedBusiness.getDetailsMap());
    }

    @Override
    public void onListFragmentInteraction(Business item){
        editBusinessFragment = EditBusinessFragment.newInstance(item);
        selectedBusiness = item;
        getSupportFragmentManager().beginTransaction().add(R.id.blankFrame, editBusinessFragment).addToBackStack("").commit();
    }
}
