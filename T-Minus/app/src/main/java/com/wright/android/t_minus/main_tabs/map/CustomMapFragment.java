package com.wright.android.t_minus.main_tabs.map;

import android.Manifest;
import android.app.AlertDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wright.android.t_minus.objects.LaunchPad;
import com.wright.android.t_minus.objects.PadLocation;
import com.wright.android.t_minus.objects.ViewingLocation;
import com.wright.android.t_minus.R;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;
import static com.wright.android.t_minus.main_tabs.map.MapFrag.MY_PERMISSIONS_REQUEST_LOCATION;

public class CustomMapFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, LocationListener {
    public static final String TAG = "Mapfragment.TAG";
    private ArrayList<PadLocation> padLocations;
    private ArrayList<ViewingLocation> viewingLocations = new ArrayList<>();
    private LocationManager locMgr;
    private GoogleMap mMap;
    private MapBaseFragment parentFrag;

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getMapAsync(this);
        parentFrag = (MapBaseFragment) getParentFragment();
        locMgr = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(LOCATION_SERVICE);
    }

    public void setData(ArrayList<PadLocation> _padLocations){
        padLocations = _padLocations;
        addMapMarkers();
    }

    public void addViewingLocation(ViewingLocation viewingLocation){
        viewingLocations.add(viewingLocation);
        addViewingMarkers();
    }

    public void removeViewingLocation(String key){
        for(ViewingLocation loc: viewingLocations){
            if(loc.getId().equals(key)){
                viewingLocations.remove(loc);
            }
        }
        addViewingMarkers();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(getContext()==null){
            return;
        }
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if(!locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            new android.support.v7.app.AlertDialog.Builder(getContext())
                    .setTitle("Location Service Is Off")
                    .setMessage("Some features on the map are disabled while locations service is turned off. Go into your devices setting to re-enable location services.")
                    .setPositiveButton("Okay", null)
                    .create()
                    .show();
        }else if(parentFrag.checkLocationPermission()){
            mMap.setLocationSource(new CurrentLocationProvider(getContext()));
            locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 100, this);
            mMap.setMyLocationEnabled(true);
            zoomInCamera();
        }
        addMapMarkers();
    }

    private void zoomInCamera(){
        if(mMap == null){
        }else
            if(parentFrag.checkLocationPermission()) {
            Location loc = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(loc == null){
                loc = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            LatLng officeLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
            CameraUpdate cameraMovement = CameraUpdateFactory.newLatLngZoom(officeLocation, 200);
            mMap.animateCamera(cameraMovement);
        }
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
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.addMarker(markerOptions);
            }
        }
    }

    private void addViewingMarkers(){
        if(mMap == null||viewingLocations==null){
            return;
        }

        for(ViewingLocation viewingLocation : viewingLocations){
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng officeLocation = new LatLng(viewingLocation.getLatitude(), viewingLocation.getLongitude());
            markerOptions.position(officeLocation);
            markerOptions.title(viewingLocation.getName());
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(markerOptions);
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
}
