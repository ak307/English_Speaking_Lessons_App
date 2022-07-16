package com.akGroup.englishspeakinglessons.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.model.Comment;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Map;

public class CommentListviewAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<Map<String, Object>> commentArrayList;


    public CommentListviewAdapter(Context context, ArrayList<Map<String, Object>> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;
    }

    @Override
    public int getCount() {
        return commentArrayList.size();
    }


    @Override
    public Object getItem(int position) {
        return commentArrayList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_layout, parent, false);
        }

        ImageView commentProfileImageView = (ImageView) convertView.findViewById(R.id.commentProfileImageView);
        TextView title = (TextView) convertView.findViewById(R.id.commenttitle);
        TextView postedTime = (TextView) convertView.findViewById(R.id.commentPostedTime);
        TextView postCommentTextview = (TextView) convertView.findViewById(R.id.postCommentTextview);



        Map<String, Object> comment_map = commentArrayList.get(position);
        String profilePicUrl = (String) comment_map.get("commentBy_profilePic");
        String profileName = (String) comment_map.get("commentBy_name");
        String commentTime = (String) comment_map.get("commentTime");
        String comment = (String) comment_map.get("comment");



        if (!profilePicUrl.equals("")) {
            Glide.with(context)
                    .load(Uri.parse(profilePicUrl))
                    .into(commentProfileImageView);
        }

        title.setText(profileName);
        postedTime.setText(commentTime);
        postCommentTextview.setText(comment);

        notifyDataSetChanged();


        return convertView;
    }

}
