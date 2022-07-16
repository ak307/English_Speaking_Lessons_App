package com.akGroup.englishspeakinglessons.api.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.api.activity.Fill_OTP_Signup_Activity;
import com.hbb20.CountryCodePicker;

public class LoginWithPhoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_phone);


        CountryCodePicker countryCodePicker = (CountryCodePicker) findViewById(R.id.logInWithPhoneCountryCodePicker);
        EditText phone =(EditText) findViewById(R.id.loginWithPhonesEnterPhoneNoEditText);
        TextView errorText =(TextView) findViewById(R.id.loginWithPhonePageErrorMassage);
        Button getOTPBtn =(Button) findViewById(R.id.loginWithPhoneGetOTPBtn);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        getOTPBtnClicked(getOTPBtn, phone, countryCodePicker, errorText);
        errorText.setVisibility(View.INVISIBLE);

    }

    private void getOTPBtnClicked(Button button, TextView phoneNum,
                                  CountryCodePicker countryCodePicker,
                                  TextView errorText){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PhoneNum = phoneNum.getText().toString().trim();
                String countryCode = countryCodePicker.getFullNumber();
                String fullPhoneNumber = "+" + countryCode + PhoneNum;
                if (TextUtils.isEmpty(PhoneNum) || TextUtils.isEmpty(countryCode)){
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText("Please add Phone number");
                }
                else {
                    sendToVerificationCodeActivity(fullPhoneNumber);
                }
            }
        });
    }


    private void sendToVerificationCodeActivity(String phoneNo){
        Intent intent = new Intent(getApplicationContext(), Fill_OTP_login_Activity.class);
        intent.putExtra("phoneNo", phoneNo);
        startActivity(intent);
    }
}