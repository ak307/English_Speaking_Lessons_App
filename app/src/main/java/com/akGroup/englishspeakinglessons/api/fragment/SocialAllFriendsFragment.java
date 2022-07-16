package com.akGroup.englishspeakinglessons.api.fragment;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.adapter.AllFriendsListviewAdapter;
import com.akGroup.englishspeakinglessons.adapter.OnlineFriendsListviewAdapter;
import com.akGroup.englishspeakinglessons.api.activity.ChatWithFriendActivity;
import com.akGroup.englishspeakinglessons.api.activity.FindFriendsActivity;
import com.akGroup.englishspeakinglessons.model.User;
import com.akGroup.englishspeakinglessons.service.AdViewService;
import com.google.android.gms.ads.AdService;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class SocialAllFriendsFragment extends Fragment {
    private EditText searchBox;
    private ImageButton searchBtn;
    private ImageButton addNewFriendBtn;
    private ListView listView;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<User> friendArraylist;
    private Utils utils = new Utils();
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdView adView;


    public SocialAllFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social_all_friends, container, false);



        searchBox = (EditText) view.findViewById(R.id.searchBox);
        searchBtn = (ImageButton) view.findViewById(R.id.searchBtn);
        addNewFriendBtn = (ImageButton) view.findViewById(R.id.addFriendBtn);
        listView = (ListView) view.findViewById(R.id.allFriendsListview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.allFriendsSwipeRefreshLayout);
        adView = (AdView) view.findViewById(R.id.allFriendFragmentAdView);

        swipeRefreshLayout.setOnRefreshListener(this::getAllFriendsData);
        firebaseFirestore = FirebaseFirestore.getInstance();
        friendArraylist = new ArrayList<>();


        getAllFriendsData();
        searchBtnClicked();
        setSearchBox();
        addNewFriendBtnClicked();
        listviewOnItemClicked();
        AdViewService.loadAd(adView);



        return  view;
    }



    private void addNewFriendBtnClicked(){
        addNewFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] array = new String[userData().getFriendsArraylist().size()];
                String[] friendList = userData().getFriendsArraylist().toArray(array);

                Bundle b = new Bundle();
                b.putStringArray("friendList", friendList);
                Intent intent = new Intent(getContext(), FindFriendsActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }


    private void searchBtnClicked(){
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = searchBox.getText().toString().trim();
                if (!TextUtils.isEmpty(inputText)){
                    retrieveCustomerSearch(inputText);
                }
            }
        });
    }




    private void retrieveCustomerSearch(String input){
        friendArraylist.clear();
        firebaseFirestore
                .collection("user_profile_info")
                .whereGreaterThanOrEqualTo("userName",  input)
                .whereLessThan("userName", input + "~")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            friendArraylist.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String id = doc.getString("uid");
                                String userName =  doc.getString("userName");
                                String email =  doc.getString("email");
                                String profile_img = doc.getString("profile_img");
                                String phone = doc.getString("phone");
                                String status = doc.getString("status");
                                ArrayList<String> friends = (ArrayList<String>) doc.get("friends");


                                friendArraylist.add(new User(id, userName, email, profile_img, phone, status, friends));
                            }
                            setAdapter();
                        }
                    }
                });
    }



    private void setSearchBox(){
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchBox.getText().toString().trim().equals("")){
                    friendArraylist.clear();
                    getAllFriendsData();
                }
            }
        });
    }




    private void getAllFriendsData(){
        if (!userData().getFriendsArraylist().isEmpty()){
            swipeRefreshLayout.setRefreshing(true);
            friendArraylist.clear();
            firebaseFirestore
                    .collection("user_profile_info")
                    .whereIn("uid", userData().getFriendsArraylist())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            swipeRefreshLayout.setRefreshing(false);
                            if (task.isSuccessful()){
                                ArrayList<DocumentSnapshot> docs = (ArrayList<DocumentSnapshot>) task.getResult().getDocuments();

                                friendArraylist.clear();

                                for (DocumentSnapshot doc : docs){
                                    String id = doc.getString("uid");
                                    String userName =  doc.getString("userName");
                                    String email =  doc.getString("email");
                                    String profile_img = doc.getString("profile_img");
                                    String phone = doc.getString("phone");
                                    String status = doc.getString("status");
                                    ArrayList<String> friends = (ArrayList<String>) doc.get("friends");


                                    friendArraylist.add(new User(id, userName, email, profile_img, phone, status, friends));
                                }

                                setAdapter();
                            }
                        }
                    });
        }


    }


    private void setAdapter(){
        AllFriendsListviewAdapter adapter = new AllFriendsListviewAdapter(getContext(), friendArraylist);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    private void listviewOnItemClicked(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAlertDialog(position);
            }
        });
    }


    private void showAlertDialog(int position){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.all_friend_popup_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button allFriendChatBtn = (Button) dialog.findViewById(R.id.allFriendChatBtn);



        allFriendChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGroupIsAlreadyExistOrNor(position);


                dialog.dismiss();
            }
        });



        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }



    private void checkGroupIsAlreadyExistOrNor(int position){
        firebaseFirestore
                .collection("group")
                .whereArrayContainsAny("members", Arrays.asList(utils.getUserID()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<DocumentSnapshot> docs = task.getResult().getDocuments();

                            if (!docs.isEmpty()){
                                int count = 0;

                                for (DocumentSnapshot doc : docs){
                                    String groupId = doc.getString("docId");
                                    ArrayList<String> members = (ArrayList<String>) doc.get("members");

                                    if (members.contains(friendArraylist.get(position).getUid())){
                                        navigateToChatActivity(position, groupId);
                                        break;
                                    }
                                    count++;
                                }

                                if (docs.size() == count){
                                    createGroupChatCollectionInFirestore(position);
                                }

                            }
                            else
                                createGroupChatCollectionInFirestore(position);

                        }
                    }
                });

    }




    private void createGroupChatCollectionInFirestore(int position){
        ArrayList<String> members = new ArrayList<>();
        members.add(utils.getUserID());
        members.add(friendArraylist.get(position).getUid());

        String docId = firebaseFirestore
                .collection("group")
                .document().getId();


        HashMap<String, Object> map = new HashMap<>();
        map.put("createdAt", FieldValue.serverTimestamp());
        map.put("createdBy", utils.getUserID());
        map.put("docId", docId);
        map.put("members", members);
        map.put("type", 1);


       firebaseFirestore
               .collection("group")
               .document(docId)
               .set(map)
               .addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){
                           navigateToChatActivity(position, docId);
                       }
                   }
               });
    }


    private void navigateToChatActivity(int position, String groupId){
        Intent intent = new Intent(getContext(), ChatWithFriendActivity.class);
        intent.putExtra("uid", friendArraylist.get(position).getUid());
        intent.putExtra("groupCollectionId", groupId);
        intent.putExtra("friendImageUrl", friendArraylist.get(position).getProfileImgUrl());
        intent.putExtra("myImageUrl", userData().getProfileImgUrl());
        intent.putExtra("myUserName", userData().getUserName());
        intent.putExtra("friendUsername", friendArraylist.get(position).getUserName());
        startActivity(intent);
    }






    private User userData(){
        return getUserDataFromSocialFragment();
    }


    private User getUserDataFromSocialFragment() {
        assert getArguments() != null;
        return getArguments().getParcelable("user_bundle_key");
    }


}