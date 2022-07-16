package com.akGroup.englishspeakinglessons.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.model.ChatListItems;
import com.akGroup.englishspeakinglessons.model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialChatFragmentListviewAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<ChatListItems> chatListItemsArrayList;
    private FirebaseFirestore firebaseFirestore;


    public SocialChatFragmentListviewAdapter(Context context, ArrayList<ChatListItems> chatListItemsArrayList) {
        this.context = context;
        this.chatListItemsArrayList = chatListItemsArrayList;
    }

    @Override
    public int getCount() {
        return chatListItemsArrayList.size();
    }


    @Override
    public Object getItem(int position) {
        return chatListItemsArrayList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_listview_layout, parent, false);
        }


        CircleImageView circleImageView = convertView.findViewById(R.id.chatListviewCircleImgView);
        TextView friendNameTextview = convertView.findViewById(R.id.chatListviewFriendName);
        TextView lastMessageTextview = convertView.findViewById(R.id.chatListviewLastMessage);
        ImageButton menuBtn = convertView.findViewById(R.id.chatListviewMenuBtn);


        firebaseFirestore = FirebaseFirestore.getInstance();



        String profilePicUrl = chatListItemsArrayList.get(position).getFriendProfileUrl();
        if (!profilePicUrl.equals("")) {
            Glide.with(context)
                    .load(Uri.parse(profilePicUrl))
                    .into(circleImageView);
        }


        friendNameTextview.setText(chatListItemsArrayList.get(position).getFriendName());
        lastMessageTextview.setText(chatListItemsArrayList.get(position).getLastMessage());


        setMenuBtn(menuBtn, convertView, position);


        return convertView;
    }

    private void setMenuBtn(ImageButton menuBtn, View view, int position){
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
                    deleteMessageDocInGroupCollectionAndMessageCollection(position);
                    chatListItemsArrayList.remove(position);
                    notifyDataSetChanged();
                    return true;

                }
                return false;
            }
        });

        popup.setForceShowIcon(true);
        popup.setGravity(Gravity.END);
        popup.show();
    }


    private void deleteMessageDocInGroupCollectionAndMessageCollection(int position){
        String doc = chatListItemsArrayList.get(position).getChatId();

        firebaseFirestore
                .collection("group")
                .document(doc).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });


        firebaseFirestore
                .collection("message")
                .document(doc)
                .collection("messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<DocumentSnapshot> docs = task.getResult().getDocuments();

                            for (DocumentSnapshot doc : docs){
                                doc.getReference().delete();
                            }
                        }
                    }
                });
    }




}
