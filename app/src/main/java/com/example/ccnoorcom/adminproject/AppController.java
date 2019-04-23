package com.example.ccnoorcom.adminproject;

import android.app.Application;
import android.content.Context;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class AppController extends Application {
    private static Context context;


    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("IRANSans(FaNum).ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
