package com.gck.batteryalertlib;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;

/**
 * Created by Pervacio on 04-10-2016.
 */

public class PreferenceUtils {

    public static final String KEY_ENABLE = "pref_enable";
    public static final String KEY_USB_ENABLE = "pref_usb_alert";
    public static final String KEY_NOTIFICATION_TONE = "pref_notification_tone";
    public static final String KEY_REPEAT_NOTIFICATION = "pref_notification_repeat";
    public static final String KEY_NOTIFICATION_REPEAT_COUNT = "pref_notification_repeat_count";
    public static final String KEY_NOTIFICATION_VIBRATION = "pref_notification_vibration";

    public static boolean isUSBAlertEnabled(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(KEY_USB_ENABLE, true);
    }

    public static String getNotificationToneUri(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(KEY_NOTIFICATION_TONE, RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM).toString());
    }

    public static boolean isRepeatEnabled(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(KEY_REPEAT_NOTIFICATION, true);
    }

    public static int getNotificationFrequency(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String pref_notification_frequency = sharedPref.getString(KEY_NOTIFICATION_REPEAT_COUNT, "2");
        return Integer.valueOf(pref_notification_frequency);
    }

    public static boolean isVibrate(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(KEY_NOTIFICATION_VIBRATION, false);
    }
}
