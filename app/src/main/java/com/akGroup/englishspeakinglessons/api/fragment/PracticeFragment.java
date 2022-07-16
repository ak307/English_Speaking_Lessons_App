package com.akGroup.englishspeakinglessons.api.fragment;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.adapter.SocialViewPagerAdapter;
import com.akGroup.englishspeakinglessons.model.Conversation;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class PracticeFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<String> tabBarTitleList = new ArrayList<>();
    private FragmentManager childFragManager;




    public PracticeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_practice, container, false);


        ImageView titleImgView = (ImageView) view.findViewById(R.id.practiceConversationImgView);
        TextView title = (TextView) view.findViewById(R.id.practiceConversationTitle);
        tabLayout = (TabLayout) view.findViewById(R.id.tabBar);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);


        childFragManager = getChildFragmentManager();


        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        setUpPracticeFragment();
        addDataToTitle(title);
        addDataToTitleImg(titleImgView);


        return view;
    }



    private void setUpPracticeFragment(){
        SocialViewPagerAdapter adapter = new SocialViewPagerAdapter(childFragManager,
                SocialViewPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.addFragment(new FirstPersonFragment(), "", conversationData());
        adapter.addFragment(new SecondPersonFragment(), "", conversationData());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);



        TabLayout.Tab tab1 = tabLayout.getTabAt(0);
        TabLayout.Tab tab2 = tabLayout.getTabAt(1);

        assert tab1 != null;
        tab1.setText("with " + conversationData().getFirstPersonConversationBy());

        assert tab2 != null;
        tab2.setText("with " + conversationData().getSecondPersonConversationBy());


    }



    private void addDataToTitle(TextView title){
        title.setText(conversationData().getTitle());
    }

    private void addDataToTitleImg(ImageView imageView){
        if (!conversationData().getImageUrl().equals("")) {
            Glide.with(getContext())
                    .load(Uri.parse(conversationData().getImageUrl()))
                    .into(imageView);
        }
    }


    private Conversation conversationData(){
        return getDataFromConversationListActivity();
    }


    private Conversation getDataFromConversationListActivity(){
        return getArguments().getParcelable("bundle_key");
    }

}