package com.dn_alan.myapplication;

import android.app.Application;
import android.os.UserManager;
import android.util.Log;

import com.dn_alan.myapplication.manager.DnUserManager;

public class MyAppLication extends Application {
    private static int i= 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("dn_alan_703", "onCreate" + (i++));
    }
}
