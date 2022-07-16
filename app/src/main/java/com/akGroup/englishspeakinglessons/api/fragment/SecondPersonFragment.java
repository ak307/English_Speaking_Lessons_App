package com.akGroup.englishspeakinglessons.api.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.databaseHelper.SavedPracticeAudioDatabaseHelper;
import com.akGroup.englishspeakinglessons.model.Conversation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;



public class SecondPersonFragment extends Fragment {
    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private Handler handler = new Handler();
    private String audioPath;
    private FirebaseFirestore firebaseFirestore;
    private Boolean isRecording = false;
    private final Utils utils = new Utils();
    private boolean isPause = false;
    private MediaRecorder recorder;
    private File audiofile = null;
    private ImageView playBtn;
    private ImageView pauseBtn;
    private SeekBar seekBar;
    private SavedPracticeAudioDatabaseHelper savedPracticeAudioDatabaseHelper;
    private  String colorCodeStart = "<br> <br> <font color='#999999'>";  // use any color as  your want
    private  String colorCodeEnd = "</font> <br> <br>";
    private String yourNewConString;


    public SecondPersonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_second_person, container, false);

        TextView conversationTextView = (TextView) view.findViewById(R.id.secondPersonConversationTextview);
        seekBar = (SeekBar) view.findViewById(R.id.secondPersonSeekBar);
        playBtn = (ImageView) view.findViewById(R.id.secondPersonBtn_play);
        pauseBtn = (ImageView) view.findViewById(R.id.secondPersonBtn_pause);

        firebaseFirestore = FirebaseFirestore.getInstance();
        savedPracticeAudioDatabaseHelper = new SavedPracticeAudioDatabaseHelper(getContext());
        mediaPlayer = new MediaPlayer();

        addConversationData(conversationTextView);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeMediaPlayer(conversationData().getSecondPersonConversationUrl(), seekBar, playBtn, pauseBtn);
            }
        }, 300);


        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        return view;
    }




    private void addConversationData(TextView textView){
        String conversationText = conversationData().getSecondPersonConversationText();

        yourNewConString = conversationText.replace("<(>",colorCodeStart); // <(> and <)> are different replace them with your color code String, First one with start tag
        yourNewConString = yourNewConString.replace("<)>",colorCodeEnd);


        textView.setText(Html.fromHtml(yourNewConString));

        textView.setMovementMethod(new ScrollingMovementMethod());

    }



    private void initializeMediaPlayer(String audioPath, SeekBar seekBar, ImageView btPlay, ImageView btPause){
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
                if (!isRecordingPermitted()){
                    requestRecordingPermission();
                }
                else {

                    btPlay.setVisibility(View.GONE);
                    btPause.setVisibility(View.VISIBLE);

                    mediaPlayer.start();

                    if (isPause)
                        resumeRecording();
                    else
                        startRecording();

                }

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
//                    pauseRecording();
                    stopRecording();

                }
                else {
                    stopRecordingAndDeleteFile();
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

                stopRecording();

                mediaPlayer.seekTo(0);
            }
        });
    }


    private boolean isRecordingPermitted(){
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }


    private void requestRecordingPermission(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 111);
    }



    private void startRecording(){
        setUpRecording();

        try {
            recorder.prepare();
            recorder.start();
            Toast.makeText(getContext(), "Recording...", Toast.LENGTH_LONG).show();


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @SuppressLint("WrongConstant")
    private void setUpRecording(){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        getRecordingFilePath();

    }


    private void getRecordingFilePath(){
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File recordDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(recordDirectory, System.currentTimeMillis()  + ".mp3");

//        if (!file.exists())
//            file.mkdirs();

        audioPath = file.getPath();
        recorder.setOutputFile(file.getPath());
    }



    private void stopRecording(){
        try {
            recorder.stop();
            recorder.release();

            showAlertDialog();

        }
        catch(RuntimeException stopException) {
            File file = new File(audioPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }


    private void showAlertDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.alert_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView warningText = dialog.findViewById(R.id.warningText);
        Button noBtn = dialog.findViewById(R.id.cancelBtn);
        Button yesBtn = dialog.findViewById(R.id.deleteBtn);

        warningText.setText("Do you want to save recording?");
        yesBtn.setText("Yes");
        noBtn.setText("No");

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss aaa");
                String dateTime = simpleDateFormat.format(calendar.getTime());

                Toast.makeText(getContext(), "saved.", Toast.LENGTH_LONG).show();
                savedPracticeAudioDatabaseHelper.addData(audioPath, conversationData().getFirstPersonConversationBy(), dateTime);

                dialog.dismiss();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(audioPath);
                if (file.exists()) {
                    file.delete();
                }

                // to restart media player from the beginning
                mediaPlayer.seekTo(0);

                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }



    private void stopRecordingAndDeleteFile(){
        try {
            recorder.stop();
            recorder.release();
            File file = new File(audioPath);
            if (file.exists()) {
                file.delete();
            }
        }
        catch(RuntimeException stopException) {

        }

    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pauseRecording(){
        recorder.pause();
        isPause = true;
        Toast.makeText(getContext(), "pause recording", Toast.LENGTH_SHORT).show();

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void resumeRecording(){
        recorder.resume();
        isPause = false;
        Toast.makeText(getContext(), "resume recording", Toast.LENGTH_SHORT).show();
    }



    private Conversation conversationData(){
        return getDataFromConversationListActivity();
    }


    private Conversation getDataFromConversationListActivity(){
        return getArguments().getParcelable("bundle_key");
    }



    @Override
    public void onPause() {
        pauseBtn.performClick();
        stopRecordingAndDeleteFile();
        isPause = false;
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


}