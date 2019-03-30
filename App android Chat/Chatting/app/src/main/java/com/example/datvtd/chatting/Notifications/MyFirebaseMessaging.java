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
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.datvtd.chatting.Adapter.UserAdapter;
import com.example.datvtd.chatting.MessageActivity;
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

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//        if( firebaseUser != null && sented.equals(firebaseUser.getUid())){
        try {
            sendNotification(remoteMessage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }
    }

    public void sendNotification(RemoteMessage remoteMessage) throws IOException {
        this.user = remoteMessage.getData().get("user");
        this.icon = remoteMessage.getData().get("icon");
        this.title = remoteMessage.getData().get("title");
        this.body = remoteMessage.getData().get("body");

        updateColor();
        getUrlAvatar();

//        RemoteMessage.Notification notification = remoteMessage.getNotification();
//        int j = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent = new Intent(this, MessageActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("ID",user);
//        intent.putExtras(bundle);
        intent.putExtra("ID", user);
        intent.putExtra("checkGroup", "false");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//        PendingIntent pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = null;

        if (this.urlAvatarSender.equals("default")) {
            largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        } else {
            URL urlImage = new URL(this.urlAvatarSender);
            largeIcon = BitmapFactory.decodeStream((InputStream) urlImage.getContent());
        }

//        URL sound = new URL(this.sound);
//        Bitmap picture = BitmapFactory.decodeStream((InputStream) sound.getContent());

//        Bitmap picture = BitmapFactory.decodeStream(new URL)

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.small_icon)  // dùng để xóa small icon trong thanh thông báo.
                .setLargeIcon(largeIcon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent).build();

        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //hien thong bao tat ca cac tin nhan cua cac nguoi dung
        noti.notify((int) System.currentTimeMillis(), notification);

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
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(user).child("imageURL");
        // phải dùng biến "user" để lấy id của người gửi. nếu dùng firebaseuser.getUid thì sẽ ra id của người nhận.
        // Lớp này xử lý trên máy của người nhận.
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

    private String user;
    private String icon;
    private String title;
    private String body;
    private String sented;
    private String[] url;
    private String urlAvatarSender = "";
    private String color = "";
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
}
