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
import com.akGroup.englishspeakinglessons.model.Message;
import com.akGroup.englishspeakinglessons.model.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MessageListviewAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<Message> messageArrayList;

    public MessageListviewAdapter(Context context, ArrayList<Message> messageArrayList) {
        this.context = context;
        this.messageArrayList = messageArrayList;
    }


    @Override
    public int getCount() {
        return messageArrayList.size();
    }


    @Override
    public Object getItem(int position) {
        return messageArrayList.get(position);
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


        return convertView;
    }
}
