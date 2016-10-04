package com.gck.batteryalertlib;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;

/**
 * Created by Pervacio on 04-10-2016.
 */

public class PreferenceUtils {

    public static String getNotificationToneUri(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String notificationPref = sharedPref.getString("key_notification", RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM).toString());
        return notificationPref;
    }
}
