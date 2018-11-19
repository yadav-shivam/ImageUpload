package com.imageupload;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;


public class MyApplication extends Application {
    private static MyApplication mApplicationInsatnce;

    public static MyApplication getInstance() {
        return mApplicationInsatnce;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationInsatnce = this;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
