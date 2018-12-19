package com.gck.batteryalertlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gck.batterylib.scheduler.ScheduleManager;
import com.gck.batterylib.scheduler.WorkerScheduler;

public class PowerConnectionReceiver extends BroadcastReceiver {
    private static final String TAG = PowerConnectionReceiver.class.getSimpleName();

    public PowerConnectionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Logger.d(TAG, "" + intent.getAction());
        String action = intent.getAction();

        ScheduleManager scheduleManager = new WorkerScheduler();

        if (action.equalsIgnoreCase(Intent.ACTION_POWER_CONNECTED)) {
            if (Util.isChargerPlugged()) {
                scheduleManager.scheduleAlarm();
                Logger.d(TAG, "Power connected");
                Toast.makeText(context, "Power connected", Toast.LENGTH_LONG).show();
            }

        } else if (action.equalsIgnoreCase(Intent.ACTION_POWER_DISCONNECTED)) {
            Logger.d(TAG, "Power disconnected");
            Toast.makeText(context, "Power disconnected", Toast.LENGTH_LONG).show();

            scheduleManager.cancelAllAlarms();

        } else if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            if (Util.isChargerPlugged()) {
                scheduleManager.scheduleAlarm();
            }
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}
