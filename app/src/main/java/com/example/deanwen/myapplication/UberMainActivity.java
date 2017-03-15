package com.example.deanwen.myapplication;

/**
 * Created by XiaodongZhou on 8/3/15.
 */
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.deanwen.myapplication.api.UberAuthTokenClient;
import com.example.deanwen.myapplication.api.UberCallback;
import com.example.deanwen.myapplication.model.User;
import com.squareup.okhttp.internal.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.scribe.model.Verb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import retrofit.RetrofitError;
import retrofit.client.Response;


public class UberMainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber_main);

        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                UberMainActivity.this.setProgress(progress * 1000);
                //System.out.println("SetProgress!!!!!!!!!!!!!!!!!!!!!!!");
            }
        });

        webView.setWebViewClient(new UberWebViewClient());

        webView.loadUrl(buildUrl());
    }

    private String buildUrl() {
        Uri.Builder uriBuilder = Uri.parse(Constants.AUTHORIZE_URL).buildUpon();
        uriBuilder.appendQueryParameter("response_type", "code");
        uriBuilder.appendQueryParameter("client_id", Constants.getUberClientId(this));
        uriBuilder.appendQueryParameter("scope", Constants.SCOPES);
        uriBuilder.appendQueryParameter("redirect_uri", Constants.getUberRedirectUrl(this));
        System.out.println("Hi!!!!!!!!!URL: "+ uriBuilder.build().toString().replace("%20", "+"));
        return uriBuilder.build().toString().replace("%20", "+");
    }

    private class UberWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return checkRedirect(url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (checkRedirect(failingUrl)) {
                return;
            }
            Toast.makeText(UberMainActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
        }

        private boolean checkRedirect(String url) {

            if (url.startsWith(Constants.getUberRedirectUrl(UberMainActivity.this))) {
                System.out.println("Come into GetUberRedirectUrl!!!!!!!!!!!!!!!");
                Uri uri = Uri.parse(url);
                System.out.println("print uri: " + uri);
                System.out.println(uri.getQueryParameter("code"));

                UberAuthTokenClient.getUberAuthTokenClient().getAuthToken(
                        Constants.getUberClientSecret(UberMainActivity.this),
                        Constants.getUberClientId(UberMainActivity.this),
                        "authorization_code",
                        uri.getQueryParameter("code"),
                        Constants.getUberRedirectUrl(UberMainActivity.this),
                        new UberCallback<User>() {
                            @Override
                            public void success(User user, Response response) {
                                String accessToken = user.getAccessToken();
                                System.out.println("Access Token:" + accessToken);
                                StringBuilder query1 = new StringBuilder();
                                query1.append("https://api.uber.com/v1.1/history?access_token=");
                                query1.append(accessToken);
                                String request1 = query1.toString();

                                String[] params = new String[]{request1, accessToken};
                                RetrieveHistory rh = new RetrieveHistory();
                                try {
                                    ArrayList<String> info =  rh.execute(params).get();
                                    SplitBillActivity.total_money = info.get(0);
                                    SplitBillActivity.name = info.get(1);
                                    SplitBillActivity.email = info.get(2);

                                    System.out.println("=============name:" + SplitBillActivity.name + " email:" + SplitBillActivity.email);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                                SplitBillActivity.start(UberMainActivity.this);
                                finish();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                System.out.println("inside of failure handler");
                                error.printStackTrace();
                            }
                        });
                return true;
            }
            //System.out.println("Invalid URL: "+ url);
            return false;
        }
    }
}
