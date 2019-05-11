package com.example.a26792.smarthometerminal.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by ${Saujyun} on 2019/5/9.
 */
public class MyApplication extends Application {
    private  static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
