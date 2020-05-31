package com.example.datvtd.chatting.Notifications;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.datvtd.chatting.R;

import java.util.Calendar;

public class AndroidPieNotification extends ContextWrapper {

    public AndroidPieNotification(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    public void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        channel.setSound(defaultSound, audioAttributes);
        channel.setShowBadge(true);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @SuppressLint("WrongConstant")
    @TargetApi(Build.VERSION_CODES.P)
    public Notification.Builder getAndroidPieNotification
            (String title, String body, PendingIntent pendingIntent, Uri soundUri, String typeNotification) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.icon_notification)
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setWhen(Calendar.getInstance().getTimeInMillis())
                .setShowWhen(true)
                .setAutoCancel(true)
                .setGroup(typeNotification);
    }

    @SuppressLint("WrongConstant")
    @TargetApi(Build.VERSION_CODES.P)
    public Notification.Builder getAndroidPieNotificationCalling
            (String title, String body, PendingIntent pendingIntent, Uri soundUri,PendingIntent buttonPendingIntent) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.icon_notification)
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(buttonPendingIntent)
                .setAutoCancel(true);
    }

    private static final String CHANNEL_ID = "com.example.datvtd.chatting";
    private static final String CHANNEL_NAME = "Chatting";
    private NotificationManager notificationManager;
}
