package com.example.datvtd.chatting.Fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.datvtd.chatting.Adapter.UserAdapter;
import com.example.datvtd.chatting.Model.Chatlist;
import com.example.datvtd.chatting.Model.User;
import com.example.datvtd.chatting.Notifications.Token;
import com.example.datvtd.chatting.ProfileActivity;
import com.example.datvtd.chatting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        this.profileImage = view.findViewById(R.id.profile_image);
        this.recyclerView = view.findViewById(R.id.recycler_view);
        this.progressBar = view.findViewById(R.id.progressBar);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        progressBar.setVisibility(View.VISIBLE);
        setImageProfile();

        userChat();
        updateToken(FirebaseInstanceId.getInstance().getToken());

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ProfileActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void userChat() {
        this.userList = new ArrayList<>();
        this.reference = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(this.firebaseUser.getUid());
        this.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    userList.add(chatlist);
                }
                chatList();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void chatList() {
        this.mUsers = new ArrayList<>();
        this.reference = FirebaseDatabase.getInstance().getReference("Users");
        this.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (Chatlist chatlist : userList) {
                        if (user.getId() != null
                                && !user.getId().equals(chatlist.getId())) {
                            mUsers.add(user);
                            break;
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateToken(String token) {
        this.reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        this.reference.child(firebaseUser.getUid()).setValue(token1);
    }

    public void setImageProfile() {
        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isAdded()){        // tranh bi crash
                    User user = dataSnapshot.getValue(User.class);
                    if(user.getImageURL() != null){
                        if (user.getImageURL().equals("default")) {
                            profileImage.setImageResource(R.mipmap.ic_launcher_round);
                        } else {
                            Glide.with(ChatsFragment.this).load(user.getImageURL()).into(profileImage);
                        }
                    } else {
                        profileImage.setImageResource(R.mipmap.ic_launcher_round);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<Chatlist> userList;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private CircleImageView profileImage;
    private ProgressBar progressBar;
}