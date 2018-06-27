package com.wright.android.t_minus;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.design.widget.FloatingActionButton;
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
import android.view.View;
import android.widget.ExpandableListView;

import com.wright.android.t_minus.MainTabs.LaunchPad.LaunchPadFragment;
import com.wright.android.t_minus.MainTabs.LaunchPad.PadAdapter;
import com.wright.android.t_minus.MainTabs.Manifest.ManifestFragment;
import com.wright.android.t_minus.MainTabs.Map.CustomMapFragment;
import com.wright.android.t_minus.MainTabs.Map.MapFrag;
import com.wright.android.t_minus.Objects.LaunchPad;
import com.wright.android.t_minus.Objects.Manifest;
import com.wright.android.t_minus.Objects.PadLocation;
import com.wright.android.t_minus.networkConnection.GetManifestsFromAPI;
import com.wright.android.t_minus.networkConnection.GetPadsFromAPI;
import com.wright.android.t_minus.networkConnection.NetworkUtils;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

public class MainTabbedActivity extends AppCompatActivity implements GetManifestsFromAPI.OnFinished, GetPadsFromAPI.OnFinished, TabLayout.OnTabSelectedListener{
    private LaunchPadFragment launchPadFragment;
    private ManifestFragment manifestFragment;
    private CustomMapFragment customMapFragment;
    private ViewPager mMainViewPager;
    private ListIterator<PadLocation> iterator;
    private ArrayList<PadLocation> padLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        mMainViewPager = findViewById(R.id.container);
        mMainViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
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
        customMapFragment = CustomMapFragment.newInstance();
        downloadManifests();
    }

    private void downloadManifests(){
        if(NetworkUtils.isConnected(this)){
            new GetManifestsFromAPI(this).execute();
        }else{
            Snackbar.make(mMainViewPager, "No internet connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload", (View v) -> downloadManifests()).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main_tabbed, menu);
//        toolbar.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private boolean containsName(final ArrayList<PadLocation> list, final int name){
        return list.stream().anyMatch((o -> o.getId() == (name)));
    }

    @Override
    public void onFinished(Manifest[] _manifests) {
        manifestFragment.setData(_manifests);
        ArrayList<PadLocation> padLocations = new ArrayList<>();
        for(Manifest manifest:_manifests){
            if (!containsName(padLocations, manifest.getPadLocation().getId())) {
                padLocations.add(manifest.getPadLocation());
            }
        }
        this.padLocations = padLocations;
        getLaunchPadData(padLocations);
    }

    private void getLaunchPadData(ArrayList<PadLocation> padLocations){

        iterator = padLocations.listIterator();
        if(NetworkUtils.isConnected(this)&&iterator.hasNext()){
            new GetPadsFromAPI(this).execute(Integer.toString(iterator.next().getId()));
        }
    }

    @Override
    public void onFinished(ArrayList<LaunchPad> _padList) {
        if(iterator.hasPrevious()) {
            padLocations.get(iterator.previousIndex()).setLaunchPads(_padList);
        }
        if(iterator.hasNext()) {
            new GetPadsFromAPI(this).execute(Integer.toString(iterator.next().getId()));
        }else{
            launchPadFragment.setData(padLocations);
            customMapFragment.setData(padLocations);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mMainViewPager.setCurrentItem(tab.getPosition());
        int tabIconColor = ContextCompat.getColor(this, R.color.selectedTabColor);
        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        int tabIconColor = ContextCompat.getColor(this, R.color.unselectedTabColor);
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
                    if(manifestFragment == null){
                        manifestFragment = ManifestFragment.newInstance();
                    }
                    return manifestFragment;
                case 1:
                    if(launchPadFragment == null){
                        launchPadFragment = LaunchPadFragment.newInstance();
                    }
                    return launchPadFragment;
                case 2:
                    if(customMapFragment == null){
                        customMapFragment = CustomMapFragment.newInstance();
                    }
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
