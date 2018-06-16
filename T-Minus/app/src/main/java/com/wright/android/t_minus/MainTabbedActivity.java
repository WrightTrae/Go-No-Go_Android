package com.wright.android.t_minus;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.wright.android.t_minus.Launches.LaunchPad.LaunchPadFragment;
import com.wright.android.t_minus.Launches.MainLaunchesFragment;
import com.wright.android.t_minus.Launches.Manifest.ManifestFragment;
import com.wright.android.t_minus.Objects.Manifest;
import com.wright.android.t_minus.Objects.PadLocation;
import com.wright.android.t_minus.networkConnection.GetManifestsFromAPI;
import com.wright.android.t_minus.networkConnection.NetworkUtils;

import java.util.ArrayList;

public class MainTabbedActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, GetManifestsFromAPI.OnFinished{

    private TabLayout launchesTab;
    private Toolbar toolbar;
    private LaunchPadFragment launchPadFragment;
    private ManifestFragment manifestFragment;
    private ViewPager mMainViewPager;
    private ViewPager mLaunchViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(R.drawable.logo_outline);
        setSupportActionBar(toolbar);

        mMainViewPager = findViewById(R.id.container);
        mMainViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

        mLaunchViewPager = findViewById(R.id.launchesContainer);
        mLaunchViewPager.setAdapter(new LaunchesSectionsPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.tabs);
        mMainViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(this);

        launchesTab = findViewById(R.id.launchesTabs);
        mLaunchViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(launchesTab));
        launchesTab.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mLaunchViewPager));

        onTabSelected(tabLayout.getTabAt(0));
        downloadManifests();
    }

    public void downloadManifests(){
        if(NetworkUtils.isConnected(this)){
            new GetManifestsFromAPI(this).execute();
        }else{
            Snackbar.make(mLaunchViewPager, "No internet connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload", (View v) -> downloadManifests()).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_tabbed, menu);
        toolbar.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mMainViewPager.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()) {
            case 0:
                mMainViewPager.setVisibility(View.GONE);
                mLaunchViewPager.setVisibility(View.VISIBLE);
                launchesTab.setVisibility(View.VISIBLE);
                break;
            default:
                mMainViewPager.setVisibility(View.VISIBLE);
                mLaunchViewPager.setVisibility(View.GONE);
                launchesTab.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public boolean containsName(final ArrayList<PadLocation> list, final int name){
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
        launchPadFragment.setData(padLocations);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:

                case 1:

                case 2:

                default:
                    return MainLaunchesFragment.newInstance("","");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public class LaunchesSectionsPagerAdapter extends FragmentPagerAdapter {

        public LaunchesSectionsPagerAdapter(FragmentManager fm) {
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
                default:
                    if(manifestFragment == null){
                        manifestFragment = ManifestFragment.newInstance();
                    }
                    return manifestFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
