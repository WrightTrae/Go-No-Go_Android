package com.wright.android.t_minus;

import android.content.Context;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;
import uk.co.appoly.arcorelocation.utils.LocationUtils;

public class ArActivity extends AppCompatActivity{

    private ArSceneView arSceneView;
    private ArFragment arFragment;
    private Session arSession;
    private LocationScene locationScene;
    private ModelRenderable andyRenderable;
    private boolean installRequested;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        arFragment = new ArFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.arFrameLayout, arFragment).commit();
//        arSceneView = arFragment.getArSceneView();
//        arSession = arSceneView.getSession();
//
//
//        CompletableFuture<ModelRenderable> andy = ModelRenderable.builder()
//                .setSource(this,R.raw.andy)
//                .build();
//        CompletableFuture.allOf(andy)
//                .handle(
//                        (notUsed, throwable) ->
//                        {
//                            if (throwable != null) {
//                                ArUtils.displayError(this, "Unable to load renderables", throwable);
//                                return null;
//                            }
//                            try {
//                                Location location = LocationServices.getFusedLocationProviderClient(this).getLastLocation().getResult();
//                                andyRenderable = andy.get();
//                                arSceneView
//                                        .getScene()
//                                        .setOnUpdateListener(
//                                                frameTime -> {
//
//                                                    if (locationScene == null) {
//                                                        locationScene = new LocationScene(this, this, arSceneView);
//                                                        locationScene.mLocationMarkers.add(
//                                                                new LocationMarker(
//                                                                        location.getLongitude(),
//                                                                        location.getLatitude(),
//                                                                        getAndy()));
//
//                                                    }
//
//                                                    if (locationScene != null) {
//                                                        locationScene.processFrame(arSceneView.getArFrame());
//                                                    }
//
//                                                });
//
//                            } catch (InterruptedException | SecurityException | ExecutionException ex) {
//                                ArUtils.displayError(this, "Unable to load renderables", ex);
//                            }
//                            return null;
//                        });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        ar();
//    }
//
//    private void ar(){
//        if (arSceneView.getSession() == null) {
//            // If the session wasn't created yet, don't resume rendering.
//            // This can happen if ARCore needs to be updated or permissions are not granted yet.
//            try {
//                Session session = ArUtils.createArSession(this, installRequested);
//                if (session == null) {
//                    installRequested = ArUtils.hasCameraPermission(this);
//                    return;
//                } else {
//                    arSceneView.setupSession(session);
//                }
//            } catch (UnavailableException e) {
//                ArUtils.handleSessionException(this, e);
//            }
//        }
//        try {
//            arSceneView.resume();
//        } catch (CameraNotAvailableException ex) {
//            ArUtils.displayError(this, "Unable to get camera", ex);
//            finish();
//            return;
//        }
//    }

    private Node getAndy() {
        Node base = new Node();
        base.setParent(arSceneView.getScene());
        base.setRenderable(andyRenderable);
        Context c = this;
        base.setOnTapListener((v, event) -> {
            Toast.makeText(
                    c, "Andy touched.", Toast.LENGTH_LONG)
                    .show();
        });
        return base;
    }
}
