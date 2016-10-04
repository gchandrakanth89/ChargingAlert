package com.gck.batteryalertlib;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class BatteryReceiverService extends Service {

    private static final String TAG = BatteryReceiverService.class.getSimpleName();

    private IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    private boolean notificationShown = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
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
            Log.d(TAG, "Percent " + batteryPct);


            if (batteryPct == 100 && !notificationShown) {
                Notification.Builder builder = new Notification.Builder(context);
                builder.setContentTitle("Battery full");
                builder.setTicker("Battery full");
                builder.setContentText("Please disconnect the charger");
                builder.setSmallIcon(R.drawable.ic_launcher);
                builder.setSound(Uri.parse(PreferenceUtils.getNotificationToneUri(context)));
                builder.setPriority(Notification.PRIORITY_HIGH);
                builder.setLights(Color.RED, 500, 500);


                Notification notification = builder.build();

                notification.flags = Notification.FLAG_INSISTENT;

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(100, notification);

                notificationShown = true;
                handler.sendEmptyMessageDelayed(1, 60 * 1000);

            }

        }
    };

    public BatteryReceiverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();

        registerReceiver(batteryReceiver, intentFilter);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service stopped");
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();

        handler.removeMessages(1);
        unregisterReceiver(batteryReceiver);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        super.onDestroy();
    }
}
