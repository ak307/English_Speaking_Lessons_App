package com.akGroup.englishspeakinglessons.api.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.MainActivity;
import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.api.activity.ImgCropperActivity;
import com.akGroup.englishspeakinglessons.api.activity.PrivacyActivity;
import com.akGroup.englishspeakinglessons.api.activity.SavedPostsActivity;
import com.akGroup.englishspeakinglessons.api.activity.TermsOfServiceActivity;
import com.akGroup.englishspeakinglessons.api.activity.YourPostsActivity;
import com.akGroup.englishspeakinglessons.service.AdViewService;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private Utils utils = new Utils();
    private final int PHONE_GALLERY_REQUEST = 1;
    private Uri newProfileImageUri;
    private String oldProfileImageUriString;
    private CircleImageView profilePic;
    ActivityResultLauncher<String> mGetContent;
    private AdView adView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private LinearLayout phoneLLayout;
    private LinearLayout emailLLayout;
    private LinearLayout logoutBtn;



    public SettingFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_setting, container, false);

        profilePic = (CircleImageView) view.findViewById(R.id.profileCircleImgView);
        nameTextView = (TextView) view.findViewById(R.id.profileName);
        emailTextView = (TextView) view.findViewById(R.id.profileEmail);
        phoneTextView = (TextView) view.findViewById(R.id.profilePhone);
        LinearLayout privacyBtn = (LinearLayout) view.findViewById(R.id.privacyLLayout);
        LinearLayout termsAndConditionsBtn = (LinearLayout) view.findViewById(R.id.termsAndConditionsLLayout);
        LinearLayout helpBtn = (LinearLayout) view.findViewById(R.id.helpLLayout);
        logoutBtn = (LinearLayout) view.findViewById(R.id.logoutLLayout);
        LinearLayout yourPostBtn = (LinearLayout) view.findViewById(R.id.yourPostLLayout);
        LinearLayout saveBtn = (LinearLayout) view.findViewById(R.id.saveLLayout);
        phoneLLayout = (LinearLayout) view.findViewById(R.id.phoneLLayout);
        emailLLayout = (LinearLayout) view.findViewById(R.id.emailLLayout);

        adView = (AdView) view.findViewById(R.id.settingFragmentAdView);


        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();


        if (utils.isLogin()){
            retrieveAndSetUserData();

            logoutBtn.setVisibility(View.VISIBLE);
//            loginBtn.setVisibility(View.GONE);
        }
        else {
            logoutBtn.setVisibility(View.GONE);
//            loginBtn.setVisibility(View.VISIBLE);
        }



        profilePicClicked(profilePic);
        privacyBtnClicked(privacyBtn);
        termsAndConditionsClicked(termsAndConditionsBtn);
        helpBtnClicked(helpBtn);
        logoutBtnClicked(logoutBtn);
        yourPostBtnClicked(yourPostBtn);
        savedPostBtnCLicked(saveBtn);
        userNameCLicked(nameTextView);
        AdViewService.loadAd(adView);


        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null){
                    Intent intent = new Intent(getContext(), ImgCropperActivity.class);
                    intent.putExtra("DATA", result.toString());
                    startActivityForResult(intent, 101);
                }
            }
        });


        return view;
    }


    private void retrieveAndSetUserData(){
        if (utils.isLogin()) {
            firebaseFirestore.collection("user_profile_info")
                    .document(utils.getUserID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    String userName = task.getResult().getString("userName");
                                    String email = task.getResult().getString("email");
                                    String profile_img = task.getResult().getString("profile_img");
                                    String phone = task.getResult().getString("phone");

                                    oldProfileImageUriString = profile_img;
                                    assert profile_img != null;
                                    if (!profile_img.isEmpty())
                                        loadImage(profile_img);

                                    addTextToNameTextView(userName);
                                    addTextToEmailTextView(email);
                                    addTextToPhoneTextView(phone);

                                } else {
                                    Toast.makeText(getContext(), "Document does not exist.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
        else {
            addTextToNameTextView("");
            addTextToEmailTextView("");
            addTextToPhoneTextView("");
        }
    }


    private void loadImage(String profileImage){
        if (profileImage != null && !profileImage.equals(""))
            Glide.with(getContext())
                    .load(Uri.parse(profileImage))
                    .into(profilePic);
    }




    private void profilePicClicked(CircleImageView profilePic){
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utils.getUserID() != null)
                    askingPermissions();
                else
                    Toast.makeText(getContext(), "Please Sign in", Toast.LENGTH_SHORT).show();
            }
        });




    }



    private void askingPermissions(){
        callPhoneGallery();
    }


    private void callPhoneGallery(){
        askPermissionBox(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PHONE_GALLERY_REQUEST);
    }


    private void askPermissionBox(String[] permissionArr, int requestCode){
        if (Build.VERSION.SDK_INT >= 23){
            int permissionChecker = PackageManager.PERMISSION_GRANTED;
            for (String permissionStr: permissionArr){
                permissionChecker = permissionChecker + ContextCompat.checkSelfPermission(getContext(), permissionStr);
            }

            if (permissionChecker != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(getActivity(), permissionArr, requestCode);
            else
                onPermissionGranted(requestCode);
        }
        else  {
            onPermissionGranted(requestCode);
        }
    }


    private void onPermissionGranted(int requestCode){
        if (requestCode == PHONE_GALLERY_REQUEST) {
            mGetContent.launch("image/*");
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1 && requestCode == 101){
            String result = data.getStringExtra("RESULT");
            if (result != null && !result.equals("")){
                newProfileImageUri = Uri.parse(result);


                if (!newProfileImageUri.equals(""))
                    Glide.with(getContext())
                            .load(newProfileImageUri)
                            .into(profilePic);


                if (oldProfileImageUriString != null && !oldProfileImageUriString.equals(""))
                    deleteImageFromFirebaseStorage();
                addImageToFirebaseStorage(newProfileImageUri);


            }
        }


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int permissionChecker = PackageManager.PERMISSION_GRANTED;

        for (int permission : grantResults)
            permissionChecker = permissionChecker + permission;

        if (permissionChecker == PackageManager.PERMISSION_GRANTED && grantResults.length > 0){

        }

        else
            Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();;

    }



    private void deleteImageFromFirebaseStorage(){
        FirebaseUser user = auth.getCurrentUser();
        if (!oldProfileImageUriString.equals("") && !oldProfileImageUriString.equals(user.getPhotoUrl().toString())){
            StorageReference storageReference = firebaseStorage.getReferenceFromUrl(oldProfileImageUriString);
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                Toast.makeText(HomeActivity.this, "delete img successful", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }



    private void addImageToFirebaseStorage(Uri profileImageUri){
        StorageReference image_path = storageReference.child("user_profile_image").child(utils.getUserID() +".jpg");

        image_path.putFile(profileImageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    addImagePathToFireBase(image_path);
                }
                else  {
                    Toast.makeText(getContext(), "failed add image to firebase storage.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    private void addImagePathToFireBase(StorageReference image_path){
        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                updateProfileInfoToFirestore(uri);
            }
        });
    }



    private void updateProfileInfoToFirestore(Uri profileImageUri){
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("profile_img", profileImageUri.toString());


        firebaseFirestore.collection("user_profile_info").document(utils.getUserID())
                .update(dataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.getMessage();
                Toast.makeText(getContext(), "Sign up unsuccessful: " + error, Toast.LENGTH_SHORT).show();

            }
        });

    }



    private void addTextToNameTextView(String name){
        nameTextView.setText(name);
    }



    private void addTextToEmailTextView(String email){
        if (email.isEmpty())
            emailLLayout.setVisibility(View.GONE);
        emailTextView.setText(email);
    }



    private void addTextToPhoneTextView(String phoneNum){
        if (phoneNum.isEmpty())
            phoneLLayout.setVisibility(View.GONE);

        phoneTextView.setText(phoneNum);
    }



    private void savedPostBtnCLicked(LinearLayout savePostBtn){
        savePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utils.isLogin()) {
                    Intent intent = new Intent(getContext(), SavedPostsActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(getContext(), "Please Sign in.", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void privacyBtnClicked(LinearLayout privacyBtn){
        privacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PrivacyActivity.class);
                startActivity(intent);
            }
        });
    }


    private void logoutBtnClicked(LinearLayout logoutBtn){
        if (utils.isLogin()){
            logoutBtn.setVisibility(View.VISIBLE);
            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlertDialog();
                }
            });
        }
        else
            logoutBtn.setVisibility(View.GONE);

    }



    private void showAlertDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.alert_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);
        Button logOutBtn = dialog.findViewById(R.id.deleteBtn);

        cancelBtn.setText("Cancel");
        logOutBtn.setText("Log out");

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeTokenFromUserProfile();

                dialog.dismiss();

            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }



    private void removeTokenFromUserProfile(){
        DocumentReference documentReference = FirebaseFirestore
                .getInstance()
                .collection("user_profile_info")
                .document(utils.getUserID());

        Map<String, Object> map = new HashMap<>();
        map.put("token", FieldValue.delete());

        documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    auth.signOut();

                    addTextToNameTextView("");
                    addTextToEmailTextView("");
                    addTextToPhoneTextView("");
                    logoutBtn.setVisibility(View.GONE);

                }
            }
        });

    }







    private void termsAndConditionsClicked(LinearLayout termsAndConditionBtn){
        termsAndConditionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TermsOfServiceActivity.class);
                startActivity(intent);
            }
        });
    }


    private void helpBtnClicked(LinearLayout helpBtn){
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void userNameCLicked(TextView userName){
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangeUserNamePopup(userName);
            }
        });
    }



    private void openChangeUserNamePopup(TextView username){
        String oldUserName = username.getText().toString();
        View mView = getLayoutInflater().inflate(R.layout.change_username_layout, null);

        final EditText name = (EditText) mView.findViewById(R.id.changeUName);

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Do you want to change username?");
        alert.setMessage("Add below");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String newUserName = name.getText().toString();

                if(!newUserName.equals(oldUserName)){
                    Map<String, Object> map = new HashMap<>();
                    map.put("userName", newUserName);

                    firebaseFirestore.collection("user_profile_info")
                            .document(utils.getUserID())
                            .update(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onSuccess(Void aVoid) {
                                    username.setText(newUserName);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setView(mView);
        alert.create().show();
    }


    private void yourPostBtnClicked(LinearLayout yourPostBtn){
        yourPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utils.isLogin()) {
                    Intent intent = new Intent(getContext(), YourPostsActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(getContext(), "Please Sign in.", Toast.LENGTH_SHORT).show();

            }
        });

    }






}