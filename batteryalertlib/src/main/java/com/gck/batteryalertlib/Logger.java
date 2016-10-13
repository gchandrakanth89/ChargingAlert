package com.gck.batteryalertlib;

import android.util.Log;

/**
 * Created by Pervacio on 28-09-2016.
 */

public class Logger {

    private static final String LOG_TAG = "AlertTag";

    public static void d(String tag, String message) {
        Log.d(LOG_TAG, tag + ": " + message);
    }

    public static void e(String tag, String message) {
        Log.e(LOG_TAG, tag + ": " + message);
    }


}
