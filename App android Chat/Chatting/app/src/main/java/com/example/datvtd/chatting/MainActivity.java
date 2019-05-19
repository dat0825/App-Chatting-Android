package com.example.datvtd.chatting;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.datvtd.chatting.Fragment.ChatsFragment;
import com.example.datvtd.chatting.Fragment.GroupChatFragment;
import com.example.datvtd.chatting.Fragment.UsersFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinch.android.rtc.Sinch;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tabLayout = findViewById(R.id.tab_layout);
        this.viewPager = findViewById(R.id.view_pager);
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.ic_chat_in_tab));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_group_chat_in_tab));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_user_in_tab));

        ViewpagerAdpater viewpagerAdpater = new ViewpagerAdpater(getSupportFragmentManager());
        //có thể tùy biên hàm addFragment ở phía dươi.( set không title...)
        viewpagerAdpater.addFragment(new ChatsFragment(), "1");
        viewpagerAdpater.addFragment(new GroupChatFragment(), "2");
        viewpagerAdpater.addFragment(new UsersFragment(), "3");


        // 2 dòng dưới dùng để set cho các tab mà có dùng title.
        //Muốn set title cho các layout thì cần phải active thêm hàm  public CharSequence getPageTitle(int position) ở dưới.
//        this.viewPager.setAdapter(viewpagerAdpater);
//        this.tabLayout.setupWithViewPager(viewPager);

        // Dùng để set cho các tab dùng icon
        viewPager.setAdapter(viewpagerAdpater);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        while(new CallActivity().sinchClient == null){
            setupSinch();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(new CallActivity().sinchClient != null){
            Toast.makeText(getApplicationContext(),"sinch client connected",Toast.LENGTH_LONG).show();
            Log.d("sinchConnect","connected");
        }
    }

    //set trang thai online hay offline
    public void status(String status) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            reference.updateChildren(hashMap);
        }
    }

    public void setupSinch() {
        new CallActivity().sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(firebaseUser.getUid())
                .applicationKey("08c39146-6d90-41b2-8c1d-2e76a03ea62b")
                .applicationSecret("LLRk8KPO6katlsOjm6VRZw==")
                .environmentHost("clientapi.sinch.com")
                .build();

        new CallActivity().sinchClient.setSupportCalling(true);
        new CallActivity().sinchClient.startListeningOnActiveConnection();
        new CallActivity().sinchClient.start();
        new CallActivity().sinchClient.getCallClient().addCallClientListener(new CallActivity.SinchCallClientListener());
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

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return titles.get(position);
//    }
}