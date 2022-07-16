package com.akGroup.englishspeakinglessons.api.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.api.CheckInternetConnection;
import com.akGroup.englishspeakinglessons.service.AdViewService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

public class ConversationLevelActivity extends AppCompatActivity {
    private AdView adView;
    private CheckInternetConnection checkInternetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converstion_level);


        Button beginnerBtn = (Button) findViewById(R.id.beginnerLevelBtn);
        Button intermediateBtn = (Button) findViewById(R.id.intermediateLevelBtn);
        Button advancedBtn = (Button) findViewById(R.id.advancedLevelBtn);
        adView = (AdView) findViewById(R.id.levelFragmentAdView);
        checkInternetConnection = new CheckInternetConnection();


        beginnerBtnClicked(beginnerBtn);
        intermediateBtnClicked(intermediateBtn);
        advancedBtnClicked(advancedBtn);
        AdViewService.loadAd(adView);
    }


    private void beginnerBtnClicked (Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.checkConnection(getApplicationContext())) {
                    Intent intent = new Intent(ConversationLevelActivity.this, ConversationListActivity.class);
                    intent.putExtra("level", "beginner");
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Check your internet connection.", Toast.LENGTH_LONG).show();
                }




            }
        });
    }


    private void intermediateBtnClicked (Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.checkConnection(getApplicationContext())) {
                    Intent intent = new Intent(ConversationLevelActivity.this, ConversationListActivity.class);
                    intent.putExtra("level", "intermediate");
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Check your internet connection.", Toast.LENGTH_LONG).show();
                }




            }
        });
    }


    private void advancedBtnClicked (Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (checkInternetConnection.checkConnection(getApplicationContext())) {
//                    Intent intent = new Intent(ConversationLevelActivity.this, ConversationListActivity.class);
//                    intent.putExtra("level", "advanced");
//                    startActivity(intent);
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), "Check your internet connection.", Toast.LENGTH_LONG).show();
//                }

                Toast.makeText(getApplicationContext(), "Coming soon...", Toast.LENGTH_LONG).show();



            }
        });
    }

}