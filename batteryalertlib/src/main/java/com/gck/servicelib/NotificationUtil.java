package com.gck.servicelib;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import com.gck.batteryalertlib.NotificationActionReceiver;
import com.gck.batteryalertlib.PreferenceUtils;
import com.gck.batteryalertlib.R;

/**
 * Created by Pervacio on 13-06-2017.
 */

class NotificationUtil {

    private static final String TAG = "NotificationUtil";
    private static final String CHANNEL_ID = String.valueOf(100);
    private static final String FG_CHANNEL_ID = String.valueOf(200);
    private static final int NOTIFICATION_ID = 1000;
    public static final int FG_NOTIFICATION_ID = 2000;

    static void cancelAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    static Notification showForeGroundNotification(Context context){
        createNotificationChannelForFg(context);
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, FG_CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context);
        }
        builder.setContentTitle("Battery Content");
        builder.setTicker("Battery Ticker");
        builder.setContentText("Battery content text");
        builder.setSmallIcon(R.drawable.notification_small_icon);
        builder.setOngoing(true);
        builder.setAutoCancel(false);

        Notification notification = builder.build();

        return notification;
    }

    static void showNotification(Context context) {

        createNotificationChannel(context);

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context);
        }
        builder.setContentTitle("Battery full");
        builder.setTicker("Battery full");
        builder.setContentText("Please disconnect the charger");
        builder.setSmallIcon(R.drawable.notification_small_icon);


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            builder.setLargeIcon(Icon.createWithResource(context, R.drawable.notification_large_icon));
        } else {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_large_icon));
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(Notification.PRIORITY_HIGH);
            builder.setSound(Uri.parse(PreferenceUtils.getNotificationToneUri(context)));
            builder.setLights(Color.GREEN, 500, 500);


            if (PreferenceUtils.isVibrate(context)) {
                long[] i = {500, 500, 500, 500};
                builder.setVibrate(i);
            }
        }
        addActionsForNotification(context, builder);

        Notification notification = builder.build();

        notification.flags = Notification.FLAG_INSISTENT;

        //Hide small icon from notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int smallIconViewId = context.getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());
            if (smallIconViewId != 0) {
                if (notification.headsUpContentView != null)
                    notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
            }
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    private static void addActionsForNotification(Context context, Notification.Builder builder) {
        Intent snoozeIntent = new Intent(context, NotificationActionReceiver.class);
        snoozeIntent.putExtra(NotificationActionReceiver.KEY_REQUEST_CODE, NotificationActionReceiver.REQUEST_CODE_SNOOZE);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 100, snoozeIntent, PendingIntent.FLAG_ONE_SHOT);

        Intent dismissIntent = new Intent(context, NotificationActionReceiver.class);
        dismissIntent.putExtra(NotificationActionReceiver.KEY_REQUEST_CODE, NotificationActionReceiver.REQUEST_CODE_DISMISS);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 101, dismissIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification.Action.Builder snoozeAction;
        Notification.Action.Builder dismissAction;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {

            snoozeAction = new Notification.Action.Builder(R.drawable.snooze, "Snooze", snoozePendingIntent);
            dismissAction = new Notification.Action.Builder(R.drawable.cancel, "Dismiss", dismissPendingIntent);

            if (PreferenceUtils.isRepeatEnabled(context)) {
                builder.addAction(snoozeAction.build());
            }
            builder.addAction(dismissAction.build());


        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

            Icon snIcon = Icon.createWithResource(context, R.drawable.snooze);
            snoozeAction = new Notification.Action.Builder(snIcon, "Snooze", snoozePendingIntent);

            Icon icon = Icon.createWithResource(context, R.drawable.cancel);
            dismissAction = new Notification.Action.Builder(icon, "Dismiss", dismissPendingIntent);

            if (PreferenceUtils.isRepeatEnabled(context)) {
                builder.setActions(snoozeAction.build(), dismissAction.build());
            } else {
                builder.setActions(dismissAction.build());
            }

        }
    }

    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            channel.setSound(Uri.parse(PreferenceUtils.getNotificationToneUri(context)), audioAttributes);

            if (PreferenceUtils.isVibrate(context)) {
                long[] i = {500, 500, 500, 500};
                channel.setVibrationPattern(i);
            }

            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static void createNotificationChannelForFg(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(FG_CHANNEL_ID, name, importance);
            channel.setDescription(description);


            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
