package com.akGroup.englishspeakinglessons.api.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.MainActivity;
import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.adapter.AllFriendsListviewAdapter;
import com.akGroup.englishspeakinglessons.adapter.AllUserListviewAdapter;
import com.akGroup.englishspeakinglessons.model.User;
import com.akGroup.englishspeakinglessons.service.AdViewService;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindFriendsActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private Utils utils;
    private ArrayList<User> allUserArraylist;
    private ListView listView;
    private ImageButton backBtn;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        backBtn = (ImageButton) findViewById(R.id.addFriendBackBtn);
        listView = (ListView) findViewById(R.id.allUserListview);
        adView = (AdView) findViewById(R.id.findFriendActivityAdView);

        firebaseFirestore = FirebaseFirestore.getInstance();
        utils = new Utils();
        allUserArraylist = new ArrayList<>();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        retrieveAllUser();
        AdViewService.loadAd(adView);

    }



    private void retrieveAllUser(){
        allUserArraylist.clear();
        firebaseFirestore
                .collection("user_profile_info")
                .whereNotEqualTo("uid", utils.getUserID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String id = doc.getString("uid");
                                String userName =  doc.getString("userName");
                                String email =  doc.getString("email");
                                String profile_img = doc.getString("profile_img");
                                String phone = doc.getString("phone");
                                String status = doc.getString("status");


                                allUserArraylist.add(new User(id, userName, email, profile_img, phone, status));
                            }
                            setAdapter();
                        }
                    }
                });
    }


    private void setAdapter(){
        AllUserListviewAdapter adapter = new AllUserListviewAdapter(FindFriendsActivity.this, allUserArraylist, getFriendListFromSocialAllFriendsFragment());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }




    private String[] getFriendListFromSocialAllFriendsFragment() {
        Bundle b = this.getIntent().getExtras();
        String[] array = b.getStringArray("friendList");

        return array;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FindFriendsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}