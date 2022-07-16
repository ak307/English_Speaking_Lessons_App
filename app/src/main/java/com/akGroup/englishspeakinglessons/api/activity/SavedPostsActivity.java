package com.akGroup.englishspeakinglessons.api.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.adapter.SavedPostsListviewAdapter;
import com.akGroup.englishspeakinglessons.adapter.SocialRecyclerViewAdapter;
import com.akGroup.englishspeakinglessons.model.LessonsPost;
import com.akGroup.englishspeakinglessons.model.PracticeConversationSaved;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SavedPostsActivity extends AppCompatActivity {
    private ArrayList<LessonsPost> lessonsPosts = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_posts);

        ListView savedPostsListview = (ListView) findViewById(R.id.savedPostsListview);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        firebaseFirestore = FirebaseFirestore.getInstance();
        retrieveSavedPostIds(savedPostsListview);



    }

    private void retrieveSavedPostIds(ListView listView){
        firebaseFirestore
                .collection("user_profile_info")
                .document(utils.getUserID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (Objects.requireNonNull(task.getResult()).exists()) {
                                DocumentSnapshot doc = task.getResult();
                                ArrayList<String> savedPostIds = (ArrayList<String>) doc.get("savedPosts");

                                if (savedPostIds != null && !savedPostIds.isEmpty()) {
                                    retrieveSavedPostsData(savedPostIds, listView);
                                }
                            }
                            else
                                Toast.makeText(getApplicationContext(), "doc does not exist", Toast.LENGTH_SHORT).show();



                        }
                    }
                });
    }


    private void retrieveSavedPostsData(ArrayList<String> savedPostIds, ListView listView){
        firebaseFirestore.collection("lesson_posts")
                .whereIn("postId", savedPostIds)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<DocumentSnapshot> data = task.getResult().getDocuments();

                            lessonsPosts.clear();
                            for (DocumentSnapshot doc : data){
                                Timestamp postedDataAndTime =  doc.getTimestamp("postedDataAndTime");
                                String description =  doc.getString("description");
                                String profilePicUrl = doc.getString("profilePicUrl");
                                String profileName = doc.getString("profileName");
                                String videoUrl = doc.getString("videoUrl");
                                long handShakeCount = doc.getLong("handShakeCount");
                                String postId = doc.getString("postId");
                                ArrayList<String> likedUserList = (ArrayList<String>) doc.get("likedUserList");


                                String date = getActualDate(postedDataAndTime);
                                lessonsPosts.add(
                                        new LessonsPost(date, description, profilePicUrl,
                                                profileName, videoUrl, handShakeCount, postId, likedUserList));

                            }
                            setAdapter(listView);
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


    private void setAdapter(ListView listView){
        SavedPostsListviewAdapter adapter = new SavedPostsListviewAdapter(SavedPostsActivity.this, lessonsPosts);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


}