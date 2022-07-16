//package com.akGroup.englishspeakinglessons.adapter;
//
//import android.content.Context;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentPagerAdapter;
//
//import com.akGroup.englishspeakinglessons.model.Conversation;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//
//public class Con_PracticePagerAdapter extends FragmentPagerAdapter {
//    private ArrayList<Fragment> fragmentArrayList;
//    private Conversation conversationObj;
//
//    public Con_PracticePagerAdapter(@NonNull FragmentManager fm, int behavior,Conversation conversationObj) {
//        super(fm, behavior);
//        fragmentArrayList = new ArrayList<>();
//        this.conversationObj = conversationObj;
//    }
//
//    @NonNull
//    @NotNull
//    @Override
//    public Fragment getItem(int position) {
//        return fragmentArrayList.get(position);
//    }
//
//
//    @Override
//    public int getCount() {
//        return fragmentArrayList.size();
//    }
//
//
//    public void addFragment(Fragment fragment){
//        Bundle bundle = new Bundle();
//        //add your ArrayList data in bundle
//        bundle.putParcelable("bundle_key", conversationObj);
//        fragment.setArguments(bundle);
//        fragmentArrayList.add(fragment);
//    }
//}
