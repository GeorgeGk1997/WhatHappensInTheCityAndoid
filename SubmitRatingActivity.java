package com.example.iqmma.whathappensinthecity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class SubmitRatingActivity extends AppCompatActivity {

    String title, snippet;
    String[] arraySnippet, arrayTitle;
    TextView titlePlace, location, totalRatings, totalUsers, todayRatings, todayUsers;
    Button submit;
    RatingBar ratingBar;
    FirebaseUser user;
    DatabaseReference reference, referenceRead;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_rating);

        //get intent extras
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        snippet = intent.getStringExtra("snippet");
        arraySnippet = snippet.split("!");
        arrayTitle = title.split(":");

        titlePlace = findViewById(R.id.titlePlaceRatingAct);
        location = findViewById(R.id.locationGooglePlaceInfoRatingAct);
        totalRatings = findViewById(R.id.totalRatingRatingAct);
        totalUsers = findViewById(R.id.totalUserRatingRatingAct);
        todayRatings = findViewById(R.id.todayRatingRatingAct);
        todayUsers = findViewById(R.id.todayUserRatingRatingAct);

        titlePlace.setText(arrayTitle[0]);
        location.setText(arraySnippet[0]);
        totalRatings.setText(arraySnippet[1]);
        totalUsers.setText(arraySnippet[3]);

        submit = findViewById(R.id.submitRatingRatingAct);
        ratingBar = findViewById(R.id.ratingBarRatingAct);

        user = FirebaseAuth.getInstance().getCurrentUser();

        readRating(arraySnippet[2]);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * */

                Calendar calendarInstance = Calendar.getInstance();
                Date date = new Date();
                calendarInstance.setTimeInMillis(date.getTime());

                writeRatingToFirebase(ratingBar.getRating(), arraySnippet[2], calendarInstance.getTime().toString() );
            }
        });
    }

    public void writeRatingToFirebase(final float rating, String placeRef, final String datetimefirebase){
        reference = FirebaseDatabase.getInstance().getReference("Places")
                .child("Rating")
                .child(placeRef)
                .child(user.getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    Toast.makeText(SubmitRatingActivity.this, "You have voted for today! ", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SubmitRatingActivity.this, "Try Again Tomorrow", Toast.LENGTH_SHORT).show();
                    ratingBar.setRating(Float.parseFloat(todayRatings.getText().toString()));
                }else {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("ratingValue", Float.toString(rating));
                    hashMap.put("datetime", datetimefirebase);
                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                float previousRating = Float.parseFloat(todayRatings.getText().toString());
                                float previousCounterUserRatings = Float.parseFloat(todayUsers.getText().toString());

                                float newRating = rating + previousRating;
                                float newCounter = previousCounterUserRatings + 1;
                                Double finalNew = round((double) newRating / (double) newCounter, 1);

                                todayRatings.setText(Double.toString(finalNew));
                                todayUsers.setText(Float.toString(newCounter));

                                Toast.makeText(SubmitRatingActivity.this, "Your rating was successfully submitted", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void readRating(String placeRef){
        reference = FirebaseDatabase.getInstance().getReference("Places")
                                .child("Rating")
                                .child(placeRef);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            double sumRating;
            double counterRating;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    RatingFirebase ratingFirebase = snapshot.getValue(RatingFirebase.class);
                    String date = ratingFirebase.getDatetime();
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                    try {
                        calendar.setTime(sdf.parse(date));// all done
                        if(DateUtils.isToday(calendar.getTimeInMillis())){
                            float rating = Float.parseFloat(ratingFirebase.getRatingValue());
                            sumRating += (double)rating;
                            counterRating++;
                        }
                    }catch (Exception e){
                        Log.d("fail", e.getMessage());
                    }

                }

                if((int)counterRating!= 0){
                    double average = round(sumRating/counterRating, 1);
                    //ratingBar.setRating(()average);
                    todayRatings.setText(Double.toString(average));
                    todayUsers.setText(Double.toString(counterRating));
                    ratingBar.setRating(Float.parseFloat(todayRatings.getText().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}