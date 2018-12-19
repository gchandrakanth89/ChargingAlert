package com.gck.batteryalertlib;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gck.batterylib.scheduler.ScheduleManager;
import com.gck.batterylib.scheduler.WorkerScheduler;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class PowerWorker extends Worker {
    private static final String TAG = "PowerWorker";

    public PowerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Logger.d(TAG, "Chandu doWork called");
        ScheduleManager scheduleManager = new WorkerScheduler();
        scheduleManager.scheduleAlarm();
        return Result.success();
    }
}
