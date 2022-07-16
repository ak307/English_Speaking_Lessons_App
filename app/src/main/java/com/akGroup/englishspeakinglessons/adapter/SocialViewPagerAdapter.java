package com.akGroup.englishspeakinglessons.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.akGroup.englishspeakinglessons.model.Conversation;
import com.akGroup.englishspeakinglessons.model.User;

import java.util.ArrayList;

public class SocialViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private ArrayList<String> titleArraylist = new ArrayList<>();

    public SocialViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleArraylist.get(position);
    }


    public void addFragment (Fragment fragment, String title, User user){

        Bundle bundle = new Bundle();
        //add your ArrayList data in bundle
        bundle.putParcelable("user_bundle_key", user);
        fragment.setArguments(bundle);

        fragmentArrayList.add(fragment);
        titleArraylist.add(title);
    }


    public void addFragment (Fragment fragment, String title, Conversation conversationObj){

        Bundle bundle = new Bundle();
        //add your ArrayList data in bundle
        bundle.putParcelable("bundle_key", conversationObj);
        fragment.setArguments(bundle);

        fragmentArrayList.add(fragment);
        titleArraylist.add(title);
    }
}
