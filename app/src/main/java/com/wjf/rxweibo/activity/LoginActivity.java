package com.wjf.rxweibo.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wjf.rxweibo.R;
import com.wjf.rxweibo.request.ApiUrl;

/**
 * 简单的完成新浪微博登录流程
 *
 * @author weijianfeng
 * @date 16/7/3
 */
public class LoginActivity extends AppCompatActivity {

    //@Bind(R.id.login_web)
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

        //请求用户授权Token
        mWebView.loadUrl(ApiUrl.AUTH_URL);
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

    }

}
