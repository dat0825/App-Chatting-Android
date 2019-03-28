package com.example.datvtd.chatting;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        this.resetButton = findViewById(R.id.b_reset_pass);
        this.emailResetPassText = findViewById(R.id.text_email_reset_pass);
        this.firebaseAuth = FirebaseAuth.getInstance();

        this.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailResetPassText.getText().toString();

                if (email.equals("")) {
                    Toast.makeText(ResetPassActivity.this, "Fill email", Toast.LENGTH_LONG).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPassActivity.this, "Check your email", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ResetPassActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(ResetPassActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private Button resetButton;
    private EditText emailResetPassText;
    private FirebaseAuth firebaseAuth;
}
