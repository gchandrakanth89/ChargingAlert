package com.gck.batteryalertlib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;

/**
 * Created by Pervacio on 27-09-2016.
 */

public class Util {

    private static final long INTERVAL_BELOW_50 = 20 * DateUtils.MINUTE_IN_MILLIS;

    private static final long INTERVAL_BELOW_80 = 10 * DateUtils.MINUTE_IN_MILLIS;

    private static final long INTERVAL_BELOW_95 = 5 * DateUtils.MINUTE_IN_MILLIS;

    private static final long INTERVAL_BELOW_98 = 2 * DateUtils.MINUTE_IN_MILLIS;

    private static final String TAG = Util.class.getSimpleName();


    private static long getTimeInterval(int batteryLevel) {


        if (batteryLevel < 50) {
            return INTERVAL_BELOW_50;
        }

        if (batteryLevel < 80) {
            return INTERVAL_BELOW_80;
        }

        if (batteryLevel < 95) {
            return INTERVAL_BELOW_95;
        }

        if (batteryLevel < 98) {
            return INTERVAL_BELOW_98;
        }

        return -1;
    }

    public static final void scheduleAlarm() {

        int batteryLevel = getBatteryLevel();
        if (batteryLevel >= 98) {
            Log.d(TAG, "Battery percent above 98 :: Starting service");
            BatteryReceiverService.startService(App.getInstance(), BatteryReceiverService.START_NORMAL);
            return;
        }

        Log.d(TAG, "Battery percent below 98 :: Scheduling alarm");

        AlarmManager alarmManager = (AlarmManager) App.getInstance().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(App.getInstance(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(App.getInstance(),
                100,
                intent,
                PendingIntent.FLAG_ONE_SHOT);

        long timeInterval = getTimeInterval(batteryLevel);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeInterval, pendingIntent);
        } else {
            alarmManager.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeInterval, 2 * 60 * 2000, pendingIntent);
        }


    }

    public static final void cancelAllAlarms() {
        AlarmManager alarmManager = (AlarmManager) App.getInstance().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(App.getInstance(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(App.getInstance(),
                100,
                intent,
                PendingIntent.FLAG_ONE_SHOT);

        alarmManager.cancel(pendingIntent);

        Intent batteryService = new Intent(App.getInstance(), BatteryReceiverService.class);
        App.getInstance().stopService(batteryService);
    }

    public static int getBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = App.getInstance().registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = (level / (float) scale) * 100;

        Log.d(TAG, "Battery capacity " + batteryPct);
        return (int) batteryPct;
    }

    public static boolean isChargerPlugged() {
        boolean isPlugged;
        Intent intent = App.getInstance().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC;
        if (PreferenceUtils.isUSBAlertEnabled(App.getInstance())) {
            isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_USB;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        }

        Log.d(TAG, "isChargerConnected " + isPlugged);
        return isPlugged;
    }

    public static String getRingtoneName(Context context, String uriString) {
        Uri ringtoneUri = Uri.parse(uriString);
        Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
        String name = ringtone.getTitle(context);
        return name;
    }

    public static String getFreqencySummery(Context context) {
        int notificationFrequency = PreferenceUtils.getNotificationFrequency(context);
        String notificationFrequencyStr = Integer.toString(notificationFrequency);
        String[] frequencyValues = context.getResources().getStringArray(R.array.pref_notification_frequency_values);
        String[] frequencyNames = context.getResources().getStringArray(R.array.pref_notification_frequency);

        for (int i = 0; i < frequencyValues.length; i++) {
            if (notificationFrequencyStr.equalsIgnoreCase(frequencyValues[i])) {
                return frequencyNames[i];
            }
        }

        return "";
    }
}
