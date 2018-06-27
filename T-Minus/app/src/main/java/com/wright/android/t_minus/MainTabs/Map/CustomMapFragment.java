package com.wright.android.t_minus.MainTabs.Map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wright.android.t_minus.ArActivity;
import com.wright.android.t_minus.Objects.LaunchPad;
import com.wright.android.t_minus.Objects.PadLocation;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.networkConnection.GetPadsFromAPI;
import com.wright.android.t_minus.networkConnection.NetworkUtils;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

public class CustomMapFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, LocationListener {
    public static final String TAG = "Mapfragment.TAG";
    private ArrayList<PadLocation> padLocations;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private LocationManager locMgr;


    private GoogleMap mMap;

    public static CustomMapFragment newInstance() {
        return new CustomMapFragment();
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getMapAsync(this);
        locMgr = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
    }

    public void setData(ArrayList<PadLocation> _padLocations){
        padLocations = _padLocations;
        addMapMarkers();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);
        if(checkLocationPermission()){
            LocationSource locationSource = new LocationSource() {
                @Override
                public void activate(OnLocationChangedListener onLocationChangedListener) {

                }

                @Override
                public void deactivate() {

                }
            };
            locationSource.activate(this::onLocationChanged);
            mMap.setLocationSource(locationSource);
            try {
                locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            mMap.setMyLocationEnabled(true);
            zoomInCamera();
        }
        addMapMarkers();
    }

    private void zoomInCamera(){
        if(mMap == null){
            return;
        }
        LatLng officeLocation = new LatLng(28.474009,-80.577174);
        CameraUpdate cameraMovement = CameraUpdateFactory.newLatLngZoom(officeLocation,100);
        mMap.animateCamera(cameraMovement);
    }

    private void addMapMarkers(){
        if(mMap == null||padLocations==null){
            return;
        }

        for(PadLocation padLocation : padLocations){
            for(LaunchPad launchPad : padLocation.getLaunchPads()) {
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng officeLocation = new LatLng(launchPad.getLatitude(), launchPad.getLongitude());
                markerOptions.position(officeLocation);
                markerOptions.title(launchPad.getName());
                markerOptions.snippet("Pad Name: "+padLocation.getName());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMap.addMarker(markerOptions);
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
//        View contents = LayoutInflater.from(getActivity()).inflate(R.layout.info_window,null);
//        ((TextView)contents.findViewById(R.id.title)).setText(marker.getTitle());
//        ((TextView)contents.findViewById(R.id.snippet)).setText(marker.getSnippet());
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        new AlertDialog.Builder(getActivity()).setTitle(marker.getTitle())
                .setMessage(marker.getSnippet())
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private boolean checkLocationPermission() {
        if(getContext()==null||getActivity()==null){
            return false;
        }
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new android.support.v7.app.AlertDialog.Builder(getContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("Please allow location permission for AR viewer")
                        .setPositiveButton("Okay", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if(getContext()==null){
            return;
        }
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        getMapAsync(this);
                    }

                } else {
                    Snackbar snack = Snackbar.make(getView(), "Location permission is needed for map", Snackbar.LENGTH_SHORT);
                    snack.show();
                }
            }
        }
    }
}
