package com.gck.batteryalertlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gck.servicelib.IService;
import com.gck.servicelib.ServiceProvider;

public class NotificationActionReceiver extends BroadcastReceiver {

    public static final String KEY_REQUEST_CODE = "request_code";
    public static final int REQUEST_CODE_DISMISS = 100;
    public static final int REQUEST_CODE_SNOOZE = 200;
    private static final String TAG = NotificationActionReceiver.class.getSimpleName();

    public NotificationActionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;
        int requestCode = extras.getInt(KEY_REQUEST_CODE);

        IService iService = ServiceProvider.getServiceProvider();

        if (requestCode == REQUEST_CODE_SNOOZE) {
            Logger.d(TAG, "Snooze");
            iService.startServiceForSnooze();
            //BatteryReceiverService.startService(context, BatteryReceiverService.START_FOR_SNOOZE);
            return;
        }

        if (requestCode == REQUEST_CODE_DISMISS) {
            Logger.d(TAG, "Dismiss");
            iService.startServiceForDismiss();
            //BatteryReceiverService.startService(context, BatteryReceiverService.START_FOR_DISMISS);
            return;
        }
    }
}
