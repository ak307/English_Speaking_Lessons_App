package com.akGroup.englishspeakinglessons.api.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.akGroup.englishspeakinglessons.MainActivity;
import com.akGroup.englishspeakinglessons.R;

public class LoadingPageActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIMEOUT = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);

        ImageView imageView = (ImageView) findViewById(R.id.splashImageView);
        TextView textView1 = (TextView) findViewById(R.id.textView10);
        TextView textView2 = (TextView) findViewById(R.id.textview11);

        Animation fadeout = new AlphaAnimation(1, 0);
        fadeout.setInterpolator(new AccelerateInterpolator());
        fadeout.setStartOffset(500);
        fadeout.setDuration(1800);
        imageView.setAnimation(fadeout);
        textView1.setAnimation(fadeout);
        textView2.setAnimation(fadeout);




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingPageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_TIMEOUT);


        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


    }
}