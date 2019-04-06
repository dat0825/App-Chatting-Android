package com.example.datvtd.chatting;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.datvtd.chatting.Fragment.ChatsFragment;
import com.example.datvtd.chatting.Fragment.GroupChatFragment;
import com.example.datvtd.chatting.Fragment.ProfileFragment;
import com.example.datvtd.chatting.Fragment.UsersFragment;
import com.example.datvtd.chatting.Model.User;
import com.example.datvtd.chatting.Notifications.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        com.bubble.android.CustomClass.CustomViewPager customViewPager;
        this.profileImage = findViewById(R.id.profile_image);
        this.nameFragment = findViewById(R.id.text_name_fragment);
        this.tabLayout = findViewById(R.id.tab_layout);
        this.viewPager = findViewById(R.id.view_pager);
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setHeader();

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_chat_in_tab));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_group_chat_in_tab));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_user_in_tab));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_user_in_tab));


        ViewpagerAdpater viewpagerAdpater = new ViewpagerAdpater(getSupportFragmentManager());
        //có thể tùy biên hàm addFragment ở phía dươi.( set không title...)
        viewpagerAdpater.addFragment(new ChatsFragment(),"1");
        viewpagerAdpater.addFragment(new GroupChatFragment(),"2");
        viewpagerAdpater.addFragment(new UsersFragment(),"3");
        viewpagerAdpater.addFragment(new ProfileFragment(),"4");


        // 2 dòng dưới dùng để set cho các tab mà có dùng title.
        //Muốn set title cho các layout thì cần phải active thêm hàm  public CharSequence getPageTitle(int position) ở dưới.
//        this.viewPager.setAdapter(viewpagerAdpater);
//        this.tabLayout.setupWithViewPager(viewPager);

        // Dùng để set cho các tab dùng icon
        viewPager.setAdapter(viewpagerAdpater);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        viewPager.setOffscreenPageLimit(0);

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

    public void setHeader() {
        this.reference = FirebaseDatabase.getInstance().
                getReference("Users").child(firebaseUser.getUid());
        this.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

//                add ảnh vào profileImage
                if (user.getImageURL() != null
                        && user.getImageURL().equals("default")) {
                    profileImage.setImageResource(R.mipmap.ic_launcher_round);
                } else {
                    Glide.with(MainActivity.this).load(user.getImageURL()).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private CircleImageView profileImage;
    public TextView nameFragment;
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