package com.wjf.rxweibo;

import android.app.Application;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/7/3
 */
public class RxWeiboApplication extends Application {

    private static RxWeiboApplication appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }

    public static RxWeiboApplication getAppContext() {
        return appContext;
    }
}
