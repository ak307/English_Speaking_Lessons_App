package com.akGroup.englishspeakinglessons.api.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.adapter.ChatWithFriendAdapter;
import com.akGroup.englishspeakinglessons.model.Chat;
import com.akGroup.englishspeakinglessons.service.ChatService;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatWithFriendActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private Utils utils;
    private RecyclerView recyclerView;
    private EditText textBox;
    private ImageButton sentBtn;
    private ArrayList<Chat> messageArrayList;
    private ChatService chatService;
    private ImageButton backBtn;
    private CircleImageView titleProfileImgView;
    private TextView titleTextview;
    private ImageButton audioCallBtn;
    private ImageButton videoCallBtn;
    private String serverKey = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_friend);


        recyclerView = (RecyclerView) findViewById(R.id.privateChatRecyclerView);
        textBox = (EditText) findViewById(R.id.privateChatTextBox);
        sentBtn = (ImageButton) findViewById(R.id.privateChatSentBtn);
        backBtn = (ImageButton) findViewById(R.id.privateChatBackBtn);
        titleProfileImgView = (CircleImageView) findViewById(R.id.privateChatProfileImage);
        titleTextview = (TextView) findViewById(R.id.privateChatTitle);
        audioCallBtn = (ImageButton) findViewById(R.id.privateChatAudioCallBtn);
        videoCallBtn = (ImageButton) findViewById(R.id.privateChatVideoCallBtn);



        firebaseFirestore = FirebaseFirestore.getInstance();
        utils = new Utils();
        messageArrayList = new ArrayList<>();
        chatService = new ChatService(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        getServerKey();
        retrieveAllMessage();
        setToolBarInfo();
        sentBtnClicked();
        backBtnClicked();
        setVideoCallBtnClicked();
        setAudioCallBtnClicked();


    }


    public void getServerKey(){
        firebaseFirestore
                .collection("cloud_messaging_server_key")
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


    private void setAudioCallBtnClicked(){
        audioCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatWithFriendActivity.this, VideoCallOutGoingActivity.class);
                intent.putExtra("receiverUid", getFriendID());
                intent.putExtra("receiverName", getFriendUsername());
                intent.putExtra("receiverProfileImg", getFriendImgUrl());
                intent.putExtra("type", "audio");
                intent.putExtra("token", getFriendToken());
                intent.putExtra("myToken", getMyToken());
                intent.putExtra("myName", getMyUserName());
                intent.putExtra("myUid", getMyUid());
                intent.putExtra("myImageUrl", getMyImgUrl());
                intent.putExtra("serverKey", serverKey);

                startActivity(intent);

            }
        });
    }


    private void setVideoCallBtnClicked(){
        videoCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatWithFriendActivity.this, VideoCallOutGoingActivity.class);
                intent.putExtra("receiverUid", getFriendID());
                intent.putExtra("receiverName", getFriendUsername());
                intent.putExtra("receiverProfileImg", getFriendImgUrl());
                intent.putExtra("type", "video");
                intent.putExtra("token", getFriendToken());
                intent.putExtra("myToken", getMyToken());
                intent.putExtra("myName", getMyUserName());
                intent.putExtra("myUid", getMyUid());
                intent.putExtra("myImageUrl", getMyImgUrl());
                intent.putExtra("serverKey", serverKey);


                startActivity(intent);

            }
        });
    }


    private void setToolBarInfo(){
        if (getFriendImgUrl() != null && !getFriendImgUrl().equals("")) {
            Glide.with(getApplicationContext())
                    .load(Uri.parse(getFriendImgUrl()))
                    .into(titleProfileImgView);
        }

        titleTextview.setText(getFriendUsername());
    }


    private void backBtnClicked(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



    private void retrieveAllMessage(){
        firebaseFirestore.collection("message")
                .document(getGroupCollectionID())
                .collection("messages")
                .orderBy("sentAt", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }


                        if (value != null && !value.isEmpty()) {
                            List<DocumentSnapshot> data = value.getDocuments();
                            messageArrayList.clear();

                            for (DocumentSnapshot item : data) {
                                String message = (String) item.getString("messageText");
                                String receiver = (String) item.getString("sentTo");
                                String sentBy = (String) item.getString("sentBy");
                                Timestamp sentAt = (Timestamp) item.getTimestamp("sentAt");


                                String sentDateAndTime = dateConvertor(sentAt);

                                messageArrayList.add(new Chat(sentBy, receiver, message, sentDateAndTime));

                            }
                            setAdapter();
                        }
                    }
                });
    }


    private void setAdapter(){
        ChatWithFriendAdapter adapter = new ChatWithFriendAdapter(getApplicationContext(), messageArrayList, getMyImgUrl(), getFriendImgUrl());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private String dateConvertor(Timestamp sentAt){
        String strDate = "";
        if (sentAt != null){
            Date date = sentAt.toDate();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat =
                    new SimpleDateFormat("dd-MM-yyyy hh:mm");
            strDate = dateFormat.format(date);
        }
        return strDate;
    }



    private void sentBtnClicked(){
        sentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFriendID() != null && !getFriendID().isEmpty()){
                    String message = textBox.getText().toString().trim();
                    if (!message.isEmpty()){
                        createNewDocInMessageCollection(message);
                        textBox.setText("");

                    }
                }
//                else {
//                    Toast.makeText(getApplicationContext(), "error: " + getFriendID(), Toast.LENGTH_SHORT).show();
//
//                }



            }
        });
    }






    private void createNewDocInMessageCollection(String messageText){
        HashMap<String, Object> map = new HashMap<>();
        map.put("messageText", messageText);
        map.put("sentAt", FieldValue.serverTimestamp());
        map.put("sentBy", utils.getUserID());
        map.put("sentTo", getFriendID());


        String chatDocID = firebaseFirestore.collection("message")
                .document(getGroupCollectionID())
                .collection("messages")
                .document().getId();


        firebaseFirestore
                .collection("message")
                .document(getGroupCollectionID())
                .collection("messages")
                .document(chatDocID)
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            updateLastMessage(messageText);
                        }
                    }
                });


        if (serverKey != null) {
            // sent notification
            chatService.getTokenAndSendNotification(getMyUserName(), messageText,
                    getFriendID(), getFriendImgUrl(), getFriendUsername(),
                    getGroupCollectionID(), getMyUid(), getMyImgUrl(), serverKey);
        }
        else
            Toast.makeText(getApplicationContext(), "server key is null", Toast.LENGTH_SHORT).show();

    }


    private void updateLastMessage(String messageText){
        HashMap<String, Object> map = new HashMap<>();
        map.put("lastMessage", messageText);
        map.put("lastCreatedAt", FieldValue.serverTimestamp());

        firebaseFirestore
                .collection("group")
                .document(getGroupCollectionID())
                .update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }


    private String getGroupCollectionID(){
        return getIntent().getStringExtra("groupCollectionId");
    }

    private String getFriendImgUrl(){
        return getIntent().getStringExtra("friendImageUrl");
    }


    private String getFriendID(){
        return getIntent().getStringExtra("uid");
    }


    private String getFriendUsername(){
        return getIntent().getStringExtra("friendUsername");
    }


    private String getFriendToken(){
        return getIntent().getStringExtra("friendToken");
    }

    private String getMyToken(){
        return getIntent().getStringExtra("myToken");
    }

    private String getMyUserName(){
        return getIntent().getStringExtra("myName");
    }

    private String getMyUid(){
        return getIntent().getStringExtra("myUid");
    }

    private String getMyImgUrl(){
        return getIntent().getStringExtra("myImageUrl");
    }






}