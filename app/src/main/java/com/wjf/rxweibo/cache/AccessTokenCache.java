package com.wjf.rxweibo.cache;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;

import com.wjf.rxweibo.RxWeiboApplication;
import com.wjf.rxweibo.model.AccessToken;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/7/5
 */
public class AccessTokenCache {

    private static final String TAG = AccessTokenCache.class.getSimpleName();
    private static SharedPreferences mPreferences;
    private static AccessToken mAccessToken;
    private static final String UID = "uid";
    private static final String EXPIRES_IN = "expires_in";
    private static final String ACCESS_TOKEN = "access_token";

    public static AccessToken getAccessToken() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(RxWeiboApplication.getAppContext());
        if (mAccessToken == null) {
            long uid = mPreferences.getLong(UID, 0L);
            long expiresIn = mPreferences.getLong(EXPIRES_IN, 0L);
            String accessToken = mPreferences.getString(ACCESS_TOKEN, null);
            // 当且仅当三项都完备的时候我们才会初始化授权信息对象！
            if (uid != 0L && expiresIn != 0L && accessToken != null) {
                mAccessToken = new AccessToken(uid, accessToken, expiresIn);
            }
        }
        return mAccessToken;
    }

    public static void saveAccessToken(AccessToken accessToken) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(RxWeiboApplication.getAppContext());
        mPreferences.edit()
                .putLong(UID, accessToken.uid)
                .putString(ACCESS_TOKEN, accessToken.token)
                .putLong(EXPIRES_IN, accessToken.expiresIn * 1000 + System.currentTimeMillis())
                .commit();
    }

    private static void checkAccessToken() {
        if (mAccessToken == null) {
            mAccessToken = getAccessToken();
        }

        if (mAccessToken != null) {
            long now = System.currentTimeMillis();
            if (now > mAccessToken.expiresIn) {
                // 保存用户的uid信息意义不大，因为只是清掉了uid，他所保存的其它信息依然存在
                Log.d(TAG, "用户授权已过期，清除授权信息...");
                invalidateAccessToken();
            } else {
                Log.d(TAG, "授权将在" + DateUtils.getRelativeTimeSpanString(mAccessToken.expiresIn) + "过期！");
            }
        }
    }

    //注销
    public static void invalidateAccessToken() {
        Log.d(TAG, "invalidate access token...");
        mPreferences = PreferenceManager.getDefaultSharedPreferences(RxWeiboApplication.getAppContext());
        mPreferences.edit()
                .remove(UID)
                .remove(EXPIRES_IN)
                .remove(ACCESS_TOKEN)
                .commit();
        mAccessToken = null;
    }


}
