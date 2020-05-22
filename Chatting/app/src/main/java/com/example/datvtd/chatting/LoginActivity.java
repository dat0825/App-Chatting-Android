package com.example.datvtd.chatting;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datvtd.chatting.Model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.Sinch;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_login);
        this.emailEditText = findViewById(R.id.txt_email);
        this.passwordEditText = findViewById(R.id.txt_password);
        this.loginButton = findViewById(R.id.b_Login);
        this.registerButton = findViewById(R.id.b_register);
        this.loginFacebookButton = findViewById(R.id.b_Login_facebook);
        this.resetPassTextView = findViewById(R.id.text_forgot_pass);
        this.auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);

        //auto login
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
//        keyhash(getApplicationContext());
        loginFacebook();

        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setCancelable(false);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_bar);
                final String email = emailEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Fill all field", Toast.LENGTH_LONG).show();
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class)
                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        this.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        this.resetPassTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPassActivity.class);
                startActivity(intent);
            }
        });
    }

    public void loginFacebook() {
        this.mCallbackManager = CallbackManager.Factory.create();
        this.loginFacebookButton.setReadPermissions(Arrays.asList("public_profile,email"));
        this.loginFacebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                getDataFacebook(loginResult);
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "oncancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //pass the activity result back to the Facebook sdk
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            updateUI();
        }
    }

    public void updateUI() {
        Toast.makeText(LoginActivity.this, "You're logged in", Toast.LENGTH_LONG).show();
        checkAccountFacebookExist();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void handleFacebookAccessToken(AccessToken token) {
//        Toast.makeText(LoginActivity.this,token.getToken(),Toast.LENGTH_LONG).show();
        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        this.auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_LONG).show();
                            firebaseUser = auth.getCurrentUser();
                            updateUI();
                        } else {
                            Toast.makeText(LoginActivity.this, "Faillll login facebook", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void getDataFacebook(final LoginResult loginResult) {
        final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String email = object.getString("email");
                            String firstname = object.getString("first_name");
                            String lastname = object.getString("last_name");
//                            String gender = object.getString("gender");
//                            String birthday = object.getString("user_birthday");
                            usernameFacebook = object.getString("name");
                            Profile profile = Profile.getCurrentProfile();
                            String id = profile.getId();
                            String link = profile.getLinkUri().toString();
                            Log.d("link_image", link);

                            if (Profile.getCurrentProfile() != null) {
                                Log.d("imagee", "Image: " + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                            }

                            Log.d("IDDD", id);
                            Log.d("Emaill", email);
                            Log.d("first_namee", firstname);
                            Log.d("last_namee", lastname);
                            Log.d("nameee", usernameFacebook);
//                            Log.d("genderrr",gender);
//                            Log.d("birthdayy",birthday);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("Failll", e.toString());
                        }
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,name,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void checkAccountFacebookExist() {
        final String ID = firebaseUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(ID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

//                    if (user.getId().equals(ID)) {
//                        Log.d("FAILLL", "FAILLL");
//                    } else {
                Log.d("Success", "FIGHTING");
                String linkImage = Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", ID);
                hashMap.put("user", usernameFacebook);
                hashMap.put("imageURL", linkImage);
                hashMap.put("status", "offline");
                reference.setValue(hashMap);
            }
//            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void keyhash(Context context) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                String Hashkey = new String(Base64.encode(messageDigest.digest(), 0));
            }
        } catch (NoSuchAlgorithmException e) {
            Log.d("XXXXX", e.toString());
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("AAAAAA", e.toString());
        } catch (Exception e) {
            Log.d("Exceptionnn", e.toString());
        }
    }

    private void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = activity.getWindow();
//            Drawable background = activity.getResources().getDrawable(R.drawable.background_login);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
//            window.setBackgroundDrawable(background);
        }
    }

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private CallbackManager mCallbackManager;
    private LoginButton loginFacebookButton;
    private DatabaseReference reference;
    private TextView resetPassTextView;
    private String usernameFacebook;
    private ProgressDialog progressDialog;
}
