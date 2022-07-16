package com.akGroup.englishspeakinglessons.api.activity;

import androidx.appcompat.app.AppCompatActivity;
import com.akGroup.englishspeakinglessons.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class LearningResourcesActivity extends AppCompatActivity {
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_resources);


        LinearLayout layout1 = (LinearLayout) findViewById(R.id.LLayout1);
        LinearLayout layout2 = (LinearLayout) findViewById(R.id.LLayout2);
        LinearLayout layout3 = (LinearLayout) findViewById(R.id.LLayout3);
        LinearLayout layout4 = (LinearLayout) findViewById(R.id.LLayout4);
        LinearLayout layout5 = (LinearLayout) findViewById(R.id.LLayout5);
        LinearLayout layout6 = (LinearLayout) findViewById(R.id.LLayout6);
        LinearLayout layout7 = (LinearLayout) findViewById(R.id.LLayout7);
        LinearLayout layout8 = (LinearLayout) findViewById(R.id.LLayout8);
        LinearLayout layout9 = (LinearLayout) findViewById(R.id.LLayout9);
        adView = (AdView) findViewById(R.id.resourcesFragmentAdView);





        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToUrl("https://www.englishcoachchad.com/");
            }
        });


        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToUrl("https://www.youtube.com/c/EnglishCoachChad");
            }
        });


        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToUrl("https://www.mmmenglish.com");
            }
        });



        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToUrl("https://www.youtube.com/channel/UCrRiVfHqBIIvSgKmgnSY66g");
            }
        });


        layout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToUrl("https://www.englishclass101.com/?src=youtube_comp_easy_conversations_yt_desc_(homepage)&utm_medium=yt_desc&utm_content=yt_desc_(homepage)&utm_campaign=comp_easy_conversations&utm_term=(not-set)&utm_source=youtube");
            }
        });



        layout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToUrl("https://www.youtube.com/channel/UCeTVoczn9NOZA9blls3YgUg");
            }
        });



        layout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToUrl("https://www.youtube.com/channel/UCXtMjo8xJqjEhS4A9KUY8GA");
            }
        });



        layout8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToUrl("https://speakenglishwithvanessa.com");
            }
        });



        layout9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToUrl("https://www.youtube.com/channel/UCxJGMJbjokfnr2-s4_RXPxQ");
            }
        });



        loadAd();


    }


    private void loadAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }


            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }



    private void navigateToUrl(String url){
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}