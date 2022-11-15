package com.slc.exper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class FormActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);



        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("https://docs.google.com/forms/d/e/1FAIpQLSfbCk5Xh_4TTk_hpLCeNvZYYBd37poy_OEc2Cf1JR35T-SlnQ/viewform?embedded=true");
    }
}