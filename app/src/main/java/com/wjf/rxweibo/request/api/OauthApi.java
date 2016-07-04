package com.wjf.rxweibo.request.api;

import com.wjf.rxweibo.model.AccessToken;
import com.wjf.rxweibo.request.ApiUrl;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/7/3
 */
public interface OauthApi {
    @FormUrlEncoded
    @POST(ApiUrl.ACCESS_TOKEN_URL)
    Observable<AccessToken> getAccessToken(@Field("code") String code);

}
