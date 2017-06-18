package com.gck.servicelib;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.view.View;

import com.gck.batteryalertlib.NotificationActionReceiver;
import com.gck.batteryalertlib.PreferenceUtils;
import com.gck.batteryalertlib.R;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by Pervacio on 13-06-2017.
 */

class NotificationUtil {

    static void cancelAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    static void showNotification(Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Battery full");
        builder.setTicker("Battery full");
        builder.setContentText("Please disconnect the charger");
        builder.setSmallIcon(R.drawable.notification_small_icon);


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            builder.setLargeIcon(Icon.createWithResource(context, R.drawable.notification_large_icon));
        } else {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_large_icon));
        }

        builder.setSound(Uri.parse(PreferenceUtils.getNotificationToneUri(context)));
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setLights(Color.RED, 500, 500);
        addActionsForNotification(context, builder);

        if (PreferenceUtils.isVibrate(context)) {
            long[] i = {500, 500, 500, 500};
            builder.setVibrate(i);
        }

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
        notificationManager.notify(100, notification);

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
}
