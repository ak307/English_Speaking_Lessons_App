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

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.model.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Map;

public class AllFriendsListviewAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<User> userArrayList;


    public AllFriendsListviewAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.all_friends_listview_layout, parent, false);
        }

        ImageView profileImageView = (ImageView) convertView.findViewById(R.id.friendProfileImageView);
        TextView friendNameTextView = (TextView) convertView.findViewById(R.id.friendNameTextView);
        TextView friendOnlineOfflineStatus = (TextView) convertView.findViewById(R.id.friendOnlineOfflineStatus);


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




        return convertView;
    }
}
