package com.wjf.rxweibo.request.api;

import com.wjf.rxweibo.model.User;


import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/7/6
 */
public interface UserApi {
    @GET("users/show.json")
    Observable<User> getUserInfo(@Query("access_token") String token, @Query("uid") long uid);
}
