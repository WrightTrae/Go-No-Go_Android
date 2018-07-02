package com.wright.android.t_minus;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wright.android.t_minus.main_tabs.launchpad.LaunchPadFragment;
import com.wright.android.t_minus.main_tabs.manifest.ManifestFragment;
import com.wright.android.t_minus.main_tabs.map.MapBaseFragment;
import com.wright.android.t_minus.objects.LaunchPad;
import com.wright.android.t_minus.objects.Manifest;
import com.wright.android.t_minus.objects.PadLocation;
import com.wright.android.t_minus.objects.ViewingLocation;
import com.wright.android.t_minus.settings.PreferencesActivity;
import com.wright.android.t_minus.network_connection.GetManifestsFromAPI;
import com.wright.android.t_minus.network_connection.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class MainTabbedActivity extends AppCompatActivity implements GetManifestsFromAPI.OnFinished, TabLayout.OnTabSelectedListener{
    private LaunchPadFragment launchPadFragment;
    private ManifestFragment manifestFragment;
    private MapBaseFragment customMapFragment;
    private ViewPager mMainViewPager;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        mMainViewPager = findViewById(R.id.container);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mMainViewPager.setOffscreenPageLimit(2);
        mMainViewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        TabLayout.Tab originalTab = tabLayout.getTabAt(0);
        if(originalTab != null && originalTab.getIcon() != null){
            originalTab.getIcon().setColorFilter(getColor(R.color.selectedTabColor), PorterDuff.Mode.SRC_IN);
        }
        for (int i = 1;i < tabLayout.getTabCount();i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if(tab != null && tab.getIcon() != null) {
                tab.getIcon().setColorFilter(getColor(R.color.unselectedTabColor), PorterDuff.Mode.SRC_IN);
            }
        }
        mMainViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(this);
        manifestFragment = ManifestFragment.newInstance();
        launchPadFragment = LaunchPadFragment.newInstance();
        customMapFragment = MapBaseFragment.newInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        downloadManifests();
    }

    private void downloadManifests(){/////////////////Download Data\\\\\\\\\\\\\\\\\\\\
        if(NetworkUtils.isConnected(this)){
            new GetManifestsFromAPI(this).execute();
        }else{
            Snackbar.make(mMainViewPager, "No internet connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload", (View v) -> downloadManifests()).show();
        }
    }

    @Override
    public void onFinished(Manifest[] _manifests) {
        ArrayList<PadLocation> padLocations = new ArrayList<>();
        for(Manifest manifest:_manifests){
            if (containsName(padLocations, manifest.getPadLocation().getId())) {
                padLocations.add(manifest.getPadLocation());
            }
        }
        checkIfPadLocationExist(padLocations);
        manifestFragment.setData(_manifests);
    }

    private boolean containsName(final ArrayList<PadLocation> list, final int name){
        return list.stream().noneMatch((o -> o.getId() == (name)));
    }

    private void checkIfPadLocationExist(ArrayList<PadLocation> _padLocations){
        mDatabaseRef.child("launch_locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (PadLocation pad: _padLocations) {
                    if(pad.getLaunchPads()==null || pad.getName().toLowerCase().equals("air launch to orbit")){
                        continue;
                    }
                    if (!dataSnapshot.hasChild(String.valueOf(pad.getId()))) {
                        updateFireballPadLocations(pad);
                    }else{
                        pad.setName((String) dataSnapshot.child(String.valueOf(pad.getId())).child("name").getValue());
                    }
                }

                for(DataSnapshot locationSnap: dataSnapshot.getChildren()){
                    int locationId = Integer.parseInt(locationSnap.getKey());
                    if (containsName(_padLocations, locationId)){
                        String name = (String) locationSnap.child("name").getValue();
                        PadLocation padLocation = new PadLocation(locationId, name, new ArrayList<>());
                        _padLocations.add(padLocation);
                    }
                }
                checkIfPadExist(_padLocations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateFireballPadLocations(PadLocation _padLocation){
        HashMap<String, Object> locationMap = new HashMap<>();
        locationMap.put("name", _padLocation.getName());
        DatabaseReference locationRef = mDatabaseRef.child("launch_locations").child(String.valueOf(_padLocation.getId()));
        locationRef.setValue(locationMap);
    }

    private void checkIfPadExist(ArrayList<PadLocation> _padLocations){
        mDatabaseRef.child("launch_pads").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (PadLocation pad: _padLocations) {
                    if(pad.getLaunchPads()==null || pad.getName().toLowerCase().equals("air launch to orbit")){
                        continue;
                    }
                    for(LaunchPad launchPad : pad.getLaunchPads()) {
                        if (!dataSnapshot.hasChild(String.valueOf(launchPad.getId()))) {
                            updateFirebasePad(launchPad);
                        }else{
                            launchPad.setName((String) dataSnapshot.child(String.valueOf(launchPad.getId())).child("name").getValue());
                        }
                    }
                }
                for(DataSnapshot padSnap: dataSnapshot.getChildren()){
                    int padId = Integer.parseInt(padSnap.getKey());
                    if (containsName(_padLocations, padId)){
                        String name = (String) padSnap.child("name").getValue();
                        double latitude = (double) padSnap.child("latitude").getValue();
                        long locationId = (long) padSnap.child("locationId").getValue();
                        double longitude = (double) padSnap.child("longitude").getValue();
                        LaunchPad launchPad = new LaunchPad(padId,name,latitude,longitude,(int)locationId);
                        for(PadLocation padLocation: _padLocations){
                            if (padLocation.getId() == launchPad.getLocationId()){
                                padLocation.addLaunchPads(launchPad);
                            }
                        }
                    }
                }
                launchPadFragment.setData(_padLocations);
                customMapFragment.customMapFragment.setData(_padLocations);
                getMapData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateFirebasePad(LaunchPad launchPad){
        HashMap<String, Object> padMap = new HashMap<>();
        padMap.put("latitude", launchPad.getLatitude());
        padMap.put("longitude", launchPad.getLongitude());
        padMap.put("locationId", launchPad.getLocationId());
        padMap.put("name", launchPad.getName());
        DatabaseReference padRef = mDatabaseRef.child("launch_pads").child(String.valueOf(launchPad.getId()));
        padRef.setValue(padMap);
    }

    private void getMapData(){
        mDatabaseRef.child("viewing_locations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                customMapFragment.customMapFragment.removeViewingLocation(key);
            }

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String name = (String) dataSnapshot.child("name").getValue();
                String id = dataSnapshot.getKey();
                double latitude = (double) dataSnapshot.child("latitude").getValue();
                double longitude = (double) dataSnapshot.child("longitude").getValue();
                customMapFragment.customMapFragment.addViewingLocation(new ViewingLocation(id, name, latitude,longitude));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {/////////////////Setup UI\\\\\\\\\\\\\\\\\\\\
        getMenuInflater().inflate(R.menu.menu_main_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, PreferencesActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mMainViewPager.setCurrentItem(tab.getPosition());
        int tabIconColor = ContextCompat.getColor(this, R.color.selectedTabColor);
        if(tab.getIcon()!=null)
            tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        int tabIconColor = ContextCompat.getColor(this, R.color.unselectedTabColor);
        if(tab.getIcon()!=null)
            tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }



        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return manifestFragment;
                case 1:
                    return launchPadFragment;
                case 2:
                    return customMapFragment;
                case 3:

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
