package com.example.datvtd.chatting;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.datvtd.chatting.Adapter.MessageAdapter;
import com.example.datvtd.chatting.Adapter.UserAdapter;
import com.example.datvtd.chatting.Fragment.APIService;
import com.example.datvtd.chatting.Model.Chat;
import com.example.datvtd.chatting.Model.GroupChat;
import com.example.datvtd.chatting.Model.User;
import com.example.datvtd.chatting.Notifications.Client;
import com.example.datvtd.chatting.Notifications.Data;
import com.example.datvtd.chatting.Notifications.MyResponse;
import com.example.datvtd.chatting.Notifications.Sender;
import com.example.datvtd.chatting.Notifications.Token;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    public static Bundle bundle;
    public int MAX_SIZE = 10000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTheme(R.style.DarkMode);  // cần đặt trước setContentView

        // full screen with no status bar
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // full screen with status bar
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_message);
        this.usernameTextView = findViewById(R.id.text_username);
        this.profileImage = findViewById(R.id.profile_image);
        this.iconInforGroup = findViewById(R.id.icon_info);
        this.sendButton = findViewById(R.id.b_send);
        this.sendImageButton = findViewById(R.id.b_send_image);
        this.backButton = findViewById(R.id.b_back);
        this.extendIconsButton = findViewById(R.id.ic_extend);
        this.callButton = findViewById(R.id.ic_call);
        this.iconsLayout = findViewById(R.id.layout_icons);
        this.contentSendEditText = findViewById(R.id.text_send);
        this.intent = getIntent();
        this.idReceiver = intent.getStringExtra("ID");
        this.idGroup = intent.getStringExtra("idGroup");
        this.nameGroup = intent.getStringExtra("nameGroup");
        this.adminGroup = intent.getStringExtra("adminGroup");
        this.checkGroup = intent.getStringExtra("checkGroup");
        this.color = intent.getStringExtra("color");
        this.mChat = new ArrayList<>();

        if (this.color == null) {
            if (new UserAdapter().color != null) {
                this.color = new UserAdapter().color;
            }
        }
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.storageReference = FirebaseStorage.getInstance().getReference("chatImage");

        this.recyclerView = findViewById(R.id.recycler_view);
        this.recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        this.recyclerView.setLayoutManager(linearLayoutManager);

        //get color from database

        bundle = new Bundle();
        bundle.putString("idCurrentUser", firebaseUser.getUid());
        bundle.putString("ID", this.idReceiver);
        bundle.putString("idGroup", this.idGroup);
        bundle.putString("nameGroup", this.nameGroup);
        bundle.putString("adminGroup", this.adminGroup);
        bundle.putString("idGroup", this.idGroup);
        bundle.putString("checkGroup", this.checkGroup);

        allowSeenMessage = true;

        //set notification
        this.apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        //get name currentUser for notification
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameCurrentUser = user.getUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (this.color == null) {
            this.color = "default";
        }

        if (this.color != null) {
            if (this.color.equals("default")) {
                updateColor("#ff008577");
            } else {
                updateColor(this.color);
            }
        }

        if (checkGroup != null) {
            if (checkGroup.equals("false")) {
                setViewChat();
            } else {
                setViewGroup();
            }
        }


        iconInforGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, InfoGroupActivity.class);
                startActivity(intent);
            }
        });

        if (checkGroup.equals("true")) {
            this.profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkChangeAvatar = "true";
                    openImage();
                }
            });

            this.usernameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MessageActivity.this, DialogConfirmActivity.class);
                    intent.putExtra("changeNameGroup", "true");
                    intent.putExtra("checkGroup", "true");
                    startActivity(intent);
                }
            });
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //send image
        this.sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        extendIconsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation slide = android.view.animation.AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_to_right);
                iconsLayout.setVisibility(View.VISIBLE);
                iconsLayout.startAnimation(slide);
                extendIconsButton.setVisibility(View.GONE);
            }
        });

        contentSendEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                iconsLayout.setVisibility(View.GONE);
                extendIconsButton.setVisibility(View.VISIBLE);
            }
        });

        contentSendEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        contentSendEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (extendIconsButton.getVisibility() != View.INVISIBLE) {
                    iconsLayout.setVisibility(View.GONE);
                    extendIconsButton.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendNotification(idReceiver, nameCurrentUser, "Calling...", true);
                Intent intent = new Intent(MessageActivity.this, CallActivity.class);
                intent.putExtra("caller", firebaseUser.getUid());
                intent.putExtra("nameCaller", nameCurrentUser);
                intent.putExtra("receiver", idReceiver);
                intent.putExtra("action", "call");
                intent.putExtra("avatarReceiver", avatarReceiver);
                intent.putExtra("nameReceiver", nameReceiver);
                startActivity(intent);
            }
        });
    }

    public void setViewChat() {
        //set view header in acitvity message for chat
        this.reference = FirebaseDatabase.getInstance().getReference("Users").child(idReceiver);
        this.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                usernameTextView.setText(user.getUser());
                nameReceiver = user.getUser();
                if (user.getImageURL().equals("default")) {
                    avatarReceiver = user.getImageURL();
                    profileImage.setImageResource(R.mipmap.ic_launcher_round);
                } else {
                    avatarReceiver = user.getImageURL();
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profileImage);
                }

                //show message update realtime
                readMessage(firebaseUser.getUid(), user.getId(), user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //set seen message
        if (allowSeenMessage) {
            seenMessage(idReceiver);
        }


        //send message
        this.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String contentMessage = contentSendEditText.getText().toString();
                if (!contentMessage.equals("")) {
                    sendMessage(firebaseUser.getUid(), idReceiver, contentMessage);
                }
                contentSendEditText.setText("");
            }
        });
    }

    public void setViewGroup() {
        //set view header in acitvity message for group chat
        this.reference = FirebaseDatabase.getInstance().getReference("ChatGroup").child(idGroup);
        this.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupChat groupChat = dataSnapshot.getValue(GroupChat.class);
                usernameTextView.setText(groupChat.getNameGroup());
                if (groupChat.getImageGroupURL().equals("default")) {
                    profileImage.setImageResource(R.mipmap.ic_launcher_round);
                } else {
                    Glide.with(getApplicationContext()).load(groupChat.getImageGroupURL()).into(profileImage);
                }

                //show message update realtime
                readMessage(firebaseUser.getUid(), groupChat.getIdGroup(), groupChat.getImageGroupURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //set seen mess
        if (allowSeenMessage) {
            seenMessageGroup(idGroup);
        }

        //send message
        this.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String contentMessage = contentSendEditText.getText().toString();
                if (!contentMessage.equals("")) {
                    sendMessage(firebaseUser.getUid(), idGroup, contentMessage);
                }
                contentSendEditText.setText("");
            }
        });
    }

    public void sendMessage(final String sender, final String receiver, String message) {
        this.checkSendImage = "false";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("typeImage", false);
        if (checkGroup.equals("true")) {
            hashMap.put("isGroup", true);
        } else {
            hashMap.put("isGroup", false);
        }
        reference.child("Chats").push().setValue(hashMap);

        if (checkGroup.equals("false")) {
            final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(sender).child(receiver);
            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        chatRef.child("id").setValue(receiver);
                        chatRef.child("color").setValue("default");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(receiver).child(sender);
            chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        chatRefReceiver.child("id").setValue(sender);
                        chatRefReceiver.child("color").setValue("default");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //notification
            final String msg = message;
            reference = FirebaseDatabase.getInstance().getReference("Users").child(receiver);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (notify) {
                        sendNotification(user.getId(), nameCurrentUser, msg, false);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //send notification cho group chat
        if (checkGroup.equals("true")) {
            //notification
            final String msg = message;
            reference = FirebaseDatabase.getInstance().getReference("ChatGroup").child(idGroup).child("members");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (!firebaseUser.getUid().equals(user.getId())) {
                            if (notify) {
                                sendNotification(user.getId(), nameCurrentUser, msg, false);
                            }
                        }
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void sendImage(final String sender, final String receiver, String message) {
        this.checkSendImage = "true";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("typeImage", true);
        if (checkGroup.equals("true")) {
            hashMap.put("isGroup", true);
        } else {
            hashMap.put("isGroup", false);
        }
        reference.child("Chats").push().setValue(hashMap);

        if (checkGroup.equals("false")) {
            notify = true;
            final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(sender).child(receiver);

            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        chatRef.child("id").setValue(receiver);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //notification
            final String msg = message; // đây là link của ảnh gửi đi

            reference = FirebaseDatabase.getInstance().getReference("Users").child(receiver);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (notify) {
                        sendNotification(user.getId(), nameCurrentUser, msg, false);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //set notification for group type image
        if (checkGroup.equals("true")) {
            Log.d("ASD!@#21312", "ASD!@#");
            final String msg = message;
            reference = FirebaseDatabase.getInstance().getReference("ChatGroup").child(idGroup).child("members");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (!firebaseUser.getUid().equals(user.getId())) {
                            if (notify) {
                                sendNotification(user.getId(), nameCurrentUser, msg, false);
                            }
                        }
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void readMessage(final String idSender, final String idReceiver, final String imageURL) {
        this.reference = (DatabaseReference) FirebaseDatabase.getInstance().getReference("Chats");
        Query query = reference.limitToLast(MAX_SIZE);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                long i = System.currentTimeMillis();
                long j = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if ((chat.getReceiver().equals(idSender) && chat.getSender().equals(idReceiver)) && checkGroup.equals("false")
                            || (chat.getReceiver().equals(idReceiver) && chat.getSender().equals(idSender) && checkGroup.equals("false"))) {

                        //set limit size chat
                        if (mChat.size() < MAX_SIZE) {
                            mChat.add(chat);
                        } else {
                            mChat.remove(0);
                            mChat.add(chat);
                        }
                    }
                    if (chat.getReceiver().equals(idReceiver) && checkGroup.equals("true")) {
                        //set limit size chat
                        if (mChat.size() < MAX_SIZE) {
                            mChat.add(chat);
                        } else {
                            mChat.remove(0);
                            mChat.add(chat);
                        }
                    }
                    j = System.currentTimeMillis();
                }
                Log.d("REWR@#!#", String.valueOf(j - i) + "---" + String.valueOf(mChat.size()));

                if (checkGroup.equals("false")) {
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageURL, false);
                } else {
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageURL, true);
                }
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void seenMessage(final String userID) {
        this.reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (allowSeenMessage) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userID)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isseen", true);
                            snapshot.getRef().updateChildren(hashMap);
                        }
                    }
                    allowSeenMessage = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void seenMessageGroup(final String idGroup) {
        this.reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (allowSeenMessage) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat.getReceiver().equals(idGroup) && !chat.getSender().equals(firebaseUser.getUid())) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isseen", true);
                            snapshot.getRef().updateChildren(hashMap);
                        }
                    }
                    allowSeenMessage = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //set status
    public void status(String status) {
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        this.reference.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        status("online");
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

    public void sendNotification(final String receiver, final String username, final String message, final boolean checkCall) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    String title = "";
                    if (checkGroup != null && checkGroup.equals("false")) {
                        title = "New message";
                    } else {
                        title = "New message from group " + nameGroup;
                    }

                    Data data = null;

                    if (!checkCall) {  // dùng cho tin nhắn text và gửi ảnh
                        if (checkSendImage.equals("false")) {
                            if (checkGroup.equals("true")) {
                                data = new Data(firebaseUser.getUid(), idGroup, username + ": " + message, title, receiver);
                            } else {
                                data = new Data(firebaseUser.getUid(), "noneGroup", username + ": " + message, title, receiver);
                            }
                        } else {
                            if (checkGroup.equals("true")) {
                                data = new Data(firebaseUser.getUid(), idGroup, username + ": " + "Send an image", title, receiver);
                            } else {
                                data = new Data(firebaseUser.getUid(), "noneGroup", username + ": " + "Send an image", title, receiver);
                            }
                        }
                    }

                    if (firebaseUser == null) {
                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    }
                    if (checkCall) { // dùng cho cuộc gọi
                        data = new Data(firebaseUser.getUid(), "Call", username + " " + message, title, receiver);
                    }

                    Sender sender = new Sender(data, token.getToken());
                    if (apiService == null) {
                        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
                    }
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        Log.d("notifiaction_success", String.valueOf(response.body().success));
                                        if (response.body().success != 1) {
                                            Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    public String getFileExtenstion(Uri uri) {
        ContentResolver contentResolver = MessageActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(MessageActivity.this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtenstion(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri dowloadUri = task.getResult();
                        String mUri = dowloadUri.toString();
                        if (checkGroup.equals("false")) {
                            sendImage(firebaseUser.getUid(), idReceiver, mUri);
                        } else {
                            sendImage(firebaseUser.getUid(), idGroup, mUri);
                        }

                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(MessageActivity.this, "Fail load image", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(MessageActivity.this, "No image selected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(MessageActivity.this, "Upload in progress", Toast.LENGTH_LONG).show();
            } else {
                if (checkChangeAvatar.equals("false")) {
                    uploadImage();
                } else {
                    changeAvatarGroup();
//                    intent = new Intent(getApplicationContext(), MessageActivity.class);
//                    startActivity(intent);
                }
            }
        }

    }

    public void changeAvatarGroup() {

        final ProgressDialog progressDialog = new ProgressDialog(MessageActivity.this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtenstion(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri dowloadUri = task.getResult();
                        String mUri = dowloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("ChatGroup")
                                .child(idGroup).child("imageGroupURL");
                        reference.setValue(mUri);

                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(MessageActivity.this, "Fail load image", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(MessageActivity.this, "No image selected", Toast.LENGTH_LONG).show();
        }
    }

    public void updateColor(String colorValue) {
        this.backButton.setColorFilter(Color.parseColor(colorValue));
        this.iconInforGroup.setColorFilter(Color.parseColor(colorValue)); // muốn set được màu thì trong file .xml cần phải để là src: "tên ảnh". K được để là setbackgound vì sẽ k set được màu
        this.sendButton.setColorFilter(Color.parseColor(colorValue));
        this.sendImageButton.setColorFilter(Color.parseColor(colorValue));
        this.callButton.setColorFilter(Color.parseColor(colorValue));
        this.extendIconsButton.setColorFilter(Color.parseColor(colorValue));

        Drawable drawable_send = getDrawable(R.drawable.message_send_background);
        GradientDrawable gradientDrawable = (GradientDrawable) drawable_send;
        String valueHexColor = Integer.toHexString(Color.parseColor(colorValue));
//        int cd = (int) Long.parseLong(hex);
        gradientDrawable.setColor(Color.argb(Integer.parseInt(valueHexColor.substring(0,2),16),
                Integer.parseInt(valueHexColor.substring(2,4),16),
                Integer.parseInt(valueHexColor.substring(4,6),16),
                Integer.parseInt(valueHexColor.substring(6,8),16)));
    }

    private EditText contentSendEditText;
    private String idReceiver = "";
    public String idGroup = "";
    private String nameGroup = "";
    private String nameCurrentUser = "";
    private String checkGroup = "";
    private String adminGroup = "";
    private String checkChangeAvatar = "false";
    private String checkSendImage = "false";
    private String color;
    private String avatarReceiver = "";
    private String nameReceiver = "";
    private boolean allowSeenMessage = false;
    private ImageView profileImage;
    private ImageView iconInforGroup;
    private ImageView backButton;
    private ImageView extendIconsButton;
    private ImageView callButton;
    private TextView usernameTextView;
    private RelativeLayout iconsLayout;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private Intent intent;
    private ImageView sendButton;
    private ImageView sendImageButton;
    private MessageAdapter messageAdapter;
    private List<Chat> mChat;
    private RecyclerView recyclerView;
    private ValueEventListener seenListener;
    private APIService apiService;
    private boolean notify = true;
    private Uri imageUri;
    private StorageReference storageReference;
    private UploadTask uploadTask;
    private static final int RESULT_OK = -1;
    private static final int IMAGE_REQUEST = 1;
}