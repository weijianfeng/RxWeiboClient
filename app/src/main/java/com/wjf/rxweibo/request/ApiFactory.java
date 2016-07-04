package com.wjf.rxweibo.request;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/7/4
 */
public class ApiFactory {

    public static <T> T createOauthApi(Class<T> clazz) {

        OkHttpClient client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(ApiUrl.OAUTH_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(clazz);

    }

    public static <T> T createWeiboApi(Class<T> clazz) {
        // 省略
        return createWeiboApi(clazz);
    }
}
