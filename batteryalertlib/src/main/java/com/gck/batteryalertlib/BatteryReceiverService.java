package com.gck.batteryalertlib;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Toast;

public class BatteryReceiverService extends Service {

    private static final String SERVICE_KEY = "service_key";
    public static final int START_NORMAL = 100;
    public static final int START_FOR_SNOOZE = 200;
    public static final int START_FOR_DISMISS = 300;

    private static final String TAG = BatteryReceiverService.class.getSimpleName();

    private IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    private boolean notificationShown = false;
    private boolean broadcastRegistered = false;

    private NotificationManager notificationManager;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Logger.d(TAG, "handleMessage");
            notificationShown = false;
            super.handleMessage(msg);
        }
    };

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            int batteryPct = (int) ((level / (float) scale) * 100);
            Logger.d(TAG, "Percent " + batteryPct);


            if (batteryPct == 100 && !notificationShown) {
                showNotification(context);

            }

        }
    };

    public BatteryReceiverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static void startService(Context context, int key) {
        Intent intent = new Intent(context, BatteryReceiverService.class);
        intent.putExtra(SERVICE_KEY, key);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        if (intent != null) {
            int serviceKey = intent.getIntExtra(SERVICE_KEY, START_NORMAL);
            if (serviceKey == START_NORMAL) {
                Logger.d(TAG, "Service started");
                Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();

                if (!broadcastRegistered)
                    registerReceiver(batteryReceiver, intentFilter);
                broadcastRegistered = true;
            } else if (serviceKey == START_FOR_SNOOZE) {
                handler.removeMessages(1);
                int notificationFrequency = PreferenceUtils.getNotificationFrequency(this);
                Logger.d(TAG, "Repeat enabled :: Frequency = " + notificationFrequency);
                handler.sendEmptyMessageDelayed(1, notificationFrequency * DateUtils.MINUTE_IN_MILLIS);
            } else if (serviceKey == START_FOR_DISMISS) {
                stopSelf();
            }
        } else {
            registerReceiver(batteryReceiver, intentFilter);
            broadcastRegistered = true;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Logger.d(TAG, "Service stopped");
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();

        handler.removeMessages(1);
        unregisterReceiver(batteryReceiver);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        super.onDestroy();
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

    private void showNotification(Context context) {
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
            int smallIconViewId = getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());
            if (smallIconViewId != 0) {
                if (notification.headsUpContentView != null)
                    notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
            }
        }

        notificationManager.notify(100, notification);

        notificationShown = true;
        if (PreferenceUtils.isRepeatEnabled(context)) {
            int notificationFrequency = PreferenceUtils.getNotificationFrequency(context);
            Logger.d(TAG, "Repeat enabled :: Frequency = " + notificationFrequency);
            handler.sendEmptyMessageDelayed(1, notificationFrequency * DateUtils.MINUTE_IN_MILLIS);
        }

    }
}
