package com.gck.servicelib;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.gck.batteryalertlib.Logger;
import com.gck.batteryalertlib.PreferenceUtils;

public class BatteryReceiverService extends Service {

    private static final String SERVICE_KEY = "service_key";
    public static final int START_NORMAL = 100;
    public static final int START_FOR_SNOOZE = 200;
    public static final int START_FOR_DISMISS = 300;

    private static final String TAG = BatteryReceiverService.class.getSimpleName();

    private IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    private boolean notificationShown = false;
    private boolean broadcastRegistered = false;

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
                //showNotification(context);
                NotificationUtil.showNotification(context);
                notificationShown = true;
                if (PreferenceUtils.isRepeatEnabled(context)) {
                    int notificationFrequency = PreferenceUtils.getNotificationFrequency(context);
                    Logger.d(TAG, "Repeat enabled :: Frequency = " + notificationFrequency);
                    handler.sendEmptyMessageDelayed(1, notificationFrequency * DateUtils.MINUTE_IN_MILLIS);
                }

            }

        }
    };

    public BatteryReceiverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    static void startService(Context context, int key) {
        Intent intent = new Intent(context, BatteryReceiverService.class);
        intent.putExtra(SERVICE_KEY, key);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationUtil.cancelAllNotifications(this);
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
}
