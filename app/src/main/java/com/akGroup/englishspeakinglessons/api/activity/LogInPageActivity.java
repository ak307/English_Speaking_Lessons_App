package com.akGroup.englishspeakinglessons.api.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akGroup.englishspeakinglessons.MainActivity;
import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInPageActivity extends AppCompatActivity {
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_page);


        ImageView logoImgView = (ImageView) findViewById(R.id.loginPageLogoImageView);
        EditText email = (EditText) findViewById(R.id.loginPageEmailEditText);
        EditText password = (EditText) findViewById(R.id.loginPagePasswordEditText);
        TextView errorTextview = (TextView) findViewById(R.id.loginPageErrorText);
        TextView forgotPassBtn = (TextView) findViewById(R.id.loginPageForgotPassTextView);
        Button loginBtn = (Button) findViewById(R.id.loginPageLogInBtn);
        Button signupBtn = (Button) findViewById(R.id.loginPageSignUpBtn);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loginPageProgressBar);
        Button loginWithPhoneBtn = (Button) findViewById(R.id.loginPageLogInWithPhoneBtn);


        auth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.INVISIBLE);
        errorTextview.setVisibility(View.INVISIBLE);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        loginBtnClicked(loginBtn, progressBar, email, password, errorTextview);
        forgotPassBtnClicked(forgotPassBtn);
        signUpBtnClicked(signupBtn);
        loginWithPhoneBtnClicked(loginWithPhoneBtn);
    }


    private void loginBtnClicked(Button logInButton, ProgressBar progressBar,
                                 TextView emailText, TextView passwordText,
                                 TextView errorTextView){
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = emailText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Please fill all fields.");
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.bringToFront();

                    signInToFirebase(email, password, progressBar, errorTextView);
                }
            }
        });
    }


    private void signInToFirebase(String email, String password,
                                  ProgressBar progressBar, TextView errorTextView){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.INVISIBLE);
                            navigateToHomePage();
                        }

                        else  {
                            String errorMessage = task.getException().getMessage();
                            errorTextView.setVisibility(View.VISIBLE);
                            errorTextView.setText(errorMessage);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }


    private void navigateToHomePage(){
        Intent intent = new Intent(LogInPageActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }



    private  void forgotPassBtnClicked(TextView forgotPassBtn){
        forgotPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInPageActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }


    private void signUpBtnClicked(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInPageActivity.this, SignupPageActivity.class);
                startActivity(intent);
            }
        });
    }




    private void loginWithPhoneBtnClicked(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInPageActivity.this, LoginWithPhoneActivity.class);
                startActivity(intent);
            }
        });
    }



}