package com.akGroup.englishspeakinglessons.api.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.model.Comment;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddNewLessonPostActivity extends AppCompatActivity {
    private Uri videoUrl;
    private static final int VIDEO_PICK_GALLERY_CODE = 100;
    private static final int VIDEO_PICK_CAMERA_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private String[] cameraPermission;
    private Utils utils = new Utils();
    private FirebaseFirestore firebaseFirestore;
    private ConstraintLayout videoConstraintLayout;
    private FirebaseStorage firebaseStorage;
    private ProgressDialog progressDialog;
    private String profile_img;
    private String userName;
    private ImageButton playBtn;
    private VideoView videoView;
    private AdView adView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_lesson_post);

        CircleImageView profilePic = (CircleImageView) findViewById(R.id.addVideoPageCircleProfilePic);
        TextView profileName = (TextView) findViewById(R.id.addVideoPageProfileNameTextView);
        Button uploadBtn = (Button) findViewById(R.id.addVideoPageUploadBtn);
        EditText description = (EditText) findViewById(R.id.addVideoPagePostDescription);
        videoView = (VideoView) findViewById(R.id.addVideoPageVideoImgView);
        playBtn = (ImageButton) findViewById(R.id.addVideoPagePlayBtn);
        videoConstraintLayout = (ConstraintLayout) findViewById(R.id.addVideoPageVideoViewConLayout);
        FloatingActionButton addVideoBtn = (FloatingActionButton) findViewById(R.id.addVideoPageFloatBtn);
        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);
        adView = (AdView) findViewById(R.id.addVideoAdView);


        firebaseFirestore = FirebaseFirestore.getInstance();
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        firebaseStorage = FirebaseStorage.getInstance();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        setUpProgressDialog();
        backBtnClicked(backBtn);
        retrieveAndSetUserData(profilePic, profileName);
        adjustVideoView(videoConstraintLayout);
        addVideoBtnClicked(addVideoBtn, videoView);
        uploadBtnClicked(uploadBtn, description, videoView);
        loadAd();

    }



    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Uploading video.....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
    }


    private void backBtnClicked(ImageButton imageButton){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void retrieveAndSetUserData(CircleImageView profilePic, TextView profileName){
        firebaseFirestore.collection("user_profile_info")
                .document(utils.getUserID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                String uid =  task.getResult().getString("uid");
                                userName =  task.getResult().getString("userName");
                                String email =  task.getResult().getString("email");
                                profile_img = task.getResult().getString("profile_img");
                                String phone = task.getResult().getString("phone");

                                if (!profile_img.equals("")) {
                                    Glide.with(getApplicationContext())
                                            .load(Uri.parse(profile_img))
                                            .into(profilePic);
                                }

                                profileName.setText(userName);


                            }

                        }
                    }
                });
    }


    private void addVideoBtnClicked(FloatingActionButton btn, VideoView videoView){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()){
                    videoView.stopPlayback();
                }

                videoView.clearAnimation();
                videoView.suspend(); // clears media player
                videoView.setVideoURI(null);


                videoPickDialog();
            }
        });
    }


    private void videoPickDialog(){
        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Video From")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0){
                            // camera clicked
                            if (!checkCameraPermission()){
                                requestCameraPermission();
                            }
                            else {
                                videoPickCamera();
                            }
                        }
                        else if (i == 1){
                            // gallery clicked
                            if (!storagePermissionIsGranted()){
                                requestStoragePermission();
                            }
                            else {
                                videoPickGallery();
                            }
                        }
                    }
                })
                .show();
    }

    private void requestStoragePermission() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        String[] permissions = {permission};
        ActivityCompat.requestPermissions(AddNewLessonPostActivity.this, permissions, 1);
    }


    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(AddNewLessonPostActivity.this, cameraPermission, CAMERA_REQUEST_CODE);
    }


    private boolean checkCameraPermission(){
        boolean result1 = ContextCompat.checkSelfPermission
                (this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        boolean result2 = ContextCompat.checkSelfPermission
                (this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;

        return result1 && result2;
    }


    private boolean storagePermissionIsGranted(){
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        return ContextCompat.checkSelfPermission(AddNewLessonPostActivity.this, permission)
                == PackageManager.PERMISSION_GRANTED;
    }


    private void videoPickGallery(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Videos"), VIDEO_PICK_GALLERY_CODE);
    }


    private void videoPickCamera(){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted) {
                        videoPickCamera();
                    }
                    else {
                        Toast.makeText(this, "camera permission are required", Toast.LENGTH_LONG).show();

                    }
                }
                break;
            case 1:
                boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (readExternalStorage) {
                    videoPickGallery();
                }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_REQUEST_CODE){
                videoUrl = data.getData();

                adjustVideoView(videoConstraintLayout);
                videoPlayBtnClicked(playBtn, videoView);


            }
            if(requestCode == VIDEO_PICK_GALLERY_CODE){
                videoUrl = data.getData();

                adjustVideoView(videoConstraintLayout);
                videoPlayBtnClicked(playBtn, videoView);


            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void adjustVideoView(ConstraintLayout videoConstraintLayout){
        if (videoUrl == null)
            videoConstraintLayout.setVisibility(View.GONE);
        else
            videoConstraintLayout.setVisibility(View.VISIBLE);
    }

    private void videoPlayBtnClicked(ImageButton playBtn, VideoView videoView){
        // this is important to show when user choose next video
        playBtn.setVisibility(View.VISIBLE);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playBtn.setVisibility(View.GONE);
                setVideoToVideoView(videoView);

            }
        });
    }



    private void setVideoToVideoView(VideoView videoView){
        videoView.setBackgroundColor(Color.TRANSPARENT);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);


        videoView.setVideoURI(videoUrl);
//        videoView.setZOrderOnTop(true);

//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                videoView.pause();
//            }
//        });
        videoView.start();
    }


    private void uploadBtnClicked(Button uploadBtn, TextView desc, VideoView videoView){
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = desc.getText().toString().trim();

                if (TextUtils.isEmpty(description)){
                    Toast.makeText(AddNewLessonPostActivity.this, "Please write something.", Toast.LENGTH_LONG).show();
                }
                else if (videoUrl == null){
                    Toast.makeText(AddNewLessonPostActivity.this, "Please add lesson video.", Toast.LENGTH_LONG).show();
                }
                else {
                    if (videoView.isPlaying())
                        videoView.stopPlayback();
                    uploadPost(description, videoUrl);
                }

            }
        });
    }


    private void uploadPost(String desc, Uri videoUrl){
        progressDialog.show();
        uploadVideoToFirebase(desc, videoUrl);
    }


    private void uploadVideoToFirebase(String desc, Uri videoUrl) {
        // timestamp
        String timestamp = "" + System.currentTimeMillis();

        // file path and name in firebase storage
        String filePathAndName = "Videos/" + "video_" + timestamp;

        // string reference
        StorageReference storageReference = firebaseStorage.getReference(filePathAndName);
        storageReference.putFile(videoUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getVideoUrl(storageReference, desc);

                    }
                })

                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setProgress((int) progress);
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void getVideoUrl(StorageReference videoPath, String desc){
        videoPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                addVideoInfoAndURlToFirestore(desc, uri);

            }
        });
    }



    private void addVideoInfoAndURlToFirestore(String desc, Uri downloadUri){
        ArrayList<String> likedUserList = new ArrayList<>();
        ArrayList<Comment> commentsList = new ArrayList<>();


        String docId = firebaseFirestore
                .collection("lesson_posts")
                .document()
                .getId();


        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("postedDataAndTime", FieldValue.serverTimestamp());
        hashMap.put("description", desc);
        hashMap.put("profilePicUrl", profile_img);
        hashMap.put("profileName", userName);
        hashMap.put("videoUrl", downloadUri.toString());
        hashMap.put("handShakeCount", 0);
        hashMap.put("postId", docId);
        hashMap.put("likedUserList", likedUserList);
        hashMap.put("commentsList", commentsList);


        firebaseFirestore.collection("lesson_posts")
                .document(docId)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()){
                            updatePostArrayInUserProfileInfo(docId);
                            onBackPressed();
                        }
                        else  {
                            Toast.makeText(getApplicationContext(), "UNSUCCESSFUL", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }


    private void updatePostArrayInUserProfileInfo(String postId){
        firebaseFirestore.collection("user_profile_info")
                .document(utils.getUserID())
                .update("postIds", FieldValue.arrayUnion(postId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       onBackPressed();
                    }
                });
    }



    // if you want to go back to previous fragment from an activity call this
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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






}