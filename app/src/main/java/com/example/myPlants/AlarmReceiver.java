package com.example.myPlants;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


/**
 * The type - Alarm receiver.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "abcd";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        //broadcast woken when needed- notification now
        long when = System.currentTimeMillis();
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH);
        mNotificationManager.createNotificationChannel(channel);


        /**Notification settings**/
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Time for Soil Check")
                .setContentText("Your Plants might be Thirsty!")
                .setAutoCancel(true)
                .setWhen(when)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        mNotificationManager.notify(1, mNotifyBuilder.build());
    }
}
