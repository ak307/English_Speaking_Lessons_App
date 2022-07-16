package com.akGroup.englishspeakinglessons.api.fragment;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.MainActivity;
import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.adapter.SocialViewPagerAdapter;
import com.akGroup.englishspeakinglessons.api.activity.Fill_OTP_Signup_Activity;
import com.akGroup.englishspeakinglessons.api.activity.LogInPageActivity;
import com.akGroup.englishspeakinglessons.api.activity.SignupPageActivity;
import com.akGroup.englishspeakinglessons.databinding.ActivityMainBinding;
import com.akGroup.englishspeakinglessons.model.User;
import com.akGroup.englishspeakinglessons.service.AdViewService;
import com.akGroup.englishspeakinglessons.service.MessagingService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;


public class SocialFragment extends Fragment {
    private Utils utils;
    private AdView adView;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private TabLayout tabs;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private ConstraintLayout noLoginLayout;
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "GOOGLEAUTH";
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private FragmentManager childFragManager;


    public SocialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_social, container, false);

        SignInButton googleSignInBtn = (SignInButton) view.findViewById(R.id.SignInWithGoogleBtn);
        Button loginBtn = (Button) view.findViewById(R.id.LoginYourAccountBtn);
        Button signupBtn = (Button) view.findViewById(R.id.signUpYourAccountBtn);
        noLoginLayout = (ConstraintLayout) view.findViewById(R.id.socialPageNoLoginLayout);
        adView = (AdView) view.findViewById(R.id.socialFragmentAdView);
        appBarLayout = (AppBarLayout) view.findViewById(R.id.appBarLayout);
        viewPager = (ViewPager) view.findViewById(R.id.socialViewPager);
        tabs = (TabLayout) view.findViewById(R.id.socialTabs);


        // configure
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        utils = new Utils();





        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_VISIBLE|WindowManager
                .LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // google configure
        createRequest();

        setUpProgressDialog();
        signInWithGoogleBtn(googleSignInBtn);
        layoutVisibilityAdjust();
        loginBtnClicked(loginBtn);
        signUpButtonClicked(signupBtn);
        AdViewService.loadAd(adView);

        if (utils.isLogin())
            retrieveAndSetUserData();



        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         childFragManager = getChildFragmentManager();



        return view;
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Signing in...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
    }


    private void createRequest(){
        // configure google sign in
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);

    }



    private void signInWithGoogleBtn(SignInButton signInButton){
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                signIn();
            }
        });
    }



    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                progressDialog.dismiss();
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);

                Toast.makeText(getContext(),  e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);

                        }
                    }
                });
    }



    private void updateUI(FirebaseUser user) {
        if (user != null){
            Toast.makeText(getContext(), "Login successful" + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

            checkDocIsExistOrNot(user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
        }
        else {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Error: something is wrong", Toast.LENGTH_SHORT).show();
        }

    }



    private void checkDocIsExistOrNot(String Username, String email, String photo){
        firebaseFirestore.collection("user_profile_info")
                .document(utils.getUserID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                retrieveAndSetUserData();
                                layoutVisibilityAdjust();

                                if (utils.isLogin())
                                    setToken();
                            }
                            else {
                                addProfileInfoToFirestore(Username, email, photo);
                            }
                        }
                    }
                });
    }


    private void addProfileInfoToFirestore(String Username, String email, String photo){
        ArrayList<String> postIds = new ArrayList<>();
        ArrayList<String> savedPosts = new ArrayList<>();
        ArrayList<String> friends = new ArrayList<>();



        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("userName", Username);
        dataMap.put("password", "");
        dataMap.put("email", email);
        dataMap.put("phone", "");
        dataMap.put("uid", utils.getUserID());
        dataMap.put("profile_img", photo);
        dataMap.put("postIds", postIds);
        dataMap.put("savedPosts", savedPosts);
        dataMap.put("friends", friends);
        dataMap.put("status", "offline");



        firebaseFirestore.collection("user_profile_info")
                .document(utils.getUserID())
                .update(dataMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                retrieveAndSetUserData();
                layoutVisibilityAdjust();

                if (utils.isLogin())
                    setToken();
            }

        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Toast.makeText(getContext(), "Sign up unsuccessful: " + error, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void setToken(){
        MessagingService service = new MessagingService();
        service.getToken();
    }




    private void retrieveAndSetUserData(){
        firebaseFirestore
                .collection("user_profile_info")
                .document(utils.getUserID())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }


                        if (value != null && value.exists()){
                            String uid =  value.getString("uid");
                            String userName =  value.getString("userName");
                            String email =  value.getString("email");
                            String profile_img = value.getString("profile_img");
                            String phone = value.getString("phone");
                            String status = value.getString("status");
                            ArrayList<String> friends = (ArrayList<String>) value.get("friends");
                            String token = value.getString("token");

                            User user = new User(uid, userName, email, profile_img, phone, status, friends, token);

                            setUpSocialTab(user);


                        }
                        else
                            Toast.makeText(getContext(), "Document does not exist.", Toast.LENGTH_SHORT).show();
                    }
                });



//
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//                            if(task.getResult().exists()){
//                                String uid =  task.getResult().getString("uid");
//                                String userName =  task.getResult().getString("userName");
//                                String email =  task.getResult().getString("email");
//                                String profile_img = task.getResult().getString("profile_img");
//                                String phone = task.getResult().getString("phone");
//                                String status = task.getResult().getString("status");
//                                ArrayList<String> friends = (ArrayList<String>) task.getResult().get("friends");
//
//                                User user = new User(uid, userName, email, profile_img, phone, status, friends);
//                                setUpSocialTab(user);
//
//
//                            }
//                            else {
//                                Toast.makeText(getContext(), "Document does not exist.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
    }



    private void setUpSocialTab(User user){
        SocialViewPagerAdapter adapter = new SocialViewPagerAdapter(childFragManager,
                SocialViewPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.addFragment(new SocialHomeFragment(), "Videos", user);
        adapter.addFragment(new SocialTalkFragment(), "Talk", user);
        adapter.addFragment(new SocialChatFragment(), "Chat", user);
        adapter.addFragment(new SocialAllFriendsFragment(), "Friends", user);

        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);


//        tabs.getTabAt(0).setIcon(R.drawable.ic_home_24);
//        tabs.getTabAt(1).setIcon(R.drawable.ic_call_24);
//        tabs.getTabAt(2).setIcon(R.drawable.ic_chat_24);
//        tabs.getTabAt(3).setIcon(R.drawable.ic_friends_24);




    }





//    private void retrieveAndSetUserData(){
//        firebaseFirestore.collection("user_profile_info")
//                .document(utils.getUserID()).get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//                            if(task.getResult().exists()){
//                                String userName =  task.getResult().getString("userName");
//                                String email =  task.getResult().getString("email");
//                                String profile_img = task.getResult().getString("profile_img");
//                                String phone = task.getResult().getString("phone");
//
//                               userList.add(new User("", userName, email, profile_img, phone));
//                            }
//                            else {
//                                Toast.makeText(getContext(), "Document does not exist.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
//    }
//
//
//
//    private void retrieveAndSetLessonPostsData(){
//        firebaseFirestore
//                .collection("lesson_posts")
//                .orderBy("postedDataAndTime", Query.Direction.DESCENDING)
//                .limit(50)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        lessonsPostList.clear();
//
//                        if (task.isSuccessful()){
//                           for (QueryDocumentSnapshot doc : task.getResult()){
//                               Timestamp postedDataAndTime =  doc.getTimestamp("postedDataAndTime");
//                               String description =  doc.getString("description");
//                               String profilePicUrl = doc.getString("profilePicUrl");
//                               String profileName = doc.getString("profileName");
//                               String videoUrl = doc.getString("videoUrl");
//                               long handShakeCount = doc.getLong("handShakeCount");
//                               String postId = doc.getString("postId");
//                               ArrayList<String> likedUserList = (ArrayList<String>) doc.get("likedUserList");
//                               ArrayList<Map<String, Object>> commentsList = (ArrayList<Map<String, Object>>) doc.get("commentsList");
//
//
//
//
//                               String date = getActualDate(postedDataAndTime);
//                               lessonsPostList.add(
//                                       new LessonsPost(date, description, profilePicUrl,
//                                               profileName, videoUrl, handShakeCount,
//                                               postId, likedUserList, commentsList)
//                               );
//
//                           }
//
//                            setRecyclerViewAdapter(recyclerView);
//
//                        }
//                    }
//                });
//
//    }
//
//
//
//    private String getActualDate(Timestamp timestamp){
//        String dateStr = "";
//        if (timestamp != null){
//            Date date = timestamp.toDate();
//            @SuppressLint("SimpleDateFormat") DateFormat dateFormat =
//                    new SimpleDateFormat("dd-MM-yyyy hh:mm");
//            dateStr = dateFormat.format(date);
//        }
//        return dateStr;
//    }



    private void layoutVisibilityAdjust(){
        if (utils.getUserID() != null){
            noLoginLayout.setVisibility(View.GONE);
            appBarLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
        }
        else {
            noLoginLayout.setVisibility(View.VISIBLE);
            appBarLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
        }
    }


    private void loginBtnClicked(Button loginBtn){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LogInPageActivity.class);
                startActivity(intent);
            }
        });
    }


    private void signUpButtonClicked(Button signUpBtn){
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SignupPageActivity.class);
                startActivity(intent);
            }
        });
    }



//
//    @Override
//    public void onResume() {
//        retrieveAndSetLessonPostsData();
//        super.onResume();
//    }


}