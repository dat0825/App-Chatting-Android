package com.example.datvtd.chatting.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.datvtd.chatting.Adapter.UserAdapter;
import com.example.datvtd.chatting.CallActivity;
import com.example.datvtd.chatting.LoginActivity;
import com.example.datvtd.chatting.MessageActivity;
import com.example.datvtd.chatting.Model.GroupChat;
import com.example.datvtd.chatting.Model.User;
import com.example.datvtd.chatting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        this.sented = remoteMessage.getData().get("sented");

        // phải dùng biến "user" để lấy id của người gửi. nếu dùng firebaseuser.getUid thì sẽ ra id của người nhận.
        // Vì Lớp này xử lý trên máy của người nhận.

//        if (firebaseUser != null && sented.equals(firebaseUser.getUid())) {
        try {
            sendNotification(remoteMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(RemoteMessage remoteMessage) throws IOException {
        this.user = remoteMessage.getData().get("user");
        this.typeNotification = remoteMessage.getData().get("typeNotification");
        this.title = remoteMessage.getData().get("title");
        this.body = remoteMessage.getData().get("body");

        updateColor();
        getUrlAvatar();

        //get nameCaller
        if (typeNotification.equals("Call")) {
            String caller[] = body.split(" Calling");
            nameCaller = caller[0];
        }

        intent = new Intent(this, MessageActivity.class);
        if (this.typeNotification != null) {
            if (typeNotification.equals("noneGroup")) {
                intent.putExtra("ID", user);
                intent.putExtra("checkGroup", "false");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else {
                if (!typeNotification.equals("Call")) {       // dùng cho gửi tin nhắn text và ảnh cho group
                    intent.putExtra("idGroup", idGroup);
                    intent.putExtra("nameGroup", nameGroup);
                    intent.putExtra("adminGroup", adminGroup);
                    intent.putExtra("color", color);
                    intent.putExtra("checkGroup", "true");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getInfoGroup(typeNotification);
                }
            }
        }

        // chuyển từ thông báo sang lớp MessageActivity.class bởi vì intent = new Intent(this, MessageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);

        if (this.urlAvatarSender.equals("default")) {
            largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        } else {
            URL urlImage = new URL(this.urlAvatarSender);
            largeIcon = BitmapFactory.decodeStream((InputStream) urlImage.getContent());
        }

        // chuyển từ thông báo sang lớp CallActivity
        Intent buttonIntent = new Intent(this, CallActivity.class);
        buttonIntent.putExtra("nameCaller", nameCaller);
        buttonIntent.putExtra("avatarCaller", urlAvatarSender);
        buttonIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent buttonPendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), buttonIntent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification;
        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //set view thông báo cho 2 loại ( tin nhắn và cuộc gọi)
        if (typeNotification.equals("Call")) {
            notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.small_icon)  // dùng để xóa small icon trong thanh thông báo.
                    .setLargeIcon(largeIcon)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentIntent(buttonPendingIntent)
//                    .setContentIntent(pendingIntent)  // thực hiện lệnh pendingIntent (chuyển sang lớp khác) khi click vào chính giữa thông báo
//                    .addAction(R.drawable.ic_add_person,getString(R.string.project_id),buttonPendingIntent)  // thực hiện lệnh buttonpendingIntent (chuyển sang lớp khác) khi click vào nút dưới thông báo
                    .build();
            //hien thong bao tat ca cac tin nhan cua cac nguoi dung
            noti.notify((int) System.currentTimeMillis(), notification);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AndroidPieNotification androidPieNotification = new AndroidPieNotification(this);
                notification = androidPieNotification.getAndroidPieNotification(title, body, pendingIntent, defaultSound).build();
                //hien thong bao tat ca cac tin nhan cua cac nguoi dung
                androidPieNotification.getManager().notify((int) System.currentTimeMillis(), notification);
            } else {
                notification = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.small_icon)  // dùng để xóa small icon trong thanh thông báo.
                        .setLargeIcon(largeIcon)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSound)
                        .setContentIntent(pendingIntent)  // thực hiện lệnh pendingIntent (chuyển sang lớp khác) click vào thông báo
                        .build();

                //hien thong bao tat ca cac tin nhan cua cac nguoi dung
                noti.notify((int) System.currentTimeMillis(), notification);
            }
        }

//        int i =0;
//
//        if(j > 0 ){
//            i =j;
//        }
//hien thong bao voi cac nguoi dung nhan tin khac nhau (neu mot nguoi nhan nhieu tin thi chi hien thong bao tin gan nhat)
//        noti.notify(i, builder.build());
    }

    public void updateColor() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid()).child(user).child("color");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    color = dataSnapshot.getValue().toString();
                    new UserAdapter().color = color;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUrlAvatar() {
        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(user).child("imageURL");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                urlAvatarSender = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getInfoGroup(final String idGroupChat) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("ChatGroup").child(idGroupChat);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupChat groupChat = dataSnapshot.getValue(GroupChat.class);
                idGroup = groupChat.getIdGroup();
                nameGroup = groupChat.getNameGroup();
                adminGroup = groupChat.getAdmin();
                color = groupChat.getColor();
                intent.putExtra("idGroup", idGroup);
                intent.putExtra("nameGroup", nameGroup);
                intent.putExtra("adminGroup", adminGroup);
                intent.putExtra("color", color);
                intent.putExtra("checkGroup", "true");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String user;
    private String typeNotification;  // nếu là thông báo từ group thì giá trị này là id của group
    private String title;
    private String body;
    private String sented;
    private String nameCaller;
    private String[] url;
    private String urlAvatarSender = "";
    private String idGroup = "";
    private String nameGroup = "";
    private String adminGroup = "";
    private String color = "";
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private Intent intent;
    private Bitmap largeIcon;

}
