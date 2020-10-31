package com.creativedesign.Ubik;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class TarifasUbik extends AppCompatActivity {

    WebView webview;
    String url = "https://wa.me/c/5493416618722";
   /* String url = "https://wa.me/c/5493416618722";*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarifas_ubik);


        webview = (WebView) findViewById(R.id.webview);
        final WebSettings ajustesVisorWeb = webview.getSettings();
        ajustesVisorWeb.setJavaScriptEnabled(true);
        webview.loadUrl(url);


    }
}
