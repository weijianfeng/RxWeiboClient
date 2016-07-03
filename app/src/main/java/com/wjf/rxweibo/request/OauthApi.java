package com.wjf.rxweibo.request;

import com.wjf.rxweibo.model.AccessToken;

import retrofit2.http.Field;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/7/3
 */
public interface OauthApi {

    @POST(ApiUrl.ACCESS_TOKEN_URL)
    Observable<AccessToken> getAccessToken(@Field("code") String code);

}
