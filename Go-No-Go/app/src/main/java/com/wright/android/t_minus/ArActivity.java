//package com.wright.android.t_minus;
//
//import android.content.Context;
//import android.location.Location;
//import android.support.annotation.NonNull;
//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.FrameLayout;
//import android.widget.Toast;
//
//import com.google.android.gms.location.LocationServices;
//import com.google.ar.core.AugmentedImage;
//import com.google.ar.core.Frame;
//import com.google.ar.core.Plane;
//import com.google.ar.core.Session;
//import com.google.ar.core.TrackingState;
//import com.google.ar.core.exceptions.CameraNotAvailableException;
//import com.google.ar.core.exceptions.UnavailableException;
//import com.google.ar.sceneform.ArSceneView;
//import com.google.ar.sceneform.Node;
//import com.google.ar.sceneform.rendering.ModelRenderable;
//import com.google.ar.sceneform.ux.ArFragment;
//
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//
//import uk.co.appoly.arcorelocation.LocationMarker;
//import uk.co.appoly.arcorelocation.LocationScene;
//import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;
//import uk.co.appoly.arcorelocation.utils.LocationUtils;
//
//public class ArActivity extends AppCompatActivity{
//
//    private ArSceneView arSceneView;
//    private ArFragment arFragment;
//    private Session arSession;
//    private Snackbar loadingMessageSnackbar = null;
//    private LocationScene locationScene;
//    private ModelRenderable andyRenderable;
//    private boolean installRequested;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ar);
//        arSceneView = findViewById(R.id.arFrameLayout);
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
//                                andyRenderable = andy.get();
//
//                            } catch (InterruptedException | ExecutionException ex) {
//                                ArUtils.displayError(this, "Unable to load renderables", ex);
//                            }
//                            return null;
//                        });
//
//        DemoUtils.requestCameraPermission(this, 1);
//
//        arSceneView
//                .getScene()
//                .setOnUpdateListener(
//                        frameTime -> {
//                            if (loadingMessageSnackbar == null) {
//                                return;
//                            }
//
//                            Frame frame = arSceneView.getArFrame();
//                            if (frame == null) {
//                                return;
//                            }
//
//                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
//                                return;
//                            }
//
//                            if (locationScene == null) {
//                                locationScene = new LocationScene(this, this, arSceneView);
//                                locationScene.mLocationMarkers.add(
//                                        new LocationMarker(
//                                                locationScene.deviceLocation.currentBestLocation.getLongitude(),
//                                                locationScene.deviceLocation.currentBestLocation.getLatitude(),
//                                                getAndy()));
//
//                            }
//
//                            if (locationScene != null) {
//                                hideLoadingMessage();
//                                locationScene.processFrame(arSceneView.getArFrame());
//                            }
//
//                        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if (arSceneView.getSession() == null) {
//            // If the session wasn't created yet, don't resume rendering.
//            // This can happen if ARCore needs to be updated or permissions are not granted yet.
//            try {
//                Session session = DemoUtils.createArSession(this, installRequested);
//                if (session == null) {
//                    installRequested = DemoUtils.hasCameraPermission(this);
//                    return;
//                } else {
//                    arSceneView.setupSession(session);
//                }
//            } catch (UnavailableException e) {
//                DemoUtils.handleSessionException(this, e);
//            }
//        }
//
//        try {
//            arSceneView.resume();
//        } catch (CameraNotAvailableException ex) {
//            DemoUtils.displayError(this, "Unable to get camera", ex);
//            finish();
//            return;
//        }
//
//        if (arSceneView.getSession() != null) {
//            showLoadingMessage();
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        arSceneView.pause();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        arSceneView.destroy();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(
//            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
//        if (!DemoUtils.hasCameraPermission(this)) {
//            if (!DemoUtils.shouldShowRequestPermissionRationale(this)) {
//                // Permission denied with checking "Do not ask again".
//                DemoUtils.launchPermissionSettings(this);
//            } else {
//                Toast.makeText(
//                        this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
//                        .show();
//            }
//            finish();
//        }
//    }
//
////    private void ar(){
////        if (arSceneView.getSession() == null) {
////            // If the session wasn't created yet, don't resume rendering.
////            // This can happen if ARCore needs to be updated or permissions are not granted yet.
////            try {
////                Session session = ArUtils.createArSession(this, installRequested);
////                if (session == null) {
////                    installRequested = ArUtils.hasCameraPermission(this);
////                    return;
////                } else {
////                    arSceneView.setupSession(session);
////                }
////            } catch (UnavailableException e) {
////                ArUtils.handleSessionException(this, e);
////            }
////        }
////        try {
////            arSceneView.resume();
////        } catch (CameraNotAvailableException | SecurityException ex) {
////            ArUtils.displayError(this, "Unable to get camera", ex);
////            finish();
////            return;
////        }
////    }
//
//    private Node getAndy() {
//        Node base = new Node();
//        base.setParent(arSceneView.getScene());
//        base.setRenderable(andyRenderable);
//        Context c = this;
//        base.setOnTapListener((v, event) -> {
//            Toast.makeText(
//                    c, "Andy touched.", Toast.LENGTH_LONG)
//                    .show();
//        });
//        return base;
//    }
//
//    private void showLoadingMessage() {
//        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
//            return;
//        }
//
//        loadingMessageSnackbar =
//                Snackbar.make(
//                        ArActivity.this.findViewById(android.R.id.content),
//                        "Loading...",
//                        Snackbar.LENGTH_INDEFINITE);
//        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
//        loadingMessageSnackbar.show();
//    }
//
//    private void hideLoadingMessage() {
//        if (loadingMessageSnackbar == null) {
//            return;
//        }
//
//        loadingMessageSnackbar.dismiss();
//        loadingMessageSnackbar = null;
//    }
//}
