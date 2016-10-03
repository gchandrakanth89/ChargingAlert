package com.gck.batteryalertlib;

import android.app.Application;
import android.content.Context;

/**
 * Created by Pervacio on 27-09-2016.
 */

public class App extends Application {
    private static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=getApplicationContext();
    }

    public static Context getInstance() {
        return instance;
    }
}
