package com.example.iqmma.whathappensinthecity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetailsActivity extends AppCompatActivity {

    TextView datetime, type, location, venue, age, displayName;
    ImageView extendUrl, eventImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        datetime = findViewById(R.id.datetimeDetailActivity);
        type = findViewById(R.id.typeDetailActivity);
        location = findViewById(R.id.locationDetailActivity);
        venue = findViewById(R.id.venueDetailActivity);
        age = findViewById(R.id.ageDetailActivity);
        extendUrl = findViewById(R.id.eventUrlDetail);
        displayName =  findViewById(R.id.displayNameDetailActivity);
        eventImage = findViewById(R.id.imgViewDetailsConcertBanner);

        final Intent intent = getIntent();

        Bundle extrasBundle = intent.getExtras();

        datetime.setText(extrasBundle.getString("DateTime"));
        type.setText(extrasBundle.getString("Type"));
        location.setText(extrasBundle.getString("Location"));
        venue.setText(extrasBundle.getString("Venue"));
        age.setText(extrasBundle.getString("Age"));
        displayName.setText(extrasBundle.getString("EventName"));

        eventImage.setImageBitmap((Bitmap)extrasBundle.getParcelable("ImageEvent"));





        extendUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = intent.getStringExtra("Url");
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url += "http://" + url;

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

    }
}
