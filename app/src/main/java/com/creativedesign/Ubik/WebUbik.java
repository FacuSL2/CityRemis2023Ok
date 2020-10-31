package com.creativedesign.Ubik;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebUbik extends AppCompatActivity {

    WebView webview1;
    String url = "http://www.ubikrosario.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_ubik);

        webview1 = (WebView) findViewById(R.id.webview1);
        final WebSettings ajustesVisorWeb = webview1.getSettings();
        ajustesVisorWeb.setJavaScriptEnabled(true);
        webview1.loadUrl(url);

    }
}
