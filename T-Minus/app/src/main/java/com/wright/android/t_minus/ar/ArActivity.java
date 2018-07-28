package com.wright.android.t_minus.ar;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.objects.LaunchPad;
import com.wright.android.t_minus.objects.PadLocation;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;

public class ArActivity extends AppCompatActivity {

    private ArSceneView arSceneView;
    private Snackbar loadingMessageSnackbar = null;
    private LocationScene locationScene;
    private ModelRenderable andyRenderable;
    private boolean installRequested;
    private ArrayList<LaunchPad> launchPads;

    public static final String ARG_LAUNCH_PAD = "ARG_LAUNCH_PAD";
    public static final String ARG_ALL_LAUNCH_PADS = "ARG_ALL_LAUNCH_PADS";
    public static final String ARG_MANIFEST_LAUNCH_PADS = "ARG_MANIFEST_LAUNCH_PADS";

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        arSceneView = findViewById(R.id.arFrameLayout);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        launchPads = new ArrayList<>();
        if(getIntent().hasExtra(ARG_LAUNCH_PAD)){
            launchPads.add((LaunchPad) getIntent().getSerializableExtra(ARG_LAUNCH_PAD));
        }else if(getIntent().hasExtra(ARG_ALL_LAUNCH_PADS)){
            ArrayList<PadLocation> padLocations = (ArrayList<PadLocation>)getIntent().getSerializableExtra(ARG_ALL_LAUNCH_PADS);
            for(PadLocation padLocation:padLocations){
                launchPads.addAll(padLocation.getLaunchPads());
            }
        }else if(getIntent().hasExtra(ARG_MANIFEST_LAUNCH_PADS)){
            launchPads.addAll((ArrayList<LaunchPad>)getIntent().getSerializableExtra(ARG_MANIFEST_LAUNCH_PADS));
        }
        if(checkCameraPermission()&&checkLocationPermission()){
            setupAr();
        }
    }

    private void setupAr(){
        SharedPreferences onBoardPrefs = getPreferences(MODE_PRIVATE);
        boolean shown = onBoardPrefs.getBoolean("shown", false);
        if(!shown) {
            Dialog onboardDialog = new Dialog(this);
            if (onboardDialog.getWindow() != null) {
                onboardDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                onboardDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
            View popup = LayoutInflater.from(this).inflate(R.layout.ar_onboard_popup_layout, null);
            onboardDialog.setContentView(popup);
            onboardDialog.setCancelable(true);
            onboardDialog.show();

            // Hide after some seconds
            final Handler handler = new Handler();
            final Runnable runnable = () -> {
                if (onboardDialog.isShowing()) {
                    onboardDialog.dismiss();
                }
            };

            onboardDialog.setOnDismissListener((DialogInterface dialog) ->
                    handler.removeCallbacks(runnable));
            handler.postDelayed(runnable, 10000);

            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("shown", true);
            editor.apply();
        }

        CompletableFuture<ModelRenderable> andy = ModelRenderable.builder()
                .setSource(this, R.raw.model)
                .build();
        CompletableFuture.allOf(andy)
                .handle(
                        (notUsed, throwable) ->
                        {
                            if (throwable != null) {
                                ArUtils.displayError(this, "Unable to load renderables", throwable);
                                return null;
                            }
                            try {
                                andyRenderable = andy.get();

                            } catch (InterruptedException | ExecutionException ex) {
                                ArUtils.displayError(this, "Unable to load renderables", ex);
                            }
                            return null;
                        });

        ArUtils.requestCameraPermission(this, 1);

        arSceneView
                .getScene()
                .setOnUpdateListener(
                        frameTime -> {
                            if (loadingMessageSnackbar == null) {
                                return;
                            }

                            Frame frame = arSceneView.getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }
                            if (locationScene == null) {
                                locationScene = new LocationScene(this, this, arSceneView);
                                for(LaunchPad launchPad: launchPads){
                                    locationScene.mLocationMarkers.add(new LocationMarker(launchPad.getLongitude(),launchPad.getLatitude(),getAndy(launchPad.getName())));
                                }
                            }

                            if (locationScene != null) {
                                hideLoadingMessage();
                                locationScene.processFrame(arSceneView.getArFrame());
                            }

                        });
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("Please allow location permission for AR viewer")
                        .setPositiveButton("Okay", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(ArActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Camera Permission Needed")
                        .setMessage("Please allow camera permission for AR viewer")
                        .setPositiveButton("Okay", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(ArActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        setupAr();
                    }

                } else {
                    Snackbar snack = Snackbar.make(findViewById(R.id.arFrameLayout), "Location permission is needed to run AR viewer", Snackbar.LENGTH_SHORT);
                    snack.show();
                    finish();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_CAMERA:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        setupAr();
                    }

                } else {
                    Snackbar snack = Snackbar.make(findViewById(R.id.arFrameLayout), "Camera permission is needed to run AR viewer", Snackbar.LENGTH_SHORT);
                    snack.show();
                    finish();
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = ArUtils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = ArUtils.hasCameraPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                ArUtils.handleSessionException(this, e);
            }
        }

        if(locationScene!=null){
            locationScene.resume();
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            ArUtils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {
            showLoadingMessage();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(locationScene!=null){
            locationScene.pause();
        }
        arSceneView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationScene!=null){
            locationScene.pause();
            locationScene = null;
        }
        arSceneView.destroy();
    }

    private Node getAndy(String name) {
        Node base = new Node();
        base.setParent(arSceneView.getScene());
        base.setRenderable(andyRenderable);
        Context c = this;
        base.setOnTapListener((v, event) -> Toast.makeText(
                c, "Launch Pad: "+name, Toast.LENGTH_LONG)
                .show());
        return base;
    }

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        ArActivity.this.findViewById(android.R.id.content),
                        "Loading...",
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }
}
