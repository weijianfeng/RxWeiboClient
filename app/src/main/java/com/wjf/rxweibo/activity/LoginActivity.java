package com.wjf.rxweibo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wjf.rxweibo.R;
import com.wjf.rxweibo.cache.AccessTokenCache;
import com.wjf.rxweibo.model.AccessToken;
import com.wjf.rxweibo.model.User;
import com.wjf.rxweibo.request.ApiFactory;
import com.wjf.rxweibo.request.ApiUrl;
import com.wjf.rxweibo.request.api.OauthApi;
import com.wjf.rxweibo.request.api.UserApi;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 简单的完成新浪微博登录流程
 *
 * @author weijianfeng
 * @date 16/7/3
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

        //请求用户授权Token
        mWebView.loadUrl(ApiUrl.OAUTH_URL);
    }

    private void initViews() {
        mWebView = (WebView) findViewById(R.id.login_web);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String code = Uri.parse(url).getQueryParameter("code");
                getAccessToken(code);
                return true;
            }

        });
    }

    private void getAccessToken(String code) {

        ApiFactory.createOauthApi(OauthApi.class).getAccessToken(code)
                .flatMap(new Func1<AccessToken, Observable<User>>() {

                    @Override
                    public Observable<User> call(AccessToken accessToken) {
                        AccessTokenCache.saveAccessToken(accessToken);
                        return ApiFactory.createWeiboApi(UserApi.class)
                                .getUserInfo(accessToken.uid);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(User user) {
                        Log.i(TAG, "USER " + user.name);
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }

//    private void getAccessToken(String code) {
//
//        ApiFactory.createOauthApi(OauthApi.class).getAccessToken(code)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<AccessToken>() {
//
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(AccessToken accessToken) {
//                        AccessTokenCache.saveAccessToken(LoginActivity.this,accessToken);
//                    }
//                });
//
//    }

}
