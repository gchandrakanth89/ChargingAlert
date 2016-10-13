package com.gck.batteryalertlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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

        if (requestCode == REQUEST_CODE_SNOOZE) {
            Logger.d(TAG,"Snooze");
            BatteryReceiverService.startService(context, BatteryReceiverService.START_FOR_SNOOZE);
            return;
        }

        if (requestCode == REQUEST_CODE_DISMISS) {
            Logger.d(TAG,"Dismiss");
            BatteryReceiverService.startService(context, BatteryReceiverService.START_FOR_DISMISS);
            return;
        }
    }
}
