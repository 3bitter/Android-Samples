package com.threebitter.sample.beaconsample;

import android.app.Application;

import com.threebitter.sdk.utils.StartUp;


/**
 * Created by yohei on 2016/01/02.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        StartUp.init(this);
    }
}
