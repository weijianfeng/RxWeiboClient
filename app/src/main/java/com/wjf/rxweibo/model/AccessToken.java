package com.wjf.rxweibo.model;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.concurrent.TimeUnit;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/7/3
 */
public class AccessToken {

    public static final String UID = "uid";
    public static final String EXPIRES_IN = "expires_in";
    public static final String ACCESS_TOKEN = "access_token";

    @Expose
    @SerializedName("uid")
    public long uid;

    @Expose
    @SerializedName("access_token")
    public String token;//用于调用access_token，接口获取授权后的access token

    @Expose
    @SerializedName("expires_in")
    public long expiresIn;//access_token的生命周期，单位是秒数

    public long expiresTime;//具体过期时间点,单位是毫秒

    public AccessToken(long uid, String token, long expiresIn) {
        this.uid = uid;
        this.token = token;
        this.expiresIn = expiresIn;
    }

    public boolean isValid(){
        long leftMillis = expiresTime - System.currentTimeMillis();
        Log.i("AccessToken", "过期时间还剩：" + TimeUnit.MILLISECONDS.toMinutes(leftMillis) + "分");
        return leftMillis > 0;
    }
}
