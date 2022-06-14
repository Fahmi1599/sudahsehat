package com.example.skripsiact;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URLEncoder;

public class viewpdf extends AppCompatActivity {

    WebView pdfview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpdf);

        pdfview= findViewById(R.id.view_pdf);
        pdfview.getSettings().setJavaScriptEnabled(true);
        pdfview.getSettings().setSupportZoom(true);
        String Health_Check_User = getIntent().getStringExtra("Health_Check_User");


        ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Opening....");

        pdfview.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
               pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
              pd.dismiss();
            }
        });
        String url="";
        try {
            url= URLEncoder.encode(Health_Check_User,"UTF-8");
        }catch (Exception ex)
        {}

        pdfview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);

    }


}