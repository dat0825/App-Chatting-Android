package com.example.datvtd.chatting;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.datvtd.chatting.Adapter.UserAdapter;
import com.example.datvtd.chatting.Animation.SwipeController;
import com.example.datvtd.chatting.Animation.SwipeControllerActions;
import com.example.datvtd.chatting.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InfoGroupActivity extends AppCompatActivity {
    public static List<User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_group);
        this.headerText = findViewById(R.id.text_member);
        this.backButton = findViewById(R.id.b_back);
        this.changeColorButton = findViewById(R.id.b_change_color);
        this.addPersonButton = findViewById(R.id.b_add_person);
        this.recyclerView = findViewById(R.id.recycler_view);
        this.recyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);
        this.mUsers = new ArrayList<>();
        this.intent = getIntent();
        this.idGroup = new MessageActivity().bundle.getString("idGroup");
        this.color = new UserAdapter().color;
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (this.idGroup != null) { // khong dung duoc .equal(null)
            showMember();
            setupSwipe();
        } else {
            this.addPersonButton.setVisibility(View.GONE);
            this.headerText.setVisibility(View.GONE);
            this.recyclerView.setVisibility(View.GONE);
        }

        if(this.color != null){
            if (!this.color.equals("default")) {
                updateColor(this.color);
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoGroupActivity.this, UserGroupActivity.class);
                intent.putExtra("checkAddPerson", "true");
                startActivity(intent);
            }
        });

        changeColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                intent = new Intent(InfoGroupActivity.this, DialogColorActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showMember() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatGroup")
                .child(idGroup).child("members");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    DatabaseReference reference = FirebaseDatabase.getInstance()
                            .getReference("Users").child(user.getId());
                    countUser++; // nó thực hiện xong cái hàm onDataChange phía trên trước rồi mới đến hàm onDataChange thứ 2
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshotMembers) {
                            if (countUser > mUsers.size()) {   // fix lỗi lặp lại user trong phần infoGroup
                                User userMember = dataSnapshotMembers.getValue(User.class);
                                mUsers.add(userMember);
                                //set recyclerView trong ham onDataChange
                                mUserAdapter = new UserAdapter(getApplicationContext(), mUsers, true, "true");
                                recyclerView.setAdapter(mUserAdapter);
                            }
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

    private void setupSwipe() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mUserAdapter);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onDeleteButtonClicked(int position) {
                User user = mUserAdapter.mUsers.get(position);
                removeUserGroup(user.getId());
            }

            @Override
            public void onPromoteButtonClicked(int position) {
                User user = mUserAdapter.mUsers.get(position);
                changeAdmin(user.getId());
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
                swipeController.onDraw(c);
            }
        });
    }

    public void removeUserGroup(String idUserRemove) {
        Intent intent = new Intent(InfoGroupActivity.this, DialogConfirmActivity.class);
        intent.putExtra("idUserRemove", idUserRemove);
        intent.putExtra("kind", "remove");
        intent.putExtra("changeNameGroup", "false");
        startActivity(intent);
    }

    public void changeAdmin(String idUser) {
        Intent intent = new Intent(InfoGroupActivity.this, DialogConfirmActivity.class);
        intent.putExtra("idUserPromote", idUser);
        intent.putExtra("kind", "promote");
        intent.putExtra("changeNameGroup", "false");
        startActivity(intent);
    }

    public void updateColor(String colorValue) {
        this.backButton.setColorFilter(Color.parseColor(colorValue));
        this.addPersonButton.setColorFilter(Color.parseColor(colorValue));
    }

    private RecyclerView recyclerView;
    private Intent intent;
    private UserAdapter mUserAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView backButton;
    private ImageView addPersonButton;
    private TextView headerText;
    private Button changeColorButton;
    private String idGroup;
    private String color;
    private int countUser = 0;
    private FirebaseUser firebaseUser;
    private SwipeController swipeController = null;
}