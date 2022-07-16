package com.akGroup.englishspeakinglessons.api.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.akGroup.englishspeakinglessons.R;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class SignupWithPhone extends AppCompatActivity {
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_with_phone);


        ImageView logo = (ImageView) findViewById(R.id.signupPhonePageImageView);
        CountryCodePicker countryCodePicker = (CountryCodePicker) findViewById(R.id.signupPhoneCountryCodePicker);
        EditText phoneNo = (EditText) findViewById(R.id.signupPhonesEnterPhoneNoEditText);
        CheckBox checkBox = (CheckBox) findViewById(R.id.signupPhonePageCheckbox);
        EditText userName = (EditText) findViewById(R.id.signupPhoneUsernameEditText);
        TextView privacyBtn = (TextView) findViewById(R.id.signupPagePrivacyPolicyTextView);
        TextView termsAndServiceBtn = (TextView) findViewById(R.id.signupPhonePageTermsOfServiceTextView);
        Button getOTPBtn = (Button) findViewById(R.id.signupPhoneGetOTPBtn);
        TextView errorMessage = (TextView) findViewById(R.id.SignupPhonePageErrorMassage);

        Auth = FirebaseAuth.getInstance();


        errorMessage.setVisibility(View.INVISIBLE);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        signUpBtnClicked(getOTPBtn, userName, countryCodePicker, phoneNo, errorMessage, checkBox);
        setPrivacyText(privacyBtn);
        setTermsOfServiceText(termsAndServiceBtn);
    }



    private void signUpBtnClicked(Button getOTPBtn, TextView username,
                                  CountryCodePicker countryCodePicker,
                                  TextView phoneNo, TextView errorMessage,
                                  CheckBox checkBox){
        getOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = username.getText().toString().trim();
                String countryCode = "+" + countryCodePicker.getFullNumber();
                String phoneNumber = phoneNo.getText().toString().trim();
                String fullPhoneNumber = countryCode + phoneNumber;


                if(TextUtils.isEmpty(userName)){
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Add username");
                }

                else if(TextUtils.isEmpty(phoneNumber)){
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Add Phone number.");
                }

                else if(!privacyCheckBoxIsChecked(checkBox)) {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Please agree Privacy Policy");
                }
                else {
                    sendToConfirmVerificationCodeSignUpActivity(fullPhoneNumber, userName);
                }

            }
        });
    }

    private Boolean privacyCheckBoxIsChecked(CheckBox privacyCheckBox){
        return privacyCheckBox.isChecked();
    }


    private void sendToConfirmVerificationCodeSignUpActivity(String phone, String userName){
        Intent intent = new Intent(SignupWithPhone.this, Fill_OTP_Signup_Activity.class);
        intent.putExtra("phoneNo", phone);
        intent.putExtra("username", userName);
        startActivity(intent);
    }


    private void setPrivacyText(TextView privacyText){
        privacyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPrivacyPage();
            }
        });
    }


    private void setTermsOfServiceText(TextView termsOfServiceText){
        termsOfServiceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToTermOfServicePage();
            }
        });
    }


    private void sendToTermOfServicePage(){
        Intent intent = new Intent(SignupWithPhone.this, TermsOfServiceActivity.class);
        startActivity(intent);
    }


    private void sendToPrivacyPage(){
        Intent intent = new Intent(SignupWithPhone.this, PrivacyActivity.class);
        startActivity(intent);
    }


}