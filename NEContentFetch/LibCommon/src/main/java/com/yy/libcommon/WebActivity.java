package com.yy.libcommon;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.natasa.progresspercent.LineProgress;

public class WebActivity extends BaseActivity {

    WebView webView;
    LineProgress line_progress_bar;
    String url;
    View failed_text;

    static public final String URL_KEY = "com.webactivity.url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.web_activity);

        initWebView();
    }


    void initWebView() {

        String url = getIntent().getStringExtra(URL_KEY);

        line_progress_bar = (LineProgress) findViewById(R.id.line_progress_bar);
        line_progress_bar.setVisibility(View.VISIBLE);
        failed_text = this.findViewById(R.id.failed_text);
        failed_text.setVisibility(View.GONE);
        webView = (WebView)this.findViewById(R.id.web_view);
        webView.setInitialScale(1);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                line_progress_bar.setProgress(progress);
                if(progress >= 100){
                    line_progress_bar.setVisibility(View.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){

                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                line_progress_bar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                line_progress_bar.setVisibility(View.GONE);
                failed_text.setVisibility(View.VISIBLE);
                showDialog("Error","Failed to load, please check your internet connection and retry.");
            }
        });
        line_progress_bar.setProgress(0);
        line_progress_bar.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }
}
