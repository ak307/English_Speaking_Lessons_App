package com.akGroup.englishspeakinglessons.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.listeners.UsersListener;
import com.akGroup.englishspeakinglessons.model.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnlineFriendsListviewAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<User> onlineFriendsArraylist;
    private UsersListener usersListener;




    public OnlineFriendsListviewAdapter(Context context, ArrayList<User> onlineFriendsArraylist, UsersListener usersListener) {
        this.context = context;
        this.onlineFriendsArraylist = onlineFriendsArraylist;
        this.usersListener = usersListener;
    }

    @Override
    public int getCount() {
        return onlineFriendsArraylist.size();
    }


    @Override
    public Object getItem(int position) {
        return onlineFriendsArraylist.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.friends_listview_layout, parent, false);
        }

        CircleImageView friendsImageView = (CircleImageView) convertView.findViewById(R.id.friendsImageView);
        TextView friendsNameTextView = (TextView) convertView.findViewById(R.id.friendsNameTextView);
        ImageView OfflineImageView = (ImageView) convertView.findViewById(R.id.OfflineImageView);
        ImageView OnlineImageView = (ImageView) convertView.findViewById(R.id.OnlineImageView);


        loadImage(onlineFriendsArraylist.get(position).getProfileImgUrl(), friendsImageView);
        friendsNameTextView.setText(onlineFriendsArraylist.get(position).getUserName());


        String status = onlineFriendsArraylist.get(position).getStatus();
        if (status.equals("online")) {
            OfflineImageView.setVisibility(View.GONE);
            OnlineImageView.setVisibility(View.VISIBLE);
        }
        else {
            OfflineImageView.setVisibility(View.VISIBLE);
            OnlineImageView.setVisibility(View.GONE);
        }




        return convertView;
    }


    private void loadImage(String profileImage, CircleImageView circleImageView){
        if (profileImage != null && !profileImage.equals(""))
            Glide.with(context)
                    .load(Uri.parse(profileImage))
                    .into(circleImageView);
    }


    private void callAudioBtnClicked(ImageView callAudio, int position){
        callAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersListener.initiateAudioMeeting(onlineFriendsArraylist.get(position));
            }
        });
    }


    private void callVideoBtnClicked(ImageView callVideo, int position){
        callVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersListener.initiateVideoMeeting(onlineFriendsArraylist.get(position));
            }
        });
    }

}
