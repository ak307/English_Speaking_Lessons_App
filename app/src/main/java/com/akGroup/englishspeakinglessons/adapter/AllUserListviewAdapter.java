package com.akGroup.englishspeakinglessons.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AllUserListviewAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<User> userArrayList;
    private FirebaseFirestore firebaseFirestore;
    private Utils utils = new Utils();
    private String[] friendArray;

    public AllUserListviewAdapter(Context context, ArrayList<User> userArrayList,String[] friendArray) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.friendArray = friendArray;
    }

    @Override
    public int getCount() {
        return userArrayList.size();
    }


    @Override
    public Object getItem(int position) {
        return userArrayList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.all_user_listview_layout, parent, false);
        }

        ImageView profileImageView = (ImageView) convertView.findViewById(R.id.userProfileImageView);
        TextView friendNameTextView = (TextView) convertView.findViewById(R.id.userNameTextView);
        TextView friendOnlineOfflineStatus = (TextView) convertView.findViewById(R.id.userOnlineOfflineStatus);
        TextView addFriendButton = (TextView) convertView.findViewById(R.id.addFriendButton);
        TextView addFriendCancelBtn = (TextView) convertView.findViewById(R.id.addFriendCancelBtn);

        firebaseFirestore = FirebaseFirestore.getInstance();

        addFriendButtonClicked(addFriendButton, addFriendCancelBtn, position);
        addFriendCancelButtonClicked(addFriendButton, addFriendCancelBtn, position);


        String profilePicUrl = userArrayList.get(position).getProfileImgUrl();
        if (!profilePicUrl.equals("")) {
            Glide.with(context)
                    .load(Uri.parse(profilePicUrl))
                    .into(profileImageView);
        }

        friendNameTextView.setText(userArrayList.get(position).getUserName());
        String status = userArrayList.get(position).getStatus();
        friendOnlineOfflineStatus.setText(status);


        if (status.equals("online")){
            friendOnlineOfflineStatus.setTextColor(Color.rgb(0, 128, 0));
        }
        else {
            friendOnlineOfflineStatus.setTextColor(Color.rgb(165,42,42));
        }


        changeBtnDependOnFriend(position, addFriendButton, addFriendCancelBtn);

        return convertView;
    }


    private void changeBtnDependOnFriend(int position, TextView addFriendBtn, TextView cancelBtn){
        List<String> friendList = Arrays.asList(friendArray);
        if (friendList.contains(userArrayList.get(position).getUid())){
            addFriendBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.VISIBLE);
        }
        else {
            addFriendBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.GONE);
        }

    }


    private void addFriendButtonClicked(TextView addFriendBtn, TextView addFriendCancelBtn, int position){
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendBtn.setVisibility(View.GONE);
                addFriendCancelBtn.setVisibility(View.VISIBLE);

                updateFriendListInMyProfile(position);
                updateFriendListInNewFriendProfile(position);

            }
        });
    }


    private void addFriendCancelButtonClicked(TextView addFriendBtn, TextView addFriendCancelBtn, int position){
        addFriendCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendBtn.setVisibility(View.VISIBLE);
                addFriendCancelBtn.setVisibility(View.GONE);

                removeFriendListFromMyProfile(position);
                removeFriendListFromNewFriendProfile(position);
            }
        });
    }


    private void updateFriendListInMyProfile(int position){
        firebaseFirestore
                .collection("user_profile_info")
                .document(utils.getUserID())
                .update("friends", FieldValue.arrayUnion(userArrayList.get(position).getUid()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {

                    }
                });
    }


    private void removeFriendListFromMyProfile(int position){
        firebaseFirestore
                .collection("user_profile_info")
                .document(utils.getUserID())
                .update("friends", FieldValue.arrayRemove(userArrayList.get(position).getUid()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {

                    }
                });
    }




    private void updateFriendListInNewFriendProfile(int position){
        firebaseFirestore
                .collection("user_profile_info")
                .document(userArrayList.get(position).getUid())
                .update("friends", FieldValue.arrayUnion(utils.getUserID()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {

                    }
                });
    }


    private void removeFriendListFromNewFriendProfile(int position){
        firebaseFirestore
                .collection("user_profile_info")
                .document(userArrayList.get(position).getUid())
                .update("friends", FieldValue.arrayRemove(utils.getUserID()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {

                    }
                });
    }


}
