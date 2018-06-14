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

import com.wright.android.t_minus.Launches.MainLaunchesFragment;
import com.wright.android.t_minus.Launches.ManifestFragment;
import com.wright.android.t_minus.Launches.PadFragment;

public class MainTabbedActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private LaunchesSectionsPagerAdapter launchesSectionsPagerAdapter;
    private TabLayout tabLayout;
    private TabLayout launchesTab;
    private Toolbar toolbar;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mMainViewPager;
    private ViewPager mLaunchViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(R.drawable.logo_outline);

        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        launchesSectionsPagerAdapter = new LaunchesSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mMainViewPager = (ViewPager) findViewById(R.id.container);
        mMainViewPager.setAdapter(mSectionsPagerAdapter);

        mLaunchViewPager = findViewById(R.id.launchesContainer);
        mLaunchViewPager.setAdapter(launchesSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mMainViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(this);

        launchesTab = findViewById(R.id.launchesTabs);
        mLaunchViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(launchesTab));
        launchesTab.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mLaunchViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        onTabSelected(tabLayout.getTabAt(0));
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
                    return ManifestFragment.newInstance();
                case 1:
                    return PadFragment.newInstance("","");
                default:
                    return ManifestFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
