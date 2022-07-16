package com.akGroup.englishspeakinglessons.api.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.adapter.SocialRecyclerViewAdapter;
import com.akGroup.englishspeakinglessons.api.activity.AddNewLessonPostActivity;
import com.akGroup.englishspeakinglessons.model.LessonsPost;
import com.akGroup.englishspeakinglessons.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class SocialHomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private List<LessonsPost> lessonsPostList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private Utils utils;
    private FloatingActionButton addNewPostBtn;
    private SwipeRefreshLayout swipeRefreshLayout;






    public SocialHomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social_home, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.socialRecyclerView);
        addNewPostBtn = (FloatingActionButton) view.findViewById(R.id.newNewPostFloatButton);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.videoSwipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this::retrieveAndSetLessonPostsData);
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        utils = new Utils();


        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        retrieveAndSetUserData();
        retrieveAndSetLessonPostsData();
        addNewBtnClicked(addNewPostBtn);

        return view;
    }


    private void retrieveAndSetUserData(){
        firebaseFirestore.collection("user_profile_info")
                .document(utils.getUserID()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                String userName =  task.getResult().getString("userName");
                                String email =  task.getResult().getString("email");
                                String profile_img = task.getResult().getString("profile_img");
                                String phone = task.getResult().getString("phone");

                                userList.add(new User("", userName, email, profile_img, phone));
                            }
                            else {
                                Toast.makeText(getContext(), "Document does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }



    private void retrieveAndSetLessonPostsData(){
        swipeRefreshLayout.setRefreshing(true);
        firebaseFirestore
                .collection("lesson_posts")
                .orderBy("postedDataAndTime", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        swipeRefreshLayout.setRefreshing(false);

                        lessonsPostList.clear();

                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                Timestamp postedDataAndTime =  doc.getTimestamp("postedDataAndTime");
                                String description =  doc.getString("description");
                                String profilePicUrl = doc.getString("profilePicUrl");
                                String profileName = doc.getString("profileName");
                                String videoUrl = doc.getString("videoUrl");
                                long handShakeCount = doc.getLong("handShakeCount");
                                String postId = doc.getString("postId");
                                ArrayList<String> likedUserList = (ArrayList<String>) doc.get("likedUserList");
                                ArrayList<Map<String, Object>> commentsList = (ArrayList<Map<String, Object>>) doc.get("commentsList");




                                String date = getActualDate(postedDataAndTime);
                                lessonsPostList.add(
                                        new LessonsPost(date, description, profilePicUrl,
                                                profileName, videoUrl, handShakeCount,
                                                postId, likedUserList, commentsList)
                                );

                            }

                            setRecyclerViewAdapter(recyclerView);

                        }
                    }
                });

    }



    private String getActualDate(Timestamp timestamp){
        String dateStr = "";
        if (timestamp != null){
            Date date = timestamp.toDate();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat =
                    new SimpleDateFormat("dd-MM-yyyy hh:mm");
            dateStr = dateFormat.format(date);
        }
        return dateStr;
    }



    private void setRecyclerViewAdapter(RecyclerView recyclerView){
        mAdapter = new SocialRecyclerViewAdapter(lessonsPostList, getContext(), userList);
        recyclerView.setAdapter(mAdapter);
    }


    private void addNewBtnClicked(FloatingActionButton addNewBtn){
        addNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddNewLessonPostActivity.class);
                startActivity(intent);
            }
        });
    }

}