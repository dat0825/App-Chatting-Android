package com.example.datvtd.chatting;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.datvtd.chatting.Fragment.ChatsFragment;
import com.example.datvtd.chatting.Fragment.GroupChatFragment;
import com.example.datvtd.chatting.Fragment.ProfileFragment;
import com.example.datvtd.chatting.Fragment.UsersFragment;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tabLayout = findViewById(R.id.tab_layout);
        this.viewPager = findViewById(R.id.view_pager);

        ViewpagerAdpater viewpagerAdpater = new ViewpagerAdpater(getSupportFragmentManager());
        viewpagerAdpater.addFragment(new ChatsFragment(), "Chats");
        viewpagerAdpater.addFragment(new GroupChatFragment(), "Group Chat");
        viewpagerAdpater.addFragment(new UsersFragment(), "Users");
        viewpagerAdpater.addFragment(new ProfileFragment(), "Profile");
        this.viewPager.setAdapter(viewpagerAdpater);
        this.tabLayout.setupWithViewPager(viewPager);
    }

    //set trang thai online hay offline
    public void status(String status) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            reference.updateChildren(hashMap);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    public void onPause() {
        super.onPause();
        status("offline");
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        status("offline");
//    }

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
}

class ViewpagerAdpater extends FragmentPagerAdapter {
    public ArrayList<Fragment> fragments = new ArrayList<>();
    public ArrayList<String> titles = new ArrayList<>();

    public ViewpagerAdpater(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        titles.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}