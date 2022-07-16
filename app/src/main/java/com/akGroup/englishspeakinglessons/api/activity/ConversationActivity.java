package com.akGroup.englishspeakinglessons.api.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.api.fragment.ListenFragment;
import com.akGroup.englishspeakinglessons.api.fragment.PracticeFragment;
import com.akGroup.englishspeakinglessons.api.fragment.SaveFragment;
import com.akGroup.englishspeakinglessons.model.Conversation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ConversationActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationview;
    private Fragment listenFragment;
    private Fragment practiceFragment;
    private Fragment saveFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        bottomNavigationview = (BottomNavigationView) findViewById(R.id.ConversationPageBottomNavigationView);

        listenFragment = new ListenFragment();
        practiceFragment = new PracticeFragment();
        saveFragment = new SaveFragment();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        replaceFragment(listenFragment);

        setBottomNavigationView();
    }


    private void setBottomNavigationView(){
        bottomNavigationview.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.listenNav:
                        replaceFragment(listenFragment);
                        return true;
                    case R.id.practiceNav:
                        replaceFragment(practiceFragment);
                        return true;
                    case R.id.saveNav:
                        replaceFragment(saveFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment){
        Bundle bundle = new Bundle();
        //put your ArrayList data in bundle
        bundle.putParcelable("bundle_key", conversationData());
//        bundle.putSerializable("classCode", getClassCodeFromClassroomFragment());
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.ConversationPageMainContainer, fragment);
        fragmentTransaction.commit();
    }



    private Conversation conversationData(){
        return getDataFromConversationListActivity();
    }


    private Conversation getDataFromConversationListActivity(){
        return getIntent().getParcelableExtra("conversation");
    }
}