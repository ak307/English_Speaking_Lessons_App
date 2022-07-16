package com.akGroup.englishspeakinglessons.api.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.adapter.ConversationsListviewAdapter;
import com.akGroup.englishspeakinglessons.model.Conversation;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ConversationListActivity extends AppCompatActivity {
    private ArrayList<Conversation> conversationArrayList = new ArrayList<>();
    private Utils utils = new Utils();
    private FirebaseFirestore firebaseFirestore;
    private ConversationsListviewAdapter adapter;
    private AdView adView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        listView = (ListView) findViewById(R.id.conversationsListView);
        adView = (AdView) findViewById(R.id.conversationListFragmentAdView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.conversationSwipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this::retrieveConversationDataFromFirestore);
        firebaseFirestore = FirebaseFirestore.getInstance();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        retrieveConversationDataFromFirestore();
        listViewItemClicked(listView);
        conversationListviewLongClicked(listView);
        loadAd();

//        addTestData();

    }


    private void retrieveConversationDataFromFirestore(){
        swipeRefreshLayout.setRefreshing(true);
        firebaseFirestore
                .collection("conversation_practice")
                .whereEqualTo("level", getLevelFromConversationLevelActivity())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (task.isSuccessful()){

                            conversationArrayList.clear();
                            for (DocumentSnapshot doc: task.getResult()){
                                String title = doc.getString("conversationTitle");
                                String titlePhoto = doc.getString("conversationPhoto");
                                String firstPersonConAudioUrl = doc.getString("firstPersonConAudioUrl");
                                String firstPersonConversationBy = doc.getString("firstPersonConversationBy");
                                String firstPersonConversationText = doc.getString("firstPersonConversationText");
                                String fullConAudioUrl = doc.getString("fullConAudioUrl");
                                String fullConversationBy = doc.getString("fullConversationBy");
                                String fullConversationText = doc.getString("fullConversationText");
                                String secPersonConAudioUrl = doc.getString("secPersonConAudioUrl");
                                String secPersonConversationBy = doc.getString("secPersonConversationBy");
                                String secPersonConversationText = doc.getString("secPersonConversationText");
                                String conversationID = doc.getString("conversationID");
                                String level = doc.getString("level");


                                conversationArrayList.add(new Conversation
                                                (title, titlePhoto,
                                        fullConversationText,
                                        firstPersonConversationText,
                                        secPersonConversationText,
                                        fullConAudioUrl,
                                        firstPersonConAudioUrl,
                                        secPersonConAudioUrl,
                                        fullConversationBy,
                                        firstPersonConversationBy,
                                        secPersonConversationBy,
                                        conversationID, level));
                            }
                            setListviewAdapter(listView);
                        }
                    }
                });
    }


//    private void addTestData(){
//        conversationArrayList.add(new Conversation
//                ("Picnic", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/76/Pat_Cummins.jpg/278px-Pat_Cummins.jpg",
//                        "fullConversationText",
//                        "firstPersonConversationText",
//                        "secPersonConversationText",
//                        "https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3",
//                        "https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3",
//                        "https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3",
//                        "John and Bob",
//                        "John",
//                        "Bob",
//                        "conversationID"));
//
//        conversationArrayList.add(new Conversation
//                ("Picnic", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/76/Pat_Cummins.jpg/278px-Pat_Cummins.jpg",
//                        "fullConversationText",
//                        "firstPersonConversationText",
//                        "secPersonConversationText",
//                        "https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3",
//                        "https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3",
//                        "https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3",
//                        "John and Bob",
//                        "John",
//                        "Bob",
//                        "conversationID"));
//    }


    private void setListviewAdapter(ListView listView){
        adapter = new ConversationsListviewAdapter(ConversationListActivity.this, conversationArrayList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private void listViewItemClicked(ListView listView){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Conversation conversationObj = conversationArrayList.get(position);

                navigateToConversationActivity(conversationObj);
            }
        });
    }


    private void conversationListviewLongClicked(ListView listView){
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {

                showAlertDialog(position);

                return true;
            }
        });
    }


    private void showAlertDialog(int position){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.alert_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Button noBtn = dialog.findViewById(R.id.cancelBtn);
        Button yesBtn = dialog.findViewById(R.id.deleteBtn);
        TextView title = dialog.findViewById(R.id.warningText);

        title.setText("Do you want to delete?");
        noBtn.setText("No");
        yesBtn.setText("Yes");

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdminLogInDialog(position);
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }



    private void showAdminLogInDialog(int position){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.admin_login_popup_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        EditText userNameEText = dialog.findViewById(R.id.adminUsernameEditText);
        EditText passwordEText = dialog.findViewById(R.id.adminPasswordEditText);
        Button loginBtn = dialog.findViewById(R.id.adminLoginBtn);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userNameEText.getText().toString().trim();
                String pass = passwordEText.getText().toString().trim();

                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pass)){
                    firebaseFirestore.collection("admin")
                            .document("admin_doc")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult().exists()){
                                            DocumentSnapshot doc = task.getResult();
                                            String userName = doc.getString("username");
                                            String password = doc.getString("password");


                                            if (userName.equals(username) && password.equals(pass)){
                                                showFinalAlertDialog(position);
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "Wrong username or password.", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(getApplicationContext(), "Fill all field.", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });



        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }



    private void showFinalAlertDialog(int position){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.alert_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Button noBtn = dialog.findViewById(R.id.cancelBtn);
        Button yesBtn = dialog.findViewById(R.id.deleteBtn);
        TextView title = dialog.findViewById(R.id.warningText);

        title.setText("Are you sure that you want to delete?");
        noBtn.setText("No");
        yesBtn.setText("Yes");

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteConversation(position);
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }


    private void deleteConversation(int position){
        String conversationPhotoUrl = conversationArrayList.get(position).getImageUrl();
        String fullConversationAudioUrl = conversationArrayList.get(position).getFullConversationUrl();
        String firstPersonConversationAudioUrl = conversationArrayList.get(position).getFirstPersonConversationUrl();
        String secPersonConversationAudioUrl = conversationArrayList.get(position).getSecondPersonConversationUrl();
        String docId = conversationArrayList.get(position).getConversationID();


        removeConversationFilesFromFirebaseStorage(conversationPhotoUrl);
        removeConversationFilesFromFirebaseStorage(fullConversationAudioUrl);
        removeConversationFilesFromFirebaseStorage(firstPersonConversationAudioUrl);
        removeConversationFilesFromFirebaseStorage(secPersonConversationAudioUrl);
        removeConversationLesson(docId, position);


    }



    private void removeConversationFilesFromFirebaseStorage(String url){
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               // success
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(getApplicationContext(), "Delete Failed", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void removeConversationLesson(String docId, int position){
        firebaseFirestore.collection("conversation_practice")
                .document(docId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        conversationArrayList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private String getLevelFromConversationLevelActivity(){
        return getIntent().getStringExtra("level");
    }


    private void navigateToConversationActivity(Conversation conversationObj){
        Intent intent = new Intent(ConversationListActivity.this, ConversationActivity.class);
        intent.putExtra("conversation", conversationObj);
        startActivity(intent);
    }

    private void loadAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }


            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }



}