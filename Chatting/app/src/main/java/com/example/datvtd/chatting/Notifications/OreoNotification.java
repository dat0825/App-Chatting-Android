package com.example.datvtd.chatting.Notifications;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.datvtd.chatting.R;

public class OreoNotification extends ContextWrapper {
    public OreoNotification(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ||
                Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannel(){

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager(){
        if(notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @SuppressLint("WrongConstant")
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotification
            (String title, String body, PendingIntent pendingIntent, Uri soundUri) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.icon_notification)
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotificationCalling(String title, String body, PendingIntent pendingIntent, Uri soundUri, PendingIntent buttonPendingIntent){
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.icon_notification)
                .setSound(soundUri)
                .setContentIntent(buttonPendingIntent)
                .setAutoCancel(true);
    }

    private static final String CHANNEL_ID = "com.example.datvtd.chatting";
    private static final String CHANNEL_NAME = "Chatting";
    private NotificationManager notificationManager;
}
