package com.wright.android.t_minus.MainTabs.Manifest;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wright.android.t_minus.Ar.ArActivity;
import com.wright.android.t_minus.Objects.Manifest;
import com.wright.android.t_minus.Objects.ManifestDetails;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.networkConnection.GetManifestsDetailsFromAPI;
import com.wright.android.t_minus.networkConnection.NetworkUtils;

public class ManifestDetailsActivity extends AppCompatActivity implements GetManifestsDetailsFromAPI.OnFinished {

    public static final String ARG_MANIFEST = "ARG_MANIFEST";
    private Manifest manifest;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manifest_details);
        progressBar = findViewById(R.id.detailsProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.logo_outline);
            getSupportActionBar().setTitle("");
        }
        if(getIntent().hasExtra(ARG_MANIFEST)){
            manifest = (Manifest) getIntent().getSerializableExtra(ARG_MANIFEST);
            downloadDetails();
        }
    }

    public void downloadDetails(){
        if(NetworkUtils.isConnected(this)){
            new GetManifestsDetailsFromAPI(this).execute(String.valueOf(manifest.getLaunchId()));
        }else{
            if(this.hasWindowFocus()) {
                Snackbar.make(this.getCurrentFocus(), "No internet connection", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reload", (View v) -> downloadDetails()).show();
            }
        }
    }

    private void setUpUi(){
        if(!manifest.getImageUrl().equals("https://s3.amazonaws.com/launchlibrary/RocketImages/placeholder_1920.png")){
            Picasso.get().load(manifest.getImageUrl()).fit().centerCrop()
                    .placeholder(R.drawable.logo_outline).into((ImageView)findViewById(R.id.manifestDetailsImage));
        }else {
            ((ImageView)findViewById(R.id.manifestDetailsImage)).setImageDrawable(getDrawable(R.drawable.logo_outline));
        }
        FloatingActionButton fab = findViewById(R.id.manifestFab);
        if (manifest.getPadLocation().getLaunchPads()==null){
            fab.setVisibility(View.GONE);
        }else {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener((View view) -> {
                Intent intent = new Intent(this, ArActivity.class);
                intent.putExtra(ArActivity.ARG_MANIFEST_LAUNCH_PADS, manifest.getPadLocation().getLaunchPads());
                startActivity(intent);
            });
        }
        ((TextView)findViewById(R.id.detailsMissionTitle)).setText(manifest.getTitle());
        ((TextView)findViewById(R.id.detailsNETTime)).setText(manifest.getTime());
        ((TextView)findViewById(R.id.detailsLocation)).setText(manifest.getPadLocation().getName());

        final ManifestDetails manifestDetails = manifest.getManifestDetails();
        ((TextView)findViewById(R.id.detailsMissionStatus)).setText(manifestDetails.getStatus());
        if(manifestDetails.getStatus().toLowerCase().equals("launch is go")){
            findViewById(R.id.detailsMissionStatus).setBackgroundColor(getColor(R.color.transparentGreen));
        }
        ((TextView)findViewById(R.id.detailsType)).setText(String.format(getString(R.string.type), manifestDetails.getType()));
        ((TextView)findViewById(R.id.detailsProbability)).setText(String.format(getString(R.string.probability), manifestDetails.getProbability()));
        ((TextView)findViewById(R.id.detailsWindowStart)).setText(String.format(getString(R.string.window_start), manifestDetails.getWindowStart()));
        ((TextView)findViewById(R.id.detailsWindowEnd)).setText(String.format(getString(R.string.window_end), manifestDetails.getWindowEnd()));
        ((TextView)findViewById(R.id.detailsMissionProvider)).setText(String.format(getString(R.string.mission_provider), manifestDetails.getMissionProvider()));
        if(manifest.getPadLocation().getLaunchPads() != null || manifest.getPadLocation().getLaunchPads().size() != 0) {
            ((TextView) findViewById(R.id.detailsLaunchPad)).setText(String.format(getString(R.string.details_launch_pad),
                    manifest.getPadLocation().getLaunchPads().get(0).getName()));
        }
        ((TextView)findViewById(R.id.detailsDescription)).setText(manifestDetails.getDescription());
        Button liveBtn = findViewById(R.id.detailsLiveBtn);
        if(manifestDetails.getUrl()==null){
            liveBtn.setVisibility(View.GONE);
        }else{
            liveBtn.setVisibility(View.VISIBLE);
            liveBtn.setOnClickListener((View v)->startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(manifestDetails.getUrl()))));
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onFinished(ManifestDetails details) {
        manifest.setManifestDetails(details);
        setUpUi();
    }
}
