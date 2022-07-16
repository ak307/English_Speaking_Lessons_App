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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class Fill_OTP_login_Activity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private FirebaseAuth Auth;
    private FirebaseFirestore firebaseFirestore;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private Utils utils = new Utils();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_otp_login);

        EditText otpEditText = (EditText) findViewById(R.id.loginOTPCodeEditText);
        TextView resendBtn = (TextView) findViewById(R.id.loginOTPResendBtn);
        Button submitBtn = (Button) findViewById(R.id.loginOTPsubmitBtn);

        Auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Verifying OTP.....");
        progressDialog.setCanceledOnTouchOutside(false);


        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sendVerifyCode(getPhoneNum());
        resendBtnClicked(resendBtn);
        submitBtnClicked(submitBtn, otpEditText);


    }


    private void resendBtnClicked(TextView resendBtn){
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerifyCode(getPhoneNum());
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
                    Toast.makeText(Fill_OTP_login_Activity.this, "error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.

                    // Save verification ID and resending token so we can use them later
                    mVerificationId = verificationId;
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
                    sendToHomePage();
                }
                else {
                    Toast.makeText(Fill_OTP_login_Activity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void submitBtnClicked(Button verifyBtn, EditText otpInput){
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otpInput.getText().toString().trim();
                if (TextUtils.isEmpty(code)){
                    Toast.makeText(Fill_OTP_login_Activity.this, "Please enter verification code", Toast.LENGTH_LONG).show();
                }
                else {
                    progressDialog.show();
                    verifyCode(code);
                }
            }
        });
    }




    private void sendToHomePage(){
        Intent intent = new Intent(Fill_OTP_login_Activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private String getPhoneNum(){
        String phoneNo = getIntent().getStringExtra("phoneNo");
        return phoneNo;
    }

}