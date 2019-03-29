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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
import java.util.List;

import static com.example.datvtd.chatting.R.layout.abc_action_bar_title_item;
import static com.example.datvtd.chatting.R.layout.item_user;

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

        this.url = body.split(": ",2);

        Log.d("sadj1",url[1]);


//        RemoteMessage.Notification notification = remoteMessage.getNotification();
//        int j = Integer.parseInt(user.replaceAll("[\\D]",""));
        updateColor();
        Intent intent = new Intent(this, MessageActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("ID",user);
//        intent.putExtras(bundle);
        intent.putExtra("ID", user);
        intent.putExtra("checkGroup", "false");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//        PendingIntent pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

//        URL url = new URL(this.url);
//        Bitmap picture = BitmapFactory.decodeStream((InputStream) url.getContent());

//        Bitmap picture = BitmapFactory.decodeStream(new URL)

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setLargeIcon(picture)
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
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid()).child(user).child("color");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                color = dataSnapshot.getValue().toString();
                new UserAdapter().color = color;
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
    private String color = "";
}
