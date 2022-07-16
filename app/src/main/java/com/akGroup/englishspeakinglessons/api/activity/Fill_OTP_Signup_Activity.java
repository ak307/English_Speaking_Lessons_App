package com.akGroup.englishspeakinglessons.api.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.MainActivity;
import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Fill_OTP_Signup_Activity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private FirebaseAuth Auth;
    private FirebaseFirestore firebaseFirestore;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private Utils utils = new Utils();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_signup_otp);

        EditText otpEditText = (EditText) findViewById(R.id.OTPCodeEditText);
        TextView resendBtn = (TextView) findViewById(R.id.resendBtn);
        Button submitBtn = (Button) findViewById(R.id.submitBtn);

        Auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Verifying OTP......");
        progressDialog.setCanceledOnTouchOutside(false);




        sendVerifyCode(getPhoneNo());

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        resendBtnClicked(resendBtn);
        submitBtnClicked(submitBtn, otpEditText);


    }

    private void submitBtnClicked(Button verifyBtn, EditText otpInput){
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otpInput.getText().toString().trim();
                if (TextUtils.isEmpty(code)){
                    Toast.makeText(Fill_OTP_Signup_Activity.this, "Please enter verification code", Toast.LENGTH_LONG).show();
                }
                else {
                    progressDialog.show();
                    verifyCode(code);
                }
            }
        });
    }




    private void resendBtnClicked(TextView resendBtn){
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerifyCode(getPhoneNo());
                Toast.makeText(Fill_OTP_Signup_Activity.this, "OPT sent", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendVerifyCode(String phoneNo){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(Auth)
                        .setPhoneNumber(phoneNo)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    String code = credential.getSmsCode();
                    if (code != null){
                        progressDialog.show();
                        verifyCode(code);
                    }
                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(Fill_OTP_Signup_Activity.this, "error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.

                    // Save verification ID and resending token so we can use them later
                    mVerificationId = verificationId;
                    mResendToken = token;
                }
            };


    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        Auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    addProfileInfoToFirestore(getUserName(), getPhoneNo());
                }
                else {
                    Toast.makeText(Fill_OTP_Signup_Activity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void addProfileInfoToFirestore(String Username, String phone){

        ArrayList<String> postIds = new ArrayList<>();
        ArrayList<String> savedPosts = new ArrayList<>();
        ArrayList<String> friends = new ArrayList<>();



        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("userName", Username);
        dataMap.put("password", "");
        dataMap.put("email", "");
        dataMap.put("phone", phone);
        dataMap.put("uid", utils.getUserID());
        dataMap.put("profile_img", "");
        dataMap.put("postIds", postIds);
        dataMap.put("savedPosts", savedPosts);
        dataMap.put("friends", friends);
        dataMap.put("status", "offline");



        firebaseFirestore.collection("user_profile_info").document(utils.getUserID())
                .set(dataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendToHomePage();
                progressDialog.dismiss();
                Toast.makeText(Fill_OTP_Signup_Activity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
            }

        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.getMessage();
                Toast.makeText(Fill_OTP_Signup_Activity.this, "Sign up unsuccessful: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void sendToHomePage(){
        Intent intent = new Intent(Fill_OTP_Signup_Activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }




    private String getPhoneNo(){
        String exerciseDocID = getIntent().getStringExtra("phoneNo");
        return exerciseDocID;
    }



    private String getUserName(){
        String exerciseDocID = getIntent().getStringExtra("username");
        return exerciseDocID;
    }



}