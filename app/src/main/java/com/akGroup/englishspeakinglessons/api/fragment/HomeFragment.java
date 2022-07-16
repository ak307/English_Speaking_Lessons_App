package com.akGroup.englishspeakinglessons.api.fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.api.activity.ConversationLevelActivity;
import com.akGroup.englishspeakinglessons.api.activity.ConversationListActivity;
import com.akGroup.englishspeakinglessons.api.activity.LearningResourcesActivity;
import com.akGroup.englishspeakinglessons.service.AdViewService;
import com.google.android.exoplayer2.C;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;


public class HomeFragment extends Fragment {
    private AdView adView;



    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        CardView conversationPracticeCardView = (CardView) view.findViewById(R.id.conversationPracticeCardView);
        CardView learningResourcesCardView = (CardView) view.findViewById(R.id.learningResourcesCardView);
        Button conversationPracticeBtn = (Button) view.findViewById(R.id.conversationPracticeBtn);
        Button learningResourcesBtn = (Button) view.findViewById(R.id.learningResourcesBtn);
        adView = (AdView) view.findViewById(R.id.homeFragmentAdView);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);




        conversationPracticeBtnClicked(conversationPracticeBtn, conversationPracticeCardView);
        learningResourcesBtnClicked(learningResourcesBtn, learningResourcesCardView);
        AdViewService.loadAd(adView);

        return view;
    }

    private void conversationPracticeBtnClicked(Button conversationPracticeBtn, CardView conversationPracticeCardView){
        conversationPracticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ConversationLevelActivity.class);
                startActivity(intent);
            }
        });

        conversationPracticeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ConversationLevelActivity.class);
                startActivity(intent);
            }
        });
    }

    private void learningResourcesBtnClicked(Button btn, CardView learningResourcesCardView){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LearningResourcesActivity.class);
                startActivity(intent);            }
        });


        learningResourcesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LearningResourcesActivity.class);
                startActivity(intent);
            }
        });
    }


}