package com.example.datvtd.chatting.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.datvtd.chatting.MessageActivity;
import com.example.datvtd.chatting.Model.User;
import com.example.datvtd.chatting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import static com.example.datvtd.chatting.R.layout.abc_action_bar_title_item;
import static com.example.datvtd.chatting.R.layout.item_user;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    public String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        this.sented = remoteMessage.getData().get("sented");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//        if( firebaseUser != null && sented.equals(firebaseUser.getUid())){
        sendNotification(remoteMessage);
//        }
    }

    public void sendNotification(RemoteMessage remoteMessage) {
        this.user = remoteMessage.getData().get("user");
        this.icon = remoteMessage.getData().get("icon");
        this.title = remoteMessage.getData().get("title");
        this.body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
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

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification1 = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent).build();

        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //hien thong bao tat ca cac tin nhan cua cac nguoi dung
        noti.notify((int) System.currentTimeMillis(), notification1);

//        int i =0;
//
//        if(j > 0 ){
//            i =j;
//        }
//hien thong bao voi cac nguoi dung nhan tin khac nhau (neu mot nguoi nhan nhieu tin thi chi hien thong bao tin gan nhat)
//        noti.notify(i, builder.build());
    }

    private String user;
    private String icon;
    private String title;
    private String body;
    private String sented;
}
