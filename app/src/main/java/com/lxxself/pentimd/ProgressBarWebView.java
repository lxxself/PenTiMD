package com.lxxself.pentimd;


import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.net.http.SslError;
import android.text.AndroidCharacter;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by lxxself on 2015/11/19.
 */
public class ProgressBarWebView extends WebView {
    private GestureDetector mGestureDetector;
    private ProgressBar progressbar;
    private WebSettings webSettings;
    public ProgressBarWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 10, 0, 0));
        progressbar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar));

        addView(progressbar);

        webSettings = getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setSupportZoom(true);
//        webSettings.setTextZoom(200);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        setWebViewClient(new WebViewClient());
        setWebChromeClient(new WebChromeClient());
    }

    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //通过所有证书
            handler.proceed();
        }

    }
    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    }



//    @Override
//    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
//        lp.x = l;
//        lp.y = t;
//        progressbar.setLayoutParams(lp);
//        super.onScrollChanged(l, t, oldl, oldt);
//    }

}
