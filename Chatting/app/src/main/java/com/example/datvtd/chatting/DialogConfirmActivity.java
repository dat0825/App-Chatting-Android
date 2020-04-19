package com.example.datvtd.chatting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DialogConfirmActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        setTitle("");
        this.buttonYes = findViewById(R.id.b_yes);
        this.buttonNo = findViewById(R.id.b_no);
        this.nameGroupEditText = findViewById(R.id.text_name_group);
        this.headerConfirmTextView = findViewById(R.id.text_title);
        this.intent = getIntent();
        this.idUserRemove = intent.getStringExtra("idUserRemove");
        this.idUserPromote = intent.getStringExtra("idUserPromote");
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.idGroup = MessageActivity.bundle.getString("idGroup");
        this.kind = intent.getStringExtra("kind");
        this.changNameGroup = intent.getStringExtra("changeNameGroup");

        if (this.changNameGroup.equals("false")) {
            setViewConfirm();
        } else {
            setViewChangeNameGroup();
        }
    }

    public void setViewConfirm() {
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kind.equals("remove")) {
                    removeUser();
                } else {
                    promoteUser();
                }
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(DialogConfirmActivity.this, InfoGroupActivity.class);
//                intent.putExtra("idGroup", idGroup);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
                onBackPressed(); // quay lại layout trước và xóa luôn layout này
            }
        });
    }

    public void setViewChangeNameGroup() {
        headerConfirmTextView.setVisibility(View.INVISIBLE);
        nameGroupEditText.setVisibility(View.VISIBLE);
        reference = FirebaseDatabase.getInstance().getReference("ChatGroup")
                .child(idGroup).child("nameGroup");

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nameGroupEditText.getText().equals(null)) {
                    reference.setValue(nameGroupEditText.getText().toString());
                    Intent intent = new Intent(DialogConfirmActivity.this,MessageActivity.class);
                    intent.putExtra("checkGroup","true");
                    intent.putExtra("idGroup",idGroup);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DialogConfirmActivity.this,MessageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void removeUser() {
        reference = FirebaseDatabase.getInstance().getReference("ChatGroup")
                .child(idGroup).child("members").child(idUserRemove);
        reference.removeValue();

        if (firebaseUser.getUid().equals(idUserRemove)) {
            Intent intent = new Intent(DialogConfirmActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(DialogConfirmActivity.this, InfoGroupActivity.class);
            intent.putExtra("idGroup", idGroup);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void promoteUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatGroup").child(idGroup);
        HashMap hashMap = new HashMap();
        hashMap.put("admin", idUserPromote);
        reference.updateChildren(hashMap);

        MessageActivity.bundle.putString("adminGroup", idUserPromote);  //update lại admin của nhóm chat

        Intent intent = new Intent(DialogConfirmActivity.this, InfoGroupActivity.class);
        intent.putExtra("idGroup", idGroup);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private Button buttonYes;
    private Button buttonNo;
    private Intent intent;
    private String idUserRemove;
    private String idUserPromote;
    private String idGroup;
    private String kind;
    private String changNameGroup = "false";
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private TextView headerConfirmTextView;
    private EditText nameGroupEditText;
}
