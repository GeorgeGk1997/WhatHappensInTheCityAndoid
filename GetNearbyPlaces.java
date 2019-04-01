package com.example.iqmma.whathappensinthecity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {

    private String googlePlaceData, url;
    private GoogleMap mMap;
    private Context context;

    @Override
    protected String doInBackground(Object... objects) {
        mMap =(GoogleMap) objects[0];
        url = (String) objects[1];
        context = (Context) objects[2];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData = downloadUrl.ReadTheUrl(url);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String s) {

        List<HashMap<String, String>> nearByPlacesList= null;

        DataParser dataParser = new DataParser();
        nearByPlacesList = dataParser.parseString(s);

        DisplayNearByPlaces(nearByPlacesList);

    }

    private void DisplayNearByPlaces(List<HashMap<String, String>> nearbyPlacesList){

        for ( int i=0 ; i<nearbyPlacesList.size(); i++){
            final MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googleNearbyPlaces = nearbyPlacesList.get(i);
            String nameOfPlace = googleNearbyPlaces.get("place_name");
            String vicinity = googleNearbyPlaces.get("vicinity");

            if( googleNearbyPlaces.get("lat") == null ||
                    googleNearbyPlaces.get("lng") == null)
                continue;

            Double lat = Double.parseDouble(googleNearbyPlaces.get("lat"));
            Double lng = Double.parseDouble(googleNearbyPlaces.get("lng"));
            String reference = googleNearbyPlaces.get("reference");
            String totalRating = googleNearbyPlaces.get("totalRating");
            String totalUserRating = googleNearbyPlaces.get("totalUserRating");

            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(nameOfPlace + " : " + vicinity );
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markerOptions.snippet(vicinity +"!"+totalRating+"!"+reference+"!"+totalUserRating+"!");

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(context));
        }
    }
}
