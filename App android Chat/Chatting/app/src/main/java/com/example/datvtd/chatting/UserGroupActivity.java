package com.example.datvtd.chatting;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.datvtd.chatting.Adapter.UserGroupAdapter;
import com.example.datvtd.chatting.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_group);
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.mUsers = new ArrayList<>();
        this.createGroupButton = findViewById(R.id.buton_createGroup);
        this.recyclerView = findViewById(R.id.recycler_view);
        this.recyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);
        this.nameGroupText = findViewById(R.id.name_Group);
        this.searchUserText = findViewById(R.id.text_search_user);
        this.intent = getIntent();
        if (intent.getExtras() != null) {
            this.checkAddPerson = intent.getStringExtra("checkAddPerson");
            this.idGroupAddPerson = MessageActivity.bundle.getString("idGroup");
        }

        if (this.checkAddPerson.equals("true")) {
            this.nameGroupText.setVisibility(View.INVISIBLE);
            this.createGroupButton.setText("Add");
            readUserAddGroup();
            createGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addUserGroup();
                }
            });
        } else {
            readUser();
            this.createGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    creatGroup();
                }
            });
        }

        this.searchUserText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                readUser();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void readUser() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                // set cho view tao group
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }
                mAdapter = new UserGroupAdapter(getApplicationContext(), mUsers);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void readUserAddGroup() {
        mUsers.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final User user = snapshot.getValue(User.class);

                    DatabaseReference referenceMembers = FirebaseDatabase.getInstance().getReference("ChatGroup")
                            .child(idGroupAddPerson).child("members");
                    referenceMembers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.child(user.getId()).exists()) {
                                mUsers.add(user);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                mAdapter = new UserGroupAdapter(getApplicationContext(), mUsers);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void searchUser(String s) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("user")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    assert firebaseUser != null;

                    if (!user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }
                mAdapter = new UserGroupAdapter(getApplicationContext(), mUsers);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void creatGroup() {
        if (mAdapter.countBoxChecked >= 2) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ChatGroup");
            String idGroup = reference.push().getKey();
            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put("idGroup", idGroup);
            hashMap.put("nameGroup", nameGroupText.getText().toString());
            hashMap.put("admin", firebaseUser.getUid());
            hashMap.put("imageGroupURL", "default");
            hashMap.put("color","default");
            reference.child(idGroup).setValue(hashMap);

            hashMap = new HashMap<>();
            for (int i = 0; i < mAdapter.countBoxChecked; i++) {
                hashMap.put("id", mAdapter.mCheckBoxes.get(i).getId());
                reference.child(idGroup).child("members").child(mAdapter.mCheckBoxes.get(i).getId()).updateChildren(hashMap);
            }
            hashMap.put("id", firebaseUser.getUid());
            reference.child(idGroup).child("members").child(firebaseUser.getUid()).updateChildren(hashMap);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            intent = new Intent(this, MessageActivity.class);
            intent.putExtra("idGroup", idGroup);
            intent.putExtra("nameGroup", nameGroupText.getText().toString());
            intent.putExtra("checkGroup", "true");
            intent.putExtra("adminGroup",firebaseUser.getUid());
            startActivity(intent);
            finish();
        }
    }

    public void addUserGroup(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ChatGroup")
                .child(this.idGroupAddPerson).child("members");

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap = new HashMap<>();
        for (int i = 0; i < mAdapter.countBoxChecked; i++) {
            hashMap.put("id", mAdapter.mCheckBoxes.get(i).getId());
            reference.child(mAdapter.mCheckBoxes.get(i).getId()).updateChildren(hashMap);
        }

        Intent intent = new Intent(UserGroupActivity.this,InfoGroupActivity.class);
        intent.putExtra("idGroup",idGroupAddPerson);
        startActivity(intent);
        finish();
    }

    private RecyclerView recyclerView;
    private UserGroupAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<User> mUsers;
    private Button createGroupButton;
    private EditText searchUserText;
    private EditText nameGroupText;
    private FirebaseUser firebaseUser;
    private Intent intent;
    private String checkAddPerson = "";
    private String idGroupAddPerson = "";
}
