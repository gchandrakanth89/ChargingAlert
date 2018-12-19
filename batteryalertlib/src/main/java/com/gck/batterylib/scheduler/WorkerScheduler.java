package com.gck.batterylib.scheduler;

import android.content.Intent;

import com.gck.batteryalertlib.App;
import com.gck.batteryalertlib.Logger;
import com.gck.batteryalertlib.PowerWorker;
import com.gck.batteryalertlib.Util;
import com.gck.servicelib.BatteryReceiverService;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class WorkerScheduler implements ScheduleManager {

    private static final String TAG = "WorkerScheduler";


    @Override
    public void scheduleAlarm() {

        int batteryLevel = Util.getBatteryLevel();
        if (batteryLevel >= 98) {
            Util.startChargingMonitorService();
            return;
        }

        Logger.d(TAG, "Battery percent below 98 :: Scheduling alarm");

        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        long timeInterval = Util.getTimeInterval(batteryLevel);

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(PowerWorker.class)
                .setConstraints(constraints)
                .setInitialDelay(timeInterval, TimeUnit.MILLISECONDS)
                .build();

        WorkManager workManager = WorkManager.getInstance();
        workManager.enqueue(oneTimeWorkRequest);

    }

    @Override
    public void cancelAllAlarms() {
        WorkManager workManager = WorkManager.getInstance();
        workManager.cancelAllWork();

        Intent batteryService = new Intent(App.getInstance(), BatteryReceiverService.class);
        App.getInstance().stopService(batteryService);
    }
}
