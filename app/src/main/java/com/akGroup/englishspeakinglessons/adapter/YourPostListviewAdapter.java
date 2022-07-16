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
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class YourPostListviewAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<LessonsPost> yourLessonPosts;
    private FirebaseFirestore firebaseFirestore;
    private Utils utils = new Utils();

    public YourPostListviewAdapter(Context context, ArrayList<LessonsPost> yourLessonPosts) {
        this.context = context;
        this.yourLessonPosts = yourLessonPosts;
    }




    @Override
    public int getCount() {
        return yourLessonPosts.size();
    }

    @Override
    public Object getItem(int position) {
        return yourLessonPosts.get(position);
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
        downloadBtn.setVisibility(View.INVISIBLE);


        String profilePicUrl = yourLessonPosts.get(position).getProfilePicUrl();
        if (!profilePicUrl.equals("")) {
            Glide.with(context.getApplicationContext())
                    .load(Uri.parse(profilePicUrl))
                    .into(profilePic);
        }


        profileNameTextview.setText(yourLessonPosts.get(position).getProfileName());
        postedDateTextview.setText(yourLessonPosts.get(position).getPostedDateAndTime());
        descriptionTextview.setText(yourLessonPosts.get(position).getDescription());



        setMenuBtn(menuBtn, position);
        playVideoBtnClicked(playVideoBtn, position);
        videoViewClicked(videoView, position);


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
        popup.getMenuInflater().inflate(R.menu.delete_item_menu, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.savedDeleteMenuBtn) {
                    String videoUrl = yourLessonPosts.get(position).getVideoUrl();
                    String postId = yourLessonPosts.get(position).getPostId();

                    removeLessonVideoFromFirebaseStorage(position, videoUrl, postId);
                    return true;
                }
                return false;
            }
        });

        popup.setForceShowIcon(true);
        popup.setGravity(Gravity.END);
        popup.show();
    }


    private void removeLessonVideoFromFirebaseStorage(int position, String url, String postId){
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                removeSavedPostIdArrayInUserProfileInfo(postId, position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(context, "Delete Failed", Toast.LENGTH_LONG).show();
            }
        });

    }





    private void removeSavedPostIdArrayInUserProfileInfo(String postId, int position){
        firebaseFirestore.collection("user_profile_info")
                .document(utils.getUserID())
                .update("postIds", FieldValue.arrayRemove(postId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        yourLessonPosts.remove(position);
                        notifyDataSetChanged();
                        removePostFromLessons(postId);
                        Toast.makeText(context, "deleted.", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void removePostFromLessons(String postId){
        firebaseFirestore
                .collection("lesson_posts")
                .document(postId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
    }





    private void playVideoBtnClicked(ImageButton playVideoBtn, int position){
        playVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPlayVideoActivity(yourLessonPosts.get(position).getVideoUrl());
            }
        });
    }




    private void videoViewClicked(VideoView videoView, int position){
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPlayVideoActivity(yourLessonPosts.get(position).getVideoUrl());
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
