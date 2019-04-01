package com.example.iqmma.whathappensinthecity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class GoogleMapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int REQUEST_USER_LOCATION_CODE = 99;

    private Double latitude, longtitude;
    private int PROXIMITY_RADIUS =10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkUserPermissionLocation();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(GoogleMapsActivity.this, SubmitRatingActivity.class);
                intent.putExtra("title", marker.getTitle());
                intent.putExtra("snippet", marker.getSnippet());
                startActivity(intent);
            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            buuldGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buuldGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longtitude = location.getLongitude();

        lastLocation = location;

        if(currentUserLocationMarker != null)
        {
            currentUserLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        /*MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.addMarker(markerOptions);*/
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

        if (googleApiClient != null )
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);



    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onClick(View view){

        String hospital = "hospital";
        String school = "school";
        String restaurant = "restaurant";

        Object transferData[] = new Object[3]; // [1] it will be the map [2] the url
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();




        switch (view.getId()){
            case R.id.searchPlaces:
                EditText addressField = findViewById(R.id.placeSearchBar);
                String address = addressField.getText().toString();

                MarkerOptions userMarkerOptions = new MarkerOptions();
                List<Address> addressList = null;
                if (!TextUtils.isEmpty(address)){
                    Geocoder geocoder = new Geocoder(this);

                    try {
                        addressList = geocoder.getFromLocationName(address, 6);

                        if(addressList != null ){
                            for (int i=0; i<addressList.size(); i++){
                                Address userAddress = addressList.get(i);
                                String lat = Double.toString(userAddress.getLatitude());
                                String lng = Double.toString(userAddress.getLongitude());
                                LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());
                                userMarkerOptions.position(latLng);
                                userMarkerOptions.title(address);
                                userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                userMarkerOptions.snippet(userAddress.getCountryCode()+", "+userAddress.getPostalCode()
                                                            +"!0!"+lat.replace(".","")+lng.replace(".","")
                                                            +"!0!");


                                mMap.addMarker(userMarkerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                            }
                        }
                        else
                            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(this, "Please write a valid location!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.hospitals:
                mMap.clear();
                String url = getUrl(latitude, longtitude, hospital);
                transferData[0] = mMap;
                transferData[1] = url;
                transferData[2] = getApplicationContext();

                getNearbyPlaces.execute(transferData);
                Toast.makeText(this, "Searching for NearBy Hospitals...", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing NearBy Hospitals...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.schools:
                mMap.clear();
                url = getUrl(latitude, longtitude, school);
                transferData[0] = mMap;
                transferData[1] = url;
                transferData[2] = getApplicationContext();

                getNearbyPlaces.execute(transferData);
                Toast.makeText(this, "Searching for NearBy Schools...", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing NearBy Schools...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.restaurants:
                mMap.clear();
                url = getUrl(latitude, longtitude, restaurant);
                transferData[0] = mMap;
                transferData[1] = url;
                transferData[2] = getApplicationContext();

                getNearbyPlaces.execute(transferData);
                Toast.makeText(this, "Searching for NearBy Restaurants...", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing NearBy Restaurants...", Toast.LENGTH_SHORT).show();
                break;

        }

    }



    public boolean checkUserPermissionLocation(){
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_USER_LOCATION_CODE);
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_USER_LOCATION_CODE);

            return false;
        }
        else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_USER_LOCATION_CODE:
                if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(googleApiClient == null)
                            buuldGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else{
                    Toast.makeText(this, "Permission is not Granted", Toast.LENGTH_SHORT).show();
                }
                return;
        }

    }
    private String getUrl(double latitude, double longtitude, String nearbyPlace){
        StringBuilder googleURL  = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + ","+ longtitude);
        googleURL.append("&radius="+ PROXIMITY_RADIUS);
        googleURL.append("&type="+ nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key="+ "AIzaSyCciXnO0dA13cwsLklcqB-kCWOPa20FL1c");

        Log.d("google_maps_activity", "getUrl: " + googleURL.toString());

        return googleURL.toString();
    }
}
