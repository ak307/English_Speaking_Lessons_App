package com.akGroup.englishspeakinglessons.api.fragment;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.adapter.SavedPracticeConversationListviewAdapter;
import com.akGroup.englishspeakinglessons.databaseHelper.SavedPracticeAudioDatabaseHelper;
import com.akGroup.englishspeakinglessons.model.PracticeConversationSaved;
import com.akGroup.englishspeakinglessons.service.AdViewService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class SaveFragment extends Fragment {
    private ArrayList<PracticeConversationSaved> arrayList = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private Utils utils = new Utils();
    private SavedPracticeAudioDatabaseHelper savedPracticeAudioDatabaseHelper;
    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private Handler handler = new Handler();
    private AdView adView;


    public SaveFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_save, container, false);

        ListView listView = (ListView) view.findViewById(R.id.saveListView);
        adView = (AdView) view.findViewById(R.id.saveFragmentAdView);

        savedPracticeAudioDatabaseHelper = new SavedPracticeAudioDatabaseHelper(getContext());


        firebaseFirestore = FirebaseFirestore.getInstance();

//        retrieveRecordedData(listView);
        getAllSavedLessonsData(listView);
        listViewOnItemClicked(listView);
        AdViewService.loadAd(adView);


        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        return view;
    }


    private void retrieveRecordedData(ListView listView){
        firebaseFirestore
                .collection("user_profile_info")
                .document(utils.getUserID())
                .collection("savedPracticeConversations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                String conversationWith = doc.getString("conversationWith");
                                String practiceConversationUrl = doc.getString("practiceConversationUrl");
                                String docId = doc.getString("docId");


                                arrayList.add(new PracticeConversationSaved(conversationWith, practiceConversationUrl, docId));
                            }

                            setListviewAdapter(listView);
                        }
                    }
                });
    }


    private void getAllSavedLessonsData(ListView listView){
        arrayList.clear();
        Cursor data = savedPracticeAudioDatabaseHelper.getAllData();

        if (data.moveToFirst()) {
            do {
                String url = data.getString(1);
                String con_with = data.getString(2);
                String dateTime = data.getString(3);


                arrayList.add(new PracticeConversationSaved(con_with, url, dateTime));
                // get the data into array, or class variable
            } while (data.moveToNext());
        }
        setListviewAdapter(listView);
        data.close();
    }


    private void listViewOnItemClicked(ListView listView){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAlertDialog(position);
            }
        });
    }


    private void showAlertDialog(int position){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_audioplayer_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        SeekBar seekBar = dialog.findViewById(R.id.popupPlayerSeekBar);
        ImageView playBtn = dialog.findViewById(R.id.popupPlayerPlayBtn);
        ImageView pauseBtn = dialog.findViewById(R.id.popupPlayerPauseBtn);
        Button closePlayerBtn = dialog.findViewById(R.id.closePlayerBtn);



        closePlayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (mediaPlayer != null)
                   if (mediaPlayer.isPlaying())
                       mediaPlayer.stop();

               dialog.dismiss();
            }
        });


        initializeMediaPlayer(Uri.parse(arrayList.get(position).getSavedConversationUrl()), seekBar, playBtn, pauseBtn);



        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }



    private void initializeMediaPlayer(Uri audioPath, SeekBar seekBar, ImageView btPlay, ImageView btPause){

        try{
            mediaPlayer = MediaPlayer.create(getContext(), audioPath);
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




    private void setListviewAdapter(ListView listView){
        SavedPracticeConversationListviewAdapter adapter = new SavedPracticeConversationListviewAdapter(getContext(), arrayList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



}