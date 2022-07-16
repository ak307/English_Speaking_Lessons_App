package com.akGroup.englishspeakinglessons.api.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.adapter.AllFriendsListviewAdapter;
import com.akGroup.englishspeakinglessons.adapter.SocialChatFragmentListviewAdapter;
import com.akGroup.englishspeakinglessons.api.activity.ChatWithFriendActivity;
import com.akGroup.englishspeakinglessons.model.ChatListItems;
import com.akGroup.englishspeakinglessons.model.User;
import com.akGroup.englishspeakinglessons.service.AdViewService;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SocialChatFragment extends Fragment {
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<ChatListItems> chatListItemsArrayList;
    private Utils utils;
    private int count = 0;
    private AdView adView;

    public SocialChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social_chat, container, false);

        listView =(ListView) view.findViewById(R.id.chatListListview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.chatListSwipeRefreshLayout);
        adView = (AdView) view.findViewById(R.id.socialChatFragmentAdsView);

        swipeRefreshLayout.setOnRefreshListener(this::retrieveAllChat);
        firebaseFirestore = FirebaseFirestore.getInstance();
        chatListItemsArrayList = new ArrayList<>();
        utils = new Utils();

        retrieveAllChat();
        listViewOnItemClicked();
        AdViewService.loadAd(adView);


        return view;
    }



    private void retrieveAllChat(){
        swipeRefreshLayout.setRefreshing(true);

        firebaseFirestore
                .collection("group")
                .whereArrayContains("members", utils.getUserID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        swipeRefreshLayout.setRefreshing(false);


                        if (task.isSuccessful()){
                            List<DocumentSnapshot> docs = task.getResult().getDocuments();

                            chatListItemsArrayList.clear();
                            for (DocumentSnapshot doc : docs){
                                String chatId = doc.getString("docId");
                                String lastMessage = doc.getString("lastMessage");
                                Timestamp lastMessageCreatedAt = doc.getTimestamp("lastCreatedAt");
                                ArrayList<String> members = (ArrayList<String>) doc.get("members");


                                String lastCreatedAt = dateConvertor(lastMessageCreatedAt);

                                assert members != null;
                                if (members.get(0).equals(utils.getUserID()))
                                    retrieveFriends(members.get(1), chatId, lastMessage, lastCreatedAt);

                                else if (members.get(1).equals(utils.getUserID()))
                                    retrieveFriends(members.get(0), chatId, lastMessage, lastCreatedAt);

                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setAdapter();                                }
                            }, 500);
                        }
                    }
                });
    }


    private void retrieveFriends(String id, String chatId, String lastMessage, String lastCreatedAt){
        firebaseFirestore
                .collection("user_profile_info")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String id = task.getResult().getString("uid");
                            String userName = task.getResult().getString("userName");
                            String profile_img = task.getResult().getString("profile_img");
                            String token = task.getResult().getString("token");


                            if (!chatListItemsArrayList.isEmpty()) {
                                String previousFriendName = chatListItemsArrayList.get(chatListItemsArrayList.size() - 1).getFriendName();
                                String previousFriendPicUrl = chatListItemsArrayList.get(chatListItemsArrayList.size() - 1).getFriendProfileUrl();


                                if (previousFriendPicUrl != null && !previousFriendName.equals(userName) && !previousFriendPicUrl.equals(profile_img))
                                    chatListItemsArrayList.add(new ChatListItems(chatId, lastMessage, lastCreatedAt, userName, profile_img, id, token));
                            }
                            else
                                chatListItemsArrayList.add(new ChatListItems(chatId, lastMessage, lastCreatedAt, userName, profile_img, id, token));


                        }
                    }
                });
//                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                        if (error != null){
//                            Toast.makeText(getContext(),":" + error.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            assert value != null;
//                            if (value.exists()){
//                                String id = value.getString("uid");
//                                String userName = value.getString("userName");
//                                String profile_img = value.getString("profile_img");
//                                String token = value.getString("token");
//
//
//                                if (!chatListItemsArrayList.isEmpty()) {
//                                    String previousFriendName = chatListItemsArrayList.get(chatListItemsArrayList.size() - 1).getFriendName();
//                                    String previousFriendPicUrl = chatListItemsArrayList.get(chatListItemsArrayList.size() - 1).getFriendProfileUrl();
//
//
//                                    if (!previousFriendName.equals(userName) && !previousFriendPicUrl.equals(profile_img))
//                                        chatListItemsArrayList.add(new ChatListItems(chatId, lastMessage, lastCreatedAt, userName, profile_img, id, token));
//                                }
//                                else
//                                    chatListItemsArrayList.add(new ChatListItems(chatId, lastMessage, lastCreatedAt, userName, profile_img, id, token));
//
//
//                            }
//                            else {
//                                Toast.makeText(getContext(),"application does not exist.", Toast.LENGTH_SHORT).show();
//
//                            }
//                        }
//                    }
//                });

    }


    private void setAdapter(){
        SocialChatFragmentListviewAdapter adapter = new SocialChatFragmentListviewAdapter(getContext(), chatListItemsArrayList);
        listView.setAdapter(adapter);
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


    private void listViewOnItemClicked(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navigateToChatActivity(position);
            }
        });
    }


    private void navigateToChatActivity(int position){
        Intent intent = new Intent(getContext(), ChatWithFriendActivity.class);
        intent.putExtra("uid", chatListItemsArrayList.get(position).getFriendId());
        intent.putExtra("groupCollectionId", chatListItemsArrayList.get(position).getChatId());
        intent.putExtra("friendImageUrl", chatListItemsArrayList.get(position).getFriendProfileUrl());
        intent.putExtra("friendUsername", chatListItemsArrayList.get(position).getFriendName());
        intent.putExtra("friendToken", chatListItemsArrayList.get(position).getFriendToken());
        intent.putExtra("myToken", userData().getToken());
        intent.putExtra("myName", userData().getUserName());
        intent.putExtra("myUid", userData().getUid());
        intent.putExtra("myImageUrl", userData().getProfileImgUrl());



//        intent.putExtra("myData", userData());

        startActivity(intent);
    }





    private User userData(){
        return getUserDataFromSocialFragment();
    }


    private User getUserDataFromSocialFragment() {
        assert getArguments() != null;
        return getArguments().getParcelable("user_bundle_key");
    }





    @Override
    public void onResume() {
        count = 0;
        retrieveAllChat();
        super.onResume();
    }




}