package com.creativedesign.Ubik;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class TrabajarenUbik extends AppCompatActivity {

    WebView webview2;
    String url = "http://www.ubikrosario.com/join";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabajaren_ubik);

        webview2 = (WebView) findViewById(R.id.webview2);
        final WebSettings ajustesVisorWeb = webview2.getSettings();
        ajustesVisorWeb.setJavaScriptEnabled(true);
        webview2.loadUrl(url);

    }
}
