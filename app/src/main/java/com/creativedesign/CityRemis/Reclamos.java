package com.creativedesign.CityRemis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class Reclamos extends AppCompatActivity {

    WebView webview3;
    String url = "https://wa.me/5493413631351";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclamos);

        webview3 = (WebView) findViewById(R.id.webview3);
        final WebSettings ajustesVisorWeb = webview3.getSettings();
        ajustesVisorWeb.setJavaScriptEnabled(true);
        webview3.loadUrl(url);

    }
}
