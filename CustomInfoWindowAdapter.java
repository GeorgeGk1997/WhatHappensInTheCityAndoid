package com.example.iqmma.whathappensinthecity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {

        this.mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);
    }

    private void renderWindow(Marker marker, View view){
        String title = marker.getTitle();


        TextView titleTextView = (TextView) view.findViewById(R.id.titlePlace);
        TextView location = (TextView) view.findViewById(R.id.locationGooglePlaceInfo);
        TextView totalRatings = (TextView) view.findViewById(R.id.totalRating);
        TextView totalUserRatings = (TextView) view.findViewById(R.id.totalUserRating);
        String[] arraySnippet;

        if(!title.isEmpty())
            titleTextView.setText(title);
        if(!marker.getSnippet().isEmpty()){
            String snippet = marker.getSnippet();
            arraySnippet = snippet.split("!");
            location.setText(arraySnippet[0]);
            totalRatings.setText(arraySnippet[1]);
            totalUserRatings.setText(arraySnippet[3]);
        }

        if(!title.isEmpty())
            titleTextView.setText(title);


    }
    @Override
    public View getInfoWindow(Marker marker) {
        renderWindow(marker,mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindow(marker, mWindow);
        return mWindow;
    }


}
