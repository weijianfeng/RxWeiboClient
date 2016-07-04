package com.wjf.rxweibo.request;

import com.wjf.rxweibo.constant.Contants;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/7/3
 */
public class ApiUrl {

    public static final String OAUTH_BASE_URL = "https://api.weibo.com/oauth2/";

    public static final String BASE_URL = "https://api.weibo.com/2/";

    //请求用户授权Token
    public static final String OAUTH_URL =  OAUTH_BASE_URL + "authorize?client_id=" + Contants.APP_KEY + "&response_type=code&redirect_uri=" + Contants.REDIRECT_URL + "&scope=all&display=mobile";
    //OAuth2的access_token接口
    public static final String ACCESS_TOKEN_URL = "access_token?client_id=" + Contants.APP_KEY + "&client_secret=" + Contants.APP_SECRET + "&grant_type=authorization_code&redirect_uri=" + Contants.REDIRECT_URL;


}
