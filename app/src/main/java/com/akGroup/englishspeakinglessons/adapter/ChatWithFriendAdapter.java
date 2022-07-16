package com.akGroup.englishspeakinglessons.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.model.Chat;
import com.akGroup.englishspeakinglessons.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatWithFriendAdapter extends RecyclerView.Adapter<ChatWithFriendAdapter.MyViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private ArrayList<Chat> chatArrayList;
    private Utils utils = new Utils();
    private String myProfileStr;
    private String hisProfileStr;

    FirebaseUser fuser;


    public ChatWithFriendAdapter(Context context, ArrayList<Chat> chatArrayList, String myProfileStr, String hisProfileStr) {
        this.context = context;
        this.chatArrayList = chatArrayList;
        this.myProfileStr = myProfileStr;
        this.hisProfileStr = hisProfileStr;
    }

    @NonNull
    @Override
    public ChatWithFriendAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        Toast.makeText(context, ": " + viewType, Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, ": " + viewType, Toast.LENGTH_SHORT).show();


        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new ChatWithFriendAdapter.MyViewHolder(view);

        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new ChatWithFriendAdapter.MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatWithFriendAdapter.MyViewHolder holder, int position) {

        Chat chat = chatArrayList.get(position);
        String userId = chat.getSender();


        if (userId.equals(utils.getUserID())){
            if (myProfileStr != null && !myProfileStr.equals("")) {
                Glide.with(context.getApplicationContext())
                        .load(Uri.parse(myProfileStr))
                        .into(holder.profile_image);
            }
        }
        else {
            if (hisProfileStr != null && !hisProfileStr.equals("")) {
                Glide.with(context.getApplicationContext())
                        .load(Uri.parse(hisProfileStr))
                        .into(holder.profile_image);
            }
        }


        holder.show_message.setText(chat.getMessage());

    }



    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }




    public class  MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profile_image;
        TextView show_message;




        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
           show_message = itemView.findViewById(R.id.show_message);

        }
    }


    @Override
    public int getItemViewType(int position) {
        if (chatArrayList.get(position).getSender().equals(utils.getUserID()))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;
    }
}
