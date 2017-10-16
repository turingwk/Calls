package com.wk.calls;

import android.app.Application;

import io.rong.imkit.RongIM;


/**
 * Created by apple on 2017/10/10.
 */

public class CallApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RongIM.init(this);
    }
}
