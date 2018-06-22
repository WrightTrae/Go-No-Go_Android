package com.wright.android.t_minus.Launches.Manifest;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wright.android.t_minus.ArActivity;
import com.wright.android.t_minus.Objects.Manifest;
import com.wright.android.t_minus.R;

public class ManifestDetailsActivity extends AppCompatActivity {

    public static final String ARG_MANIFEST = "ARG_MANIFEST";
    private Manifest manifest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manifest_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.logo_outline);
        getSupportActionBar().setTitle("");
        if(getIntent().hasExtra(ARG_MANIFEST)){
            manifest = (Manifest) getIntent().getSerializableExtra(ARG_MANIFEST);
            setUpUi();
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
        ((TextView)findViewById(R.id.detailsMissionStatus)).setText(manifest.getStatus());
        ((TextView)findViewById(R.id.detailsProbability)).setText(String.format(getString(R.string.probability), manifest.getProbability()));
        ((TextView)findViewById(R.id.detailsWindowStart)).setText(String.format(getString(R.string.window_start), manifest.getWindowStart()));
        ((TextView)findViewById(R.id.detailsWindowEnd)).setText(String.format(getString(R.string.window_end), manifest.getWindowEnd()));
        ((TextView)findViewById(R.id.detailsMissionProvider)).setText(String.format(getString(R.string.mission_provider), manifest.getMissionProvider()));
        Button liveBtn = findViewById(R.id.detailsLiveBtn);
        if(manifest.getUrl()==null){
            liveBtn.setVisibility(View.GONE);
        }else{
            liveBtn.setVisibility(View.VISIBLE);
            liveBtn.setOnClickListener((View v)->startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(manifest.getUrl()))));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
