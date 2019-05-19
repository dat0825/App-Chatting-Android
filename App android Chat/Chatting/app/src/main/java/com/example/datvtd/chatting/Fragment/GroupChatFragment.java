package com.example.datvtd.chatting.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.datvtd.chatting.Adapter.UserAdapter;
import com.example.datvtd.chatting.Model.GroupChat;
import com.example.datvtd.chatting.Model.User;
import com.example.datvtd.chatting.Notifications.Token;
import com.example.datvtd.chatting.ProfileActivity;
import com.example.datvtd.chatting.R;
import com.example.datvtd.chatting.UserGroupActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatFragment extends Fragment {
    public List<GroupChat> mGroupChats;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);
        mGroupChats = new ArrayList<>();
        mGroupChats.clear();
        this.searchGroupText = view.findViewById(R.id.text_search_group);
        this.creatGroupImage = view.findViewById(R.id.b_create_group);
        this.profileImage = view.findViewById(R.id.profile_image);
        this.recyclerView = view.findViewById(R.id.recycler_view);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        setImageProfile();

        updateToken(FirebaseInstanceId.getInstance().getToken());

        if (this.checkSearch == 0) { // để ngăn groupchat fragment lặp lại 2 hoặc nhiều lần
            readGroup();
            checkSearch++;
        }

        searchGroupText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    searchGroup(s.toString());
                }

                if (s.toString().equals("")) {
                    checkSearch++;
                    if (checkSearch > 1) {
                        readGroup();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        this.creatGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserGroupActivity.class);
                startActivity(intent);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void readGroup() {
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatGroup");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mGroupChats.clear();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final GroupChat groupChat = snapshot.getValue(GroupChat.class);

                    //xet 1 group xem nguoi dung hien tai co o trong do k de hien thi
                    DatabaseReference referenceMember = reference.child(groupChat.getIdGroup()).child("members");
                    referenceMember.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                                mGroupChats.add(groupChat);
                            }
                            Log.d("afterread", String.valueOf(mGroupChats.size()));
                            mUserAdapter = new UserAdapter(getContext(), mGroupChats, true, true);
                            recyclerView.setAdapter(mUserAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void searchGroup(final String s) {
        mGroupChats.clear();
        final Query query = FirebaseDatabase.getInstance().getReference("ChatGroup").orderByChild("nameGroup")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!s.equals("")) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        GroupChat groupChat = snapshot.getValue(GroupChat.class);
                        assert groupChat != null;
                        mGroupChats.add(groupChat);
                    }
                }
                mUserAdapter = new UserAdapter(getContext(), mGroupChats, false, true);
                recyclerView.setAdapter(mUserAdapter);
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
                if(isAdded()){     // tranh bi crash
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getImageURL().equals("default")) {
                        profileImage.setImageResource(R.mipmap.ic_launcher_round);
                    } else {
                        Glide.with(GroupChatFragment.this).load(user.getImageURL()).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private RecyclerView recyclerView;
    private EditText searchGroupText;
    private ImageView creatGroupImage;
    private CircleImageView profileImage;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private UserAdapter mUserAdapter;
    private int checkSearch = 0;
}
