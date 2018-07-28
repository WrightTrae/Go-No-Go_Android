package com.wright.android.t_minus.main_tabs.map;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.settings.PreferencesActivity;

import java.util.HashMap;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;


public class MapBaseFragment extends Fragment {

    public CustomMapFragment customMapFragment;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private LocationManager locMgr;

    public MapBaseFragment() {
        // Required empty public constructor
    }

    public static MapBaseFragment newInstance() {
        MapBaseFragment fragment = new MapBaseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locMgr = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkLocationPermission();
        customMapFragment = (CustomMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        FloatingActionButton fab = view.findViewById(R.id.mapFab);
        fab.setOnClickListener((View v) -> {
            if(checkLocationPermission()){
                showLocationDialog();
            }
        });
    }

    private void showLocationDialog(){
        if(getContext()==null){
            return;
        }
        if(!locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            new android.support.v7.app.AlertDialog.Builder(getContext())
                    .setTitle("Location Service Is Off")
                    .setMessage("Location service is needed to add a new viewing location, go to setting to turn it back on.")
                    .setPositiveButton("Okay",null)
                    .create()
                    .show();
            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            final EditText input = new EditText(getActivity());
            input.setHint("Viewing Location Name");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginStart(20);
            lp.setMarginEnd(20);
            input.setLayoutParams(lp);
            alertDialogBuilder.setView(input);

            alertDialogBuilder.setTitle("Would you like to add your location as a viewing point?");
            alertDialogBuilder
                    .setMessage("A viewing is a point on the map that has a good view of the rocket launch.")
                    .setPositiveButton("Yes, I want to share", (DialogInterface dialog, int id) ->
                            addViewingLocation(input.getText().toString()))
                    .setNegativeButton("No, I don't", null);
        }else {
            alertDialogBuilder.setTitle("Sign In Required");
            alertDialogBuilder
                    .setMessage("You need to sign in to publish a new viewing location.")
                    .setPositiveButton("Sign In", (DialogInterface dialog, int id) -> {
                        Intent intent = new Intent(getContext(), PreferencesActivity.class);
                        startActivity(intent);
                    })
                    .setNeutralButton("Cancel", null);
        }
        alertDialogBuilder.create().show();
    }

    private void addViewingLocation(String name){
        if(checkLocationPermission()) {
            Location loc = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(loc == null){
                loc = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("viewing_locations")
                    .push();
            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("name", name);
            userMap.put("latitude", loc.getLatitude());
            userMap.put("longitude", loc.getLongitude());
            userRef.setValue(userMap);
        }
    }

    public boolean checkLocationPermission() {
        if(getContext()==null||getActivity()==null){
            return false;
        }
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new android.support.v7.app.AlertDialog.Builder(getContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("Please allow location permission for feature")
                        .setPositiveButton("Allow", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .setNegativeButton("Don't Allow", null)
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if(getContext()==null){
            return;
        }
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        showLocationDialog();
                    }

                } else {
                    Snackbar snack = Snackbar.make(Objects.requireNonNull(getView()), "Location permission is needed for map", Snackbar.LENGTH_SHORT);
                    snack.show();
                }
            }
        }
    }
}
