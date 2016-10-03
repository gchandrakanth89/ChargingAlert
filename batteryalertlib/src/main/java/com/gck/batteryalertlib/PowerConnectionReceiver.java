package com.gck.batteryalertlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PowerConnectionReceiver extends BroadcastReceiver {
    private static final String TAG = PowerConnectionReceiver.class.getSimpleName();

    public PowerConnectionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Hi" + intent.getAction());
        String action = intent.getAction();

        if (action.equalsIgnoreCase(Intent.ACTION_POWER_CONNECTED)) {
            Log.d(TAG, "Power connected");
            Toast.makeText(context, "Power connected", Toast.LENGTH_LONG).show();

            Util.scheduleAlarm();

        } else if (action.equalsIgnoreCase(Intent.ACTION_POWER_DISCONNECTED)) {
            Log.d(TAG, "Power disconnected");
            Toast.makeText(context, "Power disconnected", Toast.LENGTH_LONG).show();

            Util.cancelAllAlarms();

        } else if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            if (Util.isChargerPlugged()) {
                Util.scheduleAlarm();
            }
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}
