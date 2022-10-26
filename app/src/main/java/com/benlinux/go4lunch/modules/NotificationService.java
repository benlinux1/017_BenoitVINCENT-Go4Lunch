package com.benlinux.go4lunch.modules;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.activities.UserLunchActivity;

public class NotificationService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showNotification(context);
        }

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    void showNotification(Context context) {
        String CHANNEL_ID = context.getResources().getString(R.string.app_name);
        CharSequence appName = context.getResources().getString(R.string.app_name);

        // Define notification builder
        NotificationCompat.Builder mBuilder;

        // Define notification intent to redirect user to UserLunchActivity
        Intent notificationIntent = new Intent(context, UserLunchActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Define intent with flag that update current activity if app is open
        PendingIntent contentIntent;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Define notification manager
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Add notification channel if build version >= 26
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, appName, NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mChannel);
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLights(context.getResources().getColor(R.color.colorPrimary, context.getTheme()), 2000, 500)
                    .setContentTitle(context.getString(R.string.notification_title));
        } else {
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentTitle(context.getString(R.string.notification_title));

        }

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setContentText(context.getString(R.string.notification_message));
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
