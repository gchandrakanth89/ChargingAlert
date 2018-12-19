package com.gck.batterylib.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.text.format.DateUtils;

import com.gck.batteryalertlib.AlarmReceiver;
import com.gck.batteryalertlib.App;
import com.gck.batteryalertlib.Logger;
import com.gck.batteryalertlib.Util;
import com.gck.servicelib.BatteryReceiverService;
import com.gck.servicelib.IService;
import com.gck.servicelib.ServiceProvider;

public class AlarmScheduler implements ScheduleManager {

    private static final String TAG = "AlarmScheduler";

    public AlarmScheduler() {
    }

    @Override
    public void scheduleAlarm() {

        int batteryLevel = Util.getBatteryLevel();
        if (batteryLevel >= 98) {
           Util.startChargingMonitorService();
            return;
        }

        Logger.d(TAG, "Battery percent below 98 :: Scheduling alarm");

        AlarmManager alarmManager = (AlarmManager) App.getInstance().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(App.getInstance(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(App.getInstance(),
                100,
                intent,
                PendingIntent.FLAG_ONE_SHOT);

        long timeInterval = Util.getTimeInterval(batteryLevel);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeInterval, pendingIntent);
        } else {
            alarmManager.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeInterval, 2 * 60 * 2000, pendingIntent);
        }

        BatteryReceiverService.startServiceForeground(App.getInstance());
    }

    @Override
    public void cancelAllAlarms() {

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
}
