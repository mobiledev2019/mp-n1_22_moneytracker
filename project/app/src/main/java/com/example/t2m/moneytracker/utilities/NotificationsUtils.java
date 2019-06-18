package com.example.t2m.moneytracker.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;

import com.example.t2m.moneytracker.R;

/**
 * Assisting the creation of notifications on and after Android 8.
 */

public class NotificationsUtils {

    public static final String CHANNEL_ID_DOWNLOADING = "Downloading Channel";
    public static final String CHANNEL_ID_UPLOADING = "Uploading Channel";
    public static final String CHANNEL_ID_UPLOAD_COMPLETE = "Upload Complete Channel";
    public static final String CHANNEL_ID_CONFLICT = "Sync Conflict Channel";

    public static final String NOTIFICATION_CHANNEL_NAME = "Notification Channel";

    public static void createNotificationChannel(Context context, String channelId) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            return;
        }

//            CharSequence channelName = NOTIFICATION_CHANNEL_NAME;
        //int importance = NotificationManager.IMPORTANCE_LOW;
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        String channelName = context.getString(R.string.app_name);

        NotificationChannel channel = new NotificationChannel(
                channelId, channelName, importance);

        channel.setDescription(NOTIFICATION_CHANNEL_NAME);

        //channel.setSound();

        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        //return channel;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
}
