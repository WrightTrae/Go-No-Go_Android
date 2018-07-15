package com.wright.android.t_minus.main_tabs.manifest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wright.android.t_minus.ar.ArActivity;
import com.wright.android.t_minus.notifications.NotificationHelper;
import com.wright.android.t_minus.objects.Manifest;
import com.wright.android.t_minus.objects.ManifestDetails;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.network_connection.GetManifestsDetailsFromAPI;
import com.wright.android.t_minus.network_connection.NetworkUtils;
import com.wright.android.t_minus.settings.PreferencesActivity;
import com.wright.android.t_minus.settings.PreferencesFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.wright.android.t_minus.notifications.ShowNotificationService.CHANNEL_ID;

public class ManifestDetailsActivity extends AppCompatActivity implements GetManifestsDetailsFromAPI.OnFinished {

    public static final String ARG_MANIFEST = "ARG_MANIFEST";
    private Manifest manifest;
    private ProgressBar progressBar;
    private NotificationHelper notificationHelper;
    public static final int testLaunchID = 0x1101;//TODO: THIS IS FOR TESTING ONLY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manifest_details);

        handleNotificationChannel();
        notificationHelper = new NotificationHelper(this);
        notificationHelper.setBindService();

        progressBar = findViewById(R.id.detailsProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        if(getIntent().hasExtra(ARG_MANIFEST)){
            manifest = (Manifest) getIntent().getSerializableExtra(ARG_MANIFEST);
            if(manifest.getLaunchId() == testLaunchID){
                setTestDetails();
            }else {
                downloadDetails();
            }
        }
    }

    private void setTestDetails(){
        manifest.setManifestDetails(new ManifestDetails("Launch is GO", "Unknown",
                "Test start time","Test end time","Go/No-Go",null,"TEST LAUNCH",
                "This is a test launch created for user testing to display notifications and live launch view example."));
        setUpUi();
    }

    private void handleNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Launch Time";
            String description = "Go/No-Go launch notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel c = new NotificationChannel(CHANNEL_ID, name, importance);
            c.setDescription(description);

            NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (mgr != null) {
                mgr.createNotificationChannel(c);
            }
        }
    }

    public void downloadDetails(){
        if(NetworkUtils.isConnected(this)){
            new GetManifestsDetailsFromAPI(this).execute(String.valueOf(manifest.getLaunchId()));
        }else{
            Snackbar.make(findViewById(R.id.scrollView2), "No internet connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload", (View v) -> downloadDetails()).show();
        }
    }

    private void setUpUi(){
        if(!manifest.getImageUrl().equals("https://s3.amazonaws.com/launchlibrary/RocketImages/placeholder_1920.png")){
            Picasso.get().load(manifest.getImageUrl()).fit().centerCrop()
                    .placeholder(R.drawable.logo_outline).into((ImageView)findViewById(R.id.manifestDetailsImage));
        }else {
            ((ImageView)findViewById(R.id.manifestDetailsImage)).setImageDrawable(getDrawable(R.drawable.rocket_default_image));
        }
        FloatingActionButton fab = findViewById(R.id.manifestFab);
        if (manifest.getPadLocation() == null || manifest.getPadLocation().getLaunchPads()==null){
            fab.setVisibility(View.GONE);
            ((TextView)findViewById(R.id.detailsLocation)).setText(getString(R.string.unknown_location));
        }else {
            ((TextView)findViewById(R.id.detailsLocation)).setText(manifest.getPadLocation().getName());
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener((View view) -> {
                Intent intent = new Intent(this, ArActivity.class);
                intent.putExtra(ArActivity.ARG_MANIFEST_LAUNCH_PADS, manifest.getPadLocation().getLaunchPads());
                startActivity(intent);
            });
        }
        ((TextView)findViewById(R.id.detailsMissionTitle)).setText(manifest.getTitle());
        ((TextView)findViewById(R.id.detailsNETTime)).setText(manifest.getTime());

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
        if(manifest.getPadLocation() != null && manifest.getPadLocation().getLaunchPads() != null && manifest.getPadLocation().getLaunchPads().size() != 0) {
            ((TextView) findViewById(R.id.detailsLaunchPad)).setText(String.format(getString(R.string.details_launch_pad),
                    manifest.getPadLocation().getLaunchPads().get(0).getName()));
        }
        ((TextView)findViewById(R.id.detailsDescription)).setText(manifestDetails.getDescription());

        Button notifBtn = findViewById(R.id.detailsNotifBtn);
        notifBtn.setOnClickListener((View v) -> {
            if(manifest.getTimeDate() == null){
                return;
            }
            notifBtn.setEnabled(false);
            notifBtn.setText(getString(R.string.subscribed));
            Calendar cal = Calendar.getInstance();
            cal.setTime(manifest.getTimeDate());
            int minutes = Integer.parseInt(getSharedPreferences(PreferencesFragment.PREFS, MODE_PRIVATE).getString(PreferencesFragment.NOTIF_PREF, "30"));
            cal.add(Calendar.MINUTE , -minutes);
            DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
            Toast.makeText(this, dateFormat.format(cal.getTime()),Toast.LENGTH_SHORT).show();
            notificationHelper.setAlarmForNotification(cal, manifest);
        });

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

    @Override
    protected void onStop() {
        if(notificationHelper != null)
            notificationHelper.unbindService();
        super.onStop();
    }
}
