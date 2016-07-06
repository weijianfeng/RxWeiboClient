package com.wjf.rxweibo;

import android.app.Application;
import android.content.Context;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/7/3
 */
public class RxWeiboApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }

    public static Context getAppContext() {
        return appContext;
    }
}
