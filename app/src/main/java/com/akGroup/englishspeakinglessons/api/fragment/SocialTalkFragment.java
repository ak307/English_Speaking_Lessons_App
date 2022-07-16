package com.akGroup.englishspeakinglessons.api.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.adapter.OnlineFriendsListviewAdapter;
import com.akGroup.englishspeakinglessons.api.activity.VideoCallOutGoingActivity;
import com.akGroup.englishspeakinglessons.listeners.UsersListener;
import com.akGroup.englishspeakinglessons.model.Conversation;
import com.akGroup.englishspeakinglessons.model.User;
import com.akGroup.englishspeakinglessons.service.AdViewService;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class SocialTalkFragment extends Fragment {
    private CircleImageView circleImageView;
    private TextView profileName;
    private TextView onlineText;
    private TextView offlineText;
    private Switch onlineOfflineSwitch;
    private FirebaseFirestore firebaseFirestore;
    private Utils utils;
    private ListView onlineFriendsListView;
    private ArrayList<User> onlineFriendArraylist;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdView adView;
    private String serverKey;
    private UsersListener usersListener;

    public SocialTalkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social_talk_fragment, container, false);

        circleImageView = (CircleImageView) view.findViewById(R.id.myProfileCircleImgView);
        profileName = (TextView) view.findViewById(R.id.myProfileName);
        onlineText = (TextView) view.findViewById(R.id.onlineText);
        offlineText = (TextView) view.findViewById(R.id.offlineText);
        onlineOfflineSwitch = (Switch) view.findViewById(R.id.onlineOfflineSwitch);
        onlineFriendsListView = (ListView) view.findViewById(R.id.onlineFriendsListView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.talkSwipeRefreshLayout);
        adView = (AdView) view.findViewById(R.id.talkFragmentAdView);

        swipeRefreshLayout.setOnRefreshListener(this::getOnlineFriendsData);
        firebaseFirestore = FirebaseFirestore.getInstance();
        utils = new Utils();
        onlineFriendArraylist = new ArrayList<>();




        getOnlineFriendsData();
        switchClicked();
        listViewItemClicked();
        loadImage();
        setProfileName();
        updateSwitch();
        AdViewService.loadAd(adView);
        getServerKey();


        return view;
    }


    private void updateSwitch(){
        String status = userData().getStatus();
        if (status.equals("online")) {
            onlineOfflineSwitch.setChecked(true);
            setOnlineOfflineStatus("online");
        }
        else {
            onlineOfflineSwitch.setChecked(false);
            setOnlineOfflineStatus("offline");

        }


    }


    private void switchClicked(){
        onlineOfflineSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onlineOfflineSwitch.isChecked())
                    updateStatus("online");

                else
                    updateStatus("offline");

            }
        });


    }


    private void updateStatus(String status){
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", status);

        firebaseFirestore
                .collection("user_profile_info")
                .document(utils.getUserID())
                .update(statusUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        setOnlineOfflineStatus(status);
                    }
                });
    }





    private void getOnlineFriendsData(){
        if (!userData().getFriendsArraylist().isEmpty()){
            swipeRefreshLayout.setRefreshing(true);
            firebaseFirestore
                    .collection("user_profile_info")
                    .whereIn("uid", userData().getFriendsArraylist())
                    .whereEqualTo("status", "online")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                swipeRefreshLayout.setRefreshing(false);
                                ArrayList<DocumentSnapshot> docs = (ArrayList<DocumentSnapshot>) task.getResult().getDocuments();

                                onlineFriendArraylist.clear();
                                for (DocumentSnapshot doc : docs){
                                    String id = doc.getString("uid");
                                    String userName =  doc.getString("userName");
                                    String email =  doc.getString("email");
                                    String profile_img = doc.getString("profile_img");
                                    String phone = doc.getString("phone");
                                    String status = doc.getString("status");
                                    String token = doc.getString("token");


                                    onlineFriendArraylist.add(new User(id, userName, email, profile_img, phone, status, token));
                                }
                                setAdapter();
                            }
                        }
                    });
        }
    }








    private void loadImage(){
        String profileImage = userData().getProfileImgUrl();
        if (profileImage != null && !profileImage.equals(""))
            Glide.with(getContext())
                    .load(Uri.parse(profileImage))
                    .into(circleImageView);
    }


    private void setProfileName(){
        String username = userData().getUserName();
        if (username != null && !username.isEmpty()){
            profileName.setText(username);
        }
    }


    private void setOnlineOfflineStatus(String status){
        if (status.equals("online")){
            onlineText.setVisibility(View.VISIBLE);
            offlineText.setVisibility(View.GONE);
        }
        else {
            onlineText.setVisibility(View.GONE);
            offlineText.setVisibility(View.VISIBLE);
        }

    }



    private void setAdapter(){
        OnlineFriendsListviewAdapter adapter = new OnlineFriendsListviewAdapter(getContext(), onlineFriendArraylist, usersListener);
        onlineFriendsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private void listViewItemClicked(){
        onlineFriendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (onlineOfflineSwitch.isChecked())
                    showAlertDialog(position);
                else
                    Toast.makeText(getContext(), "Please make yourself online first.", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void showAlertDialog(int position){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.call_alert_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button videoCallBtn = dialog.findViewById(R.id.videoCallBtn);
        Button audioCallBtn = dialog.findViewById(R.id.audioCallBtn);



        audioCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), VideoCallOutGoingActivity.class);
                intent.putExtra("receiverUid", onlineFriendArraylist.get(position).getUid());
                intent.putExtra("receiverName", onlineFriendArraylist.get(position).getUserName());
                intent.putExtra("receiverProfileImg", onlineFriendArraylist.get(position).getProfileImgUrl());
                intent.putExtra("type", "audio");
                intent.putExtra("token", onlineFriendArraylist.get(position).getToken());
                intent.putExtra("myToken", userData().getToken());
                intent.putExtra("myName", userData().getUserName());
                intent.putExtra("myUid", userData().getUid());
                intent.putExtra("myImageUrl", userData().getProfileImgUrl());
                intent.putExtra("serverKey", serverKey);


                startActivity(intent);

                dialog.dismiss();
            }
        });

        videoCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), VideoCallOutGoingActivity.class);
                intent.putExtra("receiverUid", onlineFriendArraylist.get(position).getUid());
                intent.putExtra("receiverName", onlineFriendArraylist.get(position).getUserName());
                intent.putExtra("receiverProfileImg", onlineFriendArraylist.get(position).getProfileImgUrl());
                intent.putExtra("type", "video");
                intent.putExtra("token", onlineFriendArraylist.get(position).getToken());
                intent.putExtra("myToken", userData().getToken());
                intent.putExtra("myName", userData().getUserName());
                intent.putExtra("myUid", userData().getUid());
                intent.putExtra("myImageUrl", userData().getProfileImgUrl());
                intent.putExtra("serverKey", serverKey);


                startActivity(intent);

                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }



    public void getServerKey(){
        firebaseFirestore.collection("cloud_messaging_server_key")
                .document("iZY3JHF3sXFzegCjcFcr")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()) {
                                serverKey = task.getResult().getString("server_key");
                            }
                        }
                    }
                });
    }



    private User userData(){
        return getUserDataFromSocialFragment();
    }


    private User getUserDataFromSocialFragment() {
        assert getArguments() != null;
        return getArguments().getParcelable("user_bundle_key");
    }




}