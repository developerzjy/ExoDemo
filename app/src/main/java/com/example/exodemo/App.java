package com.example.exodemo;

import android.app.Application;
import android.content.Context;

/**
 * Author: zhangjianyang
 * Date: 2021/2/5
 */
public class App extends Application {

    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }
}
