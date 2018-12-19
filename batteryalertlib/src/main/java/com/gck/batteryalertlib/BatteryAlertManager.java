package com.gck.batteryalertlib;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import com.gck.batterylib.scheduler.ScheduleManager;
import com.gck.batterylib.scheduler.WorkerScheduler;

/**
 * Created by Pervacio on 19-09-2016.
 */
public class BatteryAlertManager {

    private static final String TAG = "BatteryAlertManager";
    private static BatteryAlertManager instance;
    private ScheduleManager scheduleManager;

    private BatteryAlertManager() {
        scheduleManager = new WorkerScheduler();
    }

    public static BatteryAlertManager getInstance() {
        if(instance==null){
            instance=new BatteryAlertManager();
        }
        return instance;
    }

    public void enable() {
        Context context = App.getInstance();
        ComponentName componentName = new ComponentName(context, PowerConnectionReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        if (Util.isChargerPlugged()) {
            scheduleManager.scheduleAlarm();
        }

    }

    public void disable() {
        Context context = App.getInstance();
        ComponentName componentName = new ComponentName(context, PowerConnectionReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        scheduleManager.cancelAllAlarms();
    }

    public boolean isEnabled() {
        Context context = App.getInstance();
        ComponentName componentName = new ComponentName(context, PowerConnectionReceiver.class);
        PackageManager packageManager = context.getPackageManager();

        int componentEnabledSetting = packageManager.getComponentEnabledSetting(componentName);

        return componentEnabledSetting != PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

    }

}
