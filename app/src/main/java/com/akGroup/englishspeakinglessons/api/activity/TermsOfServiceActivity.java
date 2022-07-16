package com.akGroup.englishspeakinglessons.api.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebView;

import com.akGroup.englishspeakinglessons.R;

public class TermsOfServiceActivity extends AppCompatActivity {
    private WebView webView;
    private String fileUrl = "https://speakandmeet572.blogspot.com/2022/01/terms-conditions-by-downloading-or.html";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_service);


        webView = (WebView) findViewById(R.id.termOfServiceWebView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(fileUrl);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


    }
}