package com.akGroup.englishspeakinglessons.api.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.MainActivity;
import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class SignupPageActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private final int PHONE_GALLERY_REQUEST= 1;
    private Uri profileImageUri;
    private Utils utils = new Utils();
    private ImageView profileCircleImageViewBtn;
    ActivityResultLauncher<String> mGetContent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        profileCircleImageViewBtn = (ImageView) findViewById(R.id.signupPageCircleImgView);
        EditText userName = (EditText) findViewById(R.id.signupPageUserNameEditText);
        EditText email = (EditText) findViewById(R.id.signupPageEmailEditText);
        EditText password = (EditText) findViewById(R.id.signupPagePassEditText);
        EditText confirmPassword = (EditText) findViewById(R.id.signupPageConfirmPassEditText);
        CheckBox checkBox = (CheckBox) findViewById(R.id.signupPageCheckbox);
        Button signupBtn = (Button) findViewById(R.id.signupPageSignupBtn);
        Button signupWithPhoneBtn = (Button) findViewById(R.id.signupPageSignupWithPhoneBtn);
        TextView errorMess = (TextView) findViewById(R.id.signupPageErrorMessage);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.signupPageProgressBar);
        TextView termsOfServiceBtn = (TextView) findViewById(R.id.signupPageTermsOfServiceTextView);
        TextView privacyBtn = (TextView) findViewById(R.id.signupPagePrivacyPolicyTextView);


        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        signUpBtnClicked(signupBtn, userName, email, password, confirmPassword, checkBox, errorMess, progressBar);
        profileImageViewCLicked(profileCircleImageViewBtn);
        signupWithPhoneClicked(signupWithPhoneBtn);
        termsOfServiceOnClicked(termsOfServiceBtn);
        privacyPolicyClicked(privacyBtn);


        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent = new Intent(SignupPageActivity.this, ImgCropperActivity.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 101);
            }
        });
    }



    private void signUpBtnClicked(Button signupBtn, EditText username,
                                  EditText e_mail, EditText pass_word,
                                  EditText confirm_Pass, CheckBox checkBox,
                                  TextView errorMessage, ProgressBar progressBar){
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = username.getText().toString().trim();
                String email = e_mail.getText().toString().trim();
                String pass = pass_word.getText().toString().trim();
                String confirmPass = confirm_Pass.getText().toString().trim();


                if(TextUtils.isEmpty(email)){
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Add Email.");
                }
                else if (TextUtils.isEmpty(userName)){
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Add user name.");
                }

                else if(TextUtils.isEmpty(pass)){
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Add Password.");
                }

                else if(!pass.equals(confirmPass)){
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Password and Confirm Password must be the same.");
                }

                else if(!privacyCheckBoxIsChecked(checkBox)) {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Please agree Privacy Policy.");
                }

                else if(profileImageUri == null) {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Please add your profile image.");
                }

                else {
                    registerToFirebase(email, pass, userName, progressBar, errorMessage, profileImageUri);
                }

            }
        });
    }

    private Boolean privacyCheckBoxIsChecked(CheckBox privacyCheckBox){
        return privacyCheckBox.isChecked();
    }


    private void registerToFirebase(String Email, String Password,
                                    String UserName, ProgressBar progressBar,
                                    TextView errorMessage, Uri profileImageUri){
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    addImageToFirebaseStorage(UserName, Email, Password, profileImageUri);
                    navigateToHomePage();
                }
                else {
                    String error = task.getException().getMessage();
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(error);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }



    private void addImageToFirebaseStorage(String username, String email,
                                     String password, Uri profileImageUri){
        StorageReference image_path = storageReference.child("user_profile_image").child(utils.getUserID() +".jpg");

        image_path.putFile(profileImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    addImagePathToFireBase(username, email, password, image_path);
                }
                else  {
                    Toast.makeText(SignupPageActivity.this, "failed add image to firebase storage.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    private void addImagePathToFireBase(String username, String email,
                                        String password, StorageReference image_path){
        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                addProfileInfoToFirestore(username, email, password, uri);
            }
        });
    }



    private void addProfileInfoToFirestore(String Username, String Email,
                                           String Password, Uri profileImageUri){

        ArrayList<String> postIds = new ArrayList<>();
        ArrayList<String> savedPosts = new ArrayList<>();
        ArrayList<String> friends = new ArrayList<>();

        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("userName", Username);
        dataMap.put("password", Password);
        dataMap.put("email", Email);
        dataMap.put("phone", "");
        dataMap.put("uid", utils.getUserID());
        dataMap.put("profile_img", profileImageUri.toString());
        dataMap.put("postIds", postIds);
        dataMap.put("savedPosts", savedPosts);
        dataMap.put("friends", friends);
        dataMap.put("status", "offline");



        firebaseFirestore.collection("user_profile_info")
                .document(utils.getUserID())
                .set(dataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(SignupPageActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.getMessage();
                Toast.makeText(SignupPageActivity.this, "Sign up unsuccessful: " + error, Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void profileImageViewCLicked(ImageView imageView){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askingPermissions();
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
                permissionChecker = permissionChecker + ContextCompat.checkSelfPermission(getApplicationContext(), permissionStr);

            }
            if (permissionChecker != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, permissionArr, requestCode);
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
                profileImageUri = Uri.parse(result);


                Glide.with(getApplicationContext())
                        .load(profileImageUri)
                        .into(profileCircleImageViewBtn);


            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int permissionChecker = PackageManager.PERMISSION_GRANTED;

        for (int permission : grantResults)
            permissionChecker = permissionChecker + permission;

        if (permissionChecker == PackageManager.PERMISSION_GRANTED && grantResults.length > 0)
            onPermissionGranted(requestCode);
        else
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();;

    }


    private void signupWithPhoneClicked(Button btn){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupPageActivity.this, SignupWithPhone.class);
                startActivity(intent);
            }
        });
    }



    private void termsOfServiceOnClicked(TextView btn){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupPageActivity.this, TermsOfServiceActivity.class);
                startActivity(intent);
            }
        });
    }


    private void privacyPolicyClicked(TextView btn){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupPageActivity.this, PrivacyActivity.class);
                startActivity(intent);
            }
        });
    }





    private void navigateToHomePage(){
        Intent intent = new Intent(SignupPageActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}