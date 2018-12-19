package com.gck.batteryalertlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gck.batterylib.scheduler.AlarmScheduler;
import com.gck.batterylib.scheduler.ScheduleManager;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ScheduleManager scheduleManager = new AlarmScheduler();

        scheduleManager.scheduleAlarm();
    }
}
