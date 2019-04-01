package com.example.iqmma.whathappensinthecity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    ImageView aboutLogo;
    TextView aboutText;

    Animation anim1, anim2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        aboutText = findViewById(R.id.aboutText);
        aboutLogo = findViewById(R.id.imgAboutBannerAboutActivity);

        anim1 = AnimationUtils.loadAnimation(this, R.anim.slider1);
        anim2 = AnimationUtils.loadAnimation(this, R.anim.slider2);

        aboutLogo.setAnimation(anim1);
        aboutText.setAnimation(anim2);

    }
}
