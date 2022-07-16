package com.akGroup.englishspeakinglessons;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.api.fragment.HomeFragment;
import com.akGroup.englishspeakinglessons.api.fragment.SettingFragment;
import com.akGroup.englishspeakinglessons.api.fragment.SocialFragment;
import com.akGroup.englishspeakinglessons.listeners.UsersListener;
import com.akGroup.englishspeakinglessons.model.User;
import com.akGroup.englishspeakinglessons.service.MessagingService;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements UsersListener {
    private BottomNavigationView bottomNavigationview;
    private Fragment homeFragment;
    private Fragment socialFragment;
    private Fragment settingFragment;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationview = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        homeFragment = new HomeFragment();
        socialFragment = new SocialFragment();
        settingFragment = new SettingFragment();
        utils = new Utils();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                replaceFragment(homeFragment);
            }
        });


        if (utils.isLogin())
            setToken();

        setBottomNavigationView();

    }



    private void setToken(){
        MessagingService service = new MessagingService();
        service.getToken();
    }


    private void setBottomNavigationView(){
        bottomNavigationview.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.homeNav :
                        replaceFragment(homeFragment);
                        return true;
                    case R.id.socialNav:
                        replaceFragment(socialFragment);
                        return true;
                    case R.id.settingNav:
                        replaceFragment(settingFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void initiateVideoMeeting(User user) {
        if (user.getToken() == null && user.getToken().isEmpty()){
            Toast.makeText(this, user.getUserName() + " is not available.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Video chat with: " + user.getUserName(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void initiateAudioMeeting(User user) {
        if (user.getToken() == null && user.getToken().isEmpty()){
            Toast.makeText(this, user.getUserName() + " is not available.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Audio chat with: " + user.getUserName(), Toast.LENGTH_SHORT).show();

        }
    }
}