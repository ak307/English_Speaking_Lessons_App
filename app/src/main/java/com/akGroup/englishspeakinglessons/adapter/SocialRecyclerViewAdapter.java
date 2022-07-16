package com.akGroup.englishspeakinglessons.adapter;

import static com.amplitude.api.Utils.TAG;
import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.api.activity.PlayVideoActivity;
import com.akGroup.englishspeakinglessons.api.activity.ReportActivity;
import com.akGroup.englishspeakinglessons.model.Comment;
import com.akGroup.englishspeakinglessons.model.LessonsPost;
import com.akGroup.englishspeakinglessons.model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialRecyclerViewAdapter extends RecyclerView.Adapter<SocialRecyclerViewAdapter.MyViewHolder>{
    private ArrayList<Map<String, Object>> commentList = new ArrayList<>();
    private List<User> userList;
    private List<LessonsPost> lessonsPostList;
    private Context context;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private Utils utils = new Utils();
    private CommentListviewAdapter commentListviewAdapter;
    private InterstitialAd mInterstitialAd;


    public SocialRecyclerViewAdapter(List<LessonsPost> lessonsPostList, Context context, List<User> userList) {
        this.lessonsPostList = lessonsPostList;
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_post_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.posterProfileName.setText(lessonsPostList.get(position).getProfileName());
        holder.date.setText(lessonsPostList.get(position).getPostedDateAndTime());
        holder.description.setText(lessonsPostList.get(position).getDescription());


        String profilePicUrl = lessonsPostList.get(position).getProfilePicUrl();

        if (!profilePicUrl.equals("")) {
            Glide.with(context.getApplicationContext())
                    .load(Uri.parse(profilePicUrl))
                    .into(holder.profilePic);
        }


        playVideoBtnClicked(holder, position);
        downloadBtnClicked(holder, position);
        videoViewClicked(holder, position);
        setLoveLogoColorOrNoColor(holder, position);
        moreBtnClicked(holder.moreBtn, position);
        setLikeCountInTextView(position, holder);
        setCommentTextview(holder, position);

        noColorLoveBtnClicked(holder, position);
        colorLoveBtnClicked(holder, position);
        commentBtnClicked(holder.commentBtn, position);

        try {
            setThumbnail(holder, position);
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }






    @Override
    public int getItemCount() {
        return lessonsPostList.size();
    }


    private void playVideoBtnClicked(MyViewHolder holder, int position){
        holder.playVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPlayVideoActivity(lessonsPostList.get(position).getVideoUrl());
            }
        });
    }




    private void videoViewClicked(MyViewHolder holder, int position){
        holder.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPlayVideoActivity(lessonsPostList.get(position).getVideoUrl());
            }
        });
    }


    public class  MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profilePic;
        TextView posterProfileName;
        TextView date;
        TextView description;
        ImageView videoView;
        ImageButton playVideoBtn;
        ImageButton moreBtn;
        ImageView downloadBtn;
        ImageView noColorLikeBtn;
        ImageView colorLikeBtn;
        ImageView commentBtn;
        TextView likeCount;
        ImageView smallLikeLogo;
        TextView commentTextView;




        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.circleProfilePic);
            posterProfileName = itemView.findViewById(R.id.posterProfileNameTextView);
            date = itemView.findViewById(R.id.postTitleDate);
            description = itemView.findViewById(R.id.postDescription);
            videoView = itemView.findViewById(R.id.addVideoActivityVideoView);
            playVideoBtn = itemView.findViewById(R.id.addVideoActivityPlayBtn);
            downloadBtn = itemView.findViewById(R.id.postDownloadBtn);
            noColorLikeBtn = itemView.findViewById(R.id.postLikeBlackBtn);
            colorLikeBtn = itemView.findViewById(R.id.postLikeBlueBtn);
            moreBtn = itemView.findViewById(R.id.postMoreBtn);
            likeCount = itemView.findViewById(R.id.likeCount);
            commentBtn = itemView.findViewById(R.id.postCommentBtn);
            smallLikeLogo = itemView.findViewById(R.id.smallLikeLogo);
            commentTextView = itemView.findViewById(R.id.commentTextview);

        }
    }


    private void setCommentTextview(MyViewHolder holder, int position){
        holder.commentTextView.setText(lessonsPostList.get(position).getCommentsList().size() + " comments");
    }



    private void commentBtnClicked(ImageView commentBtn, int position){
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomSheetLayout(position);
            }
        });
    }



    private void setBottomSheetLayout(int position){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                context, R.style.BottomSheetDialogTheme);


        RelativeLayout relativeLayout = bottomSheetDialog.findViewById(R.id.bottomSheetContainer);

        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.comments_bottom_sheet_layout,
                        (RelativeLayout) relativeLayout);


        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);


        ListView listView = bottomSheetView.findViewById(R.id.commentListView);
        EditText messageBox = bottomSheetView.findViewById(R.id.messageBox);
        ImageButton sendBtn = bottomSheetView.findViewById(R.id.sendBtn);


        setListviewAdapter(listView, position);
        sendBtnClicked(sendBtn, messageBox, position);


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    private boolean isNoLike(int position){
        ArrayList<String> userLikedList = lessonsPostList.get(position).getLikedUserList();
        return userLikedList.size() <= 0;

    }



    private String getNowDate(){
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        return formattedDate;
    }


    private void setLikeCountInTextView(int position, MyViewHolder holder){
        if (isNoLike(position)){
            holder.smallLikeLogo.setVisibility(View.INVISIBLE);
            holder.likeCount.setVisibility(View.INVISIBLE);
        }
        else {
            holder.smallLikeLogo.setVisibility(View.VISIBLE);
            holder.likeCount.setVisibility(View.VISIBLE);
            holder.likeCount.setText(Integer.toString(lessonsPostList.get(position).getLikedUserList().size()));
        }
    }


    private void noColorLoveBtnClicked(MyViewHolder holder, int position){
        holder.noColorLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.noColorLikeBtn.setVisibility(View.GONE);
                holder.colorLikeBtn.setVisibility(View.VISIBLE);

                addLikedUserIdFromArray(position);

            }
        });
    }


    private void colorLoveBtnClicked(MyViewHolder holder, int position){
        holder.colorLikeBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                holder.noColorLikeBtn.setVisibility(View.VISIBLE);
                holder.colorLikeBtn.setVisibility(View.GONE);

                removeLikedUserIdFromArray(position);
            }
        });
    }


    private void removeLikedUserIdFromArray(int position){
        firebaseFirestore.collection("lesson_posts")
                .document(lessonsPostList.get(position).getPostId())
                .update("likedUserList", FieldValue.arrayRemove(utils.getUserID()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        lessonsPostList.get(position).getLikedUserList().remove(utils.getUserID());
                        notifyDataSetChanged();
                    }
                });
    }


    private void addLikedUserIdFromArray(int position){
        firebaseFirestore.collection("lesson_posts")
                .document(lessonsPostList.get(position).getPostId())
                .update("likedUserList", FieldValue.arrayUnion(utils.getUserID()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        lessonsPostList.get(position).getLikedUserList().add(utils.getUserID());
                        notifyDataSetChanged();

                    }
                });
    }





    private void setThumbnail(MyViewHolder holder, int position) {
        String urlPath = lessonsPostList.get(position).getVideoUrl();

        // load video thumbnail to imageview
        Glide
                .with(context)
                .load(urlPath)
                .into(holder.videoView);
    }



    private void moreBtnClicked(ImageButton moreBtn, int position){
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                showPopupMenu(moreBtn, position);
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showPopupMenu(ImageButton moreBtn, int position) {
        PopupMenu popup = new PopupMenu(context, moreBtn);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.lesson_posts_more_btn_menu, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.lessonPostSaveBtn) {
                    updateSavedPostIdArrayInUserProfileInfo(lessonsPostList.get(position).getPostId());
                    return true;

                }

                if (menuItem.getItemId() == R.id.lessonPostReportBtn) {
                    Intent intent = new Intent(context, ReportActivity.class);
                    intent.putExtra("reportedDocId", lessonsPostList.get(position).getPostId());
                    context.startActivity(intent);
                    return true;

                }

                return false;
            }
        });

        popup.setForceShowIcon(true);
        popup.show();
    }


    private void updateSavedPostIdArrayInUserProfileInfo(String postId){
        firebaseFirestore.collection("user_profile_info")
                .document(utils.getUserID())
                .update("savedPosts", FieldValue.arrayUnion(postId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "saved.", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private boolean isAlreadyLiked(int position){
        ArrayList<String> handShakeList = lessonsPostList.get(position).getLikedUserList();
        if (handShakeList.contains(utils.getUserID()))
            return true;
        else
            return false;
    }


    private void setLoveLogoColorOrNoColor(MyViewHolder holder, int position){
        if (isAlreadyLiked(position)){
            holder.noColorLikeBtn.setVisibility(View.GONE);
            holder.colorLikeBtn.setVisibility(View.VISIBLE);
        }
        else {
            holder.noColorLikeBtn.setVisibility(View.VISIBLE);
            holder.colorLikeBtn.setVisibility(View.GONE);
        }
    }


    @SuppressLint("SetTextI18n")
    private void setLikeCount(MyViewHolder holder, int position){
        holder.likeCount.setText(Long.toString(lessonsPostList.get(position).getHandShakeCount()));
    }


    private void downloadBtnClicked(MyViewHolder holder, int position){
        holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVideoDownloadBtn( holder.downloadBtn, position);

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
                        String url = lessonsPostList.get(position).getVideoUrl();
                        String title = lessonsPostList.get(position).getDescription();


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
                        loadInterstitialAd();

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


    private void loadInterstitialAd(){
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context,"ca-app-pub-9031708340876900/2202534078", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show((Activity) context);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        }, 1500);
    }


    private void sendToPlayVideoActivity(String videoUrl){
        Intent intent = new Intent(context, PlayVideoActivity.class);
        intent.setData(Uri.parse(videoUrl));
        intent.putExtra("videoUrl", videoUrl);
        context.startActivity(intent);
    }



    // send comment

    private void sendBtnClicked(ImageButton imageButton, EditText messageBox, int position){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageBox.getText().toString().trim();
                if (!TextUtils.isEmpty(message)){
                    addCommentToFirestore(message, position);
                    messageBox.setText("");
                }
            }
        });
    }


    private void addCommentToFirestore(String comment, int position){
        String commentBy_profilePic = userList.get(0).getProfileImgUrl();
        String commentBy_name = userList.get(0).getUserName();

        Comment commentObj =
                new Comment("", comment, getNowDate(), utils.getUserID(), commentBy_profilePic, commentBy_name);

        Map<String, Object> map = new HashMap<>();
        map.put("commentBy_id", utils.getUserID());
        map.put("commentBy_name", commentBy_name);
        map.put("commentBy_profilePic", commentBy_profilePic);
        map.put("commentTime", getNowDate());
        map.put("docId", "");
        map.put("comment", comment);


        commentList.add(map);
        commentListviewAdapter.notifyDataSetChanged();


        firebaseFirestore.collection("lesson_posts")
                .document(lessonsPostList.get(position).getPostId())
                .update("commentsList", FieldValue.arrayUnion(commentObj))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                        }
                    }
                });
    }






    private void setListviewAdapter(ListView listView, int position){
        commentList = lessonsPostList.get(position).getCommentsList();
        commentListviewAdapter = new CommentListviewAdapter(context, commentList);
        listView.setAdapter(commentListviewAdapter);
        commentListviewAdapter.notifyDataSetChanged();

    }

}
