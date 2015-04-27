/**
 * AlarmReceiver - broadcast receiver that receives the intent sent by the AlarmScheduler
 * and posts a notification.
 */

package com.beccap.weathervane;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        // intent that lets notification know what activity to start
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        // build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.weathervane_white_icon);

        builder.setContentTitle("Weather Reminder");
        builder.setContentText("Please check the weather!");
        builder.setContentIntent(contentIntent);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.weathervane_white_icon);
        builder.setLargeIcon(bitmap);

        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);

        // post the notification
        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
