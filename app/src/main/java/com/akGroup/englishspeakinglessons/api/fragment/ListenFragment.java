package com.akGroup.englishspeakinglessons.api.fragment;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.model.Conversation;
import com.akGroup.englishspeakinglessons.service.AdViewService;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import org.w3c.dom.Text;

import java.io.IOException;


public class ListenFragment extends Fragment {
    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private Handler handler = new Handler();
    private  String normalParagraphStart = "<br>";  // use any color as  your want
    private  String normalParagraphEnd = "<br> ";
    private String yourNewConString;
    private AdView adView;



    public ListenFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listen, container, false);

        ImageView titleImgView = (ImageView) view.findViewById(R.id.listenPageConversationImgView);
        TextView title = (TextView) view.findViewById(R.id.listenPageConversationTitle);

        TextView conversationTextView = (TextView) view.findViewById(R.id.listenPageConversationText);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.listenPageSeekBar);
        ImageView playBtn = (ImageView) view.findViewById(R.id.listenPageBtn_play);
        ImageView pauseBtn = (ImageView) view.findViewById(R.id.listenPageBtn_pause);
        TextView conversationByTextView = (TextView) view.findViewById(R.id.conversationByTextListenPage);
        adView = (AdView) view.findViewById(R.id.listenFragmentAdView);


        conversationByTextView.setText(conversationData().getConversationBy());
        addDataToTitle(title);
        addDataToTitleImg(titleImgView);
        addConversationData(conversationTextView);
        AdViewService.loadAd(adView);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeMediaPlayer(conversationData().getFullConversationUrl(), seekBar, playBtn, pauseBtn);
            }
        }, 500);


        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        return view;
    }


    private void addDataToTitle(TextView title){
        title.setText(conversationData().getTitle());

    }

    private void addDataToTitleImg(ImageView imageView){
        if (!conversationData().getImageUrl().equals("")) {
            Glide.with(getContext())
                    .load(Uri.parse(conversationData().getImageUrl()))
                    .into(imageView);
        }
    }

    private void addConversationData(TextView textView){
        String conversationText = conversationData().getFullConversationText();

        yourNewConString = conversationText.replace("<>",normalParagraphStart); // <(> and <)> are different replace them with your color code String, First one with start tag
        yourNewConString = yourNewConString.replace("<>",normalParagraphEnd);


        textView.setText(Html.fromHtml(yourNewConString));
        textView.setMovementMethod(new ScrollingMovementMethod());

    }


    private Conversation conversationData(){
        return getDataFromConversationListActivity();
    }


    private Conversation getDataFromConversationListActivity(){
        return getArguments().getParcelable("bundle_key");
    }




    private void initializeMediaPlayer(String audioPath, SeekBar seekBar, ImageView btPlay, ImageView btPause){
        mediaPlayer = new MediaPlayer();

        try{
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();
        }
        catch(Exception e){e.printStackTrace();}


        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                // handler post delay for 0.5 second
                handler.postDelayed(this::run, 500);
            }
        };


        btPlay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                btPlay.setVisibility(View.GONE);
                btPause.setVisibility(View.VISIBLE);


                mediaPlayer.start();


                seekBar.setMax(mediaPlayer.getDuration());
                handler.postDelayed(runnable, 0);
            }
        });


        btPause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                btPause.setVisibility(View.GONE);
                btPlay.setVisibility(View.VISIBLE);


                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }

                handler.removeCallbacks(runnable);
            }
        });



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    // when drag the seek bar
                    // set progress on seek bar
                    mediaPlayer.seekTo(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // hide pause btn
                btPause.setVisibility(View.GONE);
                btPlay.setVisibility(View.VISIBLE);


                mediaPlayer.seekTo(0);
            }
        });
    }




    @Override
    public void onPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}