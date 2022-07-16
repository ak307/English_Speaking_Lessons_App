package com.akGroup.englishspeakinglessons.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.model.Conversation;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ConversationsListviewAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<Conversation> conversationArrayList;


    public ConversationsListviewAdapter(Context context, ArrayList<Conversation> conversationArrayList) {
        this.context = context;
        this.conversationArrayList = conversationArrayList;
    }


    @Override
    public int getCount() {
        return conversationArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return conversationArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.conversations_listview_layout, parent, false);

        }

        ImageView titleImageView = (ImageView) convertView.findViewById(R.id.conversationImgView);
        TextView title = (TextView) convertView.findViewById(R.id.conversationTitle);

        String converTitleImgUrl = conversationArrayList.get(position).getImageUrl();

        if (!converTitleImgUrl.equals("")) {
            Glide.with(context.getApplicationContext())
                    .load(Uri.parse(converTitleImgUrl))
                    .into(titleImageView);
        }

        title.setText(conversationArrayList.get(position).getTitle());



        return convertView;
    }
}
