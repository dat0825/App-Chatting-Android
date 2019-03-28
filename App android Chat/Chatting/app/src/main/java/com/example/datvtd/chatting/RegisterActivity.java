package com.example.datvtd.chatting;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.userNameEditText = findViewById(R.id.txt_user);
        this.emailEditText = findViewById(R.id.txt_email);
        this.passEditText = findViewById(R.id.txt_password);
        this.registerButton = findViewById(R.id.b_Regiser);
        this.auth = FirebaseAuth.getInstance();

        this.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passEditText.getText().toString();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Fill all fileds", Toast.LENGTH_LONG).show();
                } else {
                    register(userName, email, password);
                }
            }
        });
    }

    public void register(final String userName, String email, String password) {
        this.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userID = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userID);
                            hashMap.put("user", userName);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Cannot Register", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private EditText userNameEditText;
    private EditText emailEditText;
    private EditText passEditText;
    private Button registerButton;
    private FirebaseAuth auth;
    private DatabaseReference reference;
}
