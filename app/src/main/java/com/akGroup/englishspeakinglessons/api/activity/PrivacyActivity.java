package com.akGroup.englishspeakinglessons.api.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebView;

import com.akGroup.englishspeakinglessons.R;

public class PrivacyActivity extends AppCompatActivity {
    private WebView webView;
    private String fileUrl = "https://speakandmeet572.blogspot.com/2022/01/privacy-policy-ak-built-speak-and-meet.html";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        webView = (WebView) findViewById(R.id.privacyWebView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(fileUrl);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}