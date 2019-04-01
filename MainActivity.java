package com.example.iqmma.whathappensinthecity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private ImageView bg;
    private TextView txtGreetingUser, logout;
    private ImageView clover, tabProfile, tabEvents, tabPlaces, tabAbout;
    private Animation frombottom;
    private LinearLayout llWelcome, llHome, llMenu;
    private GestureDetectorCompat detector;

    private FirebaseUser userF;
    private DatabaseReference reference;
    private String userId;

    //credentials
    public static String usernameCred;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bg = findViewById(R.id.imgViewBg);
        clover = findViewById(R.id.imgViewCl);
        llWelcome = findViewById(R.id.llWelcome);
        llHome = findViewById(R.id.llHome);
        llMenu = findViewById(R.id.llMenu);
        txtGreetingUser = findViewById(R.id.txtGreetingUsername);
        tabPlaces = findViewById(R.id.imgTabPlaces);
        logout = findViewById(R.id.logout);

        tabProfile = findViewById(R.id.imgTabProfile);
        tabAbout = findViewById(R.id.imgTabAbout);
        tabEvents = findViewById(R.id.imgTabEvents);

        llMenu.setVisibility(LinearLayout.GONE);
        llHome.setVisibility(LinearLayout.GONE);
        txtGreetingUser.setVisibility(View.GONE);

        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);

        detector = new GestureDetectorCompat(this, this);


        //Listeners for changing Tabs from main dashboard
        tabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goTo(2);
            }
        });

        tabAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(3);
            }
        });

        tabEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(0);
            }
        });

        tabPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(1);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });


    }

    //Writing here the network based code
    //to minimize app freezing
    @Override
    protected void onStart() {
        super.onStart();

        userF = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userF.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //Set Credentials
                usernameCred = user.getUsername();

                //Set Greeting User Text
                txtGreetingUser = findViewById(R.id.txtGreetingUsername);
                txtGreetingUser.setText("Mr/Mss " + usernameCred);
                txtGreetingUser.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Change Tabs on main Dashboard
    public void goTo(int pointerTab){
        if(pointerTab == 0)
            startActivity(new Intent(this, EventsActivity.class) );
        else if(pointerTab == 1)
            startActivity(new Intent(this, GoogleMapsActivity.class));
        else if(pointerTab == 2)
            startActivity(new Intent(this, ProfileActivity.class) );
        else
            startActivity(new Intent(this, AboutActivity.class));

    }


    //Swipe up Kinda
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        bg.animate()
                .translationY(-1500)
                .setDuration(1000)
                .setStartDelay(1100);
        clover.animate()
                .alpha(0)
                .setDuration(1000)
                .setStartDelay(400);

        llWelcome.animate()
                .translationY(200)
                .alpha(0)
                .setDuration(800)
                .setStartDelay(400);

        llMenu.setVisibility(LinearLayout.VISIBLE);
        llHome.setVisibility(LinearLayout.VISIBLE);
        llHome.startAnimation(frombottom);
        llMenu.startAnimation(frombottom);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }
}
