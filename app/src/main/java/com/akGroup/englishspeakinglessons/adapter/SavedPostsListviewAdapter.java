package com.akGroup.englishspeakinglessons.adapter;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.api.activity.PlayVideoActivity;
import com.akGroup.englishspeakinglessons.model.LessonsPost;
import com.akGroup.englishspeakinglessons.model.PracticeConversationSaved;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SavedPostsListviewAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<LessonsPost> lessonsPostArrayList;
    private FirebaseFirestore firebaseFirestore;
    private Utils utils = new Utils();


    public SavedPostsListviewAdapter(Context context, ArrayList<LessonsPost> lessonPostsArrayList) {
        this.context = context;
        this.lessonsPostArrayList = lessonPostsArrayList;
    }


    @Override
    public int getCount() {
        return lessonsPostArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return lessonsPostArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.saved_lesson_posts_layout, parent, false);

        }

        CircleImageView profilePic = (CircleImageView) convertView.findViewById(R.id.SavedCircleProfilePic);
        TextView profileNameTextview = (TextView) convertView.findViewById(R.id.SavedPosterProfileNameTextView);
        TextView postedDateTextview = (TextView) convertView.findViewById(R.id.SavedPostTitleDate);
        ImageButton menuBtn = (ImageButton) convertView.findViewById(R.id.SavedPostMoreBtn);
        TextView descriptionTextview = (TextView) convertView.findViewById(R.id.SavedPostDescription);
        VideoView videoView = (VideoView) convertView.findViewById(R.id.SavedAddVideoActivityVideoView);
        ImageButton playVideoBtn = (ImageButton) convertView.findViewById(R.id.SavedAddVideoActivityPlayBtn);
        ImageView downloadBtn = (ImageView) convertView.findViewById(R.id.SavedPostDownloadBtn);

        firebaseFirestore = FirebaseFirestore.getInstance();




        String profilePicUrl = lessonsPostArrayList.get(position).getProfilePicUrl();
        if (!profilePicUrl.equals("")) {
            Glide.with(context.getApplicationContext())
                    .load(Uri.parse(profilePicUrl))
                    .into(profilePic);
        }

        profileNameTextview.setText(lessonsPostArrayList.get(position).getProfileName());
        postedDateTextview.setText(lessonsPostArrayList.get(position).getPostedDateAndTime());
        descriptionTextview.setText(lessonsPostArrayList.get(position).getDescription());



        setMenuBtn(menuBtn, position);
        playVideoBtnClicked(playVideoBtn, position);
        videoViewClicked(videoView, position);
        downloadBtnClicked(downloadBtn, position);



        return convertView;
    }


    private void setMenuBtn(ImageButton menuBtn, int position){
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                showPopupMenu(menuBtn, position);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showPopupMenu(ImageButton menuBtn, int position) {
        PopupMenu popup = new PopupMenu(context, menuBtn);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.saved_post_menu_item, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.unsavedBtn) {
                    removeSavedPostIdArrayInUserProfileInfo(lessonsPostArrayList.get(position).getPostId(), position);
                    return true;
                }
                return false;
            }
        });

        popup.setForceShowIcon(true);
        popup.setGravity(Gravity.END);
        popup.show();
    }


    private void removeSavedPostIdArrayInUserProfileInfo(String postId, int position){
        firebaseFirestore.collection("user_profile_info")
                .document(utils.getUserID())
                .update("savedPosts", FieldValue.arrayRemove(postId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        lessonsPostArrayList.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "unsaved.", Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private void playVideoBtnClicked(ImageButton playVideoBtn, int position){
        playVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPlayVideoActivity(lessonsPostArrayList.get(position).getVideoUrl());
            }
        });
    }




    private void videoViewClicked(VideoView videoView, int position){
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPlayVideoActivity(lessonsPostArrayList.get(position).getVideoUrl());
            }
        });
    }




    private void downloadBtnClicked(ImageView downloadBtn, int position){
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVideoDownloadBtn(downloadBtn, position);
            }
        });
    }


    private void setVideoDownloadBtn(ImageView downloadBtn, int position){
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder alert =
                        new androidx.appcompat.app.AlertDialog.Builder(context);
                alert.setTitle("Alert");
                alert.setMessage("Do you want to download this video?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = lessonsPostArrayList.get(position).getVideoUrl();
                        String title = lessonsPostArrayList.get(position).getDescription();

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setTitle(title);
                        request.setDescription("Downloading video please wait.....");
                        String cookie = CookieManager.getInstance().getCookie(url);
                        request.addRequestHeader("cookie", cookie);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + ".mp4");


                        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        assert downloadManager != null;
                        downloadManager.enqueue(request);

                        Toast.makeText(context, "Download Started", Toast.LENGTH_LONG).show();


                        BroadcastReceiver onComplete = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), title + ".mp4");
                                if (file.exists()) {
                                    String videoPath = file.getPath();
                                    String videoName = title + ".mp4";


                                }
                            }
                        };

                        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                alert.create().show();
            }
        });
    }


    private void sendToPlayVideoActivity(String videoUrl){
        Intent intent = new Intent(context, PlayVideoActivity.class);
        intent.setData(Uri.parse(videoUrl));
        intent.putExtra("videoUrl", videoUrl);
        context.startActivity(intent);
    }



}
