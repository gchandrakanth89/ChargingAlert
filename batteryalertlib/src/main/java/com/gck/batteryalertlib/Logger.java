package com.gck.batteryalertlib;

import android.util.Log;

/**
 * Created by Pervacio on 28-09-2016.
 */

public class Logger {

    public static void d(String tag, String mesaage) {
        Log.d(tag, mesaage);
    }

    public static void e(String tag, String mesaage) {
        Log.e(tag, mesaage);
    }


}
